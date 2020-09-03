package com.example.schoolsms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UrlConfig extends AppCompatActivity {
    DbAdapter dbAdapter;
    EditText txtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAdapter = new DbAdapter(this);
        if(dbAdapter.getData().equalsIgnoreCase("")) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_url_config);
            Button btnSave = (Button) findViewById(R.id.save);
            txtUrl = (EditText) findViewById(R.id.url);
            btnSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!txtUrl.getText().toString().equalsIgnoreCase(""))
                    dbAdapter.insertData(txtUrl.getText().toString());
                    Intent intent = new Intent(UrlConfig.this, MainActivity.class);
                    startActivity(intent);
                }

            });
        }
        else
        {
            Intent intent = new Intent(UrlConfig.this, MainActivity.class);
            startActivity(intent);
        }
    }
}