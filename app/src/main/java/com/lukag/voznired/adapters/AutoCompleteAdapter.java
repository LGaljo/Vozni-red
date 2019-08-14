package com.lukag.voznired.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.models.Station;

import java.util.ArrayList;

import static com.lukag.voznired.helpers.BuildConstants.ISKANJE_S_SUMNIKI;

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
                    sumniki_pref = sp.getBoolean(ISKANJE_S_SUMNIKI, false);

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