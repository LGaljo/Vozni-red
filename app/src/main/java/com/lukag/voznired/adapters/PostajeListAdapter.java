package com.lukag.voznired.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lukag.voznired.R;
import com.lukag.voznired.models.StationsList;
import com.lukag.voznired.models.Voznja;

import java.util.ArrayList;

public class PostajeListAdapter extends RecyclerView.Adapter<PostajeListAdapter.MyViewHolder> {
    private static final String TAG = PostajeListAdapter.class.getSimpleName();

    private ArrayList<StationsList> voznje;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView postaja, cas;
        ImageView circle, rec_bot, rec_top;

        MyViewHolder(View view) {
            super(view);

            postaja = view.findViewById(R.id.postaja);
            cas = view.findViewById(R.id.cas);
            circle = view.findViewById(R.id.circle);
            rec_bot = view.findViewById(R.id.rectangle_bottom);
            rec_top = view.findViewById(R.id.rectangle_top);
        }
    }

    public PostajeListAdapter(ArrayList<StationsList> voznje) {
        this.voznje = voznje;
    }

    @Override
    @NonNull
    public PostajeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postaje_list_item, parent, false);

        return new PostajeListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostajeListAdapter.MyViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return voznje.size();
    }
}
