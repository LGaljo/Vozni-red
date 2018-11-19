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
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.atvoznired.Adapterji.ScheduleAdapter;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Relacija;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.UpravljanjeSPodatki.UpravljanjeSPriljubljenimi;
import com.lukag.atvoznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class DisplayMessageActivity extends AppCompatActivity {
    private Relacija iskanaRelacija;

    private ScheduleAdapter sAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;

    private UpravljanjeSPriljubljenimi favs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        SwipeBackHelper.onCreate(this);

        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        favs = UpravljanjeSPriljubljenimi.getInstance();

        setFindViews();

        iskanaRelacija = new Relacija(prenos.get(0), prenos.get(1), prenos.get(2), prenos.get(3), new ArrayList<Pot>());
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
        if (favs != null) {
            favs.shraniPriljubljene();
        }
        SwipeBackHelper.onDestroy(this);
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
        VolleyTool vt = new VolleyTool(this);

        //Log.d("Relacija", "Kličem relacijo " + rel.toString());
        vt.addParam("action", "showRoutes");
        vt.addParam("fromID", relacija.getFromID());
        vt.addParam("toID", relacija.getToID());
        vt.addParam("date", date);
        vt.addParam("general", "false");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                try {
                    JSONObject POSTreply = new JSONObject(response);
                    iskanaRelacija = DataSourcee.parseJSONResponse(iskanaRelacija, POSTreply);
                    checkUrnik();
                    relativeLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    sAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    returnToMainActivity("Napaka!");
                }
            }
        });
    }

    private void checkUrnik() {
        if (iskanaRelacija.getUrnik().size() == 0) {
            Log.d("POST", "Urnik je prazen");
            returnToMainActivity("no_connection");
            this.finish();
        } else {
            Log.d("POST", "Velikost urnika " + iskanaRelacija.getUrnik().size());
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
    private void returnToMainActivity(String reason) {
        ArrayList<String> prenos = new ArrayList<>();
        Intent intent = new Intent(DisplayMessageActivity.this, MainActivity.class);
        intent.putExtra("reason", reason);
        startActivity(intent);
    }
}
