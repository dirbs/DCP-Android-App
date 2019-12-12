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

package com.qualcomm.dcp.result.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.ContextWrapper;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.counterfeit.view.CounterfeitActivity;
import com.qualcomm.dcp.result.model.ResultResponse;
import com.qualcomm.dcp.result.presenter.ResultPresenter;
import com.qualcomm.dcp.utils.MyPreferences;
import com.qualcomm.dcp.utils.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for controlling result display activity
public class ResultDisplayActivity extends AppCompatActivity implements View.OnClickListener, ResultInterface {

    public boolean isDeviceMatchReported = false;

    // Binding views
    @BindView(R.id.txt_tac)
    public TextView txt_tac;
    @BindView(R.id.txt_marketingName)
    public TextView txt_marketingName;
    @BindView(R.id.txt_internalModelName)
    public TextView txt_internalModelName;
    @BindView(R.id.txt_manufacturer)
    public TextView txt_manufacturer;
    @BindView(R.id.txt_allocationDate)
    public TextView txt_allocationDate;
    @BindView(R.id.txt_radioInterface)
    public TextView txt_radioInterface;
    @BindView(R.id.txt_brandName)
    public TextView txt_brandName;
    @BindView(R.id.txt_modelName)
    public TextView txt_modelName;
    @BindView(R.id.txt_operatingSystem)
    public TextView txt_operatingSystem;
    @BindView(R.id.txt_nfc)
    public TextView txt_nfc;
    @BindView(R.id.txt_bluetooth)
    public TextView txt_bluetooth;
    @BindView(R.id.txt_wlan)
    public TextView txt_wlan;
    @BindView(R.id.txt_deviceType)
    public TextView txt_deviceType;
    @BindView(R.id.txt_deviceCertificationBody)
    public TextView txt_deviceCertBody;
    @BindView(R.id.txt_sim_support)
    public TextView txt_simSupport;
    @BindView(R.id.txt_lpwan)
    public TextView txt_lpwan;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.report)
    Button report;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.mismatch_con)
    LinearLayout mismatchCon;

    private Unbinder mUnbind;

    public SweetAlertDialog progressDialog;
    public Dialog dialog;
    public boolean isDialogShowing = false;

    private String imei, marketingName, internalModelName, manufacturer, allocationDate,
            radioInterface, brandName, modelName, operatingSystem, nfc, bluetooth,
            wlan, deviceType, deviceCertBody, simSupport, lpwan;
    private ResultPresenter mResultPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyPreferences myPreferences = new MyPreferences(ResultDisplayActivity.this);
        Utils.changeLanguageLocale(getBaseContext(), myPreferences.getString("locale", "en"));
        setContentView(R.layout.activity_result_display);
        mUnbind = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        parseData();
        populateData();
        setupMVP();

        scrollView.scrollTo(0, 10);
        cancel.setOnClickListener(this);
        report.setOnClickListener(this);
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

    // Upon closing of this activity views are unbidden and presenter is detached from this view
    @Override
    protected void onDestroy() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mResultPresenter != null)
            mResultPresenter.unbind();
        super.onDestroy();
    }

    // For attaching this view to its presenter
    private void setupMVP() {
        mResultPresenter = new ResultPresenter(this, ResultDisplayActivity.this);
    }

    // For extracting data out of intent
    private void parseData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (intent.hasExtra("imei")) {
                imei = bundle.getString("imei");
                if (imei != null && imei.equals("")) imei = "N/A";
            }
            if (intent.hasExtra("marketing_name")) {
                marketingName = bundle.getString("marketing_name");
                if (marketingName != null && marketingName.equals("")) marketingName = "N/A";
            }
            if (intent.hasExtra("internal_model_name")) {
                internalModelName = bundle.getString("internal_model_name");
                if (internalModelName != null && internalModelName.equals(""))
                    internalModelName = "N/A";
            }
            if (intent.hasExtra("manufacturer")) {
                manufacturer = bundle.getString("manufacturer");
                if (manufacturer != null && manufacturer.equals("")) manufacturer = "N/A";
            }
            if (intent.hasExtra("allocation_date")) {
                allocationDate = bundle.getString("allocation_date");
                if (allocationDate != null && allocationDate.equals("")) allocationDate = "N/A";
            }
            if (intent.hasExtra("radio_interface")) {
                radioInterface = bundle.getString("radio_interface");
                if (radioInterface != null && radioInterface.equals("")) radioInterface = "N/A";
            }
            if (intent.hasExtra("brand_name")) {
                brandName = bundle.getString("brand_name");
                if (brandName != null && brandName.equals("")) brandName = "N/A";
            }
            if (intent.hasExtra("model_name")) {
                modelName = bundle.getString("model_name");
                if (modelName != null && modelName.equals("")) modelName = "N/A";
            }
            if (intent.hasExtra("operating_system")) {
                operatingSystem = bundle.getString("operating_system");
                if (operatingSystem != null && operatingSystem.equals("")) operatingSystem = "N/A";
            }
            if (intent.hasExtra("nfc")) {
                nfc = bundle.getString("nfc");
                if (nfc != null && nfc.equals("")) nfc = "N/A";
            }
            if (intent.hasExtra("bluetooth")) {
                bluetooth = bundle.getString("bluetooth");
                if (bluetooth != null && bluetooth.equals("")) bluetooth = "N/A";
            }
            if (intent.hasExtra("wlan")) {
                wlan = bundle.getString("wlan");
                if (wlan != null && wlan.equals("")) wlan = "N/A";
            }
            if (intent.hasExtra("device_type")) {
                deviceType = bundle.getString("device_type");
                if (deviceType != null && deviceType.equals("")) deviceType = "N/A";
            }
            if (intent.hasExtra("deviceCertifybody")) {
                deviceCertBody = bundle.getString("deviceCertifybody");
                if (deviceCertBody != null && deviceCertBody.equals("")) deviceCertBody = "N/A";
            }
            if (intent.hasExtra("simSupport")) {
                simSupport = bundle.getString("simSupport");
                if (simSupport != null && simSupport.equals("")) simSupport = "N/A";
            }
            if (intent.hasExtra("lpwan")) {
                lpwan = bundle.getString("lpwan");
                if (lpwan != null && lpwan.equals("")) lpwan = "N/A";
            }
        }
    }

    // For showing data in views
    private void populateData() {
        txt_tac.setText(imei);
        txt_marketingName.setText(marketingName);
        txt_internalModelName.setText(internalModelName);
        txt_manufacturer.setText(manufacturer);
        txt_allocationDate.setText(allocationDate);
        txt_radioInterface.setText(radioInterface);
        txt_brandName.setText(brandName);
        txt_modelName.setText(modelName);
        txt_operatingSystem.setText(operatingSystem);
        txt_nfc.setText(nfc);
        txt_bluetooth.setText(bluetooth);
        txt_wlan.setText(wlan);
        txt_deviceType.setText(deviceType);
        txt_deviceCertBody.setText(deviceCertBody);
        txt_simSupport.setText(simSupport);
        txt_lpwan.setText(lpwan);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            if (isDeviceMatchReported)
                this.finish();
            else
                showReportDeviceDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDeviceMatchReported)
            super.onBackPressed();
        else
            showReportDeviceDialog();
    }

    private void showReportDeviceDialog() {

        isDialogShowing = true;
        dialog = new Dialog(ResultDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.report_mismatch_layout);

        Button yes = dialog.findViewById(R.id.cancel);
        Button no = dialog.findViewById(R.id.report);
        yes.setOnClickListener(ResultDisplayActivity.this);
        no.setOnClickListener(ResultDisplayActivity.this);

        dialog.show();
    }

    // For handling clicks n views present in this view
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.report) {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
            progressDialog = new SweetAlertDialog(ResultDisplayActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
            progressDialog.setTitleText(getResources().getString(R.string.updating_device_status));
            progressDialog.setCancelable(false);
            progressDialog.show();
            mResultPresenter.resultMisMatched(txt_tac.getText().toString());

        } else if (view.getId() == R.id.cancel) {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
            progressDialog = new SweetAlertDialog(ResultDisplayActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
            progressDialog.setTitleText(getResources().getString(R.string.updating_device_status));
            progressDialog.setCancelable(false);
            progressDialog.show();
            mResultPresenter.resultMatched(txt_tac.getText().toString());
        }
    }

    // Invoked by presenter to handle success response by api
    @Override
    public void displaySuccess(ResultResponse resultResponse) {
        if (progressDialog.isShowing())
            progressDialog.cancel();

        isDeviceMatchReported = true;
        if (resultResponse.isSuccess()) {

            androidx.appcompat.app.AlertDialog.Builder successDialog = new androidx.appcompat.app.AlertDialog.Builder(ResultDisplayActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
            successDialog.setView(dialogView).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    mismatchCon.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mismatchCon.setVisibility(View.GONE);
                                }
                            });
                    if (isDialogShowing) {
                        ResultDisplayActivity.this.finish();
                    }
                }
            });

            ImageView icon = dialogView.findViewById(R.id.icon);
            TextView title = dialogView.findViewById(R.id.title);
            TextView message = dialogView.findViewById(R.id.message);

            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
            title.setText(R.string.successful);
            message.setText(R.string.device_status_updated);

            androidx.appcompat.app.AlertDialog alertDialog = successDialog.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            alertDialog.show();
        } else {
            Intent intent = new Intent(ResultDisplayActivity.this, CounterfeitActivity.class);
            intent.putExtra("imei", txt_tac.getText().toString());
            startActivity(intent);
            ResultDisplayActivity.this.finish();
        }
    }

    // Invoked by presenter to handle failure response by api
    @Override
    public void displayError(Throwable throwable) {
        if (progressDialog.isShowing())
            progressDialog.cancel();

        Utils.showNetworkError(ResultDisplayActivity.this, throwable);

    }
}
