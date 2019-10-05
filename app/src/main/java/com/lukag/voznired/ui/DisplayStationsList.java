package com.lukag.voznired.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;
import com.lukag.voznired.adapters.AutoCompleteAdapter;
import com.lukag.voznired.adapters.StationsListAdapter;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.models.ResponseDepartureStations;
import com.lukag.voznired.models.Station;
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
import static com.lukag.voznired.helpers.DataSourcee.pridobiCas;

public class DisplayStationsList extends AppCompatActivity {
    private static final String TAG = DisplayStationsList.class.getSimpleName();

    @BindView(R.id.stations_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.VozniRedToolbar)
    Toolbar toolbar;

    private StationsListAdapter mAdapter;
    private ArrayList<Station> stationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stations_list);
        ButterKnife.bind(this);
        SwipeBackHelper.onCreate(this);

        mAdapter = new StationsListAdapter(stationArrayList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        toolbar.setTitle(R.string.settings);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        fetchData();
    }

    private void fetchData() {
        String timestamp = pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);

        Retrofit retrofit = RetrofitFactory.getInstance(BASE_URL);
        APICalls apiCalls = retrofit.create(APICalls.class);

        Call<List<ResponseDepartureStations>> call = apiCalls.getDepartureStations(timestamp, token, "1");
        call.enqueue(new retrofit2.Callback<List<ResponseDepartureStations>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResponseDepartureStations>> call, @NonNull Response<List<ResponseDepartureStations>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ResponseDepartureStations responseDepartureStations = response.body().get(0);
                    if (!responseDepartureStations.getError().equals("0")) {
                        View contextView = findViewById(android.R.id.content);
                        Snackbar.make(contextView, getString(R.string.error_stations), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.try_again), (View v) -> {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    ProcessPhoenix.triggerRebirth(getApplicationContext(), intent);
                                }).show();
                    } else {
                        if (response.body().get(0).getDepartureStations().isEmpty()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_data_to_show), Toast.LENGTH_LONG).show();
                        } else {
                            stationArrayList.clear();
                            stationArrayList.addAll(response.body().get(0).getDepartureStations());
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResponseDepartureStations>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                View contextView = findViewById(android.R.id.content);
                Snackbar.make(contextView, getString(R.string.error_stations), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.try_again), (View v) -> {
                            Intent intent = new Intent(getApplicationContext(), DisplayStationsList.class);
                            ProcessPhoenix.triggerRebirth(getApplicationContext(), intent);
                        }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
