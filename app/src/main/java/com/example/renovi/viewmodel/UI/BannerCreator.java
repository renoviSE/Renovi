package com.example.renovi.viewmodel.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.TypedValue;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;

public class BannerCreator {
    private Context context;

    public BannerCreator(Context context) {
        this.context = context;
    }

    public void createBanner(ConstraintLayout mainLayout, int scrollSpacerId) {
        createTextView(mainLayout);
        createBannerImageView(mainLayout, scrollSpacerId);
        createBannerTitle(mainLayout);
        createBannerDescription(mainLayout);
        createMieteBackground(mainLayout);
        createMietpreisString(mainLayout);
        createEclipseBackground(mainLayout);
    }

    public TextView createTextView(ConstraintLayout layout) {
        TextView textView = new TextView(context);
        textView.setId(R.id.mietepreisTitle);
        textView.setText(R.string.kosten_title_string);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(ContextCompat.getColor(context, R.color.black));
        textView.setTypeface(null, Typeface.BOLD);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // Breite
                ConstraintLayout.LayoutParams.WRAP_CONTENT  // Höhe
        );

        textView.setLayoutParams(layoutParams); // Layout-Parameter auf das TextView anwenden

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, ButtonCreator.getLastButtonId(), ConstraintSet.BOTTOM, dpToPx(context, 30));
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));
        constraintSet.applyTo(layout);

        return textView;
    }

    public ImageView createBannerImageView(ConstraintLayout layout, int scrollSpacerId) {
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.banner);

        // Hintergrundbild setzen
        imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.il_banner));

        // Layout-Parameter
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // Breite
                dpToPx(context, 180) // Höhe
        );

        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(dpToPx(context, 10), dpToPx(context, 10), dpToPx(context, 10), dpToPx(context, 10));

        layout.addView(imageView);

        // Constraints anwenden
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, R.id.mietepreisTitle, ConstraintSet.BOTTOM, dpToPx(context, 24));
        constraintSet.connect(imageView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(imageView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(context, 24));

        constraintSet.connect(scrollSpacerId, ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM, 0);

        constraintSet.applyTo(layout);

        return imageView;
    }

    public TextView createBannerTitle(ConstraintLayout layout) {
        TextView textView = new TextView(context);
        textView.setId(R.id.bannerTitle);
        textView.setText(R.string.banner_title_string);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray4));
        textView.setTypeface(null, Typeface.BOLD);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(textView.getId(), ConstraintSet.START, R.id.banner, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.banner, ConstraintSet.TOP, dpToPx(context, 32));
        constraintSet.applyTo(layout);

        return textView;
    }

    public TextView createBannerDescription(ConstraintLayout layout) {
        TextView textView = new TextView(context);
        textView.setId(R.id.bannerDescription);
        textView.setText(R.string.banner_description_string);
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray4));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(textView.getId(), ConstraintSet.START, R.id.banner, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.bannerTitle, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(layout);

        return textView;
    }

    public ImageView createMieteBackground(ConstraintLayout layout) {
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.mieteBackground);
        imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_miete));

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                dpToPx(context, 95),  // Breite
                dpToPx(context, 35)   // Höhe
        );
        imageView.setLayoutParams(layoutParams);

        layout.addView(imageView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(imageView.getId(), ConstraintSet.START, R.id.banner, ConstraintSet.START, dpToPx(context, 24));
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, R.id.bannerDescription, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, R.id.banner, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(layout);

        return imageView;
    }

    public TextView createMietpreisString(ConstraintLayout layout) {
        TextView textView = new TextView(context);
        textView.setId(R.id.mietpreisString);
        textView.setText(R.string.miete_price_string);
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray4));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setTypeface(null, Typeface.BOLD);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        textView.setLayoutParams(layoutParams);

        layout.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(textView.getId(), ConstraintSet.START, R.id.mieteBackground, ConstraintSet.START, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.END, R.id.mieteBackground, ConstraintSet.END, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, R.id.mieteBackground, ConstraintSet.TOP, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, R.id.mieteBackground, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(layout);

        return textView;
    }

    public ImageView createEclipseBackground(ConstraintLayout layout) {
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.eclipseBackground);
        imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_eclipse));
        imageView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray4));

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                dpToPx(context, 106),  // Breite
                dpToPx(context, 106)   // Höhe
        );

        imageView.setLayoutParams(layoutParams);

        layout.addView(imageView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(imageView.getId(), ConstraintSet.START, R.id.banner, ConstraintSet.START, 0);
        constraintSet.connect(imageView.getId(), ConstraintSet.END, R.id.banner, ConstraintSet.END, 0);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, R.id.banner, ConstraintSet.TOP, 0);
        constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, R.id.banner, ConstraintSet.BOTTOM, 0);
        constraintSet.setHorizontalBias(imageView.getId(), 0.85f); // Set horizontal bias
        constraintSet.applyTo(layout);

        return imageView;
    }

    /**
     * Konvertiert dp (Density-independent Pixels) in Pixel (px).
     * @param context Der Kontext der Anwendung, um Zugriff auf die Ressourcen und Geräte-spezifische Display-Metriken zu erhalten.
     * @param dp Der Wert in dp, der konvertiert werden soll.
     * @return Der konvertierte Wert in Pixel.
     */
    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}