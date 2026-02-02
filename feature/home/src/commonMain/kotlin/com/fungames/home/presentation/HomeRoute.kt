import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.fungames.core.navigation.Route
import com.fungames.home.presentation.HomeScreen
import com.fungames.home.presentation.HomeViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: HomeViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.homeNavigationEffect.collect { state ->
            onNavigate(state)
        }
    }

    HomeScreen(
        homeStateFlow = viewModel.homeState,
        onIntent = { intent -> viewModel.userIntent(intent) }
    )
}