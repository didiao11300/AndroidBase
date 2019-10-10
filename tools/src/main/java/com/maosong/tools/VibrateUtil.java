package com.maosong.tools;

import android.os.Vibrator;
import static android.content.Context.VIBRATOR_SERVICE;

public class VibrateUtil {
    private static Vibrator sInstance;

    public static void shortVibrator() {
        if (null == sInstance) {
            sInstance = (Vibrator) ToolsApp.getAppContext().getSystemService(VIBRATOR_SERVICE);
        }
        if (sInstance.hasVibrator()) {
            sInstance.cancel();
        }
        long[] time = {40, 100, 40, 100};
        sInstance.vibrate(time, -1);
    }

    public static void longVibrator() {
        if (null == sInstance) {
            sInstance = (Vibrator) ToolsApp.getAppContext().getSystemService(VIBRATOR_SERVICE);
        }
        if (sInstance.hasVibrator()) {
            sInstance.cancel();
        }
        sInstance.vibrate(1000);
    }
}
