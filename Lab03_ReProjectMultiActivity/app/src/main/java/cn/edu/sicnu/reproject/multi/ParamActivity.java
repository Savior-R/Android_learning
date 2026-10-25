package cn.edu.sicnu.reproject.multi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 实验三 · 二级 Activity ①：接收 MainActivity 正向传递（putExtra）的参数并显示。
 *
 * <p>正向传参的三个步骤：</p>
 * <ol>
 *   <li>MainActivity：{@code intent.putExtra(key, value)} 把数据放进 Intent；</li>
 *   <li>MainActivity：{@code startActivity(intent)} 启动本 Activity；</li>
 *   <li>本 Activity：{@code getIntent().getStringExtra(key)} 把数据取出来显示。</li>
 * </ol>
 *
 * <p>注意 key 也来自字符串资源（{@code R.string.extra_key_*}），
 * 这样布局文件与代码中都不会出现直接书写的字符串。</p>
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
public class ParamActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvId;
    private TextView tvMajor;
    private TextView tvScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        tvName = findViewById(R.id.tvName);
        tvId = findViewById(R.id.tvId);
        tvMajor = findViewById(R.id.tvMajor);
        tvScore = findViewById(R.id.tvScore);

        Intent intent = getIntent();
        tvName.setText(pick(intent.getStringExtra(getString(R.string.extra_key_name))));
        tvId.setText(pick(intent.getStringExtra(getString(R.string.extra_key_id))));
        tvMajor.setText(pick(intent.getStringExtra(getString(R.string.extra_key_major))));
        tvScore.setText(pick(intent.getStringExtra(getString(R.string.extra_key_score))));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /**
     * 参数为空时给出占位提示，避免界面出现空白。
     */
    private String pick(@Nullable String value) {
        return (value == null || value.isEmpty()) ? getString(R.string.value_empty) : value;
    }
}
