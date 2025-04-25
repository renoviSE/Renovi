package com.example.renovi.viewmodel;

import com.example.renovi.model.Renovation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RenterRenovationsListViewModel {

    public interface RenterRenovationsListCallback {
        void onRenovationList(HashMap<String, Renovation> renovationList);
        void onEmptyList();
    }

    private FirebaseFirestore db;

    public RenterRenovationsListViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRenovations(String id, RenterRenovationsListCallback callback) {
        // Greife auf die Sammlung "Renovationen" des aktuellen Mieters zu
        db.collection("Mieter").document(id).collection("Renovierungen")
                .get()
                .addOnSuccessListener(documents -> {
                    HashMap<String, Renovation> renovationList = new HashMap<>();

                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectName = document.getString("object");
                            Renovation renovation = new Renovation(
                                    document.getString("object"),
                                    document.getString("vorteile"),
                                    document.getString("nachteile"),
                                    document.getString("kosten"),
                                    document.getString("paragraph"),
                                    document.getString("zustand")
                            );

                            renovationList.put(objectName, renovation);

                            buttonId += 1;
                        }
                    }
                    // Wenn keine Renovierungen vorhanden sind
                    if (buttonId == 1) {
                        callback.onEmptyList();
                    } else {
                        callback.onRenovationList(renovationList);
                    }

                });
    }
}
