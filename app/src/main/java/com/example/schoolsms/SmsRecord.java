package com.example.schoolsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SmsRecord extends AppCompatActivity {
    String  DATE;
    String CLASS_NAME;
    String MSG_TYPE;
    String URL = "/api/student/smsdata.php?";
    JsonParser parser;
    TextView txtRecordCount;
    Button btnSend;
    private ProgressBar progressBar;
    DbAdapter dbAdapter;
    JSONArray smsJsonArray;
    Handler handler = new Handler();
    int count =0;

    public SmsRecord(){

        parser = new JsonParser();
        dbAdapter = new DbAdapter(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_record);
        URL = "http://".concat(dbAdapter.getData().concat(URL));
        Intent intent = getIntent();
        DATE = intent.getStringExtra("date");
        CLASS_NAME =  intent.getStringExtra("class_name");
        MSG_TYPE = intent.getStringExtra("msg_type");
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);

        try {
            URL = URL.concat("d="+DATE).concat("&c="+ URLEncoder.encode(CLASS_NAME, "UTF-8"))
                    .concat("&m="+ URLEncoder.encode(MSG_TYPE, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //send request
        getString(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                parser.setSmsData(result.toString());
                txtRecordCount.setText(parser.getSmsData().length()+" Records are found");
            }
        });
        //Set Record count
        txtRecordCount = (TextView)findViewById(R.id.txt_record_count);
       btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setMax(parser.getSmsData().length());
                smsJsonArray = parser.getSmsData();
                count=0;
//                //progressBar.setVisibility(View.VISIBLE);
                if (parser.getSmsData() != null && parser.getSmsData().length() < 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SmsRecord.this);
                    builder.setMessage("Records not found. Please check the input ")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(SmsRecord.this,MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    sendData.run();
                   /* try {
                        int i=0;

                        for (final HashMap<String, String> smsData : parser.getSmsData()) {
                            if(sendSMS(smsData.get("cell_num"),smsData.get("message")))
                            {

                            }
                            else
                            {

                            }
                            progressBar.setProgress(i);
                            Thread.sleep(20000);
                            i++;
                        }
                    }
                    catch (InterruptedException ex)
                    {

                    }*/

                }

            }

        });
    }

    private final Runnable sendData = new Runnable(){
        public void run(){
            try {
                if(count < smsJsonArray.length())
                {
                    progressBar.setVisibility(View.VISIBLE);
                JSONObject obj = smsJsonArray.getJSONObject(count);
                Toast.makeText(getApplicationContext(), obj.getString("cell_no")+obj.getString("message"), Toast.LENGTH_SHORT).show();
                progressBar.setProgress(count);
                //sendSMS(obj.getString("cell_no"),obj.getString("message"));
                    handler.postDelayed(this, 5000);
            }
                else {
                handler.removeCallbacks(sendData);
                progressBar.setVisibility(View.INVISIBLE);
            }
            count++;
        } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public void getString(final VolleyCallback callback) {
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error","Error :" + error.toString());
            }
        });

         mRequestQueue.add(mStringRequest);
    }

    private boolean sendSMS(String number , String msg){
        try{
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(msg);
            sms.sendMultipartTextMessage(number, null, parts, null, null);
            return true;
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}