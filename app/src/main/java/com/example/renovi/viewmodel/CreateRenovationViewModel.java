package com.example.renovi.viewmodel;

import android.util.Log;

import com.example.renovi.model.Person;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CreateRenovationViewModel {

    public interface RenterCallback {
        void onSuccess(ArrayList<String> ids, ArrayList<String> renters);
        void onFailure();
    }
    public interface RenovationCallback {
        void onSuccess();
        void onFailure();
    }

    private FirebaseFirestore db;

    public CreateRenovationViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRenters(Person user, RenterCallback callback) {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> renters = new ArrayList<>();
                        ArrayList<String> renterDocIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String firstName = document.getString("vorname");
                            String lastName = document.getString("nachname");
                            String fullName = firstName + " " + lastName;
                            renters.add(fullName);
                            renterDocIds.add(document.getId()); // Dokument-ID speichern
                        }
                        callback.onSuccess(renterDocIds, renters);
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                        callback.onFailure();
                    }
                });
    }

    public void saveRenovationToDatabase(Map<String, Object> renovationData, ArrayList<String> renterDocIds, RenovationCallback callback) {
        // Erstelle das Renovierung-Dokument in Firestore
        db.collection("Renovierung").add(renovationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Renovierung erfolgreich gespeichert. ID: " + documentReference.getId());

                    // Verknüpfe das Renovierung-Dokument mit den Mietern
                    for (String renterDocId : renterDocIds) {
                        DocumentReference renterDocRef = db.collection("Mieter").document(renterDocId);
                        renterDocRef.collection("Renovierungen").add(renovationData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Renovierung für Mieter erfolgreich verknüpft."))
                                .addOnFailureListener(e -> Log.w("Firestore", "Fehler bei der Verknüpfung der Renovierung mit dem Mieter", e));
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onFailure());
    }
}
