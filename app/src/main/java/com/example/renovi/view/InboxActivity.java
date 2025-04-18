package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;


import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private List<Button> renterButtons = new ArrayList<>();




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();
        if(Objects.equals(user.getRole(), "Renter")){
            switchToChat(user.getId());
        }

        setContentView(R.layout.activity_inbox);
        mainLayout = findViewById(R.id.inboxConstraintLayout);

        EditText searchInput = findViewById(R.id.searchRenter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRenterButtons(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        initializeBackToPreviousActivityButton();
        initializeAddMessageButton();
        loadRenter();
    }

    private void initializeAddMessageButton() {
        Button addMessageButton = findViewById(R.id.addMessageButton);
        addMessageButton.setOnClickListener(view -> switchToAddMessageAcivity());
    }

    private void switchToAddMessageAcivity() {
        Intent switchActivityIntent = new Intent(this, AddMessageActivity.class);
        startActivity(switchActivityIntent);
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }
    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.chatToPreviousButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

    private void loadRenter() {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            generatePlaceholder();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firstName = document.getString("vorname");
                                String lastName = document.getString("nachname");
                                String fullName = firstName + " " + lastName;
                                String renterId = document.getId();
                                generateButtonForRenter(renterId, fullName);
                            }
                        }
                    } else {
                        generatePlaceholder();
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void generateButtonForRenter(String renterId, String fullname) {
        ButtonCreator buttonCreator = new ButtonCreator(this);
        Button renterButton = buttonCreator.createButton(mainLayout, fullname, R.id.renterListScrollSpacer);
        renterButton.setOnClickListener(v -> switchToChat(renterId));
        renterButtons.add(renterButton); // <-- hier zur Liste hinzufügen
    }

    private void filterRenterButtons(String query) {
        String lowerCaseQuery = query.toLowerCase();

        for (Button button : renterButtons) {
            String buttonText = button.getText().toString().toLowerCase();
            boolean matches = buttonText.contains(lowerCaseQuery);

            if (matches && button.getVisibility() != View.VISIBLE) {
                fadeIn(button);
            } else if (!matches && button.getVisibility() == View.VISIBLE) {
                fadeOut(button);
            }
        }
    }


    private void switchToChat(String renterId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("renterId", renterId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0); //disables animation
        startActivity(intent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        buttonCreator.createPlaceholderView(mainLayout, R.id.renterListTopConstraintForPlaceholder, R.string.no_renter_placeholder_message);

    }
    private void fadeIn(Button button) {
        button.setAlpha(0f);
        button.setVisibility(View.VISIBLE);
        button.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null);
    }

    private void fadeOut(Button button) {
        button.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> button.setVisibility(View.GONE));
    }

}

