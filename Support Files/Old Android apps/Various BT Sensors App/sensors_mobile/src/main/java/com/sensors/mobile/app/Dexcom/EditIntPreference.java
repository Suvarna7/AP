package com.sensors.mobile.app.Dexcom;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

// The normal EditTextPreference saves data as String, this class is to save the data as an integer
// Used in preferences to set the ReceiverUpdateService alarm interval
public class EditIntPreference extends EditTextPreference
{

    public EditIntPreference(
            Context context)
    {
        super(context);
    }

    public EditIntPreference(
            Context context,
            AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EditIntPreference(
            Context context,
            AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(
        String returnValue)
    {
        return String.valueOf(getPersistedInt(-1));
    }

    @Override
    protected boolean persistString(
        String value)
    {
        return persistInt(Integer.valueOf(value));
    }
}
