package com.sy.im.ui.view.person.update

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.sy.im.MainActivity
import com.sy.im.logic.SimAPI
import com.sy.im.model.Person
import com.sy.im.ui.theme.SimandroidTheme
import com.sy.im.ui.widgets.ImageUploadButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 更新信息界面
 *@Author：sy
 *@Date：2023/11/21
 */
class ProfileUpdateActivity : ComponentActivity() {

    lateinit var personProfile : Person

    init {
        lifecycleScope.launch {
            SimAPI.personProfile.collect {
                personProfile = it  // 初始化
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SimandroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileUpdatePage(
                        personProfile = personProfile,
                        onSubmit = {
                            onSubmit(it)
                        })
                }
            }
        }
    }

    private fun onSubmit(personProfile: Person){
        lifecycleScope.launch{
            SimAPI.mainLogic.updatePersonProfile(personProfile)
            navToMainPage()
            finish()
        }
    }

    private fun navToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

@Composable
private fun ProfileUpdatePage(personProfile: Person, onSubmit:(personProfile: Person)->Unit) {

    var person = personProfile

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
        ImageUploadButton(person.imgUrl) {
            person.imgUrl = it.ifBlank { person.imgUrl }
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
            person.gender = gender
            person.nickname = nickname
            person.signature = signature

            onSubmit(person)
        }) {
            Text("提交")
        }

    }
}