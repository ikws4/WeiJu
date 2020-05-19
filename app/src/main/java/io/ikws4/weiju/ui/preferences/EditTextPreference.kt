package io.ikws4.weiju.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference

class EditTextPreference(context: Context, attributeSet: AttributeSet) : EditTextPreference(context, attributeSet) {


    override fun setText(text: String?) {
        super.setText(text)
        notifyChanged()
    }

    override fun getSummary(): CharSequence {
        return String.format(super.getSummary().toString(), text)
    }
}