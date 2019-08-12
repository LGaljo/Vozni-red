package com.lukag.voznired.helpers;

import com.lukag.voznired.models.Relacija;

import java.util.HashMap;

public class BuildConstants {
    final static public String tokenKey = "R300_VozniRed_2015";
    public static HashMap<String, String> seznamPostaj = new HashMap<>();
    public static Relacija relacija;

    public static BuildConstants buildConstants;

    private BuildConstants() {}

    public static BuildConstants getInstance() {
        if (buildConstants == null) {
            buildConstants = new BuildConstants();
        }
        return buildConstants;
    }


    public static final int PEEK_DRAWER_TIME_SECONDS = 2000;
    public static final int PEEK_DRAWER_START_DELAY_TIME_SECONDS = 1000;
    public static final String BASE_URL = "https://prometWS.alpetour.si/";
}
