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

package com.qualcomm.dcp.verify.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.counterfeit.view.CounterfeitActivity;
import com.qualcomm.dcp.result.view.ResultDisplayActivity;
import com.qualcomm.dcp.utils.ConnectionDetector;
import com.qualcomm.dcp.utils.Utils;
import com.qualcomm.dcp.verify.model.ImeiResponse;
import com.qualcomm.dcp.verify.presenter.VerifyImeiPresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for enter imei fragment
public class EnterImeiFragment extends Fragment implements View.OnClickListener, VerifyImeiInterface {

    // Binding views
    @BindView(R.id.btn_check)
    public Button checkButton;
    @BindView(R.id.txt_input)
    public EditText imeiInput;
    @BindView(R.id.imei_til)
    public TextInputLayout imeiTil;

    private Unbinder mUnbind;

    private String mUserInput;
    private AlertDialog mAlertDialog;
    private VerifyImeiPresenter mVerifyImeiPresenter;
    public SweetAlertDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(com.qualcomm.dcp.R.layout.fragment_fragment_tac, container, false);
        mUnbind = ButterKnife.bind(this, view);
        initViews();
        setupMVP();

        return view;
    }

    // Upon closing of this fragment views are unbidden and presenter is detached
    @Override
    public void onDestroyView() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mVerifyImeiPresenter != null)
            mVerifyImeiPresenter.unbind();
        super.onDestroyView();
    }

    // For initializing views
    private void initViews() {
        checkButton.setOnClickListener(this);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    // For attaching presenter t this view
    private void setupMVP() {
        mVerifyImeiPresenter = new VerifyImeiPresenter(this, getActivity());
    }

    public AlertDialog getVerifyImeiDialog() {
        return mAlertDialog;
    }

    // For showing dialog to verify imei
    private void showVerifyImeiDialog() {
        mUserInput = imeiInput.getText().toString();
        if ((mUserInput.length() >= 14) && (mUserInput.length() <= 16)) {
            if (mUserInput.matches("\\d+(?:\\.\\d+)?")) {
                imeiTil.setError(null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                LayoutInflater inflater = getActivity().getLayoutInflater();
                @SuppressLint("InflateParams") final View dialogView = inflater.inflate(com.qualcomm.dcp.R.layout.verify_input_dialog, null);
                dialogBuilder.setView(dialogView);

                final EditText imeiEntered = dialogView.findViewById(R.id.verify_imei_text);
                final TextInputLayout imeiEnteredTil = dialogView.findViewById(R.id.verify_imei_til);
                imeiEntered.setText(mUserInput);

                dialogBuilder.setTitle(getResources().getString(com.qualcomm.dcp.R.string.verify_input));
                dialogBuilder.setMessage(getResources().getString(com.qualcomm.dcp.R.string.verify_input_detail));
                dialogBuilder.setPositiveButton(getResources().getString(com.qualcomm.dcp.R.string.ok), (dialog, whichButton) -> {
                });
                dialogBuilder.setNegativeButton(getResources().getString(com.qualcomm.dcp.R.string.cancel), (dialog, whichButton) -> {
                });

                getActivity().runOnUiThread(() -> {
                    mAlertDialog = dialogBuilder.create();
                    mAlertDialog.show();
                    mAlertDialog.setOnDismissListener(dialogInterface -> {
                    });

                    mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        mUserInput = imeiEntered.getText().toString();
                        if ((mUserInput.length() >= 14) && (mUserInput.length() <= 16)) {
                            if (mUserInput.matches("\\d+(?:\\.\\d+)?")) {
                                imeiEnteredTil.setError(null);
                                mAlertDialog.dismiss();
                                imeiInput.setText(mUserInput);

                                Log.i("EnterImeiFragment", "user input = " + mUserInput);
                                //userInput=scan_result_edit.getText().toString();

                                ConnectionDetector cd = new ConnectionDetector(getActivity());
                                boolean isInternetPresent = cd.isConnectingToInternet();
                                if (!isInternetPresent) {
                                    Utils.showNoInternetDialog(getActivity());
                                } else {
                                    imeiTil.setError(null);

                                    mProgressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                                    mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
                                    mProgressDialog.setTitleText(getResources().getString(R.string.checking));
                                    mProgressDialog.setCancelable(false);
                                    mProgressDialog.show();
                                    mProgressDialog.show();
                                    mVerifyImeiPresenter.verifyImei(mUserInput);
                                }
                            } else {
                                imeiEnteredTil.setError(getResources().getString(R.string.imei_must_be_number));
                            }

                        } else {
                            imeiEnteredTil.setError(getResources().getString(R.string.fifteen_digit_imei));
                        }
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                    });
                });
            } else {
                imeiTil.setError(getResources().getString(com.qualcomm.dcp.R.string.imei_must_be_number));
            }

        } else {
            imeiTil.setError(getResources().getString(com.qualcomm.dcp.R.string.fifteen_digit_imei));
        }
    }

    // For handling clicks on views present in this view
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_check) {
            showVerifyImeiDialog();
        }
    }

    // Invoked by presenter to handle success response
    @SuppressLint("SetTextI18n")
    @Override
    public void displaySuccess(ImeiResponse imeiResponse) {
        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();
        if (imeiResponse.getData().getGsmaApprovedTac().equals("Yes")) {

            String marketingName = imeiResponse.getData().getMarketingName();
            String internalModelName = imeiResponse.getData().getInternalModelName();
            String manufacturer = imeiResponse.getData().getManufacturer();
            String allocationDate = imeiResponse.getData().getTacApprovedDate();
            String brandName = imeiResponse.getData().getBrandName();
            String modelName = imeiResponse.getData().getModelName();
            String nfc = imeiResponse.getData().getNfcSupport();
            String bluetooth = imeiResponse.getData().getBlueToothSupport();
            String wlan = imeiResponse.getData().getWlanSupport();
            String deviceType = imeiResponse.getData().getEquipmentType();
            String simSupport = imeiResponse.getData().getSimSupport();
            String lpwan = imeiResponse.getData().getLpwan();
            String gsmaApprovedTac = imeiResponse.getData().getGsmaApprovedTac();

            StringBuilder operatingSystem = new StringBuilder();

            for (int i = 0; i < imeiResponse.getData().getOperatingSystem().size(); i++) {
                if (i > 0) {
                    if (i < (imeiResponse.getData().getOperatingSystem().size() - 1))
                        operatingSystem.append("\u2022 ").append(imeiResponse.getData().getOperatingSystem().get(i)).append(System.getProperty("line.separator"));
                    else
                        operatingSystem.append("\u2022 ").append(imeiResponse.getData().getOperatingSystem().get(i));
                } else {
                    if (imeiResponse.getData().getOperatingSystem().size() > 1)
                        operatingSystem.append("\u2022 ").append(imeiResponse.getData().getOperatingSystem().get(i)).append(System.getProperty("line.separator"));
                    else
                        operatingSystem.append(imeiResponse.getData().getOperatingSystem().get(i));
                }
            }

            StringBuilder radioInterface = new StringBuilder();

            for (int i = 0; i < imeiResponse.getData().getRadioInterface().size(); i++) {
                if (i > 0) {
                    if (i < (imeiResponse.getData().getRadioInterface().size() - 1))
                        radioInterface.append("\u2022 ").append(imeiResponse.getData().getRadioInterface().get(i)).append(System.getProperty("line.separator"));
                    else
                        radioInterface.append("\u2022 ").append(imeiResponse.getData().getRadioInterface().get(i));
                } else {
                    if (imeiResponse.getData().getRadioInterface().size() > 1)
                        radioInterface.append("\u2022 ").append(imeiResponse.getData().getRadioInterface().get(i)).append(System.getProperty("line.separator"));
                    else
                        radioInterface.append(imeiResponse.getData().getRadioInterface().get(i));
                }
            }

            StringBuilder deviceCertifyBody = new StringBuilder();

            for (int i = 0; i < imeiResponse.getData().getDeviceCertifybody().size(); i++) {
                if (i > 0) {
                    if (i < (imeiResponse.getData().getDeviceCertifybody().size() - 1))
                        deviceCertifyBody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i)).append(System.getProperty("line.separator"));
                    else
                        deviceCertifyBody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i));
                } else {
                    if (imeiResponse.getData().getDeviceCertifybody().size() > 1)
                        deviceCertifyBody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i)).append(System.getProperty("line.separator"));
                    else
                        deviceCertifyBody.append(imeiResponse.getData().getDeviceCertifybody().get(i));
                }
            }

            Bundle mBundle = new Bundle();
            mBundle.putString("imei", "" + mUserInput);
            mBundle.putString("marketing_name", "" + marketingName);
            mBundle.putString("internal_model_name", "" + internalModelName);
            mBundle.putString("manufacturer", "" + manufacturer);
            mBundle.putString("allocation_date", "" + allocationDate);
            mBundle.putString("radio_interface", "" + radioInterface);
            mBundle.putString("brand_name", "" + brandName);
            mBundle.putString("model_name", "" + modelName);
            mBundle.putString("operating_system", "" + operatingSystem);
            mBundle.putString("nfc", "" + nfc);
            mBundle.putString("bluetooth", "" + bluetooth);
            mBundle.putString("wlan", "" + wlan);
            mBundle.putString("device_type", "" + deviceType);
            mBundle.putString("simSupport", "" + simSupport);
            mBundle.putString("lpwan", "" + lpwan);
            mBundle.putString("deviceCertifybody", "" + deviceCertifyBody);
            mBundle.putString("gsmaApprovedTac", "" + gsmaApprovedTac);

            mProgressDialog.cancel();
            imeiInput.setText("");
            Intent intent = new Intent(getActivity(), ResultDisplayActivity.class);
            intent.putExtras(mBundle);
            startActivity(intent);


        } else {
            AlertDialog.Builder reportDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            LayoutInflater inflater = this.getLayoutInflater();
            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
            reportDialog.setView(dialogView).setNegativeButton(getResources().getString(R.string.report_device), (dialog, whichButton) -> {
                Intent intent = new Intent(getActivity(), CounterfeitActivity.class);
                intent.putExtra("imei", "" + mUserInput);
                getActivity().startActivity(intent);

            });

            ImageView icon = dialogView.findViewById(R.id.icon);
            TextView title = dialogView.findViewById(R.id.title);
            TextView message = dialogView.findViewById(R.id.message);

            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warn));
            title.setText(R.string.device_not_found);
            message.setText(mUserInput + " " + getResources().getString(com.qualcomm.dcp.R.string.device_notfound_details));

            AlertDialog alertDialog = reportDialog.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

    }

    // Invoked by presenter to handle failure response
    @Override
    public void displayError(Throwable throwable) {
        if (mProgressDialog.isShowing())
            mProgressDialog.cancel();

        Utils.showNetworkError(getActivity(), throwable);

    }
}
