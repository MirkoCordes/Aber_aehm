package com.mirkocordes.aberhm_dastrinkspiel;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;



public class PopUp extends AppCompatActivity {

    ImageButton startAgainImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.popup_window);

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        int width = ds.widthPixels;
        int height = ds.heightPixels;
        getWindow().setBackgroundDrawable(null);
        getWindow().setLayout((int) (width*.8), (int) (height*.6));

        startAgainImageButton = findViewById(R.id.startAgainImageButton);
        startAgainImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Close PopUp and start recognizing
                //startRecognizing();
                ListeningActivity.setStarteNeu(true);
                finish();
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();

        ListeningActivity.setAppGeschlossen(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListeningActivity.setAppGeschlossen(true);
    }
}
