# Realtor Success IQ - Android App

A comprehensive Android accountability app for real estate professionals, built with Kotlin, Jetpack Compose, and Material 3.

## Features

- **Daily Lead Gen Timer**: Track your daily lead generation time blocks
- **Activity Tracking**: Log calls, texts, conversations, and appointments
- **Smart Suggestions**: AI-powered "Next Best Actions" from your CRM
- **Gamification**: Earn stars, maintain streaks, and unlock badges
- **Weekly Reviews**: Reflect on what worked and plan next week's focus
- **CRM Integration**: Connect with Follow Up Boss (and more coming)
- **Offline-First**: All data stored locally with background sync
- **Customizable Themes**: Multiple color presets + custom primary color
- **Demo Mode**: Run the app immediately without any setup

## Quick Start (Demo Mode)

The app runs in **Demo Mode** by default - no configuration needed!

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or device
4. Tap "Demo Mode Login" on the sign-in screen
5. Start using the app with mock data

## Project Structure

```
app/
├── src/main/java/com/realtorsuccessiq/
│   ├── data/
│   │   ├── crm/              # CRM connector interfaces and implementations
│   │   ├── dao/              # Room DAOs
│   │   ├── database/         # Room database setup
│   │   ├── model/            # Data models (Contact, Task, ActivityLog, etc.)
│   │   ├── network/          # Retrofit API interfaces
│   │   └── repository/       # Repository pattern implementation
│   ├── di/                   # Hilt dependency injection modules
│   ├── ui/
│   │   ├── auth/             # Authentication screens
│   │   ├── navigation/       # Navigation setup
│   │   ├── screens/          # All app screens (Today, Leads, Score, Review, Settings)
│   │   └── theme/            # Material 3 theme system
│   ├── util/                 # Utility classes (Gamification, Suggestion Engine)
│   ├── worker/               # WorkManager background workers
│   └── MainActivity.kt
```

## Setting Up Firebase Authentication (Optional)

To enable real Firebase authentication:

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app to your project
3. Download `google-services.json`
4. Place it in `app/` directory (replacing the placeholder)
5. Add your app's SHA-1 fingerprint to Firebase Console
   - Get SHA-1: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`
6. Rebuild and run

The app will automatically detect Firebase configuration and enable email/password and Google Sign-In.

## Connecting Follow Up Boss CRM

1. Go to Settings screen
2. Turn off "Demo Mode"
3. Select "Followupboss" as CRM Provider
4. Enter your Follow Up Boss API Key
5. The app will validate and sync contacts/tasks

### Getting Your Follow Up Boss API Key

1. Log in to Follow Up Boss
2. Go to Settings > Integrations > API
3. Generate or copy your API key
4. Paste it in the app's Settings screen

## Architecture

- **MVVM**: ViewModels manage UI state with StateFlow
- **Repository Pattern**: Clean separation between data sources
- **Offline-First**: Room database for local storage
- **Background Sync**: WorkManager handles periodic CRM sync
- **Dependency Injection**: Hilt for clean architecture
- **Material 3**: Modern, accessible UI components

## Data Models

- **UserSettings**: App configuration, targets, theme preferences
- **Contact**: CRM contacts with segments, tags, scores
- **Task**: Follow-up tasks and reminders
- **ActivityLog**: All logged activities (source of truth)
- **Reward**: Badges and achievements

## Gamification System

### Stars Awarded
- Call attempt: +1 star
- Conversation: +3 stars
- Appointment ask: +5 stars
- Appointment set: +15 stars
- Listing appointment: +25 stars
- Listing signed: +100 stars
- Weekly review: +20 stars

### Badges
- 50 Conversations Week
- 2 Listing Appts Week
- 10 Day Reconnect Sprint
- Lead Gen Streak 7/30 days
- Call Streak 7/30 days

## Theme System

The app supports multiple theme presets:
- **Success Minimal** (default): Green primary, navy secondary, gold accent
- **Trust Blue**: Professional blue palette
- **Modern Charcoal**: Dark, sophisticated theme
- **Coastal**: Teal-based coastal theme

Plus custom primary color picker and light/dark/system mode support.

## Broker Admin Portal

The app now includes a comprehensive broker-level admin interface for managing brokerages and their agents.

### Setting Up a Brokerage

1. **Create Brokerage Account**:
   - Tap "Admin Login" on the main sign-in screen
   - Sign in with brokerage admin email (for MVP, any email works)
   - First-time admins will need to create a brokerage profile

2. **Add Agents**:
   - Navigate to "Agents" tab
   - Tap the + button to add new agents
   - Enter agent name, email, and phone

3. **Set Up Prizes**:
   - Go to "Prizes" tab
   - Create prizes for weekly/monthly/annual competitions
   - Examples:
     - Weekly: "$100 restaurant gift card for most calls"
     - Monthly: "$500 cash for most appointments"
     - Annual: "Trip for 2 for top 10% of agents"

4. **Customize Branding**:
   - Upload brokerage logo
   - Set primary, secondary, and accent colors
   - Agents will see your branding in their app

5. **View Leaderboards**:
   - Real-time rankings by various metrics
   - Filter by period (weekly/monthly/annual)
   - Filter by metric (calls, appointments, listings, etc.)

### Prize Examples

- **Weekly Prizes**: Gift cards, small cash prizes, merchandise
- **Monthly Prizes**: Larger gift cards, experiences, cash bonuses
- **Annual Prizes**: Trips, significant cash prizes, top 10% recognition
- **Top 10%**: Automatic qualification for annual prizes

### Leaderboard Metrics

- Total Calls
- Conversations
- Appointments Set
- Listing Appointments
- Listings Signed
- Total Stars
- Lead Gen Minutes
- Streak Days

## Known Limitations

1. **SIM Selection**: Dual SIM preference is best-effort and depends on device support
2. **Salesforce Connector**: Stub implementation in MVP; full OAuth2 coming in Phase 2
3. **Call Permissions**: Uses ACTION_DIAL by default (no permission needed); CALL_PHONE requires runtime permission
4. **Firebase**: App gracefully falls back to Demo Mode if Firebase is not configured
5. **Admin Auth**: MVP uses simple email-based auth; production should use secure authentication
6. **Logo Upload**: Currently uses placeholder; full file upload integration needed for production

## Building the Project

### Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 26+ (minSdk), 34 (targetSdk)

### Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

### Gradle Commands
```bash
./gradlew build          # Build the project
./gradlew assembleDebug  # Build debug APK
./gradlew installDebug   # Install on connected device
```

## Testing

The app includes:
- Unit test structure (add tests in `test/` directory)
- UI test structure (add tests in `androidTest/` directory)

Run tests:
```bash
./gradlew test           # Unit tests
./gradlew connectedAndroidTest  # UI tests
```

## Contributing

This is a prototype MVP. Future enhancements:
- Full Salesforce OAuth2 implementation
- Additional CRM connectors (BoomTown, CINC, etc.)
- Team/multi-user support
- Subscription gating
- Brokerage branding
- Coach dashboards

## License

This project is proprietary. All rights reserved.

## Support

For issues or questions, please refer to the development spec document.

---

**Built with ❤️ for real estate professionals**

