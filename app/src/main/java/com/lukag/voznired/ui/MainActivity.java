package com.lukag.voznired.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;
import com.lukag.voznired.adapters.AutoCompleteAdapter;
import com.lukag.voznired.adapters.PriljubljenePostajeAdapter;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.helpers.ManageFavs;
import com.lukag.voznired.helpers.ManageLastSearch;
import com.lukag.voznired.models.ResponseDepartureStations;
import com.lukag.voznired.models.Station;
import com.lukag.voznired.retrofit_interface.APICalls;
import com.lukag.voznired.retrofit_interface.RetrofitFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.lukag.voznired.helpers.BuildConstants.BASE_URL;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_DATUM;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.PEEK_DRAWER_START_DELAY_TIME_SECONDS;
import static com.lukag.voznired.helpers.BuildConstants.PEEK_DRAWER_TIME_SECONDS;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.vstopna_text) AutoCompleteTextView vstopnaPostajaView;
    @BindView(R.id.izstopna_text) AutoCompleteTextView izstopnaPostajaView;
    @BindView(R.id.recycler_view_pogled_priljubljenih) RecyclerView recyclerView;
    @BindView(R.id.textCalendar) TextView koledar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private Calendar calendarView;
    private DrawerLayout mDrawerLayout;

    public String datum;
    private HashMap<String, String> seznamPostaj = new HashMap<>();

    private SwipeRefreshLayout swipeContainer;
    private PriljubljenePostajeAdapter pAdapter;

    public static Runnable runs;
    public static Boolean sourcesFound = true;

    private ManageFavs favs;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);

        // Preverjanje, če se smo se vrnili nazaj zaradi napake v programu
        intentManager();

        // Nastavi SwipeBackHelper knjižnico
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        SwipeBackHelper.getCurrentPage(this).setDisallowInterceptTouchEvent(true);

        // Nastavi AutoCompleteTextView in mu pripne Custom Arrayadapter
        pridobiSeznam();

        // Pripravi instanco koledarja za uporabo
        setCalendarView();

        // Nastavi layout in listener za klike
        handleNavigationMenu();

        // Prepreci odpiranje tipkovnice ob zagonu
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // V vnosna polja nastavim zadnje iskane
        ManageLastSearch.nastaviZadnjiIskani(this, vstopnaPostajaView, izstopnaPostajaView);

        favs = ManageFavs.getInstance();
        favs.setContext(this);

        // Pripravim recyclerview za uporabo in nastavim instanco za uporabo SharedPref
        prikazPriljubljenihRecycler();

        // Pripravim SwipeContainer in njegove barve
        manageSwipeContainer();

        // Poskrbi za peek navigation drawerja
        prikaziNavDrawerHint();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        koledar.setText(DataSourcee.pridobiCas("dd.MM.yyyy"));
        datum = DataSourcee.pridobiCas("yyyy-MM-dd");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favs.shraniPriljubljene();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void intentManager() {
        Intent intent = getIntent();
        String reason = intent.getStringExtra("reason");
        View contextView = findViewById(R.id.priljubljene_text);

        if (reason != null && reason.equals("no_connection")) {
            Snackbar.make(contextView, R.string.no_connection, Snackbar.LENGTH_LONG).show();
        } else if (reason != null) {
            Snackbar.make(contextView, R.string.error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void prikazPriljubljenihRecycler() {
        // Pripravi RecyclerView za prikaz priljubljenih relacij
        pAdapter = new PriljubljenePostajeAdapter(ManageFavs.priljubljeneRelacije, this, vstopnaPostajaView, izstopnaPostajaView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(pAdapter);
        checkForNewRides();

        recyclerView.post(runs);
    }

    /**
     * S to metodo nastaviš swipe container in kličeš runnable, da posodobi senznam
     */
    private void manageSwipeContainer() {
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> runs.run());
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Nastaviš navigation drawer in nastaviš OnNavigationItemSelectedListener,
     * da lahko prehajaš med aktivnostmi
     */
    private void handleNavigationMenu() {
        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.first_screen:
                            break;
                        case R.id.nav_info:
                            Intent apinfointent = new Intent(this, DisplayAppInfo.class);
                            startActivity(apinfointent);
                            break;
                        case R.id.nav_settings:
                            Intent gotosettings = new Intent(this, SettingsActivity.class);
                            startActivity(gotosettings);
                            break;
                        default:
                            break;
                    }
                    menuItem.setChecked(false);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }

    /**
     * Prikazi namig, da obstaja Navigation Drawer. Pokaži ga samo 3 krat.
     */
    private void prikaziNavDrawerHint() {
        SharedPreferences peekCount = getSharedPreferences("peekDrawer", Context.MODE_PRIVATE);

        int numberOfEvents = peekCount.getInt("num", -1);

        if (numberOfEvents == -1) {
            SharedPreferences.Editor urejevalnik = peekCount.edit();
            urejevalnik.putInt("num", 3);
            urejevalnik.apply();
            // Zamakni pričetek animacije za pojavljanje navigation drawerja
            new Handler().postDelayed(this::peekDrawer, (long) (PEEK_DRAWER_START_DELAY_TIME_SECONDS));
        } else if (numberOfEvents > 0) {
            // Zamakni pričetek animacije za pojavljanje navigation drawerja
            new Handler().postDelayed(this::peekDrawer, (long) (PEEK_DRAWER_START_DELAY_TIME_SECONDS));
            SharedPreferences.Editor urejevalnik = peekCount.edit();
            urejevalnik.putInt("num", numberOfEvents - 1);
            urejevalnik.apply();
        }
    }

    private void peekDrawer() {
        float x = 0.0f;
        float y = 100.0f;
        int metaState = 0;

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);
        mDrawerLayout.dispatchTouchEvent(motionEvent);
        motionEvent.recycle();

        new Handler().postDelayed(() -> {
            long downTime2 = SystemClock.uptimeMillis();
            long eventTime2 = SystemClock.uptimeMillis() + 100;
            MotionEvent motionEvent1 = MotionEvent.obtain(downTime2, eventTime2, MotionEvent.ACTION_UP, x, y, metaState);
            mDrawerLayout.dispatchTouchEvent(motionEvent1);
            motionEvent1.recycle();
        }, (long) (PEEK_DRAWER_TIME_SECONDS));
    }

    /**
     * Metoda preveri pravilnost vnosa podatkov in
     * sestavi ArrayList za prenos podatkov
     */
    @OnClick(R.id.submit)
    public void submit() {
        // Gumb za iskanje urnika
        ManageLastSearch.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView);

        String vstopnaPostaja = vstopnaPostajaView.getText().toString();
        String izstopnaPostaja = izstopnaPostajaView.getText().toString();
        String vstopnaID = seznamPostaj.get(vstopnaPostaja);
        String izstopnaID = seznamPostaj.get(izstopnaPostaja);

        if (vstopnaPostaja.equals("") || izstopnaPostaja.equals("")) {
            Toast.makeText(this, R.string.empty_search, Toast.LENGTH_LONG).show();
        } else if (vstopnaID == null || izstopnaID == null) {
            Toast.makeText(this, getString(R.string.error_search), Toast.LENGTH_LONG).show();
        } else if (vstopnaPostaja.equals(izstopnaPostaja)) {
            Toast.makeText(this, R.string.duplicated_search, Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, DisplaySchedule.class);
            intent.putExtra(INTENT_VSTOPNA_ID, vstopnaID);
            intent.putExtra(INTENT_VSTOPNA_IME, vstopnaPostaja);
            intent.putExtra(INTENT_IZSTOPNA_ID, izstopnaID);
            intent.putExtra(INTENT_IZSTOPNA_IME, izstopnaPostaja);
            intent.putExtra(INTENT_DATUM, datum);
            startActivity(intent);
        }
    }

    @OnClick(R.id.delete_vp)
    public void delete_vp() {
        // Izbrisi tekst v vnosu vstopne postaje
        vstopnaPostajaView.setText("", false);
    }

    @OnClick(R.id.delete_ip)
    public void delete_ip() {
        // Izbrisi tekst v vnosu izstopne postaje
        izstopnaPostajaView.setText("", false);

    }

    @OnClick(R.id.swap)
    public void swap() {
        // Gumb za zamenjavo postajalisc
        String tmp = izstopnaPostajaView.getText().toString();
        izstopnaPostajaView.setText(vstopnaPostajaView.getText(), false);
        vstopnaPostajaView.setText(tmp, false);
        ManageLastSearch.shraniZadnjiIskani(MainActivity.this, vstopnaPostajaView, izstopnaPostajaView);
    }

    @OnClick(R.id.textCalendar)
    public void showCalendarPicker() {
        new DatePickerDialog(MainActivity.this, date, calendarView.get(Calendar.YEAR),
                calendarView.get(Calendar.MONTH), calendarView.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * To je metoda, ki z API klicem pridobi seznam vseh postaj in jih shrani v adapter
     */
    private void pridobiSeznam() {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
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
                        Snackbar.make(contextView, "Napaka 1 pri pridobivanju postaj", Snackbar.LENGTH_INDEFINITE).show();
                    } else {
                        progressBar.setVisibility(View.GONE);

                        ArrayList<Station> stations = response.body().get(0).getDepartureStations();

                        for (Station s : stations) {
                            seznamPostaj.put(s.getPOS_NAZ(), s.getJPOS_IJPP());
                        }

                        AutoCompleteAdapter aca = new AutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item, stations);
                        AutoCompleteAdapter aca2 = new AutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item, stations);
                        vstopnaPostajaView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.autocomplete_dropdown));
                        izstopnaPostajaView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.autocomplete_dropdown));

                        vstopnaPostajaView.setThreshold(2);
                        vstopnaPostajaView.setAdapter(aca);
                        izstopnaPostajaView.setThreshold(2);
                        izstopnaPostajaView.setAdapter(aca2);

                        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) vstopnaPostajaView.getLayoutParams();

                        vstopnaPostajaView.setDropDownWidth(screenWidth() - (lp.leftMargin + lp.rightMargin));
                        izstopnaPostajaView.setDropDownWidth(screenWidth() - (lp.leftMargin + lp.rightMargin));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResponseDepartureStations>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                View contextView = findViewById(android.R.id.content);
                Snackbar.make(contextView, "Napaka 2 pri pridobivanju postaj", Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    /**
     * Metoda vrne sirino zaslona
     */
    private Integer screenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    /**
     * Metoda pripravi koledar
     */
    private void setCalendarView() {
        calendarView = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {
            String format = "dd.MM.yyyy";
            String formatApi = "yyyy-MM-dd";
            SimpleDateFormat textBoxFormat = new SimpleDateFormat(format, Locale.GERMAN);
            SimpleDateFormat ApiFormat = new SimpleDateFormat(formatApi, Locale.GERMAN);

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarView.set(Calendar.YEAR, year);
                calendarView.set(Calendar.MONTH, month);
                calendarView.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                koledar.setText(textBoxFormat.format(calendarView.getTime()));
                datum = ApiFormat.format(calendarView.getTime());
            }
        };
    }

    private void checkForNewRides() {
        runs = () -> {
            // a potentially time consuming task
            while (sourcesFound) {
                // wait
            }
            DataSourcee.findNextRides(MainActivity.this, pAdapter);
            swipeContainer.setRefreshing(false);
        };
    }
}
