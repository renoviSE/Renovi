package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.AnimationUtil;
import com.example.renovi.model.Renter;
import com.example.renovi.model.RenterSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LogInActivity extends Activity {

	final String TAG = "myTag";

	EditText firstNameData;
	EditText lastNameData;
	EditText verifyIdInput;
	private Renter renter;
	private RenterSession renterSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		firstNameData = findViewById(R.id.firstNameInput);
		lastNameData = findViewById(R.id.lastNameInput);
		verifyIdInput = findViewById(R.id.verifyIdInput);

		initializeLogInButton();
		initializeRenterSession();
	}

	private void initializeRenterSession() {
		renterSession = new RenterSession(this);
		renterSession.deleteSession();
	}

	private void initializeLogInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> checkIfIdExists());
	}

	private void checkIfIdExists() {
		String id = verifyIdInput.getText().toString();

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
									renter = new Renter(id, document.getString("vorname"), document.getString("nachname"), Float.parseFloat(document.getString("miete")));
									checkIfValid();
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

	private void checkIfValid() {
		String firstName = firstNameData.getText().toString();
		String lastName = lastNameData.getText().toString();

		if (firstName.equals(renter.getFirstName()) && lastName.equals(renter.getLastName())) {
			renterSession.putRenter(renter);
			switchToMain();
		}
		else {
			onFalseLoginData();
		}
	}

	private void onFalseLoginData() {
		// löscht den Text in den eingabe Feldern
		verifyIdInput.setText("");
		firstNameData.setText("");
		lastNameData.setText("");

		// zeigt eine Fehlermeldung am Bildschirm
		Toast.makeText(this, "Überprüfen Sie Ihre Informationen und versuchen Sie es erneut.", Toast.LENGTH_SHORT).show();

		// animeiert hint farbe
		int hintColor = ContextCompat.getColor(this, R.color.danger); // @color/success// @color/danger
		AnimationUtil.animateHintAndDrawableColor(verifyIdInput, hintColor, 1000);
		AnimationUtil.animateHintAndDrawableColor(firstNameData, hintColor, 1000);
		AnimationUtil.animateHintAndDrawableColor(lastNameData, hintColor, 1000);

	}
	private void switchToMain() {
		// animeiert hint farbe
		int hintColor = ContextCompat.getColor(this, R.color.lightBlue); // @color/lightBlue
		int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2); // @color/gray2
		AnimationUtil.animateInputAndDrawableColor(verifyIdInput, currentDrawableColor, hintColor, 1000);
		AnimationUtil.animateInputAndDrawableColor(firstNameData, currentDrawableColor, hintColor, 1000);
		AnimationUtil.animateInputAndDrawableColor(lastNameData, currentDrawableColor, hintColor, 1000);

		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		startActivity(switchActivityIntent);
	}
}