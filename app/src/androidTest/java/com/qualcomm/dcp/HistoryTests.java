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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;
import com.qualcomm.dcp.history.model.HistoryResponse;
import com.qualcomm.dcp.history.view.HistoryFragment;
import com.qualcomm.dcp.utils.UserSessionManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

// History fragment unit test
@RunWith(AndroidJUnit4.class)
public class HistoryTests {

    @Rule
    public final ActivityTestRule<MainNavActivity> historyActivityTestRule =
            new ActivityTestRule<>(MainNavActivity.class, true, true);

    @Before
    public void setUp() {

        UserSessionManager userSessionManager = new UserSessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        userSessionManager.loginStore(true, "email@test.com", "false", "loggedIn", "Test", "testToken");
        try {
            historyActivityTestRule.runOnUiThread(() -> historyActivityTestRule.getActivity().navController.navigate(com.qualcomm.dcp.R.id.historyFragment));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Test
    public void historyResultTest() {
        String historyResponseStr = "{\n" +
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
        Gson gson = new Gson();
        final HistoryResponse historyResponse = gson.fromJson(historyResponseStr, HistoryResponse.class);
        try {
            historyActivityTestRule.runOnUiThread(() -> ((HistoryFragment) Objects.requireNonNull(historyActivityTestRule.getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.history))).displayHistory(historyResponse));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
