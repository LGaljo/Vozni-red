package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPrefsManager {
    private Context context;
    private SharedPreferences priljubljene;
    private int size;

    sharedPrefsManager(Context context) {
        this.context = context;
        this.priljubljene = this.context.getSharedPreferences("priljubljenePostaje", Context.MODE_PRIVATE);
        this.size = priljubljene.getInt("number", 0);
    }

    public void dodajPriljubljene(String from, String to) {
        SharedPreferences.Editor urejevalnik = priljubljene.edit();
        this.size += 1;
        urejevalnik.putString(Integer.toString(size), from + ":" + to);
        urejevalnik.apply();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= size; i++) {
            sb.append(priljubljene.getString(Integer.toString(i), ""));
            sb.append("\n");
        }

        return sb.toString();
    }
}
