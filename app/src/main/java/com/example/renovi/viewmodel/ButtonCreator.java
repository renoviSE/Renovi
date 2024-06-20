package com.example.renovi.viewmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Button;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.renovi.R;

public class ButtonCreator {

    private Context context;
    private static int lastButtonId = R.id.upcomingRenovationsTitle; // Anfangswert auf die ID des ersten Referenz-Views setzen

    public ButtonCreator(Context context) {

        this.context = context;
    }

    public TextView createUpcomingRenovationsTitle(ConstraintLayout layout) {
        TextView textView = new TextView(context);
        textView.setId(R.id.upcomingRenovationsTitle);
        textView.setText(R.string.bevorstehende_renovierungen_title);
        textView.setTextColor(ContextCompat.getColor(context, R.color.black1));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);
        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.header_constraint, ConstraintSet.BOTTOM, dpToPx(context, 36));
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(layout);

        return textView;
    }


    public Button createButton(ConstraintLayout layout, String objectName) {
        // Erstellen des Titels
        createUpcomingRenovationsTitle(layout);

        // Erstellen des Buttons
        Button renovierungButton = new Button(context);
        renovierungButton.setId(View.generateViewId());

        // Setzen der Layout-Parameter
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // Breite entspricht der Breite des Parent-Layouts
                dpToPx(context, 85) // Höhe
        );
        renovierungButton.setLayoutParams(params);

        // Hintergrund und Texteigenschaften setzen
        renovierungButton.setBackgroundResource(R.drawable.bg_gray_round_corner);
        renovierungButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
        renovierungButton.setPaddingRelative(
                dpToPx(context, 20), // start
                0, // top
                dpToPx(context, 20), // end
                0  // bottom
        );

        configureButtonBasedOnName(renovierungButton, objectName);

        // Button zum Layout hinzufügen
        layout.addView(renovierungButton);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Wenn es der erste Button ist, wird er unter R.id.upcomingRenovationsTitle gesetzt
        // Ansonsten wird er unter den zuletzt hinzugefügten Button gesetzt
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, dpToPx(context, 4));
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(R.id.mietepreisTitle, ConstraintSet.TOP, renovierungButton.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(layout);

        // Aktualisieren der ID des zuletzt hinzugefügten Buttons
        lastButtonId = renovierungButton.getId();

        return renovierungButton;
    }

    public void configureButtonBasedOnName(Button button, String buttonName) {
        // Umwandlung von Umlauten und Konvertierung in Kleinbuchstaben
        String normalizedButtonName = normalizeString(buttonName.toLowerCase());

        // Generieren des Ressourcen-Identifikators für das Bild
        String imageName = "il_" + normalizedButtonName; // z.B. "il_fenster"
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        // Generieren des Ressourcen-Identifikators für den Titel
        String titleName = normalizedButtonName + "_button_title"; // z.B. "fenster_button_title"
        int titleResId = context.getResources().getIdentifier(titleName, "string", context.getPackageName());

        if (imageResId != 0) {
            Drawable drawableStart = ContextCompat.getDrawable(context, imageResId);
            Drawable drawableEnd = ContextCompat.getDrawable(context, R.drawable.il_next);
            button.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, drawableEnd, null);
        }

        if (titleResId != 0) {
            button.setText(context.getString(titleResId));
            button.setAllCaps(false);
            button.setTextColor(ContextCompat.getColor(context, R.color.gray1));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }
    }

    public TextView createNoRenovationsView(ConstraintLayout layout) {
        // Erstellen des Titels
        createUpcomingRenovationsTitle(layout);

        TextView textView = new TextView(context);
        textView.setId(R.id.noRenovations);
        textView.setText(R.string.kein_button_tittle);  // Text aus den Ressourcen
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray4)); // Hintergrundfarbe
        textView.setGravity(Gravity.CENTER);  // Text zentrieren

        // Hintergrund mit abgerundeten Ecken
        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_gray_round_corner);
        DrawableCompat.setTint(background, ContextCompat.getColor(context, R.color.gray4));
        textView.setBackground(background);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,dpToPx(context, 50)
        );
        textView.setLayoutParams(layoutParams);

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.upcomingRenovationsTitle, ConstraintSet.BOTTOM, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        constraintSet.applyTo(layout);

        lastButtonId = textView.getId();

        return textView;
    }

    public String normalizeString(String input) {
        return input.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace("ß", "ss");
    }

    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    public static int getLastButtonId() {
        return lastButtonId;
    }
}