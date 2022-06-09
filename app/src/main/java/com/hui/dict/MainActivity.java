package com.hui.dict;
import static android.hardware.Sensor.TYPE_LIGHT;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.gson.Gson;
import com.hui.dict.bean.TuWenBean;
import com.hui.dict.utils.FileUtil;
import com.hui.dict.utils.LightSensor;
import com.hui.dict.utils.PatternUtils;
import com.hui.dict.utils.RecognizeService;

import java.util.ArrayList;
import java.util.List;

import viewbasics.SwitchActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView pyTv,bsTv,cyuTv,twenTv,juziTv;
    EditText ziEt;
    private boolean hasGotToken = false;
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;
    private AlertDialog.Builder alertDialog;

    private static final String TAG = "MainActivity";

    // 亮度相关的
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxValue;

    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        alertDialog = new AlertDialog.Builder(this);
        initAccessTokenWithAkSk();


        // 开关相关的
        setContentView(R.layout.activity_main);
        Switch sw = findViewById(R.id.ac_switch_test);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String state;
                if (isChecked){
                    state = "开启";
                    // 开启监听亮度
                   open();
                }else {
                    state = "关闭";
                    close();
                }
                Toast.makeText(MainActivity.this,
                        "自动调节屏幕亮度"+state,
                        Toast.LENGTH_SHORT).show();
            }
        });




    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }
            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "MSaY1m8CryxI44ILaMu3e76H", "iqZmCIIWOwTKCrsQP8h7ps54yOS4KSXc");
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            //result是识别出的字符串，可以将字符串传递给下一个界面
                            TuWenBean wenBean = new Gson().fromJson(result, TuWenBean.class);
                            List<TuWenBean.WordsResultBean> wordsList = wenBean.getWords_result();
                            //将提取到的有用的汉字存放到集合当中，传递到下一个界面
                            ArrayList<String>list = new ArrayList<>();
                            if (wordsList!=null&&wordsList.size()!=0) {
                                for (int i = 0; i < wordsList.size(); i++) {
                                    TuWenBean.WordsResultBean bean = wordsList.get(i);
                                    String words = bean.getWords();
                                    String res = PatternUtils.removeAll(words);
                                    //将字符串当中每一个字符串都添加到集合当中
                                    for (int j = 0; j < res.length(); j++) {
                                        String s = String.valueOf(res.charAt(j));
//                                        添加集合之前，先判断一下，集合是否包括这个汉字
                                        if (!list.contains(s)) {
                                            list.add(s);
                                        }
                                    }
                                }
//                                判断是否有可识别的文字
                                if (list.size()==0) {
                                    Toast.makeText(MainActivity.this,"无法识别图片中的文字！",Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent it = new Intent(MainActivity.this, IdentifyImgActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("wordlist",list);
                                    it.putExtras(bundle);
                                    startActivity(it);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
    }
    private void initView() {
        pyTv = findViewById(R.id.main_tv_pinyin);
        bsTv = findViewById(R.id.main_tv_bushou);
        cyuTv = findViewById(R.id.main_tv_chengyu);
        twenTv = findViewById(R.id.main_tv_tuwen);
        juziTv = findViewById(R.id.main_tv_juzi);
        ziEt = findViewById(R.id.main_et);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.main_iv_setting:
                intent.setClass(this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.main_iv_search:
                String text = ziEt.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    intent.setClass(this,WordInfoActivity.class);
                    intent.putExtra("zi",text);
                    startActivity(intent);
                }
                break;
            case R.id.main_tv_pinyin:
                intent.setClass(this,SearchPinyinActivity.class);
                startActivity(intent);
                break;
            case R.id.main_tv_bushou:
                intent.setClass(this,SearchBuShouActivity.class);
                startActivity(intent);
                break;
            case R.id.main_tv_chengyu:
                intent.setClass(this,SearchChengyuActivity.class);
                startActivity(intent);
                break;
            case R.id.main_tv_tuwen:
                if (!checkTokenStatus()) {
                    return;
                }



                intent.setClass(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
                break;
        }
    }


    // 亮度相关的函数
    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        float currentValue = event.values[0];

        // Log.d(TAG,  String.valueOf(currentValue));

        // 修改app亮度

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = 1-200/currentValue;

        if( lp.screenBrightness<0){
            lp.screenBrightness=0.1f;
        }
       // Log.d(TAG,  String.valueOf(lp.screenBrightness));
        window.setAttributes(lp);


       // mTvLight.setText( Float.toString( currentValue ) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 亮度相关的
    public void open(){
        // 亮度相关的
        //获取传感器管理对象
        SensorManager mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // 获取传感器的类型,光线传感器
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        // 注册监听器
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void close(){
        //获取传感器管理对象
        SensorManager mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // 获取传感器的类型,光线传感器
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        // 取消监听
        mSensorManager.unregisterListener(this);
        // 改为系统亮度
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(lp);

    }

}
