package com.lukag.atvoznired.UpravljanjeSPodatki;

import android.content.Context;
import android.content.SharedPreferences;

import com.lukag.atvoznired.MainActivity;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.Objekti.Relacija;

import java.util.ArrayList;
import java.util.List;

public class UpravljanjeSPriljubljenimi {

    private static UpravljanjeSPriljubljenimi upravljanjeSPriljubljenimi;

    private Context context;
    private SharedPreferences shramba;
    private int size;
    public static List<Relacija> priljubljeneRelacije;

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
        SharedPreferences.Editor urejevalnik = this.shramba.edit();
        urejevalnik.clear();
        urejevalnik.putInt("number", 0);
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
        SharedPreferences.Editor urejevalnik = this.shramba.edit();
        izbrisiPriljubljene();
        for (int i = 0; i < priljubljeneRelacije.size(); i++) {
            urejevalnik.putString(Integer.toString(i), priljubljeneRelacije.get(i).getFromName() + ":" + priljubljeneRelacije.get(i).getToName());
            this.size += 1;
            // +1, ker je iterator za 1 manjši od števila relacij
            urejevalnik.putInt("number", i + 1);
        }

        urejevalnik.apply();
    }

    /**
     * Shrani priljubljene iz shared preferences v arraylist
     */
    public void pridobiPriljubljene() {
        priljubljeneRelacije.clear();

        for (int i = 0; i < this.size; i++) {
            Relacija rela = parseRelacija(this.shramba.getString(Integer.toString(i), ""));
            rela.setFromID(BuildConstants.seznamPostaj.get(rela.getFromName()));
            rela.setToID(BuildConstants.seznamPostaj.get(rela.getToName()));
            priljubljeneRelacije.add(rela);
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
            // TODO: Disable for now
            //MainActivity.runs.run();
            return true;
        }
        return false;
    }

    /**
     * Prejmeš kodiran string in vrneš relacijo
     * @param relacija - relacija kot "postaja1:postaja2"
     * @return - vrneš objekt relacije
     */
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
