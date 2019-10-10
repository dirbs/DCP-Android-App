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
import android.view.Gravity;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.result.view.ResultDisplayActivity;
import com.qualcomm.dcp.utils.Constants;
import com.qualcomm.dcp.utils.UserSessionManager;
import com.qualcomm.dcp.verify.model.Data;
import com.qualcomm.dcp.verify.model.ImeiResponse;
import com.qualcomm.dcp.verify.view.EnterImeiFragment;
import com.qualcomm.dcp.verify.view.HomeFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// Unit test for main nav activity
@RunWith(AndroidJUnit4.class)
public class TacFragmentTests {
    @Rule
    public final ActivityTestRule mMainActivityTestRule
            = new ActivityTestRule<>(MainNavActivity.class,
            true, true);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        Constants.BASE_URL = "http://localhost:8000";
        UserSessionManager userSessionManager = new UserSessionManager(getInstrumentation().getTargetContext());
        userSessionManager.loginStore(true, "email@test.com", "false", "loggedIn", "Test", "testToken");

    }

    @After
    public void restLoginUrl() {
        Constants.BASE_URL = BuildConfig.BASE_URL;
        mMockServerRule.stopServer();
    }




    private final String validImeiResponse = "{\n" +
            "    \"error\": false,\n" +
            "    \"success\": true,\n" +
            "    \"data\": {\n" +
            "        \"statusCode\": 200,\n" +
            "        \"statusMessage\": \"GetHandsetDetails - Success\",\n" +
            "        \"deviceId\": \"35258307\",\n" +
            "        \"brandName\": \"LG\",\n" +
            "        \"modelName\": \"LG-H815L\",\n" +
            "        \"internalModelName\": \"LG LG-H815L\",\n" +
            "        \"marketingName\": \"LG LG-H815L\",\n" +
            "        \"equipmentType\": \"Smartphone\",\n" +
            "        \"simSupport\": \"Not Known\",\n" +
            "        \"nfcSupport\": \"Yes\",\n" +
            "        \"wlanSupport\": \"Yes\",\n" +
            "        \"blueToothSupport\": \"Yes\",\n" +
            "        \"operatingSystem\": [\n" +
            "            \"Android\",\"Android\",\"Android\",\"Android\"\n" +
            "        ],\n" +
            "        \"radioInterface\": [\n" +
            "            \"Android\",\"Android\",\"Android\",\"NONE\"\n" +
            "        ],\n" +
            "        \"lpwan\": \"Not Known\",\n" +
            "        \"deviceCertifybody\": [\n" +
            "            \"Android\",\"Android\",\"Android\",\"Not Known\"\n" +
            "        ],\n" +
            "        \"manufacturer\": \"LG Electronics Inc.\",\n" +
            "        \"tacApprovedDate\": \"2015-05-01\",\n" +
            "        \"gsmaApprovedTac\": \"Yes\"\n" +
            "    }\n" +
            "}";


    // Unit test change language button in navHeader
    @Test
    public void changeLanguageTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.language)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.history)).check(matches(isDisplayed()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(isRoot()).perform(ViewActions.pressBack());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.check)).check(matches(isDisplayed()));

    }

    // Unit test logout menu in navigation
    @Test
    public void logoutTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.logout));

        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.logout)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.logout)).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.no)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.logout));

        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.yes)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.forgot_password)).check(matches(isDisplayed()));
    }

    // Unit test history menu in navigation
    @Test
    public void openHistoryTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.logout));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    // Unit test for manual input in imei field
    @Test
    public void counterfeitImeiManualInput() {

        //can be replaced with any blocked IMEI
        String imei = "123456789012345";

        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(null)));

    }

    // Unit test for manual short input in imei field
    @Test
    public void shortLengthImeiManualInput() {

        //can be replaced with any blocked IMEI
        String imei = "1234567890";

        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.fifteen_digit_imei))));
    }

    // Unit test for manual invalid characters input in imei field
    @Test
    public void invalidCharsImeiManualInput() {

        //will not be entered imei input box as the inout box only allow numeric character
        String imei = "asdfgasdf";

        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        //check if the error text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.fifteen_digit_imei))));

    }

    // Unit test for manual valid input in imei field
    @Test
    public void validImeiManualInput() {

        //can be replaced with any blocked IMEI
        String imei = "352583079012345";

        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(null)));

    }

    // Unit test UI
    @Test
    public void UiTests() {

        //check if the hint text is displayed and its ok
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.check)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.imei_til)).check
                (matches(Utils.hasTextInputLayoutHintText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.enter_digit_imei))));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed()));

    }

    // Unit test for short imei in dialog
    @Test
    public void dialogShortImeiTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText("123456789012345"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.verify_imei_text)).perform(clearText(), typeText("1234567890"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.verify_imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.fifteen_digit_imei))));

    }

    // Unit test for invalid imei in dialog
    @Test
    public void dialogInvalidCharImeiTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText("123456789012345"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.verify_imei_text)).perform(clearText(), typeText("asdfghjkl"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.verify_imei_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.fifteen_digit_imei))));

    }

    // Unit test for valid imei in dialog
    @Test
    public void dialogValidImeiTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText("123456789012345"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mMainActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        EnterImeiFragment enterImeiFragment = (EnterImeiFragment) fragmentList.get(0);

        assertFalse(enterImeiFragment.getVerifyImeiDialog().isShowing());

    }

    // For showing loading
    private void displayLoading(EnterImeiFragment enterImeiFragment) {

        enterImeiFragment.mProgressDialog = new SweetAlertDialog(Objects.requireNonNull(enterImeiFragment.getActivity()), SweetAlertDialog.PROGRESS_TYPE);
        enterImeiFragment.mProgressDialog.getProgressHelper().setBarColor(enterImeiFragment.getResources().getColor(com.qualcomm.dcp.R.color.loading));
        enterImeiFragment.mProgressDialog.setTitleText(Objects.requireNonNull(enterImeiFragment.getActivity()).getResources().getString(com.qualcomm.dcp.R.string.checking));
        enterImeiFragment.mProgressDialog.setCancelable(false);
        enterImeiFragment.mProgressDialog.show();
    }

    // Unit test for success imei response
    @Test
    public void counterfeitImeiResponse() {

        String counterfeitImeiResponse = "{\n" +
                "    \"error\": false,\n" +
                "    \"success\": true,\n" +
                "    \"data\": {\n" +
                "        \"statusCode\": 200,\n" +
                "        \"statusMessage\": \"GetHandsetDetails - Success\",\n" +
                "        \"deviceId\": \"65213231\",\n" +
                "        \"brandName\": \"NA\",\n" +
                "        \"modelName\": \"NA\",\n" +
                "        \"internalModelName\": \"NA\",\n" +
                "        \"marketingName\": \"NA\",\n" +
                "        \"equipmentType\": \"NA\",\n" +
                "        \"simSupport\": \"NA\",\n" +
                "        \"nfcSupport\": \"NA\",\n" +
                "        \"wlanSupport\": \"NA\",\n" +
                "        \"blueToothSupport\": \"NA\",\n" +
                "        \"operatingSystem\": [\"Android\",\"Android\", \"iOS\"],\n" +
                "        \"radioInterface\": [\"Android\",\"rdi1\", \"rdi2\"],\n" +
                "        \"lpwan\": \"NA\",\n" +
                "        \"deviceCertifybody\": [\"Android\",\"body1\", \"body2\"],\n" +
                "        \"manufacturer\": \"NA\",\n" +
                "        \"tacApprovedDate\": \"NA\",\n" +
                "        \"gsmaApprovedTac\": \"No\"\n" +
                "    }\n" +
                "}";

        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(counterfeitImeiResponse, ImeiResponse.class);
        try {
            mMainActivityTestRule.runOnUiThread(() -> {
                EnterImeiFragment enterImeiFragment = ((EnterImeiFragment) ((MainNavActivity) mMainActivityTestRule.getActivity()).getSupportFragmentManager().getFragments().get(0));
                displayLoading(enterImeiFragment);
                enterImeiFragment.displaySuccess(imeiResponse);
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for valid imei response
    @Test
    public void validImeiResponse() {
        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(validImeiResponse, ImeiResponse.class);
        try {
            mMainActivityTestRule.runOnUiThread(() -> {
                Intents.init();
                EnterImeiFragment enterImeiFragment = ((EnterImeiFragment) ((MainNavActivity) mMainActivityTestRule.getActivity()).getSupportFragmentManager().getFragments().get(0));
                displayLoading(enterImeiFragment);
                enterImeiFragment.displaySuccess(imeiResponse);

                intended(hasComponent(ResultDisplayActivity.class.getName()));
                Intents.release();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for CallOk response
    @Test
    public void testImeiCallOk() {

        String imei = "123456789012345";
        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());
        mMockServerRule.server().setDispatcher(getDispatcherOk());
        onView(withId(android.R.id.button1)).perform(click());

    }

    // Mock server dispatcher for CallOk
    private Dispatcher getDispatcherOk() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/lookup/AndroidApp/manual")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(validImeiResponse);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized call
    @Test
    public void testImeiCallUnauthorized() {

        String imei = "123456789012345";
        //find enter imei edit text , populate it with IMEI and perform click on check IMEI button
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_input)).perform(typeText(imei));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.btn_check)).perform(click());
        mMockServerRule.server().setDispatcher(getDispatcherUnauthorized());
        onView(withId(android.R.id.button1)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized
    private Dispatcher getDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/lookup/AndroidApp/manual")) {
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

    // Unit test model class for imei response
    @Test
    public void imeiResponseTest() {
        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(validImeiResponse, ImeiResponse.class);
        assertEquals(imeiResponse.toString(), "ImeiResponse{" +
                "data = '" + imeiResponse.getData() + '\'' +
                ",success = '" + imeiResponse.isSuccess() + '\'' +
                ",error = '" + imeiResponse.isError() + '\'' +
                "}");
        assertEquals(imeiResponse.getData().toString(), "Data{" +
                "brandName = '" + imeiResponse.getData().getBrandName() + '\'' +
                ",simSupport = '" + imeiResponse.getData().getSimSupport() + '\'' +
                ",radioInterface = '" + imeiResponse.getData().getRadioInterface() + '\'' +
                ",lpwan = '" + imeiResponse.getData().getLpwan() + '\'' +
                ",gsmaApprovedTac = '" + imeiResponse.getData().getGsmaApprovedTac() + '\'' +
                ",wlanSupport = '" + imeiResponse.getData().getWlanSupport() + '\'' +
                ",blueToothSupport = '" + imeiResponse.getData().getBlueToothSupport() + '\'' +
                ",deviceCertifybody = '" + imeiResponse.getData().getDeviceCertifybody() + '\'' +
                ",deviceId = '" + imeiResponse.getData().getDeviceId() + '\'' +
                ",operatingSystem = '" + imeiResponse.getData().getOperatingSystem() + '\'' +
                ",statusMessage = '" + imeiResponse.getData().getStatusMessage() + '\'' +
                ",marketingName = '" + imeiResponse.getData().getMarketingName() + '\'' +
                ",equipmentType = '" + imeiResponse.getData().getEquipmentType() + '\'' +
                ",manufacturer = '" + imeiResponse.getData().getManufacturer() + '\'' +
                ",modelName = '" + imeiResponse.getData().getModelName() + '\'' +
                ",nfcSupport = '" + imeiResponse.getData().getNfcSupport() + '\'' +
                ",internalModelName = '" + imeiResponse.getData().getInternalModelName() + '\'' +
                ",tacApprovedDate = '" + imeiResponse.getData().getTacApprovedDate() + '\'' +
                ",statusCode = '" + imeiResponse.getData().getStatusCode() + '\'' +
                "}");

        ArrayList<String> tempStringArrayList = new ArrayList<>();

        Data data = new Data();
        ImeiResponse imeiResp = new ImeiResponse();

        data.setBrandName("A");
        data.setSimSupport("B");
        data.setRadioInterface(tempStringArrayList);
        data.setLpwan("C");
        data.setGsmaApprovedTac("D");
        data.setWlanSupport("E");
        data.setBlueToothSupport("F");
        data.setDeviceCertifybody(tempStringArrayList);
        data.setDeviceId("G");
        data.setOperatingSystem(tempStringArrayList);
        data.setStatusMessage("H");
        data.setMarketingName("I");
        data.setEquipmentType("J");
        data.setManufacturer("K");
        data.setModelName("L");
        data.setNfcSupport("M");
        data.setInternalModelName("N");
        data.setTacApprovedDate("O");
        data.setStatusCode(0);

        assertEquals("A", data.getBrandName());
        assertEquals("B", data.getSimSupport());
        assertEquals(tempStringArrayList, data.getRadioInterface());
        assertEquals("C", data.getLpwan());
        assertEquals("D", data.getGsmaApprovedTac());
        assertEquals("E", data.getWlanSupport());
        assertEquals("F", data.getBlueToothSupport());
        assertEquals(tempStringArrayList, data.getDeviceCertifybody());
        assertEquals("G", data.getDeviceId());
        assertEquals(tempStringArrayList, data.getOperatingSystem());
        assertEquals("H", data.getStatusMessage());
        assertEquals("I", data.getMarketingName());
        assertEquals("J", data.getEquipmentType());
        assertEquals("K", data.getManufacturer());
        assertEquals("L", data.getModelName());
        assertEquals("M", data.getNfcSupport());
        assertEquals("N", data.getInternalModelName());
        assertEquals("O", data.getTacApprovedDate());
        assertEquals(0, data.getStatusCode());

        imeiResp.setData(data);
        imeiResp.setError(true);
        imeiResp.setSuccess(true);

        assertEquals(data, imeiResp.getData());
        assertTrue(imeiResp.isError());
        assertTrue(imeiResp.isSuccess());

    }
}
