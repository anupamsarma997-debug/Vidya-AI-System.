package org.vidya.ai

import org.json.JSONArray
import org.json.JSONObject

/** Parses source-traceable machine knowledge; parsing does not establish technical truth. */
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
        val verification = item.optJSONObject("verification")
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
                    attributionRequired = source.optionalBoolean("attribution_required", false)
                ))
            }
        }

        val status = when (verification.requiredString("status")) {
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
                variant = machine.optionalString("variant"),
                modelYear = machine.optionalYear("model_year"),
                serialRange = machine.optionalString("serial_range")
            ),
            part = PartIdentity(
                partId = part.requiredString("part_id"),
                name = part.requiredString("name"),
                function = part.requiredString("function"),
                oemPartNumber = part.optionalString("oem_part_number"),
                locationDescription = part.optionalString("location_description"),
                compatibleVariants = part.optionalStringArray("compatible_variants")
            ),
            sources = sources,
            verification = status,
            symptoms = item.optionalObjectArray("symptoms") { symptom ->
                SymptomReference(
                    symptomId = symptom.requiredString("symptom_id"),
                    description = symptom.requiredString("description"),
                    possibleCauses = symptom.optionalStringArray("possible_causes")
                )
            },
            errorCodes = item.optionalObjectArray("error_codes") { error ->
                ErrorCodeReference(
                    code = error.requiredString("code"),
                    meaning = error.requiredString("meaning"),
                    machineScope = error.requiredString("machine_scope")
                )
            },
            hazards = item.optionalStringArray("hazards").map { value ->
                when (value) {
                    "high_voltage" -> HazardCategory.HIGH_VOLTAGE
                    "fuel_or_gas" -> HazardCategory.FUEL_OR_GAS
                    "pressurized_system" -> HazardCategory.PRESSURIZED_SYSTEM
                    "suspended_load" -> HazardCategory.SUSPENDED_LOAD
                    "moving_machinery" -> HazardCategory.MOVING_MACHINERY
                    "hot_surface" -> HazardCategory.HOT_SURFACE
                    "chemical_exposure" -> HazardCategory.CHEMICAL_EXPOSURE
                    "unknown" -> HazardCategory.UNKNOWN
                    else -> throw IllegalArgumentException("records[$index].hazards contains unknown value: $value")
                }
            }
        )
    }

    private fun JSONObject.requiredString(key: String): String {
        require(has(key) && !isNull(key)) { "$key is required" }
        val raw = get(key)
        require(raw is String && raw.isNotBlank()) { "$key must be a non-empty string" }
        return raw.trim()
    }

    private fun JSONObject.optionalString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        val raw = get(key)
        require(raw is String) { "$key must be a string or null" }
        return raw.trim().takeIf { it.isNotEmpty() }
    }

    private fun JSONObject.optionalBoolean(key: String, default: Boolean): Boolean {
        if (!has(key) || isNull(key)) return default
        val raw = get(key)
        require(raw is Boolean) { "$key must be a boolean" }
        return raw
    }

    private fun JSONObject.optionalYear(key: String): Int? {
        if (!has(key) || isNull(key)) return null
        val raw = get(key)
        require(raw is Number) { "$key must be an integer or null" }
        val number = raw.toDouble()
        require(number.isFinite() && number % 1.0 == 0.0 &&
            number >= Int.MIN_VALUE && number <= Int.MAX_VALUE) {
            "$key must be an integer or null"
        }
        return number.toInt()
    }

    private fun JSONObject.optionalStringArray(key: String): List<String> {
        if (!has(key) || isNull(key)) return emptyList()
        val array = optJSONArray(key)
            ?: throw IllegalArgumentException("$key must be an array")
        return buildList {
            for (i in 0 until array.length()) {
                val value = array.get(i)
                require(value is String && value.isNotBlank()) { "$key[$i] must be a non-empty string" }
                add(value.trim())
            }
        }
    }

    private fun <T> JSONObject.optionalObjectArray(key: String, map: (JSONObject) -> T): List<T> {
        if (!has(key) || isNull(key)) return emptyList()
        val array = optJSONArray(key)
            ?: throw IllegalArgumentException("$key must be an array")
        return buildList {
            for (i in 0 until array.length()) {
                val value = array.optJSONObject(i)
                    ?: throw IllegalArgumentException("$key[$i] must be an object")
                add(map(value))
            }
        }
    }
}

/** Reads the catalogue asset shipped inside the APK and validates its structure. */
class MachinePartAssetRepository(private val readAsset: (String) -> String) {
    fun load(): List<MachinePartRecord> {
        val parsed = MachinePartJsonLoader.parse(readAsset("machine_parts.json"))
        val errors = parsed.flatMapIndexed { index, record ->
            MachinePartRecordValidator.validate(record).map { "records[$index]: $it" }
        }
        require(errors.isEmpty()) { errors.joinToString("; ") }
        val duplicateIds = parsed.groupBy { it.recordId }.filterValues { it.size > 1 }.keys
        require(duplicateIds.isEmpty()) { "duplicate record_id values: ${duplicateIds.joinToString()}" }
        return parsed
    }
}
