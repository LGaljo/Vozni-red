package com.lukag.atvoznired.Adapterji;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.lukag.atvoznired.DisplaySchedule;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.UpravljanjeSPodatki.DataSourcee;
import com.lukag.atvoznired.R;
import com.lukag.atvoznired.Objekti.Relacija;
import com.lukag.atvoznired.UpravljanjeSPodatki.UpravljanjeSPriljubljenimi;
import com.lukag.atvoznired.UpravljanjeSPodatki.UpravljanjeZZadnjimiIskanimi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class priljubljenePostajeAdapter extends RecyclerView.Adapter<priljubljenePostajeAdapter.MyViewHolder> {

    private List<Relacija> priljubljeneList;
    private Context context;
    private AutoCompleteTextView vstopnaPostajaView;
    private AutoCompleteTextView izstopnaPostajaView;
    private UpravljanjeSPriljubljenimi spm;
    private TextView koledar;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView priljubljenaPostaja;
        public TextView priljubljenaPostajaNextRide;

        public MyViewHolder(final View view) {
            super(view);
            priljubljenaPostaja = (TextView) view.findViewById(R.id.priljubljena_postaja);
            priljubljenaPostajaNextRide = (TextView) view.findViewById(R.id.priljubljena_postaja_next_ride);

            // Prikazi izbrano relacijo
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vstopnaPostaja = priljubljeneList.get(getAdapterPosition()).getFromName();
                    String izstopnaPostaja = priljubljeneList.get(getAdapterPosition()).getToName();
                    String vstopnaID = BuildConstants.seznamPostaj.get(vstopnaPostaja);
                    String izstopnaID = BuildConstants.seznamPostaj.get(izstopnaPostaja);
                    vstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getFromName(), false);
                    izstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getToName(), false);
                    String formatApi = "yyyy-MM-dd";
                    SimpleDateFormat ApiFormat = new SimpleDateFormat(formatApi, Locale.GERMAN);

                    UpravljanjeZZadnjimiIskanimi.shraniZadnjiIskani(context, vstopnaPostajaView, izstopnaPostajaView, DataSourcee.pridobiCas("dd.MM.yyyy"));

                    ArrayList<String> prenos = new ArrayList<>();
                    prenos.add(vstopnaID);
                    prenos.add(vstopnaPostaja);
                    prenos.add(izstopnaID);
                    prenos.add(izstopnaPostaja);
                    prenos.add(koledar.getText().toString());
                    prenos.add(DataSourcee.pridobiCas("yyyy-MM-dd"));
                    Intent intent = new Intent(view.getContext(), DisplaySchedule.class);
                    intent.putStringArrayListExtra(EXTRA_MESSAGE, prenos);
                    view.getContext().startActivity(intent);
                }
            });

            // Odstrani izbrano relacijo
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    UpravljanjeSPriljubljenimi.odstraniPriljubljeno(priljubljeneList.get(getAdapterPosition()));
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), priljubljeneList.size());
                    return false;
                }
            });
        }
    }

    public priljubljenePostajeAdapter(List<Relacija> priljubljeneList, Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, UpravljanjeSPriljubljenimi spm, TextView koledar) {
        this.priljubljeneList = priljubljeneList;
        this.context = context;
        this.vstopnaPostajaView = vp;
        this.izstopnaPostajaView = ip;
        this.spm = spm;
        this.koledar = koledar;
    }

    @Override
    public priljubljenePostajeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.priljubljena_postaja_row, parent, false);

        return new priljubljenePostajeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(priljubljenePostajeAdapter.MyViewHolder holder, int position) {
        Relacija rel = priljubljeneList.get(position);
        holder.priljubljenaPostaja.setText(rel.getFromName() + " - " + rel.getToName());
        holder.priljubljenaPostajaNextRide.setText(appendNextRide(rel));
    }

    private String appendNextRide(Relacija rel) {
        if (rel.getNextRide() == null || rel.getNextRide()[0] == null || rel.getNextRide()[0].equals("")) {
            return "";
        } else if (rel.getNextRide()[0].equals("tomorrow")) {
            return "\n" + context.getResources().getString(R.string.next_ride)+ ": " +
                    context.getResources().getString(R.string.tomorrow);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");

            // Najprej dobimo število voženje in jih preštejemo
            Boolean first = true;
            Integer num = 0;
            StringBuilder sb2 = new StringBuilder();
            for (String str : rel.getNextRide()) {
                if (str != null) {
                    if (!first) {
                        sb2.append(", ");
                    }
                    first = false;
                    num++;
                    sb2.append(str);
                }
            }

            // Vemo koliko voženj imamo
            switch (num) {
                case 1:
                    sb.append(context.getResources().getString(R.string.next_ride));
                    break;
                case 2:
                    sb.append(context.getResources().getString(R.string.next_ride_dvojina));
                    break;
                case 3:
                    sb.append(context.getResources().getString(R.string.next_ride_mnozina));
                    break;
                default:
                    break;
            }
            sb.append(": ");
            sb.append(sb2);
            return  sb.toString();
        }
    }

    @Override
    public int getItemCount() {
        return priljubljeneList.size();
    }
}
