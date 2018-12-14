package com.example.zkf.daygram;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//处理储存Daycontent数据的listview，实现listview根据需求显示不同的view
public class ListViewAdapter extends BaseAdapter{
    private  String TAG = "ListViewAdapter";
    //itemA类的type标志
    private static final int TYPE_A = 0;
    //itemB类的type标志
    private static final int TYPE_B = 1;
    private static final int TYPE_C = 2;
    private Context context;
    private String week;
    private boolean status=false;
    //整合数据
    private List<Diary> contents = new ArrayList<>();
    //传入context及Daycontent的数组（当前日记数组的数据)
     public ListViewAdapter(Context context, ArrayList<Diary> Listview) {
        this.context = context;

        //把数据装载同一个list里面
        //这里把所有数据都转为object类型是为了装载同一个list里面好进行排序
        this.contents = Listview;
    }
    //数据更新
    public void changedata(Diary content, int i)
    {
        //if(content.getContent()!=null)
        //{
            this.contents.set(i,content);
        //}
    }
    //选择了新bar的数据刷新
    public void refreshdata(ArrayList<Diary> Listview)
    {
        this.contents = Listview;
        //Log.d(TAG,"refresh");
    }
    //切换显示状态
    public void changestatus()
    {
        this.status=!this.status;
    }
    public boolean getstatus()
    {
        return this.status;
    }
   //每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int result = 0;
        //获取当前选中的view的数据,如果daycontent的content有东西，属于TYPE_A
        if (contents.get(position).getContent() != null) {
            result = TYPE_A;
  //          Log.d(TAG,"content="+contents.get(position).getContent());
        } else {
            result = TYPE_B;
        }
        //Log.d(TAG,"status:"+status);
        if(status)
            result=TYPE_C;
        Log.d(TAG,"result:"+result);
        return result;
    }

    /**
     * 获得有多少中view type
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//设置不同样式的view
        //创建两种不同种类的viewHolder变量
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        ViewHolder3 holder3 = null;
        //根据position获得View的type
        int type = getItemViewType(position);
        if (convertView == null) {
            //绑定组件
            //根据不同的type 来inflate不同的item layout
            //然后设置不同的tag
            //这里的tag设置是用的资源ID作为Key
            //判断属于点还是有数据
            switch (type) {
                case TYPE_A:
                    holder1 = new ViewHolder1();
                    convertView = View.inflate(context, R.layout.listview_item, null);
                    holder1.dayInWeek = (TextView) convertView.findViewById(R.id.weekday_text);
                    holder1.dayInMonth = (TextView) convertView.findViewById(R.id.date_Text);
                    holder1.diaryContent = (TextView) convertView.findViewById(R.id.content_text);
                    convertView.setTag(R.id.tag_1,holder1);
                    break;
                case TYPE_B:
                    holder2 = new ViewHolder2();
                    convertView = View.inflate(context, R.layout.dot_item, null);
                    holder2.Dot = convertView.findViewById(R.id.greypoint);
                    convertView.setTag(R.id.tag_2,holder2);
                    break;
                case TYPE_C:
                    holder3 = new ViewHolder3();
                    convertView = View.inflate(context, R.layout.browse_item, null);
                    holder3.day = (TextView) convertView.findViewById(R.id.list_text_day);
                    holder3.week = (TextView) convertView.findViewById(R.id.list_text_week);
                    holder3.content = (TextView) convertView.findViewById(R.id.list_text_content);
                    convertView.setTag(R.id.tag_3,holder3);
            }

        }
        else {
            //有convertView，按样式，取得原本的布局
            switch (type) {
                case TYPE_A:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.tag_1);
                    break;
                case TYPE_B:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.tag_2);
                    break;
                case TYPE_C:
                    holder3 = (ViewHolder3) convertView.getTag(R.id.tag_3);
                    break;
            }
        }

        Diary data = contents.get(position);
        String month;
        String day;
        //数据加载，设置周日为红色
        switch (type) {
            case TYPE_A:
                if(10 > data.getMonth())
                    month = "0" + Integer.toString(data.getMonth());
                else
                    month = Integer.toString(data.getMonth());
                if(10 > data.getDay())
                    day = "0" + Integer.toString(data.getDay());
                else
                    day = Integer.toString(data.getDay());
                week = getWeek(Integer.toString(data.getYear()) + month + day);
                holder1.dayInWeek.setText(week);
                if(week.equals("Sun"))
                    holder1.dayInWeek.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                else
                    holder1.dayInWeek.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                holder1.dayInMonth.setText(Integer.toString(data.getDay()));
                holder1.diaryContent.setText(data.getContent());
                break;

            case TYPE_B:
                //标准日期的格式
                if( data.getMonth()<10)
                    month = "0" + Integer.toString(data.getMonth());
                else
                    month = Integer.toString(data.getMonth());
                if( data.getDay()<10)
                    day = "0" + Integer.toString(data.getDay());
                else
                    day = Integer.toString(data.getDay());
                week = getWeek(Integer.toString(data.getYear()) + month + day);
                //周日显示红点
                if(week.equals("Sun"))
                   // holder2.Dot.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));//会把整个背景框填充了，变正方形灰色点
                    holder2.Dot.setBackgroundResource(R.drawable.redcircle);
                else
                    holder2.Dot.setBackgroundResource(R.drawable.greycircle);
                break;

            case TYPE_C:
                //标准日期的格式
                if(10 > data.getMonth())
                    month = "0" + Integer.toString(data.getMonth());
                else
                    month = Integer.toString(data.getMonth());
                if(10 > data.getDay())
                    day = "0" + Integer.toString(data.getDay());
                else
                    day = Integer.toString(data.getDay());
                week = getWeek(Integer.toString(data.getYear()) + month + day);
                holder3.week.setText(week);
                if(week.equals("Sun"))
                    holder3.week.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                else
                    holder3.week.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                holder3.day.setText(Integer.toString(data.getDay()));
                holder3.content.setText(data.getContent());
                break;
        }
        return convertView;
    }
    //数据清理
    public void clear()
    {
        this.contents=null;
        this.context=null;
    }

    /**
     * 有数据的的Viewholder
     */
    private static class ViewHolder1 {
        TextView dayInWeek;
        TextView dayInMonth;
        TextView diaryContent;
    }

    /**
     * 点的Viewholder
     */
    private static class ViewHolder2 {
        View Dot;
    }
    /**
     * 切换后的Viewholder
     */
    private  static class ViewHolder3{
        TextView day;
        TextView week;
        TextView content;
    }


    /**
     * 判断当前日期是星期几
     */

    private String getWeek(String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "Sun";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "Mon";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "Tue";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "Wed";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "Thu";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "Fri";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "Sat";
        }
        return Week;
    }

}
