package com.example.helloworldmultilang;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * 实验一：多语言版本交互式 Hello World
 * <p>
 * 实验要求 (3)：不采用布局文件，界面部分全部用纯代码（pure code）实现。
 * 因此本项目没有 activity_main.xml，所有控件均在 {@link #onCreate} 中动态创建。
 * <p>
 * 实验要求 (2)：支持三种语言版本的交互式 Hello World —— 简体中文、英文、意大利语，
 * 点击下方对应国旗按钮即可实时切换界面语言。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
public class MainActivity extends AppCompatActivity {

    /** 第三种语言：意大利语 */
    private static final Locale LOCALE_ITALIANO = new Locale("it");

    private LinearLayout rootLayout;
    private TextView helloTextView;
    private TextView languageTextView;
    private TextView hintTextView;
    private ImageView chinaFlagView;
    private ImageView englishFlagView;
    private ImageView italianFlagView;

    /** 当前语言，null 表示跟随系统默认（本项目默认为中文） */
    private Locale currentLocale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("locale", "default");
            currentLocale = "it".equals(tag) ? LOCALE_ITALIANO : ("en".equals(tag) ? Locale.ENGLISH : null);
            applyLocale(currentLocale);
        }
        buildUiByCode();
        refreshTexts();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("locale", currentLocale == null ? "default" : currentLocale.getLanguage());
    }

    // ==================================================================
    // 一、纯代码构建界面
    // ==================================================================

    private void buildUiByCode() {
        // 根布局：垂直排列的 LinearLayout（替代 XML 布局文件）
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setBackgroundColor(0xFFF5F7FA);
        int padding = dp(24);
        rootLayout.setPadding(padding, padding, padding, padding);

        // 1) 多语言 "Hello World" 主标题
        helloTextView = new TextView(this);
        helloTextView.setTextSize(34f);
        helloTextView.setTextColor(0xFF1A1A1A);
        helloTextView.setGravity(Gravity.CENTER);
        rootLayout.addView(helloTextView);

        // 2) 当前语言名称
        languageTextView = new TextView(this);
        languageTextView.setTextSize(16f);
        languageTextView.setTextColor(0xFF3F51B5);
        languageTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams langParams =
                new LinearLayout.LayoutParams(dp(240), LinearLayout.LayoutParams.WRAP_CONTENT);
        langParams.topMargin = dp(12);
        languageTextView.setLayoutParams(langParams);
        rootLayout.addView(languageTextView);

        // 3) 操作提示
        hintTextView = new TextView(this);
        hintTextView.setTextSize(14f);
        hintTextView.setTextColor(0xFF888888);
        hintTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hintParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        hintParams.topMargin = dp(8);
        hintParams.bottomMargin = dp(36);
        hintTextView.setLayoutParams(hintParams);
        rootLayout.addView(hintTextView);

        // 4) 三个国旗按钮（水平排列），点击切换语言
        LinearLayout flagBar = new LinearLayout(this);
        flagBar.setOrientation(LinearLayout.HORIZONTAL);
        flagBar.setGravity(Gravity.CENTER);
        rootLayout.addView(flagBar);

        chinaFlagView = createFlagButton(flagBar, R.drawable.flag_china, "中文", () -> switchLanguage(null));
        englishFlagView = createFlagButton(flagBar, R.drawable.flag_usa, "English", () -> switchLanguage(Locale.ENGLISH));
        italianFlagView = createFlagButton(flagBar, R.drawable.flag_italy, "Italiano", () -> switchLanguage(LOCALE_ITALIANO));

        setContentView(rootLayout);
    }

    /**
     * 创建一个国旗按钮：一张圆角图片 + 语言名，点击后执行 action。
     */
    private ImageView createFlagButton(LinearLayout parent, int resId, String label, final Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);

        ImageView flag = new ImageView(this);
        flag.setImageResource(resId);
        flag.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dp(84), dp(56));
        imgParams.leftMargin = dp(10);
        imgParams.rightMargin = dp(10);
        flag.setLayoutParams(imgParams);
        flag.setBackgroundResource(R.drawable.bg_flag);
        flag.setContentDescription(label);

        TextView caption = new TextView(this);
        caption.setText(label);
        caption.setTextSize(13f);
        caption.setTextColor(0xFF444444);
        caption.setGravity(Gravity.CENTER);

        item.addView(flag);
        item.addView(caption);

        // 整个"图片 + 文字"区域可点击，便于 adb shell 模拟触摸
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });

        parent.addView(item);
        return flag;
    }

    // ==================================================================
    // 二、语言切换逻辑
    // ==================================================================

    private void switchLanguage(Locale locale) {
        currentLocale = locale;
        applyLocale(locale);
        refreshTexts();
    }

    /**
     * 通过修改 Resources 的 Configuration 实现运行时切换语言，无需重启 Activity。
     */
    @SuppressWarnings("deprecation")
    private void applyLocale(Locale locale) {
        Configuration config = new Configuration(getResources().getConfiguration());
        // locale 为 null 时恢复为系统默认语言
        Locale target = (locale == null) ? Locale.getDefault() : locale;
        config.setLocale(target);
        config.setLayoutDirection(target);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * 按当前语言刷新界面文案。
     */
    private void refreshTexts() {
        helloTextView.setText(R.string.hello_world);
        hintTextView.setText(R.string.hint_switch);
        languageTextView.setText(getString(R.string.language_name));

        // 高亮当前选中的国旗
        chinaFlagView.setAlpha(currentLocale == null ? 1.0f : 0.35f);
        englishFlagView.setAlpha(isLanguage(Locale.ENGLISH) ? 1.0f : 0.35f);
        italianFlagView.setAlpha(isLanguage(LOCALE_ITALIANO) ? 1.0f : 0.35f);

        // 应用标题也随语言变化（例如 "You Rui 2024110544"）
        setTitle(R.string.app_name);
    }

    private boolean isLanguage(Locale locale) {
        return currentLocale != null && currentLocale.getLanguage().equals(locale.getLanguage());
    }

    /** dp 转 px 的小工具 */
    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}
