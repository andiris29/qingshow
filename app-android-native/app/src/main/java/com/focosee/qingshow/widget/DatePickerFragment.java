package com.focosee.qingshow.widget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;
import android.app.Dialog;

import java.util.Calendar;

/**
 * Created by Administrator on 2015/3/23.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDateSetListener onDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateSetListener(OnDateSetListener onDateSetListener){
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        onDateSetListener.dateSet(view, year, month, day);
    }

    public static DialogFragment showDatePickerDialog(FragmentManager fragmentManager) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fragmentManager, "datePicker");
        return newFragment;
    }

    public interface OnDateSetListener{
        public void dateSet(DatePicker view, int year, int month, int day);
    }
}
