package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Renovierung;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.net.Uri;
public class MainActivityTest extends AppCompatActivity {
    private ProgressBar rentProgressBar;
    private TextView rentCostPercentage;
    private ProgressBar co2ProgressBar;
    private Button lastButton;
    private String userId;
    final String TAG = "myTag";
    public static final String geplanteRenovierung ="com.exemple.renovi";

    private ScrollView mainScrollView;
    private TextView upcomingRenovationsTitle;

    TextView mietepreisTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getRenovierungen(db);

        setContentView(R.layout.activity_main_test);

        userId = getIntent().getStringExtra("userId"); // entnimmt die übergebene user id von der LoginActivity

        initializeButtons();
        declareViews();
        initializeViews();
    }

    private void initializeButtons() {
        initializeInboxButton();
        initializeProfileButton();
        initializeMainButton();
        initializeFaqButton();
    }

    private void declareViews() {
        rentProgressBar = (ProgressBar) findViewById(R.id.rentCostProgressBar);
        rentCostPercentage = (TextView) findViewById(R.id.rentCostPercentage);
        co2ProgressBar = (ProgressBar) findViewById(R.id.co2ProgressBar);
    }

    private void initializeViews() {
        int rentCurrentProgress = 20;
        String rentCurrentCostPercentage = String.format("%d%%", rentCurrentProgress);
        rentCostPercentage.setText(rentCurrentCostPercentage);
        rentProgressBar.setProgress(rentCurrentProgress);
        rentProgressBar.setMax(100);


        int co2CurrentProgress = 75;
        co2ProgressBar.setProgress(co2CurrentProgress);
        co2ProgressBar.setMax(100);

    }

    private void getRenovierungen(FirebaseFirestore db) {
        Log.i(TAG, "We´re trying");
        db.collection("Renovierung")
                // document = eingeloggter User!
                .whereEqualTo("mieter", db.collection("mieter").document("W6vJ0B7y1ahLHynv02AD"))
                .get()
                .addOnSuccessListener(documents -> {
                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectValue = document.getString("object");
                            Renovierung renovierung = new Renovierung(document.getString("object"), document.getString("vorteile"), document.getString("nachteile"), document.getString("kosten"), document.getString("paragraph"));
                            // Erstelle einen Button für jeden Mieter
                            generateButtonForRenter(objectValue,buttonId, renovierung);
                            buttonId+=1;
                            Log.i(TAG, "Good Job");
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.i(TAG, "NO");

                });
    }

    private void initializeOverviewButton() {
        Button startButton = findViewById(R.id.mailButton);
        //startButton.setOnClickListener(view -> switchToDetails("1"));
    }

    private void switchToDetails(Renovierung renovierung) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovierung);
        startActivity(switchActivityIntent);
    }



    private void initializeInboxButton() {
        Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(view -> switchToInbox());
        Button notificationButtonNavBar = findViewById(R.id.notificationButtonNavBar);
        notificationButtonNavBar.setOnClickListener(view -> switchToInbox());
    }
    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        startActivity(switchActivityIntent);
    }

    private void initializeProfileButton() {
        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(view -> switchToProfile(userId));
    }
    private void switchToProfile(String userId) {
        Intent switchActivityIntent = new Intent(this, ProfileActivity.class);
        switchActivityIntent.putExtra("userId", userId); // übergibt die userId an ProfileActivity

        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0); //disables animation
        startActivity(switchActivityIntent);
    }

    private void initializeMainButton() { //main button scrolls down to the TextView "upcomingRenovationsTitle"
        Button mainButton = findViewById(R.id.navBarButton);
        mainScrollView = findViewById(R.id.scrollView2);
        mietepreisTitle = findViewById(R.id.mietepreisTitle);
        mainButton.setOnClickListener(view -> scrollToTextView());
    }
    private void scrollToTextView() {
        mainScrollView.post(() -> {
            mainScrollView.smoothScrollTo(0, mietepreisTitle.getTop()); // smoothScrollTo(horizontalScroll, verticalScroll)
        });
    }

    private void initializeFaqButton() {
        Button faqButton = findViewById(R.id.faqButton);
        faqButton.setOnClickListener(view -> openWebPage("https://funktionales-kostensplitting.de"));
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


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void generateButtonForRenter(String renovationTitle, int buttonId, Renovierung renovierung) {

        if (renovationTitle != null) {
            Button renoButton = new Button(this);
            renoButton.setOnClickListener(view -> switchToDetails(renovierung)); //hier renoId eigentlich

            setButtonValues(renovationTitle, buttonId, renoButton);

            ImageView arrow = new ImageView(this);
            setImageValues(arrow);
            setImageConstraints(arrow, renoButton);

            if(renovationTitle.contains("Tür")){
                //ImageView door = new ImageView(this);
                //setDoorValues(door);
                //setImageConstraints(door, renoButton, 300);
            } else if (renovationTitle.contains("Fenster")) {

            }

            setButtonConstraints(buttonId, renoButton);

            lastButton = renoButton;
        }
    }

    private void setDoorValues(ImageView door) {
        int doorId = View.generateViewId();
        door.setId(doorId);

        door.setBackgroundResource(R.drawable.il_tuer);
        door.setMaxHeight(dpToPx(38));
        door.setMaxWidth(dpToPx(42));
        door.setElevation(6);
    }

    private static void setImageValues(ImageView arrow) {
        int arrowId = View.generateViewId();
        arrow.setId(arrowId);

        arrow.setPadding(0, 0, 0, 0);
        arrow.setBackgroundResource(R.drawable.il_next);
        arrow.setElevation(6);
    }

    private void setImageConstraints(ImageView arrow, Button renoButton) {
        ConstraintLayout constraintLayout = findViewById(R.id.inner_constraint);
        constraintLayout.addView(arrow);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(arrow.getId(), ConstraintSet.END, renoButton.getId(), ConstraintSet.END);
        //constraintSet.connect(arrow.getId(), ConstraintSet.START, renoButton.getId(), ConstraintSet.START);
        constraintSet.setMargin(arrow.getId(), ConstraintSet.END, dpToPx(30));
        
        constraintSet.connect(arrow.getId(),ConstraintSet.BOTTOM,renoButton.getId(),ConstraintSet.BOTTOM);
        constraintSet.connect(arrow.getId(),ConstraintSet.TOP,renoButton.getId(),ConstraintSet.TOP);

        constraintSet.applyTo(constraintLayout);
    }

    private void setButtonConstraints(int buttonId, Button renoButton) {
        ConstraintLayout constraintLayout = findViewById(R.id.inner_constraint);
        constraintLayout.addView(renoButton);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (buttonId == 1) {
            constraintSet.connect(renoButton.getId(), ConstraintSet.TOP, R.id.upcomingRenovationsTitle, ConstraintSet.BOTTOM);
        }else{
            constraintSet.connect(renoButton.getId(), ConstraintSet.TOP, lastButton.getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(lastButton.getId(), ConstraintSet.BOTTOM, renoButton.getId(), ConstraintSet.TOP);
        }
        constraintSet.connect(renoButton.getId(), ConstraintSet.END, R.id.verbrauchssenkungTitle, ConstraintSet.END);
        constraintSet.connect(renoButton.getId(), ConstraintSet.START, R.id.verbrauchssenkungTitle, ConstraintSet.START);
        constraintSet.connect(R.id.verbrauchssenkungTitle,ConstraintSet.TOP, renoButton.getId(),ConstraintSet.BOTTOM);
        constraintSet.setMargin(renoButton.getId(), ConstraintSet.TOP, dpToPx(16));


        constraintSet.connect(renoButton.getId(),ConstraintSet.BOTTOM,R.id.verbrauchssenkungTitle,ConstraintSet.TOP);

        constraintSet.applyTo(constraintLayout);
    }

    private void setButtonValues(String renovationTitle, int buttonId, Button renoButton) {
        renoButton.setText(renovationTitle);
        renoButton.setId(buttonId);
        renoButton.setWidth(dpToPx(340));
        renoButton.setHeight(dpToPx(85));
        renoButton.setPadding(0, 0, 0, 0);
        renoButton.setTextSize(14);
        renoButton.setTypeface(null, Typeface.BOLD);
        renoButton.setTextColor(ContextCompat.getColor(this, R.color.gray4));
        renoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lightblue));
    }
}