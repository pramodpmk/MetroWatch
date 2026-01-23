import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.Route
import com.fungames.core.navigation.result.NavigationKeys
import com.fungames.feature.timings.presentation.TimingTableViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrainTimingDetail(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry,
    onNavigate: (Route) -> Unit,
    onRegisterResultCallback: ((String, (Any) -> Unit) -> Unit)? = null,
    viewModel: TimingTableViewModel = koinViewModel()
) {
    val navigationResults = LocalNavigationResults.current
    val routingEffectState = viewModel.stationRoutingEffect.collectAsState(null)
    var selectedStation by remember {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        navigationResults
            .consume<String>(NavigationKeys.STATION_PICKER_RESULT)
            ?.let {
                println("StationPickerResult>>>$it")
                selectedStation = it
            }
    }

    LaunchedEffect(key1 = routingEffectState.value) {
        routingEffectState.value?.let { route ->
            onNavigate(Route.StationPicker)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            println("onDispose>>>TimingDetailScreen")
            navigationResults.clearResult(NavigationKeys.STATION_PICKER_RESULT)
        }
    }

    Column {
        DisplayText("Timing Detail Screen")
        Spacer(Modifier.height(16.dp))
        
        // Display selected station result (one-time event)
        selectedStation?.let { station ->
            DisplayText("Selected Station: ${station}")
            Spacer(Modifier.height(8.dp))
        }
        
        Spacer(Modifier.height(60.dp))
        Button(onClick = {
            viewModel.userIntent()
        }) {
            DisplayText("Pick Station")
        }
    }
}
