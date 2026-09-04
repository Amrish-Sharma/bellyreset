package com.codebuzz.app.bellyreset.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface BellyResetNavKey : NavKey {
    @Serializable
    data object Today : BellyResetNavKey
    
    @Serializable
    data object Progress : BellyResetNavKey
    
    @Serializable
    data object Plan : BellyResetNavKey
    
    @Serializable
    data object Settings : BellyResetNavKey
}
