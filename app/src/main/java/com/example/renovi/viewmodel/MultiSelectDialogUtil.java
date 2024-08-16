package com.example.renovi.viewmodel;

import com.example.renovi.R;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.Collections;

public class MultiSelectDialogUtil {

    public static void showMultiSelectDialog(Context context, String title, String[] itemsArray,
                                             boolean[] selectedItems, ArrayList<Integer> selectedItemList,
                                             TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMultiChoiceItems(itemsArray, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedItemList.add(which);
                    Collections.sort(selectedItemList);
                } else {
                    selectedItemList.remove(Integer.valueOf(which));
                }
            }
        });

        // "OK"-Button to confirm the selection
        builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateTextViewWithSelectedItems(itemsArray, selectedItemList, targetTextView);
            }
        });

        // "Select All"-Button (links neben dem OK-Button)
        builder.setNegativeButton(R.string.selection_view_select_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < selectedItems.length; i++) {
                    selectedItems[i] = true;
                    if (!selectedItemList.contains(i)) {
                        selectedItemList.add(i);
                    }
                }
                updateTextViewWithSelectedItems(itemsArray, selectedItemList, targetTextView);
                dialog.dismiss(); // Schließe den Dialog
            }
        });

        // "Clear"-Button (zum Abwählen aller Elemente und Schließen des Dialogs)
        builder.setNeutralButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < selectedItems.length; i++) {
                    selectedItems[i] = false;
                }
                selectedItemList.clear();
                targetTextView.setText("");
                dialog.dismiss(); // Schließe den Dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showSingleSelectDialog(Context context, String title, String[] itemsArray,
                                              int[] selectedItem, TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(itemsArray, selectedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
            }
        });

        // "OK"-Button to confirm the selection
        builder.setPositiveButton(R.string.selection_view_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                targetTextView.setText(itemsArray[selectedItem[0]]);
            }
        });

        // "Clear"-Button (zum Abwählen aller Elemente und Schließen des Dialogs)
        builder.setNegativeButton(R.string.selection_view_deselect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = -1;
                targetTextView.setText("");
                dialog.dismiss(); // Schließe den Dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void updateTextViewWithSelectedItems(String[] itemsArray, ArrayList<Integer> selectedItemList, TextView targetTextView) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < selectedItemList.size(); i++) {
            stringBuilder.append(itemsArray[selectedItemList.get(i)]);
            if (i != selectedItemList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        targetTextView.setText(stringBuilder.toString());
    }
}