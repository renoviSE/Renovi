package com.example.renovi.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataViewModel {

    public interface GetEmailCallback {
        void onFoundEmail(String email);
    }

    public interface SetEmailCallback {
        void onValidEmail();
        void onInvalidEmail();
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private FirebaseFirestore db;

    public PersonalDataViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void getEmail(Person user, GetEmailCallback callback) {
        db.collection((user.getRole()) == "Landlord"? "Vermieter" : "Mieter").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("email");
                        if (email != null){
                            callback.onFoundEmail(email);
                        }
                    } else {
                        Log.d("FirebaseError", "No such document");
                    }
                } else {
                    Log.d("FirebaseError", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void saveEmailToDatabase(String email, Person user, SetEmailCallback callback) {
        if (validate(email)) {
            db.collection((user.getRole()) == "Landlord"? "Vermieter" : "Mieter").document(user.getId())
                    .update("email", email).addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Email erfolgreich geändert");
                        callback.onValidEmail();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Fehler beim Ändern der E-Mail", e);
                    });
        }
        else {
            callback.onInvalidEmail();
        }
    }

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

}
