import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.navigation.Route
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.feature.timings.presentation.TimingTableViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrainTimingDetail(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: TimingTableViewModel = koinViewModel()
) {

    val routingEffectState = viewModel.stationRoutingEffect.collectAsState(null)

    LaunchedEffect(key1 = routingEffectState.value) {
        routingEffectState.value?.let { route ->
            onNavigate(Route.StationDetail)
        }
    }

    Column {
        DisplayText("Timing Detail Screen")
        Spacer(Modifier.height(60.dp))
        Button(onClick = {
            viewModel.userIntent()
        }) {
            DisplayText("Back")
        }

    }
}