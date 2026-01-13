import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Text view cmposable
 */
@Composable
fun DisplayText(text: String) {
    Text(
        text = text,
        color = Color.Black
    )
}