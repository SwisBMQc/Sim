package com.sy.im.ui.view.person

import androidx.compose.runtime.Stable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import com.sy.im.model.Person

@Stable
data class PersonViewState(
    val personProfile: Person,
    val previewImage: (String) -> Unit,
    val updateProfile: ()-> Unit,
    val logout:()-> Unit
)