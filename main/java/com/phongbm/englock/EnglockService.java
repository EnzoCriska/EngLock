package com.phongbm.englock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;

// Để đối tượng Service chạy được
// Bưới 1: Đăng ký với hệ thống Android: AndroidManifest.xml
// Bưới 2: Start service
public class EnglockService extends Service {
    // Khai báo đối tượng thuộc tính
    private ScreenStateReceiver screenStateReceiver;
    private LockScreenView lockScreenView;
    // Đối tượng WindowManager cho phép hiển thị một View
    // nổi lên trên tất cả các loại giao diện
    private WindowManager windowManager;

    // Đối tượng dùng để gửi và nhận thông điệp
    // Sử dụng trong case:
    // LockScreenView: gửi tín hiệu ẩn giao diện câu hỏi
    // Service: nhận tín hiệu đó và ẩn giao diện câu hỏi đi
    private Handler handler;

    private boolean isLockScreenAdded; // false

    @Override
    public void onCreate() {
        super.onCreate();
        registerScreenStateReceiver();
        disableKeyguard();

        // Khởi tạo đối tượng
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 200) {
                    hideLockScreen();
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START_STICKY: Cho phép service có thể tự động
        // hồi sinh và chạy tiếp
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterScreenStateReceiver();
        enableKeyguard();
        super.onDestroy();
    }

    // Phương thức dùng để hiển thị giao diện câu hỏi
    private void showLockScreen() {
        if (isLockScreenAdded) {
            lockScreenView.setupWords();
            return;
        }

        // Khởi tạo đối tượng
        lockScreenView = new LockScreenView(this, handler);

        // Hiển thị lên giao diện
        // Sử dụng WindowManager
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.TRANSPARENT;

        windowManager.addView(lockScreenView, params);

        isLockScreenAdded = true;
    }

    private void hideLockScreen() {
        if (isLockScreenAdded) {
            windowManager.removeView(lockScreenView);
            isLockScreenAdded = false;
        }
    }

    private void disableKeyguard() {
        KeyguardManager manager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("IN");
        lock.disableKeyguard();
    }

    private void enableKeyguard() {
        KeyguardManager manager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("IN");
        lock.reenableKeyguard();
    }

    // Đối tượng ScreenStateReceiver được dùng để
    // lắng nghe sự kiện màn hình được tắt
    // Khi màn hình tắt thì sẽ hiển thị lên
    // giao diện câu hỏi mở khóa
    // Để sử dụng được BroadcastReceiver
    // chúng ta phải đăng ký với hệ thống
    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Khi màn hình được tắt thì phương thức
            // onReceive(...) sẽ được chạy
            showLockScreen();
        }
    }

    // Đăng ký
    private void registerScreenStateReceiver() {
        // Khởi tạo đối tượng
        screenStateReceiver = new ScreenStateReceiver();

        // Đăng ký
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenStateReceiver, filter);
    }

    // Hủy đăng ký
    private void unregisterScreenStateReceiver() {
        unregisterReceiver(screenStateReceiver);
    }

}