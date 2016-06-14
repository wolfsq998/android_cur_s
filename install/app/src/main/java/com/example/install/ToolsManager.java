package com.example.install;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/3/17.
 */
public class ToolsManager {
    public static ToolsManager m_Tool = null;
    private static Toast mToast;
    private ProgressDialog dialog;
    public static ToolsManager GetInstance(){
        if(m_Tool == null)
        {
            m_Tool = new ToolsManager();
        }
        return m_Tool;
    }

    /**
     * 快速替换消息提示Toast
     * @param activity 需要将toast显示的界面
     * @param msg  需要显示的内容
     * @param duration 显示持续时间
     */
    public void setMyToast(Context activity, String msg, int duration){
        if (mToast == null) {
            mToast =  Toast.makeText(activity, msg,duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * 将array.xml中存储的对象的ID写入int数组
     * @param Array
     * @return
     */
    public int[] getReadArrayId(TypedArray Array){
        int len = Array.length();
        final int[] arrayId = new int[len];
        for (int i=0;i<len;i++){
            arrayId[i]=Array.getResourceId(i,0);
        }
        Array.recycle();
        return arrayId;
    }

    /**
     * 启动等待提示框
     * @param activity
     * @param message
     */
    public void showDialog(Activity activity,String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(activity);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    /**
     * 关闭已经打开的等待提示框
     */
    public void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    /**
     * 日期转化为毫秒
     * @param time
     * @return
     */
    public Double getTime(String time){
        double serTime=0d;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        try {
            Date t = dateFormat.parse(time);
            serTime = t.getTime();
            System.out.println(t.getTime());
//            Log.i("@@@：", "时间转换毫秒→" + t.getTime());
            String beginDate = String.valueOf(t.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String sd = sdf.format(new Date(Long.parseLong(beginDate)));
//            Log.i("@@@：", t.getTime() + "毫秒转换时间→" + sd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return serTime;
    }

    /**
     * 毫秒转换时间
     * @param date
     * @return
     */
    public String getDate(Double date){
        String str1=null;
        long lo = (new Double(date)).longValue();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String str = String.valueOf(lo);
        str1=sdf.format(new Date(Long.parseLong(str)));
        return str1;
    }

    /**
     * 使用WIFI时，获取本机IP地址
     * @param mContext
     * @return
     */
    public static String getWIFILocalIpAdress(Context mContext) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }
    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF ) + "." +
                ((ipAdress >> 8 ) & 0xFF) + "." +
                ((ipAdress >> 16 ) & 0xFF) + "." +
                ( ipAdress >> 24 & 0xFF) ;
    }

    /**
     * 使用GPRS时，获取本机IP地址
     * @return
     */
    public static String getGPRSLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 通用获取IP
     */
    public String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取网关IP地址
     * @return
     */
    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 设置NumberPicker分割线的颜色
     * @param numberPicker
     * @param color
     */
    public void setNumberPickerDividerColor(NumberPicker numberPicker,int color) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(color));
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 设置NumberPicker字色
     * @param numberPicker
     * @param color
     * @return
     */
    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                Field selectorWheelPaintField;
                try {
                    selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    try {
                        ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();  return true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取时间格式
     * @return
     */
    public String getTimeFormat(Context context){
        Calendar m_Calendar = Calendar.getInstance();//日历
        int apm = m_Calendar.get(Calendar.AM_PM);//判断早上还是下午
        ContentResolver cv = context.getContentResolver();
        // 获取当前系统设置
        String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
        if(strTimeFormat.equals("24"))
        {
            Log.i("activity","当前是24制式");
        }

        if(strTimeFormat.equals("12"))
        {
            Log.i("activity","当前是12制式");
        }
        return strTimeFormat;
    }
}
