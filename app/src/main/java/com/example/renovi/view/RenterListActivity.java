package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RenterListActivity extends AppCompatActivity {


    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private List<Button> renterButtons = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserFromSession();

        setContentView(R.layout.activity_renter_list);
        mainLayout = findViewById(R.id.renterListConstraintLayout);

        EditText searchInput = findViewById(R.id.verifyIdInput);
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
        loadRenter();
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
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

    private void initializeBackToPreviousActivityButton() {
        Button startButton = findViewById(R.id.chatToPreviousButton);
        startButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

    private void generateButtonForRenter(String renterId, String fullname) {
        ButtonCreator buttonCreator = new ButtonCreator(this);

        Button renterButton = buttonCreator.createButton(mainLayout, fullname, R.id.renterListScrollSpacer);
        renterButton.setOnClickListener(v -> switchToRenterRenovationList(renterId));
        renterButtons.add(renterButton);
    }

    private void switchToRenterRenovationList(String renterId) {
        Intent intent = new Intent(this, RenterRenovationsListActivity.class);
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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


}