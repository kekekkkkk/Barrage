package com.example.barrage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.VideoView;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MainActivity extends AppCompatActivity {

    /**
     * 定义成员变量  （文档注释）
     */

    private boolean showDanmaku;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext; // (Context:上下文，运行的环境)
    private Button sendButton;
    private LinearLayout sendLayout;
    private EditText editText;
    private VideoView videoView;
    private Switch open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();  //初始化界面控件
        playVideo(); //播放视频
        initDanmaku();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        videoView = (VideoView) findViewById(R.id.videoview);
        sendLayout = (LinearLayout) findViewById(R.id.ly_send);
        sendButton = (Button) findViewById(R.id.btn_send);
        editText = (EditText) findViewById(R.id.et_text);
        danmakuView = (DanmakuView) findViewById(R.id.danmaku);
        open=(Switch) findViewById(R.id.open);
    }
    /**
     * 播放视频
     */
    private void playVideo() {
        //视频地址
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.yuan;
        if (uri != null) {
            videoView.setVideoURI(Uri.parse(uri)); //解析视频
            videoView.start();  //播放视频
        } else {
            videoView.getBackground().setAlpha(0);//将背景设为透明,透明度：0-255
        }
    }
    /**
     * 创建弹幕解析器
     */
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    /**
     * 初始化弹幕
     */
    private void initDanmaku() {
        danmakuView.setCallback(new DrawHandler.Callback() {//设置回调函数
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start(); //开始弹幕
                generateDanmakus();  //调用随机生成弹幕方法
            }
            @Override
            public void updateTimer(DanmakuTimer timer) {
            }
            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
            }
            @Override
            public void drawingFinished() {
            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.enableDanmakuDrawingCache(true);//提升屏幕绘制效率(Cache：缓存)
        danmakuView.prepare(parser, danmakuContext);//进行弹幕准备（parser：解析器）
        //为danmakuView设置点击事件
        danmakuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendLayout.getVisibility() == View.GONE) {
                    sendLayout.setVisibility(View.VISIBLE);//显示布局
                } else {
                    sendLayout.setVisibility(View.GONE);   //隐藏布局
                }
            }
        });
        //为发送按钮设置点击事件
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    addDanmaku(content, true);//添加一条弹幕
                    editText.setText("");
                }
            }
        });

//        弹幕的开关
        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    danmakuView.show();//打开弹幕
                } else {
                    danmakuView.hide();// 关闭弹幕
                }
            }
        });

    }
    /**
     * 添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param border     弹幕是否有边框
     */
    private void addDanmaku(String content, boolean border) {
        //创建弹幕对象,TYPE_SCROLL_RL表示从右向左滚动的弹幕  TYPE_SCROLL_LR:从左向右（context上下文）
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(
                BaseDanmaku.TYPE_SCROLL_LR);
        danmaku.text = content;
        danmaku.padding = 6;
        Random random = new Random();
        danmaku.textSize =(random.nextInt(15) + 35);
        danmaku.textColor = Color.argb(random.nextInt(256),random.nextInt(256),random.nextInt(256), random.nextInt(256));
//        danmaku.textColor = Color.rgb(random.nextInt(256),random.nextInt(256), random.nextInt(256));//弹幕文字的颜色
//      速度
//        danmaku.duration.setFactor((float)Math.random()*5);//滚动系数越小越快
        danmaku.setTime(danmakuView.getCurrentTime());  //设置时间为当前时间
        if (border) {
            danmaku.borderColor = Color.RED;//弹幕文字边框的颜色
        }
        danmakuView.addDanmaku(danmaku);     //添加一条弹幕
    }
    /**
     * 随机生成一些弹幕内容
     */
    private void generateDanmakus() {   //generate 生成
        new Thread(new Runnable() {  //子线程
            @Override
            public void run() {
                while (showDanmaku) {
                    int num = new Random().nextInt(300); //生成0-299的正整数
                    String content = "" + num;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(num);  //休眠299毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() &&
                danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }
}