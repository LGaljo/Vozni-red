package com.lukag.atvoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DisplayMessageActivity extends AppCompatActivity {
    public static JSONObject POSTreply = null;
    private static String[][] vozniRed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        /*
        Dobim intent iz drugega activitya
         */
        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(MainActivity.EXTRA_MESSAGE);
        POSTiT(prenos.get(0), prenos.get(1), prenos.get(2));/*
        while (POSTreply == null) {
            Log.d("Strežnik", "Čakam na odziv");
        }*/

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(constructMessage());
    }

    // Kreiram JSON objekt
    public void SetVozniRed(JSONObject reply) {
        String status = null;
        String message = null;

        try {
            status = reply.get("status").toString();
            message = reply.get("message").toString();
            Log.d("Status","Strežnik je vrnil " + status + ": " + message);
            JSONArray schedule = reply.getJSONArray("schedule");

            vozniRed = new String[schedule.length()][6];

            for (int i = 0; i < schedule.length(); i++) {
                vozniRed[i][0] = schedule.getJSONObject(i).getString("ID");
                vozniRed[i][1] = schedule.getJSONObject(i).getString("ODHOD_FORMATED");
                vozniRed[i][2] = schedule.getJSONObject(i).getString("PRIHOD_FORMATED");
                vozniRed[i][3] = schedule.getJSONObject(i).getString("KM_POT");
                vozniRed[i][4] = schedule.getJSONObject(i).getString("CAS_FORMATED");
                vozniRed[i][5] = schedule.getJSONObject(i).getString("STATUS");
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }
    }

    /*
    Metoda od serverja zahteva podatke o voznem redu
    */
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
                        Log.d("ERROR", "error => " + error.toString());
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

    public String constructMessage() {
        String message = "start     stop   km  min  status \n";
        int length;
        if (vozniRed != null) {
            length = vozniRed.length; // Lahko je tudi null
            Log.d("Dolzina", "Array je dolg: " + length);
        } else {
            Log.d("Dolzina", "Array je prazen");
            length = 0;
        }
        for (int i = 0; i < length; i++) {
            for (int j = 1; j < 6; j++) {
                if (vozniRed[i][1].length() < 2 && j == 0) {
                    message += " " + vozniRed[i][j] + "  ";
                } else {
                    message += vozniRed[i][j] + "  ";
                }
            }
            message += "\n";
        }
    return message;
    }
}
