package org.specter.paymentdivider.app

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

sealed class NavigationOption(val option: (NavOptionsBuilder.() -> Unit)? = null) {

    data class PopBack(
        val destination: NavigationRoute? = null,
        val inclusive: Boolean = true,
        val saveState: Boolean = false
    ) : NavigationOption()

    data class Screen(
        val route: NavigationRoute,
        val opt: (NavOptionsBuilder.() -> Unit)? = null
    ) : NavigationOption(opt)
}

sealed interface NavigationRoute {

    @Serializable
    data object PaymentGraph : NavigationRoute

    @Serializable
    data object CreateHuman : NavigationRoute

    @Serializable
    data object PaymentCalculator : NavigationRoute
}

fun NavController.navigateToRoute(option: NavigationOption) {
    when (option) {
        is NavigationOption.PopBack -> {
            if (option.destination != null) {
                popBackStack(
                    option.destination,
                    option.inclusive,
                    option.saveState
                )
            } else {
                popBackStack()
            }
        }

        is NavigationOption.Screen -> {
            if (option.option != null) {
                navigate(option.route, option.option)
            } else {
                navigate(option.route)
            }
        }
    }
}


private const val ANIMATE_DURATION = 400

private val slideInEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        tween(ANIMATE_DURATION)
    )
}

private val slideOutExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        tween(ANIMATE_DURATION)
    )
}

fun <T : NavigationRoute> NavGraphBuilder.fadeAnimateComposable(
    route: KClass<T>,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    enterTransition = { fadeIn(tween(ANIMATE_DURATION)) },
    exitTransition = { fadeOut(tween(ANIMATE_DURATION)) },
    popEnterTransition = { fadeIn(tween(ANIMATE_DURATION)) },
    popExitTransition = { fadeOut(tween(ANIMATE_DURATION)) },
    content = content,
    deepLinks = deepLinks,
)

fun <T : NavigationRoute> NavGraphBuilder.slideAnimateComposable(
    route: KClass<T>,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable(
    route,
    enterTransition = slideInEnter,
    exitTransition = { fadeOut(tween(ANIMATE_DURATION)) },
    popEnterTransition = { fadeIn(tween(ANIMATE_DURATION)) },
    popExitTransition = slideOutExit,
    content = content,
    deepLinks = deepLinks,
)
