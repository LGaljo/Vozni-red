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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.atvoznired.Adapterji.ScheduleAdapter;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Relacija;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.UpravljanjeSPodatki.UpravljanjeSPriljubljenimi;
import com.lukag.atvoznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class Display_Schedule_Activity extends AppCompatActivity {
    private Relacija iskanaRelacija;

    private ScheduleAdapter sAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;
    private Toolbar toolbar;

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

        toolbar.setTitle(iskanaRelacija.getFromName() + " - " + iskanaRelacija.getToName());
        toolbar.setSubtitle(prenos.get(4));
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setSubtitleTextAppearance(getApplicationContext(), R.style.ToolbarSubTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        sAdapter = new ScheduleAdapter(iskanaRelacija, this);
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
        toolbar = findViewById(R.id.VozniRedToolbar);

        progressBar = findViewById(R.id.wait_animation);
        progressBar.setVisibility(View.VISIBLE);

        relativeLayout = findViewById(R.id.schedule_heading) ;
        relativeLayout.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recycler_view_pogled_urnik);

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
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartures.aspx";
        StringBuilder ClientId = new StringBuilder();
        ClientId.append("IMEI: ");
        ClientId.append(DataSourcee.getPhoneInfo(this));
        ClientId.append(" , MAC: ");
        ClientId.append(DataSourcee.getMacAddr(this));
        Log.d("API", timestamp + " " + token + " " + ClientId.toString() + " " + DataSourcee.getPhoneInfo(this));
        Log.d("API", relacija.getFromID() + " " + relacija.getToID() + " " + date);

        VolleyTool vt = new VolleyTool(this, url);

        vt.addParam("cTimeStamp", timestamp);
        vt.addParam("cToken", token);
        vt.addParam("JPOS_IJPPZ", relacija.getFromID());
        vt.addParam("JPOS_IJPPK", relacija.getToID());
        vt.addParam("VZVK_DAT", date); // datum oblike yyyy-MM-dd
        vt.addParam("ClientId", ClientId.toString()); // IMEI: <PHONE-ID> , MAC: <MAC-ADDRESS>
        vt.addParam("ClientIdType", DataSourcee.getPhoneInfo(this)); // IMEI
        vt.addParam("ClientLocationLatitude", "");
        vt.addParam("ClientLocationLongitude", "");
        vt.addParam("json", "1");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                if (response.equals("Error")) {
                    returnToMainActivity("Napaka!");
                }

                try {
                    JSONArray JSONresponse = new JSONArray(response);
                    iskanaRelacija.setUrnik(DataSourcee.parseJSONResponse(iskanaRelacija, JSONresponse).getUrnik());
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
            returnToMainActivity("no_connection");
            this.finish();
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
        TextView peron = (TextView)findViewById(R.id.peronh);

        Integer allMargins = DataSourcee.calcMargins(this);

        RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)start.getLayoutParams();
        RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)end.getLayoutParams();
        RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)duration.getLayoutParams();
        RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)length.getLayoutParams();
        RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)cost.getLayoutParams();
        RelativeLayout.LayoutParams lpPeron =    (RelativeLayout.LayoutParams)peron.getLayoutParams();
        lpStart.setMargins      (allMargins,0, allMargins,0);
        lpEnd.setMargins        (allMargins,0, allMargins,0);
        lpDuration.setMargins   (allMargins,0, allMargins,0);
        lpLength.setMargins     (allMargins,0, allMargins,0);
        lpCost.setMargins       (allMargins,0, allMargins,0);
        lpPeron.setMargins      (allMargins,0, allMargins,0);
        start.setLayoutParams(lpStart);
        end.setLayoutParams(lpEnd);
        duration.setLayoutParams(lpDuration);
        length.setLayoutParams(lpLength);
        cost.setLayoutParams(lpCost);
        peron.setLayoutParams(lpPeron);
    }

    /**
     * Metoda omogoča, da se v primeru, da iz strežnika ne dobim odgovora,
     * vrnem na glavni zaslon in izpisem opozorilo, da med postajama ni povezave
     */
    private void returnToMainActivity(String reason) {
        Intent intent = new Intent(Display_Schedule_Activity.this, MainActivity.class);
        intent.putExtra("reason", reason);
        startActivity(intent);
    }
}
