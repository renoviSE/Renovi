package com.example.renovi.viewmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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
    private static boolean firstButton = true;
    private static int lastButtonId = -1;
    private int  textViewId;

    public ButtonCreator(Context context) {

        this.context = context;
    }

    public TextView createUpcomingSectionTitle(ConstraintLayout layout, int title, int topConstraint) {
        TextView textView = new TextView(context);
        textView.setId(R.id.upcomingRenovationsTitle);
        textView.setText(title);
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
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, topConstraint, ConstraintSet.BOTTOM, dpToPx(context, 36));
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));
        constraintSet.applyTo(layout);

        textViewId = textView.getId();

        return textView;
    }


    public Button createButton(ConstraintLayout layout, String objectName, int scrollSpacerId) {

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
        renovierungButton.setBackgroundResource(R.drawable.bg_white_round_corner);
        renovierungButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
        renovierungButton.setElevation(dpToPx(context, 5));
        renovierungButton.setPaddingRelative(
                dpToPx(context, 20), // start
                0, // top
                dpToPx(context, 20), // end
                0  // bottom
        );

        renovierungButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        configureButtonBasedOnName(renovierungButton, "il_", objectName, true);

        // Button zum Layout hinzufügen
        layout.addView(renovierungButton);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Wenn es der erste Button ist, wird er unter R.id.upcomingRenovationsTitle gesetzt
        // Ansonsten wird er unter den zuletzt hinzugefügten Button gesetzt
        if (firstButton) {
            constraintSet.connect(renovierungButton.getId(), ConstraintSet.TOP, textViewId, ConstraintSet.BOTTOM, dpToPx(context, 24));
            firstButton = false;
        } else {
            constraintSet.connect(renovierungButton.getId(), ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, dpToPx(context, 4));
        }
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));

        constraintSet.connect(scrollSpacerId, ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, 0);

        constraintSet.applyTo(layout);

        // Aktualisieren der ID des zuletzt hinzugefügten Buttons
        lastButtonId = renovierungButton.getId();

        return renovierungButton;
    }

    public Button createColoredButton(ConstraintLayout layout, String objectName, int color, int scrollSpacerId) {

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
        renovierungButton.setBackgroundResource(R.drawable.bg_white_round_corner);
        renovierungButton.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
        renovierungButton.setElevation(dpToPx(context, 5));
        renovierungButton.setPaddingRelative(
                dpToPx(context, 20), // start
                0, // top
                dpToPx(context, 20), // end
                0  // bottom
        );

        renovierungButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        configureButtonBasedOnName(renovierungButton, "il_", objectName, true);

        // Button zum Layout hinzufügen
        layout.addView(renovierungButton);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Wenn es der erste Button ist, wird er unter R.id.upcomingRenovationsTitle gesetzt
        // Ansonsten wird er unter den zuletzt hinzugefügten Button gesetzt
        if (firstButton) {
            constraintSet.connect(renovierungButton.getId(), ConstraintSet.TOP, textViewId, ConstraintSet.BOTTOM, dpToPx(context, 24));
            firstButton = false;
        } else {
            constraintSet.connect(renovierungButton.getId(), ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, dpToPx(context, 4));
        }
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(renovierungButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));

        constraintSet.connect(scrollSpacerId, ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, 0);

        constraintSet.applyTo(layout);

        // Aktualisieren der ID des zuletzt hinzugefügten Buttons
        lastButtonId = renovierungButton.getId();

        return renovierungButton;
    }

    public int createBenefitButton(ConstraintLayout layout, String benefitName, int lastButtonId, boolean isFirst) {
        // Erstellen des Buttons
        Button benefitButton = new Button(context);
        benefitButton.setId(View.generateViewId());

        // Setzen der Layout-Parameter
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // Breite passt sich dem Inhalt an
                dpToPx(context, 48) // Höhe
        );
        benefitButton.setLayoutParams(params);

        // Hintergrund und Texteigenschaften setzen
        benefitButton.setBackgroundResource(R.drawable.bg_white_round_corner);
        benefitButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.whiteBlue));
        benefitButton.setElevation(dpToPx(context, 5));
        benefitButton.setPaddingRelative(
                dpToPx(context, 10), // start
                0, // top
                dpToPx(context, 10), // end
                0  // bottom
        );

        // Drawable Padding setzen
        benefitButton.setCompoundDrawablePadding(dpToPx(context, 8));

        benefitButton.setGravity(Gravity.CENTER);


        // Textgröße setzen
        benefitButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        // Setzen von Text und Icon auf der linken Seite
        configureButtonBasedOnName(benefitButton, "il_", benefitName, false);

        // Button zum Layout hinzufügen
        layout.addView(benefitButton);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        if (isFirst) {
            constraintSet.connect(benefitButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 32));
        } else {
            constraintSet.connect(benefitButton.getId(), ConstraintSet.START, lastButtonId, ConstraintSet.END, dpToPx(context, 16));
        }

        // Setzen der oberen und unteren Constraint, sowie des Bottom-Margin
        constraintSet.connect(benefitButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(benefitButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(context, 8));

        constraintSet.applyTo(layout);

        return benefitButton.getId(); // Gibt die ID des letzten Buttons zurück, um sie als Referenz für den nächsten Button zu verwenden
    }

    public void configureButtonBasedOnName(Button button, String type, String buttonName, Boolean nextButton) {
        // Umwandlung von Umlauten, Konvertierung in Kleinbuchstaben und Ersetzen von Leerzeichen durch Unterstriche
        String normalizedButtonName = normalizeString(buttonName.toLowerCase().replace(" ", "_"));

        // Generieren des Ressourcen-Identifikators für das Bild
        String imageName = type + normalizedButtonName; // "il_" = illustration, "ic_" = icon ... z.B.: "il_mieter_hinzufuegen"
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        // Generieren des Ressourcen-Identifikators für den Titel
        String titleName = normalizedButtonName + "_button_title"; // z.B. "mieter_hinzufuegen_button_title"
        int titleResId = context.getResources().getIdentifier(titleName, "string", context.getPackageName());

        if (imageResId != 0) {
            Drawable drawableStart = ContextCompat.getDrawable(context, imageResId);
            if (nextButton) {
                Drawable drawableEnd = ContextCompat.getDrawable(context, R.drawable.il_next);
                button.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, drawableEnd, null);
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null);
            }
        }

        if (titleResId != 0) {
            button.setText(context.getString(titleResId));
        } else {
            button.setText(buttonName);
        }

        button.setAllCaps(false);
        button.setTextColor(ContextCompat.getColor(context, R.color.gray1));
    }

    public TextView createPlaceholderView(ConstraintLayout layout, int title, int placeholderMessage) {

        TextView textView = new TextView(context);
        textView.setId(R.id.noRenovations);
        textView.setText(placeholderMessage);  // Text aus den Ressourcen
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray4)); // Hintergrundfarbe
        textView.setGravity(Gravity.CENTER);  // Text zentrieren

        // Hintergrund mit abgerundeten Ecken
        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_white_round_corner);
        DrawableCompat.setTint(background, ContextCompat.getColor(context, R.color.gray4));
        textView.setBackground(background);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,dpToPx(context, 50)
        );
        textView.setLayoutParams(layoutParams);

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(textView.getId(), ConstraintSet.TOP, title, ConstraintSet.BOTTOM, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));

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

    public boolean getFirstButton() { return firstButton; };
    public static void setFirstButton(boolean state) { firstButton = state; }

}