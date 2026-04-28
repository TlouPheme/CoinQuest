# CoinQuest

CoinQuest is a modern Android application designed to help users track their finances, set goals, and gain insights into their spending habits. Built with Kotlin and the latest Android development practices, it provides a secure and intuitive way to manage personal finances locally.

## Features

- **Transaction Tracking**: Easily record income and expenses with titles, amounts, and categories.
- **Category Summary**: View aggregated totals by category for both income and expenses.
- **Period Filtering**: Filter transactions and summaries by custom date ranges using a Material Date Range Picker.
- **Financial Goals**: Set monthly minimum income goals and maximum expense limits with visual status indicators.
- **Financial Health Score**: Get a quick overview of your financial health based on your savings ratio.
- **Image Attachments**: Attach photos of receipts or relevant images to your transactions.
- **Local Storage**: All data is stored securely on-device using a Room database, ensuring privacy and offline availability.
- **Category Normalization**: Automatic cleaning of category inputs to ensure consistent reporting.

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) pattern with Coroutines and Flow for reactive data handling.
- **Database**: Room Persistence Library for local data management.
- **UI Components**: Material Design 3, RecyclerView, ConstraintLayout.
- **Asynchronous Programming**: Kotlin Coroutines and Lifecycle-aware components.
- **Image Handling**: Modern Activity Result API for gallery access.

## Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- Android SDK 24+ (Android 7.0 Nougat)

### Installation

1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or a physical device.

## Project Structure

- `com.example.coinquest.data`: Contains database entities (Transaction, User, Goal), DAOs, and the AppDatabase configuration.
- `com.example.coinquest`: Contains Activities (UI) and Adapters for managing lists.
- `res/layout`: XML layout files for all screens and list items.

## Database Schema

The app uses three main tables:
1. `transactions`: Stores individual financial records.
2. `users`: Stores user credentials for local authentication.
3. `goals`: Stores financial targets and limits.

## License

This project is for educational purposes. All rights reserved.
