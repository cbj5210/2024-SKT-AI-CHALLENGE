package com.skt.help.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름과 버전
    private static final String DATABASE_NAME = "EmergencyHelp.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 이름과 컬럼 정의
    public static final String TABLE_NAME = "user_conditions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_KEYWORD = "keyword";
    public static final String COLUMN_CONDITIONS = "conditions";

    // 테이블 생성 SQL 쿼리
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_KEYWORD + " TEXT, " +
                    COLUMN_CONDITIONS + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스가 업그레이드될 때 호출되며, 기존 테이블을 삭제하고 새 테이블을 생성
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
