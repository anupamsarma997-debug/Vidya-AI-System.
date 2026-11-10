package org.vidya.ai

/**
 * Small in-memory catalogue for offline lookup.
 * Search exposes only complete records reviewed by a technician or confirmed by a manufacturer.
 */
object MachinePartCatalog {
    fun search(query: String, records: List<MachinePartRecord>): List<MachinePartRecord> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) return emptyList()

        return records.asSequence()
            .filter(MachinePartRecordValidator::mayDisplayAsReviewedReference)
            .filter { record ->
                searchableText(record).any { field ->
                    field.lowercase().contains(normalizedQuery)
                }
            }
            .distinctBy { it.recordId }
            .toList()
    }

    private fun searchableText(record: MachinePartRecord): List<String> = listOfNotNull(
        record.recordId, record.machine.category, record.machine.manufacturer,
        record.machine.model, record.machine.variant, record.machine.modelYear?.toString(),
        record.part.partId, record.part.name, record.part.function, record.part.oemPartNumber,
        record.part.locationDescription,
        *record.part.compatibleVariants.toTypedArray(),
        *record.symptoms.flatMap { listOf(it.symptomId, it.description) + it.possibleCauses }.toTypedArray(),
        *record.errorCodes.flatMap { listOf(it.code, it.meaning, it.machineScope) }.toTypedArray(),
        *record.hazards.map { it.name }.toTypedArray()
    )
}
