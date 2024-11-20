package com.skt.help.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.skt.help.data.database.DatabaseHelper;
import com.skt.help.model.UserCondition;

public class DatabaseRepository {
    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // 데이터베이스 열기
    public void open() {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    // 데이터 삽입 메서드
    public UserCondition insertInitialUserCondition() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_KEYWORD, "도와줘");
        values.put(DatabaseHelper.COLUMN_CONDITIONS, "");

        long id = sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, values);
        return fetchUserCondition(id);
    }

    // 사용자 업데이트 메서드
    public int updateUser(long id, String keyword, String  conditions) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_KEYWORD, keyword);
        values.put(DatabaseHelper.COLUMN_CONDITIONS, conditions);

        return sqLiteDatabase.update(DatabaseHelper.TABLE_NAME,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // 테이블이 비어있는지 확인하는 메서드
    public boolean isTableEmpty(String tableName) {
        boolean isEmpty = true;
        Cursor cursor = null;

        try {
            String query = "SELECT COUNT(*) FROM " + tableName;
            cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0); // 첫 번째 열에 있는 COUNT 값을 가져옴
                isEmpty = (count == 0); // count가 0이면 테이블이 비어있음
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isEmpty;
    }

    public UserCondition fetchUserCondition(long id) {
        UserCondition userCondition = null;

        Cursor cursor = sqLiteDatabase.query(
                "user_conditions",
                new String[]{DatabaseHelper.COLUMN_KEYWORD, DatabaseHelper.COLUMN_CONDITIONS},
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String keyword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEYWORD));
            @SuppressLint("Range") String conditions = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONDITIONS));
            userCondition = new UserCondition(keyword, conditions);
            cursor.close();
        }

        return userCondition;
    }

}
