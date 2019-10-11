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

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.qualcomm.dcp.feedback.model.FeedbackResponse;
import com.qualcomm.dcp.utils.Constants;
import com.qualcomm.dcp.utils.UserSessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Unit tests for feedback activity
@RunWith(AndroidJUnit4.class)
public class FeedbackTests {

    @Rule
    public final ActivityTestRule mMainActivityTestRule = new ActivityTestRule<>(MainNavActivity.class,
            true, true);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        Constants.BASE_URL = "http://localhost:8000";
        UserSessionManager userSessionManager = new UserSessionManager(getInstrumentation().getTargetContext());
        userSessionManager.loginStore(true, "email@test.com", "staff", "loggedIn", "Test", "testToken");
    }

    @After
    public void restLoginUrl() {
        Constants.BASE_URL = BuildConfig.BASE_URL;
        mMockServerRule.stopServer();
    }

    // Also checks the empty feedback error text
    @Test
    public void successFeedback(){

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.feedback));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.feedback_text)).perform(typeText("hey"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getSuccessFeedbackDispatcher());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.buttonSubmit)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.feedback_sent)).check(matches(isDisplayed()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.ok)).perform(click());

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.feedback_text)).perform(clearText(), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.buttonSubmit)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.feedback_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(com.qualcomm.dcp.R.string.can_not_empty))));
    }

    // Mock server dispatcher to get success response for feedback
    private Dispatcher getSuccessFeedbackDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/feedback")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody("{\n" +
                                    "    \"success\": true,\n" +
                                    "    \"message\": \"Feedback submitted successfully.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for failed response from server
    @Test
    public void failureFeedback(){

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.feedback));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.feedback_text)).perform(typeText("hey"), closeSoftKeyboard());
        mMockServerRule.server().setDispatcher(getFailedFeedbackDispatcher());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.buttonSubmit)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.oops)).check(matches(isDisplayed()));

    }

    // Mock server dispatcher to get failed response feedback
    private Dispatcher getFailedFeedbackDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/feedback")) {
                    return new MockResponse().setResponseCode(403)
                            .setBody("{\n" +
                                    "    \"error\": true,\n" +
                                    "    \"message\": \"You do not have enough permissions for this request, please contact system administrator for more details\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for feedback model class
    @Test
    public void feedbackModelTest(){

        String response = "{\n" +
                "    \"success\": true,\n" +
                "    \"message\": \"Feedback submitted successfully.\"\n" +
                "}";

        Gson gson = new Gson();
        final FeedbackResponse feedbackResponse = gson.fromJson(response, FeedbackResponse.class);
        assertEquals(feedbackResponse.toString(), "FeedbackResponse{" +
                "success = '" + feedbackResponse.isSuccess() + '\'' +
                ",message = '" + feedbackResponse.getMessage() + '\'' +
                "}");

        FeedbackResponse feedbackResp = new FeedbackResponse();
        feedbackResp.setMessage("A");
        feedbackResp.setSuccess(true);

        assertEquals("A", feedbackResp.getMessage());
        assertTrue(feedbackResp.isSuccess());
    }
}
