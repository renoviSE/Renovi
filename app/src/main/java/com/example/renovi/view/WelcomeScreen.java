package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.renovi.R;

public class WelcomeScreen extends Activity {


	private Button startButton;
	private TextView slogan;
	private TextView logotext;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcomescreen);

		logotext = (TextView) findViewById(R.id.logotext);
		slogan = (TextView) findViewById(R.id.slogan);

		initializeStartButton();
	}

	private void initializeStartButton() {
		Button startButton = findViewById(R.id.startButton);
		startButton.setOnClickListener(v -> switchToSignIn());
	}

	private void switchToSignIn() {
		Intent switchActivityIntent = new Intent(this, SignInActivity.class);
		startActivity(switchActivityIntent);
	}
}
	
	