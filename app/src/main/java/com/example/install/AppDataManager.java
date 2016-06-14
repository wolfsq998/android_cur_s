package com.example.install;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Point;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/18.
 */
public class AppDataManager {
    public static AppDataManager instance = null;

    private String
            user =null,
            appIp,
            GUID;

    private float
            totalPrice = 0;
    private int
            userid,
            fragment_num=0,
            appPort,
            order_count,
            errcd_info,
            sellType;
    private Point
            mPoint;
    private int[]
            buy_item_arr;
    private String[]
            workerinfo;

    private JSONObject msg = new JSONObject();

    private ArrayList<CurtainOrderBase> COB;
    private ArrayList<WorkerInfoBase> WIB;

    public static AppDataManager GetInstance(){
        if(instance == null)
        {
            instance = new AppDataManager();
        }
        return instance;
    }
//工人信息
    public void setWI(String [] s){workerinfo = s;}
    public String[] getWI(){return workerinfo;}

//工人数据
    public void setWIB(ArrayList<WorkerInfoBase> wib){WIB=wib;}
    public ArrayList<WorkerInfoBase> getWIB(){return WIB;}

    public void setCOB(ArrayList<CurtainOrderBase> cob){COB=cob;}
    public ArrayList<CurtainOrderBase> getCOB(){return COB;}
//用户唯一识别码
    public void setGUID(String s){GUID=s;}
    public String getGUID(){return GUID;}
//用户名
    public void setUser(String s){user=s;}
    public String getUser(){return user;}
//用户ID
    public void setUserid(int i){userid = i;}
    public int getUserid(){return userid;}
//账单信息
    public void setOrderBase(JSONObject jb){msg = jb;}
    public JSONObject getOrderBase(){return msg;}

//当前设备屏幕大小
    public void set_mPoint(Point p){mPoint=p;}
    public Point get_mPoint(){return mPoint;}
//当前服务器握手地址
    public void setAppIp(String ip){appIp=ip;}
    public String getAppIp(){return appIp;}
//当前服务器握手端口
    public void  setAppPort(int port){appPort=port;}
    public int getAppPort(){return appPort;}
//当前服务器返回的errcd代码信息
    public void  setErrcdInfo(int e){errcd_info=e;}
    public int getErrcdInfo(){return errcd_info;}

//出售物品类别
    public void setSellType(int i){sellType =i;}
    public int getSellType(){return sellType;}

// 出售商品每个品种的数量
    public void setBuy_item_arr(int[] i){buy_item_arr = i;}
    public int[] getBuy_item_arr(){return buy_item_arr;}
//出售物品总价
    public void setTotalPrice(float f){totalPrice = f;}
    public float getTotalPrice(){return totalPrice;}

//主页动态制定启动碎片
    public void setFragment_num(int i){fragment_num = i;}
    public int getFragment_num(){return fragment_num;}
//账单总量
    public void setOrderCount(int i){order_count =i;}
    public int getOrderCount(){return order_count;}



}

