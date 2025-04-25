package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.UI.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.viewmodel.UI.UIHelper;

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

        UIHelper.initializeViewFunction(this, R.id.homeButton, view -> switchToMain());
        UIHelper.initializeViewFunction(this, R.id.notificationButtonNavBar, view -> switchToInbox());
        UIHelper.initializeViewFunction(this, R.id.faqButton, view -> openWebPage("https://funktionales-kostensplitting.de"));
        UIHelper.initializeViewFunction(this, R.id.signOutButton, view -> switchToLogIn());
        UIHelper.initializeViewFunction(this, R.id.settingsButton, view -> switchToSettings());
        UIHelper.initializeViewFunction(this, R.id.privacyDataButton, view -> switchToPrivacyPolicy());
        UIHelper.initializeViewFunction(this, R.id.personalDataButton, view -> switchToPersonalData());
        initializeCopyButton();
        setUserProfile();
        initializeContactUsButton();
    }

    private void getRenterFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void initializeCopyButton() {
        Button copyVerifyIdButton = findViewById(R.id.copyVerifyIdButton);
        //This Button copies the string from "profileVerifyId"
        copyVerifyIdButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); //make clipboard
            ClipData clip = ClipData.newPlainText("Verify ID", profileVerifyId.getText().toString()); //get text
            clipboard.setPrimaryClip(clip); //copy text

            Toast.makeText(ProfileActivity.this, "ID erfolgreich kopiert", Toast.LENGTH_SHORT).show(); //shows success message
        });
    }

    private void switchToMain() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }
    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        startActivity(switchActivityIntent);
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

    private void switchToPrivacyPolicy() {
        Intent privacyIntent = new Intent(ProfileActivity.this, PrivacyPolicyActivity.class);
        startActivity(privacyIntent);
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
    private void switchToSettings() {
        Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void switchToPersonalData() {
        Intent settingsIntent = new Intent(ProfileActivity.this, PersonalDataActivity.class);
        startActivity(settingsIntent);
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