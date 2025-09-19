# TextUtils — Android Jetpack Compose Text Manipulator

<p align="center">
  <b>A sleek, Compose-based app for real-time text manipulation and voice input.</b>
</p>

---

## 🚀 Features

- **Type or paste** text, instantly edit with powerful operations.
- **Transformations:** Uppercase, lowercase, title case, trim/reverse/remove spaces, clear all.
- **Copy to clipboard** with one tap.
- **Speech-to-Text:** Tap the mic, speak—capture voice as text.
- **Real-time stats:** Word & character counts + preview.
- **Dark/Light Theme:** Adapts to system settings.
- **Modern Jetpack Compose UI**: Fast, smooth, accessible.



## 🛠️ Tech Stack

- **Jetpack Compose** (Declarative, reactive UI)
- **Kotlin** (Primary language)
- **SpeechRecognizer/RecognizerIntent** (Native Android STT)
- **Dynamic Permission Handling** (`ActivityResultLauncher`)
- **Resource Management** (`DisposableEffect` for cleanup)
- **Custom Themes** (Gradient, adaptive colors)

---

## 📱 How To Use

1. **Launch the app** (requires Android 5.0+).
2. **Type or paste** text into the field.
3. **Tap any button** to transform your text (UPPERCASE, lowercase, Title Case, etc.).
4. **Copy to clipboard** with one tap.
5. **Voice Input:** Tap the microphone, grant permission, and speak. Your words appear instantly.
6. **Clear** the field anytime.
7. **See real-time stats** for word & character counts and a preview.

---

## ⚙️ Permissions

- **RECORD_AUDIO** (For microphone/voice input)


---

## 🚦 Running the Project

1. **Clone the repo.**
2. **Open in Android Studio** (latest stable).
3. **Sync Gradle.**
4. **Run on device** (recommended) or emulator.
5. **Test all features**, especially voice input, on a physical device.

---

## 📝 Customization

- **Change UI colors** by editing constants in `AppContent`.
- **Add more text utilities** by extending the logic in `AppContent`.
- **Translate UI strings** by editing `strings.xml`.

---

## 🤝 Contributing

Open an **issue** for bugs/suggestions.  
Submit a **pull request** for new features or fixes.

---

## 📜 License

Open-source. Use, modify, share—just credit the original author.

---

## ✉️ Contact

Questions? Feedback?  
**Open an issue** or **reach out** with your suggestions!
