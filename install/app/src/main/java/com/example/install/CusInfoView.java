package com.example.install;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.pomelo.DataCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import android.os.Handler;

/**
 * Created by Administrator on 2016/5/7.
 */
public class CusInfoView extends LinearLayout {

    private static final String ARG = "CusInfoView";
    private static final int SHOWCURINFO = 0;
    private Context m_Context;
    private int m_Errcd =0;
    private String[] m_CIP, m_CRO, m_CR1, m_CR2, m_CTW;
    private ArrayList<CurtainOrderBase> m_COB = new ArrayList<>();
    public CusInfoView(final Context context){
        super(context);
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.cus_info_view, this);
        m_Context=context;
        m_CIP = new String[]{"客厅", "主卧", "次卧", "儿卧", "阳台"};
        m_CRO = new String[]{"单开", "对开"};
        m_CR1 = new String[]{"自由孔", "韩折"};
        m_CR2 = new String[]{"不对花", "横对花", "竖对花"};
        m_CTW = new String[]{"侧装", "顶装"};

        CusTel();
    }
    private void CusTel(){
        Button m_TelBut = (Button)this.findViewById(R.id.cus_tel_but);
        final Button m_TelTv = (Button)this.findViewById(R.id.cus_tel_tv);
        m_TelBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog telDialog = new AlertDialog.Builder(m_Context).create();
                telDialog.setIcon(android.R.drawable.sym_def_app_icon);
                telDialog.setTitle("联系用户");
                telDialog.setMessage("用户联系电话：" + m_TelTv.getText());
                // 这里的对话框按钮的点击监听器与普通按钮点击监听器是不同的，要声明是DialogInterface这个类
                telDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                telDialog.setButton(DialogInterface.BUTTON_POSITIVE, "快速拨打",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intentTel2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + m_TelTv.getText()));
                                if (ActivityCompat.checkSelfPermission(m_Context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                m_Context.startActivity(intentTel2);
                                // TODO Auto-generated method stub
                                Toast.makeText(m_Context, "你点击了拨打按钮",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                telDialog.show();
            }
        });
    }

    public void OrderInfoBut(final String orderid){
        Button orderInfoBut = (Button)this.findViewById(R.id.order_info_but);
        orderInfoBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsManager.GetInstance().showDialog((Activity) m_Context, "正在查找账单，请稍后！");
                MyCurOrder(orderid);
            }
        });
    }
    //  获取账单
    private void MyCurOrder(String orderid){
        m_COB.clear();//清空当前获取账单，以防重复显示
        JSONObject msg = new JSONObject();
        try {
            msg.put("guid",AppDataManager.GetInstance().getGUID());
            msg.put("ordid",orderid);
            msg.put("min",0);
            msg.put("max",20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("@@@：", "发送状态=0的获取账单请求==" + msg);
        MyClient.GetInstance().getClient().request("connector.curtainsorderHandler.getorder", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：", "获取状态=0的账单数据==" + msg);
                m_COB.clear();//清空原有数据，防止账单重复加载
                try {
                    JSONArray ja = msg.getJSONObject("data").getJSONArray("curdata");
                    if (msg.getInt("errcd")==-1 && ja!=null){
                        m_Errcd = -1;
                        for (int i = 0 ;i<msg.length();i++) {
                            CurtainOrderBase curbase = new CurtainOrderBase();
                            JSONObject jo = ja.getJSONObject(i);
                            curbase.Inspos = jo.getInt("Inspos");
                            curbase.Rem0 = jo.getInt("Rem0");
                            curbase.Rem1 = jo.getInt("Rem1");
                            curbase.Rem2 = jo.getInt("Rem2");
                            curbase.Curw = jo.getInt("Curw");
                            curbase.Curh = jo.getInt("Curh");
                            curbase.Fab = jo.getString("Fab");
                            curbase.Curc = jo.getString("Curc");
                            curbase.Curp = jo.getString("Curp");
                            curbase.Curt = jo.getString("Curt");
                            curbase.Insw = jo.getInt("Insw");
                            curbase.Tral = jo.getInt("Tral");
                            curbase.Trac = jo.getInt("Trac");
                            m_COB.add(curbase);
                            Log.i("@@@：", "获取状态=0的账单数据==" + jo);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = SHOWCURINFO;
                showCurInfo.sendMessage(message);
            }
        });
    }
    private Handler showCurInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOWCURINFO){
                if (m_Errcd == -1) {
                    ShowDialog(m_COB);
                }else{
                    ToolsManager.GetInstance().hideDialog();
                }
            }
        }
    };
    private void ShowDialog(ArrayList<CurtainOrderBase> cob ){
        Log.i("@@@","账单信息界面"+ cob.size());
        LayoutInflater lf = LayoutInflater.from(m_Context);
        final View view1 = lf.inflate(R.layout.order_info_dialog_window, null);
        LinearLayout ll = (LinearLayout)view1.findViewById(R.id.order_info_dialog_ll);
        for (int i = 0 ;i<cob.size();i++) {
            CurInfoView COIP = new CurInfoView(m_Context);
            TextView CurInsPosTv = (TextView)COIP.findViewById(R.id.cur_ins_pos_tv);
            CurInsPosTv.setText(m_CIP[cob.get(i).Inspos]);
            TextView CurRemarkTv0 = (TextView)COIP.findViewById(R.id.cur_remark0_tv);
            CurRemarkTv0.setText(m_CRO[cob.get(i).Rem0]);
            TextView CurRemarkTv1 = (TextView)COIP.findViewById(R.id.cur_remark1_tv);
            CurRemarkTv1.setText(m_CR1[cob.get(i).Rem1]);
            TextView CurRemarkTv2 = (TextView)COIP.findViewById(R.id.cur_remark2_tv);
            CurRemarkTv2.setText(m_CR2[cob.get(i).Rem2]);
            TextView CurtainWidthTv = (TextView)COIP.findViewById(R.id.curtain_width_tv);
            CurtainWidthTv.setText(String.valueOf(cob.get(i).Curw));
            TextView CurtainHeightTv = (TextView)COIP.findViewById(R.id.curtain_height_tv);
            CurtainHeightTv.setText(String.valueOf(cob.get(i).Curh));
            TextView CurFabricNumTv = (TextView)COIP.findViewById(R.id.cur_fabric_num_tv);
            CurFabricNumTv.setText(cob.get(i).Fab);
            TextView CurCraftsTv = (TextView)COIP.findViewById(R.id.cur_crafts_tv);
            CurCraftsTv.setText(cob.get(i).Curc);
            TextView CurPutteeTv = (TextView)COIP.findViewById(R.id.cur_puttee_tv);
            CurPutteeTv.setText(cob.get(i).Curp);
            TextView CurTrackTv = (TextView)COIP.findViewById(R.id.cur_track_tv);
            CurTrackTv.setText(cob.get(i).Curt);
            TextView CurTrainsWayTv = (TextView)COIP.findViewById(R.id.cur_trains_way_tv);
            CurTrainsWayTv.setText(m_CTW[cob.get(i).Insw]);
            TextView CurTrackLengthTv = (TextView)COIP.findViewById(R.id.cur_track_length_tv);
            CurTrackLengthTv.setText(String.valueOf(cob.get(i).Tral));
            TextView CurTrackCountTv = (TextView)COIP.findViewById(R.id.cur_track_count_tv);
            CurTrackCountTv.setText(String.valueOf(cob.get(i).Trac));
            ll.addView(COIP);
        }
        AlertDialog.Builder aDialog = new AlertDialog.Builder(m_Context);
        aDialog.create();
        aDialog.setView(view1);
        aDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ToolsManager.GetInstance().hideDialog();
        aDialog.show();
    }


    public void CallWorkerBut(final String tel){
        final Button callWorkerBut = (Button)this.findViewById(R.id.call_worker_but);

        callWorkerBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog telDialog = new AlertDialog.Builder(m_Context).create();
                telDialog.setIcon(android.R.drawable.sym_def_app_icon);
                telDialog.setTitle("联系用户");
                telDialog.setMessage("联系安装工：" + tel);
                // 这里的对话框按钮的点击监听器与普通按钮点击监听器是不同的，要声明是DialogInterface这个类
                telDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                telDialog.setButton(DialogInterface.BUTTON_POSITIVE, "快速拨打",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intentTel2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + tel));
                                if (ActivityCompat.checkSelfPermission(m_Context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                m_Context.startActivity(intentTel2);
                                // TODO Auto-generated method stub
                                Toast.makeText(m_Context, "你点击了拨打按钮",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                telDialog.show();
            }
        });
    }
}
