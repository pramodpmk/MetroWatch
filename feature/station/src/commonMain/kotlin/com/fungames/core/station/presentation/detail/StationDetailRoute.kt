import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.fungames.core.ui.components.DisplayText
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.station.presentation.StationViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StationDetailRoute(
    navHostController: NavHostController,
    viewModel: StationViewModel = koinViewModel()
) {
    Column {
        DisplayText("Station Detail Screen")
        Spacer(Modifier.height(60.dp))
        Button(onClick = {
            //viewModel.userIntent()
            navHostController.popBackStack()
        }) {
            DisplayText("Back")
        }

    }
}