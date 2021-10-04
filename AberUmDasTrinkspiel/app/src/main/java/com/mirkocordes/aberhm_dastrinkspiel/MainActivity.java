package com.mirkocordes.aberhm_dastrinkspiel;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;


//AppCompatActivity
public class MainActivity extends AppCompatActivity{

    EditText bannedWordEditText;
    Button switchActivityButton;
    static String bannedWord;
    ImageButton hearingImageButton;
    private SpeechRecognizer speechRecognizer;
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannedWordEditText = findViewById(R.id.bannedWordEditText);
        switchActivityButton = findViewById(R.id.switchActivityButton);
        hearingImageButton = (ImageButton) findViewById(R.id.hearingImageButton1);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new MainActivity.listener());


        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openListeningActivity();
            }
        });

        hearingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
                //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                //intent.putExtra(RecognizerIntent.EXTRA_RESULTS, true);

                speechRecognizer.startListening(intent);
            }
        });

    }

    public void openListeningActivity() {
        if(bannedWordEditText.getText().toString().isEmpty()){
            Toast.makeText(MainActivity.this, "Bitte ein Wort eingeben!", Toast.LENGTH_LONG).show();
            bannedWordEditText.setText("");
        } else{
            if(bannedWordEditText.getText().toString().contains(" ")){
                Toast.makeText(MainActivity.this, "Bitte keine Leerzeichen!", Toast.LENGTH_LONG).show();
            } else {
                setBannedWord(bannedWordEditText.getText().toString());
                Intent intent = new Intent(this, ListeningActivity.class);
                intent.putExtra("bannedWord", bannedWord);
                startActivity(intent);
            }

        }


    }

    public String getBannedWord(){
        return bannedWordEditText.getText().toString();
    }

    private void setBannedWord(String b){
        bannedWord = b;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, 10);
        }
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            bannedWordEditText.setText("Listening...");
        }
        public void onBeginningOfSpeech()
        {
        }
        public void onRmsChanged(float rmsdB)
        {
        }
        public void onBufferReceived(byte[] buffer)
        {
        }
        public void onEndOfSpeech()
        {
        }
        public void onError(int error)
        {
            bannedWordEditText.setText("");
        }
        public void onResults(Bundle results)
        {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String word = (String) data.get(0);
            if(word.contains(" ")){
                Toast.makeText(MainActivity.this, "Bitte nur ein Wort eingeben!", Toast.LENGTH_LONG).show();
                bannedWordEditText.setText("");
            }else
                bannedWordEditText.setText(word);
        }
        public void onPartialResults(Bundle partialResults)
        {

        }

        public void onEvent(int eventType, Bundle params)
        {

        }
    }
}