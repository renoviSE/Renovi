package com.example.renovi.viewmodel;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

public class AnimationUtil {

    public static void animateHintAndDrawableColor(final EditText editText, int targetColor, int duration) {
        int originalColor = editText.getCurrentHintTextColor();
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), originalColor, targetColor, originalColor);
        colorAnimator.setDuration(duration);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                editText.setHintTextColor(animatedColor);
                Drawable[] drawables = editText.getCompoundDrawables();
                for (Drawable drawable : drawables) {
                    if (drawable != null) {
                        drawable.mutate().setColorFilter(new PorterDuffColorFilter(animatedColor, PorterDuff.Mode.SRC_IN));
                    }
                }
            }
        });

        colorAnimator.start();
    }

    public static void animateInputAndDrawableColor(final EditText editText, int drawableCurrentColor, int targetColor, int duration) {
        int textCurrentColor = editText.getCurrentTextColor();
        ValueAnimator textColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), textCurrentColor, targetColor, textCurrentColor);
        textColorAnimator.setDuration(duration);

        ValueAnimator drawableColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), drawableCurrentColor, targetColor, drawableCurrentColor);
        drawableColorAnimator.setDuration(duration);

        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                editText.setTextColor(animatedColor);
            }
        });

        drawableColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                Drawable[] drawables = editText.getCompoundDrawables();
                for (Drawable drawable : drawables) {
                    if (drawable != null) {
                        drawable.mutate().setColorFilter(new PorterDuffColorFilter(animatedColor, PorterDuff.Mode.SRC_IN));
                    }
                }
            }
        });

        drawableColorAnimator.start();
        textColorAnimator.start();
    }
}