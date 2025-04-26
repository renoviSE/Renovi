package com.example.renovi.view;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.PersonalDataViewModel;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.example.renovi.viewmodel.UI.UIHelper;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.model.LocaleHelper;

public class PersonalDataActivity extends AppCompatActivity {

    private PersonalDataViewModel viewModel = new PersonalDataViewModel();
    private Session session;
    private Person user;
    EditText emailInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();
        setContentView(R.layout.activity_personal_data);
        emailInput = findViewById(R.id.emailInput);

        getEmail();

        UIHelper.initializeBackButton(this, R.id.personalDataBackButton);
        UIHelper.initializeViewFunction(this, R.id.changePersonalDataButton, v -> saveEmailToDatabase());
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void saveEmailToDatabase() {
        String email = emailInput.getText().toString();

        viewModel.saveEmailToDatabase(email, user, new PersonalDataViewModel.SetEmailCallback() {
            @Override
            public void onValidEmail() {
                // Animation + UI reset
                AnimationUtil.animateInputAndDrawableColor(emailInput, ContextCompat.getColor(PersonalDataActivity.this, R.color.gray2),
                        ContextCompat.getColor(PersonalDataActivity.this, R.color.lightBlue), 1000);

                Toast.makeText(PersonalDataActivity.this, R.string.validEmail, Toast.LENGTH_SHORT).show();

                new android.os.Handler().postDelayed(() -> {
                    emailInput.setText(""); // Feld leeren

                    // Erst nach kurzem Delay die View neu laden
                    recreate();
                }, 300);
            }

            @Override
            public void onInvalidEmail() {
                Toast.makeText(PersonalDataActivity.this, R.string.invalidEmail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEmail() {
        viewModel.getEmail(user, email -> emailInput.setHint(email));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
