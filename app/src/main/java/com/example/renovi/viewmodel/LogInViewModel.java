package com.example.renovi.viewmodel;

import com.example.renovi.model.Landlord;
import com.example.renovi.model.Person;
import com.example.renovi.model.Renter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
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

        // Erstelle Tasks für beide Sammlungen
        Task<DocumentSnapshot> mieterTask = db.collection("Mieter").document(id).get();
        Task<DocumentSnapshot> vermieterTask = db.collection("Vermieter").document(id).get();

        // Führe beide Tasks gleichzeitig aus und reagiere auf den ersten Erfolg
        mieterTask.addOnSuccessListener(document -> {
            if (document.exists() && !found.get()) {
                found.set(true);
                Person renter = new Renter("Renter", id,
                        document.getString("vorname"),
                        document.getString("nachname"),
                        new BigDecimal(document.getString("miete")));
                callback.onLoginSuccess(renter);
            }
        });

        vermieterTask.addOnSuccessListener(document -> {
            if (document.exists() && !found.get()) {
                found.set(true);
                Person landlord = new Landlord("Landlord", id,
                        document.getString("vorname"),
                        document.getString("nachname"));
                callback.onLoginSuccess(landlord);
            }
        });

        Tasks.whenAll(mieterTask, vermieterTask).addOnCompleteListener(task -> {
            if (!found.get()) {
                callback.onLoginFailure();
            }
        });
    }
}

