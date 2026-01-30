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
import com.fungames.core.ui.components.DisplayText
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.feature.timings.presentation.TimingTableViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrainTimingScreen(
    navHostController: NavHostController,
    viewModel: TimingTableViewModel = koinViewModel()
) {
    val timingState = viewModel.timingTableState.collectAsState()

    Column {
        DisplayText("Train Timing Screen ${timingState.value}")
        Spacer(Modifier.height(60.dp))
        Button(onClick = {
            //viewModel.userIntent()
            navHostController.navigate(TimingRoutes.TimingDetail)
        }) {
            DisplayText("Open Detail")
        }

    }
}