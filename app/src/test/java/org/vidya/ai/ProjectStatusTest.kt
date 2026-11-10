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

    private fun validJson(status: String = "technician_reviewed") = """
        [{
          "record_id":"pump-x-impeller",
          "machine":{"category":"pump","manufacturer":"Example Co","model":"X1","variant":null,"model_year":2024},
          "part":{"part_id":"P-01","name":"Impeller","function":"Moves fluid","oem_part_number":"OEM-1"},
          "sources":[{"source_type":"manufacturer_manual","title":"Manual","url_or_identifier":"manual-1","license":"permission required","attribution_required":true}],
          "verification":{"status":"$status","reviewed_by":null,"reviewed_date":null}
        }]
    """.trimIndent()

    @Test
    fun jsonLoaderMapsSchemaFieldsIntoDomainRecord() {
        val loaded = MachinePartJsonLoader.parse(validJson())
        assertEquals(1, loaded.size)
        assertEquals("Example Co", loaded[0].machine.manufacturer)
        assertEquals("Impeller", loaded[0].part.name)
        assertEquals(VerificationStatus.TECHNICIAN_REVIEWED, loaded[0].verification)
    }

    @Test
    fun jsonLoaderRejectsUnknownVerificationStatus() {
        try {
            MachinePartJsonLoader.parse(validJson("trusted_by_internet"))
            throw AssertionError("Unknown verification status should fail")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message!!.contains("verification.status"))
        }
    }

    @Test
    fun assetRepositoryLoadsEmptyStarterCatalogueWithoutInventingRecords() {
        val repository = MachinePartAssetRepository { name ->
            assertEquals("machine_parts.json", name)
            "[]"
        }
        assertTrue(repository.load().isEmpty())
    }

    @Test
    fun assetRepositoryRejectsStructurallyInvalidRecords() {
        val repository = MachinePartAssetRepository { validJson().replace("\"sources\":[{", "\"sources\":[]") }
        try {
            repository.load()
            throw AssertionError("Invalid source-less record should fail")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message!!.contains("at least one source"))
        }
    }
}
