<div align="center">

<img src="https://github.com/Madhu2150/Namma-Santhe-Ledger/blob/main/app/src/main/ic_launcher-playstore.png" 
     alt="Namma-Santhe Ledger Logo" 
     width="120" 
     height="120"
     style="border-radius: 20px"/>

# 🌿 Namma-Santhe Ledger

### *Simplified Digital Khata for Rural Vendors*

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen?style=for-the-badge&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2026-orange?style=for-the-badge&logo=android)](https://developer.android.com/about/versions/oreo)
[![License](https://img.shields.io/badge/License-MIT-red?style=for-the-badge)](LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/Madhu2150/Namma-Santhe-Ledger?style=for-the-badge&logo=github)](https://github.com/Madhu2150/Namma-Santhe-Ledger/stargazers)

<br/>

> **Built for Bharat 🇮🇳 — Empowering the smallest entrepreneur at every Santhe**

<br/>

[📥 Download APK](#-download) • 
[✨ Features](#-features) • 
[🛠️ Tech Stack](#️-tech-stack) • 
[🚀 Getting Started](#-getting-started) • 
[📸 Screenshots](#-screenshots) •
[🤝 Contributing](#-contributing)

</div>

---

## 📖 The Problem

<table>
<tr>
<td width="50%">

Weekly village markets **(Santhe)** are the economic backbone of rural retail in Karnataka and across India.

Small vendors — selling **vegetables, bangles, snacks** — extend informal credit **(Udari)** to regular customers as a matter of **trust**.

</td>
<td width="50%">

| 😟 Pain Point | 📌 Current Reality | 💥 Impact |
|---|---|---|
| Credit tracking | Handwritten diary | Entries lost |
| Payment records | Verbal agreements | Unpaid dues |
| Daily summary | Mental arithmetic | Wrong profit view |
| Reminders | In-person only | Often skipped |

</td>
</tr>
</table>

---

## 💡 The Solution

<div align="center">

```text
📒 Paper Diary  ──────►  📱 Namma-Santhe Ledger
     ❌ Lost                    ✅ Always safe
     ❌ Illegible               ✅ Clear & fast
     ❌ No reminders            ✅ WhatsApp alerts
     ❌ Manual math             ✅ Auto calculated
```

</div>

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🏠 Home Screen
- 📋 **Total Outstanding Dues** — always visible
- 📈 Today's Udari given
- 💰 Today's amount collected
- ⚡ Quick action buttons

</td>
<td width="50%">

### 👥 Customer Management
- Add contacts with **Name, Phone, Village, Address**
- Search by **name or village**
- Customer-wise ledger history
- **Name - Village** display format

</td>
</tr>
<tr>
<td width="50%">

### 💳 Udari & Payments
- ⚡ Add Udari in **under 5 seconds**
- Quick amount buttons **(+50, +100, +200, +500)**
- Record full or partial payments
- Real-time balance updates

</td>
<td width="50%">

### 📊 Reports & Insights
- Daily market summary
- Transaction history with filters
- **All / Udari / Payments** tabs
- Date-grouped transaction view

</td>
</tr>
<tr>
<td width="50%">

### 📱 WhatsApp Reminder
- One-tap reminder to customer
- Pre-filled Kannada/Hindi message
- No third-party API needed
- Works with any messaging app

</td>
<td width="50%">

### 🌐 Language Support
- **English** 🇬🇧
- **ಕನ್ನಡ** 🇮🇳
- Instant language switching
- Saved preference across sessions

</td>
</tr>
</table>

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology | Purpose |
|:---:|:---:|:---:|
| 📱 Language | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white) | Primary language |
| 🎨 UI | ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white) | Modern declarative UI |
| 🏗️ Architecture | ![MVVM](https://img.shields.io/badge/MVVM-Pattern-green?style=flat) | Clean architecture |
| 🗄️ Database | ![Room](https://img.shields.io/badge/Room%20DB-SQLite-blue?style=flat&logo=sqlite&logoColor=white) | Local persistence |
| 💉 DI | ![Hilt](https://img.shields.io/badge/Hilt-Dependency%20Injection-red?style=flat) | Dependency injection |
| 🧭 Navigation | ![Navigation](https://img.shields.io/badge/Jetpack%20Navigation-Component-orange?style=flat) | Screen navigation |
| ⚡ Async | ![Coroutines](https://img.shields.io/badge/Kotlin-Coroutines-purple?style=flat&logo=kotlin) | Background operations |
| 🔄 State | ![StateFlow](https://img.shields.io/badge/StateFlow-LiveData-blue?style=flat) | Reactive UI updates |

</div>

---

## 📐 Architecture

```text
┌─────────────────────────────────────────────────────┐
│                    UI Layer                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐    │
│  │   Home   │ │Customers │ │  Transactions    │    │
│  │  Screen  │ │   List   │ │     Page         │    │
│  └──────────┘ └──────────┘ └──────────────────┘    │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐    │
│  │ Add Udari│ │ Add Pay  │ │  Profile Page    │    │
│  │   Page   │ │  Page    │ │                  │    │
│  └──────────┘ └──────────┘ └──────────────────┘    │
├─────────────────────────────────────────────────────┤
│                 ViewModel Layer                     │
│  ┌─────────────────────────────────────────────┐    │
│  │            LedgerViewModel                  │    │
│  │  • totalOutstanding  • customersWithBalance │    │
│  │  • allTransactions   • homeUiState          │    │
│  │  • Smart GenAI alerts                       │    │
│  └─────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────┤
│                Repository Layer                     │
│  ┌─────────────────────────────────────────────┐    │
│  │           LedgerRepository                  │    │
│  │  Customer CRUD    │    Transaction CRUD      │    │
│  │  Daily Summary    │    Balance Calculation   │    │
│  └─────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────┤
│                  Data Layer                         │
│  ┌────────────────┐      ┌──────────────────────┐   │
│  │   Customer     │1───N │    Transaction        │   │
│  │  • id          │      │  • id                 │   │
│  │  • name        │      │  • customerId (FK)    │   │
│  │  • phone       │      │  • amount             │   │
│  │  • village     │      │  • type (UDARI/PAY)   │   │
│  │  • address     │      │  • timestamp          │   │
│  └────────────────┘      └──────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```text
app/src/main/java/com/namma/santhe/ledger/
│
├── 📂 data/
│   ├── 📂 db/
│   │   ├── AppDatabase.kt          # Room Database
│   │   ├── CustomerDao.kt          # Customer queries
│   │   └── TransactionDao.kt       # Transaction queries
│   ├── 📂 model/
│   │   ├── Customer.kt             # Customer entity
│   │   ├── Transaction.kt          # Transaction entity
│   │   └── CustomerWithBalance.kt  # Computed model
│   └── 📂 repository/
│       └── LedgerRepository.kt     # Data operations
│
├── 📂 di/
│   └── DatabaseModule.kt           # Hilt DI module
│
├── 📂 ui/
│   ├── MainApp.kt                  # MainActivity + NavGraph
│   ├── StringResources.kt          # English + Kannada strings
│   ├── LanguageManager.kt          # Language switching
│   ├── 📂 screens/
│   │   ├── HomeScreen.kt           # Main dashboard
│   │   ├── TransactionsPage.kt     # All transactions
│   │   ├── CustomersListPage.kt    # Customer list
│   │   ├── AddContactPage.kt       # Add new customer
│   │   ├── AddUdariPage.kt         # Add credit
│   │   ├── AddPaymentPage.kt       # Record payment
│   │   ├── CustomerLedgerScreen.kt # Customer history
│   │   ├── DailySummaryScreen.kt   # Day report
│   │   └── ProfilePage.kt          # Profile + Settings
│   ├── 📂 components/
│   │   └── Components.kt           # Reusable UI parts
│   └── 📂 theme/
│       └── Theme.kt                # Colors + Typography
│
├── 📂 viewmodel/
│   └── LedgerViewModel.kt          # UI state + logic
│
└── NammaSantheApp.kt               # Hilt Application
```

---

## 🚀 Getting Started

### Prerequisites

```bash
✅ Android Studio (Hedgehog or newer)
✅ JDK 17
✅ Android device or emulator (API 26+)
✅ Git
```

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/Madhu2150/Namma-Santhe-Ledger.git
```

**2. Open in Android Studio**
```bash
cd Namma-Santhe-Ledger
# Open Android Studio → Open → Select this folder
```

**3. Sync Gradle**
```text
File → Sync Project with Gradle Files
```

**4. Run the app**
```text
Click ▶ Run button
or
Press Shift + F10
```

---

## 📥 Download

<div align="center">

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?style=for-the-badge&logo=android)](https://github.com/Madhu2150/Namma-Santhe-Ledger/releases)

> **Build your own APK:**
> ```text
> Build → Build Bundle(s) / APK(s) → Build APK(s)
> ```

</div>

---

## 🔐 Permissions

```xml
<!-- Only one permission needed -->
<uses-permission android:name="android.permission.VIBRATE"/>

<!-- No internet permission — 100% offline ✅ -->
<!-- No storage permission — Room DB handles it ✅ -->
<!-- No camera permission — not needed ✅ -->
```

---

## 🌟 Key Highlights

<div align="center">

| | Feature | Detail |
|:---:|:---|:---|
| ⚡ | **Speed** | Add Udari in under 5 seconds |
| 📶 | **Offline First** | 100% works without internet |
| 🔒 | **Privacy** | All data stored on-device only |
| 🌐 | **Bilingual** | English + Kannada support |
| 🎨 | **Simple UI** | Designed for low-literacy users |
| 📱 | **Compatibility** | Android 8.0+ (covers 95% devices) |

</div>

---

## 🎯 Success Criteria

- [x] ✅ Total Outstanding Dues on Home Screen
- [x] ✅ Searchable customer ledger by name/village
- [x] ✅ 2-step Udari entry (Customer → Amount → Submit)
- [x] ✅ Payment recording with balance update
- [x] ✅ Daily Summary screen
- [x] ✅ WhatsApp reminder integration
- [x] ✅ Offline-first with Room DB persistence
- [x] ✅ Multi-language (English + Kannada)
- [x] ✅ GenAI smart overdue alerts

---

## 🤝 Contributing

Contributions are welcome! Here's how:

```bash
# 1. Fork the repository
# 2. Create your feature branch
git checkout -b feature/AmazingFeature

# 3. Commit your changes
git commit -m "Add AmazingFeature"

# 4. Push to branch
git push origin feature/AmazingFeature

# 5. Open a Pull Request
```

---

## 📄 License

```text
MIT License

Copyright (c) 2024 Madhu2150

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software.
```

---

## 👨‍💻 Author

<div align="center">

**Madhu L**

[![GitHub](https://img.shields.io/badge/GitHub-Madhu2150-black?style=for-the-badge&logo=github)](https://github.com/Madhu2150)

</div>

---

## 🙏 Acknowledgements

- [MindMatrix](https://mindmatrix.io) — VTU Internship Program
- [Android Jetpack](https://developer.android.com/jetpack) — UI toolkit
- [Hilt](https://dagger.dev/hilt/) — Dependency injection
- [Room](https://developer.android.com/training/data-storage/room) — Database

---

<div align="center">

**⭐ Star this repo if you find it helpful!**

```text
🌿 Built for Bharat
Empowering the smallest entrepreneur at every Santhe
```

![Visitors](https://visitor-badge.laobi.icu/badge?page_id=Madhu2150.Namma-Santhe-Ledger)

</div>
