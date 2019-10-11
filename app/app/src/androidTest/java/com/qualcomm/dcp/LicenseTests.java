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
import android.content.Intent;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.qualcomm.dcp.license.view.LicenseAgreementActivity;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.CoreMatchers.not;

// Unit test for license activity
@RunWith(AndroidJUnit4.class)
public class LicenseTests {

    @Rule
    public final ActivityTestRule<LicenseAgreementActivity> licenseActivityTestRule =
            new ActivityTestRule<>(LicenseAgreementActivity.class, true, false);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        Constants.BASE_URL = "http://localhost:8000";
        Intent intent = new Intent();
        intent.putExtra("license", "Test license text");
        intent.putExtra("licenseId", 1);
        intent.putExtra("userId", 1);
        licenseActivityTestRule.launchActivity(intent);
    }

    @After
    public void restLoginUrl() {
        Constants.BASE_URL = BuildConfig.BASE_URL;
        mMockServerRule.stopServer();
    }

    // Unit test for license agreed
    @Test
    public void licenseAgreeTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.continue_btn)).check(matches(not(isEnabled())));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.license_checkbox)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.continue_btn)).check(matches(isEnabled()));
    }

    // Unit test for success response fro license agreed
    @Test
    public void testLicenseOkResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherStart());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.license_checkbox)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.continue_btn)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    // Mock server dispatcher for success response for license
    private Dispatcher getDispatcherStart() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/update-user-license/1")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody("");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized response fro license agreed
    @Test
    public void testHistoryUnauthorizedResponse() {

        mMockServerRule.server().setDispatcher(getDispatcherUnauthorized());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.license_checkbox)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.continue_btn)).perform(click());
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized response for license
    private Dispatcher getDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/update-user-license/1")) {
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
