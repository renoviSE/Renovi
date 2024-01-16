package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.renovi.R;
import com.example.renovi.model.Renter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LogInActivity extends Activity {

	final String TAG = "myTag";

	private String userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initializeLogInButton();
	}

	private void initializeLogInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> checkIfIdExists());
	}

	private void checkIfIdExists() {
		EditText idData = findViewById(R.id.verifyIdInput);
		String id = idData.getText().toString();
		userId = id;

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		try {
			db.collection("Mieter").document(id).get()
					.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
						@Override
						public void onComplete(@NonNull Task<DocumentSnapshot> task) {
							if (task.isSuccessful()) {
								DocumentSnapshot document = task.getResult();
								if (document.exists()) {
									Log.i(TAG, "Mieter mit ID gefunden");
									Renter renter = new Renter(id, document.getString("vorname"), document.getString("nachname"));
									checkIfValid(renter);
								} else {
									Log.i(TAG, "kein Mieter mit ID gefunden");
									onFalseLoginData();
								}
							}
						}
					});
		}
		catch (Exception e) {
			Log.i(TAG, "keine ID");
			onFalseLoginData();
		}
	}

	private void checkIfValid(Renter renter) {
		EditText firstNameData = findViewById(R.id.firstNameInput);
		String firstName = firstNameData.getText().toString();
		EditText lastNameData = findViewById(R.id.lastNameInput);
		String lastName = lastNameData.getText().toString();

		if (firstName.equals(renter.getFirstName()) && lastName.equals(renter.getLastName())) {
			switchToMain(userId);
		}
		else {
			onFalseLoginData();
		}
	}

	private void onFalseLoginData() {
		// erstmal nur Seite neu laden
		Intent switchActivityIntent = new Intent(this, LogInActivity.class);
		startActivity(switchActivityIntent);
	}

	private void switchToMain(String userId) {
		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		switchActivityIntent.putExtra("userId", userId);
		startActivity(switchActivityIntent);
	}
}