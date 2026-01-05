# Realtor Success OS — Android Accountability App (One-Shot Build Spec)
Version: 1.0 (Secure MVP → Commercial-ready foundation)  
Timezone default: America/Vancouver  
Target user: Solo REALTOR® (single-user focus), returning to consistent daily prospecting + listing appointments.
Name: RealtorSuccessIQ

---

## 0) Product Vision
Build an Android app that turns a proven “Listings-First Operating System” into daily execution:
- **Daily lead-gen time block**
- **Calls + conversations tracked**
- **Appointments + listings tracked**
- **Weekly scorecard + review**
- **Gamified rewards (stars, streaks, levels)**
- **Smart “Next Best Action” suggestions from CRM**
- **One-tap calling with dual-SIM preference**
- **Secure sign-in (Google Sign-In preferred; email/password supported)**
- **CRM connectors (MVP: Follow Up Boss; architecture ready for Salesforce & other top CRMs)**

The app must feel:
- **Secure**
- **Simple**
- **Attractive**
- **Dopamine-friendly**
- **Obsessively usable for one Realtor**

---

## 1) The Method (embedded in app copy)
### Non-negotiables
1. Listings first
2. 2 hours/day lead gen (minimum)
3. Phone-first (conversations > texts)
4. Weekly scorecard review
5. Only 2 lead pillars for 90 days

### Default targets (editable)
Daily:
- Lead Gen: 120 minutes
- Calls: 10
- Texts: 10
- Appointment Asks: 1

Weekly:
- Conversations: 50
- Appointments Set: 5
- Listing Appointments: 2
- Local Presence: 1
- Partner Touches: 2

90-day targets (editable):
- Listing appointments: 12–20
- Signed listings: 4–8

---

## 2) UI/UX Design System (must implement)
### Design goals
- “Apple-clean” but Android-native: **Material 3 + Jetpack Compose**
- One-handed use, big targets, low cognitive load
- Data is shown as “scoreboard” + “next actions” (not a CRM clone)

### Layout & navigation
- Bottom navigation (5 tabs):
  1) **Today**
  2) **Leads**
  3) **Score**
  4) **Review**
  5) **Settings**
- Top app bar with:
  - Title
  - Quick sync status chip (Connected / Syncing / Offline / Rate-limited)
  - Profile icon

### Visual components (Compose)
- **Progress Rings** for daily targets (Lead Gen, Calls, Texts, Ask)
- **Card stack** for “Next Best Actions” (10 cards)
- **Streak banner** (subtle, at top of Today)
- **Star burst animation** on key wins (toggleable)
- **Confetti** only on major wins (appointment set, listing signed)

### Typography
- Use Material 3 default type, with these sizes:
  - Screen title: 24sp
  - Section headers: 16–18sp
  - Body: 14–16sp
  - Buttons: 14–16sp

### Spacing
- 8dp grid system
- Cards: 16dp padding
- Tap targets: minimum 48dp height

### Color guidance (Success-themed, but professional)
Default theme = “Success Minimal”
- Primary (Success Green): `#16A34A`
- Secondary (Deep Navy): `#0B1220`
- Accent (Gold): `#D4AF37`
- Background: `#F8FAFC`
- Surface: White / near-white

Why: green = progress/success, navy = trust/authority, gold = reward.

### Customizable color scheme
In Settings:
- Theme mode: Light / Dark / System
- Palette:
  - Default “Success Minimal”
  - “Trust Blue”
  - “Modern Charcoal”
  - “Coastal” (subtle teal)
- Custom primary color picker (store as ARGB)
- Use Material 3 dynamic color (Material You) *optional toggle*

Accessibility:
- Ensure contrast passes WCAG AA for text on backgrounds.

---

## 3) Authentication & Security (must be real)
### Requirements
- Secure sign-in and user session
- Simple: “Sign in with Google” is default path
- Optional: Email + password (username/password style) for users who don’t want Google
- Support biometric re-auth to open the app quickly after initial sign-in

### Implementation choice (recommended)
Use **Firebase Authentication**:
- Google Sign-In provider (primary)
- Email/password (secondary)
- Biometric gate (optional app lock)
- User data in Firestore (cloud sync) + Room (offline-first cache)

### Security rules
- Store secrets/tokens ONLY in secure storage:
  - Android Keystore + EncryptedSharedPreferences for local-only secrets
- Never log secrets
- All network via HTTPS
- Add “Privacy Mode” toggle (hides contact names on Today screen until unlocked)

### CRM token handling (MVP vs Commercial)
MVP (fast prototype):
- Store CRM tokens/API keys locally in EncryptedSharedPreferences

Commercial-ready path (recommended):
- Store CRM tokens in backend (Firebase Cloud Functions + Firestore)
- Client requests a short-lived token to call CRM proxy
- This prevents leaking CRM keys from device backups or reverse engineering

---

## 4) CRM Integrations — Research & Roadmap
### The top CRMs commonly used by realtors (North America) to plan connectors for
(These appear repeatedly in 2024–2025 “best CRM for real estate” roundups and industry coverage.)

**Core “top list” to support (5–10):**
1) **Follow Up Boss** (MVP connector)  
   - Frequently listed for brokerages/teams; known for integrations and real-estate focus.  
   - Source examples: Zapier list + other comparisons.

2) **BoomTown**  
   - Real estate platform with CRM + lead gen/IDX; widely referenced in real estate tech stacks.  
   - Source examples: Zapier list; BoomTown product.

3) **CINC**  
   - “All-in-one” lead gen + CRM platform for teams/agents.  
   - Source examples: Zapier list; CINC product.

4) **Lofty** (formerly Chime in some market discussions; also appears as its own CRM brand)  
   - Often positioned as all-in-one CRM/marketing platform.

5) **Sierra Interactive**  
   - Often paired with IDX + CRM workflows; appears in “all-in-one” choices.

6) **Real Geeks**  
   - Common small-team CRM + website/lead capture ecosystem.

7) **Wise Agent**  
   - Frequently listed as budget-friendly CRM for Realtors.

8) **Top Producer**  
   - Long-standing real-estate CRM with MLS integration positioning.

9) **BoldTrail** (Inside Real Estate ecosystem; connected to the kvCORE lineage/branding discussions)  
   - Industry coverage identifies it as a major ecosystem and rebrand in Inside Real Estate portfolio.

10) **LionDesk / Lone Wolf Relationships** (often referenced as a mid-range realtor CRM option in roundups)

**Enterprise / “general CRM” support:**
- **Salesforce** (explicit ask)
- Propertybase is Salesforce-based in some comparisons (useful for real estate teams)

#### Sources (human-readable references)
- Zapier “9 best CRMs for real estate agents” includes: Follow Up Boss, Sierra Interactive, CINC, Lofty, Top Producer, Real Geeks, Wise Agent, BoomTown.  
  https://zapier.com/blog/crm-for-real-estate/
- Ringover comparison includes Wise Agent, Follow Up Boss, Propertybase (Salesforce-based), IXACT Contact, Real Geeks, etc.  
  https://www.ringover.com/blog/real-estate-crm-software
- Inman industry coverage references Follow Up Boss competing with BoldTrail, Chime, Lone Wolf, etc.  
  https://www.inman.com/2025/12/02/in-latest-industry-hook-up-follow-up-boss-and-rechat-target-industry-power-users/
- Inside Real Estate press release on BoldTrail ecosystem.  
  https://resources.insiderealestate.com/press-releases/inside-real-estate-announces-boldtrail-uniting-product-portfolio-and-providing-enhanced-solutions-and-a-seamless-end-to-end-ecosystem
- HousingWire coverage of BoldTrail portfolio rebrand.  
  https://www.housingwire.com/articles/inside-real-estate-rebrands-product-portfolio-as-boldtrail/
- Follow Up Boss API docs (Auth + endpoints).  
  https://docs.followupboss.com/reference/authentication  
  https://docs.followupboss.com/reference/identity  
  https://docs.followupboss.com/reference/calls-post  
  https://docs.followupboss.com/reference/notes-post  
  https://docs.followupboss.com/reference/tasks-post
- Salesforce official REST API OAuth/Connected App docs.  
  https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_oauth_and_connected_apps.htm

---

## 5) Integration Architecture (must design now)
### Goal
Support many CRMs without rewriting the app.

### Connector interface
Create an interface `CrmConnector` with implementations:
- `FollowUpBossConnector` (MVP)
- `SalesforceConnector` (stub in MVP, real in Phase 2)
- Others (BoomTown, CINC, Lofty, etc.) as stubs or future

#### Required methods
- `validateConnection(): ConnectionStatus`
- `syncDownContacts(cursor): SyncResult`
- `syncDownTasks(cursor): SyncResult`
- `pushCallLog(callLog): PushResult`
- `pushNote(note): PushResult`
- `pushTask(task): PushResult`
- `searchContacts(query): List<Contact>`
- `getContactById(id): Contact?`

### OAuth and API key handling
- Support two auth modes per connector:
  - API Key (where available, like FUB Basic Auth)
  - OAuth2 (Salesforce uses Connected App + OAuth flows per docs)
- Token storage:
  - MVP: EncryptedSharedPreferences
  - Commercial: backend token vault + token exchange

### Sync engine
- Offline-first:
  - All actions logged locally into `ActivityLog`
  - A background worker pushes queued items when network is available
- Backoff:
  - exponential backoff on failures
  - special handling for rate limiting / 429 (show “sync delayed”)

---

## 6) MVP Features (must build)
### A) Today: execution cockpit
Shows:
- Lead Gen timer (120m target)
- Calls, Texts, Asks counters (tap to add)
- “Next Best Actions” list (top 10)
- Streak / stars status
- “Start Lead Gen” big button

Interactions:
- Start timer → “Focus Mode” option:
  - dims non-essential UI
  - shows one goal: call the next person
- Tap a suggestion card:
  - Call
  - Text
  - Log conversation
  - Set follow-up date
  - Add note
  - Mark appointment ask complete

### B) One-tap calling + dual SIM preference (must be best-effort)
Settings:
- Preferred outgoing SIM/account (if device supports listing PhoneAccounts)
Calling:
- Use ACTION_CALL if permission granted
- Otherwise ACTION_DIAL
After returning to app:
- Prompt for call outcome + note + follow up task creation

Hard rule:
- Never crash if SIM selection fails; fallback gracefully.

### C) Leads tab: lightweight, not a full CRM
- Search
- Filters:
  - Segment A/B/C
  - Overdue tasks
  - Past clients
  - Top 50
  - “Not contacted in X days”
- Lead detail:
  - call/text
  - timeline of interactions
  - quick “Add Win” buttons

### D) Score tab: dopamine + truth
- Daily score (stars earned)
- Weekly scoreboard (targets vs actual)
- Streaks
- Badges

### E) Weekly Review tab
Auto-generated weekly summary + 3 prompts:
1) What worked?
2) What didn’t?
3) What’s the ONE focus next week?

### F) Gamification (MVP scoring)
Stars:
- Call attempt: +1
- Conversation: +3
- Appointment ask: +5
- Appointment set: +15
- Listing appointment: +25
- Listing signed: +100
- Weekly review completed: +20

Badges:
- “50 Conversations Week”
- “2 Listing Appts Week”
- “10 Day Reconnect Sprint”
- “Lead Gen Streak x7”

Rewards:
- Confetti / sound toggles
- Optional custom rewards list

---

## 7) Suggestion Engine (“Next Best Action”)
### Inputs
- Overdue tasks (highest priority)
- Segment A/B/C
- Days since last contact
- Tags: Past Client, Top 50, Hot, Builder Partner
- Recent attempts (retry window)

### Scoring (example)
score =
- +50 overdue task
- +30 segment A
- +15 segment B
- +5 segment C
- +min(25, daysSinceContact)
- +15 tag “Past Client”
- +15 tag “Top 50”
- -10 contacted within last 3 days

Return top 10 with “why now” explanation.

---

## 8) Follow Up Boss (MVP connector) — concrete requirements
### Auth
- Support Basic Auth via API key (per FUB docs)

### Endpoints to use
- GET `/identity` (validate credentials)
- GET `/people` (sync contacts)
- GET `/tasks` + POST `/tasks` (follow-ups)
- POST `/calls` (log call)
- POST `/notes` (log note)

### Sync schedule
- On app open: if `lastSyncAt > 6h`, sync down
- After action logged: queue sync worker in 1–3 minutes
- Manual “Sync now” in Settings

---

## 9) Salesforce (Phase 2) — plan now, stub in MVP
### Auth
- Connected App + OAuth2 authorization flow (per Salesforce docs)
- Use external browser-based auth (PKCE recommended)

### Data mapping
Salesforce objects (typical):
- Leads, Contacts, Tasks, Events, Calls (Activities)
- Map to app’s internal `Contact`, `Task`, `ActivityLog`

### MVP stub requirement
Implement `SalesforceConnector` with:
- fake “connect” screen
- mock contacts/tasks
- code structure ready to drop in OAuth and REST calls later

---

## 10) Architecture (Android)
- Kotlin + Jetpack Compose + Material 3
- MVVM (ViewModel + StateFlow)
- Room (offline cache + truth for activity logs)
- Retrofit + OkHttp (network)
- Hilt (DI)
- WorkManager (background sync + reminders)
- Firebase Auth + Firestore (optional but recommended for secure login + sync)
- EncryptedSharedPreferences (token/key storage in MVP)

---

## 11) Data Model (Room entities)
### UserSettings
- timezone
- targets (daily/weekly)
- theme selection + custom primary color
- preferredPhoneAccountId
- crmProvider (enum)
- crmAuth (API_KEY / OAUTH)
- lastSyncAt
- privacyMode (bool)
- focusModeEnabled (bool)

### Contact
- id (provider id)
- name
- phone
- email
- tags[]
- segment A/B/C
- lastContactedAt
- score (float)
- rawJson

### Task
- id
- personId
- dueAt
- type
- status
- title
- notes

### ActivityLog (source of truth for accountability)
- id (uuid)
- type (CALL_ATTEMPT, CONVERSATION, TEXT_SENT, APPT_ASK, APPT_SET, LISTING_APPT, LISTING_SIGNED, LEAD_GEN_SESSION, WEEKLY_REVIEW)
- timestamp
- personId (nullable)
- durationSeconds (nullable)
- outcome (nullable)
- notes (nullable)
- starsAwarded
- synced (bool)
- providerRefs (nullable)

### Rewards
- id
- badgeType
- unlockedAt
- metadata

---

## 12) Notifications & Reminders (MVP)
- Daily “Lead Gen start” reminder
- Nudge: “You’re 5 calls away”
- Weekly Friday: “Weekly Review due”
- All adjustable or disableable

---

## 13) “Realtor-only” scope (important)
This is NOT a general task app.
Everything is Realtor workflow-focused:
- Calls, conversations, appts, listings
- CRM suggestions
- Weekly review cadence
No unrelated productivity features.

---

## 14) Acceptance Criteria (MVP = done)
1) Secure login works:
   - Google Sign-In
   - Email/password
   - Session persists; optional biometric gate
2) Today screen usable one-handed:
   - timer, counters, next actions
3) Suggestion engine produces top 10
4) Calling works with graceful dual-SIM best effort
5) Activity logging works offline and syncs later
6) Follow Up Boss connector:
   - validates identity
   - syncs contacts + tasks
   - pushes calls + notes + tasks
7) Gamification works (stars, streaks, badges)
8) Weekly review generates summary + saves prompts
9) Theme customization works (presets + custom primary)

---

## 15) Commercialization readiness (design now)
- Multi-user team mode (later)
- Subscription gating (later)
- Brokerage-branded themes (later)
- Coach dashboards (later)
- Content library (book/course built into app) (later)

END SPEC
