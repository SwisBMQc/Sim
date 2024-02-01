package com.sy.im.logic

import com.sy.im.model.Person
import com.sy.im.model.ServerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object SimAPI {

    val personProfile = MutableStateFlow(value = Person())

    val serverConnectState = MutableSharedFlow<ServerState>()

    val loginLogic = LoginLogic()
    val mainLogic = MainLogic()
    val friendLogic = FriendLogic()
    val messageLogic = MessageLogic()

}