package com.sy.im.ui.view.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.im.R
import com.sy.im.ui.widgets.LoadingDialog

@Composable
fun LoginPage(viewState: LoginViewState,
                      onClickRegisterButton:(String, String) -> Unit,
                      onClickLoginButton: (String, String) -> Unit) {

    BackHandler(enabled = true) {
        println("login page")
    }  // 防止返回退出

    var userId by remember { mutableStateOf(viewState.lastLoginUserId) }
    var password by remember { mutableStateOf("") }

    var isRegister by remember { mutableStateOf(false) }
    var textButton by remember { mutableStateOf("") }

    textButton = if (!isRegister){ "注册" } else{ "登录" }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp, 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewState.showPanel) {
                Image(painter = painterResource(id = R.drawable.sim_cover),
                    contentDescription = "cover")
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.19f)
                        .wrapContentSize(align = Alignment.BottomCenter),
                    fontSize = 60.sp,
                    fontFamily = FontFamily.Cursive,
                    textAlign = TextAlign.Center
                )
                if (isRegister){
                    Text(text = "userId建议：小写字母 <= 7个字符，不可修改", color = Color.Magenta)
                }
                TextField(
                    value = userId,
                    onValueChange = { userId = it.trim() },
                    label = { Text("userId") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it.trim() },
                    label = { Text("password") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    visualTransformation = PasswordVisualTransformation()
                )
                // 点击注册，添加输入框
                if (isRegister){
                    Spacer(modifier = Modifier.height(16.dp))
                    var password2 by remember { mutableStateOf("") }    // 注册时重新输入

                    TextField(
                        value = password2,
                        onValueChange = { password2 = it.trim() },
                        label = { Text("retype password") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (password != password2) {
                        Text(
                            text = "Passwords do not match",
                            color = Color.Red
                        )
                    }
                    // 注册按钮
                    Button(
                        enabled = userId.isNotEmpty() && password.isNotEmpty() && password == password2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        onClick = { onClickRegisterButton(userId, password) }
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 2.dp),
                            text = "注册",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
                // 登录按钮
                if (!isRegister){
                    Button(
                        enabled = userId.isNotEmpty() && password.isNotEmpty(),
                        onClick = { onClickLoginButton(userId, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Blue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 2.dp),
                            text = "登录",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
                TextButton(
                    onClick = { isRegister = !isRegister })
                {
                    Text(text = textButton)
                }
            }
        }
        LoadingDialog(visible = viewState.loading)
    }

}