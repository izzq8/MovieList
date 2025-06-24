package com.faizabhinaya.mymovielist2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.faizabhinaya.mymovielist2.ui.components.LanguageSelectionDialog
import com.faizabhinaya.mymovielist2.ui.navigation.AppNavHost
import com.faizabhinaya.mymovielist2.ui.theme.MyMovieList2Theme
import com.faizabhinaya.mymovielist2.utils.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentLanguage = LocaleHelper.getLanguage(this)
        LocaleHelper.setLocale(this, currentLanguage)

        setContent {
            MyMovieList2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showLanguageDialog by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController)

                        IconButton(
                            onClick = { showLanguageDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = stringResource(R.string.change_language)
                            )
                        }

                        if (showLanguageDialog) {
                            LanguageSelectionDialog(
                                onDismiss = { showLanguageDialog = false },
                                onLanguageSelected = { code ->
                                    LocaleHelper.setLocale(this@MainActivity, code)
                                    showLanguageDialog = false
                                    recreate()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
