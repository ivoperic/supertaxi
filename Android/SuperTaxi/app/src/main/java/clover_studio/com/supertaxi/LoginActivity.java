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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import clover_studio.com.supertaxi.api.retrofit.CustomResponse;
import clover_studio.com.supertaxi.api.retrofit.LoginRetroApiInterface;
import clover_studio.com.supertaxi.base.BaseActivity;
import clover_studio.com.supertaxi.base.SuperTaxiApp;
import clover_studio.com.supertaxi.dialog.BasicDialog;
import clover_studio.com.supertaxi.models.SignInDataModel;
import clover_studio.com.supertaxi.models.post_models.PostSignUpModel;
import clover_studio.com.supertaxi.singletons.UserSingleton;
import clover_studio.com.supertaxi.utils.Const;
import clover_studio.com.supertaxi.utils.LogCS;
import clover_studio.com.supertaxi.utils.SecretGeneratorUtils;
import retrofit2.Call;
import retrofit2.Response;

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

        ///******
        etPassword.setText("cloverpass013");
        etEmailAddress.setText("ivo.peric@clover-studio.com");
        ///******

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
            loginStart();
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
            SignUpActivity.startActivity(getActivity());
        }
    };

    private void loginStart() {
        if(etPassword.getText().toString().length() == 0){
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.error_wrong_password));
            return;
        }else if(etEmailAddress.getText().toString().length() == 0){
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.error_wrong_email));
            return;
        }

        showProgress();
        String password = etPassword.getText().toString();
        String passwordPlusSalt = password + Const.Secrets.STATIC_SALT;
        try {
            String sha1Password = SecretGeneratorUtils.SHA1(passwordPlusSalt);
            SecretGeneratorUtils.getTimeForSecret(sha1Password, getRetrofit(), getActivity(), new SecretGeneratorUtils.GetTimeForSecretListener() {
                @Override
                public void getTimeSuccess(String sha1Password, String sha1Secret) {
                    logInToServer(sha1Password, sha1Secret);
                }

                @Override
                public void getTimeTryAgainForProgress() {
                    showProgress();
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void logInToServer(final String sha1Password, final String sha1Secret){

        PostSignUpModel postModel = new PostSignUpModel(etEmailAddress.getText().toString(), sha1Password, sha1Secret);
        LoginRetroApiInterface retroApiInterface = getRetrofit().create(LoginRetroApiInterface.class);
        Call<SignInDataModel> call = retroApiInterface.signIn(postModel);
        call.enqueue(new CustomResponse<SignInDataModel>(getActivity(), true, true) {

            @Override
            public void onCustomSuccess(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onCustomSuccess(call, response);

                SignInDataModel model = response.body();

                if(model.data.token_new != null){
                    UserSingleton.getInstance().updateToken(model.data.token_new);
                }

                if(model.data.user != null){
                    UserSingleton.getInstance().updateUser(model.data);
                }

                SuperTaxiApp.getPreferences().setCustomBoolean(Const.PreferencesKey.REMEMBER_ME, cbRememberMe.isChecked());
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.SHA1_PASSWORD, sha1Password);
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.EMAIL_LOGIN, etEmailAddress.getText().toString());

                hideProgress();

                if(SuperTaxiApp.getPreferences().hasPreferences(Const.PreferencesKey.USER_TYPE)){

                }else{
                    CreateUserActivity.startActivity(getActivity(), 1);
//                    UserHomeActivity.startActivity(getActivity());
                }
                finish();

            }

            @Override
            public void onTryAgain(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onTryAgain(call, response);
                showProgress();
                logInToServer(sha1Password, sha1Secret);
            }

        });

    }

}
