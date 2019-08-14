package com.lukag.voznired.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.Request;
import com.lukag.voznired.adapters.PriljubljenePostajeAdapter;
import com.lukag.voznired.models.Departure;
import com.lukag.voznired.models.Relacija;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DataSourcee {

    /**
     * Metoda vrne današnji datum v podani obliki
     * @return - datum v obliki teksta
     */
    public static String pridobiCas(String type) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(type, Locale.GERMAN);
        return format.format(calendar.getTime());
    }

    /**
     * Metoda vrne MD5 hash
     * @param string whatever String
     * @return MD5 hash
     */
    public static String md5(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(string.getBytes(), 0, string.length());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metoda vrne MAC naslov
     * @param context Kontekst klicanega razreda
     * @return MAC naslov
     */
    public static String getMacAddr(Context context) {
        try {
            WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            return wifiInf.getMacAddress();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Metoda vrne PhoneId
     * @param context Kontekst klicanega razreda
     * @return PhoneId
     */
    public static String getPhoneInfo(Context context) {
        // Dont return IMEI codes
        return "";
    }

    /**
     * Funkcija za pretvorbo "pixel" v "density independent pixel"
     *
     * @param px - vrednost piklsov
     * @return - vrednost density independent pixel
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Funkcija za pretvorbo "density independent pixel" v "pixel"
     *
     * @param dp - vrednost density independent pixel
     * @return - vrednost piklsov
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Metoda nastavi obrobe tekstovnih polj glave urnika
     */
    public static Integer calcMargins(Context context, int num) {
        Integer allMargins = 0;
        Integer displayWidth = 0;
        Integer contentWidth = DataSourcee.dpToPx(4 * 50 + 60 + 65);
        Integer layoutPadding = DataSourcee.dpToPx(32);

        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymatrics = new DisplayMetrics();
            display.getMetrics(displaymatrics);

            try {
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allMargins = displayWidth - (contentWidth + layoutPadding);
        allMargins /= num;

        return allMargins;
    }

    /**
     * Metoda poišče naslednje tri vožnje za vsako priljubljeno relacijo
     *
     * @param context  kontekst razreda
     * @param pAdapter adapter za priljubljene postaje
     */
    public static void findNextRides(Context context, final PriljubljenePostajeAdapter pAdapter) {
        // Če se ob zagonu zgodi, da ne moreš dobiti idjev zaradi manjkajočega seznama,
        // ga poskusi ustvariti še enkrat
        if (ManageFavs.priljubljeneRelacije.isEmpty()) {
            ManageFavs.pridobiPriljubljene();
        }

        for (final int i[] = {0}; i[0] < ManageFavs.priljubljeneRelacije.size(); i[0]++) {
            final Relacija iskana = ManageFavs.priljubljeneRelacije.get(i[0]);

            String timestamp = DataSourcee.pridobiCas("yyyyMMddHHmmss");
            String token = DataSourcee.md5(BuildConstants.tokenKey + timestamp);
            String url = "https://prometWS.alpetour.si/WS_ArrivaSLO_TimeTable_TimeTableDepartures.aspx";
            StringBuilder ClientId = new StringBuilder();
            ClientId.append("IMEI: ");
            ClientId.append(DataSourcee.getPhoneInfo(context));
            ClientId.append(" , MAC: ");
            ClientId.append(DataSourcee.getMacAddr(context));
            Log.d("API", timestamp + " " + token + " " + ClientId.toString() + " " + DataSourcee.getPhoneInfo(context));
            Log.d("API", iskana.getFromID() + " " + iskana.getToID() + " " + pridobiCas("yyyy-MM-dd"));

            VolleyTool vt = new VolleyTool(context, url);

            vt.addParam("cTimeStamp", timestamp);
            vt.addParam("cToken", token);
            vt.addParam("JPOS_IJPPZ", iskana.getFromID());
            vt.addParam("JPOS_IJPPK", iskana.getToID());
            vt.addParam("VZVK_DAT", pridobiCas("yyyy-MM-dd"));
            vt.addParam("ClientId", ClientId.toString());
            vt.addParam("ClientIdType", DataSourcee.getPhoneInfo(context));
            vt.addParam("ClientLocationLatitude", "");
            vt.addParam("ClientLocationLongitude", "");
            vt.addParam("json", "1");

            vt.executeRequest(Request.Method.POST, response -> {
                try {
                    JSONArray JSONresponse = new JSONArray(response);
                    iskana.setUrnik(parseVozniRed(iskana, JSONresponse).getUrnik());

                    int ind = 0;
                    boolean found = false;
                    for (Departure departure : iskana.getUrnik()) {
                        Date time2 = newTime(departure.getROD_IODH());
                        if (primerjajCas(time2)) {
                            found = true;
                            break;
                        }
                        ind++;
                    }

                    String[] nextRide = new String[3];
                    if (found) {
                        nextRide[0] = iskana.getUrnik().get(ind).getROD_IODH();
                        if (iskana.getUrnik().size() >= 2 + ind) {
                            nextRide[1] = iskana.getUrnik().get(ind + 1).getROD_IODH();
                        }
                        if (iskana.getUrnik().size() >= 3 + ind) {
                            nextRide[2] = iskana.getUrnik().get(ind + 2).getROD_IODH();
                        }
                    } else {
                        nextRide[0] = "tomorrow";
                    }
                    iskana.setNextRide(nextRide);

                    int f = 0;
                    for (Relacija rel_3 : ManageFavs.priljubljeneRelacije) {
                        if (rel_3.getToName().equals(iskana.getToName()) && rel_3.getFromName().equals(iskana.getFromName())) {
                            ManageFavs.priljubljeneRelacije.set(f, iskana);
                            pAdapter.notifyDataSetChanged();
                            break;
                        }
                        f++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Parsaj odgovor iz strežnika
     *
     * @param resp JSONObject - odgovor strežnika
     */
    public static Relacija parseVozniRed(Relacija iskanaRelacija, JSONArray resp) {
        try {
            JSONObject responseObj = resp.getJSONObject(0);
            int napakaID = responseObj.getInt("Error");

            if (napakaID != 0) {
                String napakaMessage = responseObj.getString("ErrorMsg");
                Log.e("API Napaka", napakaMessage);
            }

            JSONArray schedule = responseObj.getJSONArray("Departure");

            if (schedule.length() == 0) {
                return iskanaRelacija;
            }

            for (int i = 0; i < schedule.length(); i++) {
                Departure novaDeparture = new Departure();
                JSONObject obj = schedule.getJSONObject(i);

                novaDeparture.setID(i);
                novaDeparture.setSPOD_SIF(obj.getInt("SPOD_SIF"));
                novaDeparture.setREG_ISIF(obj.getString("REG_ISIF"));
                novaDeparture.setRPR_SIF(obj.getString("RPR_SIF"));
                novaDeparture.setRPR_NAZ(obj.getString("RPR_NAZ"));
                novaDeparture.setOVR_SIF(obj.getString("OVR_SIF"));
                novaDeparture.setROD_IODH(obj.getString("ROD_IODH"));
                novaDeparture.setROD_IPRI(obj.getString("ROD_IPRI"));
                novaDeparture.setROD_CAS(obj.getInt("ROD_CAS"));
                novaDeparture.setROD_PER(obj.getString("ROD_PER"));
                novaDeparture.setROD_KM(obj.getInt("ROD_KM"));
                novaDeparture.setROD_OPO(obj.getString("ROD_OPO"));
                novaDeparture.setVZCL_CEN(obj.getDouble("VZCL_CEN"));
                novaDeparture.setVVLN_ZL(obj.getInt("VVLN_ZL"));
                novaDeparture.setROD_ZAPZ(obj.getString("ROD_ZAPZ"));
                novaDeparture.setROD_ZAPK(obj.getString("ROD_ZAPK"));
                novaDeparture.setStatus(true);

                iskanaRelacija.urnikAdd(novaDeparture);
            }
        } catch (JSONException e) {
            Log.e("getResponse", "Napaka v pri parsanju JSON datoteke");
        }

        return iskanaRelacija;
    }

    /**
     * Iz String vrne objekt Date
     *
     * @param timeStr String časa oblike dd.MM.yyyy HH:mm
     * @return objekt Date
     */
    public static Date newTime(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
        Date time = new Date();

        try {
            time = sdf.parse(pridobiCas("dd.MM.yyyy") + " " + timeStr);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * Metoda odstrani šumnike iz niza
     */
    public static String odstraniSumnike(String string) {
        string = string.replaceAll("[š]", "s");
        string = string.replaceAll("[č]", "c");
        string = string.replaceAll("[ć]", "c");
        string = string.replaceAll("[ž]", "z");
        return string;
    }

    /**
     * Primerja dva časa
     *
     * @param time2 čas, ki ga želiš primerjati s trenutnim
     * @return vrne true, če je time2 pred časom time1
     */
    public static Boolean primerjajCas(Date time2) {
        Date time1 = Calendar.getInstance().getTime();
        return time1.before(time2);
    }
}
