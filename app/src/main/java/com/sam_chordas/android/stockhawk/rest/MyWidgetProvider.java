package com.sam_chordas.android.stockhawk.rest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by dell on 6/17/2016.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {



        for(int i=0;i<appWidgetIds.length;i++){

            Intent intent=new Intent(context,Item_Widget_List.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                remoteViews.setRemoteAdapter(R.id.widget_lv,intent);
            else
                remoteViews.setRemoteAdapter(appWidgetIds[i],R.id.widget_lv,intent);

            remoteViews.setEmptyView(R.id.widget_lv,R.id.empty_view);

          /*  Intent it=new Intent("android.intent.action.MAIN");
            it.addCategory("android.intent.category.LAUNCHER");
            it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            it.setComponent(new ComponentName(context.getPackageName(),"MyStocksActivity.class"));
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, it, 0);
            remoteViews.setOnClickPendingIntent(R.id.ll_widget,pendingIntent);*/



            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],R.id.widget_lv);
            appWidgetManager.updateAppWidget(appWidgetIds[i],remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
}
