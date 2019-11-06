package com.lukag.voznired.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lukag.voznired.R;
import com.lukag.voznired.models.StationsList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PostajeListAdapter extends RecyclerView.Adapter<PostajeListAdapter.ViewHolder> {
    private static final String TAG = PostajeListAdapter.class.getSimpleName();

    private ArrayList<StationsList> voznje;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.postaja) TextView postaja;
        @BindView(R.id.cas) TextView cas;
        @BindView(R.id.circle) ImageView circle;
        @BindView(R.id.rectangle_bottom) ImageView rec_bot;
        @BindView(R.id.rectangle_top) ImageView rec_top;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(StationsList voznja) {
            itemView.setOnClickListener(v -> openMaps(voznja));
        }

        private void openMaps(StationsList voznja) {
            Uri gmmIntentUri = Uri.parse("geo:" + voznja.getROD_LAT() + "," + voznja.getROD_LON() +
                    "?q=" + voznja.getROD_LAT() + "," + voznja.getROD_LON() + "(" + voznja.getPOS_NAZ() + ")");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
        }
    }

    public PostajeListAdapter(ArrayList<StationsList> voznje, Context context) {
        this.voznje = voznje;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postaje_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StationsList v = voznje.get(position);
        holder.postaja.setText(v.getPOS_NAZ());
        holder.cas.setText(v.getROD_IODH());
        if (position == 0) {
            holder.rec_bot.setVisibility(View.INVISIBLE);
        } else if (position == voznje.size() - 1) {
            holder.rec_top.setVisibility(View.INVISIBLE);
            holder.cas.setText(v.getROD_IPRI());
        } else {
            holder.rec_bot.setVisibility(View.VISIBLE);
            holder.rec_top.setVisibility(View.VISIBLE);
        }

        holder.bind(v);
    }

    @Override
    public int getItemCount() {
        return voznje.size();
    }
}
