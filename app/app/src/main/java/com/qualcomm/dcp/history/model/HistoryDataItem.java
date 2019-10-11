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

package com.qualcomm.dcp.history.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

// Model class for history data item
public class HistoryDataItem {

	@SerializedName("country")
	private String country;

	@SerializedName("checking_method")
	private String checkingMethod;

	@SerializedName("city")
	private String city;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("results_matched")
	private Object resultsMatched;

	@SerializedName("user_device")
	private String userDevice;

	@SerializedName("result")
	private String result;

	@SerializedName("updated_at")
	private Object updatedAt;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("state_name")
	private String stateName;

	@SerializedName("imei_number")
	private String imeiNumber;

	@SerializedName("id")
	private int id;

	@SerializedName("state")
	private String state;

	@SerializedName("visitor_ip")
	private String visitorIp;

	@SerializedName("longitude")
	private String longitude;

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setCheckingMethod(String checkingMethod){
		this.checkingMethod = checkingMethod;
	}

	public String getCheckingMethod(){
		return checkingMethod;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setResultsMatched(Object resultsMatched){
		this.resultsMatched = resultsMatched;
	}

	public Object getResultsMatched(){
		return resultsMatched;
	}

	public void setUserDevice(String userDevice){
		this.userDevice = userDevice;
	}

	public String getUserDevice(){
		return userDevice;
	}

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setUpdatedAt(Object updatedAt){
		this.updatedAt = updatedAt;
	}

	public Object getUpdatedAt(){
		return updatedAt;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setStateName(String stateName){
		this.stateName = stateName;
	}

	public String getStateName(){
		return stateName;
	}

	public void setImeiNumber(String imeiNumber){
		this.imeiNumber = imeiNumber;
	}

	public String getImeiNumber(){
		return imeiNumber;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setVisitorIp(String visitorIp){
		this.visitorIp = visitorIp;
	}

	public String getVisitorIp(){
		return visitorIp;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	@NonNull
	@Override
 	public String toString(){
		return 
			"HistoryDataItem{" +
			"country = '" + country + '\'' + 
			",checking_method = '" + checkingMethod + '\'' + 
			",city = '" + city + '\'' + 
			",user_name = '" + userName + '\'' + 
			",latitude = '" + latitude + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",results_matched = '" + resultsMatched + '\'' + 
			",user_device = '" + userDevice + '\'' + 
			",result = '" + result + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",user_id = '" + userId + '\'' + 
			",state_name = '" + stateName + '\'' + 
			",imei_number = '" + imeiNumber + '\'' + 
			",id = '" + id + '\'' + 
			",state = '" + state + '\'' + 
			",visitor_ip = '" + visitorIp + '\'' + 
			",longitude = '" + longitude + '\'' + 
			"}";
		}
}