/*
Assignment #HW04
801081291_HW04.zip
Abinandaraj Rajendran (801081291)
Abineshwar Angamuthu Matheswaran (801075297)
 */

package com.example.rabin.hw04;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button go;
    ImageView image, prev, next;
    String[] output, outputkey;
    int temp = 0;
    EditText search;
    String s;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");
        image = (ImageView) findViewById(R.id.iv_main);
        prev = (ImageView) findViewById(R.id.iv_previous);
        next = (ImageView) findViewById(R.id.iv_next);
        search = (EditText) findViewById(R.id.et_search);
        prev.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        final RequestParams params = new RequestParams();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Internet Enabled", Toast.LENGTH_SHORT).show();
            new GetDataAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
            new GetImageAsync().execute("http://dev.theappsdr.com/apis/photos/index.php");
            go = (Button) findViewById(R.id.b_go);
            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    temp = 0;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                    builder.setTitle("Choose a keyword").setItems(outputkey, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            s = outputkey[i];
                            search.setText(s);
                            RequestParams params = new RequestParams();
                            params.addParameter("keyword", outputkey[i]);
                            new GetDataUsingParamAsync(params).execute("http://dev.theappsdr.com/apis/photos/index.php");

                        }

                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (temp < output.length - 1) {
                        temp = temp + 1;
                        new GetImage1Async().execute(output[temp]);


                    } else {
                        temp = 0;

                        new GetImage1Async().execute(output[temp]);

                    }
                }
            });
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (temp != 0) {
                        temp = temp - 1;
                        new GetImage1Async().execute(output[temp]);
                    } else {
                        temp = output.length - 1;
                        new GetImage1Async().execute(output[temp]);
                    }
                }
            });
        }
    }


    private class GetDataAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            InputStream inputStream = null;
            StringBuilder stringBuilder = new StringBuilder();
            String result = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
                outputkey = result.split(";");
            else { }
        }
    }

    private class GetImageAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                /*for (int i = 0; i < 100; i++) {
                    for (int j = 1; j < 100000; j++) {
                    }
                    publishProgress(i);
                }*/

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }
    }

    private class GetDataUsingParamAsync extends AsyncTask<String, Void, String> {
        RequestParams mparams;

        public GetDataUsingParamAsync(RequestParams params) {
            mparams = params;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            InputStream inputStream = null;
            StringBuilder stringBuilder = new StringBuilder();
            String result = null;
            try {
                URL url = new URL(mparams.getEncodedUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {


            if (!result.equals("")) {
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                output = result.split(".jpg");
                for (int i = 0; i < output.length; i++) {
                    output[i] += ".jpg";
                   }
                try {
                    p = new ProgressDialog(MainActivity.this);
                    for (int i = 0; i < 100; i++) {
                        for (int j = 0; j < 10; j++) {
                            p.setMessage("Loading");
                            p.show();
                        }
                    }
                    if (p.isShowing()) {
                        p.dismiss();
                    }

                    new GetImage1Async().execute(output[temp]);
                    next.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                    image.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    prev.setVisibility(View.INVISIBLE);
                }

            } else {
                Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                next.setVisibility(View.INVISIBLE);
                prev.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
            }

        }
    }

    private class GetImage1Async extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }
    }


}



