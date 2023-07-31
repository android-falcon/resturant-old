package restaurant.apps.falcons.flaconsrestaurant.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import restaurant.apps.falcons.flaconsrestaurant.activities.InvoiceActivity;

/**
 * Created by pure_ on 22/10/2016.
 */

public class DismissNotification extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        int notificationId = intent.getIntExtra("notId", 0);
        int reqType = intent.getIntExtra("reqType", -1);
        final int finalNotificationId = (int) (notificationId / Math.pow(10, (reqType + 1)));

        new Thread() {
            @Override
            public void run() {
                super.run();
                DataManager.dismissNotification(context, String.valueOf(finalNotificationId));
            }
        }.start();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}