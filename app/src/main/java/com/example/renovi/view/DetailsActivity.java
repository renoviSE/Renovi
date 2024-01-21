package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Renovation;
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
        Renovation renovation = (Renovation) intent.getSerializableExtra("renovierung");
        String geplanteRenovierung = intent.getStringExtra(MainActivity.geplanteRenovierung);
        System.out.println("suchmich" + geplanteRenovierung);

        //getRenovierungsDaten(db, geplanteRenovierung);
        TextView kostenTextview = findViewById(R.id.kosten);
        TextView renovierungTextView = findViewById(R.id.detailsTitle);
        TextView paragraphenTextView = findViewById(R.id.descriptionString);




        renovierungTextView.setText(renovation.getObject());
        kostenTextview.setText(renovation.getCost() + "€" );
        paragraphenTextView.setText(renovation.getParagraph());
        //kostenTextview.setText(kosten);
        kostenTextview.setTextColor(ContextCompat.getColor(this, R.color.black));


        configureImageViewBasedOnName(renovation.getObject());
    }

    private void initializeBackToMainButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToMain());
    }
    private void switchToMain() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen zurück
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

    public void configureImageViewBasedOnName(String imageName) {
        ImageView imageView = findViewById(R.id.objectIllustration);

        // Umwandlung von Umlauten und Konvertierung in Kleinbuchstaben
        String normalizedImageName = normalizeString(imageName.toLowerCase());

        // Generieren des Ressourcen-Identifikators für das Bild
        String resourceImageName = "il_" + normalizedImageName; // z.B. "il_fenster"
        int imageResId = this.getResources().getIdentifier(resourceImageName, "drawable", this.getPackageName());

        if (imageResId != 0) {
            Drawable drawable = ContextCompat.getDrawable(this, imageResId);
            imageView.setBackground(drawable);
        }
    }
    private String normalizeString(String input) {
        return input.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace("ß", "ss");
    }
}