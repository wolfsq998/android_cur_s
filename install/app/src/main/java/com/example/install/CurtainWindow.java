package com.example.install;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.os.Handler;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import com.netease.pomelo.DataCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/5/2.
 */
public class CurtainWindow extends AppCompatActivity implements View.OnClickListener {

    public static CurtainWindow instance = null;
    private static final String ARG = "CurtainWindow";
    private static final int GETWORKER = 1;
    private static final int LOGIN = 2;
    private static final int SCROLL = 3;
    private static final int UPLOADORDER_OK = 11;
    private static final int UPLOADORDER_NO = 12;



    private String[] m_MyWorker; //= new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16", "17"};
    private String[] m_CIP, m_CRO, m_CR1, m_CR2, m_CTW;

    private Calendar m_Calendar;
    private Button m_AddrBut, m_InputBut, m_SelOrderBut, m_DatePickerBut, m_DesignateBut, m_WorkIdPickerBut;
    private LinearLayout m_OrderWinLl, m_AddrLl, m_WorkIdLl, m_MasterSvLl;
    private RelativeLayout m_HeadImg;
    private EditText m_CusNameEt, m_CusTelEt, m_CusAddrEt, m_VisitTYEt, m_VisitTMEt, m_VisitTDEt, m_WorkIdEt;
    private ImageButton m_MyInfoBut;
    private ScrollView m_OrderInfoSv;

    private ArrayList<CurtainOrderBase> m_COB = new ArrayList<>();
    private ArrayList<JSONObject> m_COB1 = new ArrayList<>();
    private ArrayList<WorkerInfoBase> m_WIB = new ArrayList<>();

    private ArrayAdapter<String> m_AdapterCIP;
    private ArrayAdapter<String> m_AdapterCRO;
    private ArrayAdapter<String> m_AdapterCR1;
    private ArrayAdapter<String> m_AdapterCR2;
    private ArrayAdapter<String> m_AdapterCTW;

    private int  m_WorkId,m_ErrcdInfo,m_VerErrNum=13,m_UserId;
    private String m_GUID;
    private Point point;

    private SharedPreferences m_Preferences;
    private SharedPreferences.Editor m_Editor;

    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SCROLL){
                m_OrderInfoSv.fullScroll(ScrollView.FOCUS_DOWN);//SCROLL
            }
        }
    };
    /**
     * 自动登陆
     */
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOGIN) {
                AlertDialog telDialog = new AlertDialog.Builder(CurtainWindow.this).create();
                switch (m_ErrcdInfo){
                    case -1:
                        AppDataManager.GetInstance().setErrcdInfo(m_ErrcdInfo);
                        AppDataManager.GetInstance().setGUID(m_GUID);
                        AppDataManager.GetInstance().setUserid(m_UserId);
                        getWorker();
                        break;
                    case 101:
                        ToolsManager.GetInstance().setMyToast(CurtainWindow.this, "您输入的用户名或密码错误！", 1);
                        break;
                    case 108:
                        telDialog.setTitle("发现新版本！");
                        telDialog.setMessage("发现新版本，是否升级！");
                        telDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "再说",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        MyClient.GetInstance().getClient().disconnect();
                                        System.exit(0);
                                    }
                                });
                        telDialog.setButton(DialogInterface.BUTTON_POSITIVE, "好的",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse("http://www.mamakeji.net/mamainstall.apk"));
//                                    startActivity(intent);
                                    }
                                });
                        telDialog.show();
                        break;
                    case 111:
                        m_Editor.putString("tel", "0");
                        m_Editor.putString("pwd", "0");
                        m_Editor.commit();
                        AppDataManager.GetInstance().setErrcdInfo(-100);
                        telDialog.setTitle("信息提示！");
                        telDialog.setMessage("此账号试用期已过，如需继续使用请在个人信息界面中进行激活操作！！");
                        telDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "再说",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        MyClient.GetInstance().getClient().disconnect();
                                        System.exit(0);
                                    }
                                });
                        telDialog.setButton(DialogInterface.BUTTON_POSITIVE, "好的",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent toMyInfoWindow = new Intent(CurtainWindow.this, MyInfoWindow.class);
                                        startActivity(toMyInfoWindow);
                                    }
                                });
                        telDialog.show();
                        break;

                }
            }
        }
    };

    /**
     *提交订单
     */
    private Handler uploadorder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPLOADORDER_OK) {
                ToolsManager.GetInstance().setMyToast(CurtainWindow.this,"落单成功！",2);
                m_CusNameEt.setText("");//客户名称
                m_CusAddrEt.setText("");//客户地址
                m_CusTelEt.setText("");//客户电话
                m_WorkIdEt.setText("");//指派工人
                setTime();
                m_AddrLl.removeAllViews();
                AddCur(1);
//                Intent upData = new Intent(CurtainWindow.this,CurtainWindow.class);
//                startActivity(upData);
//                finish();

            }else if (msg.what==UPLOADORDER_NO){
                ToolsManager.GetInstance().setMyToast(CurtainWindow.this,"落单失败，请稍后重试！",2);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curtain_window);
        instance = this;

        m_Calendar = Calendar.getInstance();

        String test_ip = getResources().getString(R.string.app_ip);
        int test_port = Integer.parseInt(getResources().getString(R.string.app_port));
        MyClient.GetInstance().setServerAddre(test_ip, test_port);

        point = new Point();
        Display di = this.getWindowManager().getDefaultDisplay();
        di.getSize(point);
        AppDataManager.GetInstance().set_mPoint(point);

        //创建电话存储目录
        m_Preferences = getSharedPreferences("phone", MODE_PRIVATE);
        m_Editor = m_Preferences.edit();

        String tel = m_Preferences.getString("tel", null);
        String pwd = m_Preferences.getString("pwd", null);
        if (tel == null || pwd == null) {
            m_Editor.putString("tel", "0");
            m_Editor.putString("pwd", "0");
            m_Editor.commit();
        } else {
            AppDataManager.GetInstance().setUser(tel);
        }
        init();
        setTime();
        MyLogin();
        if (AppDataManager.GetInstance().getErrcdInfo() ==-1){
            getWorker();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (AppDataManager.GetInstance().getErrcdInfo() ==-1){
            getWorker();
        }
    }

    private void init() {
        m_OrderInfoSv = (ScrollView)findViewById(R.id.order_info_sv);
        m_OrderInfoSv.scrollTo(0, 0);
        m_OrderWinLl = (LinearLayout) findViewById(R.id.order_win_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(point.x, LinearLayout.LayoutParams.MATCH_PARENT);
        m_OrderWinLl.setLayoutParams(params);

        m_AddrBut = (Button) findViewById(R.id.add_but);
        m_AddrBut.setOnClickListener(this);
        m_InputBut = (Button) findViewById(R.id.input_but);
        m_InputBut.setOnClickListener(this);
        m_SelOrderBut = (Button) findViewById(R.id.select_order_but);
        m_SelOrderBut.setOnClickListener(this);
        m_DatePickerBut = (Button) findViewById(R.id.date_picker_but);
        m_DatePickerBut.setOnClickListener(this);
        m_DesignateBut = (Button) findViewById(R.id.designate_but);
        m_DesignateBut.setOnClickListener(this);
        m_WorkIdPickerBut = (Button) findViewById(R.id.work_id_picker_but);
        m_WorkIdPickerBut.setOnClickListener(this);
        m_MyInfoBut = (ImageButton) findViewById(R.id.my_info_but);
        m_MyInfoBut.setOnClickListener(this);


        m_WorkIdLl = (LinearLayout) findViewById(R.id.work_id_ll);

        m_AddrLl = (LinearLayout) findViewById(R.id.test_addr_ll);
        m_HeadImg = (RelativeLayout) findViewById(R.id.curtain_win_head_image);

        m_CusNameEt = (EditText) findViewById(R.id.cus_name_et);
        m_CusTelEt = (EditText) findViewById(R.id.cus_tel_et);
        m_CusAddrEt = (EditText) findViewById(R.id.cus_addr_et);

        m_WorkIdEt = (EditText) findViewById(R.id.work_id_et);

        m_CIP = new String[]{"客厅", "主卧", "次卧", "儿卧", "阳台"};
        ArrayList<String> CIPList = new ArrayList<>();
        for (int i = 0; i < m_CIP.length; i++) {
            CIPList.add(m_CIP[i]);
        }
        m_AdapterCIP = new ArrayAdapter<>(this, R.layout.simple_spinner_item, CIPList);

        m_CRO = new String[]{"单开", "对开"};
        ArrayList<String> CRList0 = new ArrayList<>();
        for (int i = 0; i < m_CRO.length; i++) {
            CRList0.add(m_CRO[i]);
        }
        m_AdapterCRO = new ArrayAdapter<>(this, R.layout.simple_spinner_item, CRList0);

        m_CR1 = new String[]{"自由孔", "韩折"};
        ArrayList<String> CRList1 = new ArrayList<>();
        for (int i = 0; i < m_CR1.length; i++) {
            CRList1.add(m_CR1[i]);
        }
        m_AdapterCR1 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, CRList1);

        m_CR2 = new String[]{"不对花", "横对花", "竖对花"};
        ArrayList<String> CRList2 = new ArrayList<>();
        for (int i = 0; i < m_CR2.length; i++) {
            CRList2.add(m_CR2[i]);
        }
        m_AdapterCR2 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, CRList2);
        m_CTW = new String[]{"侧装", "顶装"};
        ArrayList<String> CTWList = new ArrayList<>();
        for (int i = 0; i < m_CTW.length; i++) {
            CTWList.add(m_CTW[i]);
        }
        m_AdapterCTW = new ArrayAdapter<>(this, R.layout.simple_spinner_item, CTWList);
        AddCur(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_order_but://跳转订单界面
                if (AppDataManager.GetInstance().getErrcdInfo() == -1) {
                    Intent intent = new Intent(CurtainWindow.this, CurtainDownOrderWin.class);
                    startActivity(intent);
                }else{
                    Intent toLoginWin = new Intent(CurtainWindow.this, LoginWindow.class);
                    toLoginWin.putExtra("inlet", 1);
                    startActivity(toLoginWin);
                }
                break;
            case R.id.date_picker_but://安装上门时间
                final MyDatePicker MDP = new MyDatePicker(this);
                AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                aDialog.create();
                aDialog.setView(MDP);
                aDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tv = (TextView) MDP.findViewById(R.id.my_date_tv);
                        m_DatePickerBut.setText(tv.getText().toString());
                    }
                });
                aDialog.show();
                break;
            case R.id.add_but://添加新窗帘
                AddCur(0);
                break;
            case R.id.input_but://下单按钮
                m_InputBut.setClickable(false);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        m_InputBut.setClickable(true);
                    }
                },3000);
                setCurInfo();
                break;
            case R.id.work_id_picker_but://指定工人选择
                AlertDialog.Builder workIdDialog = new AlertDialog.Builder(this);
                workIdDialog.create();
                workIdDialog.setItems(m_MyWorker, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToolsManager.GetInstance().setMyToast(CurtainWindow.this, "当前点击是：" + m_MyWorker[which], 2);
                        m_WorkIdEt.setText(m_MyWorker[which]);
                        m_WorkId = m_WIB.get(which).WorkId;
                    }
                });
                workIdDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                workIdDialog.show();
                break;
            case R.id.my_info_but://跳转进入用户信息界面
                Intent toMyInfoWindow = new Intent(CurtainWindow.this, MyInfoWindow.class);
                startActivity(toMyInfoWindow);
                break;
            default:
                break;
        }
    }

    /**
     * 提交订单
     * @param date 用户填选的上门时间(字符串)
     * @param nowtime 落单时间(字符串)
     */
    private void UpBase(String date, String nowtime) {
        JSONObject msg = new JSONObject();
        JSONArray msgar = new JSONArray();
        for (int j=0;j<m_COB1.size();j++){
            msgar.put(m_COB1.get(j));

        }
        AppDataManager.GetInstance().setCOB(m_COB);
        try {
            msg.put("guid", AppDataManager.GetInstance().getGUID());//唯一识别码
            msg.put("cusname", m_CusNameEt.getText().toString());//客户名称
            msg.put("cusaddr", m_CusAddrEt.getText().toString());//客户地址
            msg.put("custel", m_CusTelEt.getText().toString());//客户电话
            msg.put("visittime", ToolsManager.GetInstance().getTime(date));//上门时间
            msg.put("settime", ToolsManager.GetInstance().getTime(nowtime));//落单时间
            if(m_WorkIdEt.getText().length()>0)
                msg.put("workerid", m_WorkId);//工人ID
            else
                msg.put("workerid", -1);//工人ID
            msg.put("orderlist", msgar);
        } catch (JSONException e) {
            Log.i("@@@" + ARG, "报错→" + e);
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "提交账单数据" + msg);
        MyClient.GetInstance().getClient().request("connector.curtainsorderHandler.upload", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
//                Log.i("@@@：" + ARG, "提交账单返回数据数据" + msg);
                try {
                    Log.i("@@@：" + ARG, "提交订单 Errcd→→ " +  msg.getInt("errcd"));
                    if (msg.getInt("errcd")==-1){
                        m_InputBut.setClickable(true);
                        Message message = new Message();
                        message.what = UPLOADORDER_OK;
                        uploadorder.sendMessage(message);
                    }else{
                        m_InputBut.setClickable(true);
                        Message message = new Message();
                        message.what = UPLOADORDER_NO;
                        uploadorder.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 创建窗帘信息界面
     * @param i 用于判断是系统添加，还是用户添加。
     *          同时控制删除按钮是否显示
     */
    private void AddCur(int i) {
        final CurView COP = new CurView(this);
        COP.CurInsPos(m_AdapterCIP);
        COP.CurRemark0(m_AdapterCRO);
        COP.CurRemark1(m_AdapterCR1);
        COP.CurRemark2(m_AdapterCR2);
        COP.CurTrainsWay(m_AdapterCTW);
        Button delBut = (Button) COP.findViewById(R.id.del_but);
        if (i == 1) {
            delBut.setVisibility(View.GONE);
            m_AddrLl.addView(COP);
        } else if (i == 0) {
            delBut.setVisibility(View.VISIBLE);
            delBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("@@@ ####", ">>>" + v.getTag());
                    m_AddrLl.removeView(COP);
                }
            });
            m_AddrLl.addView(COP);
            Log.i("@@@ ####", ">>>");
            Message message = new Message();
            message.what = SCROLL;
            handler2.sendMessage(message);

        }
    }
    /**
     * 登陆
     */
    private void MyLogin() {
        String tel = m_Preferences.getString("tel", null);
        String pwd2 = m_Preferences.getString("pwd", null);
        if (tel.length()==11 && pwd2.length()>5 && pwd2.length()<13){
//            Log.i("@@@" + ARG, "登陆！");
            final String desKey = "12345678";
            String pwd = "";
            try {
                pwd = Des.encryptDES(pwd2, desKey);
//                Log.i("@@@：" + ARG, "加密后的密码→" + pwd);
                String pwd1 = Des.decryptDES(pwd, desKey);
//                Log.i("@@@：" + ARG, "解密后的密码→" + pwd1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("@@@：" + ARG, "解密后的密码→" + e);
            }
            JSONObject msg = new JSONObject();//一种数据传输方式
            try {
                msg.put("username", tel);
                msg.put("pas", pwd);
                msg.put("version",Integer.parseInt(getResources().getString(R.string.app_version_code)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("@@@：" + ARG, "发送登陆信息→" + msg);
//            m_ErrcdInfo = -1;
            MyClient.GetInstance().getClient().request("connector.userHandler.login", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject msg) {
                    Log.i("@@@：" + ARG, "收到登陆信息→" + msg);
                    try {
                        m_GUID = msg.getJSONObject("data").getString("guid");
                        m_UserId = msg.getJSONObject("data").getInt("Id");
                        m_ErrcdInfo = msg.getInt("errcd");
                    } catch (JSONException e) {
//                    Log.i("@@@：" + ARG, "收到登陆信息 里的GUID 错误→" + e.toString());
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = LOGIN;
                    handler1.sendMessage(message);
                }
            });
        }
    }
    /**
     * 设置默认上门时间
     */
    private void setTime(){
        String myHour=null;
        if (m_Calendar.get(Calendar.HOUR_OF_DAY)>7 && m_Calendar.get(Calendar.HOUR_OF_DAY)<18){
            myHour = String.valueOf(m_Calendar.get(Calendar.HOUR_OF_DAY));
        }else if (m_Calendar.get(Calendar.HOUR_OF_DAY)<8){
            myHour = String.valueOf(8);
        }else if(m_Calendar.get(Calendar.HOUR_OF_DAY)>17){
            myHour = String.valueOf(17);
        }
        String mydate = m_Calendar.get(Calendar.YEAR) + "年" + (m_Calendar.get(Calendar.MONTH) + 1) + "月" + (m_Calendar.get(Calendar.DAY_OF_MONTH)+1) + "日 " + myHour + ":00";
        m_DatePickerBut.setText(mydate);
    }
    /**
     * 设置窗帘信息并提交
     */
    private void setCurInfo(){
        if (AppDataManager.GetInstance().getErrcdInfo() == -1) {
            int childCount = m_AddrLl.getChildCount();
            m_COB.clear();
            m_COB1.clear();
            Log.i("@@@ ", "当前View数量 = " + childCount);
            for (int i = 0; i < childCount; i++) {
                CurtainOrderBase COB = new CurtainOrderBase();
                JSONObject ordInfo = new JSONObject();
                View view = m_AddrLl.getChildAt(i);
                Spinner spinnerCIP = (Spinner) view.findViewById(R.id.cur_Ins_pos_sp);//安装位置
                Spinner spinnerCR0 = (Spinner) view.findViewById(R.id.cur_remark0_sp);//窗帘展开方式：单开 or 对开
                Spinner spinnerCR1 = (Spinner) view.findViewById(R.id.cur_remark1_sp);//窗帘悬挂孔：自由孔 or 韩折
                Spinner spinnerCR2 = (Spinner) view.findViewById(R.id.cur_remark2_sp);//窗帘对花方式：横队 or 竖对
                EditText curWidthEt = (EditText) view.findViewById(R.id.curtain_width_et);//窗帘宽度
                EditText curHeightEt = (EditText) view.findViewById(R.id.curtain_height_et);//窗帘高度
                EditText curFabricNumEt = (EditText) view.findViewById(R.id.cur_fabric_num_et);//窗帘布料货号
                EditText curCraftsEt = (EditText) view.findViewById(R.id.cur_crafts_et);//窗帘工艺样式
                EditText curPutteeEt = (EditText) view.findViewById(R.id.cur_puttee_et);//窗帘绑带
                EditText curTrackEt = (EditText) view.findViewById(R.id.cur_track_et);//窗帘轨道
                Spinner spinnerCTW = (Spinner) view.findViewById(R.id.cur_trains_way_sp);//窗帘轨道安装方式 侧装 or 顶装
                EditText curTrackLenEt = (EditText) view.findViewById(R.id.cur_track_length_et);//轨道长度
                EditText curTrackCountEt = (EditText) view.findViewById(R.id.cur_track_count_et);//轨道根数

                try {
                    ordInfo.put("inspos",spinnerCIP.getSelectedItemId());
                    ordInfo.put("rem0",spinnerCR0.getSelectedItemId());//getSelectedItem().toString()
                    ordInfo.put("rem1",spinnerCR1.getSelectedItemId());
                    ordInfo.put("rem2",spinnerCR2.getSelectedItemId());
                    ordInfo.put("curw",Integer.parseInt(curWidthEt.getText().toString()));
                    ordInfo.put("curh",Integer.parseInt(curHeightEt.getText().toString()));
                    ordInfo.put("fab",curFabricNumEt.getText().toString());
                    ordInfo.put("curc",curCraftsEt.getText().toString());
                    ordInfo.put("curp",curPutteeEt.getText().toString());
                    ordInfo.put("curt",curTrackEt.getText().toString());
                    ordInfo.put("insw",spinnerCTW.getSelectedItemId());
                    ordInfo.put("tral",Integer.parseInt(curTrackLenEt.getText().toString()));
                    ordInfo.put("trac",Integer.parseInt(curTrackCountEt.getText().toString()));
                    m_COB1.add(ordInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //判断指定数据是否正确，决定是否提交订单
            String nowTime = m_Calendar.get(Calendar.YEAR) + "年" + (m_Calendar.get(Calendar.MONTH) + 1) + "月" + m_Calendar.get(Calendar.DAY_OF_MONTH) + "日 " + m_Calendar.get(Calendar.HOUR) + ":00";
            if (!(m_DatePickerBut.getText().equals("当前选择的时间已过时"))) {
                if (m_CusNameEt.getText().length() > 0 && m_CusAddrEt.getText().length() > 0 && m_CusTelEt.getText().length() == 11) {
                    AppDataManager.GetInstance().getCOB();
                    UpBase(m_DatePickerBut.getText().toString(), nowTime);
                } else {
                    ToolsManager.GetInstance().setMyToast(CurtainWindow.this, "窗帘信息不完整，提交失败！", 2);
                }
            } else {
                ToolsManager.GetInstance().setMyToast(CurtainWindow.this, "你填写的日期已过时！", 2);
            }
        } else { //没有登陆先跳去登陆
            Intent toLoginWin = new Intent(CurtainWindow.this, LoginWindow.class);
            toLoginWin.putExtra("inlet", 1);
            startActivity(toLoginWin);
        }
    }
    /**
     * 获取工人信息
     */
    private void getWorker(){
        m_WIB.clear();
        JSONObject msg = new JSONObject();
        try {
            msg.put("guid", AppDataManager.GetInstance().getGUID());//唯一识别码
        } catch (JSONException e) {
            Log.i("@@@" + ARG, "报错→" + e);
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "提交获取工人数据" + msg);
        MyClient.GetInstance().getClient().request("connector.userHandler.getworker", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：" + ARG, "@@@@获取到的工人数据" + msg);
                try {
                    JSONArray myData = msg.getJSONObject("data").getJSONArray("mydata");
//                    Log.i("@@@：" + ARG, "@@@@获取@@@@@到的工人数据!!!!!!" + myData);
                    if (msg.getInt("errcd")==-1){
//                        Log.i("@@@：" + ARG, "@@@@获取到的工人数据!!!!!!" + msg.getInt("errcd"));
                        for (int i=0;i<myData.length();i++){
                            WorkerInfoBase WIB = new WorkerInfoBase();
                            JSONObject ja = myData.getJSONObject(i);
                            WIB.WorkId = ja.getInt("Id");//工人ID
                            WIB.Wname = ja.getString("Wname");
                            WIB.Uname = ja.getString("Uname");
//                            Log.i("@@@：" + ARG, "获取到的工人数据####" + ja);
                            m_WIB.add(WIB);
                        }
                        Message message = new Message();
                        message.what = GETWORKER;
                        GetWorkerHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    Log.i("@@@：" + ARG, "错误信息" + e);
                    e.printStackTrace();
                }
            }
        });
    }
    private Handler GetWorkerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GETWORKER) {
                AppDataManager.GetInstance().setWIB(m_WIB);
                m_MyWorker = new String[m_WIB.size()];
                for (int i=0;i<m_WIB.size();i++){
                    m_MyWorker[i] = m_WIB.get(i).Uname.toString() +"\r\r\r\r"+ m_WIB.get(i).Wname.toString();
                }
            }
        }
    };
}