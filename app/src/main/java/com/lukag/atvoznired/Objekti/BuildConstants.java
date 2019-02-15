package com.lukag.atvoznired.Objekti;

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
}
