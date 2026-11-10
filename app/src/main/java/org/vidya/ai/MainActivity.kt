package org.vidya.ai

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/** Initial Vidya shell with a local, reviewed-reference search screen. */
class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var results: TextView
    private var records: List<MachinePartRecord> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 36, 24, 24)
        }
        content.addView(text("VIDYA", 30f, true))
        content.addView(text("Offline machine knowledge assistant", 18f, true))
        content.addView(text("Local reference search (reviewed records only)", 14f, false))
        status = text("Loading local catalogue…", 14f, false)
        content.addView(status)

        val query = EditText(this).apply {
            hint = "Search machine, model, part name or ID"
            setSingleLine(true)
        }
        content.addView(query, matchWrap())
        content.addView(Button(this).apply {
            text = "Search catalogue"
            setOnClickListener { showSearchResults(query.text.toString()) }
        }, matchWrap())
        results = text("No search performed yet.", 14f, false)
        content.addView(results)

        content.addView(section("Development status"))
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
        loadCatalogue()
    }

    private fun loadCatalogue() {
        try {
            records = MachinePartAssetRepository { fileName ->
                assets.open(fileName).bufferedReader().use { it.readText() }
            }.load()
            status.text = "Local catalogue loaded: ${records.size} records. Only reviewed records can appear."
            if (records.isEmpty()) {
                results.text = "Catalogue is empty. No machine records have been added or verified yet."
            }
        } catch (error: Exception) {
            records = emptyList()
            status.text = "Catalogue could not be loaded. Search is unavailable."
            results.text = "Data error: ${error.message ?: "unknown error"}"
        }
    }

    private fun showSearchResults(query: String) {
        val matches = MachinePartCatalog.search(query, records)
        results.text = when {
            query.isBlank() -> "Enter a machine, model, part name or identifier."
            matches.isEmpty() -> "No reviewed reference records found. Do not treat this as proof that a part does not exist."
            else -> matches.joinToString("\n\n") { record ->
                "${record.machine.manufacturer} ${record.machine.model}\n" +
                    "${record.part.name} (${record.part.partId})\n" +
                    record.part.function
            }
        }
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

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
