package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.bigbangbutton.editcodeview.EditCodeWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.shimadove.coronago.databinding.ActivityPhone2VerificationBinding;
//import com.shimadove.coronago.databinding.ActivityVerificationBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import timber.log.Timber;

public class Phone2Verification extends AppCompatActivity {
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String otpBySystem;
    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    TextView mCountyList;
    private ProgressBar progressBar;
    String phoneNo;
    private String countryCode, enteredCode;
    private Button mStartButton;
    private boolean codeSent = false;
    private ImageView imageBanner;

    private FirebaseAuth mAuth;
    ActivityPhone2VerificationBinding binding;
    EditText phno, otpEnter;
    TextView enterNum, sendmsg;
    //ImageButton btn_proceed;

    String verificationID;
    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView( this, R.layout.activity_phone2_verification);
        if (savedInstanceState != null) {
            onRestoreInstanceState( savedInstanceState );
        }
        //progressBar.setVisibility(View.GONE);
        progressBar=binding.progressBar;
        progressBar.setVisibility(View.GONE);
        phno = binding.phno;
        //initView( );
        //initMode( );
        phoneNo = getIntent().getStringExtra(Phone1Verification.PHONE_NO);
        phno.setText(phoneNo);
        EditCodeView editCodeView = (EditCodeView) findViewById(R.id.edit_code);
        String s;
        editCodeView.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {
                //This function gives the complete number inputted
            }
        });
        sendVerificationCodeToUser(phoneNo);
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otpBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Phone2Verification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String otpByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpBySystem, otpByUser);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth= FirebaseAuth.getInstance();
        mAuth.signInWithCredential( credential )
                .addOnCompleteListener( Phone2Verification.this, new OnCompleteListener<AuthResult>( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful( )) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d( TAG, "signInWithCredential:success" );
                            Timber.d("signInWithCredential:success");
                            Intent intent=new Intent(getApplicationContext(),CreateProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            //FirebaseUser user = task.getResult( ).getUser( );
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Phone2Verification.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            //Log.w( TAG, "signInWithCredential:failure", task.getException( ) );
                            if (task.getException( ) instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                } );
    }


//    private void requestOTP(String phNum) {
//        PhoneAuthProvider.getInstance( ).verifyPhoneNumber( phNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks( ) {
//            @Override
//            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent( s, forceResendingToken );
//                //progressBar.setVisibility( View.GONE );
////                sendmsg.setVisibility(View.GONE);
////                otpEnter.setVisibility(View.VISIBLE);
//                verificationID = s;
//                token = forceResendingToken;
//            }
//
//            @Override
//            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
//                super.onCodeAutoRetrievalTimeOut( s );
//                Toast.makeText( Phone2Verification.this, "TimeOut", Toast.LENGTH_SHORT ).show( );
//            }
//



}