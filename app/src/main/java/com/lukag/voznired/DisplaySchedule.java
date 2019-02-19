package com.lukag.voznired;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.Adapterji.ScheduleAdapter;
import com.lukag.voznired.Objekti.Pot;
import com.lukag.voznired.Objekti.Relacija;
import com.lukag.voznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.voznired.UpravljanjeSPodatki.UpravljanjeSPriljubljenimi;

import java.util.ArrayList;

import static com.lukag.voznired.MainActivity.EXTRA_MESSAGE;

public class DisplaySchedule extends AppCompatActivity {
    private Relacija iskanaRelacija;

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
        ArrayList<String> prenos = intent.getStringArrayListExtra(EXTRA_MESSAGE);

        favs = UpravljanjeSPriljubljenimi.getInstance();

        setFindViews();

        iskanaRelacija = new Relacija(prenos.get(0), prenos.get(1), prenos.get(2), prenos.get(3), new ArrayList<Pot>());

        // Nastaviš Toolbar in njegove lastnosti
        toolbar.setTitle(iskanaRelacija.getFromName() + " - " + iskanaRelacija.getToName());
        toolbar.setSubtitle(prenos.get(4));
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setSubtitleTextAppearance(getApplicationContext(), R.style.ToolbarSubTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        // Nastaviš Adapter in RecyclerView
        ScheduleAdapter sAdapter = new ScheduleAdapter(iskanaRelacija, prenos.get(4), this);
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

        ProgressBar progressBar = findViewById(R.id.wait_animation);
        progressBar.setVisibility(View.VISIBLE);

        RelativeLayout relativeLayout = findViewById(R.id.schedule_heading) ;
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
     * Metoda omogoča, da se v primeru, da iz strežnika ne dobim odgovora,
     * vrnem na glavni zaslon in izpisem opozorilo, da med postajama ni povezave
     */
    private void returnToMainActivity(String reason) {
        Intent intent = new Intent(DisplaySchedule.this, MainActivity.class);
        intent.putExtra("reason", reason);
        startActivity(intent);
    }
}
