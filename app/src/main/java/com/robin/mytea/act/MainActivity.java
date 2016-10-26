package com.robin.mytea.act;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robin.mytea.R;
import com.robin.mytea.adapter.MainFragmentPagerAdapter;
import com.robin.mytea.app.MyApplication;
import com.robin.mytea.config.MyConfig;
import com.robin.mytea.fragment.base.ContentFragment;
import com.robin.mytea.helper.JsonHelper;
import com.robin.mytea.helper.MyHttpClientHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现主界面 百科、新闻等加载
 * Created by robin .
 */
public class MainActivity extends FragmentActivity {
    private final String TAG = "MainActivity";
    /** 导航栏 */
    private LinearLayout layout_main_container;
    /** 存储每一个Fragment的viewPager */
    private ViewPager viewPager_main_content;
    /** 文本导航内容 */
    private TextView[] arr_tabspec;
    /** viewpager适配器的数据源 */
    private List<Fragment> list = null;
    /** 主界面viewPager适配器 */
    private MainFragmentPagerAdapter mainFragmentPagerAdapter;
    /** 五个访问网络数据的url */
    private String[] urlStr;
    /** 全局变量之图片缓存map */
    protected Map<String, Bitmap> cacheImageMap;

    /**
     * oncreate生命周期的开始
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "==onCreate()");
        setContentView(R.layout.activity_main);
        initData();
        initDot();
    }

    private void initData() {
        //获取缓存的map容器<url,bitmap>
        cacheImageMap = ((MyApplication) (this.getApplication()))
                .getCacheImageMap();
        Log.i(TAG, "cacheImageMap==" + cacheImageMap);
        urlStr = new String[6];
        urlStr[0] = MyConfig.JSON_LIST_DATA_0;
        urlStr[1] = MyConfig.JSON_LIST_DATA_1;
        urlStr[2] = MyConfig.JSON_LIST_DATA_2;
        urlStr[3] = MyConfig.JSON_LIST_DATA_3;
        urlStr[4] = MyConfig.JSON_LIST_DATA_4;
        urlStr[5] = "";
        //启动网络加载异步任务
        new MyTask(MainActivity.this).execute(urlStr[0], urlStr[1], urlStr[2],
                urlStr[3], urlStr[4]);
    }

    /**
     * 初始化导航点
     */
    public void initDot() {
		/* 获得存放导航标签对象的容器 */
        layout_main_container = (LinearLayout) findViewById(R.id.layout_main_container);
        /*创建tab的数组对象*/
        arr_tabspec = new TextView[layout_main_container.getChildCount()];
        for (int i = 0; i < arr_tabspec.length; i++) {
            TextView view = (TextView) layout_main_container.getChildAt(i);
            if (i == 5) {//表示是查询
                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goSearchActivity();

                    }
                });
                break;
            }
            arr_tabspec[i] = view;//将每个tab对象textview存放到数组中
            if (i == 0) {
                arr_tabspec[i].setEnabled(false);
            } else {
                arr_tabspec[i].setEnabled(true);
                arr_tabspec[i].setBackgroundColor(Color.parseColor("#ebebeb"));
            }
            arr_tabspec[i].setTextColor(Color.GRAY);
            arr_tabspec[i].setTag(i);//保存当前tab对象的索引
            arr_tabspec[i].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    viewPager_main_content.setCurrentItem((Integer) v.getTag(),
                            false);
                }
            });

        }
    }

    /**
     * 初始化viewPager布局
     */
    private void initLayout(Map<String, List<Map<String, Object>>> result) {
        Log.i(TAG, "==initLayout()");

		/* viewPager 存放的页卡是ContentFragment碎片对象 保存到碎片列表容器中 */
        list = new ArrayList<Fragment>();
        //一个循环5次 url0,1,2,3,4
        for (int i = 0; i < arr_tabspec.length; i++) {
            Fragment fragment = new ContentFragment(urlStr[i],
                    (List<Map<String, Object>>) result.get(urlStr[i]),
                    cacheImageMap);
            list.add(fragment);//添加到碎片列表容器中
        }
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(
                getSupportFragmentManager(), list);
        viewPager_main_content = (ViewPager) findViewById(R.id.viewPager_main_content);
        viewPager_main_content.setAdapter(mainFragmentPagerAdapter);
        viewPager_main_content
                .setOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        Log.i(TAG, "onPageSelected()==arg0-->" + arg0);
                        if (arg0 == 5) {
                            goSearchActivity();// 跳转到搜索页，然后还原显示最后一个有数据的viewPager页
                            arr_tabspec[4].performClick();
                            return;
                        }
                        for (int i = 0; i < arr_tabspec.length - 1; i++) {
                            if (i == arg0) {
                                arr_tabspec[i].setEnabled(false);
                                arr_tabspec[i].setTextColor(Color
                                        .parseColor("#3d9d01"));
                                arr_tabspec[i]
                                        .setBackgroundResource(R.drawable.tabbg);
                            } else {
                                arr_tabspec[i].setEnabled(true);
                                arr_tabspec[i].setTextColor(Color.GRAY);
                                arr_tabspec[i].setBackgroundColor(Color
                                        .parseColor("#ebebeb"));
                            }
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });

    }

    /**
     * 跳转到搜索页
     */
    private void goSearchActivity() {
        Log.i(TAG, "==跳转");
        Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
        intent.putExtra("titleTagStr", "0");// 0表示查百科
        startActivity(intent);

    }

    /**
     * 访问网络得到五页的数据
     *
     *
     */
    class MyTask extends AsyncTask<String, Void, Object> {
        private Context context;
        private ProgressDialog pDialog;

        public MyTask(Context context) {
            this.context = context;
            pDialog = new ProgressDialog(this.context);
            pDialog.setTitle("网络访问");
            pDialog.setMessage("正在加载...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();//弹出加载对话框
        }

        @Override
        protected Object doInBackground(String... params) {
            Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
            for (int i = 0; i < params.length; i++) {
                String jsonString = MyHttpClientHelper.loadTextFromURL(
                        params[i], "UTF-8");// 网络访问得到数据
                //
                List<Map<String, Object>> list = JsonHelper.jsonStringToList(
                        jsonString, new String[]{"title", "source",
                                "nickname", "create_time", "wap_thumb", "id"},
                        "data");
                map.put(params[i], list);//map对象会存放urlStr[0,1,2,3,4]共5个list对象
            }
            return map;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            // Log.i(TAG,"result=="+result);
            //将网络解析好数据加载到listview
            initLayout((Map<String, List<Map<String, Object>>>) result);
            pDialog.dismiss();//关闭加载对话框

        }
    }

    /**
     * 提示是否退出对话框
     */
    protected void dialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("是否退出?");
        builder.setTitle("操作提示");
        builder.setPositiveButton("是",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 返回键双击
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();//退出弹出提示对话框
            return true;
        }
        return true;
    }


}