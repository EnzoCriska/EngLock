package com.phongbm.englock;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Database {
    private String path;
    private SQLiteDatabase sqLiteDatabase;

    public void copyDatabase(Context context) {
        try {
            path = Environment.getDataDirectory().getAbsolutePath()
                    + "/data/com.phongbm.englock/Database.db";
            File file = new File(path);
            if (file.exists()) {
                return;
            }

            // Mở file để đọc
            InputStream is = context.getAssets().open("Database.db");

            // Mở file để ghi
            FileOutputStream fos = new FileOutputStream(file);

            // Tiến hành copy dữ liệu
            byte[] b = new byte[1024];
            int length = 0;

            while ( (length = is.read(b)) != -1) {
                fos.write(b, 0, length);
            }

            // Đóng các luồng đọc, ghi
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
    }

    private void closeDatabase() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public Word[] getRandomTwoWords() {
        openDatabase();

        Cursor cursor =  sqLiteDatabase.rawQuery(
                "SELECT * FROM Technology ORDER BY Random() LIMIT 2",
                null);

        Word[] words = new Word[2];

        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            // Đọc dữ liệu trên 1 dòng
            String en = cursor.getString(cursor.getColumnIndex("EnglishMean"));
            String vn = cursor.getString(cursor.getColumnIndex("VietNameMean"));
            words[i] = new Word(en, vn);
            i++;
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return words;
    }

}