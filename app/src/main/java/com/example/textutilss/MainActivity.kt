package com.example.textutilss

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RecordVoiceOver // Make sure you have this import
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.textutilss.ui.theme.TextUtilssTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextUtilssTheme {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val wordCount = remember(text) { text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size }
    val characterCount = remember(text) { text.length }
    val reversedText = remember(text) { text.reversed() }
    val textPreview = remember(text) { text.take(100) }

    // --- Speech Recognizer Setup ---
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var isListening by remember { mutableStateOf(false) }

    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenText.isNullOrEmpty()) {
                text = spokenText[0] // Set the recognized text to your TextField
            }
        }
        isListening = false
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startListening(speechRecognitionLauncher, context)
        } else {
            Toast.makeText(context, "Permission to record audio denied", Toast.LENGTH_SHORT).show()
        }
        isListening = false // Ensure isListening is reset if permission is denied immediately
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy() // Important to release resources
        }
    }
    // --- End Speech Recognizer Setup ---

    // Define cool colors
    val primaryCoolColor = Color(0xFF5E35B1) // Deep Purple variant
    val secondaryCoolColor = Color(0xFF03A9F4) // Light Blue
    val tertiaryCoolColor = Color(0xFF00ACC1) // Cyan
    val accentCoolColor = Color(0xFF4CAF50)    // Green
    val actionBlueColor = Color(0xFF1976D2) // For Copy/Speak buttons
    val errorColor = MaterialTheme.colorScheme.error

    // Gradients
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(primaryCoolColor.copy(alpha = 0.15f), secondaryCoolColor.copy(alpha = 0.1f), tertiaryCoolColor.copy(alpha = 0.15f))
    )


    fun convertToTitleCase(input: String): String {
        return input.split(" ").joinToString(" ") { word ->
            word.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.text_field_label), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier
                        .fillMaxWidth()

                        .clip(RoundedCornerShape(12.dp)),

                    minLines = 7,
                    textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                        },
                        text = stringResource(R.string.copy_to_clipboard),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ContentCopy,
                        backgroundColor = actionBlueColor
                    )

                    ActionButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                if (!isListening) { // Prevent starting multiple times if already listening
                                    isListening = true
                                    startListening(speechRecognitionLauncher, context)
                                }
                            } else {
                                isListening = true // Set to true here to update UI while permission dialog is shown
                                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        text = if (isListening) stringResource(R.string.listening) else stringResource(R.string.speak),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.RecordVoiceOver,
                        backgroundColor = if (isListening) accentCoolColor else actionBlueColor,
                        enabled = !isListening // Consider keeping it enabled to allow retrying permission
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val buttonModifier = Modifier.weight(1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CoolButton(
                        onClick = { text = text.uppercase(Locale.getDefault()) },
                        text = stringResource(R.string.to_uppercase),
                        modifier = buttonModifier,
                        backgroundColor = primaryCoolColor,
                        fontSize = 15.sp
                    )
                    CoolButton(
                        onClick = { text = text.lowercase(Locale.getDefault()) },
                        text = stringResource(R.string.to_lowercase),
                        modifier = buttonModifier,
                        backgroundColor = secondaryCoolColor,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CoolButton(
                        onClick = { text = convertToTitleCase(text) },
                        text = stringResource(R.string.to_title_case),
                        modifier = buttonModifier,
                        backgroundColor = tertiaryCoolColor,
                        fontSize = 14.sp
                    )
                    CoolButton(
                        onClick = { text = text.replace("\\s+".toRegex(), " ").trim() },
                        text = stringResource(R.string.trim_spaces),
                        modifier = buttonModifier,
                        backgroundColor = accentCoolColor,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CoolButton(
                        onClick = { text = reversedText },
                        text = stringResource(R.string.reverse_text),
                        modifier = buttonModifier,
                        backgroundColor = primaryCoolColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    CoolButton(
                        onClick = { text = text.replace(" ", "") },
                        text = stringResource(R.string.remove_spaces),
                        modifier = buttonModifier,
                        backgroundColor = secondaryCoolColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CoolButton(
                        onClick = { text = "" },
                        text = stringResource(R.string.clear_all),
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = errorColor,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.word_count_label, wordCount), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.character_count_label, characterCount), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.preview_label, textPreview.ifEmpty { stringResource(R.string.preview_placeholder) }),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Helper function to start listening
private fun startListening(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, context: android.content.Context) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak_now_prompt))
    }
    try {
        launcher.launch(intent)
    } catch (e: Exception) {
        Log.e("SpeechToText", "Error starting speech recognition: ${e.message}")
        Toast.makeText(context, context.getString(R.string.speech_recognition_not_available), Toast.LENGTH_LONG).show()
        // Potentially reset isListening state here if needed, though it's handled in the launcher's result callback
    }
}


@Composable
fun CoolButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier, backgroundColor: Color, fontSize: TextUnit = 14.sp) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
    ) {
        Text(text, fontSize = fontSize, fontWeight = FontWeight.Medium, letterSpacing = 0.25.sp)
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    enabled: Boolean = true // Added enabled parameter
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = contentColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 6.dp),
        enabled = enabled // Use the enabled parameter
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = contentColor)
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Text(text, fontSize = fontSize, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
fun DefaultPreview() {
    TextUtilssTheme {
        AppContent()
    }
}
