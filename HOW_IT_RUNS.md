# How the App Runs

## Native Android App (Not Web/Server-Based)

This is a **native Android application**, not a web app. It does **NOT** run on localhost or in a browser.

### How It Actually Works

1. **Runs on Android Device/Emulator**
   - The app runs directly on an Android emulator (virtual device) or physical Android phone/tablet
   - No web server needed
   - No localhost URL
   - No browser required

2. **Local Database (Embedded)**
   - Uses **Room Database** (SQLite) - embedded in the app
   - Data is stored locally on the device
   - No separate database server
   - No network connection required for basic features

3. **Offline-First Architecture**
   - All data stored locally
   - Works completely offline
   - Syncs with CRM when online (optional)

## Where You See It

### Android Studio Emulator
- Virtual Android device on your computer
- Looks like a phone screen
- Runs the app directly

### Physical Android Device
- Connect via USB
- App installs and runs on your phone
- Works like any other Android app

## Comparison

| Web App | This Android App |
|---------|------------------|
| Runs in browser | Runs on Android device |
| Needs web server | No server needed |
| localhost:3000 | N/A (not applicable) |
| HTML/CSS/JS | Kotlin/Compose |
| Database on server | Database in app |

## What You'll See

When you run the app:
1. Android Studio launches an emulator (virtual phone)
2. The app installs on the emulator
3. The app opens automatically
4. You interact with it like a phone app (taps, swipes, etc.)

## If You Want Web/Server Version

If you need a web version that runs on localhost:
- That would be a separate project (web app)
- Would need a backend server (Node.js, Python, etc.)
- Would need a web frontend (React, Vue, etc.)
- Current project is Android-only

## Development Tools

While developing, you can use:
- **Android Studio**: Main IDE
- **Emulator**: Virtual Android device
- **Logcat**: View app logs (not a server log)
- **Database Inspector**: View local database (not a remote DB)

## Network Features (Optional)

The app CAN connect to external services:
- **Firebase Auth**: For authentication (cloud service)
- **Follow Up Boss API**: For CRM sync (external API)
- **WorkManager**: Background sync (runs on device)

But these are optional - the app works fully offline with local data.

## Summary

- ❌ Does NOT run on localhost
- ❌ Does NOT need a web server
- ❌ Does NOT run in a browser
- ✅ Runs on Android emulator/device
- ✅ Uses local embedded database
- ✅ Works offline
- ✅ Native Android app

