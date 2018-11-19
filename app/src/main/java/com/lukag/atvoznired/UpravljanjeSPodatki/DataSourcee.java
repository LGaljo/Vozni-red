package com.lukag.atvoznired.UpravljanjeSPodatki;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.Request;
import com.lukag.atvoznired.Adapterji.priljubljenePostajeAdapter;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Relacija;

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

    /**
     * Metoda poišče naslednje tri vožnje za vsako priljubljeno relacijo
     * @param context kontekst razreda
     * @param pAdapter adapter za priljubljene postaje
     */
    public static void findNextRides(Context context, final priljubljenePostajeAdapter pAdapter) {
        for (final int i[] = {0}; i[0] < UpravljanjeSPriljubljenimi.priljubljeneRelacije.size(); i[0]++) {
            final Relacija iskana = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]);

            final String fromID = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]).getFromID();
            final String toID = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]).getToID();
            final String fromName = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]).getFromName();
            final String toName = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]).getToName();
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

                        String[] nextRide = new String[3];
                        if (found) {
                             nextRide[0] = iskana.getUrnik().get(ind).getStart();
                             if (iskana.getUrnik().size() >= 2 + ind) {
                                 nextRide[1] = iskana.getUrnik().get(ind + 1).getStart();
                             }
                              if (iskana.getUrnik().size() >= 3 + ind) {
                                  nextRide[2] = iskana.getUrnik().get(ind + 2).getStart();
                             }
                        } else {
                            nextRide[0] = "tomorrow";
                        }
                        iskana.setNextRide(nextRide);

                        int f = 0;
                        for (Relacija rel_3 : UpravljanjeSPriljubljenimi.priljubljeneRelacije) {
                            if (rel_3.getToName().equals(iskana.getToName()) && rel_3.getFromName().equals(iskana.getFromName())) {
                                UpravljanjeSPriljubljenimi.priljubljeneRelacije.set(f, iskana);
                                pAdapter.notifyDataSetChanged();
                                break;
                            }
                            f++;
                        }

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

    /**
     * Iz String vrne objekt Date
     * @param timeStr String časa oblike dd.MM.yyyy HH:mm
     * @return objekt Date
     */
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

    /**
     * Primerja dva časa
     * @param time2 čas, ki ga želiš primerjati s trenutnim
     * @return vrne true, če je time2 pred časom time1
     */
    public static Boolean primerjajCas(Date time2) {
        Date time1 = Calendar.getInstance().getTime();
        return time1.before(time2);
    }
}
