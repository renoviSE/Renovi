package com.example.renovi.viewmodel;

import android.util.Log;

import com.example.renovi.model.Person;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedHashMap;

public class InboxViewModel {

    public interface InboxCallback {
        void onRenterList(LinkedHashMap<String, String> renterList);
        void onEmptyList();
    }

    private FirebaseFirestore db;

    public InboxViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRenters(Person user, InboxCallback callback) {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            callback.onEmptyList();
                        } else {
                            LinkedHashMap<String, String> renterList = new LinkedHashMap<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firstName = document.getString("vorname");
                                String lastName = document.getString("nachname");
                                String fullName = firstName + " " + lastName;
                                String renterId = document.getId();

                                renterList.put(renterId, fullName);
                            }
                            callback.onRenterList(renterList);
                        }
                    } else {
                        callback.onEmptyList();
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                }).addOnFailureListener(v -> callback.onEmptyList());
    }
}
