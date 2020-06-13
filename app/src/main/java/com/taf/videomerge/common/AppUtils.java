package com.taf.videomerge.common;

public class AppUtils {
    public static String convertTime(int duration) {
        String time = "";
        int durationInSecond = duration / 1000;
        int minutes = durationInSecond / 60;
        if (minutes < 10) {
            time += "0" + minutes;
        } else {
            time += minutes;
        }
        int seconds = durationInSecond - minutes * 60;
        if (seconds < 10) {
            time += ":0" + seconds;
        } else {
            time += ":" + seconds;
        }
        return time;
    }

    public static int stringToTime(String timeString) {
        String[] timeSplit = timeString.split(":");
        int minutes = Integer.parseInt(timeSplit[0]);
        int seconds =  Integer.parseInt(timeSplit[1]);
        return (minutes * 60 + seconds) * 1000;
    }
}
