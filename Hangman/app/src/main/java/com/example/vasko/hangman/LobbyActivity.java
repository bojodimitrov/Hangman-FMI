package com.example.vasko.hangman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class LobbyActivity extends AppCompatActivity {

    private GridView letters;
    private LetterAdapter ltrAdapt;

    private String word;
    private Random rand;
    private LinearLayout wordLayout;
    private TextView[] charViews;

    private ImageView[] bodyParts;
    private int bodyPartsNum = 6;
    private int currentPart = 0;
    private int wordCharsNum;
    private int correctCharsNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        bodyParts = new ImageView[bodyPartsNum];

        bodyParts[0] = (ImageView) findViewById(R.id.head);
        bodyParts[1] = (ImageView) findViewById(R.id.body);
        bodyParts[2] = (ImageView) findViewById(R.id.arm1);
        bodyParts[3] = (ImageView) findViewById(R.id.arm2);
        bodyParts[4] = (ImageView) findViewById(R.id.leg1);
        bodyParts[5] = (ImageView) findViewById(R.id.leg2);

        for (int i = 0; i < 6; i++) {
            bodyParts[i].setVisibility(View.INVISIBLE);
        }

        Resources res = getResources();

        URL url = null;
        try {
            url = new URL("http://10.110.201.85:8080/hangman/getWord");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WordHandler wordHandler = new WordHandler(url);

        Object[] ObjectTmp = new Object[1];
        AsyncTask taskResult = wordHandler.execute(ObjectTmp);
        try {
            Object result = taskResult.get();
            word = result.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        wordLayout = (LinearLayout)findViewById(R.id.word);

        letters = (GridView)findViewById(R.id.letters);
        ltrAdapt=new LetterAdapter(this);
        letters.setAdapter(ltrAdapt);

        playGame();
    }

    private void playGame() {
        String newWord = word;

        charViews = new TextView[word.length()];
        wordLayout.removeAllViews();

        for (int c = 0; c < word.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + word.charAt(c));

            charViews[c].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            charViews[c].setGravity(Gravity.CENTER);
            charViews[c].setTextColor(Color.WHITE);
            charViews[c].setBackgroundResource(R.drawable.letter_bg);
            wordLayout.addView(charViews[c]);

            wordCharsNum = word.length();
        }
    }

    public void letterPressed(View view) {
        String ltr=((TextView)view).getText().toString();
        char letterChar = ltr.charAt(0);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.letter_down);

        boolean correct = false;
        for(int k = 0; k < word.length(); k++) {
            if(word.charAt(k)==letterChar){
                correct = true;
                correctCharsNum++;
                charViews[k].setTextColor(Color.BLACK);
            }
        }

        if (correct) {
            if (correctCharsNum == wordCharsNum) {
                disableBtns();

                AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
                winBuild.setTitle("YAY");
                winBuild.setMessage("You win!\n\nThe answer was:\n\n"+word);

                winBuild.setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LobbyActivity.this.finish();
                            }});

                winBuild.show();
            }
        } else if (currentPart < bodyPartsNum) {
            bodyParts[currentPart].setVisibility(View.VISIBLE);
            currentPart++;
        } else {
            disableBtns();

            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
            loseBuild.setTitle("OOPS");
            loseBuild.setMessage("You lose!\n\nThe answer was:\n\n"+word);

            loseBuild.setNegativeButton("Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LobbyActivity.this.finish();
                        }});

            loseBuild.show();
        }
    }

    public void disableBtns() {
        int numLetters = letters.getChildCount();
        for (int l = 0; l < numLetters; l++) {
            letters.getChildAt(l).setEnabled(false);
        }
    }
}
