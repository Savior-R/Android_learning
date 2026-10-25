package cn.edu.sicnu.reproject.multi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 实验三 · 主界面 MainActivity。
 *
 * <p>本程序共包含 5 个 Activity：1 个主 Activity（本类）+ 4 个二级 Activity。
 * 主界面上有 4 个按钮，分别启动 4 个二级 Activity，覆盖实验要求的四种场景：</p>
 *
 * <ol>
 *   <li><b>正向传参</b>：把姓名、学号、专业、成绩放进 Intent 的 extras，
 *       交给 {@link ParamActivity} 显示；</li>
 *   <li><b>回传结果</b>：启动 {@link ResultActivity}，等它 {@code setResult()} + {@code finish()}
 *       之后在 {@link #onActivityResult(int, int, Intent)} 里拿到返回值；</li>
 *   <li><b>隐式 Intent 调相机</b>：启动 {@link CameraActivity}，
 *       由它用 {@code new Intent(ACTION_IMAGE_CAPTURE)} 隐式调用系统相机拍照；</li>
 *   <li><b>StopWatch</b>：启动 {@link StopWatchActivity}，横竖屏切换时计时连续不中断。</li>
 * </ol>
 *
 * <p>另外重写了 onStart / onResume / onPause / onStop / onDestroy 五个生命周期回调，
 * 把回调顺序实时追加到界面上的「生命周期回调」一行，便于在实验报告中展示
 * Activity 生命周期的执行次序。</p>
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
public class MainActivity extends AppCompatActivity {

    /** 启动 ResultActivity 并接收其回传结果的请求码 */
    private static final int REQUEST_CODE_RESULT = 1001;

    /** Intent extras 的 key 全部放在字符串资源中，代码里不出现字面量 */
    private String keyName;
    private String keyId;
    private String keyMajor;
    private String keyScore;

    private TextView tvLifecycle;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyName = getString(R.string.extra_key_name);
        keyId = getString(R.string.extra_key_id);
        keyMajor = getString(R.string.extra_key_major);
        keyScore = getString(R.string.extra_key_score);

        tvLifecycle = findViewById(R.id.tvLifecycle);
        tvResult = findViewById(R.id.tvResult);

        // ① 正向传参：显式 Intent + putExtra
        findViewById(R.id.btnParam).setOnClickListener(v -> {
            Intent intent = new Intent(this, ParamActivity.class);
            intent.putExtra(keyName, getString(R.string.student_name));
            intent.putExtra(keyId, getString(R.string.student_id));
            intent.putExtra(keyMajor, getString(R.string.student_major));
            intent.putExtra(keyScore, getString(R.string.student_score));
            startActivity(intent);
        });

        // ② 等待结果：startActivityForResult + onActivityResult
        findViewById(R.id.btnResult).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivityForResult(intent, REQUEST_CODE_RESULT);
        });

        // ③ 隐式 Intent 调相机（在二级 Activity 中实现）
        findViewById(R.id.btnCamera).setOnClickListener(v ->
                startActivity(new Intent(this, CameraActivity.class)));

        // ④ StopWatch 计时器
        findViewById(R.id.btnStopWatch).setOnClickListener(v ->
                startActivity(new Intent(this, StopWatchActivity.class)));
    }

    /**
     * 接收二级 Activity 回传的数据并显示在主界面上。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESULT && resultCode == RESULT_OK && data != null) {
            String value = data.getStringExtra(getString(R.string.extra_key_result));
            tvResult.setText(value);
        }
    }

    // ==================== Activity 生命周期回调 ====================

    @Override
    protected void onStart() {
        super.onStart();
        appendLifecycle("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        appendLifecycle("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        appendLifecycle("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        appendLifecycle("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appendLifecycle("onDestroy");
    }

    /**
     * 把当前触发的生命周期回调追加到界面上（形如：onStart → onResume）。
     */
    private void appendLifecycle(String callback) {
        if (tvLifecycle == null) {
            return;
        }
        String label = getString(R.string.main_lifecycle_label);
        StringBuilder sb = new StringBuilder(label);
        String current = tvLifecycle.getText().toString();
        int idx = current.indexOf(label);
        if (idx != -1 && current.length() > idx + label.length()) {
            sb.append(current.substring(idx + label.length())).append(" → ");
        }
        sb.append(callback);
        tvLifecycle.setText(sb.toString());
    }
}
