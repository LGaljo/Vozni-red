package com.lukag.voznired.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;
import com.lukag.voznired.adapters.PostajeListAdapter;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.models.Departure;
import com.lukag.voznired.models.ResponseDepartureStationList;
import com.lukag.voznired.models.StationsList;
import com.lukag.voznired.retrofit_interface.APICalls;
import com.lukag.voznired.retrofit_interface.RetrofitFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.lukag.voznired.helpers.BuildConstants.BASE_URL;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_OVR_SIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_REG_ISIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_ROD_ZAPK;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_ROD_ZAPZ;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_SPOD_SIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VVLN_ZL;

public class DisplayRideInfo extends AppCompatActivity {
    private static final String TAG = DisplayRideInfo.class.getSimpleName();

    @BindView(R.id.recycler_view_postaje_list) RecyclerView recyclerView;
    @BindView(R.id.VozniRedToolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);
        ButterKnife.bind(this);

        SwipeBackHelper.onCreate(this);
        Intent intent = getIntent();
        String v_ime = intent.getStringExtra(INTENT_VSTOPNA_IME);
        String iz_ime = intent.getStringExtra(INTENT_IZSTOPNA_IME);

        toolbar.setTitle(v_ime + " - " + iz_ime);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        Departure departure = new Departure();
        departure.setOVR_SIF(intent.getStringExtra(INTENT_OVR_SIF));
        departure.setSPOD_SIF(intent.getIntExtra(INTENT_SPOD_SIF, 0));
        departure.setREG_ISIF(intent.getStringExtra(INTENT_REG_ISIF));
        departure.setVVLN_ZL(intent.getIntExtra(INTENT_VVLN_ZL, 0));
        departure.setROD_ZAPZ(intent.getStringExtra(INTENT_ROD_ZAPZ));
        departure.setROD_ZAPK(intent.getStringExtra(INTENT_ROD_ZAPK));

        pridobiPodrobnostiOVoznji(departure);
    }

    private void pridobiPodrobnostiOVoznji(Departure departure) {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);

        Retrofit retrofit = RetrofitFactory.getInstance(BASE_URL);
        APICalls apiCalls = retrofit.create(APICalls.class);

        Call<List<ResponseDepartureStationList>> call = apiCalls.getDepartureStationList(timestamp,
                token, Integer.toString(departure.getSPOD_SIF()), departure.getREG_ISIF(), departure.getOVR_SIF(),
                Integer.toString(departure.getVVLN_ZL()), departure.getROD_ZAPZ(), departure.getROD_ZAPK(), "1");

        call.enqueue(new retrofit2.Callback<List<ResponseDepartureStationList>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResponseDepartureStationList>> call, @NonNull Response<List<ResponseDepartureStationList>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ResponseDepartureStationList responseDepartureStationsList = response.body().get(0);
                    ArrayList<StationsList> stationsLists = responseDepartureStationsList.getDepartureStationList();
                    PostajeListAdapter postajeListAdapter = new PostajeListAdapter(stationsLists, getApplicationContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(postajeListAdapter);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResponseDepartureStationList>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
