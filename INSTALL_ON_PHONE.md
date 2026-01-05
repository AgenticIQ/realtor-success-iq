# Installing App on Your Phone & GitHub Integration

## Method 1: Direct USB Install (Easiest)

### Step 1: Enable Developer Mode on Your Phone
1. Go to **Settings** → **About Phone**
2. Find **Build Number** (might be under Software Information)
3. Tap **Build Number** 7 times
4. You'll see "You are now a developer!"

### Step 2: Enable USB Debugging
1. Go to **Settings** → **Developer Options** (now visible)
2. Enable **USB Debugging**
3. Enable **Install via USB** (if available)

### Step 3: Connect Phone to Computer
1. Connect phone via USB cable
2. On your phone, tap **Allow USB Debugging** when prompted
3. Check "Always allow from this computer" if you want

### Step 4: Install from Android Studio
1. Open the project in Android Studio
2. Your phone should appear in the device dropdown (top toolbar)
3. Select your phone
4. Click **Run** (green play button)
5. App will build and install automatically!

### Step 5: Run App
- App opens automatically on your phone
- You can now test on real device
- Future runs: just click Run, no need to reconnect

## Method 2: Build APK and Install Manually

### Step 1: Build APK
1. In Android Studio: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait for build to complete
3. Click **locate** in the notification
4. APK file is in: `app/build/outputs/apk/debug/app-debug.apk`

### Step 2: Transfer to Phone
**Option A: USB**
- Connect phone via USB
- Copy APK file to phone's Downloads folder

**Option B: Email/Cloud**
- Email APK to yourself
- Or upload to Google Drive/Dropbox
- Download on phone

### Step 3: Install on Phone
1. On phone, open **Files** app
2. Navigate to Downloads (or where you saved APK)
3. Tap the APK file
4. Tap **Install**
5. If prompted about "Unknown sources", allow it
6. App installs!

## Method 3: GitHub Integration + CI/CD

### Step 1: Create GitHub Repository

```bash
# In your project folder
cd C:\Users\josh\Documents\Real_Estate_IQ

# Initialize git (if not already done)
git init

# Create .gitignore (already exists, but verify)
# Make sure it includes:
# - build/
# - .gradle/
# - local.properties
# - *.apk
# - *.aab

# Add files
git add .

# Commit
git commit -m "Initial commit - Realtor Success IQ Android app"

# Create repo on GitHub.com, then:
git remote add origin https://github.com/YOUR_USERNAME/realtor-success-iq.git
git branch -M main
git push -u origin main
```

### Step 2: Set Up GitHub Actions for Auto-Build

Create `.github/workflows/build-android.yml`:

```yaml
name: Build Android APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch: # Allows manual trigger

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        distribution: 'temurin'
        java-version: '17'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build APK
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### Step 3: Download APK from GitHub
1. Push code to GitHub
2. Go to **Actions** tab in GitHub
3. Click on the latest workflow run
4. Download the APK artifact
5. Install on phone (Method 2, Step 3)

## Method 4: Firebase App Distribution (Recommended for Testing)

### Step 1: Set Up Firebase
1. Go to https://console.firebase.google.com
2. Create/select project
3. Add Android app
4. Download `google-services.json` (already have placeholder)

### Step 2: Install Firebase CLI
```bash
npm install -g firebase-tools
firebase login
```

### Step 3: Configure App Distribution
1. In Firebase Console: **App Distribution** → **Get Started**
2. Add testers (your email)
3. Build APK (Method 2, Step 1)
4. Upload via Firebase CLI:

```bash
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app YOUR_APP_ID \
  --groups "testers"
```

### Step 4: Install on Phone
1. Check email for Firebase App Distribution invite
2. Click link
3. Install app directly from Firebase

## Method 5: GitHub + GitHub Mobile App

### Step 1: Install GitHub Mobile App
- Download from Play Store
- Sign in to your account

### Step 2: Access Your Repo
- Open GitHub app
- Navigate to your repo
- View code, issues, etc.

### Step 3: Download APK (if uploaded)
- You can upload APK as release
- Download via GitHub mobile app
- Install on phone

## Quick Comparison

| Method | Ease | Speed | Best For |
|--------|------|-------|----------|
| USB Direct | ⭐⭐⭐⭐⭐ | Fastest | Daily development |
| Manual APK | ⭐⭐⭐⭐ | Fast | Quick testing |
| GitHub Actions | ⭐⭐⭐ | Medium | Automated builds |
| Firebase | ⭐⭐⭐⭐ | Medium | Team testing |
| GitHub Mobile | ⭐⭐ | Slow | Viewing code only |

## Recommended Setup

**For Personal Testing:**
1. Use **Method 1 (USB Direct)** for daily testing
2. Use **Method 2 (Manual APK)** for sharing with others

**For Team/Production:**
1. Set up **GitHub repository** (Method 3)
2. Use **Firebase App Distribution** (Method 4) for beta testing
3. Use **Google Play Internal Testing** for production

## GitHub Integration Benefits

✅ **Version Control**: Track all changes
✅ **Backup**: Code safely stored
✅ **Collaboration**: Share with team
✅ **CI/CD**: Auto-build on push
✅ **Releases**: Tag versions
✅ **Issues**: Track bugs/features

## Setting Up Git (If Not Done)

```bash
# In project folder
git init

# Configure (if first time)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Add remote
git remote add origin https://github.com/YOUR_USERNAME/realtor-success-iq.git

# First push
git add .
git commit -m "Initial commit"
git push -u origin main
```

## Troubleshooting

### Phone Not Detected
- Try different USB cable
- Try different USB port
- Enable "File Transfer" mode on phone
- Install phone drivers (Windows)

### "Unknown Source" Error
- Settings → Security → Enable "Install Unknown Apps"
- Or Settings → Apps → Special Access → Install Unknown Apps

### Build Fails
- Check Android Studio logs
- Verify JDK 17 installed
- Sync Gradle files
- Clean project: Build → Clean Project

### GitHub Push Fails
- Check internet connection
- Verify GitHub credentials
- Use Personal Access Token if 2FA enabled

## Next Steps

1. **Today**: Set up USB debugging, install on phone
2. **This Week**: Create GitHub repo, push code
3. **Next Week**: Set up GitHub Actions for auto-builds
4. **Future**: Set up Firebase App Distribution for team

---

**Pro Tip**: Keep USB debugging enabled and phone connected while developing. You can click Run in Android Studio and instantly see changes on your phone!

