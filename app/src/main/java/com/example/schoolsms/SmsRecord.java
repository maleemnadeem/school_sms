package com.example.schoolsms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
    //smsdata_all
    String URL = "/api/student/smsdata.php?";
    boolean isAllClasses;
    JsonParser parser;
    TextView txtRecordCount;
    Button btnSend;
    private ProgressBar progressBar;
    DbAdapter dbAdapter;
    JSONArray smsJsonArray;
    Handler handler = new Handler();
    TableLayout tableLayout;
    private Context context = null;
    TextView textSmsCount;

    int count =0;

    public SmsRecord(){

        parser = new JsonParser();
        dbAdapter = new DbAdapter(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_record);
        context = getApplicationContext();
        URL = "http://".concat(dbAdapter.getData().concat(URL));
        Intent intent = getIntent();
        DATE = intent.getStringExtra("date");
        CLASS_NAME =  intent.getStringExtra("class_name");
        MSG_TYPE = intent.getStringExtra("msg_type");
        isAllClasses = intent.getBooleanExtra("is_all_classes",false);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_table);
        textSmsCount = (TextView) findViewById(R.id.sms_status);
        txtRecordCount = (TextView)findViewById(R.id.txt_record_count);
        txtRecordCount.setText("Loading....");
        progressBar.setVisibility(View.VISIBLE);

        try {
            if(isAllClasses)
            {
                URL = "/api/student/smsdata_all.php?";
                URL = "http://".concat(dbAdapter.getData().concat(URL));
                URL = URL.concat("d=" + DATE).concat("&m=" + URLEncoder.encode(MSG_TYPE, "UTF-8"));
            }
            else {
                URL = URL.concat("d=" + DATE).concat("&c=" + URLEncoder.encode(CLASS_NAME, "UTF-8"))
                        .concat("&m=" + URLEncoder.encode(MSG_TYPE, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //send request
        getString(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                parser.setSmsData(result.toString());
                txtRecordCount.setText(parser.getSmsData().length()+" Records are found");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        //Set Record count
       btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //progressBar.setMax(parser.getSmsData().length());
                count=0;
                smsJsonArray = parser.getSmsData();
//                //progressBar.setVisibility(View.VISIBLE);
                if (smsJsonArray == null)
                {
                   Toast.makeText(getApplicationContext(), "No Record Found.", Toast.LENGTH_LONG).show();
                }
                else {
                    btnSend.setEnabled(false);
                    sendData.run();
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
                //Toast.makeText(getApplicationContext(), obj.getString("cell_no")+obj.getString("message"), Toast.LENGTH_SHORT).show();
                dataToTable(obj.getString("std_rollno"),"Sent");
                progressBar.setProgress(count);
                textSmsCount.setText(count+1+" of "+smsJsonArray.length());
                sendSMS(obj.getString("cell_no"),obj.getString("message"));
                    handler.postDelayed(this, 20000);
            }
                else {

                handler.removeCallbacks(sendData);
                    btnSend.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                    AlertDialog alertDialog = new AlertDialog.Builder(SmsRecord.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Message sent completely");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
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
            Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_LONG).show();
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void dataToTable(String rollNumber, String status)
    {
        TableRow tableRow = new TableRow(context);

        // Set new table row layout parameters.
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);

        // Add a TextView in the first column.
        TextView txtRollNumber = new TextView(context);
        txtRollNumber.setTextColor(Color.BLACK);
        txtRollNumber.setBackgroundColor(Color.rgb(245,245,245));
        txtRollNumber.setText(rollNumber+" ");
        tableRow.addView(txtRollNumber, 0);

        // Add a TextView in the first column.
        TextView txtStatus = new TextView(context);
        txtStatus.setTextColor(Color.BLACK);
        txtStatus.setBackgroundColor(Color.rgb(220,220,220));
        txtStatus.setText(status+" ");
        tableRow.addView(txtStatus, 1);
        tableLayout.addView(tableRow);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendData);
        // Always call the superclass method first
        Toast.makeText(getApplicationContext(), "process is stopped", Toast.LENGTH_LONG).show();
    }
}


