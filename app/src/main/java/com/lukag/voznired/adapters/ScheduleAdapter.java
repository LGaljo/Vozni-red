package com.lukag.voznired.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lukag.voznired.R;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.models.Departure;
import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.ui.DisplayRideInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_OVR_SIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_REG_ISIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_ROD_ZAPK;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_ROD_ZAPZ;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_SPOD_SIF;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VVLN_ZL;
import static com.lukag.voznired.helpers.DataSourcee.newTime;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    private Relacija relacija;
    private Date current_date;
    private Date searched_date;
    private Context context;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.start)
        TextView start;
        @BindView(R.id.end)
        TextView end;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.length)
        TextView length;
        @BindView(R.id.cost)
        TextView cost;
        @BindView(R.id.peron)
        TextView peron;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            Integer margins = DataSourcee.calcMargins(context, 12);

            RelativeLayout.LayoutParams lpStart = (RelativeLayout.LayoutParams) start.getLayoutParams();
            RelativeLayout.LayoutParams lpEnd = (RelativeLayout.LayoutParams) end.getLayoutParams();
            RelativeLayout.LayoutParams lpDuration = (RelativeLayout.LayoutParams) duration.getLayoutParams();
            RelativeLayout.LayoutParams lpLength = (RelativeLayout.LayoutParams) length.getLayoutParams();
            RelativeLayout.LayoutParams lpCost = (RelativeLayout.LayoutParams) cost.getLayoutParams();
            RelativeLayout.LayoutParams lpPeron = (RelativeLayout.LayoutParams) peron.getLayoutParams();

            lpStart.setMargins(margins, 0, margins, 0);
            lpEnd.setMargins(margins, 0, margins, 0);
            lpDuration.setMargins(margins, 0, margins, 0);
            lpLength.setMargins(margins, 0, margins, 0);
            lpCost.setMargins(margins, 0, margins, 0);
            lpPeron.setMargins(margins, 0, margins, 0);

            start.setLayoutParams(lpStart);
            end.setLayoutParams(lpEnd);
            duration.setLayoutParams(lpDuration);
            length.setLayoutParams(lpLength);
            cost.setLayoutParams(lpCost);
            peron.setLayoutParams(lpPeron);
        }

        void bind(final Departure item, int position) {
            itemView.setOnClickListener(v -> openRideInfo(relacija, position));
        }

        private void openRideInfo(Relacija relacija, int rideID) {
            Intent intent = new Intent(context, DisplayRideInfo.class);
            intent.putExtra(INTENT_VSTOPNA_IME, relacija.getFromName());
            intent.putExtra(INTENT_IZSTOPNA_IME, relacija.getToName());

            intent.putExtra(INTENT_SPOD_SIF, relacija.getUrnik().get(rideID).getSPOD_SIF());
            intent.putExtra(INTENT_REG_ISIF, relacija.getUrnik().get(rideID).getREG_ISIF());
            intent.putExtra(INTENT_OVR_SIF, relacija.getUrnik().get(rideID).getOVR_SIF());
            intent.putExtra(INTENT_VVLN_ZL, relacija.getUrnik().get(rideID).getVVLN_ZL());
            intent.putExtra(INTENT_ROD_ZAPZ, relacija.getUrnik().get(rideID).getROD_ZAPZ());
            intent.putExtra(INTENT_ROD_ZAPK, relacija.getUrnik().get(rideID).getROD_ZAPK());

            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    public ScheduleAdapter(Relacija relacija, Date date, Context context) {
        this.relacija = relacija;
        this.context = context;
        this.searched_date = date;
        Calendar calendar = Calendar.getInstance();
        this.current_date = calendar.getTime();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pot_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Departure departure = relacija.getUrnik().get(position);
        holder.start.setText(departure.getROD_IODH());
        holder.end.setText(departure.getROD_IPRI());
        holder.duration.setText(String.format(Locale.GERMAN, "%d min", departure.getROD_CAS()));
        holder.length.setText(String.format(Locale.GERMAN, "%d km", departure.getROD_KM()));
        holder.cost.setText(String.format(Locale.GERMAN, "%.1f €", departure.getVZCL_CEN()));

        if (departure.getROD_PER() != null && !departure.getROD_PER().equals("")) {
            holder.peron.setText(departure.getROD_PER());
        } else {
            holder.peron.setText("  ");
        }

        Date departure_date = new Date();
        try {
            departure_date = sdf.parse(departure.getROD_IODH());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        departure_date = new Date(searched_date.getTime() + departure_date.getTime());

        if (current_date.after(departure_date)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.over));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.pending));
        }
        holder.bind(relacija.getUrnik().get(position), position);
    }

    @Override
    public int getItemCount() {
        return relacija.getUrnik().size();
    }
}