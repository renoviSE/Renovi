package com.example.renovi.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.widget.Button;

import com.example.renovi.R;
import com.example.renovi.model.Mieter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

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
		String verifyIdInput = String.valueOf(findViewById(R.id.verifyIdInput));
		String firstNameInput = String.valueOf(findViewById(R.id.firstNameInput));
		String lastNameInput = String.valueOf(findViewById(R.id.lastNameInput));

		Mieter mieter = getMieter(verifyIdInput);
		if (firstNameInput.equals(mieter.getFirstName()) && lastNameInput.equals(mieter.getLastName())) {
			// alle Daten stimmen
			switchToMain();
		}
		else {
			// Fehler... Seite neu laden
			Log.i(TAG, "falsche Login-Daten");
		}
	}

	private Mieter getMieter(String id) {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		// null falls ID nicht existiert
		AtomicReference<Mieter> mieter = null;
		mieter.set(new Mieter("0", "0", "0"));
		Log.i(TAG, "trying");
		db.collection("Mieter")
				.whereEqualTo("mieter_id", id)
				.get()
				.addOnSuccessListener(documents -> {
					// Mieter in Klasse schreiben
					DocumentSnapshot document = documents.getDocuments().get(0);
					if (document.exists()) {
						mieter.set(new Mieter(id, document.getString("vorname"), document.getString("nachname")));
					}
				})
				.addOnFailureListener(e -> Log.i(TAG, "fail"));
		return mieter.get();
	}

	private void switchToMain() {
		Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
		startActivity(switchActivityIntent);
	}
}
	
	