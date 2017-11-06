package com.example.chouyuhsien.hwai_ibeacon_prj_1029;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.M)

public class MainActivity extends AppCompatActivity {
    FragmentManager fm  = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
                 *  藍芽權限
                 *  http://blog.csdn.net/KjunChen/article/details/52769915
                 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"自Android 6.0以後需要開啟藍芽權限才可以搜索BLE設備，請允許",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }else{
                showUI();
            }
        }else{
            showUI();
        }
    }
    public void showUI(){
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
