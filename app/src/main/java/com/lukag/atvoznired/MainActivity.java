package com.lukag.atvoznired;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dodajDanasnjiDan();
        dodajAutoCompleteTextView();

        // Gumb za prikaz
        final Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                submit();
            }
        });

        Button invert = (Button) findViewById(R.id.invert);
        invert.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                TextView vstopnaPostajaView = (TextView) findViewById(R.id.vstopna_text);
                TextView izstopnaPostajaView = (TextView) findViewById(R.id.izstopna_text);

                String tmp = izstopnaPostajaView.getText().toString();
                izstopnaPostajaView.setText(vstopnaPostajaView.getText());
                vstopnaPostajaView.setText(tmp);
            }
        });
    }

    private void dodajDanasnjiDan() {
        // Današnji datum
        Calendar c = Calendar.getInstance();
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        String formattedDate = today.format(c.getTime());
        EditText koledar = (EditText)findViewById(R.id.textCalendar);
        koledar.setText(formattedDate.toString());
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

    private void submit() {
        TextView vstopnaPostajaView = (TextView) findViewById(R.id.vstopna_text);
        TextView izstopnaPostajaView = (TextView) findViewById(R.id.izstopna_text);

        String vstopnaPostaja = vstopnaPostajaView.getText().toString();
        String izstopnaPostaja = izstopnaPostajaView.getText().toString();
        String vstopnaID = DataSourcee.getIDfromMap(vstopnaPostaja);
        String izstopnaID = DataSourcee.getIDfromMap(izstopnaPostaja);

        Log.d("Autocomplete", vstopnaPostaja + ": " + vstopnaID);
        Log.d("Autocomplete", izstopnaPostaja + ": " + izstopnaID);

        // Datum
        EditText koledar = (EditText)findViewById(R.id.textCalendar);
        String date = koledar.getText().toString();

        if (vstopnaPostaja.equals(izstopnaPostaja)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Opozorilo")
                    .setMessage("Prosim vnesi različni postaji")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
