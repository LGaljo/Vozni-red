package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class sharedPrefsManager {
    private Context context;
    private SharedPreferences shramba;
    private int size;
    public static List<Relacija> priljubljeneRelacije;

    sharedPrefsManager(Context context) {
        this.context = context;
        shramba = this.context.getSharedPreferences("priljubljenePostaje", Context.MODE_PRIVATE);
        priljubljeneRelacije = new ArrayList<>();
        //izbrisiPriljubljene();
        size = shramba.getInt("number", 0);
        pridobiPriljubljene();
    }

    private void izbrisiPriljubljene() {
        SharedPreferences.Editor urejevalnik = this.shramba.edit();
        urejevalnik.clear();
        urejevalnik.putInt("number", 0);
        urejevalnik.apply();
    }

    private Boolean aliObstaja(Relacija relacija) {
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            if (priljubljeneRelacije.get(i).getFromName().equals(relacija.getFromName()) && priljubljeneRelacije.get(i).getToName().equals(relacija.getToName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shrani priljubljene iz arraylista v shared preferences
     */
    public void shraniPriljubljene() {
        SharedPreferences.Editor urejevalnik = this.shramba.edit();
        izbrisiPriljubljene();
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            urejevalnik.putString(Integer.toString(i), priljubljeneRelacije.get(i).getFromName() + ":" + priljubljeneRelacije.get(i).getToName());
            this.size += 1;
            urejevalnik.putInt("number", i);
        }

        urejevalnik.apply();
    }

    /**
     * Shrani priljubljene iz shared preferences v arraylist
     */
    public void pridobiPriljubljene() {
        priljubljeneRelacije.clear();

        for (int i = 0; i <= this.size; i++) {
            Relacija rela = parseRelacija(this.shramba.getString(Integer.toString(i), ""));
            Log.d("Favs pridobi", rela.getFromName() + " -> " + rela.getToName());
            priljubljeneRelacije.add(rela);
        }
    }

    /**
     * Odstrani priljubljeno lokacijo iz Arraylista
     * @param relacija - Relacija za dodati
     */
    public void odstraniPriljubljeno(Relacija relacija) {
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            if (priljubljeneRelacije.get(i).getFromName().equals(relacija.getFromName()) && priljubljeneRelacije.get(i).getToName().equals(relacija.getToName())) {
                priljubljeneRelacije.remove(i);
                break;
            }
        }
    }

    /**
     * Shrani priljubljeno lokacijo v Arraylist
     * @param nova - Nova relacija za dodati
     */
    public void dodajPriljubljeno(Relacija nova) {
        if (!aliObstaja(nova)) {
            priljubljeneRelacije.add(nova);
        } else {
            Toast toast = Toast.makeText(context, "Lokacija že obstaja med priljubljenimi", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private static Relacija parseRelacija(String relacija) {
        Relacija rel = new Relacija();
        String relis[] = relacija.split(":");
        if (relis.length == 2) {
            rel.setFromName(relis[0]);
            rel.setToName(relis[1]);
        }
        return rel;
    }
}
