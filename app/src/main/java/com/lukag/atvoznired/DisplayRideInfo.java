package com.lukag.atvoznired;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.atvoznired.Adapterji.PostajeListAdapter;
import com.lukag.atvoznired.Objekti.BuildConstants;

import java.util.ArrayList;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class DisplayRideInfo extends AppCompatActivity {
    private TextView text;
    private PostajeListAdapter postajeListAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);

        text = (TextView) findViewById(R.id.text);
        BuildConstants buildConstants = BuildConstants.getInstance();

        SwipeBackHelper.onCreate(this);
        Intent intent = getIntent();
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.VozniRedToolbar);
        toolbar.setTitle(buildConstants.relacija.getFromName() + " - " + buildConstants.relacija.getToName());
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        postajeListAdapter = new PostajeListAdapter(buildConstants.relacija.getUrnik().get(Integer.parseInt(prenos.get(0))), this);
        recyclerView = findViewById(R.id.recycler_view_postaje_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postajeListAdapter);
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
