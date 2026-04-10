package com.example.prj3.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.prj3.receivers.ShowtimeReminderReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Đặt lịch nhắc nhở trước giờ chiếu phim 1 phút
 * bằng AlarmManager.
 */
public class ReminderScheduler {

    private static final String TAG = "ReminderScheduler";
    private static final long REMIND_BEFORE_MS = 1 * 60 * 1000; // 1 phút

    /**
     * @param date   định dạng "dd/MM/yyyy"
     * @param time   định dạng "HH:mm"
     */
    public static void schedule(Context context, String movieTitle,
                                String theater, String date, String time) {
        // Parse ngày giờ chiếu
        long showtimeMs = parseShowtime(date, time);
        if (showtimeMs < 0) {
            Log.w(TAG, "Không parse được ngày giờ: " + date + " " + time);
            return;
        }

        long triggerMs = showtimeMs - REMIND_BEFORE_MS;
        if (triggerMs <= System.currentTimeMillis()) {
            // Đã qua giờ, không cần nhắc
            Log.d(TAG, "Suất chiếu đã qua, bỏ qua nhắc nhở");
            return;
        }

        // Tạo Intent cho BroadcastReceiver
        Intent intent = new Intent(context, ShowtimeReminderReceiver.class);
        intent.putExtra(ShowtimeReminderReceiver.EXTRA_MOVIE_TITLE, movieTitle);
        intent.putExtra(ShowtimeReminderReceiver.EXTRA_THEATER, theater);
        intent.putExtra(ShowtimeReminderReceiver.EXTRA_SHOW_TIME, time);

        int requestCode = (movieTitle + date + time).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager =
            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // Trên Android 12+ cần quyền SCHEDULE_EXACT_ALARM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent);
            } else {
                // Fallback: inexact alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent);
        }

        Log.d(TAG, "Đã đặt nhắc nhở: " + movieTitle
            + " vào lúc " + time + " ngày " + date
            + " (alarm lúc " + new Date(triggerMs) + ")");
    }

    private static long parseShowtime(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm", Locale.getDefault());
            Date d = sdf.parse(date + " " + time);
            return d != null ? d.getTime() : -1;
        } catch (ParseException e) {
            Log.e(TAG, "Parse error: " + e.getMessage());
            return -1;
        }
    }
}
