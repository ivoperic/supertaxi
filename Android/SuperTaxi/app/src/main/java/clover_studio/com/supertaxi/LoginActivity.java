package clover_studio.com.supertaxi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import clover_studio.com.supertaxi.api.retrofit.CustomResponse;
import clover_studio.com.supertaxi.api.retrofit.LoginRetroApiInterface;
import clover_studio.com.supertaxi.base.BaseActivity;
import clover_studio.com.supertaxi.base.SuperTaxiApp;
import clover_studio.com.supertaxi.dialog.BasicDialog;
import clover_studio.com.supertaxi.dialog.CustomDialog;
import clover_studio.com.supertaxi.models.SignInDataModel;
import clover_studio.com.supertaxi.models.post_models.PostSignUpModel;
import clover_studio.com.supertaxi.singletons.UserSingleton;
import clover_studio.com.supertaxi.utils.Const;
import clover_studio.com.supertaxi.utils.LogCS;
import clover_studio.com.supertaxi.utils.SecretGeneratorUtils;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 9999;

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
    private LoginButton facebookButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;

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
        facebookButton = (LoginButton) findViewById(R.id.login_button);
        facebookButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();

        facebookButton.registerCallback(callbackManager, facebookCallback);

        tvFacebook.setOnClickListener(onFacebookClick);
        tvTwitter.setOnClickListener(onTwitterClick);
        tvGooglePlus.setOnClickListener(onGooglePlusClick);
        buttonSignIn.setOnClickListener(onOnSignInClick);
        tvForgotPassword.setOnClickListener(onForgotPasswordClick);
        tvSignUp.setOnClickListener(onSignUpClick);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, onFailedGoogle /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ///******
        etPassword.setText("cloverpass013");
        etEmailAddress.setText("ivo.peric@clover-studio.com");
        ///******

    }

    private View.OnClickListener onFacebookClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginManager.getInstance().logOut();
            facebookButton.performClick();
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
            showProgress();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
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

    private GoogleApiClient.OnConnectionFailedListener onFailedGoogle = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    private FacebookCallback facebookCallback = new FacebookCallback() {
        @Override
        public void onSuccess(Object o) {
            GraphRequest request = GraphRequest.newMeRequest(((LoginResult)o).getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String userId = object.getString("id");
                        String userEmail = object.getString("email");
                        Log.d("LOG", "FACEBOOK = > EMAIL: " + userEmail + " USERID: " + userId);

                        showProgress();
                        try {
                            String sha1Password = sha1Password(userId);
                            getTimeFromServer(userEmail, sha1Password, true);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.error_login_facebook));
                    }

                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.user_canceled));
        }

        @Override
        public void onError(FacebookException error) {
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), error.toString());
            Log.e("LOG", "ERROR: " + error.toString());
        }
    };

    private View.OnClickListener onSignUpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SignUpActivity.startActivity(getActivity());
        }
    };

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            String id = acct.getId();

            Log.d("LOG", "GOOGLE+ = > EMAIL: " + email + " USERID: " + id);

            try {
                String sha1Password = sha1Password(id);
                getTimeFromServer(email, sha1Password, true);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.failed_to_sing_in_with_google_plus));
        }
    }

    private void loginStart() {
        if(etPassword.getText().toString().length() == 0){
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.error_wrong_password));
            return;
        }else if(etEmailAddress.getText().toString().length() == 0){
            BasicDialog.startOneButtonDialog(getActivity(), getString(R.string.error), getString(R.string.error_wrong_email));
            return;
        }

        showProgress();
        try {
            String sha1Password = sha1Password(etPassword.getText().toString());
            getTimeFromServer(etEmailAddress.getText().toString(), sha1Password, false);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private String sha1Password(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordPlusSalt = password + Const.Secrets.STATIC_SALT;
        String sha1Password = SecretGeneratorUtils.SHA1(passwordPlusSalt);
        return sha1Password;
    }

    private void getTimeFromServer(final String email, String sha1Password, final boolean tryToSignUp){
        SecretGeneratorUtils.getTimeForSecret(sha1Password, getRetrofit(), getActivity(), new SecretGeneratorUtils.GetTimeForSecretListener() {
            @Override
            public void getTimeSuccess(String sha1Password, String sha1Secret) {
                logInToServer(email, sha1Password, sha1Secret, tryToSignUp);
            }

            @Override
            public void getTimeTryAgainForProgress() {
                showProgress();
            }
        });
    }

    private void tryToSignUp(final String email, final String sha1Password, final String sha1Secret){
        PostSignUpModel postModel = new PostSignUpModel(email, sha1Password, sha1Secret);
        LoginRetroApiInterface retroApiInterface = getRetrofit().create(LoginRetroApiInterface.class);
        Call<SignInDataModel> call = retroApiInterface.signUp(postModel);
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

                SuperTaxiApp.getPreferences().setCustomBoolean(Const.PreferencesKey.REMEMBER_ME, true);
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.SHA1_PASSWORD, sha1Password);
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.EMAIL_LOGIN, etEmailAddress.getText().toString());

                hideProgress();

                ChooseTypeActivity.startActivity(getActivity());
                finish();

            }

            @Override
            public void onTryAgain(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onTryAgain(call, response);
                showProgress();
                tryToSignUp(email, sha1Password, sha1Secret);
            }
        });
    }

    private void logInToServer(final String email, final String sha1Password, final String sha1Secret, final boolean tryToSignUp){

        PostSignUpModel postModel = new PostSignUpModel(email, sha1Password, sha1Secret);
        LoginRetroApiInterface retroApiInterface = getRetrofit().create(LoginRetroApiInterface.class);
        Call<SignInDataModel> call = retroApiInterface.signIn(postModel);
        call.enqueue(new CustomResponse<SignInDataModel>(getActivity(), !tryToSignUp, !tryToSignUp) {

            @Override
            public void onCustomSuccess(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onCustomSuccess(call, response);

                SignInDataModel model = response.body();

                if(model.data.token_new != null){
                    UserSingleton.getInstance().updateToken(model.data.token_new);
                }

                if(model.data != null){
                    UserSingleton.getInstance().updateUser(model.data);
                }

                SuperTaxiApp.getPreferences().setCustomBoolean(Const.PreferencesKey.REMEMBER_ME, cbRememberMe.isChecked());
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.SHA1_PASSWORD, sha1Password);
                SuperTaxiApp.getPreferences().setCustomString(Const.PreferencesKey.EMAIL_LOGIN, email);

                hideProgress();

                if(SuperTaxiApp.getPreferences().hasPreferences(Const.PreferencesKey.USER_TYPE)){
                    if(!SuperTaxiApp.getPreferences().getCustomBoolean(Const.PreferencesKey.USER_CREATED)){
                        CreateUserActivity.startActivity(getActivity(), UserSingleton.getInstance().getUserType());
                    }else {
                        if(UserSingleton.getInstance().getUserType() == Const.UserType.USER_TYPE_DRIVER){
                            DriverHomeActivity.startActivity(getActivity());
                        }else{
                            UserHomeActivity.startActivity(getActivity());
                        }
                    }
                }else{
                    ChooseTypeActivity.startActivity(getActivity());
                }
                finish();

            }

            @Override
            public void onTryAgain(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onTryAgain(call, response);
                showProgress();
                logInToServer(email, sha1Password, sha1Secret, tryToSignUp);
            }

            @Override
            public void onCustomFailed(Call<SignInDataModel> call, Response<SignInDataModel> response) {
                super.onCustomFailed(call, response);
                if(tryToSignUp){
                    tryToSignUp(email, sha1Password, sha1Secret);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        
    }
}
