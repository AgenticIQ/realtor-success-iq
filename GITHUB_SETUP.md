# GitHub Setup Guide

## Quick Start: Push to GitHub

### Step 1: Create GitHub Repository

1. Go to https://github.com
2. Click **+** → **New repository**
3. Name: `realtor-success-iq` (or your preferred name)
4. Description: "Android accountability app for real estate professionals"
5. Choose **Public** or **Private**
6. **Don't** initialize with README (we already have one)
7. Click **Create repository**

### Step 2: Initialize Git (If Not Done)

Open terminal/command prompt in your project folder:

```bash
cd C:\Users\josh\Documents\Real_Estate_IQ

# Check if git is initialized
git status

# If not initialized:
git init
```

### Step 3: Configure Git (First Time Only)

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### Step 4: Add Remote and Push

```bash
# Add GitHub as remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/realtor-success-iq.git

# Or if using SSH:
git remote add origin git@github.com:YOUR_USERNAME/realtor-success-iq.git

# Stage all files
git add .

# Commit
git commit -m "Initial commit - Realtor Success IQ Android app with broker admin features"

# Push to GitHub
git branch -M main
git push -u origin main
```

### Step 5: Verify

1. Go to your GitHub repository
2. You should see all your files
3. Check that `.github/workflows/build-android.yml` exists

## Using GitHub Actions (Auto-Build APK)

### How It Works

1. **Push code** to GitHub
2. **GitHub Actions** automatically builds APK
3. **Download APK** from Actions tab
4. **Install on phone**

### Steps

1. **Push your code** (already done above)
2. Go to **Actions** tab in GitHub
3. Wait for workflow to complete (green checkmark)
4. Click on the workflow run
5. Scroll down to **Artifacts**
6. Download **app-debug-apk**
7. Install on phone!

### Manual Trigger

You can also trigger builds manually:
1. Go to **Actions** tab
2. Select **Build Android APK** workflow
3. Click **Run workflow**
4. Select branch (main)
5. Click **Run workflow**

## Daily Workflow

### Making Changes

```bash
# Make your code changes in Android Studio

# Stage changes
git add .

# Commit
git commit -m "Description of changes"

# Push to GitHub
git push
```

### Getting APK After Push

1. Wait 2-3 minutes for build
2. Go to **Actions** tab
3. Download latest APK
4. Install on phone

## GitHub Mobile App

### Install GitHub Mobile

1. Download from Play Store: **GitHub**
2. Sign in with your account
3. Navigate to your repository

### What You Can Do

- ✅ View code
- ✅ View commits
- ✅ View issues
- ✅ View releases
- ✅ Download releases/artifacts (if configured)
- ❌ Can't build APK directly (use Actions instead)

### Accessing APK from Mobile

1. Push code to GitHub
2. Wait for Actions to build
3. Open GitHub mobile app
4. Go to **Actions** tab
5. Download APK artifact
6. Install on phone

## Best Practices

### .gitignore

Already configured! Includes:
- Build files
- Local properties
- APK files
- IDE files

### Commit Messages

Use clear messages:
```bash
git commit -m "Add broker admin dashboard"
git commit -m "Fix leaderboard calculation bug"
git commit -m "Update branding screen UI"
```

### Branch Strategy

```bash
# Main branch (production-ready)
git checkout -b main

# Feature branches
git checkout -b feature/prize-management
git checkout -b bugfix/leaderboard-sorting
```

### Releases

Tag important versions:
```bash
git tag -a v1.0.0 -m "First release with broker admin"
git push origin v1.0.0
```

Then create release on GitHub:
1. Go to **Releases** → **Create a new release**
2. Tag: `v1.0.0`
3. Upload APK
4. Publish!

## Troubleshooting

### Push Fails - Authentication

**Option 1: Personal Access Token**
1. GitHub → Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Select scopes: `repo`
4. Copy token
5. Use as password when pushing

**Option 2: SSH Keys**
```bash
# Generate SSH key
ssh-keygen -t ed25519 -C "your.email@example.com"

# Add to GitHub
# Settings → SSH and GPG keys → New SSH key
# Paste public key (~/.ssh/id_ed25519.pub)

# Change remote to SSH
git remote set-url origin git@github.com:YOUR_USERNAME/realtor-success-iq.git
```

### Build Fails in Actions

- Check Actions logs
- Verify `gradlew` has execute permission
- Check JDK version (should be 17)
- Verify all dependencies in `build.gradle`

### Can't Find APK Artifact

- Wait for workflow to complete
- Check if build succeeded (green checkmark)
- Artifacts expire after 30 days (configurable)

## Security Notes

### What NOT to Commit

- ❌ Real `google-services.json` (use placeholder)
- ❌ API keys in code (use environment variables)
- ❌ Keystore files
- ❌ Local.properties with paths

### What IS Safe

- ✅ Placeholder `google-services.json` (for demo mode)
- ✅ Code structure
- ✅ Build configuration
- ✅ Documentation

## Next Steps

1. ✅ Push code to GitHub
2. ✅ Set up GitHub Actions
3. ✅ Test auto-build
4. ✅ Download APK from Actions
5. ✅ Install on phone
6. 🎉 Enjoy testing on real device!

---

**Pro Tip**: Set up GitHub Actions once, then every push automatically builds a new APK you can download and test!
