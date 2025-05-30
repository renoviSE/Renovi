package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Landlord;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.MainViewModel;
import com.example.renovi.viewmodel.UI.BannerCreator;
import com.example.renovi.viewmodel.UI.ButtonCreator;
import com.example.renovi.model.Renovation;
import com.example.renovi.model.Renter;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.viewmodel.UI.UIHelper;

import android.net.Uri;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Person user;
    private Session session;
    private List<Pair<String, Class<?>>> menuItems;
    private ConstraintLayout mainLayout;
    private MainViewModel viewModel = new MainViewModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getUserFromSession();

        // Wait for the layout to be completely drawn
        mainLayout = findViewById(R.id.middle_constraint);

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent repeated calls
                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (user instanceof Renter) {
                    getRenovations();
                } else if (user instanceof Landlord) {
                    generateMenuButtons();
                }

                setUserNameAsHeadline();
                initializeButtons();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // soll nicht wieder ins Login
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void initializeButtons() {
        UIHelper.initializeViewFunction(this, R.id.notificationButton, view -> switchToInbox());
        UIHelper.initializeViewFunction(this, R.id.notificationButtonNavBar, view -> switchToInbox());
        UIHelper.initializeViewFunction(this, R.id.profileButton, view -> switchToProfile());
        UIHelper.initializeViewFunction(this, R.id.scrollButton, vview -> scrollToTextView());
        UIHelper.initializeViewFunction(this, R.id.faqButton, view -> openWebPage("https://funktionales-kostensplitting.de"));
    }

    private void getRenovations() {
        viewModel.getRenovations(user, new MainViewModel.MainCallback() {
            @Override
            public void onRenovationList(BigDecimal objectValue, LinkedHashMap<String, Renovation> renovationList) {

                for (Map.Entry<String, Renovation> entry : renovationList.entrySet()) {
                    generateButtonForRenter(entry.getKey(), entry.getValue());
                }

                ((Renter) user).updateRent(objectValue);
                ((Renter) user).setRentDifferenceInPercentage(objectValue);
                setRentCost();
            }

            @Override
            public void onEmptyList() {
                generatePlaceholder();
                setRentCost();
            }
        });
    }

    private void initializeMenuItems() {
        menuItems = new ArrayList<>();
        menuItems.add(new Pair<>("Mieter", RenterListActivity.class));
        menuItems.add(new Pair<>("Mieter hinzufügen", CreateRenterActivity.class));
        menuItems.add(new Pair<>("Renovierung hinzufügen", CreateRenovationActivity.class));
        menuItems.add(new Pair<>("Maßnahme hinzufügen", CreateRefurbishmentActivity.class));
    }

    public void generateMenuButtons() {
        initializeMenuItems();

        ButtonCreator buttonCreator = new ButtonCreator(this);
        buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.menu_title_string, R.id.header_constraint);

        for (Pair<String, Class<?>> menuItem : menuItems) {
            String objectName = menuItem.first;
            Class<?> destination = menuItem.second;

            Button menuButton = buttonCreator.createButton(mainLayout, objectName, R.id.mainScrollSpacer);

            Intent intent = new Intent(this, destination);
            menuButton.setOnClickListener(view -> startActivity(intent));
        }
    }

    private void setUserNameAsHeadline() {
        TextView userName = findViewById(R.id.userName);
        userName.setText(user.getFirstName() + " " + user.getLastName());
    }

    private void setRentCost() {
        generateRentBannerForRenter();

        TextView rentcostString = findViewById(R.id.mietpreisString);
        rentcostString.setText(((Renter) user).getRoundedRentasString() + "€");

        TextView bannerDescription = findViewById(R.id.bannerDescription);
        String formattedPercentage = String.format("%.0f%%", ((Renter) user).getRentDifferenceInPercentage());
        String bannerDescriptionText = getString(R.string.banner_description_string, formattedPercentage);
        bannerDescription.setText(bannerDescriptionText);


        ProgressBar rentCostProgressBar = findViewById(R.id.rentCostProgressBar);
        rentCostProgressBar.setVisibility(View.VISIBLE);
        rentCostProgressBar.setProgress(((Renter) user).getRentDifferenceInPercentage().intValue());
        rentCostProgressBar.setMax(100);
    }

    private void switchToDetails(Renovation renovation) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovation);
        startActivity(switchActivityIntent);
    }

    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        Intent directlyToChat = new Intent(this, ChatActivity.class);
        if (user.getRole() == "Landlord"){
            startActivity(switchActivityIntent);
        }else{
            startActivity(directlyToChat);
        }

    }
    private void switchToProfile() {
        Intent switchActivityIntent = new Intent(this, ProfileActivity.class);

        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0); //disables animation
        startActivity(switchActivityIntent);
    }

    private void scrollToTextView() {
        ScrollView mainScrollView = findViewById(R.id.scrollView2);

        mainScrollView.post(() -> {
            mainScrollView.smoothScrollTo(0, mainScrollView.getBottom()); // smoothScrollTo(horizontalScroll, verticalScroll)
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

    private void generateButtonForRenter(String renovationTitle, Renovation renovation) {
        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator  = new ButtonCreator(this);
            TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.bevorstehende_renovierungen_title, R.id.header_constraint);

            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle, R.id.mainScrollSpacer);
            renoButton.setOnClickListener(view -> switchToDetails(renovation)); //hier renoId eigentlich
        }
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.bevorstehende_renovierungen_title, R.id.header_constraint);
        int topConstraint = buttonsTitle.getId();

        buttonCreator.createPlaceholderView(mainLayout, topConstraint, R.string.no_renovations_placeholder_message);

    }

    private void generateRentBannerForRenter() {
        // Erstellen und Hinzufügen des Banners zum Layout
        BannerCreator bannerCreator  = new BannerCreator(this);
        bannerCreator.createBanner(mainLayout, R.id.mainScrollSpacer);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


}