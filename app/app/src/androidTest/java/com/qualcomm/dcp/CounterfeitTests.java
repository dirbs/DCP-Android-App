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
import android.app.Activity;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.qualcomm.dcp.counterfeit.model.CounterfeitResponse;
import com.qualcomm.dcp.counterfeit.view.CounterfeitActivity;
import com.qualcomm.dcp.utils.Constants;
import com.qualcomm.dcp.utils.UserSessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// CounterfeitActivity unit tests
@RunWith(AndroidJUnit4.class)
public class CounterfeitTests {
    @Rule
    public final ActivityTestRule<CounterfeitActivity> counterfeitActivityTestRule =
            new ActivityTestRule<>(CounterfeitActivity.class, true, true);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

    // Unit tests empty fields in counterfeit fragment
    @Test
    public void blankFieldsTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.submit)).perform(click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(counterfeitActivityTestRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.can_not_empty))));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.model_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(counterfeitActivityTestRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.can_not_empty))));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.store_name_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(counterfeitActivityTestRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.can_not_empty))));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.description_til)).check
                (matches(Utils.hasTextInputLayoutErrorText(counterfeitActivityTestRule.getActivity().getResources().getString(com.qualcomm.dcp.R.string.can_not_empty))));
    }

    // Unit tests for non empty fields in counterfeit fragment
    @Test
    public void okFieldsTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand)).perform(typeText("Test Brand"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.model)).perform(typeText("Test Model"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.store_name)).perform(typeText("Test Store"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.submit)).perform(click());
    }

    // Unit test fot image picker
    @Test
    public void pickImageTest() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.img_text)).perform(click());
        counterfeitActivityTestRule.getActivity();
        counterfeitActivityTestRule.getActivity().onActivityResult(CounterfeitActivity.REQUEST_CODE_PICKER, Activity.RESULT_OK, null);
    }

    // Unit test for setting imaged in view
    @Test
    public void setImageTest() {

        String imageUri = "drawable://" + com.qualcomm.dcp.R.drawable.ic_device;
        final CounterfeitActivity activity = counterfeitActivityTestRule.getActivity();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand)).perform(typeText("Test Brand"), closeSoftKeyboard());
        activity.images = new ArrayList<>();
        activity.images.add(new Image(1, "image1", imageUri));
        activity.images.add(new Image(2, "image2", imageUri));
        activity.images.add(new Image(3, "image3", imageUri));
        activity.images.add(new Image(4, "image4", imageUri));
        activity.images.add(new Image(5, "image5", imageUri));

        activity.runOnUiThread(activity::setSelectedImages);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img1)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img2)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img3)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img4)).perform(scrollTo(), click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img3)).perform(scrollTo(), click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img2)).perform(scrollTo(), click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img1)).perform(scrollTo(), click());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.device_img)).perform(scrollTo(), click());
    }

    // Unit test for success response of counterfeit
    @Test
    public void testCounterfeitOkResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherStart());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand)).perform(typeText("Test Brand"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.model)).perform(typeText("Test Model"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.store_name)).perform(typeText("Test Store"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.submit)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.reported)).check(matches(isDisplayed()));

    }

    // Unit test for unsuccessful response for counterfeit
    @Test
    public void testCounterfeitUnsuccessfulResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherUnsuccessful());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand)).perform(typeText("Test Brand"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.model)).perform(typeText("Test Model"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.store_name)).perform(typeText("Test Store"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.submit)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withText("error")).check(matches(isDisplayed()));
    }

    // Mock server dispatcher to get success response for counterfeit
    private Dispatcher getDispatcherStart() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/counterfiet")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody("{\n" +
                                    "    \"success\": true,\n" +
                                    "    \"message\": \"This Device has been marked as counterfeit.\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher to get unsuccessful response for counterfeit
    private Dispatcher getDispatcherUnsuccessful() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/counterfiet")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody("{\n" +
                                    "    \"success\": false,\n" +
                                    "    \"message\": \"error\"\n" +
                                    "}");
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Mock server dispatcher to get unauthorized history response for counterfeit
    @Test
    public void testHistoryUnauthorizedResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherUnauthorized());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.brand)).perform(typeText("Test Brand"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.model)).perform(typeText("Test Model"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.store_name)).perform(typeText("Test Store"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.submit)).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));


    }

    // Mock server dispatcher to get unauthorized response for counterfeit
    private Dispatcher getDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/counterfiet")) {
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

    // Unit test for getting image uri
    @Test
    public void getImageUri() {
        com.qualcomm.dcp.utils.Utils.getImageContentUri(counterfeitActivityTestRule.getActivity(), new File("drawable://" + com.qualcomm.dcp.R.drawable.ic_device));
    }

    // Unit test for counterfeit model class
    @Test
    public void counterfeitModelTest(){

        String response = "{\n" +
                "    \"success\": true,\n" +
                "    \"message\": \"Feedback submitted successfully.\"\n" +
                "}";

        Gson gson = new Gson();
        final CounterfeitResponse counterfeitResponse = gson.fromJson(response, CounterfeitResponse.class);

        assertEquals(counterfeitResponse.toString(), "CounterfeitResponse{" +
                "success = '" + counterfeitResponse.isSuccess() + '\'' +
                ",message = '" + counterfeitResponse.getMessage() + '\'' +
                "}");

        CounterfeitResponse counterfeitResp = new CounterfeitResponse();
        counterfeitResp.setMessage("A");
        counterfeitResp.setSuccess(true);

        assertEquals("A", counterfeitResp.getMessage());
        assertTrue(counterfeitResp.isSuccess());
    }
}
