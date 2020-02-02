package com.example.quiz;

import java.util.HashMap;

public class Answer {
    static HashMap<String,String> answer0 = new HashMap<String, String>(){
        {
            put("statement","お酢に卵を殻ごといれると卵はどうなるでしょう？");
            put("1","透明な卵になる");
            put("2","鏡のようになんでもうつる卵になる");
            put("3","卵が溶けてなくなる");
            put("4","卵が石のように堅くなる");
            put("correct","透明な卵になる");
            put("explanation","お酢にはカルシウムを溶かす力があり、カルシウムでできた卵の殻は、お酢につけると溶けてなくなります。" +
                    "卵をむくときについている、薄い皮と中身が残って、透明な卵ができるのです。" +
                    "ぶよぶよしてゴムボールみたいになります!");
        }
    };

    static HashMap<String,String> answer1 = new HashMap<String, String>(){
        {
            put("statement","しゃっくりはある調味料をなめると止まります。ある調味料とはなんでしょう？");
            put("1","お酢");
            put("2","砂糖");
            put("3","醤油");
            put("4","塩");
            put("correct","砂糖");
            put("explanation","しゃっくりとは、体の中の「横隔膜」というところで起きるけいれんです。" +
                    "甘い物をのむとのどが刺激を受けて横隔膜のけいれんが止まって、しゃっくりが止まると言われています。");
        }
    };

    static HashMap[] answers = {
            answer0,
            answer1
    };

    public static HashMap<String, String> getAnser(int number) {
        return answers[number];
    }
    public static HashMap<String, String>[] getAnswers( ) { return answers; }
}
