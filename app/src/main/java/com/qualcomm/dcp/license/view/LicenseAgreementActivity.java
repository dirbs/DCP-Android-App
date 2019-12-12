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

package com.qualcomm.dcp.license.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.qualcomm.dcp.ContextWrapper;
import com.qualcomm.dcp.MainNavActivity;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.license.presenter.LicensePresenter;
import com.qualcomm.dcp.utils.ConnectionDetector;
import com.qualcomm.dcp.utils.MyPreferences;
import com.qualcomm.dcp.utils.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for controlling license activity
public class LicenseAgreementActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, LicenseInterface {

    // Binding view
    @BindView(R.id.license_checkbox)
    public AppCompatCheckBox acceptCheckBox;
    @BindView(R.id.continue_btn)
    public AppCompatButton continueButton;
    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;
    @BindView(R.id.license_text)
    public WebView view;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private Unbinder mUnbind;

    private MyPreferences mMyPreferences;
    private String mLicense;
    private int mUserId;
    private LicensePresenter mLicensePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyPreferences = new MyPreferences(this);
        Log.e("license", "locale = " + mMyPreferences.getString("locale", "en"));
        if (mMyPreferences.getString("locale", "en").equals("vi") && !mMyPreferences.getBoolean("isRecreated", false)) {
            mMyPreferences.setBoolean("isRecreated", true);
            this.recreate();
        }

        if (mMyPreferences.getString("locale", "en").equals("vi") && !getIntent().hasExtra("reopen")) {
            mMyPreferences.setBoolean("isRecreated", true);
            getIntent().putExtra("reopen", "yes");
            this.recreate();
        }

        setContentView(R.layout.activity_license_agreement);
        mUnbind = ButterKnife.bind(this);
        initViews();
        setupMVP();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            MyPreferences myPreferences = new MyPreferences(newBase);
            super.attachBaseContext(ContextWrapper.wrap(newBase, myPreferences.getString("locale", "en")));
//        }
//        else {
//            super.attachBaseContext(newBase);
//        }
    }

    // Upon closing of this activity views are unbidden and presenter is detached
    @Override
    protected void onDestroy() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mLicensePresenter != null)
            mLicensePresenter.unbind();
        super.onDestroy();
    }

    // Initializing views
    private void initViews() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        mMyPreferences = new MyPreferences(LicenseAgreementActivity.this);
        view.setVerticalScrollBarEnabled(false);

        if (getIntent().hasExtra("license")) {
            mLicense = getIntent().getStringExtra("license");
            mUserId = getIntent().getIntExtra("userId", 1);
        }

        view.loadData(mLicense, "text/html; charset=utf-8", "utf-8");

        acceptCheckBox.setOnCheckedChangeListener(this);
        continueButton.setOnClickListener(this);
    }

    // Attaching presenter to this view
    private void setupMVP() {
        mLicensePresenter = new LicensePresenter(this, LicenseAgreementActivity.this);
    }

    // For handling clicks on view present in this view
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.continue_btn) {
            ConnectionDetector cd = new ConnectionDetector(LicenseAgreementActivity.this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent) {
                Utils.showNoInternetDialog(LicenseAgreementActivity.this);
            } else {
                continueButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                mLicensePresenter.updateLicense(mUserId + "");
            }
        }
    }

    // Handling checkbox changes
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }
    }

    // Invoked by presenter to handle success response from api
    @Override
    public void displaySuccess() {
        continueButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        mMyPreferences.setBoolean("isLicenseAgreed", true);

        Intent intent = new Intent(LicenseAgreementActivity.this, MainNavActivity.class);
        startActivity(intent);
        LicenseAgreementActivity.this.finish();

    }

    // Invoked by presenter to handle failure response from api
    @Override
    public void displayError(Throwable throwable) {
        continueButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Utils.showNetworkError(LicenseAgreementActivity.this, throwable);

    }
}