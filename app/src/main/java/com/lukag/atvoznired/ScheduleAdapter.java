package com.lukag.atvoznired;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<Pot> scheduleList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView start, end, duration, length, cost;

        public MyViewHolder(View view) {
            super(view);
            start = (TextView) view.findViewById(R.id.start);
            end = (TextView) view.findViewById(R.id.end);
            duration = (TextView) view.findViewById(R.id.duration);
            length = (TextView) view.findViewById(R.id.length);
            cost = (TextView) view.findViewById(R.id.cost);

            Integer margins = DataSourcee.calcMargins(context);

            RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)start.getLayoutParams();
            RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)end.getLayoutParams();
            RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)duration.getLayoutParams();
            RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)length.getLayoutParams();
            RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)cost.getLayoutParams();
            lpStart.setMargins      (margins,0, margins,0);
            lpEnd.setMargins        (margins,0, margins,0);
            lpDuration.setMargins   (margins,0, margins,0);
            lpLength.setMargins     (margins,0, margins,0);
            lpCost.setMargins       (margins,0, margins,0);
            start.setLayoutParams(lpStart);
            end.setLayoutParams(lpEnd);
            duration.setLayoutParams(lpDuration);
            length.setLayoutParams(lpLength);
            cost.setLayoutParams(lpCost);
        }
    }

    public ScheduleAdapter(List<Pot> scheduleList, Context context) {
        this.scheduleList = scheduleList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pot_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pot pot = scheduleList.get(position);
        holder.start.setText(pot.getStart());
        holder.end.setText(pot.getEnd());
        holder.duration.setText(pot.getDuration());
        holder.length.setText(pot.getLength() + " km");
        holder.cost.setText(pot.getCost() + " €");
        if (!pot.getStatus()) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.over));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.pending));
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}