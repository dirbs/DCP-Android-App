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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.qualcomm.dcp.feedback.view.FeedbackFragment;
import com.qualcomm.dcp.history.view.HistoryFragment;
import com.qualcomm.dcp.utils.MyPreferences;
import com.qualcomm.dcp.utils.UserSessionManager;
import com.qualcomm.dcp.utils.Utils;
import com.qualcomm.dcp.verify.view.HomeFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

// View class for main nav activity
public class MainNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        View.OnClickListener,
        HistoryFragment.OnFragmentInteractionListener,
        FeedbackFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    @BindView(com.qualcomm.dcp.R.id.toolbar)
    public Toolbar toolbar;
    @BindView(com.qualcomm.dcp.R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @BindView(com.qualcomm.dcp.R.id.navigationView)
    public NavigationView navigationView;

    public NavController navController;
    private HistoryFragment historyFragment;
    public HomeFragment homeFragment;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.qualcomm.dcp.R.style.AppTheme);
        super.onCreate(savedInstanceState);
        MyPreferences myPreferences = new MyPreferences(MainNavActivity.this);
        Utils.changeLanguageLocale(getBaseContext(), myPreferences.getString("locale", "en"));
        setContentView(com.qualcomm.dcp.R.layout.activity_main_nav);
        ButterKnife.bind(this);

        setupNavigation();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            MyPreferences myPreferences = new MyPreferences(newBase);
            super.attachBaseContext(ContextWrapper.wrap(newBase, myPreferences.getString("locale", "en")));
//        }
//        else {
//            super.attachBaseContext(newBase);
//        }
    }

    // Setting Up One Time Navigation
    private void setupNavigation() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(com.qualcomm.dcp.R.string.home);

        UserSessionManager userSessionManager = new UserSessionManager(MainNavActivity.this);
        if (userSessionManager.getIsAdmin() != null) {
            if (!userSessionManager.getIsAdmin().equals("staff")) {
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(com.qualcomm.dcp.R.id.feedback).setVisible(false);
            }
        }
        navController = Navigation.findNavController(this, com.qualcomm.dcp.R.id.nav_host_fragment);

        if (getIntent().hasExtra("fragment")) {
            navController.navigate(getIntent().getIntExtra("fragment", com.qualcomm.dcp.R.id.homeFragment));
        }

        navController.addOnNavigatedListener((controller, destination) -> {
            String label = String.valueOf(destination.getLabel());
            showHideSearch(label);
        });

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout =
                navigationView.inflateHeaderView(com.qualcomm.dcp.R.layout.header_layout);
        AppCompatTextView name = headerLayout.findViewById(com.qualcomm.dcp.R.id.full_name);
        AppCompatTextView email = headerLayout.findViewById(com.qualcomm.dcp.R.id.email);

        ImageView language = headerLayout.findViewById(com.qualcomm.dcp.R.id.language);
        language.setOnClickListener(this);

        name.setText(userSessionManager.getUSERNAME());
        email.setText(userSessionManager.getUserId());
        MyPreferences myPreferences = new MyPreferences(MainNavActivity.this);
        if (myPreferences.getString("locale", "en").equals("en"))
            language.setImageResource(com.qualcomm.dcp.R.drawable.ic_vietnamese);
        else
            language.setImageResource(com.qualcomm.dcp.R.drawable.ic_english);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, com.qualcomm.dcp.R.id.nav_host_fragment));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Invoked upon selection of any item in navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();
        switch (id) {
            case com.qualcomm.dcp.R.id.home: {
                navController.navigate(com.qualcomm.dcp.R.id.homeFragment);
                String label = String.valueOf(Objects.requireNonNull(navController.getCurrentDestination()).getLabel());
                showHideSearch(label);
                break;
            }
            case com.qualcomm.dcp.R.id.history: {
                navController.navigate(com.qualcomm.dcp.R.id.historyFragment);
                String label = String.valueOf(Objects.requireNonNull(navController.getCurrentDestination()).getLabel());
                showHideSearch(label);
                break;
            }
            case com.qualcomm.dcp.R.id.feedback: {
                navController.navigate(com.qualcomm.dcp.R.id.feedbackFragment);
                String label = String.valueOf(Objects.requireNonNull(navController.getCurrentDestination()).getLabel());
                showHideSearch(label);
                break;
            }
//            case R.id.about: {
//                navController.navigate(R.id.aboutFragment);
//                String label = String.valueOf(navController.getCurrentDestination().getLabel());
//                currentFragment = label;
//                showHideSearch(label);
//                break;
//            }
            case com.qualcomm.dcp.R.id.logout:
                Utils.showLogoutConfirmationDialog(MainNavActivity.this);
                break;
        }
        return true;

    }

    @Override
    public void homeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public void historyFragment(HistoryFragment fragment) {
        historyFragment = fragment;
    }

    // Invoked upon clicks on views present in this activity
    @Override
    public void onClick(View view) {
        if (view.getId() == com.qualcomm.dcp.R.id.language) {
            MyPreferences myPreferences = new MyPreferences(MainNavActivity.this);
            if (myPreferences.getString("locale", "en").equals("en"))
                myPreferences.setString("locale", "vi");
            else
                myPreferences.setString("locale", "en");

            Intent intent = new Intent(MainNavActivity.this, MainNavActivity.class);
            intent.putExtra("fragment", Objects.requireNonNull(navController.getCurrentDestination()).getId());
            startActivity(intent);
            MainNavActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.qualcomm.dcp.R.menu.menu_search, menu);
        searchItem = menu.findItem(com.qualcomm.dcp.R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            EditText searchPlate = searchView.findViewById(com.qualcomm.dcp.R.id.search_src_text);
            searchPlate.setHint(com.qualcomm.dcp.R.string.search);
            searchPlate.setInputType(InputType.TYPE_CLASS_NUMBER);
            searchPlate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            View searchPlateView = searchView.findViewById(com.qualcomm.dcp.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {

                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    if (historyFragment != null)
                        historyFragment.searchQuery(false, "");

                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    if (historyFragment != null)
                        historyFragment.searchQuery(true, query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }

            searchItem.setVisible(false);
            showHideSearch(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString());
        }
        return true;
    }

    private void showHideSearch(String label) {
        if (label.equals(getResources().getString(com.qualcomm.dcp.R.string.history))) {
            if (searchItem != null)
                searchItem.setVisible(true);
        } else if (label.equals(getResources().getString(com.qualcomm.dcp.R.string.home))) {
            if (searchItem != null)
                searchItem.setVisible(false);
        } else if (label.equals(getResources().getString(com.qualcomm.dcp.R.string.feedback))) {
            if (searchItem != null)
                searchItem.setVisible(false);
        }
    }
}