package com.lukag.voznired.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lukag.voznired.R;
import com.lukag.voznired.helpers.DataSourcee;
import com.lukag.voznired.helpers.ManageFavs;
import com.lukag.voznired.helpers.ManageLastSearch;
import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.ui.DisplaySchedule;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lukag.voznired.helpers.BuildConstants.INTENT_DATUM;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_IZSTOPNA_IME;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_ID;
import static com.lukag.voznired.helpers.BuildConstants.INTENT_VSTOPNA_IME;

public class PriljubljenePostajeAdapter extends RecyclerView.Adapter<PriljubljenePostajeAdapter.MyViewHolder> {

    private List<Relacija> priljubljeneList;
    private Context context;
    private AutoCompleteTextView vstopnaPostajaView;
    private AutoCompleteTextView izstopnaPostajaView;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.priljubljena_postaja) TextView priljubljenaPostaja;
        @BindView(R.id.priljubljena_postaja_next_ride) TextView priljubljenaPostajaNextRide;

        MyViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);

            // Prikazi izbrano relacijo
            view.setOnClickListener(v -> {
                String vstopnaPostaja = priljubljeneList.get(getAdapterPosition()).getFromName();
                String izstopnaPostaja = priljubljeneList.get(getAdapterPosition()).getToName();
                String vstopnaID = priljubljeneList.get(getAdapterPosition()).getFromID();
                String izstopnaID = priljubljeneList.get(getAdapterPosition()).getToID();

                vstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getFromName(), false);
                izstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getToName(), false);

                ManageLastSearch.shraniZadnjiIskani(context, vstopnaPostajaView, izstopnaPostajaView);

                Intent intent = new Intent(view.getContext(), DisplaySchedule.class);
                intent.putExtra(INTENT_VSTOPNA_ID, vstopnaID);
                intent.putExtra(INTENT_VSTOPNA_IME, vstopnaPostaja);
                intent.putExtra(INTENT_IZSTOPNA_ID, izstopnaID);
                intent.putExtra(INTENT_IZSTOPNA_IME, izstopnaPostaja);
                intent.putExtra(INTENT_DATUM, DataSourcee.pridobiCas("yyyy-MM-dd"));

                //prenos.add(koledar.getText().toString());
                view.getContext().startActivity(intent);
            });

            // Odstrani izbrano relacijo
            view.setOnLongClickListener(v -> {
                ManageFavs.odstraniPriljubljeno(priljubljeneList.get(getAdapterPosition()));
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), priljubljeneList.size());
                return false;
            });
        }
    }

    public PriljubljenePostajeAdapter(List<Relacija> priljubljeneList, Context context, AutoCompleteTextView vp, AutoCompleteTextView ip) {
        this.priljubljeneList = priljubljeneList;
        this.context = context;
        this.vstopnaPostajaView = vp;
        this.izstopnaPostajaView = ip;
    }

    @Override
    public PriljubljenePostajeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.priljubljena_postaja_row, parent, false);

        return new PriljubljenePostajeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PriljubljenePostajeAdapter.MyViewHolder holder, int position) {
        Relacija rel = priljubljeneList.get(position);
        if (rel != null) {
            holder.priljubljenaPostaja.setText(rel.getFromName() + " - " + rel.getToName());
            holder.priljubljenaPostajaNextRide.setText(appendNextRide(rel));
        }
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
            boolean first = true;
            int num = 0;
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
