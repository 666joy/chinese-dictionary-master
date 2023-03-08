package viewbasics;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.hui.dict.BaseActivity;
import com.hui.dict.R;
import com.hui.dict.BaseActivity;
import com.hui.dict.R;


/**
 * 测试Switch
 *
 * @author CoderCao
 */
public class SwitchActivity extends BaseActivity  {

    private final String TAG = "SwitchActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1、加载XML声明的布局
        setContentView(R.layout.activity_main);
        Switch sw = findViewById(R.id.ac_switch_test);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String state;
                if (isChecked){
                    state = "打开";
                }else {
                    state = "关闭";
                }
                Toast.makeText(SwitchActivity.this,
                        "开关"+state,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}