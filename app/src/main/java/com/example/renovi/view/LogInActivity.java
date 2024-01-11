package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.renovi.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends Activity {

	final String TAG = "myTag";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initializeSignInButton();
	}

	private void initializeSignInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> switchToMain());
	}

	private void checkIfValid() {
		EditText verifyIdInput = findViewById(R.id.verifyIdInput);
		EditText firstNameInput = findViewById(R.id.firstNameInput);
		EditText lastNameInput = findViewById(R.id.lastNameInput);


	}

	private void getMieter(FirebaseFirestore db) {

	}

	private void switchToMain() {
		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		startActivity(switchActivityIntent);
	}
}
	
	