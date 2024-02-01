package com.sy.im.ui.view.person

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.im.logic.SimAPI
import com.sy.im.model.Person
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ContextProvider.context
import com.sy.im.ui.view.person.update.ProfileUpdateActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 个人信息界面
 */
class PersonViewModel : ViewModel() {

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    var personViewState by mutableStateOf(
        value = PersonViewState(
            personProfile = Person(),
            previewImage = ::previewImage,
            navToUpdate = ::navToUpdate,
            logout = ::logout,
        )
    )
        private set

    init {
        viewModelScope.launch {
            SimAPI.personProfile.collect {
                personViewState = personViewState.copy(personProfile = it) // 账号信息
            }
        }
    }


    private fun previewImage(imageUrl: String) {
        println("previewImage")
        if (imageUrl.isNotBlank()) {
//            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }


    private fun navToUpdate() {
        val intent = Intent(context, ProfileUpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun logout() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            if (SimAPI.loginLogic.logout()){
//                    ComposeChat.conversationProvider.clear()
//                    ComposeChat.groupProvider.clear()
//                    ComposeChat.friendshipProvider.clear()
                AccountProvider.onUserLogout()
            }
            loadingDialog(visible = false)
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }


}