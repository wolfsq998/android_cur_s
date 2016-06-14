package com.example.install;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.pomelo.DataCallBack;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/22.
 */
public class MyInfoWindow extends AppCompatActivity implements View.OnClickListener{

    public static MyInfoWindow instance =null;
    private static final String ARG = "MyInfoWindow";
    private static final int OK = 0;

    private TextView m_LoginTv,m_UserIdTv;
    private ImageButton m_BreakBut;
    private Button m_OutLoginBut,m_LoginBut,m_GetSnBut,m_SnBut;
    private EditText m_GetSnEt,m_GetSnUserEt;
    private ImageButton m_CusHotLine1,m_CusHotLine2,m_CusHotLine3;
    private LinearLayout m_UserNameLl;

    private SharedPreferences m_Preferences;
    private SharedPreferences.Editor m_Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_window);
        instance=this;

        m_Preferences = getSharedPreferences("phone",MODE_PRIVATE);
        m_Editor = m_Preferences.edit();

        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String appVersion = info.versionName; // 版本名，versionCode同理
            TextView appVersionTv = (TextView)findViewById(R.id.about_us_app_version_tv);
            appVersionTv.setText("当前版本：V\r"+appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        init();
    }
    private void init(){
        m_UserNameLl = (LinearLayout)findViewById(R.id.user_name_ll);
        m_LoginTv = (TextView)findViewById(R.id.login_tv);
        m_UserIdTv = (TextView)findViewById(R.id.user_id_tv);
        m_GetSnEt = (EditText)findViewById(R.id.get_sn_et);
        m_GetSnUserEt = (EditText)findViewById(R.id.get_sn_user_et);
        m_LoginBut = (Button)findViewById(R.id.login_but);

        int errcd = AppDataManager.GetInstance().getErrcdInfo();
        String user = AppDataManager.GetInstance().getUser();
        if (errcd == -1){
            m_LoginBut.setVisibility(View.GONE);
            m_UserNameLl.setVisibility(View.VISIBLE);
            m_LoginTv.setText(user);
            m_UserIdTv.setText(String.valueOf(AppDataManager.GetInstance().getUserid()));
        }else{
            m_LoginBut.setVisibility(View.VISIBLE);
            m_UserNameLl.setVisibility(View.GONE);
            m_LoginBut.setOnClickListener(this);
        }

        m_CusHotLine1 = (ImageButton)findViewById(R.id.cus_hotline_1);
        m_CusHotLine1.setOnClickListener(this);
        m_CusHotLine2 = (ImageButton)findViewById(R.id.cus_hotline_2);
        m_CusHotLine2.setOnClickListener(this);
        m_CusHotLine3 = (ImageButton)findViewById(R.id.cus_hotline_3);
        m_CusHotLine3.setOnClickListener(this);

        m_GetSnBut = (Button)findViewById(R.id.get_sn_but);
        m_GetSnBut.setOnClickListener(this);
        m_SnBut = (Button)findViewById(R.id.sn_but);
        m_SnBut.setOnClickListener(this);

        m_OutLoginBut = (Button)findViewById(R.id.out_login_but);
        m_OutLoginBut.setOnClickListener(this);

        m_BreakBut = (ImageButton)findViewById(R.id.break_img_but);
        m_BreakBut.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cus_hotline_1:
                Intent hotline_1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + getResources().getString(R.string.hotline1)));
                if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                instance.startActivity(hotline_1);
                break;
            case R.id.cus_hotline_2:
                Intent hotline_2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + getResources().getString(R.string.hotline2)));
                if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                instance.startActivity(hotline_2);
                break;
            case R.id.cus_hotline_3:
                Intent hotline_3 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + getResources().getString(R.string.hotline3)));
                if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                instance.startActivity(hotline_3);
                break;
            case R.id.sn_but://激活码
                GetSn();
                break;
            case R.id.get_sn_but:
                Intent intentTel2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + "18591955708"));
                if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                instance.startActivity(intentTel2);
                break;
            case R.id.break_img_but:
                finish();
                break;
            case R.id.login_but:
                Log.i("@@@@@@","########");
                Intent toLoginWindow = new Intent(MyInfoWindow.this,LoginWindow.class);
                toLoginWindow.putExtra("inlet", 0);
                startActivity(toLoginWindow);
                break;
            case R.id.out_login_but:
                AppDataManager.GetInstance().setErrcdInfo(-100);
                m_Editor.putString("tel", "0");
//                m_Editor.putString("pwd", "0");
                m_Editor.commit();
                m_LoginBut.setVisibility(View.VISIBLE);
                m_UserNameLl.setVisibility(View.GONE);
                m_LoginTv.setText("");
                Intent toLoginWin = new Intent(MyInfoWindow.this,LoginWindow.class);
                toLoginWin.putExtra("inlet", 0);
                startActivity(toLoginWin);
                break;
        }
    }

    private void GetSn(){
        String test_ip = getResources().getString(R.string.app_ip);
        int test_port = Integer.parseInt(getResources().getString(R.string.app_port));
        MyClient.GetInstance().setServerAddre(test_ip, test_port);
        JSONObject msg = new JSONObject();
        try {
            msg.put("version", getResources().getString(R.string.app_version_code));
            msg.put("username",m_GetSnUserEt.getText().toString());
            msg.put("sn", m_GetSnEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "发送获取激活码请求→" + msg);
        MyClient.GetInstance().getClient().request("connector.userHandler.activeuser", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：" + ARG, "收到激活码信息→" + msg);
                try {
                    if (msg.getInt("errcd")==-1){
                        Message message = new Message();
                        message.what = OK;
                        getSnHandler.sendMessage(message);
                        Log.i("@@@：" + ARG, "发送激活成功消息→");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private Handler getSnHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == OK){
                Log.i("@@@：" + ARG, "接受到激活成功消息→" );
                ToolsManager.GetInstance().setMyToast(instance,"激活成功！\n谢谢使用《沃纳助手·窗帘·商户版》！",2);
                Intent toLogin = new Intent(instance,LoginWindow.class);
                startActivity(toLogin);
            }
        }
    };
}
