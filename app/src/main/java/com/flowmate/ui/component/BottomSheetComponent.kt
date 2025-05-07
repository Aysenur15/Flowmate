package com.flowmate.ui.component
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A reusable “Add…” bottom sheet.
 *
 * @param sheetTitle    Title to show at top of sheet.
 * @param visible       Controls whether the sheet is shown.
 * @param onDismiss     Called when the user swipes down or taps outside.
 * @param onAdd         Called when the user confirms the add action.
 * @param sheetContent  The form fields (e.g. TextFields) to render inside the sheet.
 * @param content       The main screen content, gets passed a function to show the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    sheetTitle: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (showSheet: () -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    // When `visible` flips to true, launch to expand
    LaunchedEffect(visible) {
        if (visible) sheetState.show() else sheetState.hide()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        tonalElevation = 8.dp
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(sheetTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            // User-provided form content
            sheetContent()
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    onAdd()
                    scope.launch { sheetState.hide() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add")
            }
        }
    }

    // Wrap your screen’s content, passing in a lambda to show the sheet
    content {
        scope.launch { sheetState.show() }
    }
}
