package org.vidya.ai

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/** Initial Vidya Android shell. Recognition and repair workflows are not enabled yet. */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 36, 24, 24)
        }
        content.addView(text("VIDYA", 30f, true))
        content.addView(text("AI-Driven Multimodal Guidance", 18f, true))
        content.addView(text("Offline-first machine assistance", 14f, false))
        content.addView(section("Development status"))
        content.addView(text("App foundation loaded successfully.", 16f, false))
        content.addView(text("Camera recognition: not implemented", 14f, false))
        content.addView(text("Trained AI model: not available", 14f, false))
        content.addView(text("Repair instructions are disabled until verified sources and safety checks are ready.", 14f, false))
        content.addView(section("Planned workflow"))
        content.addView(text("1. Select machine and exact model", 15f, false))
        content.addView(text("2. Capture a clear component image", 15f, false))
        content.addView(text("3. Review candidate identification and confidence", 15f, false))
        content.addView(text("4. Check compatible documentation and safety warnings", 15f, false))
        val scroll = ScrollView(this).apply {
            addView(content, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        setContentView(scroll)
    }

    private fun section(value: String) = text(value, 19f, true).apply {
        setPadding(0, 28, 0, 8)
    }

    private fun text(value: String, size: Float, bold: Boolean) =
        TextView(this).apply {
            text = value
            textSize = size
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
}
