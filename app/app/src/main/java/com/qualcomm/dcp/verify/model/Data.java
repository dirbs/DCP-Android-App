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

package com.qualcomm.dcp.verify.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Model class for data
public class Data{

	@SerializedName("brandName")
	private String brandName;

	@SerializedName("simSupport")
	private String simSupport;

	@SerializedName("radioInterface")
	private List<String> radioInterface;

	@SerializedName("lpwan")
	private String lpwan;

	@SerializedName("gsmaApprovedTac")
	private String gsmaApprovedTac;

	@SerializedName("wlanSupport")
	private String wlanSupport;

	@SerializedName("blueToothSupport")
	private String blueToothSupport;

	@SerializedName("deviceCertifybody")
	private List<String> deviceCertifybody;

	@SerializedName("deviceId")
	private String deviceId;

	@SerializedName("operatingSystem")
	private List<String> operatingSystem;

	@SerializedName("statusMessage")
	private String statusMessage;

	@SerializedName("marketingName")
	private String marketingName;

	@SerializedName("equipmentType")
	private String equipmentType;

	@SerializedName("manufacturer")
	private String manufacturer;

	@SerializedName("modelName")
	private String modelName;

	@SerializedName("nfcSupport")
	private String nfcSupport;

	@SerializedName("internalModelName")
	private String internalModelName;

	@SerializedName("tacApprovedDate")
	private String tacApprovedDate;

	@SerializedName("statusCode")
	private int statusCode;

	public void setBrandName(String brandName){
		this.brandName = brandName;
	}

	public String getBrandName(){
		return brandName;
	}

	public void setSimSupport(String simSupport){
		this.simSupport = simSupport;
	}

	public String getSimSupport(){
		return simSupport;
	}

	public void setRadioInterface(List<String> radioInterface){
		this.radioInterface = radioInterface;
	}

	public List<String> getRadioInterface(){
		return radioInterface;
	}

	public void setLpwan(String lpwan){
		this.lpwan = lpwan;
	}

	public String getLpwan(){
		return lpwan;
	}

	public void setGsmaApprovedTac(String gsmaApprovedTac){
		this.gsmaApprovedTac = gsmaApprovedTac;
	}

	public String getGsmaApprovedTac(){
		return gsmaApprovedTac;
	}

	public void setWlanSupport(String wlanSupport){
		this.wlanSupport = wlanSupport;
	}

	public String getWlanSupport(){
		return wlanSupport;
	}

	public void setBlueToothSupport(String blueToothSupport){
		this.blueToothSupport = blueToothSupport;
	}

	public String getBlueToothSupport(){
		return blueToothSupport;
	}

	public void setDeviceCertifybody(List<String> deviceCertifybody){
		this.deviceCertifybody = deviceCertifybody;
	}

	public List<String> getDeviceCertifybody(){
		return deviceCertifybody;
	}

	public void setDeviceId(String deviceId){
		this.deviceId = deviceId;
	}

	public String getDeviceId(){
		return deviceId;
	}

	public void setOperatingSystem(List<String> operatingSystem){
		this.operatingSystem = operatingSystem;
	}

	public List<String> getOperatingSystem(){
		return operatingSystem;
	}

	public void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}

	public String getStatusMessage(){
		return statusMessage;
	}

	public void setMarketingName(String marketingName){
		this.marketingName = marketingName;
	}

	public String getMarketingName(){
		return marketingName;
	}

	public void setEquipmentType(String equipmentType){
		this.equipmentType = equipmentType;
	}

	public String getEquipmentType(){
		return equipmentType;
	}

	public void setManufacturer(String manufacturer){
		this.manufacturer = manufacturer;
	}

	public String getManufacturer(){
		return manufacturer;
	}

	public void setModelName(String modelName){
		this.modelName = modelName;
	}

	public String getModelName(){
		return modelName;
	}

	public void setNfcSupport(String nfcSupport){
		this.nfcSupport = nfcSupport;
	}

	public String getNfcSupport(){
		return nfcSupport;
	}

	public void setInternalModelName(String internalModelName){
		this.internalModelName = internalModelName;
	}

	public String getInternalModelName(){
		return internalModelName;
	}

	public void setTacApprovedDate(String tacApprovedDate){
		this.tacApprovedDate = tacApprovedDate;
	}

	public String getTacApprovedDate(){
		return tacApprovedDate;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}

	@NonNull
	@Override
 	public String toString(){
		return 
			"Data{" + 
			"brandName = '" + brandName + '\'' + 
			",simSupport = '" + simSupport + '\'' + 
			",radioInterface = '" + radioInterface + '\'' + 
			",lpwan = '" + lpwan + '\'' + 
			",gsmaApprovedTac = '" + gsmaApprovedTac + '\'' + 
			",wlanSupport = '" + wlanSupport + '\'' + 
			",blueToothSupport = '" + blueToothSupport + '\'' + 
			",deviceCertifybody = '" + deviceCertifybody + '\'' + 
			",deviceId = '" + deviceId + '\'' + 
			",operatingSystem = '" + operatingSystem + '\'' + 
			",statusMessage = '" + statusMessage + '\'' + 
			",marketingName = '" + marketingName + '\'' + 
			",equipmentType = '" + equipmentType + '\'' + 
			",manufacturer = '" + manufacturer + '\'' + 
			",modelName = '" + modelName + '\'' + 
			",nfcSupport = '" + nfcSupport + '\'' + 
			",internalModelName = '" + internalModelName + '\'' + 
			",tacApprovedDate = '" + tacApprovedDate + '\'' + 
			",statusCode = '" + statusCode + '\'' + 
			"}";
		}
}