package com.bangkit.woai.views

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurUtils {

    fun blurBitmap(context: Context, inputBitmap: Bitmap, blurRadius: Float): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setInput(input)
        script.setRadius(blurRadius)
        script.forEach(output)

        output.copyTo(inputBitmap)

        rs.destroy()
        return inputBitmap
    }
}
