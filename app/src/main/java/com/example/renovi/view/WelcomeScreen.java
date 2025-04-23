package com.example.renovi.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.widget.Button;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.viewmodel.UIHelper;

public class WelcomeScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcomescreen);

		UIHelper.initializeViewFunction(this, R.id.startButton, view -> switchToSignIn());
	}

	private void switchToSignIn() {
		Intent switchActivityIntent = new Intent(this, LogInActivity.class);
		startActivity(switchActivityIntent);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(LocaleHelper.onAttach(newBase));
	}

}
	
	