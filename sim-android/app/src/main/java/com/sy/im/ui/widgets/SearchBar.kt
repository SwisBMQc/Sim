package com.sy.im.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(hint:String,isNumericValue:Boolean = false, onSearch: (String) -> Unit,) {

    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(26.dp)){
        TextField(
            value = query,
            onValueChange = { newText ->
                val numericValue = newText.filter { it.isDigit() }
                query = if (isNumericValue) numericValue else newText
                onSearch(query)
            },
            placeholder = { Text(hint) },
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp),
            maxLines = 1,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = hint,
                    tint = if (isFocused) Color.Blue else Color.Gray
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { query = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(query) }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFFF8F8F8),
                textColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

}
