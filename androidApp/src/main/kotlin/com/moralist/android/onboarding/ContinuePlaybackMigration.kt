package com.moralist.android.onboarding

internal fun shouldApplyTvContinuePlaybackDefault(
    isTv: Boolean,
    hasLegacyInstallation: Boolean,
    onboardingComplete: Boolean,
    migrationApplied: Boolean,
): Boolean = isTv && !hasLegacyInstallation && !onboardingComplete && !migrationApplied
