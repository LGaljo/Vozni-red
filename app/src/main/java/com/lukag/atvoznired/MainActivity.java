package com.lukag.atvoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.lukag.atvoznired";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner_vstop = (Spinner) findViewById(R.id.vstop_spin);
        ArrayAdapter<CharSequence> adapter_vstop = ArrayAdapter.createFromResource(this,
                R.array.vstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_vstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_vstop.setAdapter(adapter_vstop);

        Spinner spinner_izstop = (Spinner) findViewById(R.id.izstop_spin);
        ArrayAdapter<CharSequence> adapter_izstop = ArrayAdapter.createFromResource(this,
                R.array.izstopne_postaje, android.R.layout.simple_spinner_item);
        adapter_izstop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_izstop.setAdapter(adapter_izstop);
    }

    public void POSTiT(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.alpetour.si/wp-admin/admin-ajax.php";

        StringRequest POSTrequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "showRoutes");
                params.put("fromID", "0129");
                params.put("toID", "0855");
                params.put("date", "22.3.2017");
                params.put("general", "false");

                return params;
            }
        };
        queue.add(POSTrequest);
    }
}
