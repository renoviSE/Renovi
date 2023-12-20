package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.widget.Button;

import com.example.renovi.R;

public class WelcomeScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcomescreen);

		initializeStartButton();
	}

	private void initializeStartButton() {
		Button startButton = findViewById(R.id.startButton);
		startButton.setOnClickListener(view -> switchToSignIn());
	}

	private void switchToSignIn() {
		Intent switchActivityIntent = new Intent(this, LogInActivity.class);
		startActivity(switchActivityIntent);
	}
}
	
	