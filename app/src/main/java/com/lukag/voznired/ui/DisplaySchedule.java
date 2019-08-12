package com.lukag.voznired.ui;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;
import com.lukag.voznired.adapters.ScheduleAdapter;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.helpers.UpravljanjeSPriljubljenimi;
import com.lukag.voznired.helpers.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lukag.voznired.helpers.BuildConstants.INTENT_DATUM;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;

public class DisplaySchedule extends AppCompatActivity {
    private Relacija iskanaRelacija;
    private ScheduleAdapter sAdapter;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    private UpravljanjeSPriljubljenimi favs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SwipeBackHelper.onCreate(this);

        Intent intent = getIntent();
        String v_id = intent.getStringExtra(INTENT_VSTOPNA_ID);
        String iz_id = intent.getStringExtra(INTENT_IZSTOPNA_ID);
        String v_ime = intent.getStringExtra(INTENT_VSTOPNA_IME);
        String iz_ime = intent.getStringExtra(INTENT_IZSTOPNA_IME);
        String dat = intent.getStringExtra(INTENT_DATUM);

        favs = UpravljanjeSPriljubljenimi.getInstance();

        setFindViews();

        iskanaRelacija = new Relacija(v_id, iz_id,v_ime, iz_ime, new ArrayList<>());

        pridobiUrnikMedPostajama(iskanaRelacija, dat);
        // Nastaviš Toolbar in njegove lastnosti
        toolbar.setTitle(iskanaRelacija.getFromName() + " - " + iskanaRelacija.getToName());
        toolbar.setSubtitle(dat);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setSubtitleTextAppearance(getApplicationContext(), R.style.ToolbarSubTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        // Nastaviš Adapter in RecyclerView
        sAdapter = new ScheduleAdapter(iskanaRelacija,  this);
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
     * Metoda nastavi obrobe tekstovnih polj glave urnika
     */
    private void setMarginsToHeading() {
        TextView start = (TextView)findViewById(R.id.starth);
        TextView end = (TextView)findViewById(R.id.endh);
        TextView length = (TextView)findViewById(R.id.lengthh);
        TextView duration = (TextView)findViewById(R.id.durationh);
        TextView cost = (TextView)findViewById(R.id.costh);
        TextView peron = (TextView)findViewById(R.id.peronh);

        Integer allMargins = DataSourcee.calcMargins(this, 12);

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
     * Metoda od serverja zahteva podatke o voznem redu
     */
    public void pridobiUrnikMedPostajama(Relacija relacija, String date) {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartures.aspx";
        StringBuilder ClientId = new StringBuilder();
        ClientId.append("IMEI: ");
        ClientId.append(DataSourcee.getPhoneInfo(this));
        ClientId.append(" , MAC: ");
        ClientId.append(DataSourcee.getMacAddr(this));
        //Log.d("API", timestamp + " " + token + " " + ClientId.toString() + " " + DataSourcee.getPhoneInfo(this));
        //Log.d("API", relacija.getFromID() + " " + relacija.getToID() + " " + date);

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
                try {
                    JSONArray JSONresponse = new JSONArray(response);

                    JSONObject responseObj = JSONresponse.getJSONObject(0);
                    int napakaID = responseObj.getInt("Error");

                    if (napakaID != 0) {
                        String napakaMessage = responseObj.getString("ErrorMsg");
                        Log.e("API Napaka", napakaMessage);

                        if (napakaID == 11) {
                            returnToMainActivity("no_connection");
                        } else {
                            returnToMainActivity("");
                        }
                    }

                    //Log.d("API", response);
                    iskanaRelacija.setUrnik(DataSourcee.parseVozniRed(iskanaRelacija, JSONresponse).getUrnik());

                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    sAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Metoda omogoča, da se v primeru, da iz strežnika ne dobim odgovora,
     * vrnem na glavni zaslon in izpisem opozorilo, da med postajama ni povezave
     */
    private void returnToMainActivity(String reason) {
        Intent intent = new Intent(DisplaySchedule.this, MainActivity.class);
        intent.putExtra("reason", reason);
        startActivity(intent);
    }
}
