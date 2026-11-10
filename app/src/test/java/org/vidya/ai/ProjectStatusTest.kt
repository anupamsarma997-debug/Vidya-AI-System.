package org.vidya.ai

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectStatusTest {
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
}
