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
import android.content.Context;
import android.content.Intent;

import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.counterfeit.view.CounterfeitActivity;
import com.qualcomm.dcp.result.model.ResultResponse;
import com.qualcomm.dcp.result.view.ResultDisplayActivity;
import com.qualcomm.dcp.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.qualcomm.dcp.Utils.setTextInTextView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Unit tests for reault activity
@RunWith(AndroidJUnit4.class)
public class ResultTests {

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Rule
    public final ActivityTestRule<ResultDisplayActivity> mActivityRule =
            new ActivityTestRule<>(ResultDisplayActivity.class, true, true);
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

    private final String resultMatchedResponse = "{\"success\":true,\"status\":\"results_matched\"}";
    private final String resultMisMatchedResponse = "{\n" +
            "    \"success\": false,\n" +
            "    \"status\": \"results_not_matched\"\n" +
            "}";

    // Unit test for populating data into views
    @Test
    public void resultDataPopulateTest() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, ResultDisplayActivity.class);

        intent.putExtra("imei", "123456789012345");
        intent.putExtra("marketing_name", "Samsund S8");
        intent.putExtra("internal_model_name", "S8");
        intent.putExtra("manufacturer", "Samsung");
        intent.putExtra("allocation_date", "13/01/2017");
        intent.putExtra("radio_interface", "Yes");
        intent.putExtra("brand_name", "Samsung");
        intent.putExtra("model_name", "S8");
        intent.putExtra("operating_system", "Android");
        intent.putExtra("nfc", "Yes");
        intent.putExtra("bluetooth", "Yes");
        intent.putExtra("wlan", "Yes");
        intent.putExtra("device_type", "Smartphone");
        intent.putExtra("simSupport", "Yes");
        intent.putExtra("lpwan", "Yes");
        intent.putExtra("deviceCertifybody", "GSMA");
        intent.putExtra("gsmaApprovedTac", "Yes");

        mActivityRule.launchActivity(intent);

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_tac)).check
                (matches(withText("123456789012345")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.deviceIdTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.imei)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_marketingName)).check
                (matches(withText("Samsund S8")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.marketingNameTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.marketing_name)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_internalModelName)).check
                (matches(withText("S8")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.internalModelNameTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.internal_model_name)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_manufacturer)).check
                (matches(withText("Samsung")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.manufacturerTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.manufacturer)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_allocationDate)).check
                (matches(withText("13/01/2017")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.tacApprovedDateTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.allocation_date)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_radioInterface)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.radioInterfaceTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.radio_interface)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_brandName)).check
                (matches(withText("Samsung")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brandNameTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.brand_name)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_modelName)).check
                (matches(withText("S8")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.modelNameTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.model_name)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_operatingSystem)).check
                (matches(withText("Android")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.operatingSystemTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.operating_system)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_nfc)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.nfcTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.nfc)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_bluetooth)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.bluetoothTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.bluetooth)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_wlan)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.wlanTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.wlan)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_deviceType)).check
                (matches(withText("Smartphone")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.equipmentTypeTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.device_type)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_sim_support)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.simSupportTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.sim_support)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_lpwan)).check
                (matches(withText("Yes")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.lpwanTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.lpwan)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_deviceCertificationBody)).check
                (matches(withText("GSMA")));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.deviceCertifyBodyTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.device_certification_body)));
    }

    // Unit test for mismatched imei reporting dialog
    @Test
    public void reportMismatchDialogUI() {
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.reportDialogTitle)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.report_mismatch_description)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.cancel)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.yes)));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.report)).check
                (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.no_report)));
    }

    // Unit test for mismatched imei reporting dialog yes button
    @VisibleForTesting
    public void reportMismatchDialogYes() {
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.cancel))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed()));
    }

    // Unit test for mismatched imei reporting dialog no button
    @VisibleForTesting
    public void reportMismatchDialogNo() {
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.report))            // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed()));
    }

    // Unit test for back button press in reporting activity
    @Test
    public void reportBackTest() {
        if (!mActivityRule.getActivity().isDeviceMatchReported) {
            Espresso.pressBack();
            assertTrue(mActivityRule.getActivity().isDialogShowing);
            assertTrue(mActivityRule.getActivity().dialog.isShowing());
            onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.reportDialogTitle)).check
                    (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.report_mismatch_description)));

            onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.cancel)).check
                    (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.yes)));

            onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.report)).check
                    (matches(ViewMatchers.withText(com.qualcomm.dcp.R.string.no_report)));
        }
    }

    // For displaying loading
    private void displayLoading() {
        mActivityRule.getActivity().progressDialog = new SweetAlertDialog(mActivityRule.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mActivityRule.getActivity().progressDialog.getProgressHelper().setBarColor(mActivityRule.getActivity().getResources().getColor(com.qualcomm.dcp.R.color.loading));
        mActivityRule.getActivity().progressDialog.setTitleText(mActivityRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.checking));
        mActivityRule.getActivity().progressDialog.setCancelable(false);
        mActivityRule.getActivity().progressDialog.show();
    }

    // Unit test mismatched ime
    @Test
    public void mismatchTest() {
        Gson gson = new Gson();
        final ResultResponse resultResponse = gson.fromJson(resultMisMatchedResponse, ResultResponse.class);

        try {
            mActivityRule.runOnUiThread(() -> {
                Intents.init();
                displayLoading();
                mActivityRule.getActivity().displaySuccess(resultResponse);
                intended(hasComponent(CounterfeitActivity.class.getName()));
                Intents.release();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for matched imei
    @Test
    public void matchTest() {
        Gson gson = new Gson();
        final ResultResponse resultResponse = gson.fromJson(resultMatchedResponse, ResultResponse.class);
        try {
            mActivityRule.runOnUiThread(() -> {
                displayLoading();
                mActivityRule.getActivity().displaySuccess(resultResponse);
                assertTrue(mActivityRule.getActivity().progressDialog.isShowing());
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    // Unit test for result model class
    @Test
    public void resultModelTest() {
        Gson gson = new Gson();
        final ResultResponse resultResponse = gson.fromJson(resultMatchedResponse, ResultResponse.class);
        assertEquals(resultResponse.toString(), "ResultResponse{" +
                "success = '" + resultResponse.isSuccess() + '\'' +
                ",message = '" + resultResponse.getMessage() + '\'' +
                ",status = '" + resultResponse.getStatus() + '\'' +
                "}");

        ResultResponse resultResp = new ResultResponse();
        resultResp.setMessage("A");
        resultResp.setStatus("B");
        resultResp.setSuccess(true);

        assertEquals("A", resultResp.getMessage());
        assertEquals("B", resultResp.getStatus());
        assertTrue(resultResp.isSuccess());
    }

    // Unit test for success response for matched ok result
    @Test
    public void testMatchedOkResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherMatched());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_tac))
                .perform(setTextInTextView());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.cancel)).perform(click());


    }

    // Mock server dispatcher for success response for matched ok result
    private Dispatcher getDispatcherMatched() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/results-matched/123456789012345")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(resultMatchedResponse);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized response for matched ok result
    @Test
    public void testMatchedUnauthorizedResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherUnauthorizedMatched());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_tac))
                .perform(setTextInTextView());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.cancel)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized response for matched ok result
    private Dispatcher getDispatcherUnauthorizedMatched() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/results-matched/123456789012345")) {
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

    // Unit test for OK response for matched ok result
    @Test
    public void testMisMatchedOkResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherMisMatched());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_tac))
                .perform(setTextInTextView());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.report)).perform(click());
    }

    // Mock server dispatcher for success response for mismatched result
    private Dispatcher getDispatcherMisMatched() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/results-not-matched/123456789012345")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(resultMisMatchedResponse);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized response for mismatched result
    @Test
    public void testMisMatchedUnauthorizedResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherUnauthorizedMisMatched());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.txt_tac))
                .perform(setTextInTextView());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.report)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized response for mismatched result
    private Dispatcher getDispatcherUnauthorizedMisMatched() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/results-not-matched/123456789012345")) {
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

}
