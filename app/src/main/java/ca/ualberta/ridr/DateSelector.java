package ca.ualberta.ridr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import java.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * fragment that allows the user to chose a date from a calender
 * sets the text in a text view to a date of the format dd/MM/yyyy
 */

public class DateSelector extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    //fragment that displays a date picker
    // based off of code from http://stackoverflow.com/questions/27225815/android-how-to-show-datepicker-in-fragment

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar cal = Calendar.getInstance();;
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker View, int year, int month, int day){
        populateSetDate(year, month+1, day);
    }

    public void populateSetDate(int year, int month, int day){
        TextView dateView = (TextView) getActivity().findViewById(R.id.dateText);
        dateView.setText(day + "/" + month + "/" + year);
    }

}
