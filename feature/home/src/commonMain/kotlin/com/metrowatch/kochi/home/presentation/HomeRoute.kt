import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.metrowatch.kochi.navigation.LocalNavigationResults
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.navigation.result.NavigationKeys
import com.metrowatch.kochi.home.presentation.HomeScreen
import com.metrowatch.kochi.home.presentation.HomeViewModel
import com.metrowatch.kochi.home.presentation.HomePageIntent
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: HomeViewModel
) {
    val navigationResults = LocalNavigationResults.current

    // Consume station picker result and dispatch to ViewModel
    LaunchedEffect(navigationResults.version) {
        val name = navigationResults.consume<String>(NavigationKeys.STATION_PICKER_RESULT)
        val id = navigationResults.consume<String>(NavigationKeys.STATION_PICKER_ID_RESULT)
        if (name != null) {
            viewModel.userIntent(HomePageIntent.StationPickedForTrip(name, id ?: ""))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.homeNavigationEffect.collect { route ->
            if (route == Route.PlanTrip) {
                // Write pre-fill data before navigating so PlanTripScreen auto-submits
                val state = viewModel.homeState.value
                val fromName = state.planFromStation.ifEmpty {
                    if (state.nearestStationAvailable) state.nearestStation.stationName else ""
                }
                val fromId = state.planFromId.ifEmpty {
                    if (state.nearestStationAvailable) state.nearestStation.stationCode else ""
                }
                val toName = state.planToStation
                val toId = state.planToId
                if (fromName.isNotEmpty() && toName.isNotEmpty()) {
                    navigationResults.set(NavigationKeys.PLAN_TRIP_FROM_NAME, fromName)
                    navigationResults.set(NavigationKeys.PLAN_TRIP_FROM_ID, fromId)
                    navigationResults.set(NavigationKeys.PLAN_TRIP_TO_NAME, toName)
                    navigationResults.set(NavigationKeys.PLAN_TRIP_TO_ID, toId)
                }
                onNavigate(route)
            } else {
                onNavigate(route)
            }
        }
    }

    HomeScreen(
        homeStateFlow = viewModel.homeState,
        onIntent = { intent -> viewModel.userIntent(intent) }
    )
}
