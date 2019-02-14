package com.lukag.atvoznired.Objekti;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Postaje {
    public static HashMap<String, String> seznamPostaj = new HashMap<>();
    public String[] seznamImenPostaj = new String[0];
    private Context context;
    private ArrayAdapter<String> adapter;

    public Postaje(Context context, ArrayAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public void dodajPostaje() {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);

        VolleyTool vt = new VolleyTool(this.context, "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_DepartureStations.aspx");

        vt.addParam("cTimeStamp", timestamp);
        vt.addParam("cToken", token);
        vt.addParam("json", "1");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                try {
                    parsajPostaje(new JSONArray(response));
                    Log.d("Postaje", Integer.toString(seznamImenPostaj.length));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void parsajPostaje(JSONArray jsonArray) {
        try {
            JSONObject responseObj = jsonArray.getJSONObject(0);
            int napakaID = responseObj.getInt("Error");

            if (napakaID != 0) {
                String napakaMessage = responseObj.getString("ErrorMsg");
                Log.e("API", napakaMessage);
            }

            JSONArray postaje = responseObj.getJSONArray("DepartureStations");

            if (postaje != null) {
                this.seznamImenPostaj = new String[postaje.length()];

                for (int i = 0; i < postaje.length(); i++) {
                    JSONObject postaja = postaje.getJSONObject(i);
                    String idPostaje = postaja.getString("JPOS_IJPP");
                    String imePostaje = postaja.getString("POS_NAZ");
                    seznamPostaj.put(imePostaje, idPostaje);
                    this.seznamImenPostaj[i] = imePostaje;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
