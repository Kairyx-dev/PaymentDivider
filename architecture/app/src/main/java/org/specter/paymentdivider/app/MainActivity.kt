package org.specter.paymentdivider.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dagger.hilt.android.AndroidEntryPoint
import org.specter.paymentdivider.app.ui.screen.CreateHumanScreen
import org.specter.paymentdivider.app.ui.screen.PaymentCalculateScreen
import org.specter.paymentdivider.app.ui.theme.PaymentDividerTheme
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()

            PaymentDividerTheme {
                Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                    NavHost(navController, startDestination = NavigationRoute.PaymentGraph::class) {
                        rootGraph()
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.rootGraph() {
        navigation(route = NavigationRoute.PaymentGraph::class, startDestination = NavigationRoute.CreateHuman::class) {
            fadeAnimateComposable(NavigationRoute.CreateHuman::class) {
                val graphEntry = it.remember(NavigationRoute.PaymentGraph::class)

                CreateHumanScreen(hiltViewModel(graphEntry)) { route ->
                    navController.navigateToRoute(route)
                }
            }

            fadeAnimateComposable(NavigationRoute.PaymentCalculator::class) {
                val graphEntry = it.remember(NavigationRoute.PaymentGraph::class)

                PaymentCalculateScreen(paymentViewModel = hiltViewModel(graphEntry)) { route ->
                    navController.navigateToRoute(route)
                }
            }
        }
    }

    @Composable
    private fun <A : NavigationRoute> NavBackStackEntry.remember(routeClass: KClass<A>): NavBackStackEntry {
        return remember(this) {
            navController.getBackStackEntry(routeClass)
        }
    }
}
