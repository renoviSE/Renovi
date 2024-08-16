package com.example.renovi.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renovi.R;

import java.util.ArrayList;
import java.util.Collections;

public class CreateRenovationActivity extends AppCompatActivity {

    TextView benefits;
    boolean[] selectedBenefit;
    ArrayList<Integer> benefitsList = new ArrayList<>();
    String[] benefitsArray = {"Brandschutz", "Einbruchschutz", "Isoaltion"};

    TextView states;
    boolean[] selectedState;
    ArrayList<Integer> statesList = new ArrayList<>();
    String[] statesArray = {"gut", "mittel", "schlecht"};

    TextView renovations;
    boolean[] selectedRenovation;
    ArrayList<Integer> renovationsList = new ArrayList<>();
    String[] renovationsArray = {"Tür", "Fenster", "WC", "Dach"};

    TextView renter;
    boolean[] selectedRenter;
    ArrayList<Integer> renterList = new ArrayList<>();
    String[] renterArray = {"Beispiel", "Test", "Fake", "DB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_renovation_acitivity);

        benefits = findViewById(R.id.create_renovation_benefits);
        states = findViewById(R.id.create_renovation_state);
        renovations = findViewById(R.id.create_renovation_renovations);
        renter = findViewById(R.id.create_renovation_Renter);

        selectedBenefit = new boolean[benefitsArray.length];
        selectedState = new boolean[statesArray.length];
        selectedRenovation = new boolean[renovationsArray.length];
        selectedRenter = new boolean[renterArray.length];

        initializeBackToPreviousActivityButton();

        benefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateRenovationActivity.this
                );
                builder.setTitle(R.string.selection_view_benefits_title);
                builder.setCancelable(false);
                builder.setMultiChoiceItems(benefitsArray, selectedBenefit, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            //When checkbox selected
                            //Add position in benefits List
                            benefitsList.add(which);
                            //Sort benefits List
                            Collections.sort(benefitsList);
                        } else {
                            //When checkbox unselected
                            //Remove position from day list
                            benefitsList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < benefitsList.size(); j++) {
                            //Concat array value
                            stringBuilder.append(benefitsArray[benefitsList.get(j)]);
                            if (j != benefitsList.size() - 1) {
                                //When j value not equal to benefit list size -1
                                //Add comma
                                stringBuilder.append(", ");
                            }
                        }
                        //Set text on text view
                        benefits.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton(R.string.selection_view_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < selectedBenefit.length; j++) {
                            //Remove all selection
                            selectedBenefit[j] = false;
                            //Clear benefits list
                            benefitsList.clear();
                            //Cleat text view value
                            benefits.setText("");
                        }
                    }
                });
                //Show dialog
                builder.show();
            }
        });

        states.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateRenovationActivity.this
                );
                builder.setTitle(R.string.selection_view_states_title);
                builder.setCancelable(false);
                builder.setMultiChoiceItems(statesArray, selectedState, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            statesList.add(which);
                            Collections.sort(statesList);
                        } else {
                            //When checkbox unselected
                            //Remove position from day list
                            statesList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < statesList.size(); j++) {
                            //Concat array value
                            stringBuilder.append(statesArray[statesList.get(j)]);
                            if (j != statesList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        //Set text on text view
                        states.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton(R.string.selection_view_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < selectedState.length; j++) {
                            //Remove all selection
                            selectedState[j] = false;
                            //Clear states list
                            statesList.clear();
                            //Cleat text view value
                            states.setText("");
                        }
                    }
                });
                //Show dialog
                builder.show();
            }
        });

        renovations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateRenovationActivity.this
                );
                builder.setTitle(R.string.selection_view_renovations_title);
                builder.setCancelable(false);
                builder.setMultiChoiceItems(renovationsArray, selectedRenovation, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            renovationsList.add(which);
                            Collections.sort(renovationsList);
                        } else {
                            //When checkbox unselected
                            //Remove position from day list
                            renovationsList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < renovationsList.size(); j++) {
                            //Concat array value
                            stringBuilder.append(renovationsArray[renovationsList.get(j)]);
                            if (j != renovationsList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        //Set text on text view
                        renovations.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton(R.string.selection_view_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < selectedRenovation.length; j++) {
                            //Remove all selection
                            selectedRenovation[j] = false;
                            //Clear renovations list
                            renovationsList.clear();
                            //Cleat text view value
                            renovations.setText("");
                        }
                    }
                });
                //Show dialog
                builder.show();
            }
        });

        renter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateRenovationActivity.this
                );
                builder.setTitle(R.string.selection_view_renter_title);
                builder.setCancelable(false);
                builder.setMultiChoiceItems(renterArray, selectedRenter, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            renterList.add(which);
                            Collections.sort(renterList);
                        } else {
                            //When checkbox unselected
                            //Remove position from day list
                            renterList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < renterList.size(); j++) {
                            //Concat array value
                            stringBuilder.append(renterArray[renterList.get(j)]);
                            if (j != renterList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        //Set text on text view
                        renter.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton(R.string.selection_view_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < selectedRenter.length; j++) {
                            //Remove all selection
                            selectedRenter[j] = false;
                            //Clear renter list
                            renterList.clear();
                            //Cleat text view value
                            renter.setText("");
                        }
                    }
                });
                //Show dialog
                builder.show();
            }
        });
    }
    private void initializeBackToPreviousActivityButton() {
        Button startButton = findViewById(R.id.CreateRenovationToMainButton);
        startButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }
}