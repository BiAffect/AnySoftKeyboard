package com.anysoftkeyboard.ui.settings.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.menny.android.anysoftkeyboard.BiAffect.bridge.BiAffectBridge;
import com.menny.android.anysoftkeyboard.LauncherSettingsActivity;
import com.menny.android.anysoftkeyboard.R;

import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import java.util.regex.Pattern;

import rx.Subscription;

public class LoggingPage extends AppCompatActivity {
    private String _email, _password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_page);

        EditText email_edText = (EditText) findViewById(R.id.input_email);
        EditText password_edText = (EditText) findViewById(R.id.input_password);

        final Button button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the inputs of email and password
                String input_email = email_edText.getText().toString();
                String input_password = password_edText.getText().toString();
                // reject the empty email
                if(input_email.matches("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Email should not be empty", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -130);
                    toast.show();
                    return;
                }

                //check the email is valid
                if(!isValidEmail(input_email)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Not a valid email address", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -130);
                    toast.show();
                    return;
                }
                // reject the empty password
                if(input_password.matches("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Password should not be empty", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -130);
                    toast.show();
                    return;
                }
                // use the bridge sdk to sign in
                signIn(input_email, input_password);
            }
        });

        final Button exit_button = (Button) findViewById(R.id.btn_exit);
        exit_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

    }


    // for email validation
    private boolean isValidEmail(String email) {
        Pattern pattern = EMAIL_ADDRESS_PATTERN;
        return pattern.matcher(email).matches();
    }

    // sign in function
    private void signIn(String email, String password) {
        _email = email;
        _password = password;
        // Use Bridge to login
        Subscription s = BiAffectBridge.getInstance()
                                       .logIn( email, password )
                                       .subscribe( this::onSignInSuccess, this::onSignInError );
    }

    private void onSignInSuccess( UserSessionInfo __ ) {
        String SuccessMsg = "You've successfully logged in";
        // login successfully, use intent to pass the message and jump to the next page
        Intent toLogging = new Intent( LoggingPage.this, LauncherSettingsActivity.class );
        toLogging.putExtra( "successMsg", SuccessMsg );
        startActivity( toLogging );
        // Log.d("success","success! move on to the next screen");
    }

    private void onSignInError( Throwable throwable ) {
        if( throwable instanceof ConsentRequiredException ) {
            UserSessionInfo session = ((ConsentRequiredException)throwable).getSession();
            Subscription s = BiAffectBridge.getInstance()
                                           .bypassConsent( session )
                                           .subscribe( (__) -> signIn( _email, _password ),
                                                       (error) -> onSignInError( new Throwable( "consent" ) ) );
            return;
        }
        // get the error message from backend
        String errorMsg = throwable.getMessage();
        ErrorMsgDialogFragment dialog = new ErrorMsgDialogFragment();
        // handle the error message
        switch (errorMsg) {
            case "Account not found.":
                errorMsg = "Incorrect/Unregistered email or incorrect password";
                break;
            case "Unable to resolve host \"webservices.sagebridge.org\": No address associated with hostname":
                errorMsg = "Internet Error, please check your internet connection";
                break;
            case "consent":
                errorMsg = "Consent error";
                break;
            default:
                errorMsg = "Unknown Error";
        }
        // use setArguments to pass the error message to the dialog
        Bundle args = new Bundle();
        args.putString("msgName", errorMsg);
        dialog.setArguments(args);
        // show the dialog to the user
        dialog.show(getSupportFragmentManager(), "errorMsg");
//                            Log.d("login error", errorMsg);
    }

    //the valid email pattern, which refers to
    //https://en.wikipedia.org/wiki/Email_address#RFC_specification
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "(?:[a-z0-9A-Z!#$%&'*+/=?^_`{|}~-]{1,64}+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
   );

    // use DialogFragment class to show the error message
    public static class ErrorMsgDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // get the content of error message
            builder.setMessage(getArguments().getString("msgName"))
                    .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ErrorMsgDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}
