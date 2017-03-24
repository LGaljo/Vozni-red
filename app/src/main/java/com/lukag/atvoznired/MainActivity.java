package com.lukag.atvoznired;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
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
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";

    public JSONObject POSTreply = null;

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

                String vstopnaPostaja = null;
                String izstopnaPostaja = null;

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
                    POSTiT(vstopnaPostaja, izstopnaPostaja, "24.3.2017");
                }

            }
        });
    }

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

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("reply.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void SetVozniRed(JSONObject reply) {

        HashMap<String, String> vozniRed = new HashMap<>();
        vozniRed.put("key", "value");
        String status = null;
        String message = null;

        try {
            status = reply.get("status").toString();
            message = reply.get("message").toString();
            Log.d("Status","Strežnik je vrnil " + status + ": " + message);
            JSONArray schedule = reply.getJSONArray("schedule");

            for (int i = 0; i < schedule.length(); i++) {
                vozniRed.put("ID", schedule.getJSONObject(i).getString("ID"));
                vozniRed.put("ODHOD_FORMATED", schedule.getJSONObject(i).getString("ODHOD_FORMATED"));
                vozniRed.put("PRIHOD_FORMATED", schedule.getJSONObject(i).getString("PRIHOD_FORMATED"));
                vozniRed.put("KM_POT", schedule.getJSONObject(i).getString("KM_POT"));
                vozniRed.put("CAS_FORMATED", schedule.getJSONObject(i).getString("CAS_FORMATED"));
                vozniRed.put("STATUS", schedule.getJSONObject(i).getString("STATUS"));
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }

        Intent intent;
        intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, vozniRed);
        startActivity(intent);
    }

    public void POSTiT(final String fromID, final String toID, final String date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.alpetour.si/wp-admin/admin-ajax.php";

        StringRequest POSTrequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            POSTreply = new JSONObject(response);
                            SetVozniRed(POSTreply);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
