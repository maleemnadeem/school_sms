package com.example.schoolsms;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonParser extends AppCompatActivity {
    ArrayList<String> className = new ArrayList<>();
    ArrayList<String> msgType = new ArrayList<>();
    ArrayList<HashMap<String, String>> smsDataList = new ArrayList<>();
    JSONArray smsJsonArray;

    public void confiData(String json) {
        try {
            JSONObject jObj = new JSONObject(json);
            JSONArray jsonArry = jObj.getJSONArray("records");
            for (int i = 0; i < jsonArry.length(); i++) {
                JSONObject obj = jsonArry.getJSONObject(i);
                System.out.println("-----------------------"+obj.getString("class_name"));
                //Toast.makeText(getApplicationContext(),"Selected User: "+obj.getString("class_name") ,Toast.LENGTH_SHORT).show();
               if(!className.contains(obj.getString("class_name"))) {
                   className.add(obj.getString("class_name"));
               }
               if(!msgType.contains(obj.getString("msg_type"))) {
                   msgType.add(obj.getString("msg_type"));
               }
            }
        }
        catch (JSONException ex){

        }
    }

    public ArrayList<String> getClassName()
    {
        return className;
    }
    public ArrayList<String> getMsgType()
    {
        return msgType;
    }
    public void setSmsData(String json){
        try {
            JSONObject jObj = new JSONObject(json);
            smsJsonArray = jObj.getJSONArray("records");
           /* for(int i=0;i<jsonArry.length();i++) {
                HashMap<String,String> smsData = new HashMap<>();
                JSONObject obj = jsonArry.getJSONObject(i);
                smsData.put("cell_num", obj.getString("cell_no"));
                smsData.put("message", obj.getString("message"));
                smsData.put("std_rollno", obj.getString("std_rollno"));
                smsDataList.add(smsData);
            }*/
            }catch (JSONException ex){}

        }
        public JSONArray getSmsData(){
            return smsJsonArray;
        }
    }

