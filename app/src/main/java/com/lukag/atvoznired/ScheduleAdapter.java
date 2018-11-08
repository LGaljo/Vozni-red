package com.lukag.atvoznired;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public Integer[] getMarginsForRecyclerView() {
        Integer allMargins = 0;
        Integer displayWidth = 0;
        Integer contentWidth = dpToPx(290);
        Integer layoutPadding = dpToPx(32);
        Integer margins[] = new Integer[4];

        try {
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymatrics = new DisplayMetrics();
            display.getMetrics(displaymatrics);

            try{
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allMargins = displayWidth - (contentWidth + layoutPadding);
        if (allMargins < 0) {
            margins[0] = 0;
            margins[1] = 0;
            margins[2] = 0;
            margins[3] = 0;
        } else {
            margins[0] = allMargins / 8;
            margins[1] = 0;
            margins[2] = allMargins / 8;
            margins[3] = 0;
        }

        return margins;
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
        }

        Integer margins[] = getMarginsForRecyclerView();

        RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)holder.start.getLayoutParams();
        RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)holder.end.getLayoutParams();
        RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)holder.duration.getLayoutParams();
        RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)holder.length.getLayoutParams();
        RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)holder.cost.getLayoutParams();
        lpStart.setMargins      (0,0, margins[2],0);
        lpEnd.setMargins        (margins[0],0, margins[2],0);
        lpDuration.setMargins   (margins[0],0, margins[2],0);
        lpLength.setMargins     (margins[0],0, margins[2],0);
        lpCost.setMargins       (margins[0],0, 0,0);
        holder.start.setLayoutParams(lpStart);
        holder.end.setLayoutParams(lpEnd);
        holder.duration.setLayoutParams(lpDuration);
        holder.length.setLayoutParams(lpLength);
        holder.cost.setLayoutParams(lpCost);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
}