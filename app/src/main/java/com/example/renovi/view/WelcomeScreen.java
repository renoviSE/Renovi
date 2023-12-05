package com.example.renovi.view;


import android.app.Activity;
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
		startButton = (Button) findViewById(R.id.StartButton);

	}
}
	
	