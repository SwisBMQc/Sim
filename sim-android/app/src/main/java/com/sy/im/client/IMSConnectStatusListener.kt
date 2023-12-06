package com.sy.im.client

import android.util.Log
import com.sy.im.interf.IMSConnectStatusCallback
import com.sy.im.logic.SimAPI
import com.sy.im.model.ServerState

class IMSConnectStatusListener : IMSConnectStatusCallback{
    override fun onConnecting() {
        SimAPI.loginLogic.dispatchServerState(serverState = ServerState.Connecting)
    }

    override fun onConnected() {
        SimAPI.loginLogic.dispatchServerState(serverState = ServerState.ConnectSuccess)
    }

    override fun onConnectFailed() {
        SimAPI.loginLogic.dispatchServerState(serverState = ServerState.ConnectFailed)
    }

    override fun onLoginAuthFailed() {
        SimAPI.loginLogic.dispatchServerState(serverState = ServerState.KickedOffline)
    }

}