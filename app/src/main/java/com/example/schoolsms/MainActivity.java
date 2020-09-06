package com.example.schoolsms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.provider.Telephony;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DatePickerDialog datePicker;
    ProgressBar viewProgressBar;
    EditText txtDate;
    CheckBox isAllClass;
    //http://royalfoodindustries.com.pk
    String url = "/api/student/read.php";
    JsonParser parser = new JsonParser();
    Spinner classNameSpin;
    Spinner msgTypeSpin;
    DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbAdapter = new DbAdapter(this);
        url = "http://".concat(dbAdapter.getData().concat(url));
        System.out.println(url);
        isAllClass = (CheckBox) findViewById(R.id.is_all_classes);
        txtDate=(EditText) findViewById(R.id.txt_date);
        txtDate.setInputType(InputType.TYPE_NULL);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePicker = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        //spinner for class name
        classNameSpin = (Spinner) findViewById(R.id.class_name);
        msgTypeSpin = (Spinner) findViewById(R.id.msg_type);
        viewProgressBar = (ProgressBar)findViewById(R.id.loading_bar);
        viewProgressBar.setVisibility(View.VISIBLE);
        sendAndRequestResponse();
        //viewProgressBar.setVisibility(View.INVISIBLE);
        //GO Button
        Button btnGo = (Button) findViewById(R.id.btn_go);
        btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!txtDate.getText().toString().equals("")) {
                    Intent intent = new Intent(MainActivity.this, SmsRecord.class);
                    intent.putExtra("date", txtDate.getText().toString());
                    intent.putExtra("class_name", classNameSpin.getSelectedItem().toString());
                    intent.putExtra("msg_type", msgTypeSpin.getSelectedItem().toString());
                    intent.putExtra("msg_type", msgTypeSpin.getSelectedItem().toString());
                    intent.putExtra("is_all_classes", isAllClass.isChecked());
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Please Select the date ")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });

        //Refresh Button
        Button btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAndRequestResponse();
            }

        });
    }
    public void sendAndRequestResponse() {
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Selected User: "+response ,Toast.LENGTH_SHORT).show();
                parser.confiData(response.toString());
                addDataToSpin(classNameSpin,parser.getClassName());
                addDataToSpin(msgTypeSpin,parser.getMsgType());
                viewProgressBar.setVisibility(View.INVISIBLE);

                //System.out.println("Result"+response.toString());

                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error","Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }
    private void addDataToSpin(Spinner spin,ArrayList data)
    {
        ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, data);
        classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(classNameAdapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        finish();
        moveTaskToBack(true);
    }

}
