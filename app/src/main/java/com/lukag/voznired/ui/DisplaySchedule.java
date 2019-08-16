package com.lukag.voznired.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;
import com.lukag.voznired.adapters.ScheduleAdapter;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.helpers.ManageFavs;
import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.models.ResponseDepartures;
import com.lukag.voznired.retrofit_interface.APICalls;
import com.lukag.voznired.retrofit_interface.RetrofitFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.lukag.voznired.helpers.BuildConstants.BASE_URL;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_DATUM;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;
import static com.lukag.voznired.helpers.DataSourcee.pridobiCas;

public class DisplaySchedule extends AppCompatActivity {
    private static final String TAG = DisplaySchedule.class.getSimpleName();

    @BindView(R.id.VozniRedToolbar)
    Toolbar toolbar;
    @BindView(R.id.wait_animation)
    ProgressBar progressBar;
    @BindView(R.id.schedule_heading)
    RelativeLayout relativeLayout;
    @BindView(R.id.recycler_view_pogled_urnik)
    RecyclerView recyclerView;
    @BindView(R.id.fabfav)
    FloatingActionButton fab;

    private Relacija iskanaRelacija;
    private ScheduleAdapter sAdapter;

    private ManageFavs favs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        SwipeBackHelper.onCreate(this);

        Intent intent = getIntent();
        String v_id = intent.getStringExtra(INTENT_VSTOPNA_ID);
        String iz_id = intent.getStringExtra(INTENT_IZSTOPNA_ID);
        String v_ime = intent.getStringExtra(INTENT_VSTOPNA_IME);
        String iz_ime = intent.getStringExtra(INTENT_IZSTOPNA_IME);
        String dat = intent.getStringExtra(INTENT_DATUM);

        iskanaRelacija = new Relacija(v_id, v_ime, iz_id, iz_ime, new ArrayList<>());

        favs = ManageFavs.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);

        // Nastaviš Toolbar in njegove lastnosti
        toolbar.setTitle(iskanaRelacija.getFromName() + " - " + iskanaRelacija.getToName());
        toolbar.setSubtitle(dat);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setSubtitleTextAppearance(getApplicationContext(), R.style.ToolbarSubTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pridobiUrnikMedPostajama2(iskanaRelacija, dat);

        setMarginsToHeading();

        fab.setOnClickListener(view -> {
            if (favs.dodajPriljubljeno(iskanaRelacija)) {
                Snackbar.make(view, R.string.fav_saved, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, R.string.fav_already_saved, Snackbar.LENGTH_LONG).show();
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
        TextView start = findViewById(R.id.starth);
        TextView end = findViewById(R.id.endh);
        TextView length = findViewById(R.id.lengthh);
        TextView duration = findViewById(R.id.durationh);
        TextView cost = findViewById(R.id.costh);
        TextView peron = findViewById(R.id.peronh);

        Integer allMargins = DataSourcee.calcMargins(this, 12);

        RelativeLayout.LayoutParams lpStart = (RelativeLayout.LayoutParams) start.getLayoutParams();
        RelativeLayout.LayoutParams lpEnd = (RelativeLayout.LayoutParams) end.getLayoutParams();
        RelativeLayout.LayoutParams lpDuration = (RelativeLayout.LayoutParams) duration.getLayoutParams();
        RelativeLayout.LayoutParams lpLength = (RelativeLayout.LayoutParams) length.getLayoutParams();
        RelativeLayout.LayoutParams lpCost = (RelativeLayout.LayoutParams) cost.getLayoutParams();
        RelativeLayout.LayoutParams lpPeron = (RelativeLayout.LayoutParams) peron.getLayoutParams();
        lpStart.setMargins(allMargins, 0, allMargins, 0);
        lpEnd.setMargins(allMargins, 0, allMargins, 0);
        lpDuration.setMargins(allMargins, 0, allMargins, 0);
        lpLength.setMargins(allMargins, 0, allMargins, 0);
        lpCost.setMargins(allMargins, 0, allMargins, 0);
        lpPeron.setMargins(allMargins, 0, allMargins, 0);
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
    public void pridobiUrnikMedPostajama2(Relacija relacija, String date) {
        Retrofit retrofit = RetrofitFactory.getInstance(BASE_URL);
        APICalls apiCalls = retrofit.create(APICalls.class);
        String timestamp = pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String ClientId = "IMEI: " + DataSourcee.getPhoneInfo(this) +
                " , MAC: " + DataSourcee.getMacAddr(this);

        Call<List<ResponseDepartures>> call = apiCalls.getDepartures(timestamp, token, relacija.getFromID(),
                relacija.getToID(), date, ClientId, DataSourcee.getPhoneInfo(this), "", "", "1");

        call.enqueue(new retrofit2.Callback<List<ResponseDepartures>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResponseDepartures>> call, @NonNull Response<List<ResponseDepartures>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ResponseDepartures responseDepartures = response.body().get(0);

                    int napakaID = Integer.parseInt(responseDepartures.getError());

                    if (napakaID != 0) {
                        String napakaMessage = responseDepartures.getErrorMsg();
                        Log.e("API Napaka", napakaMessage);

                        if (napakaID == 11) {
                            returnToMainActivity("no_connection");
                        } else {
                            returnToMainActivity("");
                        }
                    }

                    iskanaRelacija.setUrnik((responseDepartures.getDepartures()));

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date time = new Date();
                    try {
                        time = sdf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Nastaviš Adapter in RecyclerView
                    sAdapter = new ScheduleAdapter(iskanaRelacija, time, getApplicationContext());

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(sAdapter);

                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    sAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResponseDepartures>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
