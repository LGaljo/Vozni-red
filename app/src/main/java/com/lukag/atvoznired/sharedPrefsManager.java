package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class sharedPrefsManager {
    private Context context;
    private SharedPreferences priljubljene;
    private int size;
    public static List<Relacija> priljubljeneRelacije;

    sharedPrefsManager(Context context) {
        this.context = context;
    }

    public void init() {
        priljubljene = this.context.getSharedPreferences("priljubljenePostaje", Context.MODE_PRIVATE);
        //izbrisiVse();
        size = priljubljene.getInt("number", 0);
        priljubljeneRelacije = new ArrayList<>();
        pridobiPriljubljene();
    }

    private void izbrisiVse() {
        SharedPreferences.Editor urejevalnik = this.priljubljene.edit();
        urejevalnik.clear();
        urejevalnik.putInt("number", 0);
        urejevalnik.putString(Integer.toString(0), "");
        urejevalnik.putString(Integer.toString(1), "");
        urejevalnik.putString(Integer.toString(2), "");
        urejevalnik.putString(Integer.toString(3), "");
        urejevalnik.putString(Integer.toString(4), "");
        urejevalnik.putString(Integer.toString(5), "");
        urejevalnik.putString(Integer.toString(6), "");
        urejevalnik.putString(Integer.toString(7), "");
        urejevalnik.putString(Integer.toString(8), "");
        urejevalnik.putString(Integer.toString(9), "");
        urejevalnik.putString(Integer.toString(10), "");
        urejevalnik.commit();
    }

    private Boolean aliObstaja(String fromID, String toID) {
        for (int i = 0; i < this.size; i++) {
            if (this.priljubljene.getString(Integer.toString(i), "").equals(fromID + ":" + toID)) {
                return true;
            }
        }

        return false;
    }


    public void dodajPriljubljene(String from, String to) {
        if (!aliObstaja(from, to)) {
            Log.d("Favs dodaj", from + to);
            SharedPreferences.Editor urejevalnik = this.priljubljene.edit();
            urejevalnik.putString(Integer.toString(this.size), from + ":" + to);
            this.size += 1;
            urejevalnik.putInt("number", size);
            urejevalnik.apply();
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

    public void pridobiPriljubljene() {
                if (this.size == 0) {
            return;
        }

        priljubljeneRelacije.clear();

        for (int i = 0; i < this.size; i++) {
            Relacija rela = parseRelacija(this.priljubljene.getString(Integer.toString(i), ""));
            Log.d("Favs pridobi", rela.getFromName() + " -> " + rela.getToName());
            priljubljeneRelacije.add(rela);
        }
    }

    public void odstraniPriljubljeno(String from, String to) {
        SharedPreferences.Editor urejevalnik = priljubljene.edit();
        for (int i = 0; i < size; i++) {
            String tmp = priljubljene.getString(Integer.toString(i), "");
            if (tmp.equals(from + ":" + to)) {
                for (int j = i + 1; j < size; i++) {
                    tmp = priljubljene.getString(Integer.toString(j), "");
                    urejevalnik.putString(Integer.toString(j - 1), tmp);
                }

                break;
            }
        }
        size -= 1;
        urejevalnik.putInt("number", size);
        urejevalnik.apply();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size; i++) {
            sb.append(this.priljubljene.getString(Integer.toString(i), ""));
            sb.append("\n");
        }

        return sb.toString();
    }
}
