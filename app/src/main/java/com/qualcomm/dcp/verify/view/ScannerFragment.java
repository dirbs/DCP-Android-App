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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.counterfeit.view.CounterfeitActivity;
import com.qualcomm.dcp.result.view.ResultDisplayActivity;
import com.qualcomm.dcp.utils.ConnectionDetector;
import com.qualcomm.dcp.utils.Utils;
import com.qualcomm.dcp.verify.model.ImeiResponse;
import com.qualcomm.dcp.verify.presenter.VerifyImeiPresenter;

import java.util.List;
import java.util.Objects;

public class ScannerFragment extends Fragment implements VerifyImeiInterface {

    private boolean isFlashAvailable;
    private ImageButton flashButton;
    private AlertDialog mAlertDialog;
    private DecoratedBarcodeView barcodeView;
    private VerifyImeiPresenter mVerifyImeiPresenter;
    public SweetAlertDialog mProgressDialog;
    private String mUserInput = "";
    private boolean mFlash;
    private AlertDialog alertDialog;
    private boolean mIsScannerVisible;

    public ScannerFragment() {
        // Required empty public constructor
    }

    //call back to capture scan result
    public BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String imei = result.getText();
            barcodeView.pause();
            handleResult(imei);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.scan_fragment, container, false);

        barcodeView = view.findViewById(R.id.barcode_view);
        barcodeView.decodeContinuous(callback);

        flashButton = view.findViewById(R.id.btn_flash);

        isFlashAvailable = Objects.requireNonNull(getActivity()).getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (isFlashAvailable)
            flashButton.setOnClickListener(view1 -> {
                toggleFlash();
                if (mFlash)
                    flashButton.setImageResource(R.drawable.ic_flash_on);
                else
                    flashButton.setImageResource(R.drawable.ic_flash_off);
            });
        else
            flashButton.setVisibility(View.GONE);

        setupMVP();

        return view;
    }

    public AlertDialog getScanDialog() {
        return mAlertDialog;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView.resume();
            } else {
                barcodeView.pauseAndWait();
            }
        }
    }

    private void toggleFlash() {
        mFlash = !mFlash;
        if (mFlash) {
            barcodeView.setTorchOn();
        } else {
            barcodeView.setTorchOff();
        }
    }

    // For attaching this view to presenter
    private void setupMVP() {
        mVerifyImeiPresenter = new VerifyImeiPresenter(this, getActivity());
    }

    public void startScanner(boolean b) {
        mIsScannerVisible = b;
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    public void stopScanner(boolean isScannerVisible) {
        mIsScannerVisible = isScannerVisible;
        if (barcodeView != null) {
//            barcodeView.pauseAndWait();
            barcodeView.pause();
        }
    }

    @Override
    public void onResume() {
        startScanner(true);
        super.onResume();
    }

    @Override
    public void onPause() {
        stopScanner(true);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        stopScanner(true);
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        stopScanner(true);
        super.onStop();
    }

    public void handleResult(String rawResult) {
        showVerifyImeiDialog(rawResult);
    }

    // For showing dialog to verify imei
    private void showVerifyImeiDialog(String imei) {

        if (isFlashAvailable) {
            barcodeView.setTorchOff();
            mFlash = false;
            flashButton.setImageResource(R.drawable.ic_flash_off);

        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.scan_confirmation_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText scan_result_edit = dialogView.findViewById(R.id.scan_result_et);
        final TextInputLayout scan_result_til = dialogView.findViewById(R.id.scan_result_til);
        scan_result_edit.setText(imei);

        dialogBuilder.setTitle(getResources().getString(R.string.verify_scan));
        dialogBuilder.setMessage(getResources().getString(R.string.verify_scan_detail));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.ok), (dialog, whichButton) -> {

        });

        dialogBuilder.setCancelable(false);

        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, whichButton) -> {

        });

        dialogBuilder.setOnDismissListener(dialogInterface -> barcodeView.resume());

        getActivity().runOnUiThread(() -> {
            mAlertDialog = dialogBuilder.create();
            mAlertDialog.show();
            mAlertDialog.setOnDismissListener(dialogInterface -> {

            });

            mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String result_scan = scan_result_edit.getText().toString();
                if ((result_scan.length() > 13) && (result_scan.length() < 17)) {
                    if (result_scan.matches("\\d+(?:\\.\\d+)?")) {
                        ConnectionDetector cd = new ConnectionDetector(getActivity());
                        boolean isInternetPresent = cd.isConnectingToInternet();
                        if (!isInternetPresent) {
                            getActivity().runOnUiThread(() -> Utils.showNoInternetDialog(getActivity()));

                        } else {
                            scan_result_til.setError(null);
                            mAlertDialog.dismiss();
                            mProgressDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.PROGRESS_TYPE);
                            mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
                            mProgressDialog.setTitleText(getResources().getString(R.string.checking));
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                            mUserInput = result_scan;
                            mVerifyImeiPresenter.verifyScanImei(result_scan);
                        }
                    } else {
                        getActivity().runOnUiThread(() -> scan_result_til.setError(getResources().getString(R.string.imei_must_be_number)));
                    }
                } else {
                    getActivity().runOnUiThread(() -> scan_result_til.setError(getResources().getString(R.string.fifteen_digit_imei)));
                }
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            });

            mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> {
                mAlertDialog.dismiss();
                if (mIsScannerVisible)
                    barcodeView.resume();
            });
        });
    }

    // Invoked by presenter to handle success response by api
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

            StringBuilder deviceCertifybody = new StringBuilder();

            for (int i = 0; i < imeiResponse.getData().getDeviceCertifybody().size(); i++) {
                if (i > 0) {
                    if (i < (imeiResponse.getData().getDeviceCertifybody().size() - 1))
                        deviceCertifybody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i)).append(System.getProperty("line.separator"));
                    else
                        deviceCertifybody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i));
                } else {
                    if (imeiResponse.getData().getDeviceCertifybody().size() > 1)
                        deviceCertifybody.append("\u2022 ").append(imeiResponse.getData().getDeviceCertifybody().get(i)).append(System.getProperty("line.separator"));
                    else
                        deviceCertifybody.append(imeiResponse.getData().getDeviceCertifybody().get(i));
                }

            }

            Bundle bundle = new Bundle();
            bundle.putString("imei", "" + mUserInput);
            bundle.putString("marketing_name", "" + marketingName);
            bundle.putString("internal_model_name", "" + internalModelName);
            bundle.putString("manufacturer", "" + manufacturer);
            bundle.putString("allocation_date", "" + allocationDate);
            bundle.putString("radio_interface", "" + radioInterface);
            bundle.putString("brand_name", "" + brandName);
            bundle.putString("model_name", "" + modelName);
            bundle.putString("operating_system", "" + operatingSystem);
            bundle.putString("nfc", "" + nfc);
            bundle.putString("bluetooth", "" + bluetooth);
            bundle.putString("wlan", "" + wlan);
            bundle.putString("device_type", "" + deviceType);
            bundle.putString("simSupport", "" + simSupport);
            bundle.putString("lpwan", "" + lpwan);
            bundle.putString("deviceCertifybody", "" + deviceCertifybody);
            bundle.putString("gsmaApprovedTac", "" + gsmaApprovedTac);

            mProgressDialog.cancel();
            Intent intent = new Intent(getActivity(), ResultDisplayActivity.class);
            intent.putExtras(bundle);
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
            String msgTxt = mUserInput + " " + getResources().getString(R.string.device_notfound_details);
            message.setText(msgTxt);

            alertDialog = reportDialog.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            alertDialog.show();
        }
    }

    // Invoked by presenter to handle failure response by api
    @Override
    public void displayError(Throwable throwable) {
        Utils.showNetworkError(getActivity(), throwable);
    }
}