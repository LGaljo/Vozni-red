package com.lukag.voznired.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lukag.voznired.R;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.models.Station;
import com.lukag.voznired.models.StationsList;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.lukag.voznired.helpers.BuildConstants.ISKANJE_S_SUMNIKI;

public class StationsListAdapter extends RecyclerView.Adapter<StationsListAdapter.ViewHolder> implements Filterable {
    private static final String TAG = StationsListAdapter.class.getSimpleName();

    private ArrayList<Station> stationsAll;
    private ArrayList<Station> stationsToShow;
    private Boolean sumniki_pref;
    private SharedPreferences sp;

    // Provide a suitable constructor (depends on the kind of dataset)
    public StationsListAdapter(ArrayList<Station> locationsAll, Context context) {
        this.stationsAll = locationsAll;
        this.stationsToShow = locationsAll;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Filtriranje rezultatov iskanja na listu vseh interesnih točk
     *
     * @return results
     */
    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter Filter now");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                sumniki_pref = sp.getBoolean(ISKANJE_S_SUMNIKI, false);

                if (charString.isEmpty()) {
                    stationsToShow = stationsAll;
                } else {
                    ArrayList<Station> filteredList = new ArrayList<>();
                    for (Station str : stationsAll) {
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
                            filteredList.add(str);
                        }
                    }

                    stationsToShow = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = stationsToShow;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                stationsToShow = (ArrayList<Station>) results.values;
                Log.d(TAG, "publishResults: Show " + stationsToShow);

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView item_pic;
        TextView item_name;
        String locationId;

        ViewHolder(View view) {
            super(view);
            this.item_pic = view.findViewById(R.id.item_pic);
            this.item_name = view.findViewById(R.id.item_name);
        }

        void bind(Station station) {
            itemView.setOnClickListener(v -> openMaps(station));
        }

        private void openMaps(Station station) {
            /*
            Uri gmmIntentUri = Uri.parse("geo:" + voznja.getROD_LAT() + "," + voznja.getROD_LON() +
                    "?q=" + voznja.getROD_LAT() + "," + voznja.getROD_LON() + "(" + voznja.getPOS_NAZ() + ")");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }*/
        }

    }

    @NonNull
    @Override
    public StationsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_location_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StationsListAdapter.ViewHolder holder, int position) {
        Station location = stationsToShow.get(position);
        holder.item_name.setText(location.getPOS_NAZ());
        holder.locationId = location.getJPOS_IJPP();

        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return stationsToShow.size();
    }
}
