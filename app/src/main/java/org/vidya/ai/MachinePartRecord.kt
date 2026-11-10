package org.vidya.ai

/** Dependency-free, source-traceable machine knowledge. Structural validity is not technical verification. */
data class MachineIdentity(
    val category: String,
    val manufacturer: String,
    val model: String,
    val variant: String? = null,
    val modelYear: Int? = null,
    val serialRange: String? = null
)

data class PartIdentity(
    val partId: String,
    val name: String,
    val function: String,
    val oemPartNumber: String? = null,
    val locationDescription: String? = null,
    val compatibleVariants: List<String> = emptyList()
)

data class SymptomReference(
    val symptomId: String,
    val description: String,
    val possibleCauses: List<String> = emptyList()
)

data class ErrorCodeReference(
    val code: String,
    val meaning: String,
    val machineScope: String
)

data class KnowledgeSource(
    val sourceType: String,
    val title: String,
    val urlOrIdentifier: String,
    val license: String,
    val attributionRequired: Boolean
)

enum class VerificationStatus {
    UNREVIEWED, SOURCE_CHECKED, TECHNICIAN_REVIEWED, MANUFACTURER_CONFIRMED, REJECTED
}

data class MachinePartRecord(
    val recordId: String,
    val machine: MachineIdentity,
    val part: PartIdentity,
    val sources: List<KnowledgeSource>,
    val verification: VerificationStatus,
    val symptoms: List<SymptomReference> = emptyList(),
    val errorCodes: List<ErrorCodeReference> = emptyList(),
    val hazards: List<HazardCategory> = emptyList()
)

object MachinePartRecordValidator {
    fun validate(record: MachinePartRecord): List<String> = buildList {
        if (record.recordId.isBlank()) add("recordId is required")
        if (record.machine.category.isBlank()) add("machine.category is required")
        if (record.machine.manufacturer.isBlank()) add("machine.manufacturer is required")
        if (record.machine.model.isBlank()) add("machine.model is required")
        if (record.machine.modelYear != null && record.machine.modelYear !in 1900..2100) {
            add("machine.modelYear must be between 1900 and 2100")
        }
        if (record.part.partId.isBlank()) add("part.partId is required")
        if (record.part.name.isBlank()) add("part.name is required")
        if (record.part.function.isBlank()) add("part.function is required")
        if (record.sources.isEmpty()) add("at least one source is required")
        record.sources.forEachIndexed { index, source ->
            if (source.sourceType.isBlank()) add("sources[$index].sourceType is required")
            if (source.title.isBlank()) add("sources[$index].title is required")
            if (source.urlOrIdentifier.isBlank()) add("sources[$index].urlOrIdentifier is required")
            if (source.license.isBlank()) add("sources[$index].license is required")
        }
        record.symptoms.forEachIndexed { index, symptom ->
            if (symptom.symptomId.isBlank()) add("symptoms[$index].symptomId is required")
            if (symptom.description.isBlank()) add("symptoms[$index].description is required")
        }
        record.errorCodes.forEachIndexed { index, error ->
            if (error.code.isBlank()) add("errorCodes[$index].code is required")
            if (error.meaning.isBlank()) add("errorCodes[$index].meaning is required")
            if (error.machineScope.isBlank()) add("errorCodes[$index].machineScope is required")
        }
    }

    fun mayDisplayAsReviewedReference(record: MachinePartRecord): Boolean =
        validate(record).isEmpty() &&
            record.verification in setOf(
                VerificationStatus.TECHNICIAN_REVIEWED,
                VerificationStatus.MANUFACTURER_CONFIRMED
            )
}
