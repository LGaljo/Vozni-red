package com.lukag.voznired.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lukag.voznired.helpers.BuildConstants.STORAGE_FAVS;

public class ManageFavs {
    public static final String TAG = ManageFavs.class.getSimpleName();

    private static ManageFavs upravljanjeSPriljubljenimi;
    private static SharedPreferences shramba;
    public static ArrayList<Relacija> priljubljeneRelacije;

    private ManageFavs() {
    }

    public static ManageFavs getInstance() {
        if (upravljanjeSPriljubljenimi == null) {
            upravljanjeSPriljubljenimi = new ManageFavs();
        }

        return upravljanjeSPriljubljenimi;
    }

    public void setContext(Context context) {
        shramba = PreferenceManager.getDefaultSharedPreferences(context);
        priljubljeneRelacije = new ArrayList<>();
        pridobiPriljubljene();
    }

    /**
     * Metoda preveri ali je podana relacija v seznamu priljubljeni
     *
     * @param relacija - objekt relacije
     * @return true - če je relacija že med priljubljenimi, drugače vrne false
     */
    private Boolean aliObstaja(Relacija relacija) {
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            if (priljubljeneRelacije.get(i).getFromName().equals(relacija.getFromName()) && priljubljeneRelacije.get(i).getToName().equals(relacija.getToName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shrani priljubljene iz arraylista v shared preferences v JSON obliki
     */
    public void shraniPriljubljene() {
        try {
            shramba.edit().remove(STORAGE_FAVS).apply();
            shramba.edit().putString(STORAGE_FAVS, createJson(priljubljeneRelacije).toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda naredi JSON objekt iz Arraylista relacij za shrambo v SharedPreferencih
     */
    private JSONObject createJson(ArrayList<Relacija> priljubljeneRelacije) throws JSONException {
        JSONArray seznam = new JSONArray();
        JSONObject out = new JSONObject();

        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            JSONObject enota = new JSONObject();
            Relacija r = priljubljeneRelacije.get(i);

            enota.put("fromN", r.getFromName());
            enota.put("toN", r.getToName());
            enota.put("fromID", r.getFromID());
            enota.put("toID", r.getToID());

            seznam.put(enota);
        }

        out.put("seznam", seznam);

        return out;
    }

    /**
     * Shrani priljubljene iz shared preferences v arraylist relacij
     */
    public static void pridobiPriljubljene() {
        try {
            priljubljeneRelacije.clear();
            String json = shramba.getString(STORAGE_FAVS, "");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("seznam");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String fN = obj.getString("fromN");
                String tN = obj.getString("toN");
                String fD = obj.getString("fromID");
                String tD = obj.getString("toID");
                Relacija nova = new Relacija(fD, fN, tD, tN, new ArrayList<>());
                priljubljeneRelacije.add(nova);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Odstrani priljubljeno relacijo iz Arraylista
     *
     * @param relacija - Relacija za dodati
     */
    public static void odstraniPriljubljeno(Relacija relacija) {
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            if (priljubljeneRelacije.get(i).getFromName().equals(relacija.getFromName()) &&
                    priljubljeneRelacije.get(i).getToName().equals(relacija.getToName())) {
                priljubljeneRelacije.remove(i);
                Log.d(TAG, "odstraniPriljubljeno: Removed one");
                MainActivity.runs.run();
                break;
            }
        }
    }

    /**
     * Shrani priljubljeno lokacijo v Arraylist
     *
     * @param nova - Nova relacija za dodati
     */
    public boolean dodajPriljubljeno(Relacija nova) {
        if (!aliObstaja(nova)) {
            priljubljeneRelacije.add(nova);
            MainActivity.runs.run();
            return true;
        }
        return false;
    }
}
