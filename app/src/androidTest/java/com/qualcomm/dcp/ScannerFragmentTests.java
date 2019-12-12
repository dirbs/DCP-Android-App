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

import androidx.fragment.app.Fragment;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.utils.Constants;
import com.qualcomm.dcp.utils.UserSessionManager;
import com.qualcomm.dcp.verify.model.ImeiResponse;
import com.qualcomm.dcp.verify.view.HomeFragment;
import com.qualcomm.dcp.verify.view.ScannerFragment;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.qualcomm.dcp.Utils.hasTextInputLayoutErrorText;

// Unit tests for scanner fragment
@RunWith(AndroidJUnit4.class)
public class ScannerFragmentTests {

    @Rule
    public final ActivityTestRule mStartActivityTestRule
            = new ActivityTestRule<>(MainNavActivity.class,
            true, true);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {

        Constants.BASE_URL = "http://localhost:8000";
        UserSessionManager userSessionManager = new UserSessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
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

    // For showing loading
    private void displayLoading(ScannerFragment scanImeiFragment) {
        scanImeiFragment.mProgressDialog = new SweetAlertDialog(Objects.requireNonNull(scanImeiFragment.getActivity()), SweetAlertDialog.PROGRESS_TYPE);
        scanImeiFragment.mProgressDialog.getProgressHelper().setBarColor(scanImeiFragment.getResources().getColor(R.color.loading));
        scanImeiFragment.mProgressDialog.setTitleText(scanImeiFragment.getActivity().getResources().getString(R.string.checking));
        scanImeiFragment.mProgressDialog.setCancelable(false);
        scanImeiFragment.mProgressDialog.show();
    }

    // Unit test for manual input of imei in counterfeit
    @Test
    public void counterfeitImeiManualInput() {

        final String imei = "asdfgasdfgasdfg";

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withTagValue(Matchers.is("SCAN_TAB"))).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanner = (ScannerFragment) fragmentList.get(2);

        mStartActivityTestRule.getActivity().runOnUiThread(() -> scanner.handleResult(imei));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(R.id.scan_result_et)).perform(typeText(imei), closeSoftKeyboard());
        final androidx.appcompat.app.AlertDialog dialog = scanner.getScanDialog();
        if (dialog.isShowing()) {
            onView(withId(android.R.id.button1)).perform(click());
        }
    }

    // Unit test for manual input of short imei in counter fiet
    @Test
    public void shortLengthImeiManualInput() {

        final String imei = "1234567890";

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withTagValue(Matchers.is("SCAN_TAB"))).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanner = (ScannerFragment) fragmentList.get(2);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        mStartActivityTestRule.getActivity().runOnUiThread(() -> scanner.handleResult(imei));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(R.id.scan_result_et)).perform(typeText(imei), closeSoftKeyboard());
        final androidx.appcompat.app.AlertDialog dialog = scanner.getScanDialog();

        if (dialog.isShowing()) {
            onView(withText(R.string.ok)).perform(click());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        }
    }

    // Unit test for manual input of invalid characters imei in counter fiet
    @Test
    public void invalidCharsImeiManualInput() {

        final String imei = "asdfgasdfgasdfg";

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withTagValue(Matchers.is("SCAN_TAB"))).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanner = (ScannerFragment) fragmentList.get(2);

        mStartActivityTestRule.getActivity().runOnUiThread(() -> scanner.handleResult(imei));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(R.id.scan_result_et)).perform(typeText(imei), closeSoftKeyboard());
        final androidx.appcompat.app.AlertDialog dialog = scanner.getScanDialog();
        if (dialog.isShowing()) {
            onView(withId(android.R.id.button1)).perform(click());
        }
    }


    // Unit test for manual input of valid imei in counter fiet
    @Test
    public void validImeiManualInput() {

        final String imei = "353166060063981";

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withTagValue(Matchers.is("SCAN_TAB"))).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanner = (ScannerFragment) fragmentList.get(2);

        mStartActivityTestRule.getActivity().runOnUiThread(() -> scanner.handleResult(imei));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(R.id.scan_result_et)).perform(typeText(imei), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.scan_result_til)).check(matches(hasTextInputLayoutErrorText(null)));
    }

    // Unit test for success imei response for counter fiet
    @Test
    @UiThreadTest
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
                "        \"operatingSystem\": [\"Android\", \"iOS\"],\n" +
                "        \"radioInterface\": [\"rdi1\", \"rdi2\"],\n" +
                "        \"lpwan\": \"NA\",\n" +
                "        \"deviceCertifybody\": [\"body1\", \"body2\"],\n" +
                "        \"manufacturer\": \"NA\",\n" +
                "        \"tacApprovedDate\": \"NA\",\n" +
                "        \"gsmaApprovedTac\": \"No\"\n" +
                "    }\n" +
                "}";

        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(counterfeitImeiResponse, ImeiResponse.class);

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> scanImeiFragment1 = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanImeiFragment = (ScannerFragment) scanImeiFragment1.get(2);
        displayLoading(scanImeiFragment);
        scanImeiFragment.displaySuccess(imeiResponse);
    }

    // Unit test for valid imei response
    @Test
    @UiThreadTest
    public void validImeiResponse() {

        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(validImeiResponse, ImeiResponse.class);

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> scanImeiFragment1 = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanImeiFragment = (ScannerFragment) scanImeiFragment1.get(2);

        displayLoading(scanImeiFragment);
        scanImeiFragment.displaySuccess(imeiResponse);

    }

    // Unit test imeiCallOk
    @Test
    public void testImeiCallOk() {

        String imei = "123456789012345";

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withTagValue(Matchers.is("SCAN_TAB"))).perform(click());

        MainNavActivity mainNavActivity = (MainNavActivity) mStartActivityTestRule.getActivity();
        HomeFragment homeFragment = mainNavActivity.homeFragment;
        List<Fragment> fragmentList = homeFragment.getChildFragmentManager().getFragments();
        ScannerFragment scanner = (ScannerFragment) fragmentList.get(2);

        mStartActivityTestRule.getActivity().runOnUiThread(() -> scanner.handleResult(imei));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(R.id.scan_result_et)).perform(typeText(imei), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getDispatcherOk());
        final androidx.appcompat.app.AlertDialog dialog = scanner.getScanDialog();
        if (dialog.isShowing()) {
            onView(withId(android.R.id.button1)).perform(click());
        }
    }

    // Mock server dispatcher for callOk
    private Dispatcher getDispatcherOk() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/lookup/AndroidApp/scan")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(validImeiResponse);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }
}