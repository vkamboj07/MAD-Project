# 📱 MAD Project — Mobile Application Development

A collection of Android applications built using **Java** to demonstrate core Android development concepts, APIs, and UI practices.

This repository contains **four standalone Android projects**, each focusing on a different aspect of mobile application development.

---

## 🚀 Projects Overview

| #      | Project            | Description                                    |
| ------ | ------------------ | ---------------------------------------------- |
| **Q1** | Currency Converter | Offline currency conversion with theme support |
| **Q2** | Media Player       | Audio playback + video streaming with controls |
| **Q3** | Sensor Reader      | Real-time hardware sensor data display         |
| **Q4** | Photo Gallery      | Camera capture + gallery browsing app          |

---

## 📌 Q1 — Currency Converter

A lightweight offline currency converter supporting multiple currencies.

### ✨ Features

* Convert between **INR, USD, JPY, EUR**
* Real-time conversion as you type
* Swap currencies instantly
* Light/Dark theme toggle
* Persistent theme settings using `SharedPreferences`

### 📂 Key Files

* `Rates.java` — Static exchange rate definitions
* `Currency.java` — Enum for supported currencies
* `SettingsActivity.java` — Theme settings UI
* `ThemePrefs.java` — Preference handling

---

## 🎵 Q2 — Media Player

A multimedia player supporting both audio playback and video streaming.

### ✨ Features

* Play audio from local storage
* Stream video via URL (`VideoView`)
* Playback controls: **Play / Pause / Stop / Restart**
* Interactive **SeekBar** with time tracking
* Auto show/hide player controls

### 📂 Key Files

* `MainActivity.java` — Core playback logic
* `activity_main.xml` — Player UI layout

---

## 📡 Q3 — Sensor Reader

Displays real-time data from device sensors.

### ✨ Features

* Accelerometer (**X, Y, Z values**)
* Light sensor (**lux**)
* Proximity sensor (**distance**)
* Handles missing sensors gracefully
* Efficient lifecycle handling (`onResume` / `onPause`)

### 📂 Key Files

* `MainActivity.java` — Sensor event handling
* `activity_main.xml` — UI layout for sensor data

---

## 📷 Q4 — Photo Gallery Camera

A modern gallery app with camera integration.

### ✨ Features

* Select folders from device storage
* Capture photos using camera (`FileProvider`)
* Grid-based gallery view
* Full image preview with metadata
* Delete images from app
* Clean **Material3 UI**

### 📂 Key Files

* `MainActivity.java` — Navigation & folder selection
* `CameraActivity.java` — Camera integration
* `GalleryActivity.java` — Image grid
* `ImageDetailActivity.java` — Image preview & metadata
* `ImageAdapter.java` — RecyclerView adapter (Glide)

---

## ⚙️ Getting Started

### ✅ Prerequisites

* Android Studio **Outer 2 or newer**
* Android SDK **24+**
* Emulator or physical device

### ▶️ Run the Project

```bash
git clone https://github.com/vkamboj07/MAD-Project.git
```

1. Open **Android Studio**
2. Click **Open Project**
3. Select any project folder (e.g. `Q1_Currency_Convertor`)
4. Wait for **Gradle Sync**
5. Click **Run ▶**

> ⚠️ Each project is independent — open them individually.

---

## 🛠️ Tech Stack

| Technology                    | Purpose                   |
| ----------------------------- | ------------------------- |
| **Java**                      | Core development language |
| **Android SDK 35**            | Target platform           |
| **Material Components**       | UI design                 |
| **ViewBinding / DataBinding** | Safer view handling       |
| **Glide**                     | Image loading (Q4)        |
| **MediaPlayer / VideoView**   | Media playback (Q2)       |
| **SensorManager**             | Sensor data (Q3)          |
| **FileProvider**              | Secure file sharing (Q4)  |

---

## 📁 Project Structure

```
MAD-Project/
├── Q1_Currency_Convertor/
├── Q2_MediaPlayer/
├── Q3_SensorReader/
└── Q4_PhotoGallery/
```

---

## 💡 Highlights

* Clean and modular project structure
* Covers multiple Android domains:

  * UI/UX
  * Sensors
  * Media
  * Storage & Files
* Beginner-friendly yet practical implementations

---

## 📜 License

This project is for educational purposes.

---

## 👨‍💻 Author

**Vaibhav Kamboj**
GitHub: https://github.com/vkamboj07

---

⭐ If you found this helpful, consider giving the repository a star!
