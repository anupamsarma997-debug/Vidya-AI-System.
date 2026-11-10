package org.vidya.ai

import org.json.JSONArray
import org.json.JSONObject

/**
 * Converts the documented snake_case JSON record format into domain records.
 * Parsing/structural validation is not proof that the source or technical content is true.
 */
object MachinePartJsonLoader {
    fun parse(json: String): List<MachinePartRecord> {
        val root = JSONArray(json)
        return buildList {
            for (index in 0 until root.length()) {
                val item = root.optJSONObject(index)
                    ?: throw IllegalArgumentException("records[$index] must be an object")
                add(parseRecord(item, index))
            }
        }
    }

    private fun parseRecord(item: JSONObject, index: Int): MachinePartRecord {
        val machine = item.optJSONObject("machine")
            ?: throw IllegalArgumentException("records[$index].machine is required")
        val part = item.optJSONObject("part")
            ?: throw IllegalArgumentException("records[$index].part is required")
        val sourceArray = item.optJSONArray("sources")
            ?: throw IllegalArgumentException("records[$index].sources is required")
        val verificationObject = item.optJSONObject("verification")
            ?: throw IllegalArgumentException("records[$index].verification is required")

        val sources = buildList {
            for (i in 0 until sourceArray.length()) {
                val source = sourceArray.optJSONObject(i)
                    ?: throw IllegalArgumentException("records[$index].sources[$i] must be an object")
                add(KnowledgeSource(
                    sourceType = source.requiredString("source_type"),
                    title = source.requiredString("title"),
                    urlOrIdentifier = source.requiredString("url_or_identifier"),
                    license = source.requiredString("license"),
                    attributionRequired = source.optBoolean("attribution_required", false)
                ))
            }
        }

        val year = if (machine.isNull("model_year")) null else machine.optInt("model_year")
        val status = when (verificationObject.requiredString("status")) {
            "unreviewed" -> VerificationStatus.UNREVIEWED
            "source_checked" -> VerificationStatus.SOURCE_CHECKED
            "technician_reviewed" -> VerificationStatus.TECHNICIAN_REVIEWED
            "manufacturer_confirmed" -> VerificationStatus.MANUFACTURER_CONFIRMED
            "rejected" -> VerificationStatus.REJECTED
            else -> throw IllegalArgumentException("records[$index].verification.status is unknown")
        }

        return MachinePartRecord(
            recordId = item.requiredString("record_id"),
            machine = MachineIdentity(
                category = machine.requiredString("category"),
                manufacturer = machine.requiredString("manufacturer"),
                model = machine.requiredString("model"),
                variant = machine.nullableString("variant"),
                modelYear = year
            ),
            part = PartIdentity(
                partId = part.requiredString("part_id"),
                name = part.requiredString("name"),
                function = part.requiredString("function"),
                oemPartNumber = part.nullableString("oem_part_number")
            ),
            sources = sources,
            verification = status
        )
    }

    private fun JSONObject.requiredString(key: String): String {
        val value = optString(key, "").trim()
        if (value.isEmpty()) throw IllegalArgumentException("$key is required")
        return value
    }

    private fun JSONObject.nullableString(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
}

/** Reads the versioned catalogue asset shipped inside the APK. */
class MachinePartAssetRepository(private val readAsset: (String) -> String) {
    fun load(): List<MachinePartRecord> {
        val parsed = MachinePartJsonLoader.parse(readAsset("machine_parts.json"))
        val errors = parsed.flatMapIndexed { index, record ->
            MachinePartRecordValidator.validate(record).map { "records[$index]: $it" }
        }
        require(errors.isEmpty()) { errors.joinToString("; ") }
        return parsed
    }
}
