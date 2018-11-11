package com.lukag.atvoznired;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
    private Relacija relacija;
    private RecyclerView recyclerView;
    private ScheduleAdapter sAdapter;
    sharedPrefsManager favs;
    private ProgressBar progressBar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        SwipeBackHelper.onCreate(this);

        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        relacija = new Relacija();
        relacija.setFromID(prenos.get(0));
        relacija.setFromName(prenos.get(1));
        relacija.setToID(prenos.get(2));
        relacija.setToName(prenos.get(3));
        relacija.initUrnik();

        progressBar = (ProgressBar) findViewById(R.id.wait_animation);
        relativeLayout = (RelativeLayout)findViewById(R.id.schedule_heading) ;
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pogled_urnik);

        sAdapter = new ScheduleAdapter(relacija.getUrnik(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(sAdapter);

        setMarginsToHeading(this);
        POSTiT(relacija, prenos.get(4));
        sAdapter.notifyDataSetChanged();

        FloatingActionButton fab = findViewById(R.id.fabfav);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favs.dodajPriljubljeno(relacija)) {
                    Snackbar.make(view, R.string.fav_saved, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, R.string.fav_already_saved, Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
        favs = new sharedPrefsManager(this);
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
                novaPot.setStatus(schedule.getJSONObject(i).getString("STATUS").equals("pending"));
                relacija.urnikAdd(novaPot);
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }
    }

    private void setMarginsToHeading(Context context) {
        TextView start = (TextView)findViewById(R.id.starth);
        TextView end = (TextView)findViewById(R.id.endh);
        TextView length = (TextView)findViewById(R.id.lengthh);
        TextView duration = (TextView)findViewById(R.id.durationh);
        TextView cost = (TextView)findViewById(R.id.costh);

        Integer allMargins = 0;
        Integer displayWidth = 0;
        Integer contentWidth = DataSourcee.dpToPx(3*60+65+50);
        Integer layoutPadding = DataSourcee.dpToPx(32);
        Integer margins[] = new Integer[4];

        try {
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymatrics = new DisplayMetrics();
            display.getMetrics(displaymatrics);

            try{
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allMargins = displayWidth - (contentWidth + layoutPadding);
        if (allMargins < 0) {
            margins[0] = 0;
            margins[1] = 0;
            margins[2] = 0;
            margins[3] = 0;
        } else {
            margins[0] = allMargins / 10;
            margins[1] = 0;
            margins[2] = allMargins / 10;
            margins[3] = 0;
        }

        //Log.d("Margins", "allmargins: "+allMargins+" display: "+displayWidth+" content: "+contentWidth);

        RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)start.getLayoutParams();
        RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)end.getLayoutParams();
        RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)duration.getLayoutParams();
        RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)length.getLayoutParams();
        RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)cost.getLayoutParams();
        lpStart.setMargins      (margins[0],0, margins[2],0);
        lpEnd.setMargins        (margins[0],0, margins[2],0);
        lpDuration.setMargins   (margins[0],0, margins[2],0);
        lpLength.setMargins     (margins[0],0, margins[2],0);
        lpCost.setMargins       (margins[0],0, margins[2],0);
        start.setLayoutParams(lpStart);
        end.setLayoutParams(lpEnd);
        duration.setLayoutParams(lpDuration);
        length.setLayoutParams(lpLength);
        cost.setLayoutParams(lpCost);
    }

    private void returnToMainActivity() {
        ArrayList<String> prenos = new ArrayList<>();
        Intent intent = new Intent(DisplayMessageActivity.this, MainActivity.class);
        intent.putExtra("reason", "no_connection");
        startActivity(intent);
    }
}
