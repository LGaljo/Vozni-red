package com.lukag.atvoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Map;

public class DisplayMessageActivity extends AppCompatActivity {
    private Relacija relacija;
    private RecyclerView recyclerView;
    private ScheduleAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(MainActivity.EXTRA_MESSAGE);

        relacija = new Relacija();
        relacija.setFromID(prenos.get(0));
        relacija.setFromName(prenos.get(1));
        relacija.setToID(prenos.get(2));
        relacija.setToName(prenos.get(3));
        relacija.initUrnik();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        sAdapter = new ScheduleAdapter(relacija.getUrnik(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(sAdapter);

        POSTiT(relacija, prenos.get(4));
        sAdapter.notifyDataSetChanged();
    }

    /**
     * Metoda od serverja zahteva podatke o voznem redu
     * @param relacija Objekt, ki hrani podatke o relaciji
     * @param date Datum potovanja
     */
    public void POSTiT(final Relacija relacija, final String date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.alpetour.si/wp-admin/admin-ajax.php";

        StringRequest POSTrequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject POSTreply = new JSONObject(response);
                            parseJSONResponse(POSTreply);
                            sAdapter.notifyDataSetChanged();
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
                params.put("fromID", relacija.getFromID());
                params.put("toID",relacija.getToID());
                params.put("date", date);
                params.put("general", "false");
                Log.d("Volley", params.toString());
                return params;
            }
        };
        queue.add(POSTrequest);
    }

    /**
     * Parsaj odgovor iz strežnika
     * @param resp JSONObject - odgovor strežnika
     */
    public void parseJSONResponse(JSONObject resp) {
        try {
            String status = resp.get("status").toString();
            String message = resp.get("message").toString();
            Log.d("Status","Strežnik je vrnil " + status + ": " + message);

            JSONArray schedule = resp.getJSONArray("schedule");

            for (int i = 0; i < schedule.length(); i++) {
                Pot novaPot = new Pot();
                novaPot.setID(Integer.parseInt(schedule.getJSONObject(i).getString("ID")));
                novaPot.setStart(schedule.getJSONObject(i).getString("ODHOD_FORMATED"));
                novaPot.setEnd(schedule.getJSONObject(i).getString("PRIHOD_FORMATED"));
                novaPot.setLength(schedule.getJSONObject(i).getString("KM_POT"));
                novaPot.setDuration(schedule.getJSONObject(i).getString("CAS_FORMATED"));
                novaPot.setCost(schedule.getJSONObject(i).getString("VZCL_CEN"));
                novaPot.setStatus(schedule.getJSONObject(i).getString("STATUS").equals("pending"));
                relacija.urnikAdd(novaPot);
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }
    }
}
