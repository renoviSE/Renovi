package com.example.renovi.view;


import android.app.Activity;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.renovi.R;

public class SignInActivity extends Activity {

	private ImageView bgIllustration;
	private ImageView signinIllustration;
	private TextView signinTitle;
	private TextView descriptiontext;
	private EditText verifyIdInput;
	private EditText firstNameInput;
	private EditText lastNameInput;
	private EditText birthdateInput;
	private Button signinButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		bgIllustration = (ImageView) findViewById(R.id.bgIllustration);
		signinIllustration = (ImageView) findViewById(R.id.signinIllustration);
		signinTitle = (TextView) findViewById(R.id.signinTitle);
		descriptiontext = (TextView) findViewById(R.id.descriptiontext);
		verifyIdInput = (EditText) findViewById(R.id.verifyIdInput);
		firstNameInput = (EditText) findViewById(R.id.firstNameInput);
		lastNameInput = (EditText) findViewById(R.id.lastNameInput);
		birthdateInput = (EditText) findViewById(R.id.birthdateInput);
		signinButton = (Button) findViewById(R.id.signinButton);
	}
}
	
	