package io.ikws4.weiju.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SeekBarPreference

class SeekBarPreference(context: Context, attributeSet: AttributeSet) : SeekBarPreference(context, attributeSet) {

    override fun persistInt(value: Int): Boolean {
        notifyChanged()
        return super.persistInt(value)
    }

    override fun getSummary(): CharSequence {
        return String.format(super.getSummary().toString(), value)
    }
}