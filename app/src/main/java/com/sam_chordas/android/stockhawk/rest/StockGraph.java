package com.sam_chordas.android.stockhawk.rest;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dell on 6/14/2016.
 */
public class StockGraph extends Fragment {
    Date d1,d2;
    LineChartView lcv;
    LineSet dataset;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StockGraph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockGraph newInstance(String param1, String param2) {
        StockGraph fragment = new StockGraph();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_line_graph, container, false);
       // int k=getArguments().getInt("pos");
        String k=getArguments().getString("sym");

        lcv=(LineChartView)v.findViewById(R.id.linechart);

        Calendar c=Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String current_date =df.format(c.getTime());


        try {
             d1=df.parse(current_date);
             d2=df.parse(current_date);

            if(d2.getDay()<=7){
                int kd=d2.getDay();
                int km=d2.getMonth();
                d2.setDate(30-7+kd);
                if(km==1)
                    d2.setMonth(12);
                else
                    d2.setMonth(km-1);
            }
            else {
                int kd=d2.getDay();
                d2.setDate(kd-7);
            }

           /* if(d2.getMonth()==1)
            {   int kh=d2.getMonth();
                int ky=d2.getYear();
                d2.setMonth(12);
                d2.setYear(ky-1);
            }
            else
            {
                int kh=d2.getMonth();
                d2.setMonth(kh-1);
            }*/
            //int kh=d2.getYear();
            //d2.setYear(kh-1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String url1="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20";
        url1=url1+k+"%20%3D%20%22YHOO%22%20and%20"+df.format(d2)+"%20%3D%20%222009-09-11%22%20and%20"+df.format(d1)+
                "%20%3D%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        Log.e("position clicked",k+"");
        Log.e("Date 2",df.format(d2)+"");
        Log.e("Date 1",df.format(d1)+"");

        String url2="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22";
        url2=url2+k+"%22%20and%20startDate%20%3D%20%22"+df.format(d2)+"%22%20and%20endDate%20%3D%20%22";
        url2+=df.format(d1)+"%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        new piyush().execute(url2);

        return v;
    }
    private class piyush extends AsyncTask<String,Void,LineSet>{

        @Override
        protected LineSet doInBackground(String... params) {
            try {
                String url2=params[0];
                OkHttpClient client=new OkHttpClient();
                Request req= new Request.Builder().url(url2).build();
                Response res=client.newCall(req).execute();
                String response=res.body().string();

                try {
                    JSONObject ans=new JSONObject(response);
                    JSONObject query=ans.getJSONObject("query");
                    int num=query.getInt("count");

                    //if(num>20)
                   //     num=20;
                    String[] dates=new String[num];
                    float[] values=new float[num];
                    JSONObject results=query.getJSONObject("results");
                    JSONArray quote=results.getJSONArray("quote");

                    for(int i=0;i<num;i++)
                    {
                        JSONObject x=quote.getJSONObject(i);
                        dates[i]=x.getString("Date");
                        values[i]=(float)x.getDouble("Adj_Close");
                    }

                     dataset=new LineSet(dates,values);
                   // lcv.addData(dataset);
                   // lcv.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataset;
        }

        @Override
        protected void onPostExecute(LineSet lineSet) {
            lcv.addData(lineSet);
            lcv.show();
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
