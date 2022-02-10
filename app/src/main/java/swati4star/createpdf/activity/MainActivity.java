package swati4star.createpdf.activity;

import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.fragment.ImageToPdfFragment;
import swati4star.createpdf.providers.fragmentmanagement.FragmentManagement;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FeedbackUtils;
import swati4star.createpdf.util.MainActivityUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.ThemeUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FeedbackUtils mFeedbackUtils;
    private MainActivityUtils mMainActivityUtils;
    private NavigationView mNavigationView;
    //    private SharedPreferences mSharedPreferences;
    private SparseIntArray mFragmentSelectedMap;
    private FragmentManagement mFragmentManagement;

    private boolean mSettingsActivityOpenedForManageStoragePermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mNavigationView = findViewById(R.id.nav_view);
        mMainActivityUtils = new MainActivityUtils();

        mMainActivityUtils.setThemeOnActivityExclusiveComponents(this, mNavigationView);

        checkAndAskForStoragePermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        // Set navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);

        //Replaced setDrawerListener with addDrawerListener because it was deprecated.
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // initialize values
        initializeValues();

        mMainActivityUtils.setXMLParsers();
//        setXMLParsers();

        // Check for app shortcuts & select default fragment
        Fragment fragment = mFragmentManagement.checkForAppShortcutClicked();

        // Check if  images are received
        mMainActivityUtils.handleReceivedImagesIntent(this, fragment);

        mMainActivityUtils.displayFeedBackAndWhatsNew(this, mFeedbackUtils);
//        displayFeedBackAndWhatsNew();

//        if (!isStoragePermissionGranted()) {
//            Log.d("TTTG", "onCreate: here1");
//            getRuntimePermissions();
//        }

        //check for welcome activity
        mMainActivityUtils.openWelcomeActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            DirectoryUtils.makeAndClearTemp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSettingsActivityOpenedForManageStoragePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    askStorageManagerPermission();
                }
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        if (actionBar != null)
            actionBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favourites_item) {
            setTitle(R.string.favourites);
            mFragmentManagement.favouritesFragmentOption();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ininitializes default values
     */
    private void initializeValues() {
        mFeedbackUtils = new FeedbackUtils(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        mFragmentManagement = new FragmentManagement(this, mNavigationView);
        mFragmentSelectedMap = mMainActivityUtils.setTitleMap(mFragmentSelectedMap);
        mMainActivityUtils.setTitleMap(mFragmentSelectedMap);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean shouldExit = mFragmentManagement.handleBackPressed();
            if (shouldExit)
                super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        setTitleFragment(mFragmentSelectedMap.get(item.getItemId()));
        return mFragmentManagement.handleNavigationItemSelected(item.getItemId());
    }

    public void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }

    private void checkAndAskForStoragePermission() {
        if (!PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            getRuntimePermissions();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    askStorageManagerPermission();
                }
            }
        }
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(this, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::askStorageManagerPermission);
    }

    private void askStorageManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.one_more_thing_text)
                        .setMessage(R.string.storage_manager_permission_rationale)
                        .setCancelable(false)
                        .setPositiveButton(R.string.allow_text, (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            mSettingsActivityOpenedForManageStoragePermission = true;
                            startActivity(intent);
                            dialog.dismiss();
                        }).setNegativeButton(R.string.close_app_text, ((dialog, which) -> finishAndRemoveTask()))
                        .show();
            }
        }
    }

    /**
     * puts image uri's in a bundle and start ImageToPdf fragment with this bundle
     * as argument
     *
     * @param imageUris - ArrayList of image uri's in temp directory
     */
    public void convertImagesToPdf(ArrayList<Uri> imageUris) {
        Fragment fragment = new ImageToPdfFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.bundleKey), imageUris);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    /**
     * Sets fragment title
     *
     * @param title - string resource id
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            setTitle(title);
    }

}
