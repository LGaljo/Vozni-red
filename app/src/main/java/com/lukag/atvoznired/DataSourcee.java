package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class DataSourcee {
    private static HashMap<String, String> postaje = new HashMap<String, String>();
    public static String[] samoPostaje = null;

    /**
     * Metoda napolni HashMap in String Array s podatki o postajah
     * @param context - kontekst razreda iz katerega je klicana metoda
     */
    public static void init(Context context) {
        String job = postajeFromAsset(context);
        try {
            JSONArray arr = new JSONArray(job);
            samoPostaje = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String p = o.getString("postaja");
                String in = o.getString("id");
                postaje.put(p, in);
                samoPostaje[i] = p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Metoda, ki mi iz JSON datoteke prebere vse postaje
     *  Vrne mi string v katerem je postaja
     */
    public static String postajeFromAsset(Context con) {
        String v_json = null;

        try {
            InputStream is = con.getAssets().open("Postaje.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            v_json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return v_json;
    }

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

    /**
     * Metoda vrne String imena postaje iz podanega IDja
     * @param id - id postaje
     * @return - ime postaje
     */
    public static String getIDfromMap(String id) {
        return postaje.get(id);
    }

    /**
     * Metoda vrne današnji datum
     * @return - datum v obliki teksta
     */
    public static String dodajDanasnjiDan() {
        // Današnji datum
        Calendar c = Calendar.getInstance();
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        return today.format(c.getTime());
    }

    /**
     * Funkcija za pretvorbo "pixel" v "density independent pixel"
     * @param px - vrednost piklsov
     * @return - vrednost density independent pixel
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Funkcija za pretvorbo "density independent pixel" v "pixel"
     * @param dp - vrednost density independent pixel
     * @return - vrednost piklsov
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Metoda nastavi obrobe tekstovnih polj glave urnika
     */
    public static Integer calcMargins(Context context) {
        Integer allMargins = 0;
        Integer displayWidth = 0;
        Integer contentWidth = DataSourcee.dpToPx(3*60+65+50);
        Integer layoutPadding = DataSourcee.dpToPx(32);

        try {
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymatrics = new DisplayMetrics();
            display.getMetrics(displaymatrics);

            try{
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allMargins = displayWidth - (contentWidth + layoutPadding);
        allMargins /= 10;

        return allMargins;
    }

    public static void findNextRides(Context context, final priljubljenePostajeAdapter pAdapter) {
        for (final int i[] = {0}; i[0] < favoritesManagement.priljubljeneRelacije.size(); i[0]++) {
            final Relacija iskana = favoritesManagement.priljubljeneRelacije.get(i[0]);

            final String fromID = favoritesManagement.priljubljeneRelacije.get(i[0]).getFromID();
            final String toID = favoritesManagement.priljubljeneRelacije.get(i[0]).getToID();
            final String fromName = favoritesManagement.priljubljeneRelacije.get(i[0]).getFromName();
            final String toName = favoritesManagement.priljubljeneRelacije.get(i[0]).getToName();
            VolleyTool vt = new VolleyTool(context);

            //Log.d("Relacija", "Kličem relacijo " + rel.toString());
            vt.addParam("action", "showRoutes");
            vt.addParam("fromID", fromID);
            vt.addParam("toID", toID);
            vt.addParam("date", dodajDanasnjiDan());
            vt.addParam("general", "false");

            vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

                @Override
                public void getResponse(String response) {
                    try {
                        JSONObject POSTreply = new JSONObject(response);
                        iskana.setUrnik(parseJSONResponse(iskana, POSTreply).getUrnik());

                        Integer ind = 0;
                        Boolean found = false;
                        for (Pot pot : iskana.getUrnik()) {
                            Date time2 = newTime(pot.getStart());
                            //Log.d("COMPARE", time1.toString() + " " + time2.toString());
                            if (primerjajCas(time2)) {
                                found = true;
                                break;
                            }
                            ind++;
                        }
                        //Log.d("Čas", found + " " + iskana.getUrnik().get(ind).getStart());

                        String nextRide;
                        if (found) {
                             nextRide = iskana.getUrnik().get(ind).getStart();
                        } else {
                            nextRide = "tomorrow";
                        }
                        iskana.setNextRide(nextRide);

                        int f = 0;
                        for (Relacija rel_3 : favoritesManagement.priljubljeneRelacije) {
                            if (rel_3.getToName().equals(iskana.getToName()) && rel_3.getFromName().equals(iskana.getFromName())) {
                                favoritesManagement.priljubljeneRelacije.set(f, iskana);
                                pAdapter.notifyDataSetChanged();
                                break;
                            }
                            f++;
                        }

                        //Log.d("","");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Parsaj odgovor iz strežnika
     * @param resp JSONObject - odgovor strežnika
     */
    public static Relacija parseJSONResponse(Relacija iskanaRelacija, JSONObject resp) {
        try {
            String status = resp.get("status").toString();
            String message = resp.get("message").toString();
            Log.d("Status","Strežnik je vrnil " + status + ": " + message);

            JSONArray schedule = resp.getJSONArray("schedule");

            if (schedule.length() == 0) {
                return iskanaRelacija;
            }

            for (int i = 0; i < schedule.length(); i++) {
                Pot novaPot = new Pot();
                novaPot.setID(Integer.parseInt(schedule.getJSONObject(i).getString("ID")));
                novaPot.setStart(schedule.getJSONObject(i).getString("ODHOD_FORMATED"));
                novaPot.setEnd(schedule.getJSONObject(i).getString("PRIHOD_FORMATED"));
                novaPot.setLength(schedule.getJSONObject(i).getString("KM_POT"));
                novaPot.setDuration(schedule.getJSONObject(i).getString("CAS_FORMATED"));
                novaPot.setCost(schedule.getJSONObject(i).getString("VZCL_CEN"));
                String statuss = schedule.getJSONObject(i).getString("STATUS");
                novaPot.setStatus(!statuss.equalsIgnoreCase("over"));
                iskanaRelacija.urnikAdd(novaPot);
                Log.d("JSON parse", iskanaRelacija.getFromName() + " -> " + iskanaRelacija.getToName() + " : " + statuss);
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }

        return iskanaRelacija;
    }

    public static Date newTime(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
        Date time = new Date();

        try {
            time = sdf.parse(dodajDanasnjiDan() + " " + timeStr);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static Boolean primerjajCas(Date time2) {
        Date time1 = trenutniCas();
        if (time1.before(time2)) {
            return true;
        }
        return false;
    }

    public static Date trenutniCas() {
        Date time = Calendar.getInstance().getTime();
        return time;
    }
}
