package com.example.chouyuhsien.hwai_ibeacon_prj_1029;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.List;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

/**
 * Created by Chou Yu Hsien on 2017/11/4.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class iBeaconScanService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60000;
    // use android sdk VERSION > 19 (如果超過ANDROID4.4版本以上)
    private BluetoothLeScanner bluetoothLeScanner;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //任務執行
        bluetoothManager =  (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (Build.VERSION.SDK_INT <= 19) {
                mBluetoothAdapter.startLeScan(LeScanCallback);
            } else {
                bluetoothLeScanner.startScan(scanCallback);
            }

        } else {
            if (Build.VERSION.SDK_INT <= 19) {
                mBluetoothAdapter.stopLeScan(LeScanCallback);
            } else {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
    }
    //Device SDK Version >19 BLE
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes(), System.currentTimeMillis());
            // 如果設備為 IBeacon Device
            if (BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON) {
                final IBeaconDevice iBeaconDevice = new IBeaconDevice(deviceLe);
                iBeacon_distance(deviceLe.getRssi(),iBeaconDevice.getUUID(),iBeaconDevice.getCalibratedTxPower());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    // Device SDK Version <= 19 BLE  (方法適用於設備在Android SDK Version <= 19)
    private  BluetoothAdapter.LeScanCallback LeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            Log.e("Device Name: ", deviceLe.getName());
            Log.e("Device RSSI: ", String.valueOf(deviceLe.getRssi()));
            // 如果設備為 IBeacon Device
            if (BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON) {
                final IBeaconDevice iBeaconDevice = new IBeaconDevice(deviceLe);
                iBeacon_distance(deviceLe.getRssi(),iBeaconDevice.getUUID(),iBeaconDevice.getCalibratedTxPower());
            }
            Log.e("Address:", deviceLe.getAddress());

        }
    };
    //距離換算
    private void iBeacon_distance(final int iBeacon_RSSI,final String UUID,final int TxPower){
        double power = iBeacon_RSSI * 1.0 / TxPower;
        double distance = Math.pow(power,10);
        if(distance <= 1){
            search_ble_sqlite(UUID);
        }
    }
    //搜尋SQLite 比對UUID 如資料符合跳出訊息
    private void search_ble_sqlite(final String Ibeacon_UUID){
        //取得通知管理器
        NotificationManager noMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        iBeaconSQLite sqlHelper = new iBeaconSQLite(getApplicationContext(),null);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM `iBeacon_devices` WHERE `iBeacon_UUID` =  ? ",new String[]{Ibeacon_UUID});
        if(cursor.getCount() != 0){
            if(cursor.getString(cursor.getColumnIndex("iBeacon_link")) != null){
                /*
                 * 點擊通知後執行開啟 Browser
                 */
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("iBeacon_link")));
                //當使用者按下通知欄中的通知要開啟的Activity
                Intent call = new Intent(Intent.ACTION_VIEW,uri);
                //建立待處理意圖
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),0,call,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            /// /跳出通知
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(cursor.getString(cursor.getColumnIndex("iBeacon_title")))
                    .setContentText(cursor.getString(cursor.getColumnIndex("iBeacon_content")));
            Notification notification = mBuilder.build();
            noMgr.notify(1,notification);
        }
    }
}
