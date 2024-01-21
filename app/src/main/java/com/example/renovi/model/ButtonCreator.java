package com.example.renovi.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;

public class ButtonCreator {

    private Context context;
    private static int lastButtonId = R.id.upcomingRenovationsTitle; // Anfangswert auf die ID des ersten Referenz-Views setzen
    public ButtonCreator(Context context) {
        this.context = context;
    }

    public Button createButton(ConstraintLayout layout, String objectName) {
        // Erstellen des Buttons
        Button tuerRenovierungButton = new Button(context);
        tuerRenovierungButton.setId(View.generateViewId());

        // Setzen der Layout-Parameter
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // Breite entspricht der Breite des Parent-Layouts
                (int) (85 * context.getResources().getDisplayMetrics().density) // Höhe
        );
        tuerRenovierungButton.setLayoutParams(params);

        // Hintergrund und Texteigenschaften setzen
        tuerRenovierungButton.setBackgroundResource(R.drawable.bg_gray_round_corner);
        tuerRenovierungButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
        tuerRenovierungButton.setPaddingRelative(
                (int) (20 * context.getResources().getDisplayMetrics().density), // start
                0, // top
                (int) (20 * context.getResources().getDisplayMetrics().density), // end
                0  // bottom
        );

        configureButtonBasedOnName(tuerRenovierungButton, objectName);

        //tuerRenovierungButton.setText(R.string.tuer_renovierung_button_title);


        // Drawable hinzufügen

        // Button zum Layout hinzufügen
        layout.addView(tuerRenovierungButton);

        // Constraints setzen
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        // Wenn es der erste Button ist, setzen Sie ihn unter R.id.upcomingRenovationsTitle
        // Ansonsten setzen Sie ihn unter den zuletzt hinzugefügten Button
        constraintSet.connect(tuerRenovierungButton.getId(), ConstraintSet.TOP, lastButtonId, ConstraintSet.BOTTOM, 16);

        constraintSet.connect(tuerRenovierungButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(tuerRenovierungButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        constraintSet.connect(R.id.mietepreisTitle, ConstraintSet.TOP, tuerRenovierungButton.getId(), ConstraintSet.BOTTOM, 36);

        constraintSet.applyTo(layout);

        // Aktualisieren der ID des zuletzt hinzugefügten Buttons
        lastButtonId = tuerRenovierungButton.getId();

        return tuerRenovierungButton;
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

    private String normalizeString(String input) {
        return input.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace("ß", "ss");
    }
}