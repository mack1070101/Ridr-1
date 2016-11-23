package ca.ualberta.ridr;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Add Driver view
 * Created by Justin Barclay Nov 22, 2016
 *
 * This activity displays the add user screen, and handles the text input logic, and parsing of data.
 * Also checks to see if the user we are attempting to make is already in the database, and adds the
 * user if the user is valid.
 */
public class AddDriverView extends Activity {
    /**
     * The Username edit text.
     */
    EditText usernameEditText;
    /**
     * The Dob edit text.
     */
    EditText dobEditText;
    /**
     * The Email edit text.
     */
    EditText emailEditText;
    /**
     * The Phone edit text.
     */
    EditText phoneEditText;
    /**
     * The Credit edit text.
     */
    EditText creditEditText;
    /**
     * The Create account button.
     */
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_driver);

        //defining view objects
        usernameEditText = (EditText) findViewById(R.id.username_add_account_edit_text);
        dobEditText = (EditText) findViewById(R.id.dob_add_account_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_add_account_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_add_account_edit_text);
        creditEditText = (EditText) findViewById(R.id.credit_add_account_edit_text);
        createAccountButton = (Button) findViewById(R.id.create_account_button);

        //text formatting listener for phone edit text
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new DateSelector();
                frag.show(getFragmentManager(), "DatePicker");
            }
        });
        //Create account Button code.
        createAccountButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //all edit text parsing logic

                //if the username edit text is empty or hasn't been changed
                String formattedNameString = usernameEditText.getText().toString().trim();
                if(TextUtils.isEmpty(formattedNameString)){
                    usernameEditText.setError("The Name Field cannot be empty.");
                    return;
                }

                //if the dob edit text is empty or hasn't been changed
                String formattedDateString = dobEditText.getText().toString().trim();
                if(TextUtils.isEmpty(formattedDateString)){
                    dobEditText.setError("The Date Field cannot be empty. It must be in format YYYY/MM/DD.");
                    return;
                }
                Date DOB = returnsValidDate(formattedDateString);
                if((DOB == null)){
                    dobEditText.setError("Your date is not a valid date. It must be in format YYYY/MM/DD.");
                    return;
                }

                //if the email edit text is empty or hasn't been changed
                String formattedEmailString = emailEditText.getText().toString().trim();
                if(TextUtils.isEmpty(formattedEmailString)){
                    emailEditText.setError("The Email Field cannot be empty, " +
                            "and you must provide an email using the pattern john@example.com.");
                    return;
                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(formattedEmailString).matches()){
                    //got idea from http://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext?noredirect=1&lq=1
                    //from answer by user1737884
                    emailEditText.setError("A valid email with the pattern john@example.com must be used.");
                    return;
                }

                //if the phone edit text is empty or hasn't been changed
                String unformattedPhoneString = phoneEditText.getText().toString().trim();
                //code is commented out as it doesn't currently work, and doesn't need to at this moment
                //TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String formattedPhoneString = PhoneNumberUtils.formatNumber(unformattedPhoneString);
                //gets country code from sim card
                //idea from http://stackoverflow.com/questions/12210696/how-to-get-country-or-its-iso-code from Sahil Mahajan Mj
                if(TextUtils.isEmpty(formattedPhoneString)){
                    phoneEditText.setError("The Phone Field cannot be empty, and must be of pattern (123) 456-7890, or 1 123-456-7890.");
                    return;
                }
                if(formattedPhoneString.length() != 14){
                    phoneEditText.setError("The Phone Field cannot be empty, and must be of pattern (123) 456-7890, or 1 123-456-7890.");
                    return;
                }

                //if the credit edit text is empty or hasn't been changed
                //I didn't think parsing credit info was important at this moment, but here's how to do it
                //http://stackoverflow.com/questions/11790102/format-credit-card-in-edit-text-in-android
                String formattedCreditString = creditEditText.getText().toString().trim();
                if(TextUtils.isEmpty(formattedCreditString)){
                    creditEditText.setError("The Credit Card Field cannot be empty, and pattern must be exactly XXXXBBBBYYYYAAAA.");
                    return;
                }
                if(formattedCreditString.length() != 16){
                    creditEditText.setError("The Credit Card Field must be 16 characters in length, and pattern must be exactly XXXXBBBBYYYYAAAA.");
                    return;
                }

                //make the objects
                User user = new User(formattedNameString, DOB,
                        formattedCreditString, formattedEmailString, formattedPhoneString);

                //check that account doesn't already exist
                AsyncController controller = new AsyncController();
                User onlineUser = null;
                try{
                    onlineUser = new Gson().fromJson(controller.get("user", "name", user.getName()), User.class);
                    if(onlineUser != null){
                        //if we found another rider with the same name
                        Toast.makeText(AddDriverView.this, "Sorry, that name cannot be used, as it is already in use.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e){
                    Toast.makeText(AddDriverView.this, "Could not communicate with the elastic search server", Toast.LENGTH_SHORT).show();
                    return;
                }

                //successful account creation
                Toast.makeText(AddDriverView.this, "Making Account!", Toast.LENGTH_SHORT).show();
                //save account in elastic search
                try {
                    controller.create("user", user.getID().toString(), new Gson().toJson(user));
                } catch (Exception e){
                    Log.i("Communication Error", "Could not communicate with the elastic search server");
                    return;
                }


                finish();
            }
        });




    }

    /**
     * Returns a date if the input string is a valid date, and null if it isn't
     * @param formattedDateString
     * @return
     */
    private Date returnsValidDate(String formattedDateString){
        //got idea from http://www.java2s.com/Tutorial/Java/0120__Development/CheckifaStringisavaliddate.htm

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Date inputDate;
        Date currentDate = new Date();

        dateFormat.setLenient(false);
        try {
            inputDate = dateFormat.parse(formattedDateString);
        } catch (ParseException pe) {
            //date was not in correct format
            return null;
        }
        if(inputDate.after(currentDate)){
            //date of birth is after current date, shouldn't happen
            return null;
        }
        return inputDate;
    }
}
