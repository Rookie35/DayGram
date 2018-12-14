package com.example.zkf.daygram;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditDiaryActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private String content;
    private String weekday;
    private String monthText;

    private TextView title;
    private TextView titleweek;
    private Button done_btn;
    private RelativeLayout clock_btn;
    private ImageButton home_btn;
    private EditText Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //接收数据
        Bundle bundle;
        bundle = this.getIntent().getExtras();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        day = bundle.getInt("day");
        content = bundle.getString("diaryContent");
        String tempMonth;
        String tempDay;

        //需要保持格式，方便使用SimpleDateFormat
        if ( month<10)
            tempMonth = "0" + Integer.toString(month);
        else
            tempMonth = Integer.toString(month);
        if (day<10)
            tempDay = "0" + Integer.toString(day);
        else
            tempDay = Integer.toString(day);
        weekday = Judgeweek(Integer.toString(year) + tempMonth + tempDay);
        //显示星期几
        titleweek = (TextView) findViewById(R.id.titleweek);
        titleweek.setText(weekday);
        if (weekday.equals("SUNDAY"))
            titleweek.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorRed));
        monthText = getMonthText(month);
        //显示月份和年份
        title = (TextView) findViewById(R.id.datetitle);
        title.setText(  "/"+monthText+"/"+ day+"/"+ year);
        //编写组件数据初始化
        Editor = (EditText) findViewById(R.id.diary_content_editor);
        Editor.setText(content);

        //时钟插入时间
        clock_btn = (RelativeLayout) findViewById(R.id.clock);
        clock_btn.setOnClickListener(new ClockBtnClick());
        //Log.d(TAG,"Done");
        //确定键绑定功能
        done_btn = (Button) findViewById(R.id.done_button);
        done_btn.setOnClickListener(new ReturnClick());
        //
        home_btn=(ImageButton)findViewById(R.id.home);
        home_btn.setOnClickListener(new ReturnClick());
    }

    class ClockBtnClick implements View.OnClickListener {
        @Override
            public void onClick(View v) {
            int index = Editor.getSelectionStart();//获取光标所在位置
            String noon;
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            int hour = Integer.parseInt(sdf.format(new java.util.Date()));
            sdf = new SimpleDateFormat("mm");
            String minute = sdf.format(new java.util.Date());
            if (hour >= 12) {
                noon = "下午 ";
                hour = hour - 12;
            } else {
                noon = "上午 ";
            }
            String timetext =noon+Integer.toString(hour) + ":" +minute ;
            Editable edit = Editor.getEditableText();//获取EditText的文字
            if (index < 0 || index >= edit.length()) {
                edit.append(timetext);
            } else {
                edit.insert(index, timetext);//光标所在位置插入文字
            }
        }
    }

    class ReturnClick implements View.OnClickListener{
        @Override
    public void onClick(View v) {
        content = Editor.getText().toString();
        if (content.equals(""))
            content = null;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);
        bundle.putString("diaryContent", content);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}

    @Override
    public void onBackPressed() {
        content = Editor.getText().toString();
        if (content.equals(""))
            content = null;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);
        bundle.putString("diaryContent", content);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 判断当前日期是星期几
     */

    private String Judgeweek(String time) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week = "SUNDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week = "MONDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week = "TUESDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week = "WEDNESDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week = "THURSDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week = "FRIDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week = "SATURDAY";
        }

        return Week;
    }

    private String getMonthText(int month) {
        switch (month) {
            case 1:return "JANUARY";case 2:return "FEBRUARY";case 3:return "MARCH";case 4:return "APRIL";case 5:return "MAY";case 6:return "JUNE";
            case 7:return "JULY";case 8:return "AUGUST";case 9:return "SEPTEMBER";case 10:return "OCTOBER";case 11:return "NOVEMBER";case 12:return "DECEMBER";
            default:return null;
        }
    }


    @Override
    protected void onRestart(){
        super.onRestart();
    }

    public static boolean isAppOnForeground(Context context){
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses == null)
            return false;
        for(ActivityManager.RunningAppProcessInfo appProcess:appProcesses){
            if(appProcess.processName.equals(packageName) &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }
}
