package clover_studio.com.supertaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import clover_studio.com.supertaxi.base.BaseActivity;

public class SignUpActivity extends BaseActivity {

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, SignUpActivity.class));
    }

    private EditText etUsername;
    private EditText etEmailAddress;
    private EditText etPassword;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar(R.id.tToolbar, R.layout.custom_toolbar_title_left_text);
        setToolbarTitle(getString(R.string.app_name).toUpperCase(Locale.getDefault()));
        setToolbarLeftText(getString(R.string.cancel));
        setToolbarLeftTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(onSignUpClick);

    }

    private View.OnClickListener onSignUpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

}
