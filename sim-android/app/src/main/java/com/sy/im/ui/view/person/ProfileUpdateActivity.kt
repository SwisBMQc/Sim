package com.sy.im.ui.view.person

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.sy.im.R
import com.sy.im.logic.SimAPI
import com.sy.im.model.Person
import com.sy.im.ui.theme.SimandroidTheme
import kotlinx.coroutines.launch

/**
 * 更新信息界面
 *@Author：sy
 *@Date：2023/11/21
 */
class ProfileUpdateActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val personProfile: Person = Gson().fromJson(
            intent.extras?.get("personProfile").toString(),
            Person::class.java)

        setContent {
            SimandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileUpdatePage(person = personProfile, onSubmit = {
                        run {
                            onSubmit(it)
                        }
                    } )
                }
            }
        }
    }

    private fun onSubmit(personProfile: Person){
        lifecycleScope.launch{
            SimAPI.loginLogic.updatePersonProfile(personProfile)
            finish()
        }
    }

}

@Composable
private fun ProfileUpdatePage(person: Person, onSubmit:(person: Person) -> Unit,) {

    var nickname by remember { mutableStateOf(person.nickname) }
    var gender by remember { mutableStateOf(person.gender) }
    var signature by remember { mutableStateOf(person.signature) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = "profile picture",
            modifier = Modifier
                .height(120.dp)
                .clip(RectangleShape)
                .aspectRatio(1f) // 设置宽高比为1:1，即正方形
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            /* TODO 上传图片 */
        }) {
            Text("上传图片")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "userId: ${person.userId}")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("昵称") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("性别")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = gender == "男",
                onClick = { gender = "男" }
            )
            Text("男")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = gender == "女",
                onClick = { gender = "女" }
            )
            Text("女")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = signature,
            onValueChange = { signature = it },
            label = { Text("个性签名/介绍") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            var personSubmit = person
            personSubmit.gender = gender
            personSubmit.nickname = nickname
            personSubmit.signature = signature

            onSubmit(personSubmit)
        }) {
            Text("提交")
        }

    }
}