package com.mirkocordes.aberhm_dastrinkspiel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ListeningActivity extends AppCompatActivity {

    Button startListeningButton;
    Button stopListeningButton;
    ImageButton editImageButton;


    TextView outputTextView;
    TextView bannedWordTextView1;

    ImageView hearingImageView;

    private static String bannedWord;

    private SpeechRecognizer speechRecognizer;
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private boolean isStarted;
    private static boolean starteNeu;
    private boolean notify;
    private static boolean appGeschlossen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Intent intent = getIntent();
        bannedWord = intent.getStringExtra("bannedWord");

        startListeningButton = findViewById(R.id.startListeningButton);
        stopListeningButton = findViewById(R.id.stopListeningButton);

        outputTextView = findViewById(R.id.outputTextView);
        bannedWordTextView1 = findViewById(R.id.bannedWordTextView1);
        hearingImageView = findViewById(R.id.hearingImageView);
        editImageButton = findViewById(R.id.editImageButton);

        bannedWordTextView1.setText(new StringBuilder().append("\"").append(bannedWord).append("\"").toString());
        isStarted = false;

        checkPermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new listener());

        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startRecognizing();
            }
        });


        stopListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStarted = false;
                disableHearing(true);
                speechRecognizer.stopListening();
                outputTextView.setText("");
            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(starteNeu){
            starteNeu = false;
            startRecognizing();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(notify && appGeschlossen){
            notify = false;
            appGeschlossen = false;
            notification();

        }
    }

    private void notification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Drink now", "Drink now", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Drink now")
                .setSmallIcon(R.drawable.ic_baseline_sports_bar_24)
                .setContentTitle(new StringBuilder().append("\"").append(bannedWord).append("\"").append(" wurde gesagt").toString())
                .setContentText("Das Wort wurde gesagt!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Jetzt App öffnen und einen Schluck trinken!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(R.attr.colorPrimary)
                .setColorized(true)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, PopUp.class), PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, builder.build());
    }

    public static void setStarteNeu(boolean starteNeu) {
        ListeningActivity.starteNeu = starteNeu;
    }

    public static void setAppGeschlossen(boolean appGeschlossen) {
        ListeningActivity.appGeschlossen = appGeschlossen;
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            outputTextView.setHint("Listening...");
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
            startListeningAgain();
        }
        public void onResults(Bundle results)
        {
            outputTextView.setText("");
            startListeningAgain();
        }
        public void onPartialResults(Bundle partialResults)
        {
            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String word = (String) data.get(data.size() - 1);

            String str[] = word.split(" ");
            ArrayList<String> al = new ArrayList<String>();

            for(int i= 0; i<str.length; i++){
                al.add(str[i]);
                outputTextView.setText(al.get(0));
                if(al.get(0).toUpperCase(Locale.ROOT).equals(bannedWord.toUpperCase(Locale.ROOT)) && isStarted){
                    isStarted = false;
                    speechRecognizer.stopListening();
                    outputTextView.setText(al.get(0));

                    startActivity(new Intent(ListeningActivity.this, PopUp.class));
                    notify = true;
                    stopListeningButton.setVisibility(View.INVISIBLE);
                    startListeningButton.setVisibility(View.INVISIBLE);
                }
                if(al.size() > 0)
                    al.remove(0);

            }

        }

        public void onEvent(int eventType, Bundle params)
        {
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            disableAllObjects(true);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, 10);

            disableAllObjects(false);
            disableHearing(true);
        }
    }

    private void disableHearing(Boolean b){
        if(b){
            startListeningButton.setVisibility(View.VISIBLE);
            stopListeningButton.setVisibility(View.INVISIBLE);
            outputTextView.setText("");
            hearingImageView.setImageResource(R.drawable.ic_baseline_hearing_disabled_24);
            //drinkConstraintLayout.setVisibility(View.INVISIBLE);
        }else {
            startListeningButton.setVisibility(View.INVISIBLE);
            stopListeningButton.setVisibility(View.VISIBLE);
            hearingImageView.setImageResource(R.drawable.ic_baseline_hearing_24);
        }
    }

    private void disableAllObjects(Boolean b){
        if(b){
            startListeningButton.setVisibility(View.INVISIBLE);
            stopListeningButton.setVisibility(View.INVISIBLE);
            //startAgainImageButton.setVisibility(View.INVISIBLE);
            outputTextView.setVisibility(View.INVISIBLE);
            bannedWordTextView1.setVisibility(View.INVISIBLE);
            //bannedWordTextView2.setVisibility(View.INVISIBLE);
            hearingImageView.setVisibility(View.INVISIBLE);
            //drinkConstraintLayout.setVisibility(View.INVISIBLE);
        } else {
            startListeningButton.setVisibility(View.VISIBLE);
            //startAgainImageButton.setVisibility(View.VISIBLE);
            outputTextView.setVisibility(View.VISIBLE);
            bannedWordTextView1.setVisibility(View.VISIBLE);
            //bannedWordTextView2.setVisibility(View.VISIBLE);
            hearingImageView.setVisibility(View.VISIBLE);
        }
    }


    private void startRecognizing(){
        isStarted = true;
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //intent.putExtra(RecognizerIntent.EXTRA_RESULTS, true);

        disableHearing(false);
        speechRecognizer.startListening(intent);
    }

    private void startListeningAgain(){
        if(isStarted){
            speechRecognizer.startListening(intent);
        } else {
            speechRecognizer.stopListening();
        }

    }

    public static String getBannedWord() {
        return bannedWord;
    }
}