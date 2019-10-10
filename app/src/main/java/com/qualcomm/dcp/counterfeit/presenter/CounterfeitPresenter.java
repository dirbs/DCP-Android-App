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

package com.qualcomm.dcp.counterfeit.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.qualcomm.dcp.counterfeit.model.CounterfeitResponse;
import com.qualcomm.dcp.utils.Utils;
import com.qualcomm.dcp.counterfeit.network.CounterfeitNetworkInterface;
import com.qualcomm.dcp.counterfeit.view.CounterfeitViewInterface;
import com.qualcomm.dcp.networks.NetworkClient;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

// Counterfeit presenter class for sending request for counterfeit and handling response from it
// and notifying view about response.
public class CounterfeitPresenter implements CounterfeitPresenterInterface {

    private CounterfeitViewInterface mCounterfeitViewInterface;
    private Activity mActivity;
    private final String TAG = "HistoryPresenter";

    public CounterfeitPresenter(CounterfeitViewInterface counterfeitViewInterface, Activity activity) {
        this.mCounterfeitViewInterface = counterfeitViewInterface;
        this.mActivity = activity;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public void addCounterfeitDevice(Map<String, RequestBody> files, String brand, String imei, String description, String model, String store, String address) {
        getObservable(files, brand, imei, description, model, store, address).subscribeWith(getObserver());
    }

    private Observable<CounterfeitResponse> getObservable(Map<String, RequestBody> files, String brand, String imei, String description, String model, String store, String address) {
        return NetworkClient.getRetrofit(mActivity).create(CounterfeitNetworkInterface.class)
                .addCounterfeitDevice(files,
                        Utils.createPartFromString(brand),
                        Utils.createPartFromString(imei),
                        Utils.createPartFromString(description),
                        Utils.createPartFromString(model),
                        Utils.createPartFromString(store),
                        Utils.createPartFromString(address))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<CounterfeitResponse> getObserver() {
        return new DisposableObserver<CounterfeitResponse>() {
            @Override
            public void onNext(@NonNull CounterfeitResponse counterfeitResponse) {
                Log.e("res", "history response = " + counterfeitResponse.toString());
                if (mCounterfeitViewInterface != null)
                    mCounterfeitViewInterface.displaySuccess(counterfeitResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "Error" + e);
                e.printStackTrace();
                if (mCounterfeitViewInterface != null)
                    mCounterfeitViewInterface.displayError(e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Completed");
            }
        };
    }

    // For unbinding this presenter from its view
    public void unbind() {
        if (mCounterfeitViewInterface != null)
            mCounterfeitViewInterface = null;
        if (mActivity != null)
            mActivity = null;
    }
}