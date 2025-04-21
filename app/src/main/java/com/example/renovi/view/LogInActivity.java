package com.example.renovi.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.Landlord;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.viewmodel.AnimationUtil;
import com.example.renovi.model.Renter;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.Session;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;


public class LogInActivity extends Activity {

	final String TAG = "myTag";

	EditText firstNameData;
	EditText lastNameData;
	EditText verifyIdInput;
	private Person person;
	private Session session;

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
		session = Session.getInstance(this);
		session.deleteSession();
	}

	private void initializeLogInButton() {
		Button signInButton = findViewById(R.id.signinButton);
		signInButton.setOnClickListener(view -> checkIfIdExists());
	}

	private void checkIfIdExists() {
		String id = verifyIdInput.getText().toString();
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		AtomicBoolean found = new AtomicBoolean(false);

		try {
			// Erstelle Tasks für beide Sammlungen
			Task<DocumentSnapshot> mieterTask = db.collection("Mieter").document(id).get();
			Task<DocumentSnapshot> vermieterTask = db.collection("Vermieter").document(id).get();

			// Führe beide Tasks gleichzeitig aus und reagiere auf den ersten Erfolg
			mieterTask.addOnSuccessListener(document -> {
				try {
					if (document.exists() && !found.get()) {
						found.set(true);
						Log.i(TAG, "Mieter mit ID gefunden");
						person = new Renter("Renter",id, document.getString("vorname"), document.getString("nachname"), new BigDecimal(document.getString("miete")));
						checkIfValid();
					}
				} catch (Exception e) {
					Log.e(TAG, "Fehler bei der Verarbeitung des Mieter-Dokuments: " + e.getMessage());
				}
			});

			vermieterTask.addOnSuccessListener(document -> {
				try {
					if (document.exists() && !found.get()) {
						found.set(true);
						Log.i(TAG, "Vermieter mit ID gefunden");
						person = new Landlord("Landlord", id, document.getString("vorname"), document.getString("nachname"));
						checkIfValid();
					}
				} catch (Exception e) {
					Log.e(TAG, "Fehler bei der Verarbeitung des Vermieter-Dokuments: " + e.getMessage());
				}
			});

			Tasks.whenAll(mieterTask, vermieterTask).addOnCompleteListener(task -> {
				if (!found.get()) {
					Log.i(TAG, "Kein Benutzer mit ID in Mieter oder Vermieter gefunden");
					onFalseLoginData();
				}
			});
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim Zugriff auf Firestore oder bei der Ausführung der Tasks: " + e.getMessage());
			onFalseLoginData();
		}
	}


	private void checkIfValid() {
		String firstName = firstNameData.getText().toString();
		String lastName = lastNameData.getText().toString();

		if (firstName.equals(person.getFirstName()) && lastName.equals(person.getLastName())) {
			session.putUser(person);
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
		int hintColor = ContextCompat.getColor(this, R.color.danger); // @color/danger
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

		Intent switchActivityIntent = new Intent(this, MainActivity.class);
		startActivity(switchActivityIntent);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(LocaleHelper.onAttach(newBase));
	}

}