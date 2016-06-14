package com.example.install;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/11.
 */
public class MyDatePicker extends LinearLayout {

    private static final int UPDATE_MONTH = 0;
    private Calendar mCalendar;
    private int m_PickMonth,m_PickYear;
    private NumberPicker yearNP,monthNP,dayNP,hourNP;
    private String[] mYearArray = new String[10];
    private String[] mMonthArray = new String[12];
    private String[] mDayArray = new String[31];
    private String[] mHourArray = new String[10];
    private TextView mMyDate;
    private ArrayList<NumberPicker> npList = new ArrayList<>();

    public MyDatePicker(final Context context){
        super(context);
        ((Activity)getContext()).getLayoutInflater().inflate(R.layout.my_date_picker, this);
        mCalendar = Calendar.getInstance();
        DisplayDate(mCalendar.get(Calendar.YEAR), (mCalendar.get(Calendar.MONTH) + 1),
                mCalendar.get(Calendar.DAY_OF_MONTH)+1, mCalendar.get(Calendar.HOUR_OF_DAY));
        MyDateTv();
    }
    private void init(){
        for (int i=0;i<10;i++) mYearArray[i] = String.valueOf(mCalendar.get(Calendar.YEAR)+i);
        for (int i=0;i<12;i++) mMonthArray[i]=String.valueOf(i+1);
        for (int i=0;i<31;i++) mDayArray[i]=String.valueOf(i+1);
        for (int i=0;i<10;i++) mHourArray[i]=String.valueOf(8+i);
        yearNP = (NumberPicker)findViewById(R.id.numberPicker_year);
        monthNP = (NumberPicker)findViewById(R.id.numberPicker_month);
        dayNP = (NumberPicker)findViewById(R.id.numberPicker_day);
        hourNP = (NumberPicker)findViewById(R.id.numberPicker_hour);
        npList.add(yearNP);
        npList.add(monthNP);
        npList.add(dayNP);
        npList.add(hourNP);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            if (msg.what == UPDATE_MONTH) {
                dayNP.setValue(0);
                Log.i("@@@ timeNp", "" + yearNP.getValue());
                setDayCount();
                MyDateTv();
            }
        }
    };
    private void setDayCount(){
        if (m_PickMonth == 2) { // 二月
            if (m_PickYear % 4 == 0) {
                dayNP.setMaxValue(mDayArray.length - 3);
            } else {
                dayNP.setMaxValue(mDayArray.length - 4);
            }
        } else if (m_PickMonth == 7) { //七月
            dayNP.setMaxValue(mDayArray.length-1);
        } else if (m_PickMonth < 7) { //上半年
            if (m_PickMonth % 2 == 0) {//小月
                dayNP.setMaxValue(mDayArray.length - 2);
            } else if (m_PickMonth % 2 == 1) {//大月
                dayNP.setMaxValue(mDayArray.length-1);
            }
        } else if(m_PickMonth > 7){  // 下半年
            if (m_PickMonth % 2 == 0) {//大月
                dayNP.setMaxValue(mDayArray.length-1);
            } else if (m_PickMonth % 2 == 1) {//小月
                dayNP.setMaxValue(mDayArray.length - 2);
            }
        }
    }
    private void setOVCL(){
        for (int i= 0; i<npList.size();i++){
            if (i!=2){
                npList.get(i).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        m_PickYear=Integer.parseInt(mYearArray[yearNP.getValue()]);
                        m_PickMonth=Integer.parseInt( mMonthArray[monthNP.getValue()]);
                        Message message  = new Message();
                        message.what = UPDATE_MONTH;
                        handler.sendMessage(message);
                    }
                });
            }else{
                npList.get(i).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        MyDateTv();
                    }
                });
            }
        }
    }
    private void setNP(){
        for (int i=0;i<npList.size();i++){
            ToolsManager.GetInstance().setNumberPickerTextColor(npList.get(i), getResources().getColor(R.color.colorBlack));
            ToolsManager.GetInstance().setNumberPickerDividerColor(npList.get(i), getResources().getColor(R.color.colorPrimaryDark));
            npList.get(i).setMinValue(0);
            npList.get(i).setWrapSelectorWheel(false);//在现实条目大于3事是否循环
            npList.get(i).setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
    }
    private void setNPStartValue(int year,int month, final int day,int hour){
        yearNP.setMaxValue(mYearArray.length - 1);
        for (int i=0;i<mYearArray.length;i++){
            if (mYearArray[i].equals(String.valueOf(year))) {
                yearNP.setValue(i);
                break;
            }
        }
        yearNP.setDisplayedValues(mYearArray);

        monthNP.setMaxValue(mMonthArray.length - 1);
        for (int i=0;i<mMonthArray.length;i++){
            if (mMonthArray[i].equals(String.valueOf(month))) {
                monthNP.setValue(i);
                break;
            }
        }
        monthNP.setDisplayedValues(mMonthArray);

        dayNP.setMaxValue(mDayArray.length - 1);
        for (int i=0;i<mDayArray.length;i++){
            if (mDayArray[i].equals(String.valueOf(day))){
                dayNP.setValue(i);
                break;
            }
        }
        dayNP.setDisplayedValues(mDayArray);

        hourNP.setMaxValue(mHourArray.length - 1);
        for (int i=0;i<mHourArray.length;i++){
            if (mHourArray[i].equals(String.valueOf(hour))){
                hourNP.setValue(i);
                break;
            }
        }
        hourNP.setDisplayedValues(mHourArray);
    }
    private void MyDateTv(){
        mMyDate = (TextView)findViewById(R.id.my_date_tv);

        String nowTime =mCalendar.get(Calendar.YEAR)+"年"+ (mCalendar.get(Calendar.MONTH)+1)+"月"+ mCalendar.get(Calendar.DAY_OF_MONTH)+"日 "+ mCalendar.get(Calendar.HOUR_OF_DAY)+":00";

        String inputTime =mYearArray[yearNP.getValue()] + "年" + mMonthArray[monthNP.getValue()] + "月" + (dayNP.getValue()+1) + "日 " + mHourArray[hourNP.getValue()]+":00";

        double nowT = ToolsManager.GetInstance().getTime(nowTime);
        double inputT = ToolsManager.GetInstance().getTime(inputTime);
        Log.i("@@@", "当前系统时间 = " + nowT + " == " + mCalendar.get(Calendar.YEAR) + "年" + (mCalendar.get(Calendar.MONTH) + 1) + "月" + mCalendar.get(Calendar.DAY_OF_MONTH) + "日 " + mCalendar.get(Calendar.HOUR_OF_DAY) + ":00");
        Log.i("@@@", "当前选择时间 = " + inputT + " == " + mYearArray[yearNP.getValue()] + "年" + mMonthArray[monthNP.getValue()] + "月" + (dayNP.getValue() + 1) + "日 " + mHourArray[hourNP.getValue()] + ":00");
        if (inputT<nowT){
            mMyDate.setText("当前选择的时间已过时");
        }else{
            String dateTv = mYearArray[yearNP.getValue()] + "年" + mMonthArray[monthNP.getValue()] + "月" + (dayNP.getValue()+1) + "日 " + mHourArray[hourNP.getValue()]+":00";
            mMyDate.setText(dateTv);
        }


    }
    private void DisplayDate(int year,int month, final int day,int hour){
        init();
        setOVCL();
        setNP();
        setNPStartValue(year, month, day, hour);
    }

}
