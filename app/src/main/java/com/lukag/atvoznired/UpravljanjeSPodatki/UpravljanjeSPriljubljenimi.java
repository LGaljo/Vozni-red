package com.lukag.atvoznired.UpravljanjeSPodatki;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lukag.atvoznired.MainActivity;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Relacija;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpravljanjeSPriljubljenimi {

    private static UpravljanjeSPriljubljenimi upravljanjeSPriljubljenimi;

    private Context context;
    private static SharedPreferences shramba;
    private static int size;
    public static ArrayList<Relacija> priljubljeneRelacije;

    private UpravljanjeSPriljubljenimi() {
    }

    public static UpravljanjeSPriljubljenimi getInstance() {
        if (upravljanjeSPriljubljenimi == null) {
            upravljanjeSPriljubljenimi = new UpravljanjeSPriljubljenimi();
        }

        return upravljanjeSPriljubljenimi;
   }

   public void setContext(Context context) {
       this.context = context;
       shramba = this.context.getSharedPreferences("priljubljenePostaje", Context.MODE_PRIVATE);
       size = shramba.getInt("number", 0);
       priljubljeneRelacije = new ArrayList<>();
       pridobiPriljubljene();
   }

    /**
     * Metoda izbriše shrambo priljubljenih lokacij in
     * nastavi število priljubljenih na nič
     */
    private void izbrisiPriljubljene() {
        SharedPreferences.Editor urejevalnik = shramba.edit();
        urejevalnik.clear();
        urejevalnik.apply();
    }

    /**
     * Metoda preveri ali je podana relacija v seznamu priljubljeni
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
     * Shrani priljubljene iz arraylista v shared preferences
     */
    public void shraniPriljubljene() {
        try {
            SharedPreferences.Editor urejevalnik = shramba.edit();
            izbrisiPriljubljene();
            JSONObject jsonObj = createJson(priljubljeneRelacije);
            urejevalnik.putString("seznam", jsonObj.toString());
            //Log.d("JSON", jsonObj.toString());
            urejevalnik.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject createJson(ArrayList<Relacija> priljubljeneRelacije) throws JSONException {
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
     * Shrani priljubljene iz shared preferences v arraylist
     */
    public static void pridobiPriljubljene() {
        try {
            priljubljeneRelacije.clear();
            String json = shramba.getString("seznam", "");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("seznam");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String fN = obj.getString("fromN");
                String tN = obj.getString("toN");
                String fD = obj.getString("fromID");
                String tD = obj.getString("toID");
                Relacija nova = new Relacija(fD, fN, tD, tN, new ArrayList<Pot>());
                priljubljeneRelacije.add(nova);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        MainActivity.sourcesFound = false;
    }

    /**
     * Odstrani priljubljeno lokacijo iz Arraylista
     * @param relacija - Relacija za dodati
     */
    public static void odstraniPriljubljeno(Relacija relacija) {
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            if (priljubljeneRelacije.get(i).getFromName().equals(relacija.getFromName()) && priljubljeneRelacije.get(i).getToName().equals(relacija.getToName())) {
                priljubljeneRelacije.remove(i);
                MainActivity.runs.run();
                break;
            }
        }
    }

    /**
     * Shrani priljubljeno lokacijo v Arraylist
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
