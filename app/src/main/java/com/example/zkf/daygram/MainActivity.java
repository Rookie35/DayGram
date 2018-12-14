package com.example.zkf.daygram;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private  String TAG = "Mainactivity";
    private HashMap<String,ArrayList<Diary>> ListOfAll;//哈希表，存储月份和对应数组的映射
    private ArrayList<Diary> ListOfMonth = new ArrayList<Diary>();//存储月份日记的数组
    private ArrayList<Diary> ListFullDiary = new ArrayList<Diary>();//存储月份中有日记内容的数组
    private ListView listView;//试图列表
    private ListViewAdapter adapter;//存储ListOfMonth、LisFullDiar数组的适配器
    private DiaryCollectionOperater operate=new DiaryCollectionOperater();//数据存储操作
    private int currentYear;//当前年份
    private int currentMonth;//当前月份
    private int currentDay;//当前日期
    private int selectYear;//选定年份
    private int selectMonth;//选定月份
    private Button month_btn,year_btn;//月份、年份选择按钮
    private ImageButton add_btn,browse_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//绑定布局文件
        //时间初始化
        SimpleDateFormat sdf ;
        sdf= new SimpleDateFormat("yyyy");
        currentYear = Integer.parseInt(sdf.format(new java.util.Date()));
        sdf = new SimpleDateFormat("MM");
        currentMonth = Integer.parseInt(sdf.format(new java.util.Date()));
        sdf = new SimpleDateFormat("dd");
        currentDay = Integer.parseInt(sdf.format(new java.util.Date()));
        selectYear = currentYear;
        selectMonth = currentMonth;
        //测试1
        /*
        //判断是否为第一次启动
        setting = getSharedPreferences("phone", 0);
        user_first = setting.getBoolean("FIRST",true);
        if(user_first){//第一次启动，初始化ListOfAll的数据，从2008-1-1到2018-12-31
            setting.edit().putBoolean("FIRST", false).commit();
            ListOfAll=new HashMap<String,ArrayList<Diary>>();
            firstinit();
        }
        else {
            //非第一次启动,将文件中的数据加载到ListOfAll中
            ListOfAll=operate.load(getBaseContext());
         }
         //将当前月份的数组从ListOfALL哈希表中加载到ListOfMonth中
         ListOfMonth=ListOfAll.get(Integer.toString(currentYear)+Integer.toString(currentMonth));
        */
        ListOfAll=operate.load(getBaseContext());//从文件中加载哈希表到LisOfAll
        if(ListOfAll==null){//如果载入为空，说明是第一次启动程序，需要初始化哈希表
            ListOfAll=new HashMap<String,ArrayList<Diary>>();//新建哈希表
            firstinit();//初始化哈希表
        }
        //从哈希表中获取当月的数组，加载到ListOfMonth中
        ListOfMonth=ListOfAll.get(Integer.toString(currentYear)+Integer.toString(currentMonth));

        //初始化适配器，加载数据到listview显示
        adapter = new ListViewAdapter(MainActivity.this, this.ListOfMonth);
        listView =(ListView)this.findViewById(R.id.MyListView);
        listView.setAdapter(adapter);

        //初始化listview的点击事件
            listView.setOnItemClickListener(new mItemClick());

        //初始化月份选择按钮
        month_btn = (Button) findViewById(R.id.month);
        month_btn.setText(getMonthStr(selectMonth));//设置月份按钮显示当前月份
        month_btn.setOnClickListener(new MonthBtnClick());

        //初始化年份选择
        year_btn = (Button) findViewById(R.id.year);
        year_btn.setText(Integer.toString(selectYear));//设置年份按钮显示当前年份
        year_btn.setOnClickListener(new YearBtnClick());

        //初始化添加按钮
        add_btn = (ImageButton) findViewById(R.id.add);
        add_btn.setOnClickListener(new AddBtnClick());

        //初始化浏览按钮
        browse_btn = (ImageButton) findViewById(R.id.browse);
        browse_btn.setOnClickListener(new BrowseBtnClick());
    }
    //处理返回主界面的操作
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle;
                    bundle = data.getExtras();
                    //获取返回值
                    int year = bundle.getInt("year");
                    int month = bundle.getInt("month");
                    int day = bundle.getInt("day");
                    String content = bundle.getString("diaryContent");
                    //设置新日记
                    Diary diary=new Diary();
                    diary.setDay(day);
                    diary.setMonth(month);
                    diary.setYear(year);
                    diary.setContent(content);
                    adapter.changedata(diary,day-1);//改变listOfMonth中被修改的日记
                    adapter.notifyDataSetChanged();//刷新适配器
                    month_btn.setText(getMonthStr(selectMonth));
                    year_btn.setText(Integer.toString(selectYear));
                    //更新哈希表中的月份数组
                    ListOfAll.put(Integer.toString(year)+Integer.toString(month),ListOfMonth);
                    operate.save(getBaseContext(),ListOfAll);//把哈希表保存到文件
                    break;
                }
        }
    }
    //注册月份选择按钮事件
    class MonthBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final AlertDialog MonthDialog=new AlertDialog.Builder(MainActivity.this,R.style.MonthDialog).create();//创建自定义对话框，默认会遮住整个页面
            MonthDialog.show();
            WindowManager.LayoutParams lp = MonthDialog.getWindow().getAttributes();//获取窗口属性
            //获取屏幕分辨率属性
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //保持窗口与屏幕的宽度分辨率相同
            lp.width = dm.widthPixels;
            MonthDialog.getWindow().setAttributes(lp);
            //窗口内容绑定
            MonthDialog.getWindow().setContentView(R.layout.month_tabbar);//整个窗口内容看作一个新页面
            MonthDialog.setCancelable(true);//返回键返回
            //点击窗口之外可取消
            MonthDialog.getWindow().findViewById(R.id.month_tabbar)
                    .setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            MonthDialog.dismiss();
                        }
                    });

            int [] buttonId={R.id.Jan_bar,R.id.Feb_bar,R.id.Mar_bar,R.id.Apr_tar,R.id.May_tar,R.id.Jun_bar,R.id.Jul_bar,
                    R.id.Aug_bar,R.id.Sep_bar,R.id.Oct_bar,R.id.Nov_bar,R.id.Dec_bar};
            //选择项变黑
            MonthDialog.getWindow().findViewById(buttonId[selectMonth-1]).setBackgroundResource(R.drawable.blackcircle);
            //各个按钮绑定
            for (int i=0;i<12;i++)
            {
                MonthDialog.getWindow().findViewById(buttonId[i]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int j=0;j<12;j++){
                            if(((TextView)view).getText().toString().equals(getMonthStr(j+1)))
                            {
                                selectMonth=j+1;
                                break;
                            }
                        }
                        refreshlist();//更新ListOfMonth和adapter
                        month_btn.setText(getMonthStr(selectMonth));
                        MonthDialog.dismiss();
                    }
                });
            }
        }
    }
    //注册年份选择按钮事件
    class YearBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final AlertDialog YearDialog=new AlertDialog.Builder(MainActivity.this,R.style.MonthDialog).create();//创建自定义对话框，默认会遮住整个页面
            YearDialog.show();
            WindowManager.LayoutParams lp = YearDialog.getWindow().getAttributes();//获取窗口属性
            //获取屏幕分辨率属性
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //保持窗口与屏幕的宽度分辨率相同
            lp.width = dm.widthPixels;
            YearDialog.getWindow().setAttributes(lp);
            //窗口内容绑定
            YearDialog.getWindow().setContentView(R.layout.year_tabbar);//整个窗口内容看作一个新页面
            YearDialog.setCancelable(true);//返回键返回
            //点击窗口之外可取消
            YearDialog.getWindow().findViewById(R.id.year_tabbar)
                    .setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            YearDialog.dismiss();
                        }
                    });
            int [] buttonId={R.id.first_year,R.id.second_year,R.id.third_year,R.id.fourth_year,R.id.fifth_year,R.id.sixth_year,R.id.seventh_year,
                    R.id.eighth_year};
            //选择项变黑
            ((TextView)YearDialog.getWindow().findViewById(buttonId[selectYear-2018])).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
            //各个按钮绑定
            for (int i=0;i<8;i++)
            {
                YearDialog.getWindow().findViewById(buttonId[i]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int j=0;j<8;j++){
                            if(((TextView)view).getText().toString().equals(Integer.toString(2018+j)))
                            {
                                selectYear=2018+j;
                                break;
                            }
                        }

                        refreshlist();//更新ListOfMonth和adapter
                        year_btn.setText(Integer.toString(selectYear));
                        YearDialog.dismiss();
                    }
                });
            }
        }
    }
    //注册添加按钮事件
    class AddBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            selectYear = currentYear;
            selectMonth = currentMonth;
            refreshlist();//更新ListOfMonth为当前月份
            Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
            Bundle bundle = new Bundle();
            //将当天的日记数据传递给EditDiaryActivity
            bundle.putInt("year", currentYear);
            bundle.putInt("month", currentMonth);
            bundle.putInt("day", currentDay);
            if (ListOfMonth.get(currentDay-1).getContent()==null)
                bundle.putString("diaryContent", null);
            else
                bundle.putString("diaryContent",ListOfMonth.get(currentDay-1).getContent());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);//有返回结果
        }
    }
    //注册浏览按钮点击事件
    class BrowseBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //取出月份中有日记的内容到ListFullDiary数组中
            ListFullDiary.clear();
            for (int i=0;i<ListOfMonth.size();i++)
            {
                if(ListOfMonth.get(i).getContent()!=null)
                    ListFullDiary.add(ListOfMonth.get(i));
            }
            if(adapter.getstatus())//适配器处于browse状态，点击按钮后返回listview状态
            {
                adapter = new ListViewAdapter(MainActivity.this, ListOfMonth);
                listView.setAdapter(adapter);
            }
            else {//适配器处于listview状态，点击按钮后显示browse状态
                adapter.changestatus();//改变适配器状态为browse
                //更新适配器绑定LisFullDiary数组
                adapter.refreshdata(ListFullDiary);
                adapter.notifyDataSetChanged();
            }
        }
    }
    //程序第一次启动，初始化哈希表
    private void firstinit(){
        ListOfAll=new HashMap<String, ArrayList<Diary>>();
        //for循环遍历2018-2025年的所有月份，将其‘年份+月份’String -> List加入哈希表
        for(int i=2018;i<=2025;i++){
            for(int j=1;j<=12;j++) {
                ArrayList<Diary> newListOfMonth=new ArrayList<Diary>();//新建月份数组
                //新建月份数组初始化数据
                for(int k=1;k<=getDayCount(i,j);k++){
                    //新建日记
                    Diary diary=new Diary();
                    diary.setYear(i);
                    diary.setMonth(j);
                    diary.setDay(k);
                    diary.setContent(null);
                    newListOfMonth.add(diary);
                }
                ListOfAll.put(Integer.toString(i)+Integer.toString(j),newListOfMonth);//将新建数组加入哈希表
            }
        }
    }
    //根据选中的年份、月份更新ListOfMonth,同步更新适配器
    private void refreshlist(){
       // ListOfMonth.clear();
        ListOfMonth=new ArrayList<Diary>();
        ListOfMonth=ListOfAll.get(Integer.toString(selectYear)+Integer.toString(selectMonth));
        //更新适配器
        adapter = new ListViewAdapter(MainActivity.this, ListOfMonth);
        listView.setAdapter(adapter);

    }


    //测试2
    /*
    private void dataintialise()
    {
        //Log.d(TAG,"month"+sellectMonth+",year"+sellectYear);
        this.ListOfMonth.clear();
        this.ListOfAll=operate.load(getBaseContext());
        if(ListOfAll==null)
            ListOfAll=new HashMap<String, ArrayList<Diary>>();
        //如果对应的月份无数据，正常初始化
        if(ListOfAll.get(Integer.toString(selectYear)+Integer.toString(selectMonth))!=null)
        {
            ListOfMonth=ListOfAll.get(Integer.toString(selectYear)+Integer.toString(selectMonth));
        }
        //如果是初始加载，直接加上距上次没有加那些
        if(selectMonth==currentMonth&&selectYear==currentYear) {
            for (int i = ListOfMonth.size() + 1; i <= getDayCount(currentYear,currentMonth); i++) {
                Diary content = new Diary();
                content.setYear(currentYear);
                content.setMonth(currentMonth);
                content.setDay(i);
                this.ListOfMonth.add(content);
            }
            if(adapter!=null)//第二次刷新，adapter要重置
            {
                adapter = new ListViewAdapter(MainActivity.this, ListOfMonth);
                listView.setAdapter(adapter);
            }
        }//不是当前月份
        else {
           // Log.d(TAG,"size:"+DaycontentsList.size()+",count:"+getDayCount(time));
            for(int i=ListOfMonth.size() +1;i<=getDayCount(selectMonth,selectYear);i++)
            {
                Diary content = new Diary();
                content.setYear(selectYear);
                content.setMonth(selectMonth);
                content.setDay(i);
                this.ListOfMonth.add(content);
            }
            adapter = new ListViewAdapter(MainActivity.this, ListOfMonth);
            listView.setAdapter(adapter);
        }
    }
    */

    //listview的Item点击事件
    class mItemClick implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!adapter.getstatus()){//适配器处于listview状态才有点击编辑事件
            Diary selectcontent = ListOfMonth.get(i);//获取选中的数据
            Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("year", selectcontent.getYear());
            bundle.putInt("month", selectcontent.getMonth());
            bundle.putInt("day", selectcontent.getDay());
            bundle.putString("diaryContent", selectcontent.getContent());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
            Log.d(TAG,"click");
            }
        }
    }

   //获取月份的String
   private String getMonthStr(int month) {
       switch (month) {
           case 1:return "JAN";case 2:return "FEB";case 3:return "MAR";case 4:return "APR";case 5:return "MAY";case 6:return "JUN";
           case 7:return "JUL";case 8:return "AUG";case 9:return "SEP";case 10:return "OCT";case 11:return "NOV";case 12:return "DEC";
           default:return null;
       }
   }
   //获取选定月份的天数
    private int getDayCount(int year,int month){
        Calendar calendar = Calendar.getInstance();
        // 格式化日期--设置date
        calendar.set(Calendar.YEAR,selectYear);
        calendar.set(Calendar.MONTH,selectMonth-1);
        int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return count;
    }
}
