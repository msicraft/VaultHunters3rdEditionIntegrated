package me.msicraft.vaulthuntersintegrated.aCommon.Util;

public class MathUtil {

    public static double getRandomValueDouble(double max, double min) {
        if (max == min || Double.compare(max, min) == 0) {
            return max;
        }
        double x = (Math.random() * (max - min) + min);
        return Math.round(x * 100.0) / 100.0;
    }

    public static int getRandomValueInt(int max, int min) {
        if (min == max) {
            return max;
        }
        return (int) (Math.random() * (max - min + 1) + min);
    }

}
