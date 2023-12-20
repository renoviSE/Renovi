package com.example.renovi.viewmodel;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";

    public static void getUserData(FirebaseFirestore db,String collectionName, String userId, FirestoreCallback callback) {
        db.collection(collectionName)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            callback.onSuccess(document); // Rückgabe des einzelnen Dokuments
                        } else {
                            // Behandlung, wenn das Dokument nicht existiert
                            Log.w(TAG, "Dokument existiert nicht.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public interface FirestoreCallback {
        void onSuccess(DocumentSnapshot document);
        void onFailure(Exception e);
    }
}
