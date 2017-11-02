package com.sridhar008.updatedialogandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsPaperActivity extends AppCompatActivity
{

    String filepath;
    TextView textView;
    PDFView pdfView;
    String pdfFileName;
    File pdfFile;
    String extStorageDirectory;
    String fileName1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_paper);

        textView=(TextView)findViewById(R.id.textView);


        filepath="https://sites.google.com/site/myapps4748/android/telugunewspapers/Sakshi_Ts.pdf";

        pdfView= (PDFView)findViewById(R.id.pdfView);


         fileName1 = filepath.substring( filepath.lastIndexOf('/')+1, filepath.length() );
        Log.e("msg","url last name :"+fileName1);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "testthreepdf");
        folder.mkdir();

        pdfFile = new File(folder, fileName1);


        try
        {
            pdfFile.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        /*final ProgressDialog pDialog = new ProgressDialog(NewsPaperActivity.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        WebView webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();
            }
        });
        String pdf = "https://sites.google.com/site/myapps4748/android/telugunewspapers/Eenadu_Ts.pdf";
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);*/
    }



    public void download(View v)
    {
        new DownloadFile().execute();
    }

    public void view(View v)
    {


       if(pdfFile.length()==0)
       {

           Toast.makeText(NewsPaperActivity.this,"if Please Download",Toast.LENGTH_SHORT).show();
       }
       else
       {
           pdfView.fromFile(pdfFile)
                   .enableSwipe(true)
                   .swipeHorizontal(false)
                   .enableAnnotationRendering(true)
                   .scrollHandle(new DefaultScrollHandle(this))
                   .load();

       }
    }



    private class DownloadFile extends AsyncTask<String, Integer, Void>
    {
        ProgressDialog progressDialog;
        private static final int  MEGABYTE = 1024 * 1024;
        private  int downloadedSize = 0;
        private  int totalsize = 0;
        private  float per = 0;
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(NewsPaperActivity.this);
            progressDialog.setTitle("Loading... "+fileName1);
            progressDialog.setMessage("you can Read offline after loading\n*Use 3G/WiFi for fast download.");
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings)
        {


            //FileDownloader.downloadFile(fileUrl, pdfFile);

            try
            {

                URL url = new URL(filepath);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                totalsize = urlConnection.getContentLength();
                Log.e("msg","total size :"+totalsize);

                byte[] buffer = new byte[1024*1024];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 )
                {
                    fileOutputStream.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    per = ((float) downloadedSize / totalsize) * 100;

                    Integer[] numArr = new Integer[1];
                    numArr[0] = Integer.valueOf((int) (((float) downloadedSize / totalsize) * 100));
                    publishProgress(numArr);

                    //Log.e("msg","per :"+(int)per);
                    setText("Total PDF File size  : "
                            + (totalsize / 1024)
                            + " KB\n\nDownloading PDF " + (int) per
                            + "% complete");




                }

                fileOutputStream.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            progressDialog.setProgress(progress[0].intValue());
        }
    }

    private void setText(final String s)
    {

        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(s);
            }
        });
    }
}
