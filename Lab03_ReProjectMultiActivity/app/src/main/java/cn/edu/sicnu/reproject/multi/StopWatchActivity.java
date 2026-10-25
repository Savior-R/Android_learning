package cn.edu.sicnu.reproject.multi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * 实验三 · 二级 Activity ④：StopWatch 计时器，要求横屏 / 竖屏切换时计时连续不中断。
 *
 * <p>连续计时的关键在于：<strong>以 {@link SystemClock#elapsedRealtime()} 的绝对时刻为基准</strong>，
 * 而不是在 onCreate() 里从 0 开始累加。</p>
 * <ul>
 *   <li>{@code mBaseTime}：最近一次开始 / 恢复计时的绝对时刻；</li>
 *   <li>{@code mAccumulated}：本段计时结束之前已累计的毫秒数；</li>
 *   <li>{@code mRunning}：当前是否处于计时状态。</li>
 * </ul>
 *
 * <p>旋转屏幕时 Activity 会被销毁重建，{@code onSaveInstanceState()} 把三个状态保存下来，
 * {@code onCreate()} 恢复后因为基准是开机以来的单调时钟，所以计时天然连续、不会归零。
 * 这正是实验目的中「掌握横屏竖屏改变的时候信息的保存和恢复」的落点。</p>
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
public class StopWatchActivity extends AppCompatActivity implements Runnable {

    /** 刷新间隔：50ms，保证显示到百分秒 */
    private static final long REFRESH_INTERVAL = 50L;

    /** onSaveInstanceState 中使用的 Bundle key（放在字符串资源中，避免代码里出现字面量） */
    private String keyAccumulated;
    private String keyBaseTime;
    private String keyRunning;

    private TextView tvTime;

    /** 计时基准：最近一次开始 / 恢复时刻（elapsedRealtime 绝对值） */
    private long mBaseTime;
    /** 已累计毫秒数 */
    private long mAccumulated;
    /** 是否正在计时 */
    private boolean mRunning;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        tvTime = findViewById(R.id.tvTime);
        keyAccumulated = getString(R.string.state_key_accumulated);
        keyBaseTime = getString(R.string.state_key_base_time);
        keyRunning = getString(R.string.state_key_running);

        if (savedInstanceState != null) {
            // 横竖屏切换后从 Bundle 恢复状态，计时不会中断
            mBaseTime = savedInstanceState.getLong(keyBaseTime, 0L);
            mAccumulated = savedInstanceState.getLong(keyAccumulated, 0L);
            mRunning = savedInstanceState.getBoolean(keyRunning, false);
        }

        findViewById(R.id.btnStart).setOnClickListener(v -> start());
        findViewById(R.id.btnPause).setOnClickListener(v -> pause());
        findViewById(R.id.btnReset).setOnClickListener(v -> reset());

        updateTime();
        if (mRunning) {
            handler.post(this);
        }
    }

    /** 开始 / 继续计时：把基准重置为当前时刻 */
    private void start() {
        mBaseTime = SystemClock.elapsedRealtime();
        mRunning = true;
        handler.post(this);
    }

    /** 暂停：把本段时长累加到 mAccumulated 中，并停止刷新 */
    private void pause() {
        if (mRunning) {
            mAccumulated += SystemClock.elapsedRealtime() - mBaseTime;
            mRunning = false;
        }
        handler.removeCallbacks(this);
        updateTime();
    }

    /** 复位：累计时长清零 */
    private void reset() {
        handler.removeCallbacks(this);
        mBaseTime = 0L;
        mAccumulated = 0L;
        mRunning = false;
        updateTime();
    }

    /** 获取当前计时总毫秒数 */
    private long currentElapsed() {
        return mRunning ? mAccumulated + (SystemClock.elapsedRealtime() - mBaseTime) : mAccumulated;
    }

    /** 把计时结果格式化为 mm:ss.SS */
    private void updateTime() {
        long ms = currentElapsed();
        long totalSeconds = ms / 1000;
        long minute = totalSeconds / 60;
        long second = totalSeconds % 60;
        long hundredths = (ms % 1000) / 10;
        tvTime.setText(String.format(Locale.US, "%02d:%02d.%02d", minute, second, hundredths));
    }

    @Override
    public void run() {
        updateTime();
        if (mRunning) {
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    }

    /**
     * 旋转屏幕前保存计时状态（累计值、基准、运行标志），保证重建后连续。
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(keyAccumulated, currentElapsed());
        outState.putLong(keyBaseTime, mBaseTime);
        outState.putBoolean(keyRunning, mRunning);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
    }
}
