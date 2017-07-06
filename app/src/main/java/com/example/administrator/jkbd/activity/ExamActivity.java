package com.example.administrator.jkbd.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.jkbd.ExamApplication;
import com.example.administrator.jkbd.R;
import com.example.administrator.jkbd.bean.ExamInfo;
import com.example.administrator.jkbd.bean.Question;
import com.example.administrator.jkbd.biz.ExamBiz;
import com.example.administrator.jkbd.biz.IExamBiz;
import com.example.administrator.jkbd.view.QuestionAdapter;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Who on 2017/6/29.
 */

public class ExamActivity extends AppCompatActivity {
    char[] optoin=new char[4];
    TextView tvExamInfo,tvExamTitle,tvOp1,tvOp2,tvOp3,tvOp4,tvload,tvNo,tvTime,tvAnswer;
    LinearLayout layoutLoading,layout03,layout04;
    CheckBox cb01,cb02,cb03,cb04;
    CheckBox[] cbs = new CheckBox[4];
    TextView[] tvOps = new TextView[4];
    ProgressBar dialog;
    ImageView mImageView;
    Gallery mgallery;
    IExamBiz biz;
    QuestionAdapter mAdapter;

    boolean isLoadExamInfo=false;
    boolean isLoadQuestions=false;

    boolean isLoadExamInfoReceiver=false;
    boolean isLoadQuestionsReceiver=false;

    LoadExamBroadcast mLoadExamBroadcast;
    LoadQuestionBroadcast mLoadQuestionBroadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        mLoadExamBroadcast = new LoadExamBroadcast();
        mLoadQuestionBroadcast = new LoadQuestionBroadcast();
        setListener();
        initView();    //初始化控件
        biz = new ExamBiz();
        loadData();
    }

    private void setListener() {
        registerReceiver(mLoadExamBroadcast,new IntentFilter(ExamApplication.LOAD_EXAM_INFO));
        registerReceiver(mLoadQuestionBroadcast,new IntentFilter(ExamApplication.LOAD_EXAM_QUESTION));
    }

    private void loadData() {
        layoutLoading.setEnabled(false);
        dialog.setVisibility(View.VISIBLE);
        tvload.setText("下载数据...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                biz.beginExam();
            }
        }).start();
    }

    private void initView() {
        optoin[0]='A';
        optoin[1]='B';
        optoin[2]='C';
        optoin[3]='D';
        layoutLoading=(LinearLayout)findViewById(R.id.layout_loading);
        layout03 = (LinearLayout) findViewById(R.id.layout_03);
        layout04 = (LinearLayout) findViewById(R.id.layout_04);
        dialog=(ProgressBar) findViewById(R.id.load_dialog);
        tvExamInfo = (TextView) findViewById(R.id.tv_examinfo);
        tvExamTitle = (TextView) findViewById(R.id.tv_exam_title);
        tvOp1 = (TextView) findViewById(R.id.tv_op1);
        tvOp2 = (TextView) findViewById(R.id.tv_op2);
        tvOp3 = (TextView) findViewById(R.id.tv_op3);
        tvOp4 = (TextView) findViewById(R.id.tv_op4);
        tvload=(TextView) findViewById(R.id.tv_load);
        tvNo=(TextView) findViewById(R.id.tv_exam_no);
        tvAnswer=(TextView)findViewById(R.id.tv_answer) ;
        cb01 = (CheckBox) findViewById(R.id.cb_01);
        cb02 = (CheckBox) findViewById(R.id.cb_02);
        cb03 = (CheckBox) findViewById(R.id.cb_03);
        cb04 = (CheckBox) findViewById(R.id.cb_04);
        cbs[0] = cb01;
        cbs[1] = cb02;
        cbs[2] = cb03;
        cbs[3] = cb04;
        tvOps[0] = tvOp1;
        tvOps[1] = tvOp2;
        tvOps[2] = tvOp3;
        tvOps[3] = tvOp4;
        mImageView = (ImageView) findViewById(R.id.im_exam_image);
        mgallery=(Gallery)findViewById(R.id.gallery) ;
        layoutLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        cb01.setOnCheckedChangeListener(listener);
        cb02.setOnCheckedChangeListener(listener);
        cb03.setOnCheckedChangeListener(listener);
        cb04.setOnCheckedChangeListener(listener);
    }

    //答案选项互斥
    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                int userAnswer = 0;
                switch (buttonView.getId()) {
                    case R.id.cb_01:
                        userAnswer = 1;
                        break;
                    case R.id.cb_02:
                        userAnswer = 2;
                        break;
                    case R.id.cb_03:
                        userAnswer = 3;
                        break;
                    case R.id.cb_04:
                        userAnswer = 4;
                        break;
                }
                Log.e("checkedChanged", "usera=" + userAnswer + ",isChecked=" + isChecked);
                if (userAnswer > 0) {
                    for (CheckBox cb : cbs) {
                        cb.setChecked(false);
                    }
                    cbs[userAnswer - 1].setChecked(true);
                }
            }
        }
    };

    //初始化数据
    private void initData() {
        if(isLoadExamInfoReceiver&&isLoadQuestionsReceiver){
            if (isLoadExamInfo && isLoadQuestions) {
                layoutLoading.setVisibility(View.GONE);
                ExamInfo examInfo = ExamApplication.getInstance().getExamInfo();
                if (examInfo != null) {
                    showData(examInfo);
                    initTimer(examInfo);  //调用倒计时方法
                }
                initGallery();
                showExam(biz.getExam());
                }
            }else{
                layoutLoading.setEnabled(true);
                dialog.setVisibility(View.GONE);
                tvload.setText("下载失败，点击重新下载");
            }
        }

    private void initGallery() {
        mAdapter=new QuestionAdapter(this);   //实例化适配器对象
        mgallery.setAdapter(mAdapter);
        mgallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {   //Gallery点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveUserAnswer();
                showExam(biz.getExam(position));
            }
        });
    }

    //进入考试界面开始倒计时
    private void initTimer(ExamInfo examInfo) {
        int sumTime=examInfo.getLimitTime()*60*1000;
        final long overTime=sumTime+System.currentTimeMillis();
        final Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long l=overTime-System.currentTimeMillis();
                final long min=l/1000/60;
                final long sec=l/1000%60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText("剩余时间:"+min+"分"+sec+"秒");
                    }
                });
            }
        },0,1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commit();
                    }
                });
            }
        },sumTime);

    }


    private void showExam(Question exam) {
        Log.e("showExam","showExam,exam="+exam);
        if (exam != null) {
            tvNo.setText(biz.getExamIndex());
            tvExamTitle.setText(exam.getQuestion());
            tvOp1.setText(exam.getItem1());
            tvOp2.setText(exam.getItem2());
            tvOp3.setText(exam.getItem3());
            tvOp4.setText(exam.getItem4());
            tvTime=(TextView)findViewById(R.id.tv_timer);
            //考试题判断题只显示AB选项，CD不显示
            layout03.setVisibility(exam.getItem3().equals("") ? View.GONE : View.VISIBLE);
            cb03.setVisibility(exam.getItem3().equals("") ? View.GONE : View.VISIBLE);
            layout04.setVisibility(exam.getItem4().equals("") ? View.GONE : View.VISIBLE);
            cb04.setVisibility(exam.getItem4().equals("") ? View.GONE : View.VISIBLE);
            //判断试题有无图片，有就使用Picasso加载网络图片
            if (exam.getUrl() != null && !exam.getUrl().equals("")) {
                mImageView.setVisibility(View.VISIBLE);
                Picasso.with(ExamActivity.this)
                        .load(exam.getUrl())
                        .into(mImageView);
            } else {
                mImageView.setVisibility(View.GONE);
            }
            resetOptions();//调用初始化选项方法
            setOptionsColor();  //调用初始化下一题的选项颜色
            //进行答题时，保存答题的答案，使得点击上一题下一题时显示已选的答案
            String userAnswer =exam.getUserAnswer();
            if(userAnswer!=null&&!userAnswer.equals("")) {
                int userCB = Integer.parseInt(userAnswer) - 1;
                cbs[userCB].setChecked(true);
                setOptions(true);   //保存答案将答案锁定
                setAnswerTextColor(userAnswer,exam.getAnswer());
                //显示正确答案和用户答案和解析
                tvAnswer.setText("正确答案："+optoin[Integer.parseInt(exam.getAnswer())-1]+"\n"+"你的答案："+optoin[userCB]+"\n"+"解析："+exam.getExplains());
            }else{
                setOptions(false);
                setOptionsColor();
            }
        }
    }

    private void setOptionsColor() {
        for (TextView tvOp : tvOps) {
            tvOp.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void setAnswerTextColor(String userAnswer, String answer) {
        int ra = Integer.parseInt(answer)-1;
        for (int i = 0; i < tvOps.length; i++) {
            if (i==ra){
                tvOps[i].setTextColor(getResources().getColor(R.color.green));
            }else{
                if (!userAnswer.equals(answer)) {
                    int ua = Integer.parseInt(userAnswer) - 1;
                    if (i == ua) {
                        tvOps[i].setTextColor(getResources().getColor(R.color.red));
                    } else {
                        tvOps[i].setTextColor(getResources().getColor(R.color.black));
                    }
                }
            }
        }
    }

    //设置答案不可修改
    private void setOptions(boolean hasAnswer){
        for(CheckBox cb : cbs){
            cb.setEnabled(!hasAnswer);
        }
    }
    //初始化选项
    private void resetOptions() {
        for(CheckBox cb:cbs){
            cb.setChecked(false);
        }
    }

    //保存用户答案
    private void saveUserAnswer(){
        for(int i=0;i<cbs.length;i++){
            if(cbs[i].isChecked()) {
                biz.getExam().setUserAnswer(String.valueOf(i+1));
                setOptions(true);
                mAdapter.notifyDataSetChanged();   //刷新
                return;
            }
        }
        biz.getExam().setUserAnswer("");
        mAdapter.notifyDataSetChanged();
    }

    private void showData(ExamInfo examInfo) {
        tvExamInfo.setText(examInfo.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadExamBroadcast!=null){
            unregisterReceiver(mLoadExamBroadcast);
        }
        if (mLoadQuestionBroadcast!=null){
            unregisterReceiver(mLoadQuestionBroadcast);
        }
    }

    //上一题点击事件
    public void preExam(View view) {
        saveUserAnswer();
        showExam(biz.preQuestion());
    }

    //下一题点击事件
    public void nextExam(View view) {
        saveUserAnswer();
        showExam(biz.nextQuestion());
    }

    //确认交卷后不能继续答题
    public void commit(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.exam_commit32x32)
                .setTitle("交卷")
                .setMessage("确认交卷吗?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commit();
                    }
                })
                .setNegativeButton("取消",null);
        builder.create().show();
    }

    //交卷并显示分数
    public void commit() {
            saveUserAnswer();
            int s = biz.commitExam();
            View inflate = View.inflate(this, R.layout.layout_result, null);
            TextView tvResult = (TextView) inflate.findViewById(R.id.tv_result);
            tvResult.setText("你的分数为\n"+s+"分！");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.exam_commit32x32)
                    .setTitle("交卷")
                    .setView(inflate)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setCancelable(false); //设置除点击对话框的ok外的点击无效
            builder.create().show();
    }

    class LoadExamBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSuccess = intent.getBooleanExtra(ExamApplication.LOAD_DATA_SUCCESS,false);
            Log.e("LoadExamBroadcast","LoadExamBroadcast,isSuccess="+isSuccess);
            if(isSuccess){
                isLoadExamInfo=true;
            }
            isLoadExamInfoReceiver=true;
            initData();
        }
    }
    class LoadQuestionBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSuccess = intent.getBooleanExtra(ExamApplication.LOAD_DATA_SUCCESS,false);
            Log.e("LoadQuestionBroadcast","LoadQuestionBroadcast,isSuccess="+isSuccess);
            if(isSuccess){
                isLoadQuestions=true;
            }
            isLoadQuestionsReceiver=true;
            initData();
        }
    }
}
