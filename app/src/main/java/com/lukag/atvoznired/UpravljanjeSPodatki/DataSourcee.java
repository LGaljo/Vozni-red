package com.lukag.atvoznired.UpravljanjeSPodatki;

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
import com.lukag.atvoznired.Adapterji.priljubljenePostajeAdapter;
import com.lukag.atvoznired.Objekti.BuildConstants;
import com.lukag.atvoznired.Objekti.Pot;
import com.lukag.atvoznired.Objekti.Relacija;

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
     *
     * @return - datum v obliki teksta
     */
    public static String pridobiCas(String type) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(type, Locale.GERMAN);
        return format.format(calendar.getTime());
    }


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
    public static Integer calcMargins(Context context) {
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
        allMargins /= 12;

        return allMargins;
    }

    /**
     * Metoda poišče naslednje tri vožnje za vsako priljubljeno relacijo
     *
     * @param context  kontekst razreda
     * @param pAdapter adapter za priljubljene postaje
     */
    public static void findNextRides(Context context, final priljubljenePostajeAdapter pAdapter) {
        // Če se ob zagonu zgodi, da ne moreš dobiti idjev zaradi manjkajočega seznama,
        // ga poskusi ustvariti še enkrat
        if (UpravljanjeSPriljubljenimi.priljubljeneRelacije.isEmpty()) {
            UpravljanjeSPriljubljenimi.pridobiPriljubljene();
        }


        for (final int i[] = {0}; i[0] < UpravljanjeSPriljubljenimi.priljubljeneRelacije.size(); i[0]++) {
            final Relacija iskana = UpravljanjeSPriljubljenimi.priljubljeneRelacije.get(i[0]);

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

            vt.executeRequest(Request.Method.POST, new VolleyTool.VolleyCallback() {

                @Override
                public void getResponse(String response) {
                    try {
                        JSONArray JSONresponse = new JSONArray(response);
                        iskana.setUrnik(parseJSONResponse(iskana, JSONresponse).getUrnik());

                        Integer ind = 0;
                        Boolean found = false;
                        for (Pot pot : iskana.getUrnik()) {
                            Date time2 = newTime(pot.getRod_iodh());
                            if (primerjajCas(time2)) {
                                found = true;
                                break;
                            }
                            ind++;
                        }

                        String[] nextRide = new String[3];
                        if (found) {
                            nextRide[0] = iskana.getUrnik().get(ind).getRod_iodh();
                            if (iskana.getUrnik().size() >= 2 + ind) {
                                nextRide[1] = iskana.getUrnik().get(ind + 1).getRod_iodh();
                            }
                            if (iskana.getUrnik().size() >= 3 + ind) {
                                nextRide[2] = iskana.getUrnik().get(ind + 2).getRod_iodh();
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
     *
     * @param resp JSONObject - odgovor strežnika
     */
    public static Relacija parseJSONResponse(Relacija iskanaRelacija, JSONArray resp) {
        try {
            JSONObject responseObj = resp.getJSONObject(0);
            int napakaID = responseObj.getInt("Error");

            if (napakaID != 0) {
                String napakaMessage = responseObj.getString("ErrorMsg");
                Log.e("API", napakaMessage);
            }

            JSONArray schedule = responseObj.getJSONArray("Departures");

            if (schedule.length() == 0) {
                return iskanaRelacija;
            }

            for (int i = 0; i < schedule.length(); i++) {
                Pot novaPot = new Pot();
                JSONObject obj = schedule.getJSONObject(i);

                novaPot.setID(i);
                novaPot.setSpod_sif(obj.getInt("SPOD_SIF"));
                novaPot.setReg_isif(obj.getString("REG_ISIF"));
                novaPot.setRpr_sif(obj.getString("RPR_SIF"));
                novaPot.setRpr_naz(obj.getString("RPR_NAZ"));
                novaPot.setOvr_sif(obj.getString("OVR_SIF"));
                novaPot.setRod_iodh(obj.getString("ROD_IODH"));
                novaPot.setRod_ipri(obj.getString("ROD_IPRI"));
                novaPot.setRod_cas(obj.getInt("ROD_CAS"));
                novaPot.setRod_per(obj.getString("ROD_PER"));
                novaPot.setRod_km(obj.getInt("ROD_KM"));
                novaPot.setRod_opo(obj.getString("ROD_OPO"));
                novaPot.setVzcl_cen(obj.getDouble("VZCL_CEN"));
                novaPot.setVvln_zl(obj.getInt("VVLN_ZL"));
                novaPot.setRod_zapz(obj.getString("ROD_ZAPZ"));
                novaPot.setRod_zapk(obj.getString("ROD_ZAPK"));
                // TODO: Nastavi status poteka glede na trenutni čas
                novaPot.setStatus(true);

                iskanaRelacija.urnikAdd(novaPot);
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
