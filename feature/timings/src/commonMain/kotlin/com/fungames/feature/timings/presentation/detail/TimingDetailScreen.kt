import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
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
import com.fungames.core.navigation.NavigationResultHandler
import com.fungames.core.navigation.Route
import com.fungames.core.navigation.StationPickerResult
import com.fungames.feature.timings.navigation.TimingRoutes
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
    // State to hold the selected station result (one-time event)
    var selectedStation by remember { mutableStateOf<StationPickerResult?>(null) }

    val routingEffectState = viewModel.stationRoutingEffect.collectAsState(null)

    // Register result callback for this navigation entry (CMP-safe, navigation-scoped)
    LaunchedEffect(backStackEntry.id) {
        onRegisterResultCallback?.invoke(backStackEntry.id) { result ->
            when (result) {
                is StationPickerResult -> {
                    // One-time event: store result
                    selectedStation = result
                    println("Selected station: ${result.name}")
                }
            }
        }
    }

    LaunchedEffect(key1 = routingEffectState.value) {
        routingEffectState.value?.let { route ->
            onNavigate(Route.StationPicker)
        }
    }

    Column {
        DisplayText("Timing Detail Screen")
        Spacer(Modifier.height(16.dp))
        
        // Display selected station result (one-time event)
        selectedStation?.let { station ->
            DisplayText("Selected Station: ${station.name} (ID: ${station.id})")
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
