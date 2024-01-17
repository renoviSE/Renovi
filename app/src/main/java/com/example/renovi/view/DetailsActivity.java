package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Renovierung;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailsActivity extends AppCompatActivity {
    private String id;
    private String kosten;
    private String nachteile;
    private String object;
    private String vorteile;
    private String paragraph;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initializeBackToMainButton();
        initializeReadMoreLink();

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        Renovierung renovierung = (Renovierung) intent.getSerializableExtra("renovierung");
        String geplanteRenovierung = intent.getStringExtra(MainActivityTest.geplanteRenovierung);
        System.out.println("suchmich" + geplanteRenovierung);

        //getRenovierungsDaten(db, geplanteRenovierung);
        TextView kostenTextview = findViewById(R.id.kosten);
        TextView renovierungTextView = findViewById(R.id.detailsTitle);
        TextView paragraphenTextView = findViewById(R.id.descriptionString);




        renovierungTextView.setText(renovierung.getObject());
        kostenTextview.setText(renovierung.getCost() + "€" );
        paragraphenTextView.setText(renovierung.getParagraph());
        //kostenTextview.setText(kosten);
        kostenTextview.setTextColor(ContextCompat.getColor(this, R.color.black));






    }

    private void initializeBackToMainButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToMain());
    }

    private void switchToMain() {
        Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
        startActivity(switchActivityIntent);
    }

    private void initializeReadMoreLink() { //reade more redirects to website
        TextView descriptionString = findViewById(R.id.descriptionString);
        descriptionString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("https://www.gesetze-im-internet.de/bgb/__555b.html");
            }
        });
    }
    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Keine Anwendung gefunden, um diese URL zu öffnen", Toast.LENGTH_SHORT).show();
        }
    }

}