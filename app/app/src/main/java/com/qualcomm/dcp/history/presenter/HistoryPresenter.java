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

package com.qualcomm.dcp.history.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.qualcomm.dcp.history.network.HistoryNetworkInterface;
import com.qualcomm.dcp.networks.NetworkClient;
import com.qualcomm.dcp.history.model.HistoryResponse;
import com.qualcomm.dcp.history.network.SearchHistoryNetworkInterface;
import com.qualcomm.dcp.history.view.HistoryViewInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

// Presenter class for fetching history data
public class HistoryPresenter implements HistoryPresenterInterface {

    private HistoryViewInterface mHistoryViewInterface;
    private final String TAG = "HistoryPresenter";
    private Activity mActivity;

    public HistoryPresenter(HistoryViewInterface historyViewInterface, Activity activity) {
        this.mHistoryViewInterface = historyViewInterface;
        this.mActivity = activity;
    }

    // For fetching history data
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public void getHistory(int pageNumber) {
        getObservable(pageNumber).subscribeWith(getObserver());
    }

    private Observable<HistoryResponse> getObservable(int pageNumber) {
        Log.e("pageNum", "page number" + pageNumber);
        return NetworkClient.getRetrofit(mActivity).create(HistoryNetworkInterface.class)
                .getHistory(pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // For fetching history data for searched item
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public void searchHistory(String query, int page) {
        getObservable(query, page).subscribeWith(getObserver());
    }

    private Observable<HistoryResponse> getObservable(String query, int page) {
        return NetworkClient.getRetrofit(mActivity).create(SearchHistoryNetworkInterface.class)
                .searchHistory(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<HistoryResponse> getObserver() {
        return new DisposableObserver<HistoryResponse>() {

            @Override
            public void onNext(@NonNull HistoryResponse historyResponse) {
                Log.e("res", "history response = " + historyResponse.toString());
                if (mHistoryViewInterface != null)
                    mHistoryViewInterface.displayHistory(historyResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "Error" + e);
                e.printStackTrace();
                if (mHistoryViewInterface != null)
                    mHistoryViewInterface.displayError(e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Completed");
            }
        };
    }

    // For unbinding this presenter from its view
    public void unbind() {
        if (mHistoryViewInterface != null)
            mHistoryViewInterface = null;
        if (mActivity != null)
            mActivity = null;
    }
}
