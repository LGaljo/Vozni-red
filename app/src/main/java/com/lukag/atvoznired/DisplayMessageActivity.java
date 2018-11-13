package com.lukag.atvoznired;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class DisplayMessageActivity extends AppCompatActivity {
    private Relacija iskanaRelacija;

    private favoritesManagement favs;

    private ScheduleAdapter sAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        SwipeBackHelper.onCreate(this);

        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        setFindViews();

        iskanaRelacija = new Relacija(prenos.get(0), prenos.get(1), prenos.get(2), prenos.get(3), null);
        iskanaRelacija.initUrnik();
        POSTiT(iskanaRelacija, prenos.get(4));

        sAdapter = new ScheduleAdapter(iskanaRelacija.getUrnik(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(sAdapter);

        setMarginsToHeading();
        sAdapter.notifyDataSetChanged();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favs.dodajPriljubljeno(iskanaRelacija)) {
                    Snackbar.make(view, R.string.fav_saved, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, R.string.fav_already_saved, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * V display message activity layoutu poisce iskane objekte
     */
    private void setFindViews() {
        progressBar = (ProgressBar) findViewById(R.id.wait_animation);
        progressBar.setVisibility(View.VISIBLE);

        relativeLayout = (RelativeLayout)findViewById(R.id.schedule_heading) ;
        relativeLayout.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pogled_urnik);

        fab = findViewById(R.id.fabfav);
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

    @Override
    protected void onStart() {
        super.onStart();
        favs = new favoritesManagement(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

            if (schedule.length() == 0) {
                // Med postajama ni povezave
                returnToMainActivity();
                this.finish();
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            for (int i = 0; i < schedule.length(); i++) {
                Pot novaPot = new Pot();
                novaPot.setID(Integer.parseInt(schedule.getJSONObject(i).getString("ID")));
                novaPot.setStart(schedule.getJSONObject(i).getString("ODHOD_FORMATED"));
                novaPot.setEnd(schedule.getJSONObject(i).getString("PRIHOD_FORMATED"));
                novaPot.setLength(schedule.getJSONObject(i).getString("KM_POT"));
                novaPot.setDuration(schedule.getJSONObject(i).getString("CAS_FORMATED"));
                novaPot.setCost(schedule.getJSONObject(i).getString("VZCL_CEN"));
                String statuss = schedule.getJSONObject(i).getString("STATUS");
                novaPot.setStatus(statuss.equals("pending"));
                iskanaRelacija.urnikAdd(novaPot);
                Log.d("JSON parse", iskanaRelacija.getFromName() + " -> " + iskanaRelacija.getToName() + " : " + statuss);
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }
    }

    /**
     * Metoda nastavi obrobe tekstovnih polj glave urnika
     */
    private void setMarginsToHeading() {
        TextView start = (TextView)findViewById(R.id.starth);
        TextView end = (TextView)findViewById(R.id.endh);
        TextView length = (TextView)findViewById(R.id.lengthh);
        TextView duration = (TextView)findViewById(R.id.durationh);
        TextView cost = (TextView)findViewById(R.id.costh);

        Integer allMargins = DataSourcee.calcMargins(this);

        RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)start.getLayoutParams();
        RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)end.getLayoutParams();
        RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)duration.getLayoutParams();
        RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)length.getLayoutParams();
        RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)cost.getLayoutParams();
        lpStart.setMargins      (allMargins,0, allMargins,0);
        lpEnd.setMargins        (allMargins,0, allMargins,0);
        lpDuration.setMargins   (allMargins,0, allMargins,0);
        lpLength.setMargins     (allMargins,0, allMargins,0);
        lpCost.setMargins       (allMargins,0, allMargins,0);
        start.setLayoutParams(lpStart);
        end.setLayoutParams(lpEnd);
        duration.setLayoutParams(lpDuration);
        length.setLayoutParams(lpLength);
        cost.setLayoutParams(lpCost);
    }

    /**
     * Metoda omogoča, da se v primeru, da iz strežnika ne dobim odgovora,
     * vrnem na glavni zaslon in izpisem opozorilo, da med postajama ni povezave
     */
    private void returnToMainActivity() {
        ArrayList<String> prenos = new ArrayList<>();
        Intent intent = new Intent(DisplayMessageActivity.this, MainActivity.class);
        intent.putExtra("reason", "no_connection");
        startActivity(intent);
    }
}
