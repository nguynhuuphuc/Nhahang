package com.example.nhahang.Utils;

import android.app.DatePickerDialog;
import android.graphics.Color;

import com.example.nhahang.R;

public class DatePickerDialogButton {
    public DatePickerDialogButton() {
    }

    public DatePickerDialogButton(DatePickerDialog dialog){
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}
