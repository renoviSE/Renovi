package com.example.renovi.viewmodel;

import com.example.renovi.model.Landlord;
import com.example.renovi.model.Person;
import com.example.renovi.model.Renter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogInViewModel {

    public interface LoginCallback {
        void onLoginSuccess(Person person);
        void onLoginFailure();
    }

    private FirebaseFirestore db;

    public LogInViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void verifyUserId(String id, LoginCallback callback) {
        AtomicBoolean found = new AtomicBoolean(false);

        db.collection("Mieter").document(id).get().addOnSuccessListener(document -> {
            if (document.exists() && !found.get()) {
                found.set(true);
                Person renter = new Renter("Renter", id,
                        document.getString("vorname"),
                        document.getString("nachname"),
                        new BigDecimal(document.getString("miete")));
                callback.onLoginSuccess(renter);
            }
        });

        db.collection("Vermieter").document(id).get().addOnSuccessListener(document -> {
            if (document.exists() && !found.get()) {
                found.set(true);
                Person landlord = new Landlord("Landlord", id,
                        document.getString("vorname"),
                        document.getString("nachname"));
                callback.onLoginSuccess(landlord);
            }
        }).addOnFailureListener(e -> {
            callback.onLoginFailure();
        });
    }
}

