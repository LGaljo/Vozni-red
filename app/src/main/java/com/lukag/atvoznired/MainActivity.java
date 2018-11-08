package com.lukag.atvoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";

    private AutoCompleteTextView vstopnaPostajaView;
    private AutoCompleteTextView izstopnaPostajaView;

    private TextView koledar;

    private RecyclerView recyclerView;
    private priljubljenePostajeAdapter pAdapter;

    sharedPrefsManager favs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dodajAutoCompleteTextView();
        findViews();

        DataSourcee.nastaviZadnjiIskani(this, vstopnaPostajaView, izstopnaPostajaView);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        favs = new sharedPrefsManager(this);

        pAdapter = new priljubljenePostajeAdapter(sharedPrefsManager.priljubljeneRelacije, this, vstopnaPostajaView, izstopnaPostajaView, favs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(pAdapter);

        koledar.setText(DataSourcee.dodajDanasnjiDan());

        // Gumb za shranitev priljubljene relacije
        final ImageView star = (ImageView)findViewById(R.id.zvezda);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vstopnaPostaja = vstopnaPostajaView.getText().toString();
                String izstopnaPostaja = izstopnaPostajaView.getText().toString();

                if (vstopnaPostaja.equals(izstopnaPostaja) || vstopnaPostaja.equals("") || izstopnaPostaja.equals("")) {
                    Toast.makeText(MainActivity.this, "Neveljaven vnos", Toast.LENGTH_SHORT).show();
                } else {
                    favs.dodajPriljubljeno(new Relacija("", vstopnaPostajaView.getText().toString(), "", izstopnaPostajaView.getText().toString(), null));
                    pAdapter.notifyDataSetChanged();
                }
            }
        });

        // Gumb za prikaz
        Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DataSourcee.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView);
                submit();
            }
        });

        // Gumb za zamenjavo postajalisc
        final ImageView invert = (ImageView)findViewById(R.id.swap);
        invert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tmp = izstopnaPostajaView.getText().toString();
                izstopnaPostajaView.setText(vstopnaPostajaView.getText(), false);
                vstopnaPostajaView.setText(tmp, false);
                DataSourcee.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView);
            }
        });

        koledar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "To bo odprlo koledar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favs.shraniPriljubljene();
    }

    private void dodajAutoCompleteTextView() {
        AutoCompleteTextView vstop;
        AutoCompleteTextView izstop;

        DataSourcee.init(this);

        vstop = (AutoCompleteTextView) findViewById(R.id.vstopna_text);
        izstop = (AutoCompleteTextView) findViewById(R.id.izstopna_text);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,R.layout.dropdown, DataSourcee.samoPostaje);

        vstop.setThreshold(2);
        vstop.setAdapter(adapter);
        izstop.setThreshold(2);
        izstop.setAdapter(adapter);

    }

    private void findViews() {
        vstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.vstopna_text);
        izstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.izstopna_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pogled_priljubljenih);
        koledar = (TextView) findViewById(R.id.textCalendar);
    }

    private void submit() {
        String vstopnaPostaja = vstopnaPostajaView.getText().toString();
        String izstopnaPostaja = izstopnaPostajaView.getText().toString();
        String vstopnaID = DataSourcee.getIDfromMap(vstopnaPostaja);
        String izstopnaID = DataSourcee.getIDfromMap(izstopnaPostaja);
        String date = koledar.getText().toString();

        Log.d("Autocomplete", vstopnaPostaja + ": " + vstopnaID + " --> " + izstopnaPostaja + ": " + izstopnaID);

        if (vstopnaPostaja.equals(izstopnaPostaja) || vstopnaPostaja.equals("") || izstopnaPostaja.equals("")) {
            Toast.makeText(MainActivity.this, "Neveljaven vnos", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<String> prenos = new ArrayList<>();
            prenos.add(vstopnaID);
            prenos.add(vstopnaPostaja);
            prenos.add(izstopnaID);
            prenos.add(izstopnaPostaja);
            prenos.add(date);
            Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
            intent.putStringArrayListExtra(EXTRA_MESSAGE, prenos);
            startActivity(intent);
        }
    }
}
