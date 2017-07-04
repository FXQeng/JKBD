package com.example.administrator.jkbd.biz;

import com.example.administrator.jkbd.bean.Question;

/**
 * Created by Who on 2017/7/2.
 */

public interface IExamBiz {
    void beginExam();
    Question getExam();
    Question nextQuestion();
    Question preQuestion();
    int commitExam();
    String getExamIndex();
}
