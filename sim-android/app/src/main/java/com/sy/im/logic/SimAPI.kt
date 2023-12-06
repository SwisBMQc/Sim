package com.sy.im.logic

import com.sy.im.model.Person
import com.sy.im.model.ServerState
import com.sy.im.util.FillEmptyUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object SimAPI {

    val personProfile = MutableStateFlow(value = FillEmptyUtil.setEmpty(Person()))

    val serverConnectState = MutableSharedFlow<ServerState>()

    val loginLogic: LoginLogic = LoginLogic()
    val mainLogic: MainLogic = MainLogic()

}