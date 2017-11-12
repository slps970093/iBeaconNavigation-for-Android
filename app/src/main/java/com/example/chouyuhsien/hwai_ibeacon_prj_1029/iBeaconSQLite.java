package com.example.chouyuhsien.hwai_ibeacon_prj_1029;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chou Yu Hsien on 2017/11/4.
 */

public class iBeaconSQLite extends SQLiteOpenHelper {
    public iBeaconSQLite(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, "iBeacon_devices.db", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE iBeacon_devices"+
                    "(id INTEGER PRIMARY KEY NOT NULL,"+
                    "iBeacon_Mac_Address VARCHAR,"+
                    "iBeacon_link VARCHAR,"+
                    "iBeacon_title VARCHAR,"+
                    "iBeacon_content VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
