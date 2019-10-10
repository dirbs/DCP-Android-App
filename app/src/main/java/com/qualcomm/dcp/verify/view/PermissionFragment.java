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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.qualcomm.dcp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for getting camera permission
public class PermissionFragment extends Fragment implements View.OnClickListener {
    // Binding views
    @BindView(R.id.btn_check)
    public Button checkButton;

    private ScannerTab mScannerTab;
    private static final int REQUEST_PERMISSION_CAMERA = 100;

    private Unbinder mUnbind;
    private FragmentManager mFragmentManager;

    public PermissionFragment(){}

    public PermissionFragment(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permission, container, false);
        mUnbind = ButterKnife.bind(this, view);
        mScannerTab = new ScannerTab();
        initViews();
        return view;
    }

    // Upon closing of this fragment views are unbidden and presenter is detached
    @Override
    public void onDestroyView() {
        if (mUnbind != null)
            mUnbind.unbind();
        super.onDestroyView();
    }

    // For initializing views
    private void initViews() {
        checkButton.setOnClickListener(this);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    // For handling clicks on views present in this view
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_check) {
            checkCameraPermission();
        }
    }

    private void checkCameraPermission() {

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.AlertDialogTheme);
                alertDialogBuilder.setTitle(Objects.requireNonNull(getActivity()).getString(R.string.permission_access));
                alertDialogBuilder.setMessage(Objects.requireNonNull(getActivity()).getString(R.string.camera_permission_needed));

                alertDialogBuilder.setPositiveButton(Objects.requireNonNull(getActivity()).getString(R.string.ok), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_CAMERA);
                });

                alertDialogBuilder.setNegativeButton(Objects.requireNonNull(getActivity()).getString(R.string.cancel), (dialog, which) -> Toast.makeText(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getActivity()).getString(R.string.permission_denied), Toast.LENGTH_SHORT).show());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.white));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlack));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.white));

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        } else {
            if (getParentFragment() != null && getParentFragment().getFragmentManager() != null) {
                mScannerTab.permissionCallback(mFragmentManager);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (getParentFragment() != null && getParentFragment().getFragmentManager() != null) {
                        mScannerTab.permissionCallback(mFragmentManager);
                    }
                }
            } else {

                Toast.makeText(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getActivity()).getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result codes
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            checkCameraPermission();
        }

        // call super method to ensure unhandled result codes are handled
        super.onActivityResult(requestCode, resultCode, data);

    }

}
