# RealtorSuccessIQ — Requests, Change Log, and Current Spec (Single Source of Truth)

This file is a **living reference** of what you requested, what changed over time, and the **latest consolidated spec** we are implementing.  
If something is missing here, it will likely be missing in the app—so we keep this up to date.

## Original V1 Requested App (baseline)
- **Platform / stack**: Android, Kotlin, Jetpack Compose, Material 3, MVVM + StateFlow, Hilt, Room, WorkManager.
- **Offline-first**: local Room storage; background sync.
- **Auth**:
  - Firebase Auth (Google Sign-In + Email/Password)
  - Demo Mode fallback
- **CRM integration**:
  - Follow Up Boss connector (real API; demo connector for mock)
  - Key entry in Settings
- **Navigation**: Today, Leads, Score, Review, Settings.
- **Suggestion engine**: Next Best Actions from CRM + scoring.
- **Gamification**: stars, streaks, badges, leaderboards.
- **Theming**: presets + configurable branding.
- **README** and build instructions.

## Chronological Change Requests (what you asked for, in order)
### 1) “Broker level admin” interface
- Brokerage can upload custom branding.
- Leaderboards for agents (calls, booked appts).
- Prize management (weekly gift cards, annual trip, etc).

### 2) “Get it on my phone and update easily”
- GitHub integration + CI builds.
- In-app download/install button (simple update flow).
- Fix “app not installed” via consistent signing + versioning.

### 3) App icon update
- Add a small white line-drawing house inside the green icon.

### 4) CRM focus filters in-app
- Pull-down / multi-select for **tags** and **stages** from the user’s CRM.
- Filtering should affect Leads + suggestions (and ideally score/review metrics).
- Must pull **all tags/stages** (not just a small subset).

### 5) V2 / V3 roadmap and “implement it all”
Key items you approved/asked to implement:
- Default segmentation: **Hot / Warm / Cold**.
- “Texts don’t count as conversations” should be selectable.
- Success Plan system: default plan + editable steps + accountability + progress.
- Broker admin improvements and onboarding.
- UX polish and build reliability.

### 6) Latest explicit fixes requested (Jan 7, 2026)
- Admin login should not be a dead-end:
  - **Register/setup brokerage** flow (name, address, phone, etc).
  - Ability to return to agent login from admin login screen.
- UI color:
  - Remove “pink”; use **soft green** with **high contrast** text.
  - Theme selection in Settings should actually apply to the app.
- Review screen:
  - Bottom button should not be compressed; should adapt to device size.
- Settings security:
  - API key should be masked (****) after save; reveal via eye icon + **password gate** (device credential/biometric).
- CRM filters:
  - Tags/stages list is incomplete → must fetch all pages from Follow Up Boss.
  - Selecting tags/stages must actually filter the leads list.

## Current “Two App” Release Strategy (required)
We maintain two installable apps:
- **Stable (v1)**: package `com.realtorsuccessiq`, release channel `nightly`
- **NEXT (v2)**: package `com.realtorsuccessiq.next`, release channel `nightly-next`

Both must compile and publish in CI. NEXT may add features without breaking Stable.

## LATEST CONSOLIDATED SPEC (build against this)
### Identity / versions
- Stable and NEXT are **separate installable apps** with clearly visible version + flavor in Settings.
- CI produces `realtorsuccessiq-nightly.apk` and `realtorsuccessiq-next.apk`.
- Builds are **always signed with the same keystore** so in-app updates work.

### UI / Theme
- Default palette: **soft green** with high contrast.
- Theme selection in Settings updates the entire app immediately.

### Admin portal
- Admin screen has:
  - **Sign in** + **Register brokerage** tabs
  - Register requires brokerage name + admin email + password; optionally phone + address
  - **Back to Agent Login** button

### CRM + filters
- Follow Up Boss sync:
  - Fetch contacts via cursor pagination (all pages).
  - Tags/stages in Settings are derived from all synced contacts.
- Selecting focus tags/stages:
  - Filters **Leads** list immediately.
  - Filters suggestions (Today), and analytics (Score/Review) consistently.

### Security
- API key masked by default; reveal requires device credential/biometric.

### Success plan (NEXT)
- NEXT includes Success Plan tab with adjustable targets and selectable “texts count as conversations”.

## Implementation Status (maintained by assistant)
- This section is updated after each batch of changes to show what is complete vs pending.
- **Pending right now**: verify on-device that theme selection + filters are visibly taking effect; add visible “flavor/version” indicator so Stable vs NEXT cannot be confused.


