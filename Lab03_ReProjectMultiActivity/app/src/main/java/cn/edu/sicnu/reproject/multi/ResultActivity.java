package cn.edu.sicnu.reproject.multi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 实验三 · 二级 Activity ②：输入内容后把结果回传给 MainActivity。
 *
 * <p>结果回传的三步骤：</p>
 * <ol>
 *   <li>MainActivity 用 {@code startActivityForResult(intent, requestCode)} 启动本 Activity；</li>
 *   <li>本 Activity 构造结果 Intent，{@code setResult(RESULT_OK, result)} 携带数据，
 *       然后 {@code finish()} 结束自己；</li>
 *   <li>MainActivity 的 {@code onActivityResult(requestCode, resultCode, data)} 被回调，
 *       从 data 中取出数据并显示。</li>
 * </ol>
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
public class ResultActivity extends AppCompatActivity {

    private EditText etInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        etInput = findViewById(R.id.etInput);
        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (text.isEmpty()) {
                // 未输入内容时用默认文案回传，保证回传链路依然可演示
                text = getString(R.string.result_default_text);
            }

            // 构造结果 Intent，携带 key = extra_result
            Intent result = new Intent();
            result.putExtra(getString(R.string.extra_key_result), text);

            // RESULT_OK 表示成功，随后 finish() 让 MainActivity 的 onActivityResult 收到结果
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
