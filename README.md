# 🧠 MindBoost – A mobile app supporting teenagers' mental health
<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Status-Bachelor's_Thesis-blue?style=for-the-badge" />
</p>

## 📌 About the project

**MindBoost** is a native Android mobile app (API 33), created as a Bachelor's engineering thesis and awarded the **highest grade (excellent)**. The application is a response to the growing mental health problems among children and adolescents. 

The main goal of the project is to enable teenagers to monitor their mood (emotion journal), learn stress-coping mechanisms (meditation, breathing exercises), and perform an initial self-diagnosis using the clinically recognized **Beck Depression Inventory (BDI)**.

## ✨ Key Features

* **Emotion Journal:** Daily logging of mental state using intuitive icons, along with in-depth reflection on the causes of the mood.
* **Beck Depression Inventory:** Full implementation of the test (20 questions adapted to the user's age) with automatic point calculation and clinical interpretation.
* **Recommendation System:** Smart activity suggestions (e.g., sports, walking, contacting loved ones) if a low mood is detected for several consecutive days.
* **PDF Reports:** The ability to generate mood history into a PDF file, which facilitates cooperation with a psychotherapist.
* **Help Database:** Direct access to verified support lines and foundations (integration with the phone dialer app).

## 🖼️ Visual Documentation

### Use Case Diagram (UML)
The project was preceded by a thorough business analysis. The diagram illustrates user interactions with the system, including authentication processes (support for Facebook/Google Auth) and the main modules of the application.

<img width="1123" height="1044" alt="image" src="https://github.com/user-attachments/assets/b00301f0-eab9-4690-80ad-a1e098941f0d" />

### Layouts and User Interfaces
The app was designed in a soothing, light blue color palette to minimize user stress.

| Main Dashboard | Mood Details Activity |
|:---:|:---:|
| <img width="476" height="1009" alt="image" src="https://github.com/user-attachments/assets/2e01c302-a9bf-4fbc-8ae3-813330c78e9f" /> |<img width="476" height="1009" alt="image" src="https://github.com/user-attachments/assets/40234caa-b79c-436b-b357-79171fd41b12" /> |
| **Beck Depression Inventory Intro Screen** | **Test Result** |
| <img width="410" height="867" alt="image" src="https://github.com/user-attachments/assets/12e78ece-5af6-4880-9d8e-21ac67160e50" /> | <img width="434" height="920" alt="image" src="https://github.com/user-attachments/assets/33932494-ebe1-4b2a-b730-032ff004bfb1" /> |
| **Support Foundations Screen** | **Mood Improvement Suggestions Screen** |
| <img width="520" height="1097" alt="image" src="https://github.com/user-attachments/assets/aa22f6ac-3cba-4c79-a18c-b8b041650d54" /> | <img width="335" height="900" alt="image" src="https://github.com/user-attachments/assets/d7fbbc70-5b54-4579-9748-a6f53662ea2e" />|


## 🛠️ Tech Stack

* **Language:** Kotlin, Java.
* **Architecture:** Division into Activities and Fragments, component lifecycle management.
* **Backend:** Firebase Authentication: Email, Google, and Facebook login.
* **Firebase Realtime Database:** Real-time storage of mood history and test results.
* **UI/UX:** XML, Material Design, integration with external applications (Phone Dialer).
* **Business Logic:** Custom algorithms `setReminder` and `daysBetween` to monitor the continuity of the mental state.

## 🚀 Installation and Setup

1. Clone the repository: `git clone https://github.com/harikiriki/MindBoost.git`
2. Open the project in **Android Studio**.
3. Configure your own project in the Firebase console.
4. Run on an emulator with API 33+.
