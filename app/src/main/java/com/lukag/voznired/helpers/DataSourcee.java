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

import androidx.annotation.NonNull;

import com.lukag.voznired.adapters.PriljubljenePostajeAdapter;
import com.lukag.voznired.models.Departure;
import com.lukag.voznired.models.Relacija;
import com.lukag.voznired.models.ResponseDepartures;
import com.lukag.voznired.retrofit_interface.APICalls;
import com.lukag.voznired.retrofit_interface.RetrofitFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.lukag.voznired.helpers.BuildConstants.BASE_URL;

public class DataSourcee {
    private static final String TAG = DataSourcee.class.getSimpleName();

    /**
     * Metoda vrne MD5 hash
     *
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
     *
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
     *
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
     * Iz String vrne objekt Date
     *
     * @param timeStr String časa oblike dd.MM.yyyy HH:mm
     * @return objekt Date
     */
    public static Date newTime(String timeStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
        Date time = new Date();

        try {
            time = sdf.parse(pridobiCas("dd.MM.yyyy") + " " + timeStr);
            calendar.setTime(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

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
