package com.lukag.atvoznired;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prvi spinner
        Spinner spinner_vstop = (Spinner) findViewById(R.id.vstop_spin);
        ArrayAdapter<CharSequence> adapter_vstop = ArrayAdapter.createFromResource(this,
                R.array.vstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_vstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_vstop.setAdapter(adapter_vstop);

        //drugi spinner
        Spinner spinner_izstop = (Spinner) findViewById(R.id.izstop_spin);
        ArrayAdapter<CharSequence> adapter_izstop = ArrayAdapter.createFromResource(this,
                R.array.izstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_izstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_izstop.setAdapter(adapter_izstop);

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

                Log.d("Vhod","Sem ga dobil: " + vstop_text + " " + izstop_text); //to deluje

                if (vstop_text.equals(izstop_text)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Brezveznež")
                            .setMessage("Prosim vnesi različni postaji!?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    //tukaj kličem POSTit????
                    POSTiT("0129", "0855", "23.3.2017");
                }

            }
        });
    }

    public String VstopneFromAsset(int index) {
        String v_json = null;
        try {
            InputStream is = getAssets().open("vstopnePostaje.json");
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

    public void POSTiT(final String fromID, final String toID, final String date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.alpetour.si/wp-admin/admin-ajax.php";

        StringRequest POSTrequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "showRoutes");
                params.put("fromID", fromID);
                params.put("toID", toID);
                params.put("date", date);
                params.put("general", "false");

                return params;
            }
        };
        queue.add(POSTrequest);
    }
}
