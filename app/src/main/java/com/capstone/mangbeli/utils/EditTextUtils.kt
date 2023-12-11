package com.capstone.mangbeli.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

object EditTextUtils {

    fun setupTextWatcher(vararg editTexts: EditText, onChanged: () -> Unit) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                onChanged.invoke()
            }

            override fun afterTextChanged(editable: Editable?) {}
        }

        editTexts.forEach { it.addTextChangedListener(textWatcher) }
    }

}

object ButtonUtils {

    fun enableButtonIfEdited(button: Button, isEdited: Boolean) {
        button.isEnabled = isEdited
    }

}