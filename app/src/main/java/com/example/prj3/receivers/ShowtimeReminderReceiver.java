package com.example.prj3.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.prj3.R;
import com.example.prj3.activities.MainActivity;

/**
 * BroadcastReceiver nhận alarm từ AlarmManager
 * và hiển thị notification nhắc giờ chiếu phim.
 */
public class ShowtimeReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_MOVIE_TITLE = "movie_title";
    public static final String EXTRA_THEATER     = "theater";
    public static final String EXTRA_SHOW_TIME   = "show_time";
    private static final String CHANNEL_ID       = "cinetix_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE);
        String theater    = intent.getStringExtra(EXTRA_THEATER);
        String showTime   = intent.getStringExtra(EXTRA_SHOW_TIME);

        String title = "🎬 Sắp đến giờ chiếu!";
        String body  = movieTitle + " lúc " + showTime
                     + "\n📍 " + theater
                     + "\n⏰ Còn 30 phút nữa – hãy chuẩn bị!";

        sendNotification(context, title, body);
    }

    private void sendNotification(Context context, String title, String body) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 300, 200, 300})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getColor(R.color.primary))
                .setContentIntent(pendingIntent);

        NotificationManager manager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "CineTix Thông báo", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
