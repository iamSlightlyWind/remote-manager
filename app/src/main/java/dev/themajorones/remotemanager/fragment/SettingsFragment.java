package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Locale;

import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_THEME = "theme";

    private static final String THEME_KEY_LIGHT = "light";
    private static final String THEME_KEY_DARK = "dark";
    private static final String THEME_KEY_SYSTEM = "system";

    private AutoCompleteTextView themeDropdown;
    private MaterialButtonToggleGroup languageToggleGroup;
    private MaterialButton buttonEmail;
    private MaterialButton buttonGitHub;
    private boolean isLoadingSettings = false;

    private static SettingsFragment savedInstance;

    public SettingsFragment() {
        super(R.layout.settings);
    }

    public static SettingsFragment getInstance() {
        return savedInstance;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupThemeDropdown();
        loadSettings();
        setupLanguageToggle();
        setupCreditButtons();

        savedInstance = this;
    }

    private void initializeViews(@NonNull View view) {
        themeDropdown = view.findViewById(R.id.theme_dropdown);
        languageToggleGroup = view.findViewById(R.id.language_toggle_group);
        MaterialButton buttonEnglish = view.findViewById(R.id.button_english);
        MaterialButton buttonVietnamese = view.findViewById(R.id.button_vietnamese);
        buttonEmail = view.findViewById(R.id.button_email);
        buttonGitHub = view.findViewById(R.id.button_github);
    }

    private void setupThemeDropdown() {
        String[] themeOptions = {
                getString(R.string.theme_light),
                getString(R.string.theme_dark),
                getString(R.string.theme_system)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                themeOptions
        );

        themeDropdown.setAdapter(adapter);

        String savedThemeKey = getThemePreference();
        themeDropdown.setText(getThemeDisplayName(savedThemeKey), false);

        themeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedThemeKey = getThemeKeyFromDisplay(themeOptions[position]);
            onThemeChanged(selectedThemeKey);
        });
    }

    private String getThemeDisplayName(String themeKey) {
        if (THEME_KEY_LIGHT.equals(themeKey)) {
            return getString(R.string.theme_light);
        } else if (THEME_KEY_DARK.equals(themeKey)) {
            return getString(R.string.theme_dark);
        } else {
            return getString(R.string.theme_system);
        }
    }

    private String getThemeKeyFromDisplay(String displayName) {
        if (displayName.equals(getString(R.string.theme_light))) {
            return THEME_KEY_LIGHT;
        } else if (displayName.equals(getString(R.string.theme_dark))) {
            return THEME_KEY_DARK;
        } else {
            return THEME_KEY_SYSTEM;
        }
    }

    private void onThemeChanged(String themeKey) {
        try {
            saveThemePreference(themeKey);
            applyTheme(themeKey);

            if (THEME_KEY_LIGHT.equals(themeKey)) {
                ViewUtils.notify("Light theme selected");
            } else if (THEME_KEY_DARK.equals(themeKey)) {
                ViewUtils.notify("Dark theme selected");
            } else if (THEME_KEY_SYSTEM.equals(themeKey)) {
                ViewUtils.notify("System theme selected");
            }
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed to change theme: ", e);
        }
    }

    private void applyTheme(String themeKey) {
        if (THEME_KEY_LIGHT.equals(themeKey)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (THEME_KEY_DARK.equals(themeKey)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (THEME_KEY_SYSTEM.equals(themeKey)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void onLanguageChanged(String languageCode) {
        try {
            saveLanguagePreference(languageCode);

            if (languageCode.equals("en")) {
                ViewUtils.notify("English language selected");
            } else if (languageCode.equals("vi")) {
                ViewUtils.notify("Vietnamese language selected");
            }

            setAppLocale(languageCode);

            requireActivity().finish();
            requireActivity().startActivity(requireActivity().getIntent());
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed to change language: ", e);
        }
    }

    private void setAppLocale(String languageCode) {
        Locale locale = Locale.forLanguageTag(languageCode);
        Locale.setDefault(locale);

        Context context = requireContext();
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_LANGUAGE, languageCode).apply();
    }

    private String getLanguagePreference() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_LANGUAGE, "en");
    }

    private void saveThemePreference(String themeKey) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_THEME, themeKey).apply();
    }

    private String getThemePreference() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_THEME, THEME_KEY_SYSTEM);
    }

    public void loadSettings() {
        try {
            isLoadingSettings = true; 
            String savedThemeKey = getThemePreference();
            themeDropdown.setText(getThemeDisplayName(savedThemeKey), false);

            applyTheme(savedThemeKey);

            String savedLanguage = getLanguagePreference();
            if (savedLanguage.equals("en")) {
                languageToggleGroup.check(R.id.button_english);
            } else if (savedLanguage.equals("vi")) {
                languageToggleGroup.check(R.id.button_vietnamese);
            }

            isLoadingSettings = false;
        } catch (Exception e) {
            isLoadingSettings = false;
            ViewUtils.throwNotify("Failed to load settings: ", e);
        }
    }

    public void saveSettings() {
        try {
            ViewUtils.notify("Settings saved successfully");
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed to save settings: ", e);
        }
    }

    public void resetSettings() {
        try {
            isLoadingSettings = true; 
            themeDropdown.setText(getThemeDisplayName(THEME_KEY_SYSTEM), false);
            languageToggleGroup.check(R.id.button_english);

            saveThemePreference(THEME_KEY_SYSTEM);
            saveLanguagePreference("en");
            setAppLocale("en");

            applyTheme(THEME_KEY_SYSTEM);

            isLoadingSettings = false;
            ViewUtils.notify("Settings reset to defaults");
        } catch (Exception e) {
            isLoadingSettings = false;
            ViewUtils.throwNotify("Failed to reset settings: ", e);
        }
    }

    private void setupLanguageToggle() {
        languageToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && !isLoadingSettings) {
                if (checkedId == R.id.button_english) {
                    onLanguageChanged("en");
                } else if (checkedId == R.id.button_vietnamese) {
                    onLanguageChanged("vi");
                }
            }
        });
    }

    private void setupCreditButtons() {
        buttonEmail.setOnClickListener(v -> openEmailApp());
        buttonGitHub.setOnClickListener(v -> openGitHubRepo());
    }

    private void openEmailApp() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            String[] recipients = new String[] { "iam.slightlywind@themajorones.dev" };
            emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Remote Manager App Feedback");

            Intent chooser = Intent.createChooser(emailIntent, "Send feedback via");
            if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                ViewUtils.notify("No email app found");
            }
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed to open email app: ", e);
        }
    }

    private void openGitHubRepo() {
        try {
            Uri uri = Uri.parse("https://github.com/iamSlightlyWind");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);

            if (browserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                ViewUtils.notify("No browser app found");
            }
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed to open GitHub repository: ", e);
        }
    }

    public static void applyLanguageSettings(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedLanguage = prefs.getString(PREF_LANGUAGE, "en");

            Locale locale = Locale.forLanguageTag(savedLanguage);
            Locale.setDefault(locale);

            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(locale);

            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyThemeSettings(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedThemeKey = prefs.getString(PREF_THEME, THEME_KEY_SYSTEM);

            if (THEME_KEY_LIGHT.equals(savedThemeKey)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (THEME_KEY_DARK.equals(savedThemeKey)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}