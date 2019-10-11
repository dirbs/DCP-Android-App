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
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

// Unit test for history fragment
@RunWith(AndroidJUnit4.class)
public class HistoryNetworkTests {
    @Rule
    public ActivityTestRule<MainNavActivity> historyActivityTestRule =
            new ActivityTestRule<>(MainNavActivity.class, true, true);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    private final String historySearchResponse = "{\n" +
            "    \"activity\": {\n" +
            "        \"current_page\": 1,\n" +
            "        \"data\": [],\n" +
            "        \"first_page_url\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/search_users_activity?page=1\",\n" +
            "        \"from\": null,\n" +
            "        \"last_page\": 1,\n" +
            "        \"last_page_url\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/search_users_activity?page=1\",\n" +
            "        \"next_page_url\": null,\n" +
            "        \"path\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/search_users_activity\",\n" +
            "        \"per_page\": 10,\n" +
            "        \"prev_page_url\": null,\n" +
            "        \"to\": null,\n" +
            "        \"total\": 0\n" +
            "    }\n" +
            "}";

    private final String historyResponseStr = "{\n" +
            "    \"activity\": {\n" +
            "        \"current_page\": 1,\n" +
            "        \"data\": [\n" +
            "            {\n" +
            "                \"id\": 60,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"3122131233221313                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": null,\n" +
            "                \"user_id\": 1,\n" +
            "                \"user_name\": \"dcp super\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-17 09:21:38\",\n" +
            "                \"updated_at\": null,\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 59,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"1234567789056345                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": null,\n" +
            "                \"user_id\": 1,\n" +
            "                \"user_name\": \"dcp super\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-17 07:45:06\",\n" +
            "                \"updated_at\": null,\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 58,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"1233232132132312                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": \"No\",\n" +
            "                \"user_id\": 1,\n" +
            "                \"user_name\": \"dcp super\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-17 07:15:19\",\n" +
            "                \"updated_at\": \"2019-01-17 07:15:23\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 57,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"123456789012345                                                                                                                                                                                                                                                \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": \"No\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:45:43\",\n" +
            "                \"updated_at\": \"2019-01-14 12:03:34\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 56,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"1234567891012345                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": null,\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:22:49\",\n" +
            "                \"updated_at\": null,\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 55,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"123456789012345                                                                                                                                                                                                                                                \",\n" +
            "                \"result\": \"Invalid\",\n" +
            "                \"results_matched\": \"No\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:17:10\",\n" +
            "                \"updated_at\": \"2019-01-14 12:03:34\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 54,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"8667780200000010                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Found\",\n" +
            "                \"results_matched\": \"Yes\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:17:03\",\n" +
            "                \"updated_at\": \"2019-01-14 11:17:07\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 53,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"8667780200000010                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Found\",\n" +
            "                \"results_matched\": \"Yes\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:12:48\",\n" +
            "                \"updated_at\": \"2019-01-14 11:17:07\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 52,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"8667780200000010                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Found\",\n" +
            "                \"results_matched\": \"Yes\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 11:00:59\",\n" +
            "                \"updated_at\": \"2019-01-14 11:17:07\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 51,\n" +
            "                \"user_device\": \"web\",\n" +
            "                \"checking_method\": \"manual\",\n" +
            "                \"imei_number\": \"8667780200000011                                                                                                                                                                                                                                               \",\n" +
            "                \"result\": \"Found\",\n" +
            "                \"results_matched\": \"Yes\",\n" +
            "                \"user_id\": 2,\n" +
            "                \"user_name\": \"dcp\",\n" +
            "                \"visitor_ip\": \"115.186.146.252\",\n" +
            "                \"created_at\": \"2019-01-14 10:53:04\",\n" +
            "                \"updated_at\": \"2019-01-14 11:14:17\",\n" +
            "                \"latitude\": \"33.710000\",\n" +
            "                \"longitude\": \"73.058300\",\n" +
            "                \"city\": \"Islamabad\",\n" +
            "                \"country\": \"Pakistan\",\n" +
            "                \"state\": \"IS\",\n" +
            "                \"state_name\": \"Islāmābād\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"first_page_url\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/datatable/users-activity?page=1\",\n" +
            "        \"from\": 1,\n" +
            "        \"last_page\": 6,\n" +
            "        \"last_page_url\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/datatable/users-activity?page=6\",\n" +
            "        \"next_page_url\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/datatable/users-activity?page=2\",\n" +
            "        \"path\": \"http://api.vietnam.dcp.smartforum.org/v2/public/api/datatable/users-activity\",\n" +
            "        \"per_page\": 10,\n" +
            "        \"prev_page_url\": null,\n" +
            "        \"to\": 10,\n" +
            "        \"total\": 59\n" +
            "    }\n" +
            "}";

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        Constants.BASE_URL = "http://localhost:8000";
        UserSessionManager userSessionManager = new UserSessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        userSessionManager.loginStore(true, "email@test.com", "false", "loggedIn", "Test", "testToken");
        Intents.init();
    }

    @After
    public void restLoginUrl() {
        Constants.BASE_URL = BuildConfig.BASE_URL;
        Intents.release();
        mMockServerRule.stopServer();
    }

    // For opening feedback fragment
    private void openActivity() {

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(com.qualcomm.dcp.R.id.history));

    }

    // Unit test for success response for history
    @Test
    public void testHistoryOkResponse() {
        mMockServerRule.server().setDispatcher(getDispatcherStart());
        openActivity();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.history_recycler_view)).check(matches(isDisplayed()));


    }

    // Mock server dispatcher for success history response
    private Dispatcher getDispatcherStart() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/datatable/my-activity?page=1")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(historyResponseStr);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized response for history
    @Test
    public void testHistoryUnauthorizedResponse() {

        mMockServerRule.server().setDispatcher(getDispatcherUnauthorized());
        openActivity();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized history response
    private Dispatcher getDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/datatable/my-activity?page=1")) {
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

    // Unit test for success response for search history
    @Test
    public void testHistorySearchOkResponse() {

        mMockServerRule.server().setDispatcher(getDispatcherStart());
        openActivity();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.history_recycler_view)).check(matches(isDisplayed()));
        mMockServerRule.server().setDispatcher(getSearchDispatcherStart());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.action_search)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("123"), pressKey(KeyEvent.KEYCODE_ENTER));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.history_recycler_view)).check(matches(isDisplayed()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

    }

    // Mock server dispatcher for success search history response
    private Dispatcher getSearchDispatcherStart() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/search_users_activity?page=1")) {
                    return new MockResponse().setResponseCode(200)
                            .setBody(historySearchResponse);
                }
                throw new IllegalStateException("no mock set up for " + request.getPath());
            }
        };
    }

    // Unit test for unauthorized response for search history
    @Test
    public void testHistorySearchUnauthorizedResponse() {

        mMockServerRule.server().setDispatcher(getDispatcherStart());
        openActivity();
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.history_recycler_view)).check(matches(isDisplayed()));
        mMockServerRule.server().setDispatcher(getSearchDispatcherUnauthorized());
        onView(ViewMatchers.withId(com.qualcomm.dcp.R.id.action_search)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("123"), pressKey(KeyEvent.KEYCODE_ENTER));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(ViewMatchers.withText(com.qualcomm.dcp.R.string.session_expired_detail)).check(matches(isDisplayed()));
    }

    // Mock server dispatcher for unauthorized search history response
    private Dispatcher getSearchDispatcherUnauthorized() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/api/search_users_activity?page=1")) {
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
