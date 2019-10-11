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

package com.qualcomm.dcp.history.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.qualcomm.dcp.R;
import com.qualcomm.dcp.adapters.LogListAdapter;
import com.qualcomm.dcp.history.model.HistoryDataItem;
import com.qualcomm.dcp.history.model.HistoryResponse;
import com.qualcomm.dcp.history.presenter.HistoryPresenter;
import com.qualcomm.dcp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// View class for handling history fragment
public class HistoryFragment extends Fragment implements HistoryViewInterface, SwipyRefreshLayout.OnRefreshListener {

    // Binding view
    @BindView(R.id.history_recycler_view)
    RecyclerView rvHistory;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_records_tv)
    TextView noRecordTv;
    @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout swipyRefreshLayout;

    private Unbinder mUnbind;

    private HistoryPresenter mMainPresenter;
    private int mCurrentPage = 1;
    private List<HistoryDataItem> mHistoryList;
    private boolean mIsSearch = false;
    private String mSearchQuery = "";

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        mUnbind = ButterKnife.bind(this, v);

        mHistoryList = new ArrayList<>();
        setupMVP();
        setupViews();
        getHistoryList();
        mListener.historyFragment(this);
        return v;
    }

    // Upon closing of this fragment views are unbidden and presenter is detached
    @Override
    public void onDestroyView() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mMainPresenter != null)
            mMainPresenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void historyFragment(HistoryFragment fragment);
    }

    // Invoked by main nav activity which passes search query entered in search field
    public void searchQuery(boolean isSearch, String query) {

        mIsSearch = isSearch;
        mSearchQuery = query;
        mCurrentPage = 1;
        mHistoryList.clear();
        if (rvHistory != null && progressBar != null) {
            rvHistory.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        if (mIsSearch) {
            searchHistoryList(mSearchQuery);
        } else {
            getHistoryList();
        }
    }

    // Attaching presenter to this view
    private void setupMVP() {
        mMainPresenter = new HistoryPresenter(this, getActivity());
    }

    // Setting up views
    private void setupViews() {
        //enable back button
        progressBar.setVisibility(View.VISIBLE);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipyRefreshLayout.setOnRefreshListener(this);

    }

    // Requesting presenter to fetch history data
    private void getHistoryList() {
        if (noRecordTv != null)
            noRecordTv.setVisibility(View.INVISIBLE);
        mMainPresenter.getHistory(mCurrentPage);
    }

    // Requesting presenter to fetch history data against searched item
    private void searchHistoryList(String query) {
        if (noRecordTv != null)
            noRecordTv.setVisibility(View.INVISIBLE);
        mMainPresenter.searchHistory(query, mCurrentPage);
    }

    // Invoked by presenter to handle success response from api
    @Override
    public void displayHistory(HistoryResponse historyResponse) {
        progressBar.setVisibility(View.GONE);
        swipyRefreshLayout.setRefreshing(false);
        if (historyResponse != null) {
            if (historyResponse.getActivity() != null) {
                if (historyResponse.getActivity().getData() != null) {
                    if (historyResponse.getActivity().getData().size() == 0 && mCurrentPage == 1 && mHistoryList.size() == 0) {
                        noRecordTv.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
                        String TAG = "MainActivity";
                        Log.d(TAG, "History response null");
                    } else {
                        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
                        noRecordTv.setVisibility(View.GONE);
                        rvHistory.setVisibility(View.VISIBLE);
                        if (mCurrentPage == 1)
                            mHistoryList.clear();
                        mHistoryList.addAll(historyResponse.getActivity().getData());
                        LogListAdapter adapter = new LogListAdapter(mHistoryList);
                        rvHistory.setAdapter(adapter);
                    }
                } else {
                    noRecordTv.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                }
            } else {
                noRecordTv.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            }
        } else {
            noRecordTv.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        }
    }

    // Invoked by presenter to handle failed response from api
    @Override
    public void displayError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        swipyRefreshLayout.setRefreshing(false);
        Utils.showNetworkError(getActivity(), throwable);
    }

    // Invoked upon bottom or top refresh of list
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            mCurrentPage = 1;
            rvHistory.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (mIsSearch)
                searchHistoryList(mSearchQuery);
            else
                getHistoryList();
        } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
            mCurrentPage++;
            if (mIsSearch)
                searchHistoryList(mSearchQuery);
            else
                getHistoryList();
        }
    }
}
