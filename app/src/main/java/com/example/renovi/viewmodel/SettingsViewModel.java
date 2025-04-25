package com.example.renovi.viewmodel;

import android.content.Context;

import com.example.renovi.model.LocaleHelper;

public class SettingsViewModel {

    public int getLanguageIndex(Context context) {
        String lang = LocaleHelper.getLanguage(context);
        return lang.startsWith("de") ? 0 : 1;
    }

    public boolean changeLanguage(Context context, int position) {
        String selectedLanguage = (position == 1) ? "en" : "de";
        String currentLanguage = LocaleHelper.getLanguage(context);

        if (!currentLanguage.equals(selectedLanguage)) {
            LocaleHelper.setLocale(context, selectedLanguage);
            return true;
        }
        return false;
    }
}
