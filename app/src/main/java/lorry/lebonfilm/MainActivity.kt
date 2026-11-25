package lorry.lebonfilm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import lorry.lebonfilm.ui.theme.LeBonFilmTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import lorry.lebonfilm._components.displayActor.DisplayedActor
import lorry.lebonfilm._tools.Logger
import lorry.lebonfilm.ui.screens.Screens
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding

lateinit var colors: ColorScheme
val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

class MainActivity : ComponentActivity() {

    val displayedActor: DisplayedActor by inject()
    val logger: Logger by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        logger.debug("dans OnCreate()")

        setContent {
            LeBonFilmTheme {
                colors = MaterialTheme.colorScheme
                LeBonFilmApp()
            }
        }
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }
}

@PreviewScreenSizes
@Composable
fun MainActivity.LeBonFilmApp(
    screenState: Screens.NavigationState = koinInject()
) {
    val navSuiteType = calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        NavigationSuiteScaffoldLayout(
            layoutType = navSuiteType,
            navigationSuite = {
                if (navSuiteType == NavigationSuiteType.NavigationRail) {
                    NavigationRail {
                        Screens.listOfScreens
                            .dropLast(1)
                            .forEach { item ->
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                        )
                                    },
                                    label = { Text(item.label) },
                                    selected = screenState.currentScreen == item,
                                    onClick = { screenState.currentScreen = item }
                                )
                            }

                        Spacer(Modifier.weight(1f))

                        Screens.listOfScreens
                            .last().let { screen ->
                                NavigationRailItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.label,
                                        )
                                    },
                                    label = { Text(screen.label) },
                                    selected = screenState.currentScreen == screen,
                                    onClick = { screenState.currentScreen = screen }
                                )
                            }
                    }
                } else {
                    NavigationSuite {
                        Screens.listOfScreens
                            .forEach { item ->
                                item(
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                        )
                                    },
                                    label = { Text(item.label) },
                                    selected = screenState.currentScreen == item,
                                    onClick = { screenState.currentScreen = item }
                                )
                            }
                    }
                }
            }
        ) {
            screenState.currentScreen.content?.invoke(this@LeBonFilmApp, androidx.compose.foundation.layout.PaddingValues())
        }
    }

//        navigationSuiteItems = {
//            Screens.entries.forEach {
//                item(
//                    icon = {
//                        Icon(
//                            imageVector = it.icon,
//                            contentDescription = it.label,
//                        )
//                    },
//                    label = { Text(it.label) },
//                    selected = it == screenState.currentScreen,
//                    onClick = { screenState.currentScreen = it }
//                )
//            }
//
//
//        }
//    ),
//        navigationSuiteColors = NavigationSuiteDefaults.colors(
//            shortNavigationBarContainerColor = Color.Black,
//            shortNavigationBarContentColor = Color.Magenta,
//            wideNavigationRailColors = WideNavigationRailDefaults.colors(),
//            /*.colors(
//
//                            containerColor = Color.Black,
//                            contentColor = Color.White,
//                            modalContainerColor = Color.Black,
//                            modalScrimColor = Color.Black.copy(alpha = 0.6f),
//                            modalContentColor = Color.White,
//
//                        )
//                        */
//            navigationBarContainerColor = Color.Black,
//            navigationBarContentColor = Color.Red,
//            navigationRailContainerColor = Color.Black,
//            navigationRailContentColor = Color.Blue,
//            navigationDrawerContainerColor = Color.Black,
//            navigationDrawerContentColor = Color.Green,
//        ),
//    ) {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            Column(
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ) {
//                screenState.currentScreen.content?.invoke(this@LeBonFilmApp, innerPadding)
//            }
//        }
//    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LeBonFilmTheme {
        Greeting("Android")
    }
}

fun MainActivity.hideKeyboard() {
    hideKeyboard()
}