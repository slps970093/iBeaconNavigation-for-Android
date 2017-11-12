package com.example.chouyuhsien.hwai_ibeacon_prj_1029;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    FragmentManager fm  = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new web().execute();
        /*
         *藍芽權限 用於Android 6.0 以上權限檢查
         */
        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("權限","false");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }else{
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this, "Not Support", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.myui,new BLEListFragment());
                    ft.commit();
                }
            }
        }else{
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, "Not Support", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.myui,new BLEListFragment());
                ft.commit();
            }
        }
        startService(new Intent(this,iBeaconScanService.class));

    }
    class web extends AsyncTask<Void,Void,Void>{
        String msg = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iBeaconSQLite sqlHelper = new iBeaconSQLite(getApplicationContext(),null);
            SQLiteDatabase db = sqlHelper.getWritableDatabase();
            db.delete("iBeacon_devices",null,null); //重置資料
            Log.e("json aa:",msg);
            try{
                JSONArray jsonArray = new JSONArray(msg);
                for(int i = 0;i<=jsonArray.length()-1;i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String mac_address = jsonObject.getString("mac_address");
                    String link = jsonObject.getString("link");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    db.execSQL("INSERT INTO `iBeacon_devices`(`id`,`iBeacon_Mac_Address`,`iBeacon_link`,`iBeacon_title`,`iBeacon_content`) VALUES (NULL,?,?,?,?)",
                            new String[]{mac_address,link,title,content});
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Cursor cursor =  db.rawQuery("SELECT * FROM iBeacon_devices",null);
            Log.e("size",String.valueOf(cursor.getCount()));
            if(cursor.getCount() != 0){
                Toast.makeText(MainActivity.this, "資料庫已新增 "+cursor.getCount()+" 筆資料可供使用", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL("http://school.slps970093.nctu.me/hwai_iBeacon_guide/public/index.php/api/ibeacon/get").openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        message.write(buffer, 0, len);
                    }
                    // 释放资源
                    is.close();
                    message.close();
                    // 返回字符串
                    msg = new String(message.toByteArray());
                    Log.e("JSON: ",msg);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
