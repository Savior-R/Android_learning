package cn.edu.sicnu.reproject.multi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 实验三 · 二级 Activity ③：采用<strong>隐式 Intent</strong>的方式启动系统相机拍照，拍照成功后显示照片。
 *
 * <p>实现要点（对应参考 PDF《采用隐式 Intent 调用相机拍照功能》）：</p>
 * <ol>
 *   <li>{@code AndroidManifest.xml} 中声明 CAMERA 权限，并在运行时动态申请；</li>
 *   <li>用 {@code new Intent(动作字符串)} 构造隐式 Intent，动作字符串来自字符串资源
 *       （{@code R.string.action_image_capture} = android.media.action.IMAGE_CAPTURE），
 *       不指定具体组件，由系统匹配出相机应用；</li>
 *   <li>在 {@link #onActivityResult(int, int, Intent)} 中从返回 Intent 的 extras 里
 *       取出缩略图 Bitmap 并显示到 ImageView。</li>
 * </ol>
 *
 * <p>「隐式」的关键在于：Intent 只给出 action，不写目标组件名。
 * 系统会在所有已安装、且声明了 {@code IMAGE_CAPTURE} 动作的应用中挑选一个来处理，
 * 因此换成别的相机 App 也能正常工作，这正是隐式 Intent 的价值。</p>
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
public class CameraActivity extends AppCompatActivity {

    /** 调用系统相机拍照的请求码 */
    private static final int REQUEST_CODE_CAMERA = 2001;

    /** 运行时权限请求码 */
    private static final int REQUEST_CODE_PERMISSION = 2002;

    private ImageView ivPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ivPhoto = findViewById(R.id.ivPhoto);
        findViewById(R.id.btnPhoto).setOnClickListener(v -> startCamera());
    }

    /**
     * 先检查 CAMERA 权限：未授予则动态申请，授予后再发隐式 Intent。
     */
    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
            return;
        }
        dispatchCameraIntent();
    }

    /**
     * 隐式调用系统相机：只给出动作，不指定具体组件，由系统匹配出相机应用。
     */
    private void dispatchCameraIntent() {
        String action = getString(R.string.action_image_capture);
        Intent cameraIntent = new Intent(action);

        // resolveActivity 为空说明系统里没有能处理该动作的应用
        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }

    /**
     * 权限申请回调：授予则继续拍照，拒绝则提示。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchCameraIntent();
            } else {
                Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 相机拍照成功后，系统会把缩略图 Bitmap 放在返回 Intent 的 extras 的 "data" 键中。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_CAMERA || resultCode != RESULT_OK || data == null) {
            return;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        Bitmap thumbnail = extras.getParcelable(getString(R.string.extra_key_thumbnail));
        if (thumbnail != null) {
            ivPhoto.setImageBitmap(thumbnail);
        } else {
            Toast.makeText(this, R.string.camera_no_photo, Toast.LENGTH_SHORT).show();
        }
    }
}
