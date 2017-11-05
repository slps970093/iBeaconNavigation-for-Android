package com.example.chouyuhsien.hwai_ibeacon_prj_1029;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    FragmentManager fm  = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         *藍芽權限
         */
        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},Manifest.permission.ACCESS_COARSE_LOCATION);
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


    }
}
