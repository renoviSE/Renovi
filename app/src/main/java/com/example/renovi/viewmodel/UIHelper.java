package com.example.renovi.viewmodel;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class UIHelper {
    public static void initializeBackButton(Activity activity, int button) {
        Button startButton = activity.findViewById(button);
        startButton.setOnClickListener(view -> activity.finish());
        activity.overridePendingTransition(0, 0); // Deaktiviert Animation beim Zurückkehren
    }

    public static void initializeViewFunction(Activity activity, int id, View.OnClickListener listener) {
        View view  = activity.findViewById(id);
        view.setOnClickListener(listener);
    }
}
