package com.example.jack.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.zxing.ScanListener;
import com.example.jack.myapplication.Util.zxing.ScanManager;
import com.example.jack.myapplication.Util.zxing.decode.DecodeThread;
import com.example.jack.myapplication.Util.zxing.decode.Utils;
import com.example.jack.myapplication.View.MyImageView;
import com.google.zxing.Result;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jack on 2016/8/12.
 */
public class ScanActivity extends Activity implements ScanListener, View.OnClickListener{
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
    ScanManager scanManager;
    TextView iv_light;
    TextView qrcode_g_gallery;
    TextView qrcode_ic_back;
    final int PHOTOREQUESTCODE = 1111;

    private int scanMode;//扫描模型（条形，二维码，全部）


    @BindView(R.id.authorize_return)
    ImageView authorize_return;

    @BindView(R.id.common_title_TV_center)
    TextView title;
    @BindView(R.id.tv_scan_result)
    TextView tv_scan_result;

    @BindView(R.id.scan_hint)
    TextView scan_hint;
    @BindView(R.id.scan_image)
    MyImageView scan_image;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        scanMode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }

    void initView() {
        switch (scanMode){
            case DecodeThread.ALL_MODE:
                title.setText(R.string.scan_allcode_title);
                scan_hint.setText(R.string.scan_allcode_hint);
                break;
        }
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcode_g_gallery = (TextView) findViewById(R.id.qrcode_g_gallery);
        qrcode_g_gallery.setOnClickListener(this);
        qrcode_ic_back = (TextView) findViewById(R.id.qrcode_ic_back);
        qrcode_ic_back.setOnClickListener(this);
        iv_light = (TextView) findViewById(R.id.iv_light);
        iv_light.setOnClickListener(this);

        authorize_return.setOnClickListener(this);
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode,this);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
        scan_image.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }


    public void scanResult(Result rawResult, Bundle bundle) {

//        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
//            scan_image.setVisibility(View.VISIBLE);
//            Bitmap barcode = null;
//            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
//            if (compressedBitmap != null) {
//                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
//                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
//            }
//            scan_image.setImageBitmap(barcode);
//        }
//        scan_image.setVisibility(View.VISIBLE);
        tv_scan_result.setVisibility(View.VISIBLE);
        //rawResult.getText()就是扫描结果，条形码就是一串数字
        tv_scan_result.setText("结果："+rawResult.getText());
        if(rawResult.getText().contains("cartNumber")){
            startActivity(new Intent(ScanActivity.this, ConnectActivity.class));
        }
        else {
            EventBus.getDefault().post(new InternetEvent(rawResult.getText(), Constant.REQUEST_INTERNET_BAR));
        }
        finish();
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if(e.getMessage()!=null&&e.getMessage().startsWith("相机")){
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 打开系统本有的相册，进行扫描识别
     * @param requestCode
     */
    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("ScanActivity","onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_g_gallery:
                //打开相册
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.iv_light:
                scanManager.switchLight();
                break;
            case R.id.qrcode_ic_back:
                finish();
                break;
            case R.id.authorize_return:
                finish();
                break;
            default:
                break;
        }
    }
}
