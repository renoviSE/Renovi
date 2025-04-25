package com.example.renovi.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.AnimationUtil;
import com.example.renovi.viewmodel.UIHelper;
import com.example.renovi.viewmodel.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.renovi.model.LocaleHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    EditText emailInput;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();
        setContentView(R.layout.activity_personal_data);
        emailInput = (EditText) findViewById(R.id.emailInput);

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
        if (validate(email)) {
            db.collection((user.getRole()) == "Landlord"? "Vermieter" : "Mieter").document(user.getId())
                    .update("email", email).addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Email erfolgreich geändert");

                        // Animation + UI reset
                        AnimationUtil.animateInputAndDrawableColor(emailInput, ContextCompat.getColor(this, R.color.gray2),
                                ContextCompat.getColor(this, R.color.lightBlue), 1000);

                        Toast.makeText(this, R.string.validEmail, Toast.LENGTH_SHORT).show();

                        new android.os.Handler().postDelayed(() -> {
                            emailInput.setText(""); // Feld leeren

                            // Erst nach kurzem Delay die View neu laden
                            recreate();
                        }, 300);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Fehler beim Ändern der E-Mail", e);
                    });
        }
        else {
            Toast.makeText(this, R.string.invalidEmail, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    private void getEmail() {
        db.collection((user.getRole()) == "Landlord"? "Vermieter" : "Mieter").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("email");
                        if (email != null){
                            emailInput.setHint(email);
                        }
                    } else {
                        Log.d("FirebaseError", "No such document");
                    }
                } else {
                    Log.d("FirebaseError", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
