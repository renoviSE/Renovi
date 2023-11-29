package com.example.renovi.viewmodel;

import android.content.Context;

import com.example.renovi.model.RenovationDataSource;

public class RenovationViewModel {

    private RenovationDataSource dataSource;

    public RenovationViewModel(Context context) {
        dataSource = new RenovationDataSource(context);
    }

    public void open() {
        dataSource.open();
    }

    public void close() {
        dataSource.close();
    }

    public long addMieter(String firstName, String lastName) {
        return dataSource.addMieter(firstName, lastName);
    }

    public long addRenovation(String object, String advantages, String disadvantages,
                              int cost, String paragraph, long mieterId) {
        return dataSource.addRenovation(object, advantages, disadvantages, cost, paragraph, mieterId);
    }
}

