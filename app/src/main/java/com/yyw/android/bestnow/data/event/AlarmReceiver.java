package com.yyw.android.bestnow.data.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by samsung on 2016/12/19.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        Bundle extras = intent.getExtras();

        Intent service = new Intent(context, ScheduleService.class);
        service.setData(uri);
        service.putExtras(extras);
        startWakefulService(context, service);
    }

}
