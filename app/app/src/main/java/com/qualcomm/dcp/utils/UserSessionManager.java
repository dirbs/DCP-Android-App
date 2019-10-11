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

import android.content.Context;

/*
 * This class maintains user login session status.
 **/
public class UserSessionManager {

    private final MyPreferences myPreferences;
    private final String LOGGED_IN = "LoggedIn";
    private final String USER_ID = "UserId";
    private final String IS_ADMIN = "IsAdmin";
    private final String STATUS = "Status";

    public String getUSERNAME() {
        return myPreferences.getString(USERNAME, "");
    }

    private final String USERNAME = "Username";
    private final String LOGIN_TOKEN = "LoginToken";

    public UserSessionManager(Context context) {
        myPreferences = new MyPreferences(context);

    }

    //    Save login status.
    public void loginStore(boolean loggedIn, String userid, String isadmin, String status, String username, String token) {
        myPreferences.setBoolean(LOGGED_IN, loggedIn);
        myPreferences.setString(USER_ID, userid);
        myPreferences.setString(IS_ADMIN, isadmin);
        myPreferences.setString(STATUS, status);
        myPreferences.setString(USERNAME, username);
        myPreferences.setString(LOGIN_TOKEN, token);
    }

    //    Save login status.
    public void logout() {
        myPreferences.setBoolean(LOGGED_IN, false);
        myPreferences.setString(USER_ID, "");
        myPreferences.setString(IS_ADMIN, "");
        myPreferences.setString(STATUS, "");
        myPreferences.setString(USERNAME, "");
        myPreferences.setString(LOGIN_TOKEN, "");
    }

    //    Get login status.
    public boolean loginCheck() {
        boolean loginStatus;
        loginStatus = myPreferences.getBoolean(LOGGED_IN, false);
        return loginStatus;
    }

    //Get Login token
    public String getLoginToken() {
        return myPreferences.getString(LOGIN_TOKEN, "");
    }

    public String getUserId() {
        return myPreferences.getString(USER_ID, null);
    }

    public String getIsAdmin() {
        return myPreferences.getString(IS_ADMIN, null);
    }

}
