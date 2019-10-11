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

package com.qualcomm.dcp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qualcomm.dcp.R;
import com.qualcomm.dcp.history.model.HistoryDataItem;

import java.util.List;

/**
 * Created by Hamza on 14/03/2017.
 */

// Custom adapter for populating data in history fragment.
public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.PersonViewHolder> {
    private final List<HistoryDataItem> mLogs;

    public LogListAdapter(List<HistoryDataItem> logs) {
        this.mLogs = logs;
    }

    @Override
    public int getItemCount() {
        return mLogs.size();
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.logs_card_view, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
        personViewHolder.user.setText(mLogs.get(i).getUserName());
        personViewHolder.source.setText(mLogs.get(i).getUserDevice());
        personViewHolder.deviceId.setText(mLogs.get(i).getVisitorIp());
        personViewHolder.userSearch.setText(mLogs.get(i).getImeiNumber());
        personViewHolder.timestamp.setText(mLogs.get(i).getCreatedAt());
        personViewHolder.result.setText(mLogs.get(i).getResult());

    }

    @Override
    public void onAttachedToRecyclerView(@androidx.annotation.NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        final TextView user;
        final TextView source;
        final TextView deviceId;
        final TextView userSearch;
        final TextView timestamp;
        final TextView result;

        PersonViewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username_txt);
            source = itemView.findViewById(R.id.device_txt);
            deviceId = itemView.findViewById(R.id.ip_txt);
            userSearch = itemView.findViewById(R.id.searched_for_txt);
            timestamp = itemView.findViewById(R.id.time_txt);
            result = itemView.findViewById(R.id.result_txt);
        }
    }
}
