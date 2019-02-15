package com.lukag.atvoznired;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class DisplayRideInfo extends AppCompatActivity {
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);

        text = (TextView) findViewById(R.id.text);

        SwipeBackHelper.onCreate(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.VozniRedToolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        BuildConstants buildConstants = BuildConstants.getInstance();
        pridobiPodrobnostiOVoznji(buildConstants.relacija.getUrnik().get(Integer.parseInt(prenos.get(0))));
    }

    private void pridobiPodrobnostiOVoznji(Pot pot) {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartureStationList.aspx";
        StringBuilder ClientId = new StringBuilder();
        ClientId.append("IMEI: ");
        ClientId.append(DataSourcee.getPhoneInfo(this));
        ClientId.append(" , MAC: ");
        ClientId.append(DataSourcee.getMacAddr(this));

        VolleyTool vt = new VolleyTool(this, url);

        vt.addParam("cTimeStamp", timestamp);
        vt.addParam("cToken", token);
        vt.addParam("SPOD_SIF", Integer.toString(pot.getSpod_sif()));
        vt.addParam("REG_ISIF", pot.getReg_isif());
        vt.addParam("OVR_SIF", pot.getOvr_sif());
        vt.addParam("VVLN_ZL", Integer.toString(pot.getVvln_zl()));
        vt.addParam("ROD_ZAPZ", pot.getRod_zapz());
        vt.addParam("ROD_ZAPK", pot.getRod_zapk());
        vt.addParam("json", "1");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                try {
                    JSONArray JSONresponse = new JSONArray(response);
                    text.append(response);
                } catch (JSONException e) {
                    e.printStackTrace();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
