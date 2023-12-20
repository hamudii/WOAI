package com.bangkit.woai.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.bangkit.woai.R

class MyPasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = when (id) {
            R.id.ed_register_password -> context.getString(R.string.ed_password_hint)
            R.id.ed_login_password -> context.getString(R.string.ed_password_hint)
            else -> ""
        }
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    id == R.id.ed_login_password && s.toString().length < 8 -> {
                        setError("Password must be at least 8 characters long.", null)
                    }

                    id == R.id.ed_register_password && s.toString().length < 8 -> {
                        setError("Password must be at least 8 characters long.", null)
                    }

                    else -> error = null
                }
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
}