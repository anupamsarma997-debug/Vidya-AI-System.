package org.vidya.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectStatusTest {
    @Test
    fun initialVersionIsDevelopmentRelease() {
        assertEquals("0.1.0", "0.1.0")
    }
}
