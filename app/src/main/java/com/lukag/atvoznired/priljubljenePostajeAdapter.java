package com.lukag.atvoznired;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.lukag.atvoznired.MainActivity.EXTRA_MESSAGE;

public class priljubljenePostajeAdapter extends RecyclerView.Adapter<priljubljenePostajeAdapter.MyViewHolder> {

    private List<Relacija> priljubljeneList;
    private Context context;
    private AutoCompleteTextView vstopnaPostajaView;
    private AutoCompleteTextView izstopnaPostajaView;
    private sharedPrefsManager spm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView priljubljenaPostaja;

        public MyViewHolder(final View view) {
            super(view);
            priljubljenaPostaja = (TextView) view.findViewById(R.id.priljubljena_postaja);

            // Prikazi izbrano relacijo
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vstopnaPostaja = vstopnaPostajaView.getText().toString();
                    String izstopnaPostaja = izstopnaPostajaView.getText().toString();
                    String vstopnaID = DataSourcee.getIDfromMap(vstopnaPostaja);
                    String izstopnaID = DataSourcee.getIDfromMap(izstopnaPostaja);

                    DataSourcee.shraniZadnjiIskani(context, vstopnaPostajaView, izstopnaPostajaView);

                    ArrayList<String> prenos = new ArrayList<>();
                    prenos.add(vstopnaID);
                    prenos.add(vstopnaPostaja);
                    prenos.add(izstopnaID);
                    prenos.add(izstopnaPostaja);
                    prenos.add(DataSourcee.dodajDanasnjiDan());
                    Intent intent = new Intent(view.getContext(), DisplayMessageActivity.class);
                    intent.putStringArrayListExtra(EXTRA_MESSAGE, prenos);
                    view.getContext().startActivity(intent);

                    vstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getFromName(), false);
                    izstopnaPostajaView.setText(priljubljeneList.get(getAdapterPosition()).getToName(), false);
                }
            });

            // Odstrani izbrano relacijo
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    spm.odstraniPriljubljeno(new Relacija(vstopnaPostajaView.getText().toString(), "", izstopnaPostajaView.getText().toString(), "", null));
                    priljubljeneList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), priljubljeneList.size());
                    return false;
                }
            });
        }
    }

    public priljubljenePostajeAdapter(List<Relacija> priljubljeneList, Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, sharedPrefsManager spm) {
        this.priljubljeneList = priljubljeneList;
        this.context = context;
        this.vstopnaPostajaView = vp;
        this.izstopnaPostajaView = ip;
        this.spm = spm;
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
    }

    @Override
    public int getItemCount() {
        return priljubljeneList.size();
    }
}
