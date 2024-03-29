package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.viewmodel.RenterSession;
import com.example.renovi.model.Renter;

public class ProfileActivity extends AppCompatActivity {

    TextView profileFirstName;
    TextView profileLastName;
    TextView profileVerifyId;
    String userId;
    private static final String TAG = ProfileActivity.class.getSimpleName(); //getSimpleName() gibt einfachen Namen der Klasse als String, ohne jeglichen Paketnamen, zurück, mit der die Instanz assoziiert ist.

    private RenterSession renterSession;
    private Renter renter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getRenterFromSession();

        profileFirstName = findViewById(R.id.profileFirstName);
        profileLastName = findViewById(R.id.profileLastName);
        profileVerifyId = findViewById(R.id.profileVerifyId);

        initializeBackToMainButton();
        initializeCopyButton();
        initializeInboxButton();
        initializeFaqButton();
        setUserProfile();

    }

    private void getRenterFromSession() {
        renterSession = RenterSession.getInstance(this);
        renter = renterSession.getRenter();
    }

    private void initializeCopyButton() {
        TextView profileVerifyId = findViewById(R.id.profileVerifyId);
        Button copyVerifyIdButton = findViewById(R.id.copyVerifyIdButton);
        copyVerifyIdButton.setOnClickListener(new View.OnClickListener() { //This Button copies the string from "profileVerifyId"
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); //make clipboard
                ClipData clip = ClipData.newPlainText("Verify ID", profileVerifyId.getText().toString()); //get text
                clipboard.setPrimaryClip(clip); //copy text

                Toast.makeText(ProfileActivity.this, "ID erfolgreich kopiert", Toast.LENGTH_SHORT).show(); //shows success message
            }
        });
    }

    private void initializeBackToMainButton() {
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(view -> switchToMain());
    }
    private void switchToMain() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
        overridePendingTransition(0,0); //disables animation
    }

    private void initializeInboxButton() {
        Button notificationButtonNavBar = findViewById(R.id.notificationButtonNavBar);
        notificationButtonNavBar.setOnClickListener(view -> switchToInbox());
    }
    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        startActivity(switchActivityIntent);
    }

    private void initializeFaqButton() {
        Button faqButton = findViewById(R.id.faqButton);
        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("https://funktionales-kostensplitting.de");
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

    private void setUserProfile() {
        profileFirstName.setText(renter.getFirstName());
        profileLastName.setText(renter.getLastName());
        profileVerifyId.setText(renter.getId());

    }
}