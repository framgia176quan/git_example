package com.example.framgia.weathersimple.util;

/**
 * Created by framgia on 31/03/2016.
 */
public class Tool {
    public static float convertKenvilToCelcius(float kenvil) {
        float fahrenheit = (float) (1.8*(kenvil-273)+32);
        return Math.round(((fahrenheit - 32) * 5 / 9));
    }
}
