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

package com.qualcomm.dcp.counterfeit.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.textfield.TextInputLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.ContextWrapper;
import com.qualcomm.dcp.MainNavActivity;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.counterfeit.model.CounterfeitResponse;
import com.qualcomm.dcp.counterfeit.presenter.CounterfeitPresenter;
import com.qualcomm.dcp.utils.ConnectionDetector;
import com.qualcomm.dcp.utils.MyPreferences;
import com.qualcomm.dcp.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.MediaType;
import okhttp3.RequestBody;

// View class for CounterFeit Activity
public class CounterfeitActivity extends AppCompatActivity implements View.OnClickListener, CounterfeitViewInterface {

    // Binding xml views.
    @BindView(R.id.img_text)
    AppCompatButton selectImage;
    @BindView(R.id.device_img)
    ImageButton image0;
    @BindView(R.id.device_img1)
    ImageButton image1;
    @BindView(R.id.device_img2)
    ImageButton image2;
    @BindView(R.id.device_img3)
    ImageButton image3;
    @BindView(R.id.device_img4)
    ImageButton image4;
    @BindView(R.id.img_con)
    HorizontalScrollView imagesCon;
    @BindView(R.id.device_img_remove)
    ImageView close0;
    @BindView(R.id.device_img1_remove)
    ImageView close1;
    @BindView(R.id.device_img2_remove)
    ImageView close2;
    @BindView(R.id.device_img3_remove)
    ImageView close3;
    @BindView(R.id.device_img4_remove)
    ImageView close4;
    @BindView(R.id.brand)
    EditText deviceName;
    @BindView(R.id.model)
    EditText deviceModel;
    @BindView(R.id.description)
    EditText deviceDescription;
    @BindView(R.id.store_name)
    EditText storeName;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.brand_til)
    TextInputLayout deviceNameTil;
    @BindView(R.id.model_til)
    TextInputLayout deviceModelTil;
    @BindView(R.id.description_til)
    TextInputLayout deviceDescriptionTil;
    @BindView(R.id.store_name_til)
    TextInputLayout storeNameTil;
    @BindView(R.id.address_til)
    TextInputLayout addressTil;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private Unbinder mUnbind;

    // Global variables to be used with in this class
    private static final int REQUEST_PERMISSION = 100;
    public static final int REQUEST_CODE_PICKER = 110;

    public ArrayList<Image> images;
    private SweetAlertDialog progressDialog;
    private String mImei = "";
    private boolean mProcessSubmit = true;
    private CounterfeitPresenter mCounterfietPresenter;

    private static Bitmap getThumbnail(ContentResolver cr, String path) {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        if (ca != null) {
            ca.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyPreferences myPreferences = new MyPreferences(this);
        Utils.changeLanguageLocale(getBaseContext(), myPreferences.getString("locale", "en"));
        setContentView(R.layout.activity_add_new_device);
        mUnbind = ButterKnife.bind(this);

        setupUi();
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

    // Invoked upon closing of this activity. View is unbinded from presenter and xml objects
    @Override
    protected void onDestroy() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mCounterfietPresenter != null)
            mCounterfietPresenter.unbind();

        super.onDestroy();
    }

    // For binding this view with its presenter
    private void setupMVP() {
        mCounterfietPresenter = new CounterfeitPresenter(this, this);
    }

    private void setupUi() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        cancel.setVisibility(View.GONE);

        if (getIntent().hasExtra("imei")) {
            mImei = getIntent().getStringExtra("imei");
        }

        selectImage.setOnClickListener(this);
        image0.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    // For validating input fields in counterfeit activity
    private boolean validate() {
        boolean isValid = true;
        if (deviceName.getText().toString().equals("")) {
            isValid = false;
            deviceNameTil.setError(getResources().getString(R.string.can_not_empty));

        } else {
            deviceNameTil.setError(null);
        }
        if (deviceModel.getText().toString().equals("")) {
            isValid = false;
            deviceModelTil.setError(getResources().getString(R.string.can_not_empty));

        } else {
            deviceModelTil.setError(null);
        }
        if (deviceDescription.getText().toString().equals("")) {
            isValid = false;
            deviceDescriptionTil.setError(getResources().getString(R.string.can_not_empty));

        } else {
            deviceDescriptionTil.setError(null);
        }
        if (storeName.getText().toString().equals("")) {
            isValid = false;
            storeNameTil.setError(getResources().getString(R.string.can_not_empty));

        } else {
            storeNameTil.setError(null);
        }
        if (address.getText().toString().equals("")) {
            isValid = false;
            addressTil.setError(getResources().getString(R.string.can_not_empty));

        } else {
            addressTil.setError(null);
        }
        return isValid;
    }

    // Checking if necessary permissions are granted or not
    public void checkForPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                alertDialogBuilder.setTitle(this.getString(R.string.permission_access));
                alertDialogBuilder.setMessage(this.getString(R.string.storage_permission_needed));

                alertDialogBuilder.setPositiveButton(this.getString(R.string.ok), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION);
                });


                alertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), (dialog, which) -> Toast.makeText(this, this.getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.white));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlack));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.white));

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        } else {
            pickImages();
        }
    }

    // Handling permission grant requests result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    pickImages();
                }
            } else {

                Toast.makeText(this, this.getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    // For opening image picker to select images
    private void pickImages() {
        ImagePicker.ImagePickerWithActivity imagePicker = ImagePicker.create(CounterfeitActivity.this);
        imagePicker.limit(5)
                .multi()
                .start(REQUEST_CODE_PICKER);
    }

    // Handling result of image picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            if (images != null)
                images.clear();
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            setSelectedImages();
        } else if (requestCode == REQUEST_PERMISSION) {
            checkForPermission();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.please_report_device_to_close), Toast.LENGTH_SHORT).show();
    }

    // Set selected images from image picker into image view
    public void setSelectedImages() {
        if (images != null) {
            close0.setVisibility(View.GONE);
            close1.setVisibility(View.GONE);
            close2.setVisibility(View.GONE);
            close3.setVisibility(View.GONE);
            close4.setVisibility(View.GONE);

            image0.setVisibility(View.GONE);
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.GONE);
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            imagesCon.setVisibility(View.VISIBLE);

            image0.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
            image1.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
            image2.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
            image3.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
            image4.setImageDrawable(getResources().getDrawable(R.drawable.no_img));

            for (int i = 0, l = images.size(); i < l; i++) {

                try {
                    if (i == 0) {
                        close0.setVisibility(View.VISIBLE);
                        image0.setVisibility(View.VISIBLE);
                        image0.setImageBitmap(getThumbnail(CounterfeitActivity.this.getContentResolver(), images.get(i).getPath()));
                    } else if (i == 1) {
                        close1.setVisibility(View.VISIBLE);
                        image1.setVisibility(View.VISIBLE);
                        image1.setImageBitmap(getThumbnail(CounterfeitActivity.this.getContentResolver(), images.get(i).getPath()));
                    } else if (i == 2) {
                        close2.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.VISIBLE);
                        image2.setImageBitmap(getThumbnail(CounterfeitActivity.this.getContentResolver(), images.get(i).getPath()));
                    } else if (i == 3) {
                        close3.setVisibility(View.VISIBLE);
                        image3.setVisibility(View.VISIBLE);
                        image3.setImageBitmap(getThumbnail(CounterfeitActivity.this.getContentResolver(), images.get(i).getPath()));

                    } else if (i == 4) {
                        close4.setVisibility(View.VISIBLE);
                        image4.setVisibility(View.VISIBLE);
                        image4.setImageBitmap(getThumbnail(CounterfeitActivity.this.getContentResolver(), images.get(i).getPath()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Invoked upon different UI clicks
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.img_text) {
            if (Build.VERSION.SDK_INT >= 23) {
                checkForPermission();
            } else {
                pickImages();
            }

        } else if (view.getId() == R.id.device_img) {
            if (images != null)
                if (images.size() > 0) {
                    close0.setVisibility(View.GONE);
                    image0.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
                    images.remove(0);
                    setSelectedImages();
                }

        } else if (view.getId() == R.id.device_img1) {
            if (images != null)
                if (images.size() > 0) {
                    close1.setVisibility(View.GONE);
                    image1.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
                    images.remove(1);
                    setSelectedImages();
                }

        } else if (view.getId() == R.id.device_img2) {
            if (images != null)
                if (images.size() > 0) {
                    close2.setVisibility(View.GONE);
                    image2.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
                    images.remove(2);
                    setSelectedImages();
                }

        } else if (view.getId() == R.id.device_img3) {
            if (images != null)
                if (images.size() > 0) {
                    close3.setVisibility(View.GONE);
                    image3.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
                    images.remove(3);
                    setSelectedImages();
                }

        } else if (view.getId() == R.id.device_img4) {
            if (images != null)
                if (images.size() > 0) {
                    close4.setVisibility(View.GONE);
                    image4.setImageDrawable(getResources().getDrawable(R.drawable.no_img));
                    images.remove(4);
                    setSelectedImages();
                }

        } else if (view.getId() == R.id.cancel) {
            Intent intent = new Intent(CounterfeitActivity.this, MainNavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            CounterfeitActivity.this.finish();

        } else if (view.getId() == R.id.submit) {
            if (validate()) {
                ConnectionDetector cd = new ConnectionDetector(CounterfeitActivity.this);
                boolean isInternetPresent = cd.isConnectingToInternet();

                if (!isInternetPresent) {
                    Utils.showNoInternetDialog(CounterfeitActivity.this);

                } else {
                    if (mProcessSubmit) {
                        //send network call
                        CounterfeitActivity.this.runOnUiThread(() -> {
                            progressDialog = new SweetAlertDialog(CounterfeitActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
                            progressDialog.setTitleText(getResources().getString(R.string.adding_device_info));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        });
                        String brand = deviceName.getText().toString();
                        String model = deviceModel.getText().toString();
                        String store = storeName.getText().toString();
                        String addres = address.getText().toString();
                        String description = deviceDescription.getText().toString();
                        Map<String, RequestBody> parts = new HashMap<>();
                        if (images == null)
                            images = new ArrayList<>();


                        for (int i = 0; i < images.size(); i++) {
                            File file = new File(images.get(i).getPath());
                            String filename = "counterImage[" + i + "]"; //key for upload file like : imagePath0
                            parts.put(filename + "\"; filename=\"" + file.getName(), RequestBody.create(MediaType.parse("images/*"), file));
                        }
                        mCounterfietPresenter.addCounterfeitDevice(parts, brand, mImei, description, model, store, addres);
                    }
                    mProcessSubmit = false;
                }
            }
        }
    }

    // Invoked by presenter to handle api success response
    @Override
    public void displaySuccess(CounterfeitResponse counterfeitResponse) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        mProcessSubmit = true;

        if (counterfeitResponse.isSuccess()) {
            androidx.appcompat.app.AlertDialog.Builder successDialog = new androidx.appcompat.app.AlertDialog.Builder(CounterfeitActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
            successDialog.setView(dialogView).setPositiveButton(getResources().getString(R.string.ok), (dialog, whichButton) -> {
                dialog.dismiss();
                startActivity(new Intent(CounterfeitActivity.this, MainNavActivity.class));
                CounterfeitActivity.this.finish();
            });

            ImageView icon = dialogView.findViewById(R.id.icon);
            TextView title = dialogView.findViewById(R.id.title);
            TextView message = dialogView.findViewById(R.id.message);

            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_success));
            title.setText(R.string.reported);
            message.setText(counterfeitResponse.getMessage());

            androidx.appcompat.app.AlertDialog alertDialog = successDialog.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            alertDialog.show();

        } else {
            new SweetAlertDialog(CounterfeitActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(counterfeitResponse.getMessage())
                    .show();
        }
    }

    // Invoked by presenter to handle api failure response
    @Override
    public void displayError(Throwable throwable) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        mProcessSubmit = true;
        Utils.showNetworkError(CounterfeitActivity.this, throwable);
    }

    // For showing tooltip for brand
    @OnClick(R.id.openBrandInfo)
    public void openBrandInfo() {
        View yourView = findViewById(R.id.openBrandInfo);
        showToolTip(yourView, getString(R.string.brandInfo));
    }

    // For showing tooltip for model
    @OnClick(R.id.openModelInfo)
    public void openModelInfo() {
        View yourView = findViewById(R.id.openModelInfo);
        showToolTip(yourView, getString(R.string.modelInfo));
    }

    // For showing tooltip for store
    @OnClick(R.id.openStoreInfo)
    public void openStoreInfo() {
        View yourView = findViewById(R.id.openStoreInfo);
        showToolTip(yourView, getString(R.string.storeInfo));
    }

    // For showing tooltip for address
    @OnClick(R.id.openAddressInfo)
    public void openAddressInfo() {
        View yourView = findViewById(R.id.openAddressInfo);
        showToolTip(yourView, getString(R.string.addressInfo));
    }

    // For showing tooltip for description
    @OnClick(R.id.openDescrptionInfo)
    public void openDescrptionInfo() {
        View yourView = findViewById(R.id.openDescrptionInfo);
        showToolTip(yourView, getString(R.string.desInfo));
    }

    // For showing tooltip for image
    @OnClick(R.id.openImagesInfo)
    public void openImagesInfo() {
        View yourView = findViewById(R.id.openImagesInfo);
        showToolTip(yourView, getString(R.string.imagesInfo));
    }

    // Method to display tooltip
    private void showToolTip(View yourView, String message) {
        new SimpleTooltip.Builder(this)
                .anchorView(yourView)
                .text(message)
                .gravity(Gravity.BOTTOM)
                .textColor(getResources().getColor(R.color.ef_white))
                .backgroundColor(getResources().getColor(R.color.text_color_dark))
                .arrowColor(getResources().getColor(R.color.text_color_dark))
                .animated(false)
                .transparentOverlay(true)
                .build()
                .show();
    }
}