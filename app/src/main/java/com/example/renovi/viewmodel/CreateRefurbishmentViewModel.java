package com.example.renovi.viewmodel;

import android.util.Log;
import android.widget.Toast;

import com.example.renovi.model.Person;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CreateRefurbishmentViewModel {

    public interface RenterCallback {
        void onSuccess(ArrayList<String> addresses, ArrayList<String> renterIds, ArrayList<Double> qms);
        void onFailure();
    }

    public interface RenovationCallback {
        void onSuccess();
        void onFailure();
    }

    private FirebaseFirestore db;

    public CreateRefurbishmentViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRenterAdresses(Person user, RenterCallback callback) {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashSet<String> uniqueAddresses = new HashSet<>();
                        ArrayList<String> addresses = new ArrayList<>();
                        ArrayList<String> renterIds = new ArrayList<>();
                        ArrayList<Double> qms = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String address = document.getString("adresse");
                            String qmString = document.getString("qm");
                            if (address != null && uniqueAddresses.add(address)) {
                                addresses.add(address);
                                renterIds.add(document.getId());
                                try {
                                    qms.add(Double.parseDouble(qmString));
                                } catch (NumberFormatException e) {
                                    Log.e("LoadObjects", "Invalid qm value: " + qmString, e);
                                    qms.add(0.0);
                                }
                            }
                        }
                        callback.onSuccess(addresses, renterIds, qms);
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                        callback.onFailure();
                    }
                });
    }

    public void saveRenovationToDatabase(String kostenInput, Calendar refurbishmentDate, ArrayList<Integer> selectedAddressesIndices, ArrayList<String> addressList, RenovationCallback callback) {
        double totalCost;
        try {
            totalCost = Double.parseDouble(kostenInput);
        } catch (NumberFormatException e) {
            callback.onFailure();
            return;
        }

        for (int addressIndex : selectedAddressesIndices) {
            String address = addressList.get(addressIndex);

            // Hole Mieter für die Adresse
            db.collection("Mieter")
                    .whereEqualTo("adresse", address)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        double totalSquareMeters = 0.0;
                        ArrayList<QueryDocumentSnapshot> renters = new ArrayList<>();

                        for (QueryDocumentSnapshot renter : queryDocumentSnapshots) {
                            renters.add(renter);
                            String qmString = renter.getString("qm");
                            try {
                                totalSquareMeters += Double.parseDouble(qmString);
                            } catch (NumberFormatException e) {
                                Log.w("Renovierung", "Fehler beim Parsen von Quadratmetern für Mieter: " + renter.getId(), e);
                            }
                        }

                        // Verteilung der Kosten
                        for (QueryDocumentSnapshot renter : renters) {
                            String renterId = renter.getId();
                            String qmString = renter.getString("qm");
                            double renterSquareMeters = 0.0;

                            try {
                                renterSquareMeters = Double.parseDouble(qmString);
                            } catch (NumberFormatException e) {
                                Log.w("Renovierung", "Fehler beim Parsen von Quadratmetern für Mieter: " + renterId, e);
                            }

                            // Kostenberechnung und Rundung auf 2 Nachkommastellen
                            double renterCost = totalSquareMeters > 0 ? (renterSquareMeters / totalSquareMeters) * totalCost : 0.0;
                            renterCost = Math.round(renterCost * 100.0) / 100.0;

                            // Renovierungsdaten erstellen
                            Map<String, Object> renovationData = new HashMap<>();
                            renovationData.put("kosten", String.valueOf(renterCost));
                            renovationData.put("eintrittsdatum", new Timestamp(refurbishmentDate.getTime()));
                            renovationData.put("object", "Fenster");
                            renovationData.put("zustand", "gut");
                            renovationData.put("vorteile", "");
                            renovationData.put("nachteile", "");
                            renovationData.put("paragraph", "");

                            // Renovierung für den Mieter direkt in seiner Sammlung "Renovierungen" speichern
                            DocumentReference renterDocRef = db.collection("Mieter").document(renterId);
                            renterDocRef.collection("Renovierungen")
                                    .add(renovationData)
                                    .addOnSuccessListener(aVoid -> Log.d("Renovierung", "Renovierung erfolgreich hinzugefügt für Mieter: " + renterId))
                                    .addOnFailureListener(e -> Log.w("Renovierung", "Fehler beim Hinzufügen der Renovierung", e));
                        }
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> Log.w("Renovierung", "Fehler beim Abrufen der Mieter für Adresse: " + address, e));
        }
    }
}
