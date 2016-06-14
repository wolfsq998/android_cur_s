package com.example.install;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.pomelo.DataCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/1.
 */
public class RegWindow extends AppCompatActivity implements View.OnClickListener {

    private static final String ARG = "RegWindow";
    public static RegWindow instance =null;

    private static final int USERREG =0;
    private int m_ErrcdInfo=0;
    private int m_Inlet=-1;
    private int m_RegType;
    private ArrayList<Drawable> m_Img = new ArrayList<>();
    private EditText m_PhoneEt,m_PawEt1,m_PawEt2,m_RecNumEt;
    private Button m_RegBut,m_RetBut,m_RedButXY;
    private TextView m_LabelTv;
    public CheckBox m_CheckBoxXY;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == USERREG) {
                switch (m_ErrcdInfo) {
                    case -1:
                        ToolsManager.GetInstance().setMyToast(RegWindow.this, "注册成功，谢谢使用《沃纳助手·窗帘·用户版》！", 1);
                        finish();
                        break;
                    case 1:
                        ToolsManager.GetInstance().setMyToast(RegWindow.this, "此账号已注册，请直接登陆！", 1);
                        break;
                    case 3:
                        ToolsManager.GetInstance().setMyToast(RegWindow.this, "验证码错误！", 1);
                        break;
                    default:
                        ToolsManager.GetInstance().setMyToast(RegWindow.this, "注册失败，请稍后重试！", 1);
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_win);
        instance=this;

        String test_ip = getResources().getString(R.string.app_ip);
        int test_port = Integer.parseInt(getResources().getString(R.string.app_port));
        MyClient.GetInstance().setServerAddre(test_ip, test_port);
        Intent getInlet = getIntent();
        m_Inlet=getInlet.getIntExtra("inlet",-1);

        Intent getRegType = getIntent();
        m_RegType=getRegType.getIntExtra("regType", -1);

        init();

        if (m_RegType == 0){
            m_LabelTv.setText("注册");
//            m_PasMsgTv.setText("设置登录密码");
            m_RegBut.setText("立即注册");
        }
        if (m_RegType == 1){
            m_CheckBoxXY.setVisibility(View.GONE);
            m_LabelTv.setText("重置密码");
//            m_PasMsgTv.setText("设置新的登录密码");
            m_RegBut.setText("立即重置");
        }
    }
    private void init(){
        m_Img.add(getResources().getDrawable(R.drawable.icon_checkmark1));
        m_Img.add(getResources().getDrawable(R.drawable.icon_checkmark3));
        for (int i = 0; i < m_Img.size(); i++) {
            m_Img.get(i).setBounds(1, 1, m_Img.get(i).getIntrinsicWidth() / 3 * 2, m_Img.get(i).getIntrinsicHeight() / 3 * 2);
        }

        m_RegBut = (Button)findViewById(R.id.reg_win_reg_but);//立即注册
        m_RegBut.setOnClickListener(this);
        m_RetBut = (Button)findViewById(R.id.reg_win_return_but);//返回
        m_RetBut.setOnClickListener(this);

        m_LabelTv = (TextView)findViewById(R.id.reg_win_label_tv);//注册界面标题
        m_PhoneEt = (EditText)findViewById(R.id.reg_win_phone_et);//注册账号
        m_PawEt1 = (EditText)findViewById(R.id.reg_win_paw_et1);//注册密码1
        m_PawEt2 = (EditText)findViewById(R.id.reg_win_paw_et2);//注册密码2
        m_RecNumEt = (EditText)findViewById(R.id.rec_num_et);//推荐码
        m_RedButXY = (Button)findViewById(R.id.red_xieyi_but); //阅读协议
        m_RedButXY.setOnClickListener(this);
        m_CheckBoxXY = (CheckBox)findViewById(R.id.checkBox_xieyi);//协议选择
        m_CheckBoxXY.setCompoundDrawables(m_Img.get(0),null,null,null);
        m_CheckBoxXY.setOnClickListener(this);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ev) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            finish();
        }
        return false;
    }
    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkBox_xieyi:
                if(m_CheckBoxXY.isChecked()){
                    m_CheckBoxXY.setCompoundDrawables(m_Img.get(0),null,null,null);
                }else{
                    m_CheckBoxXY.setCompoundDrawables(m_Img.get(1),null,null,null);
                }
                break;
            case R.id.red_xieyi_but:
                Intent toRedAgr = new Intent(RegWindow.this,Agreement.class);
                startActivity(toRedAgr);
                break;
            case R.id.reg_win_reg_but:
                Log.i("@@@" + ARG, "点击了注册！");
                if (m_PhoneEt.getText().length()==11){
                    if(m_PawEt1.getText().length()>5){
                        if(m_PawEt1.getText().toString().equals(m_PawEt2.getText().toString())){
                            if(m_CheckBoxXY.isChecked()){
//                                ToolsManager.GetInstance().setMyToast(RegWindow.this, "您已认可《沃纳助手使用协议》！", 1);
                                MyReg();
                            }else{
                                ToolsManager.GetInstance().setMyToast(RegWindow.this, "您没有认可《沃纳助手使用协议》！", 1);
                            }
                        }else{
                            ToolsManager.GetInstance().setMyToast(RegWindow.this, "两次密码不同，请重新输入！",1);
                        }
                    }else{
                        ToolsManager.GetInstance().setMyToast(RegWindow.this, "密码长度有误！",1);
                    }

                }else{
                    ToolsManager.GetInstance().setMyToast(RegWindow.this, "您输入的注册电话有误！",1);
                }

                break;
            case R.id.reg_win_return_but:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 注册
     */
    private void MyReg(){
        Log.i("@@@" + ARG, "注册！");
        final String desKey = "12345678";
        String pwd = "";
        try {
            pwd = Des.encryptDES(m_PawEt1.getText().toString(),desKey);
            Log.i("@@@：" + ARG, "加密后的密码→" + pwd);
            String pwd1 = Des.decryptDES(pwd,desKey);
            Log.i("@@@：" + ARG, "解密后的密码→" + pwd1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("@@@：" + ARG, "解密后的密码→" + e);
        }
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        JSONObject msg = new JSONObject();//一种数据传输方式
        try {
            msg.put("username",m_PhoneEt.getText().toString());
            msg.put("pas",pwd);
            msg.put("recnum",m_RecNumEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "注册信息→" + msg);
        MyClient.GetInstance().getClient().request("connector.userHandler.reguser",msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：" + ARG, "注册返回值→" + msg);
                try {
                    m_ErrcdInfo = msg.getInt("errcd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//        m_ErrcdInfo = -1;
                Message message = new Message();
                message.what = USERREG;
                handler.sendMessage(message);
            }
        });
    }
}