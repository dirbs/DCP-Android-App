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

package com.qualcomm.dcp.feedback.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.qualcomm.dcp.feedback.presenter.FeedbackPresenter;
import com.qualcomm.dcp.utils.Utils;

import com.qualcomm.dcp.R;
import com.qualcomm.dcp.feedback.model.FeedbackResponse;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// View class for controlling feedback fragment
public class FeedbackFragment extends Fragment implements FeedbackViewInterface {

    // Binding view
    @BindView(R.id.feedback_til)
    TextInputLayout feedbackTil;
    @BindView(R.id.feedback_text)
    EditText feedbackText;

    private Unbinder mUnbind;

    private FeedbackPresenter mMainPresenter;
    private SweetAlertDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        mUnbind = ButterKnife.bind(this, v);
        mMainPresenter = new FeedbackPresenter(this, getActivity());

        return v;
    }

    // On closing of this fragment unbind views and detaching presenter from this view
    @Override
    public void onDestroyView() {
        if (mUnbind != null)
            mUnbind.unbind();
        if (mMainPresenter != null)
            mMainPresenter.unbind();
        super.onDestroyView();
    }

    // Invoked upon submit button click
    @OnClick(R.id.buttonSubmit)
    void submitFeedback() {
        if (feedbackText.getText().toString().equals("")) {
            feedbackTil.setError(getResources().getString(R.string.can_not_empty));
        } else {
            feedbackTil.setError(null);
            mProgressDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.PROGRESS_TYPE);
            mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.loading));
            mProgressDialog.setTitleText(getResources().getString(R.string.submitting_feedback));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mMainPresenter.submitFeedback(feedbackText.getText().toString());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // Invoked by presenter to handle successful api response
    @Override
    public void displayHistory(FeedbackResponse feedbackResponse) {
        mProgressDialog.dismiss();
        String titleStr;
        int iconDrw;
        if (feedbackResponse.isSuccess()) {
            titleStr = getString(R.string.feedback_sent);
            iconDrw = R.drawable.ic_success;
            feedbackText.setText("");
        } else {
            titleStr = getString(R.string.oops);
            iconDrw = R.drawable.ic_warn;
        }
        androidx.appcompat.app.AlertDialog.Builder successDialog = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.status_dialog, null);
        successDialog.setView(dialogView).setPositiveButton(getResources().getString(R.string.ok), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(getResources().getDrawable(iconDrw));
        title.setText(titleStr);
        message.setText(feedbackResponse.getMessage());

        androidx.appcompat.app.AlertDialog alertDialog = successDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // Invoked by presenter to handle failure api response
    @Override
    public void displayError(Throwable throwable) {
        mProgressDialog.dismiss();
        Utils.showNetworkError(getActivity(), throwable);
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
    }
}
