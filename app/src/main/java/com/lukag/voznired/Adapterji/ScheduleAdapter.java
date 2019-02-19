package com.lukag.voznired.Adapterji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.lukag.voznired.DisplayRideInfo;
import com.lukag.voznired.Objekti.BuildConstants;
import com.lukag.voznired.Objekti.Relacija;
import com.lukag.voznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.voznired.Objekti.Pot;
import com.lukag.voznired.R;
import com.lukag.voznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.lukag.voznired.MainActivity.EXTRA_MESSAGE;
import static com.lukag.voznired.UpravljanjeSPodatki.DataSourcee.newTime;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    private Relacija relacija;
    private List<Pot> scheduleList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView start, end, duration, length, cost, peron;

        public MyViewHolder(View view) {
            super(view);
            start = (TextView) view.findViewById(R.id.start);
            end = (TextView) view.findViewById(R.id.end);
            duration = (TextView) view.findViewById(R.id.duration);
            length = (TextView) view.findViewById(R.id.length);
            cost = (TextView) view.findViewById(R.id.cost);
            peron = (TextView) view.findViewById(R.id.peron);

            Integer margins = DataSourcee.calcMargins(context, 12);

            RelativeLayout.LayoutParams lpStart =   (RelativeLayout.LayoutParams)start.getLayoutParams();
            RelativeLayout.LayoutParams lpEnd =     (RelativeLayout.LayoutParams)end.getLayoutParams();
            RelativeLayout.LayoutParams lpDuration =(RelativeLayout.LayoutParams)duration.getLayoutParams();
            RelativeLayout.LayoutParams lpLength =  (RelativeLayout.LayoutParams)length.getLayoutParams();
            RelativeLayout.LayoutParams lpCost =    (RelativeLayout.LayoutParams)cost.getLayoutParams();
            RelativeLayout.LayoutParams lpPeron =    (RelativeLayout.LayoutParams)peron.getLayoutParams();

            lpStart.setMargins      (margins,0, margins,0);
            lpEnd.setMargins        (margins,0, margins,0);
            lpDuration.setMargins   (margins,0, margins,0);
            lpLength.setMargins     (margins,0, margins,0);
            lpCost.setMargins       (margins,0, margins,0);
            lpPeron.setMargins      (margins,0, margins,0);

            start.setLayoutParams(lpStart);
            end.setLayoutParams(lpEnd);
            duration.setLayoutParams(lpDuration);
            length.setLayoutParams(lpLength);
            cost.setLayoutParams(lpCost);
            peron.setLayoutParams(lpPeron);
        }

        public void bind(final Pot item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openRideInfo(relacija, item.getID());
                }
            });
        }

        private void openRideInfo(Relacija relacija, int PotID) {
            ArrayList<String> prenos = new ArrayList<>();
            prenos.add(Integer.toString(PotID));
            BuildConstants buildConstants = BuildConstants.getInstance();
            buildConstants.relacija = relacija;
            Intent intent = new Intent(context, DisplayRideInfo.class);
            intent.putStringArrayListExtra(EXTRA_MESSAGE, prenos);
            context.startActivity(intent);
        }
    }

    public ScheduleAdapter(Relacija relacija, String date, Context context) {
        this.scheduleList = relacija.getUrnik();
        this.relacija = relacija;
        this.context = context;

        pridobiUrnikMedPostajama(date);
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
        holder.start.setText(pot.getRod_iodh());
        holder.end.setText(pot.getRod_ipri());
        holder.duration.setText(String.format(Locale.GERMAN, "%d min", pot.getRod_cas()));
        holder.length.setText(String.format(Locale.GERMAN, "%d km", pot.getRod_km()));
        holder.cost.setText(String.format(Locale.GERMAN, "%.1f €", pot.getVzcl_cen()));

        if (!pot.getRod_per().equals("")) {
            holder.peron.setText(pot.getRod_per());
        } else {
            holder.peron.setText("  ");
        }

        Date time2 = newTime(pot.getRod_iodh());
        if (!DataSourcee.primerjajCas(time2)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.over));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.pending));
        }
        holder.bind(scheduleList.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }


    /**
     * Metoda od serverja zahteva podatke o voznem redu
     */
    public void pridobiUrnikMedPostajama(String date) {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartures.aspx";
        StringBuilder ClientId = new StringBuilder();
        ClientId.append("IMEI: ");
        ClientId.append(DataSourcee.getPhoneInfo(context));
        ClientId.append(" , MAC: ");
        ClientId.append(DataSourcee.getMacAddr(context));
        //Log.d("API", timestamp + " " + token + " " + ClientId.toString() + " " + DataSourcee.getPhoneInfo(context));
        //Log.d("API", relacija.getFromID() + " " + relacija.getToID() + " " + date);

        VolleyTool vt = new VolleyTool(context, url);

        vt.addParam("cTimeStamp", timestamp);
        vt.addParam("cToken", token);
        vt.addParam("JPOS_IJPPZ", relacija.getFromID());
        vt.addParam("JPOS_IJPPK", relacija.getToID());
        vt.addParam("VZVK_DAT", date); // datum oblike yyyy-MM-dd
        vt.addParam("ClientId", ClientId.toString()); // IMEI: <PHONE-ID> , MAC: <MAC-ADDRESS>
        vt.addParam("ClientIdType", DataSourcee.getPhoneInfo(context)); // IMEI
        vt.addParam("ClientLocationLatitude", "");
        vt.addParam("ClientLocationLongitude", "");
        vt.addParam("json", "1");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                try {
                    JSONArray JSONresponse = new JSONArray(response);

                    relacija.setUrnik(DataSourcee.parseVozniRed(relacija, JSONresponse).getUrnik());

                    ProgressBar progressBar = ((Activity)context).findViewById(R.id.wait_animation);
                    progressBar.setVisibility(View.GONE);
                    RelativeLayout relativeLayout = ((Activity)context).findViewById(R.id.schedule_heading) ;
                    relativeLayout.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}