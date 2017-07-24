package com.example.lhb.jt808;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Byte2;
import android.util.Log;

import com.example.lhb.jt808.Jt808Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/4 0004.
 */

public class JT808SQLite {

    SQLiteDatabase db;

    public JT808SQLite(){}

    public void openDatabase(Context context)
    {
       // db=SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.lhb/databases/aa.db", null);
        // androi4.2中引入了多用户机制，普通用户无法访问根目录下的/data/data目录

        db=SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getAbsolutePath()+"JT808Data.db", null);
//        Cursor cursor=db.rawQuery("select count(*)  from sqlite_master where type='table' and name = 'SEND_DATAS'",null);
//        if(cursor.moveToNext()){
//            int count=cursor.getInt(0);
//            if(count<=0){
//                db.execSQL("CREATE TABLE SEND_DATAS(_id integer PRIMARY KEY AUTOINCREMENT NOT NULL,value BLOB)");
//                Log.e("cursor-count","create");
//            }
//            Log.e("cursor-count","create");
//        }
        db.execSQL("CREATE TABLE IF NOT EXISTS SEND_DATAS(_id integer PRIMARY KEY AUTOINCREMENT NOT NULL,value BLOB)");

    }

    public List<byte[]> getList(Context context)
    {
        openDatabase(context);
        List<byte[]>arr=new ArrayList<>();
        Cursor cursor=db.rawQuery("select value from SEND_DATAS",null);
        while (cursor.moveToNext())
        {
            arr.add(cursor.getBlob(cursor.getColumnIndex("value")));
        }
        Log.e("cursor-count",cursor.getCount()+"");
        return  arr;
    }

    public void drop(Context context)
    {
        db=SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getAbsolutePath()+"JT808Data.db", null);
        db.execSQL("DROP TABLE IF EXISTS SEND_DATAS");
        closeDatabases();
    }

    public void insert(byte[] data)
    {
        db.execSQL("INSERT INTO SEND_DATAS(value) values(?)",new Object[]{data});
    }

    public void closeDatabases()
    {
        if(db!=null&&db.isOpen())
            db.close();
    }


    public void insertData(Context context, byte[] sendbyte)
    {
        openDatabase(context);
        insert(sendbyte);
        closeDatabases();
    }

}
