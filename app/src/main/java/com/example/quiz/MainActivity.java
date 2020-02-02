package com.example.quiz;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

import java.util.HashMap;


/*
参考サイト
https://codeforfun.jp/android-studio-quiz-game-1/
 */
public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;

    private Button answerBtn1;
    private Button answerBtn2;
    private Button answerBtn3;
    private Button answerBtn4;
    private TextView countLabel;

    private int correctSound = 0;
    private int wrongSound = 0;
    private SoundPool soundPool;

    private int quizCount = 0;
    private HashMap<String, String> quiz;
    static private int QUIZ_COUNT;
    int correctCount = 0;

    private Handler handler;
    private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this,this);

        // 配列の長さの分
        QUIZ_COUNT = Answer.getAnswers().length;

        handler = new Handler();

        //音声再生用クラス読み込み
        /*
            usage: 利用用途
            contentType: 種別
            attributes: 属性
         */
        AudioAttributes  attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        /*
            max: ストリーム数(同時に扱う効果音の数)
         */
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(1)
                .build();

        correctSound = soundPool.load(this, R.raw.correct,1);
        wrongSound = soundPool.load(this, R.raw.wrong,1);

        //SoundPool.loadは非同期なのでListenerが必要
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    Log.d("debug","sampleId="+sampleId);
                    Log.d("debug","status="+status);
            }
        });


        answerBtn1 = findViewById(R.id.answerBtn1);
        answerBtn2 = findViewById(R.id.answerBtn2);
        answerBtn3 = findViewById(R.id.answerBtn3);
        answerBtn4 = findViewById(R.id.answerBtn4);

        countLabel = findViewById(R.id.countLabel);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this,this);
    }

    public void showNextQuiz() {
        countLabel.setText("Q." + String.valueOf(quizCount + 1));

        quiz = Answer.getAnser(quizCount);

        // 回答ボタンに選択肢4つを表示
        answerBtn1.setText(quiz.get("1"));
        answerBtn2.setText(quiz.get("2"));
        answerBtn3.setText(quiz.get("3"));
        answerBtn4.setText(quiz.get("4"));
        String statement = quiz.get("statement");
        pepperSay(statement);
    }

    // btnが押されたらcheckAnswerが呼ばれる
    public void checkAnswer(View view) {
        if(correctSound == 0 || wrongSound == 0) return;

        Button answerBtn = findViewById(view.getId());
        String btnText = answerBtn.getText().toString();

        // 答えと説明
        String explanation = quiz.get("explanation");
        String correct = quiz.get("correct");
        Log.d("debug","correct" + correct);
        String Message;
        if (btnText.equals(correct)) {
            soundPool.play(correctSound,1.0f, 1.0f, 0, 0, 1);
            Message = "正解!";
            correctCount++;
        } else {
            soundPool.play(wrongSound,1.0f, 1.0f, 0, 0, 1);
            Message = "不正解…";
        }

        pepperSay(Message + explanation);

        // ダイアログを表示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(explanation);
        builder.setTitle(Message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // まだ表示できるクイズがあるなら
                if (quizCount < QUIZ_COUNT-1) {
                    quizCount++;
                    showNextQuiz();
                }
                // 最後は集計画面が作れるといいかも
                else {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void pepperSay(String message) {
        if(qiContext == null) return;
        // 前の発話があったらキャンセルする
        if(future != null) future.requestCancellation();

        future = SayBuilder.with(qiContext)
                .withText(message).buildAsync()
                .andThenCompose(say -> say.async().run());

    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        Log.d("debug","onRobotFocus");
        // RobotFocusを取らないと喋らないのでここで
        //　しかしsetTextはUIスレッド上でないといけないので
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Handlerを使用してメイン(UI)スレッドに処理を依頼する
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showNextQuiz();
                    }
                });
            }
        }).start();

    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}
