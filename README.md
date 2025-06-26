# ESP32 Companion App 📱🔌

![Platform](https://img.shields.io/badge/platform-Android-blue.svg)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-ff69b4.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

The **ESP32 Companion App** is a modern Android application built with **Jetpack Compose**, designed to monitor and interact with a dynamic network of **ESP32 devices** using **ESP-NOW and MQTT**. It is ideal for IoT environments such as cold chain monitoring, sensor-based infrastructure, and mesh gateway networks.

---

## ✨ Features

- 📶 **Mesh Monitoring**  
  Visualize active gateways and connected nodes in the ESP32 mesh network.

- 📈 **Performance KPIs**  
  View device-level statistics such as uptime, number of readings, packets sent, and transmission failures.

- 🌡️ **Sensor Readings**  
  Access and inspect recent environmental data like temperature, humidity, pressure, and light intensity in a clean, user-friendly layout.

- ⚙️ **Settings Dialog**  
  Easily configure API endpoints and settings using a dedicated dialog accessible from any screen.

- 👤 **Profile Screen**  
  View the active JWT token and logout from your session securely.

---

## 🧩 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Network**: Ktor Client (CIO engine)
- **JSON Parsing**: Kotlinx Serialization
- **Architecture**: Stateless composables, scoped coroutines, centralized session management

---

## 🚀 Getting Started

1. Clone the repository:
```bash
git clone https://github.com/rednexx46/esp32-companion-app.git
```

2. Open in **Android Studio Giraffe or newer**.

3. Update your `.env`-like storage or constants (e.g., API base URL) if needed.

4. Run on a physical device or emulator with internet access.

---

## 📡 Backend Integration

This app connects to a secure backend that:

* Authenticates via JWT
* Serves real-time data via `/api/data`, `/api/kpis`, `/api/status`
* Uses WebSocket or polling (future feature) for live updates
* Integrates with an MQTT broker that receives ESP32 sensor payloads

---

## 🛡️ Security

* Secure authentication via JWT
* Local token storage via `SessionManager`
* Role-based API access supported on the backend

---

## 📱 UI Structure

* Bottom Navigation with 5 pages:

  * **Home**
  * **Mesh**
  * **Sensors**
  * **KPIs**
  * **Profile**

* Global **Settings button** always visible in the top-right

---

## 📌 Roadmap (WIP)

* [ ] Real-time WebSocket updates
* [ ] BLE commands to individual ESP32 nodes
* [ ] Dark mode support
* [ ] Sensor graphs using ChartsCompose
* [ ] Device health alerts & notifications
