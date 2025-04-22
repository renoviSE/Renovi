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
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;

public class ProfileActivity extends AppCompatActivity {

    TextView profileFirstName;
    TextView profileLastName;
    TextView profileVerifyId;
    String userId;
    private static final String TAG = ProfileActivity.class.getSimpleName(); //getSimpleName() gibt einfachen Namen der Klasse als String, ohne jeglichen Paketnamen, zurück, mit der die Instanz assoziiert ist.

    private Session session;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getRenterFromSession();

        profileFirstName = findViewById(R.id.profileFirstName);
        profileLastName = findViewById(R.id.profileLastName);
        profileVerifyId = findViewById(R.id.profileVerifyIdBackground);

        initializeBackToMainButton();
        initializeCopyButton();
        initializeInboxButton();
        initializeFaqButton();
        setUserProfile();
        initializeSignOutButton();
        initializeContactUsButton();
        initializeSettingsButton();
        initializePrivacyPolicyButton();
        initializePersonalDataButton();
    }

    private void getRenterFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void initializeCopyButton() {
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

    private void initializeSignOutButton() {
        Button notificationButtonNavBar = findViewById(R.id.signOutButton);
        notificationButtonNavBar.setOnClickListener(view -> switchToLogIn());
    }

    private void switchToLogIn() {
        // Lösche die gespeicherte Sitzung, wenn der Benutzer sich abmeldet
        session.deleteSession();

        // Setzt den FirstButton Zustand zurück, um beim nächsten Login eine Verschiebung der Buttons zu verhindern
        ButtonCreator.setFirstButton(true);

        // Erstelle einen neuen Intent für die LoginActivity
        Intent switchActivityIntent = new Intent(this, LogInActivity.class);
        // Setze die Flags, um den Backstack zu bereinigen
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Starte die LoginActivity
        startActivity(switchActivityIntent);

        // Beende die aktuelle Aktivität, um Ressourcen freizugeben und sicherzustellen,
        // dass der Benutzer nicht zur vorherigen Aktivität zurückkehren kann
        finish();
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

    private void initializePrivacyPolicyButton() {
        Button privacyDataButton = findViewById(R.id.privacyDataButton);
        privacyDataButton.setOnClickListener(view -> {
            // Startet eine Activity mit der Policy
            Intent privacyIntent = new Intent(ProfileActivity.this, PrivacyPolicyActivity.class);
            startActivity(privacyIntent);
                }
        );
    }

    private void initializeContactUsButton() {
        Button contactUsButton = findViewById(R.id.contactUsButton);
        contactUsButton.setOnClickListener(view -> {
            // Öffne ein E-Mail-Intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "support@renovi.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Anfrage");
            try {
                startActivity(Intent.createChooser(emailIntent, "E-Mail auswählen"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ProfileActivity.this, "Keine E-Mail-Anwendung gefunden", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeSettingsButton() {
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(view -> {
            // Startet eine neue Activity für die Einstellungen
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

    }private void initializePersonalDataButton() {
        Button settingsButton = findViewById(R.id.personalDataButton);
        settingsButton.setOnClickListener(view -> {
            // Startet eine neue Activity für die Einstellungen
            Intent settingsIntent = new Intent(ProfileActivity.this, PersonalDataActivity.class);
            startActivity(settingsIntent);
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
        profileFirstName.setText(user.getFirstName());
        profileLastName.setText(user.getLastName());
        profileVerifyId.setText(user.getId());

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}