package com.lukag.atvoznired.UpravljanjeSPodatki;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class UpravljanjeZZadnjimiIskanimi {
    /**
     * Metoda iz sharedPreferenca dobi postaji in datum ter nastavi vrednosti v prave objekte
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     * @param dateView - TextView za tekst koledarja
     */
    public static void nastaviZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, TextView dateView) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        String fromID = priljubljene.getString("fromID", "");
        String toID = priljubljene.getString("toID", "");
        String date = priljubljene.getString("date", "");

        vp.setText(fromID, false);
        ip.setText(toID, false);
        dateView.setText(date);
    }

    /**
     * Metoda iz objektov pridobi vrednosti in jih shrani v sharedPreference
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     * @param date - TextView za tekst koledarja
     */
    public static void shraniZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, String date) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        SharedPreferences.Editor urejevalnik = priljubljene.edit();
        urejevalnik.putString("fromID", vp.getText().toString());
        urejevalnik.putString("toID", ip.getText().toString());
        urejevalnik.putString("date", date);

        urejevalnik.apply();
    }
}
