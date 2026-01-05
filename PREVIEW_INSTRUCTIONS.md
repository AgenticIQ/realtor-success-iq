# How to Preview the App

## Quick Start - Preview in Android Studio

### Step 1: Open the Project
1. Open **Android Studio** (Hedgehog 2023.1.1 or later recommended)
2. Click **File → Open**
3. Navigate to: `C:\Users\josh\Documents\Real_Estate_IQ`
4. Click **OK** to open the project

### Step 2: Sync Gradle
1. Android Studio will automatically prompt to sync Gradle
2. Click **Sync Now** if prompted
3. Wait for Gradle sync to complete (may take a few minutes on first run)

### Step 3: Set Up an Emulator (or Use Physical Device)

#### Option A: Create Android Emulator
1. Click **Tools → Device Manager**
2. Click **Create Device**
3. Select a device (e.g., **Pixel 7**)
4. Click **Next**
5. Select a system image (e.g., **API 34 - Android 14**)
6. Click **Download** if needed, then **Next**
7. Click **Finish**

#### Option B: Use Physical Device
1. Enable **Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap **Build Number** 7 times
2. Enable **USB Debugging**:
   - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Accept the debugging prompt on your device

### Step 4: Run the App
1. Select your emulator/device from the device dropdown (top toolbar)
2. Click the **Run** button (green play icon) or press **Shift + F10**
3. Wait for the app to build and install
4. The app will launch automatically

## Previewing Features

### Agent Mode (Default)
1. On the sign-in screen, tap **"Demo Mode Login"**
2. You'll see the main app with 5 tabs:
   - **Today**: Lead gen timer, progress rings, next actions
   - **Leads**: Contact list
   - **Score**: Stars, streaks, badges
   - **Review**: Weekly review prompts
   - **Settings**: App configuration

### Admin Mode (Broker Portal)
1. On the sign-in screen, tap **"Admin Login"** (top right)
2. For MVP, enter any email and password (e.g., `admin@brokerage.com` / `password`)
3. If no brokerage exists, you'll need to create one first (see below)

### Creating a Test Brokerage (First Time)
Since this is MVP, you may need to create a brokerage manually. Here's how:

1. **Option 1: Use Demo Data Initializer**
   - The app should auto-create demo data on first launch
   - Check the logs if it doesn't work

2. **Option 2: Create via Code (Temporary)**
   - You can add a test brokerage by modifying `DataInitializer.kt`
   - Or use Android Studio's Database Inspector (see below)

## Using Android Studio Tools

### Database Inspector (View Data)
1. Run the app
2. Click **View → Tool Windows → App Inspection**
3. Select **Database Inspector**
4. Expand `realtor_success_db`
5. Browse tables: `brokerages`, `agents`, `leaderboard_entries`, `prizes`, etc.

### Layout Inspector (View UI)
1. Run the app
2. Click **View → Tool Windows → Layout Inspector**
3. Select your running app
4. Inspect the UI hierarchy and properties

### Logcat (View Logs)
1. Click **View → Tool Windows → Logcat**
2. Filter by tag: `RealtorSuccessIQ`
3. See app logs, errors, and debug messages

## Quick Test Scenarios

### Test Agent Features
1. **Today Screen**:
   - Tap "Start" on Lead Gen Timer
   - Tap "Call" on a suggestion card
   - Watch progress rings update

2. **Score Screen**:
   - View your stars and streaks
   - Check badges earned

3. **Review Screen**:
   - Fill out weekly review prompts
   - Tap "Save Review"

### Test Admin Features
1. **Create Brokerage** (if needed):
   - Sign in as admin
   - System should create brokerage automatically

2. **Add Agents**:
   - Go to Agents tab
   - Tap + button
   - Add test agents

3. **Create Prize**:
   - Go to Prizes tab
   - Tap + button
   - Create a weekly prize for "most calls"

4. **View Leaderboard**:
   - Go to Leaderboards tab
   - Select metric and period
   - Tap "Refresh Leaderboard"

5. **Customize Branding**:
   - Go to Branding tab
   - Upload logo (placeholder for MVP)
   - Adjust colors
   - See preview

## Troubleshooting

### App Won't Build
- **Check Gradle Sync**: File → Sync Project with Gradle Files
- **Check JDK**: File → Project Structure → SDK Location (should be JDK 17)
- **Invalidate Caches**: File → Invalidate Caches → Invalidate and Restart

### App Crashes on Launch
- **Check Logcat** for error messages
- **Verify Hilt**: Make sure `@HiltAndroidApp` is on Application class
- **Check Database Migration**: Database version should be 2

### Can't See Admin Login Button
- Make sure you're on the sign-in screen
- The button should be in the top-right corner
- If missing, check MainActivity.kt for the admin mode toggle

### Database Migration Issues
- If you get migration errors, you may need to:
  1. Uninstall the app from device/emulator
  2. Reinstall (this will create fresh database with v2 schema)

## Preview Checklist

- [ ] Project opens in Android Studio
- [ ] Gradle syncs successfully
- [ ] Emulator/device is connected
- [ ] App builds without errors
- [ ] App launches on device/emulator
- [ ] Can sign in with Demo Mode
- [ ] Can see Today screen with suggestions
- [ ] Can access Admin Login
- [ ] Can sign in as admin
- [ ] Can see admin dashboard
- [ ] Can add agents
- [ ] Can create prizes
- [ ] Can view leaderboards
- [ ] Can customize branding

## Next Steps

Once you can preview the app:
1. Test all agent features
2. Test all admin features
3. Create test data (agents, prizes)
4. Verify leaderboard calculations
5. Test branding customization
6. Check database with Database Inspector

## Need Help?

- Check `README.md` for general setup
- Check `BROKER_ADMIN_FEATURES.md` for admin feature details
- Check `App_Develpment_Spec.md` for full specification
- Review Logcat for runtime errors
- Use Android Studio's built-in debugging tools

