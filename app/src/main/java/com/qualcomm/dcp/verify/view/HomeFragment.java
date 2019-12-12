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

package com.qualcomm.dcp.verify.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.qualcomm.dcp.R;

import java.util.Objects;

// View class for controlling home fragment
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ScannerTab mScannerTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        WebView view = v.findViewById(R.id.instructions_webview);
        view.setVerticalScrollBarEnabled(false);

        WebSettings settings = view.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        view.loadDataWithBaseURL(null, getString(R.string.instruction_detail), "text/html", "utf-8", null);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = v.findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(mViewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(R.layout.custom_tab_title);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(R.layout.custom_tab_title);
        View tab1_view = Objects.requireNonNull(tabLayout.getTabAt(0)).getCustomView();
        assert tab1_view != null;
        TextView tab1_title = tab1_view.findViewById(R.id.tv_tab_title);
        ImageView img1 = tab1_view.findViewById(R.id.img);
        tab1_view.setTag("IMEI_TAB");
        View tab2_view = Objects.requireNonNull(tabLayout.getTabAt(1)).getCustomView();
        assert tab2_view != null;
        TextView tab2_title = tab2_view.findViewById(R.id.tv_tab_title);
        ImageView img2 = tab2_view.findViewById(R.id.img);
        tab2_view.setTag("SCAN_TAB");

        tab1_title.setText(getString(R.string.type_imei));
        img1.setImageResource(R.drawable.ic_action_keyboard);
        tab2_title.setText(getString(R.string.scan_imei));
        img2.setImageResource(R.drawable.ic_action_scanner);

        mScannerTab = new ScannerTab();

        mListener.homeFragment(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    //hide keyboard for second fragment
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.scan_help_text), Snackbar.LENGTH_LONG)
                            .show();
                    mScannerTab.startScanner(true);
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null && getActivity().getCurrentFocus() != null)
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mScannerTab.stopScanner(false);
                }
        }

        @Override
        public void onPageScrollStateChanged ( int state){
        }
    });
        return v;
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

public interface OnFragmentInteractionListener {

    void homeFragment(HomeFragment homeFragment);
}

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public androidx.fragment.app.Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch (position) {
            case 0:
                return new EnterImeiFragment();
            case 1:
                return mScannerTab;
        }
        return new EnterImeiFragment();
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
            case 1:
                return "";
        }
        return null;
    }
}
}
