package com.radx.ankunv2.screens.profile

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.radx.ankunv2.screens.intro.IntroActivity
import com.radx.ankunv2.screens.intro.IntroMenus
import com.radx.ankunv2.screens.intro.SignInScreen
import com.radx.ankunv2.ui.theme.Grey

@Composable
fun ProfileNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ProfileMenus.Profile.route
    ) {
        composable(route = ProfileMenus.Profile.route) { ProfileMainScreen(navController) }
    }
}

@Composable
fun ProfileScreen() {
    val navController = rememberNavController()
    ProfileNavigationHost(navController)
}

@Composable
fun ProfileMainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firebaseAuth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // profile email
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val currentUser = Firebase.auth.currentUser
            var userEmail: String? = ""
            if (currentUser != null) {
                userEmail = currentUser.email
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Email",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 8.dp)
                )
                Text(
                    text = "Change",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                            onClick = { }
                        )
                )
            }
            Text(
                text = userEmail!!,
                color = Grey,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Divider()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp, 0.dp, 0.dp),
        ) {
            // sign out button
            Text(
                text = "Sign Out",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                        onClick = {
                            firebaseAuth.signOut()
                            // navigate back to intro menus (sign in page)
                            context.startActivity(
                                Intent(context, IntroActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        }
                    )
            )

            // settings menu button
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                        onClick = {

                        }
                    )
            )
        }
    }
}