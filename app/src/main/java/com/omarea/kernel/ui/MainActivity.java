package com.omarea.kernel.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omarea.kernel.BuildConfig;
import com.omarea.kernel.R;
import com.omarea.kernel.ui.fragments.BuildPropEditorFragment;
import com.omarea.kernel.ui.fragments.ChargeBoosterFragment;
import com.omarea.kernel.ui.fragments.CoreControlFragment;
import com.omarea.kernel.ui.fragments.CpuFrequencyFragment;
import com.omarea.kernel.ui.fragments.CpuHotplugFragment;
import com.omarea.kernel.ui.fragments.CpusetFragment;
import com.omarea.kernel.ui.fragments.GovernorTuningFragment;
import com.omarea.kernel.ui.fragments.GpuControlFragment;
import com.omarea.kernel.ui.fragments.IOControlFragment;
import com.omarea.kernel.ui.fragments.SettingsFragment;
import com.omarea.kernel.ui.fragments.ThermalFragment;
import com.omarea.kernel.ui.fragments.TimeInStatesFragment;
import com.omarea.kernel.ui.fragments.VirtualMemoryFragment;
import com.omarea.kernel.ui.fragments.WakeLocksFragment;
import com.omarea.kernel.utils.CPUHotplugUtils;
import com.omarea.kernel.utils.ChargerBoosterUtils;
import com.omarea.kernel.utils.CoreControlUtils;
import com.omarea.kernel.utils.CpuSetUtils;
import com.omarea.kernel.utils.GpuUtils;
import com.omarea.kernel.utils.ThermalControlUtils;
import com.splunk.mint.Mint;
import com.stericson.RootTools.RootTools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private TextView appCompatibilityMessage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.ENABLE_ANALYTICS) {
            Mint.initAndStartSession(this.getApplication(), "7e3b93f8");
            Mint.enableDebugLog();
            Mint.enableLogging(true);
        }
        setContentView(R.layout.fragment_main_layout_navbar);

        navigationView = findViewById(R.id.navigation);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        appCompatibilityMessage = findViewById(R.id.app_compatibility_status);
        progressBar = findViewById(R.id.loading_main);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        //disable the navigation bar initially
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar,
                R.string.settings, R.string.settings) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        new Task().execute();
    }

    public void populateGui() {

        //enable navigation drawer
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        actionBar.setTitle("CPU");

        //TODO add settings based on whether they are supported or not
        if (GpuUtils.isGpuFrequencyScalingSupported()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_gpu);
            menuItem.setVisible(true);
        }
        if (CPUHotplugUtils.hasCpuHotplug()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_cpu_hotplug);
            menuItem.setVisible(true);
        }
        if (CoreControlUtils.isSupported()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_core);
            menuItem.setVisible(true);
        }
        if (ChargerBoosterUtils.isSupported()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_battery_charger);
            menuItem.setVisible(true);
        }
        if (ThermalControlUtils.isSupported()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_thermal_control);
            menuItem.setVisible(true);
        }
        if (CpuSetUtils.isSupported()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_cpuset);
            menuItem.setVisible(true);
        }

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_cpu));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_cpu:
                        fragment = new CpuFrequencyFragment();
                        actionBar.setTitle(getString(R.string.cpu));
                        break;
                    case R.id.nav_core:
                        fragment = new CoreControlFragment();
                        actionBar.setTitle(getString(R.string.core_control));
                        break;
                    case R.id.nav_thermal_control:
                        fragment = new ThermalFragment();
                        actionBar.setTitle(getString(R.string.thermal_control));
                        break;
                    case R.id.nav_battery_charger:
                        fragment = new ChargeBoosterFragment();
                        actionBar.setTitle(R.string.battery_charger);
                        break;
                    case R.id.nav_cpuset:
                        fragment = new CpusetFragment();
                        actionBar.setTitle(R.string.cpuset);
                        break;
                    case R.id.nav_tis:
                        fragment = new TimeInStatesFragment();
                        actionBar.setTitle(R.string.time_in_state);
                        break;
                    case R.id.nav_iocontrol:
                        fragment = new IOControlFragment();
                        actionBar.setTitle(getString(R.string.io));
                        break;
                    case R.id.nav_wakelocks:
                        fragment = new WakeLocksFragment();
                        actionBar.setTitle(getString(R.string.wakelocks));
                        break;
                    case R.id.nav_settings:
                        fragment = new SettingsFragment();
                        actionBar.setTitle(getString(R.string.settings));
                        break;
                    case R.id.nav_gpu:
                        fragment = new GpuControlFragment();
                        actionBar.setTitle(getString(R.string.gpu));
                        break;
                    case R.id.build_prop:
                        fragment = new BuildPropEditorFragment();
                        actionBar.setTitle(R.string.build_prop);
                        break;
                    case R.id.vm:
                        fragment = new VirtualMemoryFragment();
                        actionBar.setTitle(getString(R.string.vm));
                        break;
                    case R.id.nav_cpu_hotplug:
                        fragment = new CpuHotplugFragment();
                        actionBar.setTitle(getString(R.string.cpu_hotplug));
                        break;
                }
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_animation);
                    fragmentTransaction.replace(R.id.main_content, fragment).commitAllowingStateLoss();
                }
            }
        }, 400);

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().findFragmentByTag(GovernorTuningFragment.TAG) != null
                && getFragmentManager().findFragmentByTag(GovernorTuningFragment.TAG).isVisible()) {
            //To go back to cpu frequency fragment by pressing back button
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.main_content, new CpuFrequencyFragment())
//                    .commit();
        } else {
            //  Toast.makeText(getBaseContext(), "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    private class Task extends AsyncTask<Void, Void, Void> {
        private boolean hasRoot, hasBusyBox;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            hasRoot = RootTools.isAccessGiven();
            hasBusyBox = RootTools.isBusyboxAvailable() || RootTools.findBinary("toybox");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (hasRoot && hasBusyBox) {
                populateGui();
            } else {

                progressBar.setVisibility(View.GONE);

                appCompatibilityMessage.setVisibility(View.VISIBLE);
                appCompatibilityMessage
                        .setText(!hasRoot ? "No root access found" : "No Busybox found");

                if (hasRoot) {
                    //TODO redirect to playstore for installing busybox
                    try {
                        startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri
                                        .parse("market://details?id=stericson.busybox")));
                    } catch (ActivityNotFoundException ignored) {
                    }
                }
            }
        }
    }
}
