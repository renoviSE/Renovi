package com.example.renovi.viewmodel;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CreateRenterViewModel {

    public interface CreateRenterCallback {
        void onSuccess();
        void onFailure();
    }

    private FirebaseFirestore db;

    public CreateRenterViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveRenterToDatabase(Map<String, Object> renterData, CreateRenterCallback callback) {
        // Erstelle das Mieter-Dokument in Firestore
        db.collection("Mieter").add(renterData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Mieter erfolgreich gespeichert. ID: " + documentReference.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Speichern des Mieters", e);
                    callback.onFailure();
                });
    }
}
