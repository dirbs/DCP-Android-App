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

package com.qualcomm.dcp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qualcomm.dcp.R;
import com.qualcomm.dcp.login.view.LoginView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.annotations.NonNull;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by Hamza on 21/03/2017.
 */
public class Utils {

    // For changing application language
    public static void changeLanguageLocale(Context baseContext, String languageToChange) {

        Locale locale = new Locale(languageToChange);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics());
    }

    // For showing no internet connection dialog
    public static void showNoInternetDialog(final Activity context) {

        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        logoutDialog.setView(dialogView).setPositiveButton(context.getResources().getString(R.string.yes_enable), (dialog, whichButton) -> {
            dialog.dismiss();
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

        })
                .setNegativeButton(context.getResources().getString(R.string.no), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error));
        title.setText(R.string.no_internet);
        message.setText(R.string.error_network_error);


        androidx.appcompat.app.AlertDialog alertDialog = logoutDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


        alertDialog.show();
    }

    // For showing error dialog
    private static void showErrorDialog(final Activity context, String messageStr, String titleStr) {

        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        logoutDialog.setView(dialogView).setPositiveButton(context.getResources().getString(R.string.ok), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error));
        title.setText(titleStr);
        message.setText(messageStr);


        androidx.appcompat.app.AlertDialog alertDialog = logoutDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


        alertDialog.show();
    }

    // For showing logout confirmation dialog
    public static void showLogoutConfirmationDialog(final Activity context) {

        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        logoutDialog.setView(dialogView).setPositiveButton(context.getResources().getString(R.string.yes), (dialog, whichButton) -> {
            dialog.dismiss();
            UserSessionManager userSessionManager = new UserSessionManager(context);
            userSessionManager.logout();
            Intent intent = new Intent(context, LoginView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        })
                .setNegativeButton(context.getResources().getString(R.string.no), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logout));
        title.setText(R.string.logout);
        message.setText(R.string.sure_to_logout);


        androidx.appcompat.app.AlertDialog alertDialog = logoutDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


        alertDialog.show();
    }

    // For getting image URI
    public static void getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    // For showing network error
    public static void showNetworkError(final Activity context, Throwable throwable) {
        // We had non-200 http error
        if (context != null) {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                if (response != null) {
                    if (response.code() == 401) {
                        if (context.getClass().getSimpleName().equals("LoginView"))//show different error on login for 401
                        {

                            try {
                                String res = null;
                                if (response.errorBody() != null) {
                                    res = response.errorBody().string();
                                    Log.e("res", res);
                                }
                                JSONObject jsonObject = null;
                                if (res != null) {
                                    jsonObject = new JSONObject(res);
                                }

                                if (jsonObject != null) {
                                    Utils.showErrorDialog(context, jsonObject.getString("message"), context.getResources().getString(R.string.oops));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Utils.showSessionExpireDialog(context);
                        }
                    } else if (response.code() == 422) {
                        try {
                            String res = null;
                            if (response.errorBody() != null) {
                                res = response.errorBody().string();
                            }
                            if (res != null) {
                                Log.e("res", res);
                            }
                            JSONObject jsonObject = null;
                            if (res != null) {
                                jsonObject = new JSONObject(res);
                            }
                            Iterator<String> iter;
                            StringBuilder errors = new StringBuilder();
                            if (jsonObject != null) {
                                if (jsonObject.has("errors")) {
                                    iter = Objects.requireNonNull(jsonObject.optJSONObject("errors")).keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        for (int i = 0; i < Objects.requireNonNull(jsonObject.optJSONObject("errors")).getJSONArray(key).length(); i++) {
                                            errors.append("- ").append(Objects.requireNonNull(jsonObject.optJSONObject("errors")).getJSONArray(key).getString(0)).append("\n");
                                        }
                                    }
                                } else if (jsonObject.has("ip")) {
                                    iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        for (int i = 0; i < jsonObject.getJSONArray(key).length(); i++) {
                                            errors.append("- ").append(jsonObject.getJSONArray(key).getString(0)).append("\n");
                                        }
                                    }
                                }
                            }
                            Utils.showErrorDialog(context, errors.toString(), context.getResources().getString(R.string.oops));

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 403) {
                        try {
                            String res;
                            JSONObject jsonObject = null;
                            if (response.errorBody() != null) {
                                res = response.errorBody().string();
                                Log.e("res", res);
                                jsonObject = new JSONObject(res);

                            }

                            if (jsonObject != null) {
                                Utils.showErrorDialog(context, jsonObject.getString("message"), context.getResources().getString(R.string.oops));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() >= 500 && response.code() < 600) {
                        Utils.showErrorDialog(context, context.getString(R.string.error_server_error), context.getResources().getString(R.string.oops));
                    } else {
                        Utils.showErrorDialog(context, context.getString(R.string.error_server_error), context.getResources().getString(R.string.oops));
                    }
                }

            }
            // A network error happened
            else if (throwable instanceof IOException) {
                Utils.showErrorDialog(context, context.getString(R.string.error_server_error), context.getResources().getString(R.string.oops));
            } else {
                Utils.showErrorDialog(context, context.getString(R.string.error_server_error), context.getResources().getString(R.string.oops));
            }

            // We don't know what happened. We need to simply convert to an unknown error
        }
    }

    // For showing session expiry dialog
    private static void showSessionExpireDialog(Activity context) {

        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        logoutDialog.setView(dialogView).setPositiveButton(context.getResources().getString(R.string.ok), (dialog, whichButton) -> {
            dialog.dismiss();
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            UserSessionManager userSessionManager = new UserSessionManager(context);
            userSessionManager.logout();
            Intent intent = new Intent(context, LoginView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_warn));
        title.setText(R.string.session_expired);
        message.setText(R.string.session_expired_detail);


        androidx.appcompat.app.AlertDialog alertDialog = logoutDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


        alertDialog.show();
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}



