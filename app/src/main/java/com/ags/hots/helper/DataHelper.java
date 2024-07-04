package com.ags.hots.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    public static final String TABLE_SOAL = "soal";
    public static final String COLUMN_ID = "id_soal";//0
    public static final String COLUMN_NO = "nomor";//1
    public static final String COLUMN_SOAL = "soal";//2
    public static final String COLUMN_JAWABAN1 = "jawaban1";//3
    public static final String COLUMN_JAWABAN2 = "jawaban2";//4
    public static final String COLUMN_JAWABAN3 = "jawaban3";//5
    public static final String COLUMN_JAWABAN4 = "jawaban4";//6
    public static final String COLUMN_JAWABAN5 = "jawaban5";//7
    public static final String COLUMN_STATUS = "status";//8
    public static final String COLUMN_BANYAK = "banyak";//9
    public static final String TABLE_POPULER = "populer";
    public static final String COLUMN_IDKELAS = "id_kelas";//0
    public static final String COLUMN_NAMAKELAS = "nama_kelas";//1
    public static final String COLUMN_FOTOKELAS = "foto_kelas";//1
    public static final String COLUMN_STATKELAS = "status";//2
    private static final String DATABASE_NAME = "dataasn.db";
    private static final int DATABASE_VERSION = 1;


    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table " + TABLE_SOAL + "(" + COLUMN_ID + " text null," +
                COLUMN_NO + " text null, " +
                COLUMN_SOAL + " text null," +
                COLUMN_JAWABAN1 + " text null," +
                COLUMN_JAWABAN2 + " text null, " +
                COLUMN_JAWABAN3 + " text null," +
                COLUMN_JAWABAN4 + " text null," +
                COLUMN_JAWABAN5 + " text null," +
                COLUMN_STATUS + " text null," +
                COLUMN_BANYAK + " text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);

        String sql2 = "create table " + TABLE_POPULER + "(" + COLUMN_IDKELAS + " text null," +
                COLUMN_NAMAKELAS + " text null, " +
                COLUMN_FOTOKELAS + " text null, " +
                COLUMN_STATKELAS + " text null);";
        Log.d("Data", "onCreate: " + sql2);
        db.execSQL(sql2);
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        arg0.execSQL("DROP TABLE IF EXISTS soal");
        arg0.execSQL("DROP TABLE IF EXISTS populer");
    }


}