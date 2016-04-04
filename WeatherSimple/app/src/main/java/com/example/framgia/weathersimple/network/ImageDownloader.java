package com.example.framgia.weathersimple.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.framgia.weathersimple.data.WeatherDataObject;
import com.example.framgia.weathersimple.network.image.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by framgia on 01/04/2016.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;
    private WeatherDataObject weatherObjectData;
    private ProgressDialog pDialog;


    public ImageDownloader(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


    @Override
    protected Bitmap doInBackground(String... param) {
        // TODO Auto-generated method stub
       return null;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        if (pDialog.isShowing())
            pDialog.dismiss();

    }
}
