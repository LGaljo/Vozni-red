package com.lukag.voznired.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.volley.Request;
import com.lukag.voznired.helpers.BuildConstants;
import com.lukag.voznired.SettingsActivity;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.helpers.VolleyTool;
import com.lukag.voznired.models.ResponseDepartureStations;
import com.lukag.voznired.models.Station;
import com.lukag.voznired.retrofit_interface.APICalls;
import com.lukag.voznired.retrofit_interface.RetrofitFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.lukag.voznired.helpers.BuildConstants.BASE_URL;
import static com.lukag.voznired.helpers.BuildConstants.seznamPostaj;

public class AutoCompleteAdapter extends ArrayAdapter<Station> implements Filterable {
    private static final String TAG = AutoCompleteAdapter.class.getSimpleName();

    private ArrayList<Station> seznamPostaj;
    private ArrayList<Station> suggestions = new ArrayList<>();
    private Boolean sumniki_pref;
    private SharedPreferences sp;

    public AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Station> seznam) {
        super(context, resource);
        this.seznamPostaj = seznam;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * To je custom filter, s katerim lahko prosto filtram seznam
     * Omogoča primerjanje besede s šumniki in enako besedo brez šumnikov
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                suggestions.clear();

                if (constraint != null) {
                    sumniki_pref = sp.getBoolean(SettingsActivity.ISKANJE_S_SUMNIKI, false);

                    for (Station str : seznamPostaj) {
                        // Odstrani šumnike za uporabnike, ki jih ne uporabljajo
                        String str1;
                        String str2;

                        if (sumniki_pref) {
                            str1 = DataSourcee.odstraniSumnike(str.getPOS_NAZ().toLowerCase());
                            str2 = DataSourcee.odstraniSumnike(constraint.toString().toLowerCase());
                        } else {
                            str1 = str.getPOS_NAZ().toLowerCase();
                            str2 = constraint.toString().toLowerCase();
                        }

                        if (str1.contains(str2)) {
                            suggestions.add(str);
                        }
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();

                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.d(TAG, "publishResults PUBLISH");
                clear();
                if (results != null) {
                    addAll((ArrayList<Station>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
}