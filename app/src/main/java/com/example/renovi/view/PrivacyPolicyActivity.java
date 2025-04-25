package com.example.renovi.view;


import android.content.Context;
import android.os.Bundle;
import android.app.Activity;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.viewmodel.UI.UIHelper;

public class PrivacyPolicyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        UIHelper.initializeBackButton(this, R.id.privacyBackButton);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
