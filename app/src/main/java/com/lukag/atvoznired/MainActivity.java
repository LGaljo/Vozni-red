package com.lukag.atvoznired;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeBackLayout;
import com.jude.swipbackhelper.SwipeBackPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";

    private AutoCompleteTextView vstopnaPostajaView;
    private AutoCompleteTextView izstopnaPostajaView;
    private Calendar calendarView;

    private ImageView delete_vp;
    private ImageView delete_ip;

    private TextView koledar;
    private View contextView;

    private RecyclerView recyclerView;
    private priljubljenePostajeAdapter pAdapter;

    sharedPrefsManager favs;

    @Override
    protected void onStart() {
        super.onStart();
        koledar.setText(DataSourcee.dodajDanasnjiDan());
        favs = new sharedPrefsManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String reason = intent.getStringExtra("reason");
        contextView = findViewById(R.id.priljubljene_text);

        if (reason != null && reason.equals("no_connection")) {
            Snackbar.make(contextView, R.string.no_connection, Snackbar.LENGTH_LONG).show();
        }

        calendarView = Calendar.getInstance();

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        SwipeBackHelper.getCurrentPage(this).setDisallowInterceptTouchEvent(true);

        dodajAutoCompleteTextView();
        findViews();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        favs = new sharedPrefsManager(this);
        DataSourcee.nastaviZadnjiIskani(this, vstopnaPostajaView, izstopnaPostajaView, koledar);

        pAdapter = new priljubljenePostajeAdapter(sharedPrefsManager.priljubljeneRelacije, this, vstopnaPostajaView, izstopnaPostajaView, favs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(pAdapter);

        onClickListeners();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favs.shraniPriljubljene();
        SwipeBackHelper.onDestroy(this);
    }

    private void updateLabel() {
        String format = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMAN);

        koledar.setText(sdf.format(calendarView.getTime()));
    }

    private void dodajAutoCompleteTextView() {
        DataSourcee.init(this);

        vstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.vstopna_text);
        izstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.izstopna_text);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, R.layout.autocomplete_list_item, DataSourcee.samoPostaje);
        vstopnaPostajaView.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.autocomplete_dropdown));
        izstopnaPostajaView.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.autocomplete_dropdown));

        vstopnaPostajaView.setThreshold(2);
        vstopnaPostajaView.setAdapter(adapter);
        izstopnaPostajaView.setThreshold(2);
        izstopnaPostajaView.setAdapter(adapter);

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) vstopnaPostajaView.getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Integer screenWidth = size.x;

        vstopnaPostajaView.setDropDownWidth(screenWidth - (lp.leftMargin + lp.rightMargin));
        izstopnaPostajaView.setDropDownWidth(screenWidth - (lp.leftMargin + lp.rightMargin));
    }

    private void onClickListeners() {
        // Izbrisi tekst v vnosu vstopne postaje
        delete_vp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vstopnaPostajaView.setText("", false);
            }
        });

        // Izbrisi tekst v vnosu izstopne postaje
        delete_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                izstopnaPostajaView.setText("", false);
            }
        });

        // Odpri koledar
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarView.set(Calendar.YEAR, year);
                calendarView.set(Calendar.MONTH, month);
                calendarView.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        // Gumb za iskanje urnika
        Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DataSourcee.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView, koledar.getText().toString());
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
                DataSourcee.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView, koledar.getText().toString());
            }
        });

        // Pokazi koledar
        koledar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date, calendarView.get(Calendar.YEAR), calendarView.get(Calendar.MONTH), calendarView.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void findViews() {
        vstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.vstopna_text);
        izstopnaPostajaView = (AutoCompleteTextView) findViewById(R.id.izstopna_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pogled_priljubljenih);
        koledar = (TextView) findViewById(R.id.textCalendar);
        delete_vp = (ImageView) findViewById(R.id.delete_vp);
        delete_ip = (ImageView) findViewById(R.id.delete_ip);
    }

    private void submit() {
        String vstopnaPostaja = vstopnaPostajaView.getText().toString();
        String izstopnaPostaja = izstopnaPostajaView.getText().toString();
        String vstopnaID = DataSourcee.getIDfromMap(vstopnaPostaja);
        String izstopnaID = DataSourcee.getIDfromMap(izstopnaPostaja);
        String date = koledar.getText().toString();

        if (vstopnaPostaja.equals(izstopnaPostaja) || vstopnaPostaja.equals("") || izstopnaPostaja.equals("")) {
            Snackbar.make(contextView, R.string.invalid_search, Snackbar.LENGTH_LONG).show();
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
