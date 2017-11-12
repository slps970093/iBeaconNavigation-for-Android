package com.example.chouyuhsien.hwai_ibeacon_prj_1029;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BLEListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BLEListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public BLEListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BLEListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BLEListFragment newInstance(String param1, String param2) {
        BLEListFragment fragment = new BLEListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60000;
    private Handler mHander = new Handler();
    private boolean mScanning;
    // use android sdk VERSION > 19 (如果超過ANDROID4.4版本以上)
    private BluetoothLeScanner bluetoothLeScanner;
    //自定義列表
    private LinkedList<HashMap<String,BLEList>> mData  = new LinkedList<HashMap<String,BLEList>>();
    private BLEListAdapter mAdapter = null;
    private ListView blelist_view;
    private HashMap<String,BLEList> iBeacon_device = new HashMap<String,BLEList>();
    private ArrayList deviceList = new ArrayList();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blelist, container, false);
        bluetoothManager =  (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }else{

            scanLeDevice(true);
        }
        return view;
    }
    /*
     * 版本判斷參考
     * https://developer.android.com/guide/topics/manifest/uses-sdk-element.html
     */
    private void scanLeDevice(final boolean enable) {
        if(enable){
            //幾秒後停止
            mHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if(Build.VERSION.SDK_INT <=19){
                        mBluetoothAdapter.stopLeScan(LeScanCallback);
                    }else{
                        bluetoothLeScanner.stopScan(scanCallback);
                    }
                }
            },SCAN_PERIOD);
            mScanning = true;
            if(Build.VERSION.SDK_INT <= 19){
                mBluetoothAdapter.startLeScan(LeScanCallback);
            }else{
                bluetoothLeScanner.startScan(scanCallback);
            }

        }else{
            mScanning = false;
            if(Build.VERSION.SDK_INT <= 19){
                mBluetoothAdapter.stopLeScan(LeScanCallback);
            }else{
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
            // iBeacon
            String UUID = "";
            if(BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON){
                final IBeaconDevice iBeaconDevice = new IBeaconDevice(deviceLe);
                Log.e("UUID:",iBeaconDevice.getUUID());
                UUID = iBeaconDevice.getUUID();
            }

            //產生列表
            if(blelist_view == null){
                blelist_view = (ListView) getActivity().findViewById(R.id.ble_list);
            }
            if(deviceLe.getName() != null) {
                Log.e("Name ", deviceLe.getName());
                Log.e("Address:", deviceLe.getAddress());
            }
            if(UUID != null){
                // 搜尋物件是否存在
                if(!iBeacon_device.containsKey(deviceLe.getAddress())){
                    iBeacon_device.put(deviceLe.getAddress(),new BLEList(R.mipmap.ic_launcher,deviceLe.getName(),"RSSI: "+String.valueOf(deviceLe.getRssi())+" UUID: "+ UUID));
                    mData.add(iBeacon_device);
                    deviceList.add(deviceLe.getAddress());
                }
                Log.e("size: ",String.valueOf(mData.size()));
            }else{
                if(!iBeacon_device.containsKey(deviceLe.getAddress())){
                    iBeacon_device.put(deviceLe.getAddress(),new BLEList(R.mipmap.ic_launcher,deviceLe.getName(),"RSSI: "+String.valueOf(deviceLe.getRssi())));
                    mData.add(iBeacon_device);
                    deviceList.add(deviceLe.getAddress());
                }
                Log.e("size: ",String.valueOf(mData.size()));
            }
            mAdapter = new BLEListAdapter((LinkedList<HashMap<String,BLEList>>) mData,getActivity(),deviceList);
            mAdapter.notifyDataSetChanged();    //更新內容
            blelist_view.setAdapter(mAdapter);
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
    private  BluetoothAdapter.LeScanCallback LeScanCallback = new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device,rssi,scanRecord,System.currentTimeMillis());
            Log.e("Device Name: ",deviceLe.getName());
            Log.e("Device RSSI: ", String.valueOf(deviceLe.getRssi()));
            // iBeacon
            String UUID = "";
            if(BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON){
                final IBeaconDevice iBeaconDevice = new IBeaconDevice(deviceLe);
                Log.e("UUID:",iBeaconDevice.getUUID());
                UUID = iBeaconDevice.getUUID();
            }
            //產生列表
            if(blelist_view == null){
                blelist_view = (ListView) getActivity().findViewById(R.id.ble_list);
            }
            Log.e("Address:",deviceLe.getAddress());
            if(UUID != null){
                // 搜尋物件是否存在
                if(iBeacon_device.containsKey(deviceLe.getAddress())){
                    iBeacon_device.put(deviceLe.getAddress(),new BLEList(R.mipmap.ic_launcher,deviceLe.getName(),"RSSI: "+String.valueOf(deviceLe.getRssi())+" UUID: "+ UUID));
                    mData.add(iBeacon_device);
                    deviceList.add(deviceLe.getAddress());
                }
                Log.e("size: ",String.valueOf(mData.size()));
            }else{
                if(iBeacon_device.containsKey(deviceLe.getAddress())){
                    iBeacon_device.put(deviceLe.getAddress(),new BLEList(R.mipmap.ic_launcher,deviceLe.getName(),"RSSI: "+String.valueOf(deviceLe.getRssi())));
                    mData.add(iBeacon_device);
                    deviceList.add(deviceLe.getAddress());
                }
                Log.e("size: ",String.valueOf(mData.size()));
            }
            mAdapter = new BLEListAdapter((LinkedList<HashMap<String,BLEList>>) mData,getActivity(),deviceList);
            mAdapter.notifyDataSetChanged();    //更新內容
            blelist_view.setAdapter(mAdapter);
        }
    };
}
