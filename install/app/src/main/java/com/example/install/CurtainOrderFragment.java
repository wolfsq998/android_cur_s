package com.example.install;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.pomelo.DataCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



/**
 * Created by Administrator on 2016/3/31.
 */
public class
        CurtainOrderFragment extends Fragment implements View.OnClickListener{

    private static final String ARG = "COrderFragment";

    private static final int SPAN = 3;

    private static final int CUS_COUNT_0 =0;
    private static final int CUS_COUNT_1 =1;
    private static final int CUS_COUNT_2 =2;

    private static final int ORDER_STATE_0= 10;
    private static final int ORDER_STATE_1= 11;
    private static final int ORDER_STATE_2= 12;

    public ArrayList<CurOrderCusBase>
            m_NewCusOrderList,
            m_OldCusOrderList,
            m_NowCusOrderList;
    public ArrayList<ScrollView> m_SvList;
    private ScrollView
            m_NewOrderSv,
            m_NowOrderSv,
            m_OldOrderSv;
    private LinearLayout
            m_NewOrderLl,
            m_NowOrderLl,
            m_OldOrderLl;
    private RelativeLayout m_WaitRl;
    private Button
            m_NewOrderRb,
            m_NowOrderRb,
            m_OldOrderRb;
    private int
            m_OrderCount=0,
            m_Begin=0,
            m_mid=0,
            m_End=10;
    private Drawable m_TelIcon ;
    private TextView m_WaitTv;

    private Handler CusOrderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ORDER_STATE_0) {
                if (m_NewCusOrderList.size() > 0){
                    Log.i("@@@：" + ARG, "当前收到的账单状态消息 →→" + -1);
                    AddOrder(m_NewOrderLl, m_NewCusOrderList);
                    m_NewOrderSv.setVisibility(View.VISIBLE);
                    m_WaitRl.setVisibility(View.GONE);
                }else {
                    ToolsManager.GetInstance().hideDialog();
                    ToolsManager.GetInstance().setMyToast(getActivity(), "未找到您的窗帘待受理的账单！", 1);
                    m_WaitTv.setText("未找到您的窗帘待受理的账单！");
                    m_WaitRl.setVisibility(View.VISIBLE);
                }
            }else if (msg.what == ORDER_STATE_1){
                if(m_NowCusOrderList.size()>0) {
                    Log.i("@@@：" + ARG, "当前收到的账单状态消息 →→" + 0);
                    AddOrder(m_NowOrderLl, m_NowCusOrderList);
                    m_NewOrderSv.setVisibility(View.VISIBLE);
                    m_WaitRl.setVisibility(View.GONE);
                }else {
                    ToolsManager.GetInstance().hideDialog();
                    ToolsManager.GetInstance().setMyToast(getActivity(), "未找到您的窗帘已受理的账单！", 1);
                    m_WaitTv.setText("未找到您的窗帘已受理的账单！");
                    m_WaitRl.setVisibility(View.VISIBLE);
                }
            }else if (msg.what == ORDER_STATE_2){
                Log.i("@@@：" + ARG, "当前收到的账单状态消息 →→" + 1);
                if(m_OldCusOrderList.size()>0) {
                    AddOrder(m_OldOrderLl, m_OldCusOrderList);
                    m_NewOrderSv.setVisibility(View.VISIBLE);
                    m_WaitRl.setVisibility(View.GONE);
                }else {
                    ToolsManager.GetInstance().hideDialog();
                    ToolsManager.GetInstance().setMyToast(getActivity(), "未找到您的窗帘已完成的账单！", 1);
                    m_WaitTv.setText("未找到您的窗帘已完成的账单！");
                    m_WaitRl.setVisibility(View.VISIBLE);
                }

            }
        }
    };

    private Handler CusCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(m_OrderCount>0){
                if (m_mid == 0) m_Begin = 0;
                if (m_mid != 0) m_Begin = m_End;
                if (m_OrderCount >=SPAN) m_End = SPAN;
                if (m_OrderCount <SPAN) m_End = m_OrderCount;
                if (msg.what ==CUS_COUNT_0){
                    MyCusOrder(-1);
                }else if (msg.what ==CUS_COUNT_1){
                    MyCusOrder(0);
                }else if (msg.what ==CUS_COUNT_2){
                    MyCusOrder(1);
                }
            }else{

            }
            if(m_OrderCount>0) {
                    Log.i("@@@：" + ARG, "已拿到数量" + m_OrderCount);
                AppDataManager.GetInstance().setOrderCount(m_OrderCount);

                    AddOrder(m_NewOrderLl,m_NewCusOrderList);
                }else{
                    ToolsManager.GetInstance().hideDialog();
                    ToolsManager.GetInstance().setMyToast(getActivity(),"你目前没有窗帘账单！",1);
                }

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.curtain_order_window, container, false);
        m_NewCusOrderList = new ArrayList<>();
        m_OldCusOrderList = new ArrayList<>();
        m_NowCusOrderList = new ArrayList<>();
        m_SvList = new ArrayList<>();
        init(rootView);
        MyCusCount(-1);
        return rootView;
    }
    private void init(View rootView){
        m_TelIcon =getResources().getDrawable(R.drawable.icon_tel1);
        m_WaitTv = (TextView)rootView.findViewById(R.id.curtain_wait_info);

        m_NewOrderSv=(ScrollView)rootView.findViewById(R.id.new_order_sv);
        m_NewOrderSv.setOnTouchListener(new TouchListenerImpl(m_NewOrderSv));
        m_NowOrderSv=(ScrollView)rootView.findViewById(R.id.now_order_sv);
        m_NowOrderSv.setOnTouchListener(new TouchListenerImpl(m_NowOrderSv));
        m_OldOrderSv=(ScrollView)rootView.findViewById(R.id.old_order_sv);
        m_OldOrderSv.setOnTouchListener(new TouchListenerImpl(m_OldOrderSv));

        m_SvList.add(m_NewOrderSv);
        m_SvList.add(m_NowOrderSv);
        m_SvList.add(m_OldOrderSv);

        m_NewOrderLl=(LinearLayout)rootView.findViewById(R.id.new_order_sv_ll);
        m_NowOrderLl=(LinearLayout)rootView.findViewById(R.id.now_order_sv_ll);
        m_OldOrderLl=(LinearLayout)rootView.findViewById(R.id.old_order_sv_ll);

        m_WaitRl = (RelativeLayout)rootView.findViewById(R.id.curtain_wait_rl);

        m_NewOrderRb=(Button)rootView.findViewById(R.id.order_new);
        m_NewOrderRb.setOnClickListener(this);
        m_NowOrderRb=(Button)rootView.findViewById(R.id.order_now);
        m_NowOrderRb.setOnClickListener(this);
        m_OldOrderRb=(Button)rootView.findViewById(R.id.order_old);
        m_OldOrderRb.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        m_Begin=0;
        m_mid=0;
        switch(v.getId()) {
            case R.id.order_new:
                m_NewOrderLl.removeAllViews();
                m_WaitRl.setVisibility(View.VISIBLE);
                m_NewOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab1));
                m_NowOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                m_OldOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                for (int i=0;i<m_SvList.size();i++)
                    if (i==0) m_SvList.get(i).setVisibility(View.VISIBLE);
                    else m_SvList.get(i).setVisibility(View.GONE);
                MyCusCount(-1);
                break;
            case R.id.order_now:
                m_NowOrderLl.removeAllViews();
                m_WaitRl.setVisibility(View.VISIBLE);
                m_NewOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                m_NowOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab1));
                m_OldOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                for (int i=0;i<m_SvList.size();i++)
                    if (i==1) m_SvList.get(i).setVisibility(View.VISIBLE);
                    else m_SvList.get(i).setVisibility(View.GONE);
                MyCusCount(0);
                break;
            case R.id.order_old:
                m_OldOrderLl.removeAllViews();
                m_WaitRl.setVisibility(View.VISIBLE);
                m_NewOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                m_NowOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab2));
                m_OldOrderRb.setBackgroundColor(getResources().getColor(R.color.colortab1));
                for (int i=0;i<m_SvList.size();i++)
                    if (i==2) m_SvList.get(i).setVisibility(View.VISIBLE);
                    else m_SvList.get(i).setVisibility(View.GONE);
                MyCusCount(1);
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 获取账单数量
     * @param orderState
     */
    private void MyCusCount(final int orderState){
        JSONObject msg = new JSONObject();
        try {
            msg.put("guid",AppDataManager.GetInstance().getGUID());
            msg.put("state",orderState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "获取账单数量申请数据" + msg);
        MyClient.GetInstance().getClient().request("connector.curtainsorderHandler.getcuscount", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：" + ARG, "获取账单数量" + msg);
                try {
                    if (msg.getInt("errcd") == -1) {
                        m_OrderCount = msg.getJSONObject("data").getInt("count");
                        Message message = new Message();
                        if (orderState == -1) {
                            message.what = CUS_COUNT_0;
                        } else if (orderState == 0) {
                            message.what = CUS_COUNT_1;
                        } else if (orderState == 1) {
                            message.what = CUS_COUNT_2;
                        }
                        CusCountHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    /**
     * 获取窗帘订单
     * @param orderState 订单状态
     */
    private void MyCusOrder(final int orderState){
        ToolsManager.GetInstance().showDialog(getActivity(), "正在查找账单，请稍后！");
        if (orderState == -1) {
            m_NewCusOrderList.clear();
        } else if (orderState == 0) {
            m_NowCusOrderList.clear();
        } else if (orderState == 1) {
            m_OldCusOrderList.clear();
        }
        JSONObject msg = new JSONObject();
        try {
            msg.put("guid",AppDataManager.GetInstance().getGUID());
            msg.put("state",orderState);
            msg.put("min",m_Begin);
            msg.put("max",m_End);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("@@@：" + ARG, "获取账单数量申请数据" + msg);
        MyClient.GetInstance().getClient().request("connector.curtainsorderHandler.getcusorder", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                Log.i("@@@：" + ARG, "获取账单数量" + msg);
                try {
                    JSONArray myData = msg.getJSONObject("data").getJSONArray("cusdata");
//                    Log.i("@@@：" + ARG, "获取账单数量" + myData.length());
                    if (msg.getInt("errcd") == -1 && myData != null) {
                        for (int i = 0; i < myData.length(); i++) {
                            CurOrderCusBase COCB = new CurOrderCusBase();
                            JSONObject ja = myData.getJSONObject(i);
                            COCB.Cusname = ja.getString("Cusname");
                            COCB.Custel = ja.getString("Custel");
                            COCB.Cusaddr = ja.getString("Cusaddr");
                            COCB.Visittime = ja.getDouble("Visittime");
                            COCB.Settime = ja.getDouble("Settime");
                            COCB.Workid = ja.getInt("Workerid");
                            COCB.Ordstate = ja.getInt("Ordstate");
                            COCB.Orderid = ja.getString("Ordid");
//                            Log.i("@@@：" + ARG, "当前ja账单" + ja);
                            if (orderState == -1) {
                                m_NewCusOrderList.add(COCB);
                                Log.i("@@@：" + ARG, "当前未受理账单数量 = " + m_NewCusOrderList.size());
                            } else if (orderState == 0) {
                                m_NowCusOrderList.add(COCB);
                                Log.i("@@@：" + ARG, "当前已受理账单数量 = " + m_NowCusOrderList.size());
                            } else if (orderState == 1) {
                                m_OldCusOrderList.add(COCB);
                                Log.i("@@@：" + ARG, "当前已完成账单数量 = " + m_OldCusOrderList.size());
                            }
                        }
                        Message message = new Message();
                        if (orderState == -1) {
                            Log.i("@@@：" + ARG, "当前账单状态 = " + orderState);
                            message.what = ORDER_STATE_0;
                        } else if (orderState == 0) {
                            Log.i("@@@：" + ARG, "当前账单状态 = " + orderState);
                            message.what = ORDER_STATE_1;
                        } else if (orderState == 1) {
                            Log.i("@@@：" + ARG, "当前账单状态 = " + orderState);
                            message.what = ORDER_STATE_2;
                        }
                        CusOrderHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //获取账单并分类显示
    private void AddOrder(LinearLayout ll,ArrayList<CurOrderCusBase> cob) {
//        Log.i("@@@：" + ARG, "当前收到的账单用户名为 →→" + cob.get(0).Cusname);
        ArrayList<WorkerInfoBase> WIB = AppDataManager.GetInstance().getWIB();
        if (cob!=null){
            m_WaitRl.setVisibility(View.GONE);
            for (int i=0;i<cob.size();i++){
                CusInfoView m_CDOP = new CusInfoView(getActivity());
                if (cob.get(i).Workid != -1){
                    LinearLayout m_WorkerInfoLl = (LinearLayout)m_CDOP.findViewById(R.id.worker_id_ll);
                    m_WorkerInfoLl.setVisibility(View.VISIBLE);
                    Button m_WorkerInfo = (Button)m_CDOP.findViewById(R.id.worker_info);
                    for(int j=0;j<WIB.size();j++){
                        if (cob.get(i).Workid == WIB.get(j).WorkId){
                            m_WorkerInfo.setText(WIB.get(j).Uname.toString() +":"+ WIB.get(j).Wname.toString());
                            m_CDOP.CallWorkerBut(WIB.get(j).Wname.toString());
                        }
                    }
                }
                Button m_CusNameTv = (Button)m_CDOP.findViewById(R.id.cus_name_tv);
                m_CusNameTv.setText(cob.get(i).Cusname);
                Button m_CustelTv = (Button)m_CDOP.findViewById(R.id.cus_tel_tv);
                m_CustelTv.setText(cob.get(i).Custel);
                Button m_CusaddrTv =(Button)m_CDOP.findViewById(R.id.cus_addr_tv);
                m_CusaddrTv.setText(cob.get(i).Cusaddr);
                Button m_VisittimeTv = (Button)m_CDOP.findViewById(R.id.visit_time_tv);
                m_VisittimeTv.setText(ToolsManager.GetInstance().getDate(cob.get(i).Visittime));
                m_CDOP.OrderInfoBut(cob.get(i).Orderid);
                ll.addView(m_CDOP);
                if(m_NewOrderLl.getChildCount()>0)
                    m_WaitRl.setVisibility(View.GONE);
            }
            ToolsManager.GetInstance().hideDialog();
        }else {
            ToolsManager.GetInstance().setMyToast(getActivity(),"暂无订单",1);
        }
    }

    //  ScrollView 滑动
    private class TouchListenerImpl implements View.OnTouchListener {
        private int intCount=0;
        private ScrollView ScrollView;
        public TouchListenerImpl (ScrollView sv){
            ScrollView = sv;
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int scrollY=view.getScrollY();
            final int height=view.getHeight();
            final int scrollViewMeasuredHeight=ScrollView.getChildAt(0).getMeasuredHeight();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if(scrollY==0){
                        intCount++;
                        if(intCount==1) {
                            Log.i("@@@：" + ARG, "滑动到了顶端 view.getScrollY()=" + scrollY);
                            OrderSpan(ScrollView);
                        }
                    }
                    intCount=0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if((scrollY+height)==scrollViewMeasuredHeight){
                        intCount++;
                        if(intCount==1){
//                            Log.i("@@@：" + ARG, "当前剩余账单数 orderCount=" + orderCount);
                            OrderSpan(ScrollView);
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }
    private void OrderSpan(ScrollView sv){
        LinearLayout ll;
        int state=-1;
        switch (sv.getId()){
            case R.id.new_order_sv:
                ll=m_NewOrderLl;
                state=-1;
                break;
            case R.id.now_order_sv:
                ll=m_NowOrderLl;
                state=0;
                break;
            case R.id.old_order_sv:
                ll=m_OldOrderLl;
                state=1;
                break;
            default:
                ll=m_NewOrderLl;
                break;
        }
        if (m_OrderCount >0) {
            m_mid=1;
            if (m_OrderCount-m_End==0){
                if(ll.getChildCount()>0)
                    ToolsManager.GetInstance().setMyToast(getActivity(), "窗帘待受理账单已全部加载完成！", Toast.LENGTH_SHORT);
                else {
                    ToolsManager.GetInstance().setMyToast(getActivity(), "未找到您的窗帘待受理的账单！", Toast.LENGTH_SHORT);
                    m_WaitTv.setText("未找到您的窗帘待受理的账单！");
                }
            }
            else {
                ToolsManager.GetInstance().showDialog(getActivity(),"正在查找账单，请稍后！");
                m_Begin=m_End;
                Log.i("@@@：" + ARG, "当前账单起始值 beginCount=" + m_Begin);
                if (m_OrderCount-m_End<=SPAN){
                    m_End=m_OrderCount;
                Log.i("@@@：" + ARG, "(orderCount-m_End<=SPAN) m_End=" + m_End);
                    MyCusOrder(state);
                }
                else if (m_OrderCount-m_End>SPAN){
                    m_End=m_End+SPAN;
                Log.i("@@@：" + ARG, "(orderCount-m_End>SPAN) m_End=" + m_End);
                    MyCusOrder(state);
                }
            }
        }else{
            ToolsManager.GetInstance().setMyToast(getActivity(), "您当前还没有账单！！", Toast.LENGTH_SHORT);
        }
    }
}
