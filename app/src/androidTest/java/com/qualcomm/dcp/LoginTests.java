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

package com.qualcomm.dcp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.license.view.LicenseAgreementActivity;
import com.qualcomm.dcp.login.model.CurrentActiveLicense;
import com.qualcomm.dcp.login.model.Data;
import com.qualcomm.dcp.login.model.ForgotPasswordResponse;
import com.qualcomm.dcp.login.model.LicensesItem;
import com.qualcomm.dcp.login.model.LoginResponse;
import com.qualcomm.dcp.login.model.Meta;
import com.qualcomm.dcp.login.model.Pivot;
import com.qualcomm.dcp.login.model.RolesItem;
import com.qualcomm.dcp.login.view.LoginView;
import com.qualcomm.dcp.utils.Constants;
import com.qualcomm.dcp.utils.UserSessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Hamza on 24/04/2017.
 */

// Unit tests for login activity
@RunWith(AndroidJUnit4.class)
public class LoginTests {

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Rule
    public final ActivityTestRule<LoginView> loginActivityTestRule =
            new ActivityTestRule<>(LoginView.class, true, false);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void setUp() {
        Constants.BASE_URL = "http://localhost:8000";
    }

    @After
    public void restLoginUrl() {
        Constants.BASE_URL = BuildConfig.BASE_URL;
        mMockServerRule.stopServer();
    }


    private final String loginResponseStart = "{\n" +
            "    \"data\": {\n" +
            "        \"id\": 6,\n" +
            "        \"first_name\": \"first\",\n" +
            "        \"last_name\": \"last\",\n" +
            "        \"email\": \"email@test.com\",\n" +
            "        \"avatar\": \"default.jpg\",\n" +
            "        \"loginCount\": 0,\n" +
            "        \"agreement\": \"Agreed\",\n" +
            "        \"created_at\": \"2019-01-14 13:05:15\",\n" +
            "        \"updated_at\": \"2019-01-16 05:39:41\",\n" +
            "        \"active\": true,\n" +
            "        \"activation_token\": null,\n" +
            "        \"is_verified\": false,\n" +
            "        \"roles\": [\n" +
            "            {\n" +
            "                \"id\": 1,\n" +
            "                \"slug\": \"staff\",\n" +
            "                \"name\": \"Moderator Staff\",\n" +
            "                \"created_at\": \"2018-12-18 10:02:14\",\n" +
            "                \"updated_at\": \"2018-12-18 10:02:14\",\n" +
            "                \"pivot\": {\n" +
            "                    \"user_id\": 6,\n" +
            "                    \"role_id\": 1\n" +
            "                }\n" +
            "            }\n" +
            "        ],\n" +
            "        \"licenses\": [\n" +
            "            {\n" +
            "                \"id\": 3,\n" +
            "                \"content\": \"test license\",\n" +
            "                \"version\": \"2.0\",\n" +
            "                \"user_id\": 1,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"created_at\": \"2019-01-14 13:06:56\",\n" +
            "                \"updated_at\": \"2019-01-14 13:06:56\",\n" +
            "                \"pivot\": {\n" +
            "                    \"user_id\": 6,\n" +
            "                    \"license_agreement_id\": 2,\n" +
            "                    \"created_at\": \"2019-01-16 05:39:41\",\n" +
            "                    \"updated_at\": \"2019-01-16 05:39:41\",\n" +
            "                    \"type\": \"login\"\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"roles\": [\n" +
            "        {\n" +
            "            \"id\": 1,\n" +
            "            \"slug\": \"staff\",\n" +
            "            \"name\": \"Moderator Staff\",\n" +
            "            \"created_at\": \"2018-12-18 10:02:14\",\n" +
            "            \"updated_at\": \"2018-12-18 10:02:14\",\n" +
            "            \"pivot\": {\n" +
            "                \"user_id\": 6,\n" +
            "                \"role_id\": 1\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"licenses\": [\n" +
            "        {\n" +
            "            \"id\": 3,\n" +
            "            \"content\": \"test license\",\n" +
            "            \"version\": \"2.0\",\n" +
            "            \"user_id\": 1,\n" +
            "            \"user_name\": \"dcp\",\n" +
            "            \"created_at\": \"2019-01-14 13:06:56\",\n" +
            "            \"updated_at\": \"2019-01-14 13:06:56\",\n" +
            "            \"pivot\": {\n" +
            "                \"user_id\": 6,\n" +
            "                \"license_agreement_id\": 2,\n" +
            "                \"created_at\": \"2019-01-16 05:39:41\",\n" +
            "                \"updated_at\": \"2019-01-16 05:39:41\",\n" +
            "                \"type\": \"login\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"current_active_license\": {\n" +
            "        \"id\": 3,\n" +
            "        \"content\": \"test license\",\n" +
            "        \"version\": \"3.0\",\n" +
            "        \"user_id\": 1,\n" +
            "        \"user_name\": \"dcp\",\n" +
            "        \"created_at\": \"2019-01-18 12:20:37\",\n" +
            "        \"updated_at\": \"2019-01-18 12:20:37\"\n" +
            "    },\n" +
            "    \"meta\": {\n" +
            "        \"token\": \"test token\"\n" +
            "    }\n" +
            "}";

    // For clearing user session
    public void clearLoginSession() {

        UserSessionManager userSessionManager = new UserSessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        userSessionManager.logout();

        Intent intent = new Intent();
        loginActivityTestRule.launchActivity(intent);
    }

    // Unit test for change language button
    @Test
    public void changeLanguage() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.translateBtn)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    // Unit test for valid email
    @Test
    public void validEmail() {
        clearLoginSession();
        //can be replaced with any valid email
        String email = "abc@test.com";

        //find enter email edit text , populate it with email and perform click on check login
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText(email), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_username)).check
                (matches(Utils.hasTextInputLayoutErrorText(null)));

    }

    // Unit test for invalid email
    @Test
    public void invalidEmail() {
        clearLoginSession();
        //can be replaced with any invalid email
        String email = "abc";

        //find enter email edit text , populate it with email and perform click on check login
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText(email), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_username)).check
                (matches(Utils.hasTextInputLayoutErrorText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.email_invalid))));

    }

    // Unit test for empty email
    @Test
    public void emptyEmail() {
        clearLoginSession();
        //must be an empty string
        String email = "";
        //find enter email edit text , populate it with email and perform click on check login
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText(email), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_username)).check
                (matches(Utils.hasTextInputLayoutErrorText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.can_not_empty))));

    }

    // Unit test emailTil
    @Test
    public void emailTilUITest() {
        clearLoginSession();

        //check if the hint text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_username)).check
                (matches(Utils.hasTextInputLayoutHintText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.username))));
    }

    // Unit test for valid password
    @Test
    public void validPassword() {
        clearLoginSession();
        //can be replaced with any 6 digit password
        String password = "abc@test.com";

        //find enter password edit text , populate it with email and perform click on check login
        EditText editText = loginActivityTestRule.getActivity().findViewById(com.qualcomm.dcp.R.id.input_username);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText(password), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_password)).check
                (matches(Utils.hasTextInputLayoutErrorText(null)));

    }

    // Unit test for invalid password
    @Test
    public void invalidPassword() {
        clearLoginSession();
        //can be replaced with any invalid password
        String password = "abc";
        //find enter password edit text , populate it with email and perform click on check login
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText(password), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_password)).check
                (matches(Utils.hasTextInputLayoutErrorText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.password_should_be_min_six))));

    }

    // Unit test empty password
    @Test
    public void emptyPassword() {
        clearLoginSession();
        //must be an empty string
        String password = "";
        //find enter password edit text , populate it with email and perform click on check login
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText(password), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_password)).check
                (matches(Utils.hasTextInputLayoutErrorText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.can_not_empty))));

    }

    // Unit test for passwordTil
    @Test
    public void passwordTilUITest() {
        clearLoginSession();

        //check if the hint text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.til_password)).check
                (matches(Utils.hasTextInputLayoutHintText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.password))));
    }

    // Unit test for login button
    @Test
    public void loginBtnTest() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText(""), closeSoftKeyboard());
        //check if the hint text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.title_activity_login)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed()));

    }

    // Unit test for forgot password dialog invalid email
    @Test
    public void forgotPasswordInvalidEmailTest() {
        clearLoginSession();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_btn))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final androidx.appcompat.app.AlertDialog dialog = loginActivityTestRule.getActivity().getForgotPasswordDialog();

        if (dialog.isShowing()) {
            try {
                onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_pass_email_et)).perform(typeText("abc"), closeSoftKeyboard());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_email_til)).check
                        (matches(Utils.hasTextInputLayoutErrorText(loginActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.email_invalid))));

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    // Unit test for forgot password dialog valid email
    @Test
    public void forgotPasswordValidEmailTest() {
        clearLoginSession();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_btn))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final androidx.appcompat.app.AlertDialog dialog = loginActivityTestRule.getActivity().getForgotPasswordDialog();

        if (dialog.isShowing()) {
            try {
                onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_pass_email_et)).perform(typeText("abc@test.com"), closeSoftKeyboard());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_email_til)).check
                        (matches(Utils.hasTextInputLayoutErrorText(null)));

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    // Unit test for forgot password dialog button
    @Test
    public void forgotPasswordBtnUITest() {
        clearLoginSession();
        //check if the hint text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_btn)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.forgot_password)));

    }

    // For showing loading
    private void displayLoading() {
        loginActivityTestRule.getActivity().mProgressDialog = new SweetAlertDialog(loginActivityTestRule.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        loginActivityTestRule.getActivity().mProgressDialog.getProgressHelper().setBarColor(loginActivityTestRule.getActivity().getResources().getColor(com.qualcomm.dcp.R.color.loading));
        loginActivityTestRule.getActivity().mProgressDialog.setTitleText(loginActivityTestRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.checking));
        loginActivityTestRule.getActivity().mProgressDialog.setCancelable(false);
        loginActivityTestRule.getActivity().mProgressDialog.show();
    }

    // Unit test for open license activity
    @Test
    public void openLicenseAgreementTest() {
        String loginResponseLicense = "{\n" +
                "    \"data\": {\n" +
                "        \"id\": 6,\n" +
                "        \"first_name\": \"first\",\n" +
                "        \"last_name\": \"last\",\n" +
                "        \"email\": \"email@test.com\",\n" +
                "        \"avatar\": \"default.jpg\",\n" +
                "        \"loginCount\": 0,\n" +
                "        \"agreement\": \"Agreed\",\n" +
                "        \"created_at\": \"2019-01-14 13:05:15\",\n" +
                "        \"updated_at\": \"2019-01-16 05:39:41\",\n" +
                "        \"active\": true,\n" +
                "        \"activation_token\": null,\n" +
                "        \"is_verified\": false,\n" +
                "        \"roles\": [\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"slug\": \"staff\",\n" +
                "                \"name\": \"Moderator Staff\",\n" +
                "                \"created_at\": \"2018-12-18 10:02:14\",\n" +
                "                \"updated_at\": \"2018-12-18 10:02:14\",\n" +
                "                \"pivot\": {\n" +
                "                    \"user_id\": 6,\n" +
                "                    \"role_id\": 1\n" +
                "                }\n" +
                "            }\n" +
                "        ],\n" +
                "        \"licenses\": [\n" +
                "            {\n" +
                "                \"id\": 2,\n" +
                "                \"content\": \"test license\",\n" +
                "                \"version\": \"2.0\",\n" +
                "                \"user_id\": 1,\n" +
                "                \"user_name\": \"dcp\",\n" +
                "                \"created_at\": \"2019-01-14 13:06:56\",\n" +
                "                \"updated_at\": \"2019-01-14 13:06:56\",\n" +
                "                \"pivot\": {\n" +
                "                    \"user_id\": 6,\n" +
                "                    \"license_agreement_id\": 2,\n" +
                "                    \"created_at\": \"2019-01-16 05:39:41\",\n" +
                "                    \"updated_at\": \"2019-01-16 05:39:41\",\n" +
                "                    \"type\": \"login\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"roles\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"slug\": \"staff\",\n" +
                "            \"name\": \"Moderator Staff\",\n" +
                "            \"created_at\": \"2018-12-18 10:02:14\",\n" +
                "            \"updated_at\": \"2018-12-18 10:02:14\",\n" +
                "            \"pivot\": {\n" +
                "                \"user_id\": 6,\n" +
                "                \"role_id\": 1\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"licenses\": [\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"content\": \"test license\",\n" +
                "            \"version\": \"2.0\",\n" +
                "            \"user_id\": 1,\n" +
                "            \"user_name\": \"dcp\",\n" +
                "            \"created_at\": \"2019-01-14 13:06:56\",\n" +
                "            \"updated_at\": \"2019-01-14 13:06:56\",\n" +
                "            \"pivot\": {\n" +
                "                \"user_id\": 6,\n" +
                "                \"license_agreement_id\": 2,\n" +
                "                \"created_at\": \"2019-01-16 05:39:41\",\n" +
                "                \"updated_at\": \"2019-01-16 05:39:41\",\n" +
                "                \"type\": \"login\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"current_active_license\": {\n" +
                "        \"id\": 3,\n" +
                "        \"content\": \"test license\",\n" +
                "        \"version\": \"3.0\",\n" +
                "        \"user_id\": 1,\n" +
                "        \"user_name\": \"dcp\",\n" +
                "        \"created_at\": \"2019-01-18 12:20:37\",\n" +
                "        \"updated_at\": \"2019-01-18 12:20:37\"\n" +
                "    },\n" +
                "    \"meta\": {\n" +
                "        \"token\": \"test token\"\n" +
                "    }\n" +
                "}";
        clearLoginSession();
        Gson gson = new Gson();
        final LoginResponse loginResponse = gson.fromJson(loginResponseLicense, LoginResponse.class);

        try {
            loginActivityTestRule.runOnUiThread(() -> {
                Intents.init();
                displayLoading();
                loginActivityTestRule.getActivity().displaySuccess(loginResponse);
                intended(hasComponent(LicenseAgreementActivity.class.getName()));
                Intents.release();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for opening start activity
    @Test
    public void openStartActivityTest() {
        clearLoginSession();
        Gson gson = new Gson();
        final LoginResponse loginResponse = gson.fromJson(loginResponseStart, LoginResponse.class);

        try {
            loginActivityTestRule.runOnUiThread(() -> {
                Intents.init();
                displayLoading();
                loginActivityTestRule.getActivity().displaySuccess(loginResponse);
                intended(hasComponent(MainNavActivity.class.getName()));
                Intents.release();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for success login request
    @Test
    public void testLoginCallOkStart() {
        Intents.init();
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherStart());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        Intents.release();
    }

    // Unit test for unauthorized login request
    @Test
    public void testLoginCallUnauthorized() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherUnauthorized());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        onView(withText("These credentials do not match our records.")).check(matches(isDisplayed()));
    }

    // Unit test for un processable login request
    @Test
    public void testLoginCallUnProcessable() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherUnProcessable());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.oops)).check(matches(isDisplayed()));
    }

    // Unit test for forbidden login request
    @Test
    public void testLoginCallForbidden() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherForbidden());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.oops)).check(matches(isDisplayed()));
    }

    // Unit test for server error login request
    @Test
    public void testLoginCallServerError() {
        clearLoginSession();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherServerError());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.error_server_error)).check(matches(isDisplayed()));
    }

    // Unit test for network error login request
    @Test
    public void testLoginCallNetworkError() {
        clearLoginSession();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_username)).perform(typeText("email@test.com"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.input_password)).perform(typeText("123456"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherNetworkError());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_login)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.oops)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for success response of login request
    private Dispatcher getDispatcherStart() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(loginResponseStart);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher for unauthorized response of login request
    private Dispatcher getDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(401)
                            .setBody("{\n" +
                                    "    \"errors\": true,\n" +
                                    "    \"message\": \"These credentials do not match our records.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher for forbidden response of login request
    private Dispatcher getDispatcherForbidden() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(403)
                            .setBody("{\n" +
                                    "    \"errors\": true,\n" +
                                    "    \"message\": \"These credentials do not match our records.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher for server error response of login request
    private Dispatcher getDispatcherServerError() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(500)
                            .setBody("{\n" +
                                    "    \"errors\": true,\n" +
                                    "    \"message\": \"These credentials do not match our records.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher for network error response of login request
    private Dispatcher getDispatcherNetworkError() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(404)
                            .setBody("{\n" +
                                    "    \"errors\": true,\n" +
                                    "    \"message\": \"These credentials do not match our records.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher for un processable response of login request
    private Dispatcher getDispatcherUnProcessable() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/login")) {
                    return new MockResponse().setResponseCode(422)
                            .setBody("{\n" +
                                    "    \"message\": \"The given data was invalid.\",\n" +
                                    "    \"errors\": {\n" +
                                    "        \"email\": [\n" +
                                    "            \"Email is invalid\"\n" +
                                    "        ]\n" +
                                    "    }\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for forgot password request
    @Test
    public void testForgotPasswordCall() {
        clearLoginSession();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_btn))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final androidx.appcompat.app.AlertDialog dialog = loginActivityTestRule.getActivity().getForgotPasswordDialog();
        if (dialog.isShowing()) {
            onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_pass_email_et)).perform(typeText("abc@test.com"), closeSoftKeyboard());
            mMockServerRule.server().setDispatcher(getDispatcherForgotPassword());
            try {
                loginActivityTestRule.runOnUiThread(() -> dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.reset_email_sent)).check
                        (matches(isDisplayed()));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    // Mock server dispatcher for success response of forgot password request
    private Dispatcher getDispatcherForgotPassword() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/recover")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody("{\n" +
                                    "    \"error\": false,\n" +
                                    "    \"message\": \"We have e-mailed your password reset link!\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for forgot password unauthorized request
    @Test
    public void testForgotPasswordCallUnauthorized() {
        clearLoginSession();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_password_btn))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final androidx.appcompat.app.AlertDialog dialog = loginActivityTestRule.getActivity().getForgotPasswordDialog();

        if (dialog.isShowing()) {
            onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.forgot_pass_email_et)).perform(typeText("abc@test.com"), closeSoftKeyboard());
            mMockServerRule.server().setDispatcher(getDispatcherForgotPasswordUnauthorized());
            try {
                loginActivityTestRule.runOnUiThread(() -> dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                onView(withText("Email not present")).check
                        (matches(isDisplayed()));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


        }
    }

    // Mock server dispatcher for unauthorized response of forgot password request
    private Dispatcher getDispatcherForgotPasswordUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/recover")) {
                    return new MockResponse().setResponseCode(401)
                            .setBody("{\n" +
                                    "    \"error\": true,\n" +
                                    "    \"message\": \"Email not present\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for login model class
    @Test
    public void testLoginModel() {
        Gson gson = new Gson();
        final LoginResponse loginResponse = gson.fromJson(loginResponseStart, LoginResponse.class);

        assertEquals(loginResponse.toString(), "LoginResponse{" +
                "licenses = '" + loginResponse.getLicenses() + '\'' +
                ",current_active_license = '" + loginResponse.getCurrentActiveLicense() + '\'' +
                ",data = '" + loginResponse.getData() + '\'' +
                ",meta = '" + loginResponse.getMeta() + '\'' +
                ",roles = '" + loginResponse.getRoles() + '\'' +
                "}");
        assertEquals(loginResponse.getLicenses().get(0).toString(), "LicensesItem{" +
                "updated_at = '" + loginResponse.getLicenses().get(0).getUpdatedAt() + '\'' +
                ",user_id = '" + loginResponse.getLicenses().get(0).getUserId() + '\'' +
                ",user_name = '" + loginResponse.getLicenses().get(0).getUserName() + '\'' +
                ",created_at = '" + loginResponse.getLicenses().get(0).getCreatedAt() + '\'' +
                ",pivot = '" + loginResponse.getLicenses().get(0).getPivot() + '\'' +
                ",id = '" + loginResponse.getLicenses().get(0).getId() + '\'' +
                ",version = '" + loginResponse.getLicenses().get(0).getVersion() + '\'' +
                ",content = '" + loginResponse.getLicenses().get(0).getContent() + '\'' +
                "}");
        assertEquals(loginResponse.getCurrentActiveLicense().toString(), "CurrentActiveLicense{" +
                "updated_at = '" + loginResponse.getCurrentActiveLicense().getUpdatedAt() + '\'' +
                ",user_id = '" + loginResponse.getCurrentActiveLicense().getUserId() + '\'' +
                ",user_name = '" + loginResponse.getCurrentActiveLicense().getUserName() + '\'' +
                ",created_at = '" + loginResponse.getCurrentActiveLicense().getCreatedAt() + '\'' +
                ",id = '" + loginResponse.getCurrentActiveLicense().getId() + '\'' +
                ",version = '" + loginResponse.getCurrentActiveLicense().getVersion() + '\'' +
                ",content = '" + loginResponse.getCurrentActiveLicense().getContent() + '\'' +
                "}");
        assertEquals(loginResponse.getData().toString(), "Data{" +
                "agreement = '" + loginResponse.getData().getAgreement() + '\'' +
                ",roles = '" + loginResponse.getData().getRoles() + '\'' +
                ",last_name = '" + loginResponse.getData().getLastName() + '\'' +
                ",created_at = '" + loginResponse.getData().getCreatedAt() + '\'' +
                ",active = '" + loginResponse.getData().isActive() + '\'' +
                ",avatar = '" + loginResponse.getData().getAvatar() + '\'' +
                ",is_verified = '" + loginResponse.getData().isIsVerified() + '\'' +
                ",loginCount = '" + loginResponse.getData().getLoginCount() + '\'' +
                ",activation_token = '" + loginResponse.getData().getActivationToken() + '\'' +
                ",licenses = '" + loginResponse.getData().getLicenses() + '\'' +
                ",updated_at = '" + loginResponse.getData().getUpdatedAt() + '\'' +
                ",id = '" + loginResponse.getData().getId() + '\'' +
                ",first_name = '" + loginResponse.getData().getFirstName() + '\'' +
                ",email = '" + loginResponse.getData().getEmail() + '\'' +
                "}");
        assertEquals(loginResponse.getData().toString(), "Data{" +
                "agreement = '" + loginResponse.getData().getAgreement() + '\'' +
                ",roles = '" + loginResponse.getData().getRoles() + '\'' +
                ",last_name = '" + loginResponse.getData().getLastName() + '\'' +
                ",created_at = '" + loginResponse.getData().getCreatedAt() + '\'' +
                ",active = '" + loginResponse.getData().isActive() + '\'' +
                ",avatar = '" + loginResponse.getData().getAvatar() + '\'' +
                ",is_verified = '" + loginResponse.getData().isIsVerified() + '\'' +
                ",loginCount = '" + loginResponse.getData().getLoginCount() + '\'' +
                ",activation_token = '" + loginResponse.getData().getActivationToken() + '\'' +
                ",licenses = '" + loginResponse.getData().getLicenses() + '\'' +
                ",updated_at = '" + loginResponse.getData().getUpdatedAt() + '\'' +
                ",id = '" + loginResponse.getData().getId() + '\'' +
                ",first_name = '" + loginResponse.getData().getFirstName() + '\'' +
                ",email = '" + loginResponse.getData().getEmail() + '\'' +
                "}");
        assertEquals(loginResponse.getMeta().toString(), "Meta{" +
                "token = '" + loginResponse.getMeta().getToken() + '\'' +
                "}");
        assertEquals(loginResponse.getRoles().get(0).toString(), "RolesItem{" +
                "updated_at = '" + loginResponse.getRoles().get(0).getUpdatedAt() + '\'' +
                ",name = '" + loginResponse.getRoles().get(0).getName() + '\'' +
                ",created_at = '" + loginResponse.getRoles().get(0).getCreatedAt() + '\'' +
                ",pivot = '" + loginResponse.getRoles().get(0).getPivot() + '\'' +
                ",id = '" + loginResponse.getRoles().get(0).getId() + '\'' +
                ",slug = '" + loginResponse.getRoles().get(0).getSlug() + '\'' +
                "}");

        CurrentActiveLicense currentActiveLicense = new CurrentActiveLicense();
        ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();
        Meta meta = new Meta();
        Pivot pivot = new Pivot();
        RolesItem rolesItem = new RolesItem();
        LicensesItem licensesItem = new LicensesItem();
        Data data = new Data();
        LoginResponse loginResp = new LoginResponse();

        currentActiveLicense.setUpdatedAt("A");
        currentActiveLicense.setUserId(0);
        currentActiveLicense.setUserName("B");
        currentActiveLicense.setCreatedAt("C");
        currentActiveLicense.setId(1);
        currentActiveLicense.setVersion("D");
        currentActiveLicense.setContent("E");

        assertEquals("A", currentActiveLicense.getUpdatedAt());
        assertEquals(0, currentActiveLicense.getUserId());
        assertEquals("B", currentActiveLicense.getUserName());
        assertEquals("C", currentActiveLicense.getCreatedAt());
        assertEquals(1, currentActiveLicense.getId());
        assertEquals("D", currentActiveLicense.getVersion());
        assertEquals("E", currentActiveLicense.getContent());

        forgotPasswordResponse.setError(true);
        forgotPasswordResponse.setMessage("A");

        assertTrue(forgotPasswordResponse.isError());
        assertEquals("A", forgotPasswordResponse.getMessage());

        meta.setToken("A");

        assertEquals("A", meta.getToken());

        pivot.setRoleId(0);
        pivot.setUserId(1);

        assertEquals(0, pivot.getRoleId());
        assertEquals(1, pivot.getUserId());

        rolesItem.setUpdatedAt("A");
        rolesItem.setName("B");
        rolesItem.setCreatedAt("C");
        rolesItem.setPivot(pivot);
        rolesItem.setId(0);
        rolesItem.setSlug("D");

        assertEquals("A", rolesItem.getUpdatedAt());
        assertEquals("B", rolesItem.getName());
        assertEquals("C", rolesItem.getCreatedAt());
        assertEquals(pivot, rolesItem.getPivot());
        assertEquals(0, rolesItem.getId());
        assertEquals("D", rolesItem.getSlug());

        licensesItem.setUpdatedAt("A");
        licensesItem.setUserId(0);
        licensesItem.setUserName("B");
        licensesItem.setCreatedAt("C");
        licensesItem.setPivot(pivot);
        licensesItem.setId(1);
        licensesItem.setVersion("D");
        licensesItem.setContent("E");

        assertEquals("A", licensesItem.getUpdatedAt());
        assertEquals(0, licensesItem.getUserId());
        assertEquals("B", licensesItem.getUserName());
        assertEquals("C", licensesItem.getCreatedAt());
        assertEquals(pivot, licensesItem.getPivot());
        assertEquals(1, licensesItem.getId());
        assertEquals("D", licensesItem.getVersion());
        assertEquals("E", licensesItem.getContent());

        ArrayList<RolesItem> tempRolesItemsList = new ArrayList<>();
        ArrayList<LicensesItem> tempLicensesItemList = new ArrayList<>();

        data.setAgreement("A");
        data.setRoles(tempRolesItemsList);
        data.setLastName("B");
        data.setCreatedAt("C");
        data.setActive(true);
        data.setAvatar("D");
        data.setIsVerified(true);
        data.setLoginCount(0);
        data.setActivationToken("E");
        data.setLicenses(tempLicensesItemList);
        data.setUpdatedAt("F");
        data.setId(1);
        data.setFirstName("G");
        data.setEmail("H");

        assertEquals("A", data.getAgreement());
        assertEquals(tempRolesItemsList, data.getRoles());
        assertEquals("B", data.getLastName());
        assertEquals("C", data.getCreatedAt());
        assertTrue(data.isActive());
        assertEquals("D", data.getAvatar());
        assertTrue(data.isIsVerified());
        assertEquals(0, data.getLoginCount());
        assertEquals("E", data.getActivationToken());
        assertEquals(tempLicensesItemList, data.getLicenses());
        assertEquals("F", data.getUpdatedAt());
        assertEquals(1, data.getId());
        assertEquals("G", data.getFirstName());
        assertEquals("H", data.getEmail());

        loginResp.setLicenses(tempLicensesItemList);
        loginResp.setCurrentActiveLicense(currentActiveLicense);
        loginResp.setData(data);
        loginResp.setMeta(meta);
        loginResp.setRoles(tempRolesItemsList);

    }

}
