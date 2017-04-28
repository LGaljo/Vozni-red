package com.lukag.atvoznired;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    public static String date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Današnji datum
        Calendar c = Calendar.getInstance();
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = today.format(c.getTime());
        EditText text = (EditText)findViewById(R.id.textCalendar);
        text.setText(formattedDate);

        // Prvi spinner
        Spinner spinner_vstop = (Spinner) findViewById(R.id.vstop_spin);
        ArrayAdapter<CharSequence> adapter_vstop = ArrayAdapter.createFromResource(this,
                R.array.vstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_vstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_vstop.setAdapter(adapter_vstop);

        // Drugi spinner
        Spinner spinner_izstop = (Spinner) findViewById(R.id.izstop_spin);
        ArrayAdapter<CharSequence> adapter_izstop = ArrayAdapter.createFromResource(this,
                R.array.izstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_izstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_izstop.setAdapter(adapter_izstop);

        // Koledar
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(24)
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, myDateListener, year, month, day);
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }

            private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker arg0, int year, int month, int day) {
                            date = day + "." + month + "." + year;
                        }
                    };
        });

        // Gumb za prikaz
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dobim spinnerja
                Spinner spinner_vstop = (Spinner) findViewById(R.id.vstop_spin);
                Spinner spinner_izstop = (Spinner) findViewById(R.id.izstop_spin);

                Log.d("Preverba", "Preverjam ali gumb deluje"); //to deluje

                //primerjam podatke v spinnerju z JSONom
                String vstop_text = spinner_vstop.getSelectedItem().toString(); //podatek od uporabnika
                String izstop_text = spinner_izstop.getSelectedItem().toString(); //podatek od uporabnika

                Log.d("Vhod","Sem ga dobil: " + vstop_text + " " + izstop_text);

                String vstopnaPostaja = null;
                String izstopnaPostaja = null;

                // Datum
                EditText text = (EditText)findViewById(R.id.textCalendar);
                String date = text.getText().toString();

                if (vstop_text.equals(izstop_text)) {
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
                    try {
                        JSONObject objV = new JSONObject(postajeFromAsset(1));
                        JSONObject objIZ = new JSONObject(postajeFromAsset(2));
                        vstopnaPostaja = objV.getString(vstop_text);
                        izstopnaPostaja = objIZ.getString(izstop_text);
                        Log.d("Iskanje", "Našel sem: " + vstopnaPostaja + " " + izstopnaPostaja);

                    } catch(JSONException e) {
                        Log.e("JSON Parser", "Napaka pri pridobivanju podatkov");
                    }
                    Log.i("Postaje", "Vstopna postaja: " + vstopnaPostaja + " Izstopna postaja: " + izstopnaPostaja);

                    /*
                    V nov activity pošljem podatke, ki jih je izbral uporabnik
                    */
                    ArrayList<String> prenos = new ArrayList<>();
                    prenos.add(0, vstopnaPostaja);
                    prenos.add(1, izstopnaPostaja);
                    prenos.add(2, date);
                    //prenos.add(0, "0129");
                    //prenos.add(1, "0855");
                    //prenos.add(2, "28.4.2017");
                    Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                    intent.putStringArrayListExtra(EXTRA_MESSAGE, prenos);
                    startActivity(intent);
                }
            }
        });
    }

    /*
    Metoda, ki mi iz JSON datoteke prebere vse postaje
    Vrne mi string v katerem je postaja
     */
    public String postajeFromAsset(int index) {
        String v_json = null;
        try {
            InputStream is = null;
            if (index == 1) {
                is = getAssets().open("vstopnePostaje.json");
            } else {
                is = getAssets().open("izstopnePostaje.json");
            }
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            v_json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return v_json;
    }
}
