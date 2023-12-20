package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.widget.Button;
import android.widget.EditText;

import com.example.renovi.R;

public class LogInActivity extends Activity {
	private EditText verifyIdInput;
	private EditText firstNameInput;
	private EditText lastNameInput;
	private EditText birthdateInput;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		verifyIdInput = findViewById(R.id.verifyIdInput);
		firstNameInput = findViewById(R.id.firstNameInput);
		lastNameInput = findViewById(R.id.lastNameInput);

		initializeSignInButton();
	}

	private void initializeSignInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> switchToMain());
	}

	private void switchToMain() {
		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		startActivity(switchActivityIntent);
	}
}
	
	