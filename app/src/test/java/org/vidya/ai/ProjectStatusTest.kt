package org.vidya.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectStatusTest {
    private fun validRecord(
        status: VerificationStatus = VerificationStatus.UNREVIEWED
    ) = MachinePartRecord(
        recordId = "record-001",
        machine = MachineIdentity("pump", "Example Manufacturer", "Model X"),
        part = PartIdentity("part-001", "Impeller", "Moves fluid through the pump"),
        sources = listOf(
            KnowledgeSource(
                "manufacturer_manual",
                "Service manual",
                "manual-id-001",
                "Permission required / verify before use",
                false
            )
        ),
        verification = status
    )

    @Test
    fun noHazardDoesNotTriggerTechnicianGate() {
        assertFalse(SafetyGate.requiresQualifiedTechnician(HazardCategory.NONE))
    }

    @Test
    fun everyKnownHazardAndUnknownStateTriggersTechnicianGate() {
        HazardCategory.values()
            .filter { it != HazardCategory.NONE }
            .forEach { hazard ->
                assertTrue("$hazard must stop automated repair guidance",
                    SafetyGate.requiresQualifiedTechnician(hazard))
            }
    }

    @Test
    fun completeRecordPassesStructuralValidation() {
        assertTrue(MachinePartRecordValidator.validate(validRecord()).isEmpty())
    }

    @Test
    fun missingSourceAndBlankPartAreRejected() {
        val invalid = validRecord().copy(
            part = PartIdentity("", " ", " "),
            sources = emptyList()
        )
        val errors = MachinePartRecordValidator.validate(invalid)
        assertTrue(errors.contains("part.partId is required"))
        assertTrue(errors.contains("part.name is required"))
        assertTrue(errors.contains("at least one source is required"))
    }

    @Test
    fun onlyTechnicianOrManufacturerReviewedRecordsMayBeDisplayedAsReviewed() {
        assertFalse(MachinePartRecordValidator.mayDisplayAsReviewedReference(validRecord()))
        assertTrue(MachinePartRecordValidator.mayDisplayAsReviewedReference(
            validRecord(VerificationStatus.TECHNICIAN_REVIEWED)
        ))
        assertTrue(MachinePartRecordValidator.mayDisplayAsReviewedReference(
            validRecord(VerificationStatus.MANUFACTURER_CONFIRMED)
        ))
        assertFalse(MachinePartRecordValidator.mayDisplayAsReviewedReference(
            validRecord(VerificationStatus.REJECTED)
        ))
    }

    @Test
    fun invalidModelYearIsRejected() {
        val invalid = validRecord().copy(
            machine = validRecord().machine.copy(modelYear = 1800)
        )
        assertEquals(
            listOf("machine.modelYear must be between 1900 and 2100"),
            MachinePartRecordValidator.validate(invalid)
        )
    }

    @Test
    fun catalogueFindsReviewedPartCaseInsensitively() {
        val reviewed = validRecord(VerificationStatus.TECHNICIAN_REVIEWED)
        assertEquals(listOf(reviewed), MachinePartCatalog.search("IMPELLER", listOf(reviewed)))
    }

    @Test
    fun catalogueExcludesUnreviewedAndInvalidRecords() {
        val reviewed = validRecord(VerificationStatus.MANUFACTURER_CONFIRMED)
        val unreviewed = validRecord()
        val invalid = reviewed.copy(sources = emptyList())
        assertEquals(listOf(reviewed), MachinePartCatalog.search("pump", listOf(
            unreviewed, invalid, reviewed
        )))
    }

    @Test
    fun catalogueMatchesMachineAndPartIdentifiers() {
        val reviewed = validRecord(VerificationStatus.TECHNICIAN_REVIEWED)
        assertEquals(listOf(reviewed), MachinePartCatalog.search("model x", listOf(reviewed)))
        assertEquals(listOf(reviewed), MachinePartCatalog.search("part-001", listOf(reviewed)))
    }

    @Test
    fun catalogueReturnsNoResultsForBlankOrMissingQuery() {
        val reviewed = validRecord(VerificationStatus.TECHNICIAN_REVIEWED)
        assertTrue(MachinePartCatalog.search("  ", listOf(reviewed)).isEmpty())
        assertTrue(MachinePartCatalog.search("compressor", listOf(reviewed)).isEmpty())
    }
}
