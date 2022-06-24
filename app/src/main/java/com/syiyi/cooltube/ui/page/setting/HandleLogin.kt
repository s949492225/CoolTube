package com.syiyi.cooltube.ui.page.setting

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.syiyi.cooltube.R

@Composable
fun HandleLogin(
    show: Boolean,
    onLogin: (String, String) -> Unit,
    onCancel: () -> Unit,
) {

    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }


    if (show) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { onLogin(userName, password) }) {
                    Text(text = stringResource(id = R.string.login))
                }
            },
            dismissButton = {
                TextButton(onClick = { onCancel() })
                { Text(text = stringResource(id = R.string.cancel)) }
            },
            title = {
                Text(text = "login")
            },
            text = {
                Column {
                    // Username
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userName,
                        onValueChange = { userName = it.trim().replace("\n", "") },
                        label = {
                            Text(
                                text = stringResource(R.string.username)
                            )
                        },
                        singleLine = true
                    )

                    // Password
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it.replace("\n", "").trim() },
                        label = {
                            Text(
                                text = stringResource(R.string.password)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            Crossfade(targetState = showPassword) {
                                IconButton(onClick = {
                                    showPassword = !showPassword
                                }) {
                                    Icon(
                                        if (it) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        null
                                    )
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}