package restaurant.apps.falcons.flaconsrestaurant.util;

/**
 * Created by Salah on 8/17/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyService.class));
    }
}
