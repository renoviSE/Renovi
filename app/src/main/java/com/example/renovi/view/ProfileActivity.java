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
import android.util.Log;
import com.example.renovi.viewmodel.FirestoreHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.renovi.model.Renter;

public class ProfileActivity extends AppCompatActivity {

    TextView profileFirstName;
    TextView profileLastName;
    TextView profileVerifyId;
    String userId;
    private static final String TAG = ProfileActivity.class.getSimpleName(); //getSimpleName() gibt einfachen Namen der Klasse als String, ohne jeglichen Paketnamen, zurück, mit der die Instanz assoziiert ist.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getStringExtra("userId");

        profileFirstName = findViewById(R.id.profileFirstName);
        profileLastName = findViewById(R.id.profileLastName);
        profileVerifyId = findViewById(R.id.profileVerifyId);

        initializeHomeButton();
        initializeCopyButton();
        initializeInboxButton();
        initializeFaqButton();
        setUserProfile();

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

    private void initializeHomeButton() {
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(view -> switchToHome());
    }
    private void switchToHome() {
        Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(switchActivityIntent);
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //String userId = "W6vJ0B7y1ahLHynv02AD"; // Die ID des Nutzers
        String collectionName = "Mieter";
        FirestoreHelper.getUserData(db, collectionName, userId, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                // Überprüfen Sie, ob das Dokument existiert und Felder hat
                if (document.exists() && document.getData() != null) {
                    profileFirstName.setText(document.getString("vorname"));
                    profileLastName.setText(document.getString("nachname"));
                    profileVerifyId.setText(document.getString("mieter_id"));
                } else {
                    Log.w(TAG, "Dokument existiert nicht.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Fehlerbehandlung, z.B. eine Fehlermeldung anzeigen
                Log.e(TAG, "Fehler beim Abrufen des Dokuments: ", e);
            }
        });
    }
}