package com.lukag.atvoznired.UpravljanjeSPodatki;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyTool {

    final String contentType = "application/json; charset=utf-8";
    String JsonURL = "https://www.alpetour.si/wp-admin/admin-ajax.php";
    Context context;
    private RequestQueue requestQueue;

    private Map<String, String> header;
    private Map<String, String> params;

    public VolleyTool(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.header = new HashMap<>();
        this.params = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public void executeRequest(int method, final VolleyCallback callback) {

        StringRequest stringRequest = new StringRequest(method, JsonURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.getResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Napaka pri pridobivanju podatkov");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return header;
            }
        };
        requestQueue.add(stringRequest);

    }

    public interface VolleyCallback {
        public void getResponse(String response);
    }
}