package com.sridhar008.updatedialogandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    private static  String url = "https://sites.google.com/site/myapps4748/android/updates.json";
    String VersionUpdate,appnames,appurls,appimgg;
    TextView appname;
    Button click;
    ImageView appimgs;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appname=(TextView)findViewById(R.id.appname);

        click=(Button)findViewById(R.id.click);
        appimgs=(ImageView)findViewById(R.id.appimgs);
        new VersionCheck().execute();

    }

    Bitmap image;
    private class VersionCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();


            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray version = jsonObj.getJSONArray("Version");
                    for (int i = 0; i < version.length(); i++)
                    {

                        JSONObject v = version.getJSONObject(i);
                        VersionUpdate = v.getString("version");
                        appnames=v.getString("appname");
                        appurls=v.getString("appurl");
                        appimgg=v.getString("appimg");
                        image=(getBitmapFromURL(appimgg));

                    }



                }
                catch (final JSONException e)
                {
                    // Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                //Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            String VersionName = BuildConfig.VERSION_NAME;
            if (VersionUpdate.equals(VersionName))
            {
                //Do Nothing
                Log.e("msg","app version : "+VersionUpdate);
                Log.e("msg","appname  :"+appnames);
                Log.e("msg","appurl:  "+appurls);
                appname.setText(""+appnames);
                click.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        /*startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(appurls)));*/

                        startActivity(new Intent(MainActivity.this,NewsPaperActivity.class));
                    }
                });
                appimgs.setImageBitmap(image);
            }
            else
            {
                Log.e("msg","apps update");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Our App got Update");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setCancelable(false);
                builder.setMessage("New version available, select update to update our app")
                        .setPositiveButton("UPDATE", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final String appName = getPackageName();
                                try
                                {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                                }
                                catch (android.content.ActivityNotFoundException anfe)
                                {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                                }
                                finish();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
