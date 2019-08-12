package com.lukag.voznired.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class ManageLastSearch {
    /**
     * Metoda iz sharedPreferenca dobi postaji in datum ter nastavi vrednosti v prave objekte
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     */
    public static void nastaviZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fromID = sharedPreferences.getString("from", "");
        String toID = sharedPreferences.getString("to", "");

        vp.setText(fromID, false);
        ip.setText(toID, false);
    }

    /**
     * Metoda iz objektov pridobi vrednosti in jih shrani v sharedPreference
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     */
    public static void shraniZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("from", vp.getText().toString()).putString("to", ip.getText().toString()).apply();
    }
}
