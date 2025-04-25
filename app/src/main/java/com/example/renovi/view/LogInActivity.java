package com.example.renovi.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.LogInViewModel;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.viewmodel.UI.UIHelper;


public class LogInActivity extends Activity {

	EditText firstNameData;
	EditText lastNameData;
	EditText verifyIdInput;

	private Session session;
	private LogInViewModel logInViewModel = new LogInViewModel();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		firstNameData = findViewById(R.id.firstNameInput);
		lastNameData = findViewById(R.id.lastNameInput);
		verifyIdInput = findViewById(R.id.verifyIdInput);

		UIHelper.initializeViewFunction(this, R.id.signinButton, view -> checkLogin());
		initializeSession();
	}

	private void initializeSession() {
		session = Session.getInstance(this);
		session.deleteSession();
	}

	private void checkLogin() {
		String id = verifyIdInput.getText().toString();
		String firstName = firstNameData.getText().toString();
		String lastName = lastNameData.getText().toString();

		logInViewModel.verifyUserId(id, new LogInViewModel.LoginCallback() {
			@Override
			public void onLoginSuccess(Person person) {
				if (firstName.equals(person.getFirstName()) && lastName.equals(person.getLastName())) {
					session.putUser(person);
					switchToMain();
				}
				else {
					onFalseLoginData();
				}
			}

			@Override
			public void onLoginFailure() {
				onFalseLoginData();
			}
		});
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