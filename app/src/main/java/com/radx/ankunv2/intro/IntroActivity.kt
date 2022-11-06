package com.radx.ankunv2.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.radx.ankunv2.MainActivity
import com.radx.ankunv2.Utils.toast
import com.radx.ankunv2.ui.theme.AnKunv2Theme
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.Transparent

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen()
        }
    }
}

@Composable
fun IntroNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = IntroMenus.SignUp.route
    ) {
        composable(route = IntroMenus.SignIn.route) { SignInScreen(navController) }
        composable(route = IntroMenus.SignUp.route) { SignUpScreen(navController) }
    }
}

@Composable
fun IntroScreen() {
    val navController = rememberNavController()
    IntroNavigationHost(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {
    val firebaseAuth = Firebase.auth
    val context = LocalContext.current

    AnKunv2Theme {
        Scaffold(
            content = { padding->
                Box(modifier = Modifier.padding(padding)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val email = remember { mutableStateOf(TextFieldValue()) }
                        val password = remember { mutableStateOf(TextFieldValue()) }

                        Text(
                            text = "AnKun",
                            fontSize = 60.sp,
                            fontFamily = FontFamily.Cursive,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 70.dp)
                        )

                        TextField(
                            label = { Text(text = "Email") },
                            value = email.value,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = Grey,
                                disabledTextColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent,
                                disabledIndicatorColor = Transparent
                            ),
                            onValueChange = { email.value = it }
                        )

                        TextField(
                            label = { Text(text = "Password") },
                            value = password.value,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = Grey,
                                disabledTextColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent,
                                disabledIndicatorColor = Transparent
                            ),
                            onValueChange = { password.value = it },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )

                        Button(
                            content = { Text (text = "Sign In", fontSize = 16.sp) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            enabled = !(email.value.text == "" || password.value.text == ""),
                            onClick = {
                                if (email.value.text == "" || password.value.text == "") {
                                    toast(context, "Invalid inputs.")
                                }
                                else {
                                    firebaseAuth.signInWithEmailAndPassword(email.value.text, password.value.text)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Sign in success
                                                // navigate into main app menu
                                                context.startActivity(
                                                    Intent(context, MainActivity::class.java)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                )
                                            }
                                            else {
                                                toast(context, task.exception!!.localizedMessage!!)
                                            }
                                        }
                                }
                            }
                        )

                        Text(
                            text = "Forgot Password?",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .padding(0.dp, 16.dp, 0.dp, 0.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                    onClick = { }
                                )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(PaddingValues(top = 60.dp))
                        ) {
                            Text(
                                text = "Don't have an account?",
                                fontSize = 16.sp,
                            )
                            Text(
                                text = "Sign Up",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                        onClick = {
                                            navController.navigate(IntroMenus.SignUp.route) {
                                                popUpTo(0) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    val firebaseAuth = Firebase.auth
    val context = LocalContext.current

    AnKunv2Theme {
        Scaffold(
            content = { padding->
                Box(modifier = Modifier.padding(padding)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val email = remember { mutableStateOf(TextFieldValue()) }
                        val password = remember { mutableStateOf(TextFieldValue()) }
                        val confPassword = remember { mutableStateOf(TextFieldValue()) }

                        Text(
                            text = "AnKun",
                            fontSize = 60.sp,
                            fontFamily = FontFamily.Cursive,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 70.dp)
                        )

                        TextField(
                            label = { Text(text = "Email") },
                            value = email.value,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = Grey,
                                disabledTextColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent,
                                disabledIndicatorColor = Transparent
                            ),
                            onValueChange = { email.value = it }
                        )

                        TextField(
                            label = { Text(text = "Password") },
                            value = password.value,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = Grey,
                                disabledTextColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent,
                                disabledIndicatorColor = Transparent
                            ),
                            onValueChange = { password.value = it },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )

                        TextField(
                            label = { Text(text = "Reenter Password") },
                            value = confPassword.value,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            isError = password.value.text != confPassword.value.text,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = Grey,
                                disabledTextColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent,
                                disabledIndicatorColor = Transparent
                            ),
                            onValueChange = { confPassword.value = it },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )

                        Button(
                            content = { Text (text = "Sign Up", fontSize = 16.sp) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            enabled = !(email.value.text == "" || (password.value.text == "" && confPassword.value.text == password.value.text)),
                            onClick = {
                                if (email.value.text == "" || password.value.text == "") {
                                    toast(context, "Invalid inputs.")
                                }
                                else {
                                    firebaseAuth.createUserWithEmailAndPassword(email.value.text, password.value.text)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Sign up success
                                                // navigate into main app menu
                                                context.startActivity(
                                                    Intent(context, MainActivity::class.java)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                )
                                            }
                                            else {
                                                toast(context, task.exception!!.localizedMessage!!)
                                            }
                                        }
                                }
                            }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(PaddingValues(top = 60.dp))
                        ) {
                            Text(
                                text = "Have an account?",
                                fontSize = 16.sp,
                            )
                            Text(
                                text = "Sign In",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                        onClick = {
                                            navController.navigate(IntroMenus.SignIn.route) {
                                                popUpTo(0) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        )
    }
}