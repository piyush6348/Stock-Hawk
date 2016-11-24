package com.sam_chordas.android.stockhawk.rest;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 6/17/2016.
 */

public class Item_Widget_List extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new Other_Widget_List(this.getApplicationContext(),intent);
    }
}
class Other_Widget_List implements RemoteViewsService.RemoteViewsFactory {
    Cursor c;
    private Context context = null;
    private int appWidgetId;
    RemoteViews row;

    public Other_Widget_List(Context context, Intent intent){

        this.context=context;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {
        Log.e("Item_Widget_Oncreate","is running");


    }

    @Override
    public void onDataSetChanged() {
        Log.e("Item_Widget_onDataSEt","is running");
        initData();
    }

    private void initData() {
        if(c!=null)
            c.close();

        c=context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},null);
    }

    @Override
    public void onDestroy() {
    if(c!=null)
        c.close();
    }

    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.e("Item_Widget_getview","is running");
        String symbol = "";
        String bidPrice = "";
        String change = "";

        if(c.moveToPosition(position)){
            symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));

            bidPrice = c.getString(c.getColumnIndex(QuoteColumns.BIDPRICE));

            change = c.getString(c.getColumnIndex(QuoteColumns.PERCENT_CHANGE));

        }

        row=new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        row.setTextViewText(R.id.stock_symbol,symbol);
        row.setTextViewText(R.id.bid_price,bidPrice);
        row.setTextViewText(R.id.change,change);
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
