package swati4star.createpdf.util;

import static swati4star.createpdf.util.Constants.IS_WELCOME_ACTIVITY_SHOWN;
import static swati4star.createpdf.util.Constants.LAUNCH_COUNT;
import static swati4star.createpdf.util.Constants.THEME_BLACK;
import static swati4star.createpdf.util.Constants.THEME_DARK;
import static swati4star.createpdf.util.Constants.THEME_SYSTEM;
import static swati4star.createpdf.util.Constants.THEME_WHITE;
import static swati4star.createpdf.util.Constants.VERSION_NAME;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseIntArray;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import swati4star.createpdf.BuildConfig;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.WelcomeActivity;

public class MainActivityUtils {

    private SharedPreferences mSharedPreferences;
    private SparseIntArray mFragmentSelectedMap;

    /**
     * Set suitable xml parsers for reading .docx files.
     */
    public void setXMLParsers() {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");
    }

    /**
     * A method for the feedback and whats new dialogs.
     */
    public void displayFeedBackAndWhatsNew(Activity activity, FeedbackUtils mFeedbackUtils) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int count = mSharedPreferences.getInt(LAUNCH_COUNT, 0);
        if (count > 0 && count % 15 == 0) {
            mFeedbackUtils.rateUs();
        }
        if (count != -1) {
            mSharedPreferences.edit().putInt(LAUNCH_COUNT, count + 1).apply();
        }

        String versionName = mSharedPreferences.getString(VERSION_NAME, "");
        if (versionName != null && !versionName.equals(BuildConfig.VERSION_NAME)) {
            WhatsNewUtils.getInstance().displayDialog(activity);
            mSharedPreferences.edit().putString(VERSION_NAME, BuildConfig.VERSION_NAME).apply();
        }
    }

    /**
     * if welcome activity isnt opened ever, it is shown
     */
    public void openWelcomeActivity(Activity activity) {
        if (!mSharedPreferences.getBoolean(IS_WELCOME_ACTIVITY_SHOWN, false)) {
            Intent intent = new Intent(activity, WelcomeActivity.class);
            mSharedPreferences.edit().putBoolean(IS_WELCOME_ACTIVITY_SHOWN, true).apply();
            activity.startActivity(intent);
        }
    }

    /**
     * Checks if images are received in the intent
     *
     * @param fragment - instance of current fragment
     */
    public void handleReceivedImagesIntent(Activity activity, Fragment fragment) {
        Intent intent = activity.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (type == null || !type.startsWith("image/"))
            return;

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            handleSendMultipleImages(activity, intent, fragment); // Handle multiple images
        } else if (Intent.ACTION_SEND.equals(action)) {
            handleSendImage(activity, intent, fragment); // Handle single image
        }
    }

    /**
     * Get image uri from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    private void handleSendImage(Activity activity, Intent intent, Fragment fragment) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris.add(uri);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(activity.getString(R.string.bundleKey), imageUris);
        fragment.setArguments(bundle);
    }

    /**
     * Get ArrayList of image uris from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    private void handleSendMultipleImages(Activity activity, Intent intent, Fragment fragment) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(activity.getString(R.string.bundleKey), imageUris);
            fragment.setArguments(bundle);
        }
    }

    // Hashmap for setting the mFragmentSelectedMap.
    public SparseIntArray setTitleMap(SparseIntArray mFragmentSelectedMap) {
        mFragmentSelectedMap = new SparseIntArray();
        mFragmentSelectedMap.append(R.id.nav_home, R.string.app_name);
        mFragmentSelectedMap.append(R.id.nav_camera, R.string.images_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_qrcode, R.string.qr_barcode_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_text, R.string.add_text);
        mFragmentSelectedMap.append(R.id.nav_gallery, R.string.viewFiles);
        mFragmentSelectedMap.append(R.id.nav_merge, R.string.merge_pdf);
        mFragmentSelectedMap.append(R.id.nav_split, R.string.split_pdf);
        mFragmentSelectedMap.append(R.id.nav_text_to_pdf, R.string.text_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_history, R.string.history);
        mFragmentSelectedMap.append(R.id.nav_add_password, R.string.add_password);
        mFragmentSelectedMap.append(R.id.nav_remove_password, R.string.remove_password);
        mFragmentSelectedMap.append(R.id.nav_about, R.string.about_us);
        mFragmentSelectedMap.append(R.id.nav_settings, R.string.settings);
        mFragmentSelectedMap.append(R.id.nav_extract_images, R.string.extract_images);
        mFragmentSelectedMap.append(R.id.nav_pdf_to_images, R.string.pdf_to_images);
        mFragmentSelectedMap.append(R.id.nav_remove_pages, R.string.remove_pages);
        mFragmentSelectedMap.append(R.id.nav_rearrange_pages, R.string.reorder_pages);
        mFragmentSelectedMap.append(R.id.nav_compress_pdf, R.string.compress_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_images, R.string.add_images);
        mFragmentSelectedMap.append(R.id.nav_remove_duplicate_pages, R.string.remove_duplicate_pages);
        mFragmentSelectedMap.append(R.id.nav_invert_pdf, R.string.invert_pdf);
        mFragmentSelectedMap.append(R.id.nav_add_watermark, R.string.add_watermark);
        mFragmentSelectedMap.append(R.id.nav_zip_to_pdf, R.string.zip_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_rotate_pages, R.string.rotate_pages);
        mFragmentSelectedMap.append(R.id.nav_excel_to_pdf, R.string.excel_to_pdf);
        mFragmentSelectedMap.append(R.id.nav_faq, R.string.faqs);
        return mFragmentSelectedMap;
    }

    public void setThemeOnActivityExclusiveComponents(Activity activity, NavigationView mNavigationView) {
        RelativeLayout toolbarBackgroundLayout = activity.findViewById(R.id.toolbar_background_layout);
        MaterialCardView content = activity.findViewById(R.id.content);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String themeName = mSharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                Constants.DEFAULT_THEME);
        switch (themeName) {
            case THEME_WHITE:
                toolbarBackgroundLayout.setBackgroundResource(R.drawable.toolbar_bg);
                content.setCardBackgroundColor(activity.getResources().getColor(R.color.lighter_gray));
                mNavigationView.setBackgroundResource(R.color.white);
                break;
            case THEME_BLACK:
                toolbarBackgroundLayout.setBackgroundResource(R.color.black);
                content.setCardBackgroundColor(activity.getResources().getColor(R.color.black));
                mNavigationView.setBackgroundResource(R.color.black);
                mNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                mNavigationView.setItemIconTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                mNavigationView.setItemBackgroundResource(R.drawable.navigation_item_selected_bg_selector_dark);
                break;
            case THEME_DARK:
                toolbarBackgroundLayout.setBackgroundResource(R.color.colorBlackAltLight);
                content.setCardBackgroundColor(activity.getResources().getColor(R.color.colorBlackAlt));
                mNavigationView.setBackgroundResource(R.color.colorBlackAlt);
                mNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                mNavigationView.setItemIconTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                mNavigationView.setItemBackgroundResource(R.drawable.navigation_item_selected_bg_selector_dark);
                break;
            case THEME_SYSTEM:
            default:
                if ((activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    toolbarBackgroundLayout.setBackgroundResource(R.color.colorBlackAltLight);
                    content.setCardBackgroundColor(activity.getResources().getColor(R.color.colorBlackAlt));
                    mNavigationView.setBackgroundResource(R.color.colorBlackAlt);
                    mNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                    mNavigationView.setItemIconTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
                    mNavigationView.setItemBackgroundResource(R.drawable.navigation_item_selected_bg_selector_dark);
                } else {
                    toolbarBackgroundLayout.setBackgroundResource(R.drawable.toolbar_bg);
                    content.setCardBackgroundColor(activity.getResources().getColor(R.color.lighter_gray));
                    mNavigationView.setBackgroundResource(R.color.white);
                }
        }
    }
}

