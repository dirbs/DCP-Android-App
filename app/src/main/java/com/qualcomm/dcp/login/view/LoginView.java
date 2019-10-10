/*
 * Copyright (c) 2018-2019 Qualcomm Technologies, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted (subject to the limitations in the disclaimer below) provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  Neither the name of Qualcomm Technologies, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *  The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment is required by displaying the trademark/log as per the details provided here: [https://www.qualcomm.com/documents/dirbs-logo-and-brand-guidelines]
 *  Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 *  This notice may not be removed or altered from any source distribution.
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.dcp.login.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.BuildConfig;
import com.qualcomm.dcp.ContextWrapper;
import com.qualcomm.dcp.MainNavActivity;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.license.view.LicenseAgreementActivity;
import com.qualcomm.dcp.login.model.CurrentActiveLicense;
import com.qualcomm.dcp.login.model.LicensesItem;
import com.qualcomm.dcp.login.model.LoginResponse;
import com.qualcomm.dcp.login.presenter.ForgotPasswordPresenter;
import com.qualcomm.dcp.login.presenter.LoginPresenter;
import com.qualcomm.dcp.utils.ConnectionDetector;
import com.qualcomm.dcp.utils.MyPreferences;
import com.qualcomm.dcp.utils.UserSessionManager;
import com.qualcomm.dcp.utils.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for controlling Login activity
public class LoginView extends AppCompatActivity implements LoginViewInterface, View.OnClickListener {

    // Binding views
    @BindView(R.id.input_username)
    TextInputEditText usernameText;
    @BindView(R.id.input_password)
    TextInputEditText passwordText;
    @BindView(R.id.til_username)
    TextInputLayout usernameTil;
    @BindView(R.id.til_password)
    TextInputLayout passwordTil;
    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.forgot_password_btn)
    Button forgotPasswordText;
    @BindView(R.id.translateBtn)
    ImageButton translateBtn;

    private Unbinder mUnbind;

    public SweetAlertDialog mProgressDialog;
    private MyPreferences mMyPreferences;
    private String mUsername;
    private String mPassword;
    private final String TAG = "LoginView";
    private UserSessionManager mUserSessionManager;
    private LoginPresenter mLoginPresenter;
    private ForgotPasswordPresenter mForgotPasswordPresenter;
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        mMyPreferences = new MyPreferences(this);
        Utils.changeLanguageLocale(getBaseContext(), mMyPreferences.getString("locale", "en"));
        setContentView(R.layout.activity_login);
        mUnbind = ButterKnife.bind(this);

//        update language icon
        if (mMyPreferences.getString("locale", "en").equals("vi")) {
            translateBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_english));
        } else {
            translateBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_vietnamese));
        }

        if (BuildConfig.DEBUG) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock;
            if (km != null) {
                keyguardLock = km.newKeyguardLock("TAG");
                keyguardLock.disableKeyguard();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        }

        isUserSessionActive();
        initViews();
        setupMVP();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            MyPreferences myPreferences = new MyPreferences(newBase);
            super.attachBaseContext(ContextWrapper.wrap(newBase, myPreferences.getString("locale", "en")));
        }
        else {
            super.attachBaseContext(newBase);
        }
    }

    // Upon closing of this activity views are unbidden and presenter is detached
    @Override
    protected void onDestroy() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mForgotPasswordPresenter != null)
            mForgotPasswordPresenter.unbind();
        if (mLoginPresenter != null)
            mLoginPresenter.unbind();
        super.onDestroy();
    }

    // Attaching this presenter to its view
    private void setupMVP() {
        mLoginPresenter = new LoginPresenter(this, this);
        mForgotPasswordPresenter = new ForgotPasswordPresenter(this, this);
    }

    // Requesting api to login
    private void login() {
        try {
            Log.d(TAG, "Login");

            if (!validate()) {
                return;
            }

            mProgressDialog = new SweetAlertDialog(LoginView.this, SweetAlertDialog.PROGRESS_TYPE);
            mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
            mProgressDialog.setTitleText(getResources().getString(R.string.authenticating));
            mProgressDialog.setCancelable(false);

            mProgressDialog.show();

            mUsername = Objects.requireNonNull(usernameText.getText()).toString();
            mPassword = Objects.requireNonNull(passwordText.getText()).toString();

            // On complete call either onLoginSuccess or onLoginFailed
            try {
                ConnectionDetector cd = new ConnectionDetector(LoginView.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (!isInternetPresent) {
                    mProgressDialog.dismiss();
                    Utils.showNoInternetDialog(LoginView.this);
                } else {
                    mLoginPresenter.login(mUsername, mPassword);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            // Disable going back to the MainActivity
            moveTaskToBack(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Initializing views
    private void initViews() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        forgotPasswordText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        translateBtn.setOnClickListener(this);

    }

    // For Hiding keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // For validating input fields
    private boolean validate() {
        boolean valid = true;
        try {
            mUsername = Objects.requireNonNull(usernameText.getText()).toString();
            mPassword = Objects.requireNonNull(passwordText.getText()).toString();


            if (mUsername.isEmpty()) {
                Log.e(TAG, getResources().getString(R.string.can_not_empty));
                usernameTil.setError(getResources().getString(R.string.can_not_empty));
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mUsername).matches()) {
                Log.e(TAG, getResources().getString(R.string.email_invalid));
                usernameTil.setError(getResources().getString(R.string.email_invalid));
                valid = false;
            } else {
                usernameTil.setError(null);
            }

            if (mPassword.isEmpty()) {
                Log.e(TAG, getResources().getString(R.string.can_not_empty));
                passwordTil.setError(getResources().getString(R.string.can_not_empty));
                valid = false;
            } else if (mPassword.length() < 6) {

                passwordTil.setError(getResources().getString(R.string.password_should_be_min_six));
                valid = false;
            } else {
                passwordTil.setError(null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mMyPreferences.setBoolean("loginValid", valid);
        return valid;
    }

    public AlertDialog getForgotPasswordDialog() {
        return mAlertDialog;
    }

    // For showing forgot password dialog box
    private void showForgotPasswordDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginView.this);
        LayoutInflater inflater = LoginView.this.getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.forgot_email_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText scan_result_edit = dialogView.findViewById(R.id.forgot_pass_email_et);
        final TextInputLayout scan_result_til = dialogView.findViewById(R.id.forgot_password_email_til);

        dialogBuilder.setTitle(getResources().getString(R.string.reset_password));
        dialogBuilder.setMessage(getResources().getString(R.string.please_enter_password_to_reset));

        dialogBuilder.setPositiveButton(getResources().getString(R.string.ok), (dialog, whichButton) -> {
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, whichButton) -> {
        });

        LoginView.this.runOnUiThread(() -> {
            mAlertDialog = dialogBuilder.create();
            mAlertDialog.show();
            mAlertDialog.setOnDismissListener(dialogInterface -> {
            });

            mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String result_scan = scan_result_edit.getText().toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(result_scan).matches()) {

                    ConnectionDetector cd = new ConnectionDetector(LoginView.this);
                    boolean isInternetPresent = cd.isConnectingToInternet();
                    if (!isInternetPresent) {
                        LoginView.this.runOnUiThread(() -> Utils.showNoInternetDialog(LoginView.this));

                    } else {
                        scan_result_til.setError(null);
                        mAlertDialog.dismiss();
                        mProgressDialog = new SweetAlertDialog(LoginView.this, SweetAlertDialog.PROGRESS_TYPE);
                        mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
                        mProgressDialog.setTitleText(getResources().getString(R.string.verifying));
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        mForgotPasswordPresenter.forgotPassword(result_scan);
                    }
                } else {
                    LoginView.this.runOnUiThread(() -> scan_result_til.setError(getResources().getString(R.string.email_invalid)));
                }
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            });
        });
    }

    // For checking if user session is active or not
    private void isUserSessionActive() {
        try {

            mUserSessionManager = new UserSessionManager(this);
            // Check if user is already logged in or not
            if (mUserSessionManager.loginCheck() && !mUserSessionManager.getLoginToken().equals("")) {
                // User is already logged in. Take him to main activity

                Intent intent;

                if (!mMyPreferences.getBoolean("isLicenseAgreed", false)) {
                    intent = new Intent(LoginView.this, LicenseAgreementActivity.class);
                    intent.putExtra("license", mMyPreferences.getString("license", "<p>License Agreement</p>"));
                    intent.putExtra("licenseId", mMyPreferences.getInt("licenseId", 1));
                    intent.putExtra("licenseId", mMyPreferences.getInt("userId", 1));
                } else
                    intent = new Intent(LoginView.this, MainNavActivity.class);
                startActivity(intent);
                LoginView.this.finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Invoked by presenter to handle success response by api for login
    @Override
    public void displaySuccess(LoginResponse loginResponse) {

        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();

        if (loginResponse != null) {

            Log.e("login", loginResponse.toString());
            //user data
            String email = loginResponse.getData().getEmail();
            String isAdmin = loginResponse.getData().getRoles().get(0).getSlug();
            String status = loginResponse.getData().isActive() + "";
            String name = loginResponse.getData().getFirstName() + " " + loginResponse.getData().getLastName();
            String token = loginResponse.getMeta().getToken();
            mUserSessionManager.loginStore(true, email, isAdmin, status, name, token);

            //license data
            String agreement = loginResponse.getData().getAgreement();
            LicensesItem lastAgreedLicense = null;
            if (loginResponse.getLicenses().size() > 0)
                lastAgreedLicense = loginResponse.getLicenses().get(loginResponse.getLicenses().size() - 1);
            CurrentActiveLicense currentActiveLicense = loginResponse.getCurrentActiveLicense();

            String license = "";
            int licenseId = 0;
            if (loginResponse.getCurrentActiveLicense() != null) {
                license = loginResponse.getCurrentActiveLicense().getContent();
                licenseId = loginResponse.getCurrentActiveLicense().getId();
            }

            Intent intent;
            boolean agreedLatest = false;
            if (currentActiveLicense != null) {
                if (lastAgreedLicense == null || (lastAgreedLicense.getId() != currentActiveLicense.getId())) {
                    agreedLatest = true;
                }
            }

            if (isAdmin.equals("staff")) {
                if (agreement.equals("Not Agreed") || agreedLatest) {
                    intent = new Intent(LoginView.this, LicenseAgreementActivity.class);
                    intent.putExtra("license", license);
                    intent.putExtra("licenseId", licenseId);
                    intent.putExtra("userId", loginResponse.getData().getId());
                    mMyPreferences.setString("license", license);
                    mMyPreferences.setInt("licenseId", licenseId);
                    mMyPreferences.setInt("userId", loginResponse.getData().getId());
                    mMyPreferences.setBoolean("isLicenseAgreed", false);

                } else {
                    mMyPreferences.setBoolean("isLicenseAgreed", true);
                    intent = new Intent(LoginView.this, MainNavActivity.class);
                }

            } else {
                mMyPreferences.setBoolean("isLicenseAgreed", true);
                intent = new Intent(LoginView.this, MainNavActivity.class);
            }
            startActivity(intent);
            LoginView.this.finish();
        }
    }

    // Invoked by presenter to handle success response by api for forgot password
    @Override
    public void displayForgotPasswordSuccess() {
        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();

        AlertDialog.Builder successDialog = new AlertDialog.Builder(LoginView.this);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        successDialog.setView(dialogView).setPositiveButton(getResources().getString(R.string.ok), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
        title.setText(R.string.successful);
        message.setText(R.string.reset_email_sent);

        AlertDialog alertDialog = successDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // Invoked by presenter to handle failure response by api
    @Override
    public void displayError(Throwable throwable) {
        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();

        Utils.showNetworkError(LoginView.this, throwable);
    }

    // For handling clicks on views present in login activity
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            login();

        } else if (view.getId() == R.id.forgot_password_btn) {
            showForgotPasswordDialog();

        } else if (view.getId() == R.id.translateBtn) {
            if (mMyPreferences.getString("locale", "en").equals("en"))
                mMyPreferences.setString("locale", "vi");
            else
                mMyPreferences.setString("locale", "en");

            usernameTil.setError(null);
            passwordTil.setError(null);

            Intent intent = new Intent(LoginView.this, LoginView.class);
            startActivity(intent);
            LoginView.this.finish();
        }
    }

}