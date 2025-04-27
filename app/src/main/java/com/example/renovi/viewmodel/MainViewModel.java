package com.example.renovi.viewmodel;

import com.example.renovi.model.Person;
import com.example.renovi.model.Renovation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class MainViewModel {

    public interface MainCallback {
        void onRenovationList(BigDecimal objectValue, LinkedHashMap<String, Renovation> renovationList);
        void onEmptyList();
    }

    private FirebaseFirestore db;

    public MainViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRenovations(Person user, MainCallback callback) {
        // Zugriff auf die Sammlung "Renovationen" des aktuellen Mieters
        db.collection("Mieter").document(user.getId()).collection("Renovierungen")
                .get()
                .addOnSuccessListener(documents -> {
                    LinkedHashMap<String, Renovation> renovationList = new LinkedHashMap<>();
                    BigDecimal allObjectsValue = new BigDecimal("0");

                    // Daten erfolgreich erhalten
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
                            // Speichere Kosten von allen Objekten
                            allObjectsValue = allObjectsValue.add(renovation.getObjectValue());

                            buttonId += 1;
                        }
                    }
                    // Wenn keine Renovierungen vorhanden sind
                    if (buttonId == 1) {
                        callback.onEmptyList();
                    } else {
                        callback.onRenovationList(allObjectsValue, renovationList);
                    }
                })
                .addOnFailureListener(e -> callback.onEmptyList());
    }
}
