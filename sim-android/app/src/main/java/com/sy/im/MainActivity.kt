package com.sy.im

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.sy.im.client.IMSClientBootstrap
import com.sy.im.ui.theme.SimandroidTheme
import com.sy.im.ulit.IMSConfig.APP_STATUS_FOREGROUND

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}


@Composable
fun LoginScreen() {
    val userId = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userId.value,
            onValueChange = { userId.value = it },
            label = { Text("userId") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { performLogin(userId.value, password.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

fun performLogin(userId: String, password: String) {
    // 在此处执行登录逻辑
    Log.i("sim","username: $userId, password: $password")

    val token = "token_$userId";
    val hosts = "[{\"host\":\"192.168.200.1\", \"port\":9000}]"; // 注意不要写127.0.0.1
    IMSClientBootstrap.getInstance().init(userId,token,hosts,APP_STATUS_FOREGROUND) //应用在前台标识为0

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimandroidTheme {
        LoginScreen()
    }
}