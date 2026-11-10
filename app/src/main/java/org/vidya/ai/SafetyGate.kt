package org.vidya.ai

/** Hazard categories that must stop automated repair guidance. */
enum class HazardCategory {
    NONE,
    HIGH_VOLTAGE,
    FUEL_OR_GAS,
    PRESSURIZED_SYSTEM,
    SUSPENDED_LOAD,
    MOVING_MACHINERY,
    HOT_SURFACE,
    CHEMICAL_EXPOSURE,
    UNKNOWN
}

/**
 * Conservative gate: only an explicit NONE assessment may pass.
 * This is a basic software safeguard, not a substitute for a risk assessment.
 */
object SafetyGate {
    fun requiresQualifiedTechnician(hazard: HazardCategory): Boolean =
        hazard != HazardCategory.NONE
}
