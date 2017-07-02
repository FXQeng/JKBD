package com.example.administrator.jkbd;

import android.app.Application;

import com.example.administrator.jkbd.bean.ExamInfo;
import com.example.administrator.jkbd.bean.Question;
import com.example.administrator.jkbd.biz.ExamBiz;
import com.example.administrator.jkbd.biz.IExamBiz;

import java.util.List;

/**
 * Created by Who on 2017/7/2.
 */

public class ExamApplication extends Application {
    public static String LOAD_EXAM_INFO = "load_exam_info";
    public static String LOAD_EXAM_QUESTION = "load_exam_question";
    public static String LOAD_DATA_SUCCESS = "load_data_success";
     ExamInfo mExamInfo;
     List<Question> mExamList;
     private static ExamApplication instance;
     IExamBiz  biz;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        biz=new ExamBiz();

           }



    public static ExamApplication getInstance(){
              return instance;
            }

    public ExamInfo getExamInfo() {
               return mExamInfo;
           }

    public void setExamInfo(ExamInfo examInfo) {
               mExamInfo = examInfo;
           }

    public List<Question> getExamList() {
                return mExamList;
          }

    public void setExamList(List<Question> examList) {
            mExamList = examList;
         }


}
