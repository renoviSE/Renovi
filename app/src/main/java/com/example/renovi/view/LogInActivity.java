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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initializeSignInButton();
	}

	private void initializeSignInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> checkIfIdExists());
	}

	private void checkIfIdExists() {
		EditText idData = findViewById(R.id.verifyIdInput);
		String id = idData.getText().toString();

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		Log.i(TAG, id);
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

	private void checkIfValid(Renter renter) {
		EditText firstNameData = findViewById(R.id.firstNameInput);
		String firstName = firstNameData.getText().toString();
		EditText lastNameData = findViewById(R.id.lastNameInput);
		String lastName = lastNameData.getText().toString();

		if (firstName.equals(renter.getFirstName()) && lastName.equals(renter.getLastName())) {
			switchToMain();
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

	private void switchToMain() {
		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		startActivity(switchActivityIntent);
	}
}
	
	