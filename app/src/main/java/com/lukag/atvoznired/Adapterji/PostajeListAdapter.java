package com.lukag.atvoznired.Adapterji;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Voznja;
import com.lukag.atvoznired.R;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.UpravljanjeSPodatki.VolleyTool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PostajeListAdapter extends RecyclerView.Adapter<PostajeListAdapter.MyViewHolder> {
    private ArrayList<Voznja> voznje;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView postaja, cas;
        public ImageView circle, rec_bot, rec_top;

        public MyViewHolder(View view) {
            super(view);

            postaja = (TextView) view.findViewById(R.id.postaja);
            cas = (TextView) view.findViewById(R.id.cas);
            circle = (ImageView) view.findViewById(R.id.circle);
            rec_bot = (ImageView) view.findViewById(R.id.rectangle_bottom);
            rec_top = (ImageView) view.findViewById(R.id.rectangle_top);
        }
    }

    public PostajeListAdapter(Pot pot, Context context) {
        this.context = context;
        this.voznje = new ArrayList<>();
        pridobiPodrobnostiOVoznji(pot);
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
        Voznja v = voznje.get(position);
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

    private void pridobiPodrobnostiOVoznji(Pot pot) {
        String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
        String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
        String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartureStationList.aspx";

        VolleyTool vt = new VolleyTool(context, url);

        vt.addParam("cTimeStamp", timestamp);
        vt.addParam("cToken", token);
        vt.addParam("SPOD_SIF", Integer.toString(pot.getSpod_sif()));
        vt.addParam("REG_ISIF", pot.getReg_isif());
        vt.addParam("OVR_SIF", pot.getOvr_sif());
        vt.addParam("VVLN_ZL", Integer.toString(pot.getVvln_zl()));
        vt.addParam("ROD_ZAPZ", pot.getRod_zapz());
        vt.addParam("ROD_ZAPK", pot.getRod_zapk());
        vt.addParam("json", "1");

        vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

            @Override
            public void getResponse(String response) {
                try {
                    JSONArray JSONresponse = new JSONArray(response);
                    voznje = DataSourcee.parseVoznje(JSONresponse);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
