package com.example.renovi.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalDataActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();
        setContentView(R.layout.activity_personal_data);
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void getEmail() {
        db.collection("Mieter").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("email");
                        EditText emailInput = (EditText) findViewById(R.id.emailInput);
                        emailInput.setText(email);
                    } else {
                        Log.d("FirebaseError", "No such document");
                    }
                } else {
                    Log.d("FirebaseError", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
