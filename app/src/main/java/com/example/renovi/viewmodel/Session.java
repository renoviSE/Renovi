package com.example.renovi.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.renovi.model.Landlord;
import com.example.renovi.model.Person;
import com.example.renovi.model.Renter;

import java.math.BigDecimal;

public final class Session {
    final String PREF_USER = "user_prefs";
    final String KEY_ROLE = "key_role";
    final String KEY_ID = "key_id";
    final String KEY_FIRSTNAME = "key_firstname";
    final String KEY_LASTNAME = "key_lastname";
    final String KEY_RENT = "key_rent";
    private static volatile Session INSTANCE = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Session(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
    }

    public static Session getInstance(Context context) {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (Session.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    INSTANCE = new Session(context);
                }
            }
        }
        return INSTANCE;
    }

    public void deleteSession() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void putUser(Person person) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_ROLE, person.getRole());
        editor.putString(KEY_ID, person.getId());
        editor.putString(KEY_FIRSTNAME, person.getFirstName());
        editor.putString(KEY_LASTNAME, person.getLastName());
        if (person instanceof Renter) {
            editor.putString(KEY_RENT, ((Renter) person).getRent().toString());
        }
        editor.commit();
    }

    public Person getUser() {
        String role = sharedPreferences.getString(KEY_ROLE, null);
        String id = sharedPreferences.getString(KEY_ID, null);
        String firstName = sharedPreferences.getString(KEY_FIRSTNAME, null);
        String lastName = sharedPreferences.getString(KEY_LASTNAME, null);

        if (role.equals("Renter")) {
            String rentAsString = sharedPreferences.getString(KEY_RENT, null);
            BigDecimal rent = new BigDecimal(rentAsString);

            return new Renter(role, id, firstName, lastName, rent);
        } else {
            return new Landlord(role, id, firstName, lastName);
        }
    }
}
