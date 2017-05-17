/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.packet.*;
import com.bodymedia.btle.packet.Handedness;
import com.sensors.mobile.app.BM.AppPrefs;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.bodymedia.mobile.sdk.model.ArmbandConfiguration;
import com.bodymedia.mobile.sdk.validator.ValidationError;
import com.bodymedia.mobile.sdk.validator.ValidationException;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class UserInfoFragment extends AbstractArmbandFragment {

    private static final Logger LOG = Logger.getInstance(UserInfoFragment.class);

    private static final double LBS_IN_KG = 2.2046;
    private static final double INCHES_IN_CM = 0.3937;

    private ProgressDialog pDialog;
    private EditText massInKgText;
    private EditText massInLbsText;
    private EditText heightInCmText;
    private EditText heightInInchText;
    private EditText dayWrap;
    private EditText sleepWrap;
    private Switch enableECG;
    private Switch smoker;
    private Switch offBodyEstimates;
    private Switch alwaysOn;
    private DatePicker datePicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_info_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (checkConnectedShowToast()) {
                    try {
                        saveArmbandData();
                    } catch (ValidationException validationException) {
                        LOG.w(validationException, "Validation exception occurred.");
                        handleValidationErrors(validationException.getValidationErrors());
                    }
                }
                break;
            case R.id.show_minute_rate:
                ((MainActivityBM) getActivity()).showFragment(new MinuteRateFragment());
                break;
            case R.id.show_device_info:
                ((MainActivityBM) getActivity()).showFragment(new ConnectedDeviceFragment());
                break;
            case R.id.show_high_rate_screen:
                ((MainActivityBM) getActivity()).showFragment(new HighRateFragment());
                break;
        }
        UIUtils.hideKeyboard(getActivity());
        return super.onOptionsItemSelected(item);
    }

    // Overriding method from AbstractArmbandFragment instead of standard onCreateView.
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.user_info));

        View layout = inflater.inflate(R.layout.user_info, container, false);

        initViews(layout);

        // Two mass fields are depends on each other (as well as height fields).
        // When entering value in one, the other is changing automatically (just converting value from the focused field).
        massInKgText.addTextChangedListener(generateWatcherForDependentFields(massInKgText, massInLbsText, LBS_IN_KG));
        massInLbsText.addTextChangedListener(generateWatcherForDependentFields(massInLbsText, massInKgText, 1 / LBS_IN_KG));
        heightInCmText.addTextChangedListener(generateWatcherForDependentFields(heightInCmText, heightInInchText, INCHES_IN_CM));
        heightInInchText.addTextChangedListener(generateWatcherForDependentFields(heightInInchText, heightInCmText, 1 / INCHES_IN_CM));

        return layout;
    }

    private void initViews(View view){
        massInKgText = (EditText) view.findViewById(R.id.mass_in_kg);
        massInLbsText = (EditText) view.findViewById(R.id.mass_in_lbs);
        heightInCmText = (EditText) view.findViewById(R.id.height_in_cm);
        heightInInchText = (EditText) view.findViewById(R.id.height_in_inch);
        dayWrap = (EditText) view.findViewById(R.id.day_wrap);
        sleepWrap = (EditText) view.findViewById(R.id.sleep_wrap);
        enableECG = (Switch) view.findViewById(R.id.enableECGToggleButton);
        smoker = (Switch) view.findViewById(R.id.smokerToggleButton);
        offBodyEstimates = (Switch) view.findViewById(R.id.offBodyEstimatesToggleButton);
        alwaysOn = (Switch) view.findViewById(R.id.enableAlwaysOnToggleButton);
        datePicker = (DatePicker) view.findViewById(R.id.birthdayPicker);
    }

    /**
     * Generate watcher for text field that has dependent field. E.g., two fields that measure one parameter but with different units: kg - lbs, cm - inches, etc.
     * @param mainField         Watcher should be generated for this field. Entering value in it will change value in dependentField.
     * @param dependentField    Value of this field will be changed according to the value in mainField.
     * @param modifier          Value modifier. Value in dependentField is calculated with formula: value_in_mainField * modifier
     * @return                  Text watcher that handles input in mainField.
     */
    private TextWatcher generateWatcherForDependentFields(final EditText mainField, final EditText dependentField, final double modifier) {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (mainField.isFocused()) {
                    if (s.toString().trim().length() == 0) {
                        dependentField.setText("0");
                        return;
                    }
                    try {
                        double number = Integer.parseInt(s.toString()) * modifier;
                        dependentField.setText(String.format(Locale.ENGLISH, "%d", (int) Math.round(number)));
                    } catch (NumberFormatException e) {
                        dependentField.setText("0");
                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        loadArmbandData();
    }

    @Override
    public void onDestroy() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
        super.onDestroy();
    }

    /**
     * Sends request to the armband to get its configuration. When configuration is received, updates UI.
     */
    protected void loadArmbandData() {
        SenseWearApplication.get().getArmband().readUserConfiguration()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArmbandConfiguration>() {

                    @Override
                    public void onCompleted() {
                        LOG.d("Armbend data loading completed.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOG.e(e, "Something went wrong trying to get the configuration");
                        UIUtils.showToast(getActivity(),
                                "Something went wrong getting Armband Configuration:" + e.getMessage());
                    }

                    @Override
                    public void onNext(ArmbandConfiguration armbandConfiguration) {
                        updateConfigurationView(armbandConfiguration);
                    }

                });
    }

    protected void saveArmbandData() throws ValidationException {
        try {
            final ArmbandConfiguration armbandConfiguration = getArmbandConfigurationFromView();
            LOG.d("We're going to update the armband configuration:" + armbandConfiguration);
            configureArmband(armbandConfiguration);
        } catch (NumberFormatException nfe) {
            LOG.e(nfe, "Caught format exception occurred.");
            UIUtils.showToast(getActivity(), "Please enter valid values into the form fields.");
        }
    }

    /**
     * Returns an ID of text field that caused an exception during configuration saving.
     * @param error Error instance received during configuration saving.
     * @return ID of the text field that caused the error.
     */
    protected int getFieldId(ValidationError error) {
        switch (error.getField()) {
        case MASS:
            return R.id.mass_in_kg;
        case HEIGHT:
            return R.id.height_in_cm;
        case DAY_WRAP:
            return R.id.day_wrap;
        case SLEEP_WRAP:
            return R.id.sleep_wrap;
        default:
            return -1;
        }
    }

    /**
     * Generates armband configuration from entered data.
     * @return
     */
    public ArmbandConfiguration getArmbandConfigurationFromView() {

        final ArmbandConfiguration armbandConfiguration = new ArmbandConfiguration(DateTimeZone.getDefault());
        armbandConfiguration.setMassInKg(Double.valueOf(massInKgText.getText().toString()));
        armbandConfiguration.setHeightInCm(Integer.valueOf(heightInCmText.getText().toString()));
        armbandConfiguration.setConfigured(true);
        armbandConfiguration.setGender(getSelectedGender());
        armbandConfiguration.setHandedness(getSelectedHandedness());
        armbandConfiguration.setEnableEcgSensor(enableECG.isChecked());
        armbandConfiguration.setSmoker(smoker.isChecked());
        armbandConfiguration.setEnableOffBodyEstimates(offBodyEstimates.isChecked());
        armbandConfiguration.setDayWrap(Integer.valueOf(dayWrap.getText().toString()));
        armbandConfiguration.setSleepWrap(Integer.valueOf(sleepWrap.getText().toString()));
        armbandConfiguration.setEnableAlwaysOn(alwaysOn.isChecked());

        LocalDate birthday = new LocalDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        armbandConfiguration.setUserBirthday(birthday);

        // Using default values for simplicity
        armbandConfiguration.setModerateThreshold(2);
        armbandConfiguration.setVigorousThreshold(5);
        armbandConfiguration.setUserId("android-1");

        LOG.d("Value of ArmbandConfiguration loaded from UI: " + armbandConfiguration);
        return armbandConfiguration;
    }

    /**
     * Updates fragment view according to the received armband configuration.
     * @param armbandConfiguration
     */
    private void updateConfigurationView(ArmbandConfiguration armbandConfiguration) {
        double massInKg = armbandConfiguration.getMassInKg();
        massInKgText.setText(String.valueOf(Math.round(massInKg)));
        massInLbsText.setText(String.valueOf(Math.round(massInKg * LBS_IN_KG)));
        double heightInCm = armbandConfiguration.getHeightInCm();
        heightInCmText.setText(String.valueOf(Math.round(heightInCm)));
        heightInInchText.setText(String.valueOf(Math.round(heightInCm * INCHES_IN_CM)));
        enableECG.setChecked(armbandConfiguration.isEnableEcgSensor());
        smoker.setChecked(armbandConfiguration.isSmoker());
        offBodyEstimates.setChecked(armbandConfiguration.isEnableOffBodyEstimates());
        alwaysOn.setChecked(armbandConfiguration.isEnableAlwaysOn());
        dayWrap.setText(String.valueOf(armbandConfiguration.getDayWrap()));
        sleepWrap.setText(String.valueOf(armbandConfiguration.getSleepWrap()));

        final LocalDate birthday = armbandConfiguration.getUserBirthday();
        datePicker.updateDate(birthday.getYear(), birthday.getMonthOfYear() - 1, birthday.getDayOfMonth());

        setButtonChecked(R.id.maleRadioButton, armbandConfiguration.getGender() == Gender.MALE);
        setButtonChecked(R.id.femaleRadioButton, armbandConfiguration.getGender() == Gender.FEMALE);
        setButtonChecked(R.id.leftRadioButton, armbandConfiguration.getHandedness() == Handedness.LEFT);
        setButtonChecked(R.id.rightRadioButton, armbandConfiguration.getHandedness() == Handedness.RIGHT);

        AppPrefs.getInstance().setECGstatus(armbandConfiguration.isEnableEcgSensor());
    }

    private void setButtonChecked(final int buttonId, final boolean checked) {
        ((CompoundButton) getActivity().findViewById(buttonId)).setChecked(checked);
    }

    /**
     * @return The selected gender from the UI
     */
    private Gender getSelectedGender() {
        RadioGroup genderRadioGroup = (RadioGroup) getActivity().findViewById(R.id.genderRadioGroup);
        switch (genderRadioGroup.getCheckedRadioButtonId()) {
        case R.id.femaleRadioButton:
            return Gender.FEMALE;
        case R.id.maleRadioButton:
            return Gender.MALE;
        default:
            LOG.e("Gender was not selected, so we default to MALE");
            return Gender.MALE;
        }
    }

    private Handedness getSelectedHandedness() {
        RadioGroup genderRadioGroup = (RadioGroup) getActivity().findViewById(R.id.handedRadioGroup);
        switch (genderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.leftRadioButton:
                return Handedness.LEFT;
            case R.id.rightRadioButton:
                return Handedness.RIGHT;
            default:
                LOG.e("Handedness was not selected, so we default to UNKNOWN");
                return Handedness.UNKNOWN;
        }
    }

    /**
     * Setups armband configuration.
     * @param armbandConfiguration
     */
    private void configureArmband(final ArmbandConfiguration armbandConfiguration){
        SenseWearApplication.get().getArmband().configureArmband(armbandConfiguration)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArmbandConfiguration>() {

                    @Override
                    public void onCompleted() {
                        UIUtils.showToast(getActivity(), "Configuration updated");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOG.e("Error trying to update armband configuration:" + e.getMessage());
                    }

                    @Override
                    public void onNext(ArmbandConfiguration data) {
                        updateConfigurationView(data);
                    }

                });
    }

    private void setFieldError(final int fieldId, final String message) {
        ((EditText) getActivity().findViewById(fieldId)).setError(message);
    }

    protected void handleValidationErrors(List<ValidationError> validationErrors) {
        for (ValidationError error : validationErrors) {
            final int fieldId = getFieldId(error);
            if (fieldId != -1) {
                setFieldError(fieldId, error.getMessage());
            } else {
                LOG.w("Error caught for a field that's not mapped to an EditText: " + error.getField());
            }
        }
    }

}
