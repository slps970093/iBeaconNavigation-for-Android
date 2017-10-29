package com.example.chouyuhsien.hwai_ibeacon_prj_1029;


import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    FragmentManager fm  = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
