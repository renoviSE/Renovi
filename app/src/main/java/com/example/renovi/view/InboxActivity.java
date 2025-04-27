package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;


import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.InboxViewModel;
import com.example.renovi.viewmodel.UI.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.viewmodel.UI.UIHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private Session session;
    private Person user;
    private List<Button> renterButtons = new ArrayList<>();

    private InboxViewModel viewModel = new InboxViewModel();


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

        initializeSearchRenter();

        UIHelper.initializeBackButton(this, R.id.inboxToPreviousButton);
        UIHelper.initializeViewFunction(this, R.id.addMessageButton, view -> switchToAddMessageActivity());
        loadRenter();
    }

    private void initializeSearchRenter() {
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
    }

    private void switchToAddMessageActivity() {
        Intent switchActivityIntent = new Intent(this, AddMessageActivity.class);
        startActivity(switchActivityIntent);
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void loadRenter() {
        viewModel.getRenters(user, new InboxViewModel.InboxCallback() {
            @Override
            public void onRenterList(LinkedHashMap<String, String> renterList) {
                for (Map.Entry<String, String> entry : renterList.entrySet()) {
                    generateButtonForRenter(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void onEmptyList() {
                generatePlaceholder();
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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


}

