package com.phongbm.englock;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

public class LockScreenView extends FrameLayout implements View.OnClickListener {
    // Đối tượng LayoutInflater dùng để ánh xạ một file giao diện .xml
    // thành đối tượng View
    // Khai báo
    private LayoutInflater inflater;

    private TextView txtEn;
    private TextView txtVi1;
    private TextView txtVi2;

    private Handler handler;
    private Database database;
    private Random random;
    private Word[] words;

    private Vibrator vibrator;

    public LockScreenView(Context context, Handler handler) {
        super(context);

        this.handler = handler;
        database = new Database();
        random = new Random();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        database.copyDatabase(context);

        // Khởi tạo đối tượng
        inflater = LayoutInflater.from(context);
        // Gắn file giao diện .xml với file java
        inflater.inflate(R.layout.view_lock_screen, this);

        txtEn = (TextView) findViewById(R.id.txt_en);
        txtVi1 = (TextView) findViewById(R.id.txt_vi_1);
        txtVi2 = (TextView) findViewById(R.id.txt_vi_2);

        txtVi1.setOnClickListener(this);
        txtVi2.setOnClickListener(this);

        setupWords();
    }

    public void setupWords() {
        words = database.getRandomTwoWords();

        txtEn.setText(words[0].getEnglish());

        if (random.nextInt(100) < 50) {
            txtVi1.setText(words[0].getVi());
            txtVi2.setText(words[1].getVi());
        } else {
            txtVi1.setText(words[1].getVi());
            txtVi2.setText(words[0].getVi());
        }
    }

    @Override
    public void onClick(View view) {
        /* Bỏ
        // Mở khóa màn hình
        // Gửi thông điệp sang bên Service
        handler.sendEmptyMessage(200);
        */

        switch (view.getId()) {
            case R.id.txt_vi_1:
                if (txtVi1.getText().equals(words[0].getVi())) {
                    handler.sendEmptyMessage(200);
                } else {
                    vibrator.vibrate(1000);
                    setupWords();
                }
                break;

            case R.id.txt_vi_2:
                if (txtVi2.getText().equals(words[0].getVi())) {
                    handler.sendEmptyMessage(200);
                } else {
                    vibrator.vibrate(1000);
                    setupWords();
                }
                break;
        }
    }

}