package restaurant.apps.falcons.flaconsrestaurant.util;

/**
 * Created by Salah on 8/17/2016.
 */

import android.app.*;
import android.content.*;
import android.graphics.BitmapFactory;
import android.os.*;

import android.view.View;

import androidx.core.app.NotificationCompat;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Table;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        getBaseContext().startService(new Intent(getBaseContext(), MyService.class));
    }

    @Override
    public void onCreate() {
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                try {
                    List<String> needWaiterTablesIds = DataManager.checkIfWaiterIsCalled(getApplicationContext());
                    List<Table> ordersTables = DataManager.checkForOrders(getApplicationContext());
                    List<Table> checkOutTables = DataManager.checkForCheckOuts(getApplicationContext());
                    List<String> dismissedTables = DataManager.getDismissedTables();
                    List<String> undismissedTablesIds = DataManager.getUnDismissedTablesIds();

                    for (String tableId : needWaiterTablesIds) {
                        int notId = Integer.parseInt(tableId);
                        generateNotification(getApplicationContext(), getString(R.string.need_a_waiter), getString(R.string.table) + " " + tableId, notId * 10, 0);
                    }

                    for (Table table : ordersTables) {
                        int notId = Integer.parseInt(table.getId());
                        generateNotification(getApplicationContext(), getString(R.string.order_is_ready), getString(R.string.table) + " " + table.getId(), notId * 100, 1);
                    }

                    for (Table table : checkOutTables) {
                        int notId = Integer.parseInt(table.getId());
                        generateNotification(getApplicationContext(), getString(R.string.ready_for_checkout) + " - " + table.getNote(), getString(R.string.table) + " " + table.getId(), notId * 1000, 2);
                    }

                    for (String tableId : dismissedTables) {
                        if (undismissedTablesIds.contains(tableId)) continue;
                        int notId = Integer.parseInt(tableId);
                        dismissNotification(notId);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS));

        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        getBaseContext().startService(new Intent(getBaseContext(), MyService.class));
    }

    private static void generateNotification(Context context, String message, String title, int notId, int reqType) {

        Intent dismissIntent = new Intent(context, DismissNotification.class);
        dismissIntent.putExtra("notId", notId);
        dismissIntent.putExtra("reqType", reqType);

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, notId, dismissIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                        .setContentTitle(title)
                        .setContentText(message).setAutoCancel(true)
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        mBuilder.addAction(0, context.getString(R.string.dismiss), dismissPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
            if (smallIconViewId != 0) {
                if (notification.contentIntent != null)
                    notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                if (notification.headsUpContentView != null)
                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }

        mNotificationManager.notify(notId, notification);

        int tableId = (int) (notId / (Math.pow(10, (reqType + 1))));
        DataManager.sendNotification(context, tableId + "");
    }

    private void dismissNotification(int notificationId) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}