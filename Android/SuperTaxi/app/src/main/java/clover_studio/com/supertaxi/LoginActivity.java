package clover_studio.com.supertaxi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

import clover_studio.com.supertaxi.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    private TextView tvFacebook;
    private TextView tvTwitter;
    private TextView tvGooglePlus;
    private EditText etEmailAddress;
    private EditText etPassword;
    private CheckBox cbRememberMe;
    private Button buttonSignIn;
    private TextView tvForgotPassword;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolbar(R.id.tToolbar, R.layout.custom_toolbar_title);
        setToolbarTitle(getString(R.string.app_name).toUpperCase(Locale.getDefault()));

        tvFacebook = (TextView) findViewById(R.id.tvFacebook);
        tvTwitter = (TextView) findViewById(R.id.tvTwitter);
        tvGooglePlus = (TextView) findViewById(R.id.tvGPlus);
        etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        cbRememberMe = (CheckBox) findViewById(R.id.cbRememberMe);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);

        tvFacebook.setOnClickListener(onFacebookClick);
        tvTwitter.setOnClickListener(onTwitterClick);
        tvGooglePlus.setOnClickListener(onGooglePlusClick);
        buttonSignIn.setOnClickListener(onOnSignInClick);
        tvForgotPassword.setOnClickListener(onForgotPasswordClick);
        tvSignUp.setOnClickListener(onSignUpClick);

    }

    private View.OnClickListener onFacebookClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onTwitterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onGooglePlusClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onOnSignInClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onForgotPasswordClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onSignUpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

}
