Continuing the SMS functionality document from where it was cut off:

```markdown
d:\thunai\SMS_FUNCTIONALITY.md
# 📱 SMS Functionality — Complete Technical Guide

> **Document Type:** SMS System Deep Dive
> **Covers:** How SMS works end-to-end, free approaches, architecture, and code
> **Companion:** VISION.md (full system), work_flow.md (website flow)

---

## Table of Contents

1. [The Reality of "Free" SMS](#1-the-reality-of-free-sms)
2. [SMS Architecture Options](#2-sms-architecture-options)
3. [Option A: Android Phone as SMS Gateway (100% Free)](#3-option-a-android-phone-as-sms-gateway-100-free)
4. [Option B: Jasmin SMS Gateway + SMPP Provider](#4-option-b-jasmin-sms-gateway--smpp-provider)
5. [Option C: Free Tier SMS APIs (Development)](#5-option-c-free-tier-sms-apis-development)
6. [Option D: USSD Gateway (Alternative to SMS)](#6-option-d-ussd-gateway-alternative-to-sms)
7. [Recommended Hybrid Approach](#7-recommended-hybrid-approach)
8. [Complete SMS Flow — Step by Step](#8-complete-sms-flow--step-by-step)
9. [Inbound SMS Processing Pipeline](#9-inbound-sms-processing-pipeline)
10. [Outbound SMS Response Pipeline](#10-outbound-sms-response-pipeline)
11. [Session Management in SMS](#11-session-management-in-sms)
12. [SMS Command Parser — Full Logic](#12-sms-command-parser--full-logic)
13. [Multi-Tenant SMS Routing](#13-multi-tenant-sms-routing)
14. [SMS Template Engine](#14-sms-template-engine)
15. [Rate Limiting & Security](#15-rate-limiting--security)
16. [Cost Comparison](#16-cost-comparison)
17. [Setup Guide — Free Development Environment](#17-setup-guide--free-development-environment)

---

## 1. The Reality of "Free" SMS

### Important Truth

```
┌────────────────────────────────────────────────────────────────────────┐
│                    HOW SMS ACTUALLY WORKS                               │
│                                                                        │
│  SMS messages travel through TELECOM NETWORKS (SMSC).                  │
│  Every SMS has a cost somewhere in the chain:                          │
│                                                                        │
│  Customer Phone ──► Cell Tower ──► SMSC ──► Cell Tower ──► Your Server│
│                                                                        │
│  The SMSC (Short Message Service Center) is operated by                │
│  telecom companies. They charge for SMS delivery.                      │
│                                                                        │
│  ⚠️  You CANNOT send/receive SMS over the internet for free            │
│      to real phone numbers without SOME telecom connection.            │
│                                                                        │
│  BUT there are ways to minimize cost or use free tiers:                │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### What IS Free vs What ISN'T

| Component | Free? | Explanation |
|-----------|-------|-------------|
| SMS Gateway Software (Jasmin, Kannel) | ✅ YES | Open-source software is free |
| Your Java microservices processing SMS | ✅ YES | Your code is free |
| Android phone as SMS gateway | ✅ YES* | Uses your SIM's SMS plan |
| SIM card SMS plan | ✅ YES* | Many plans include unlimited SMS |
| SMPP connection to telecom | ❌ NO | Telecom charges per SMS |
| Twilio/Vonage API | ❌ NO | Charges per SMS (but free trials exist) |
| Short code (e.g., 555123) | ❌ NO | Monthly rental fee |
| Long number (regular phone) | ✅ YES* | Just needs a SIM with SMS plan |

### The 4 Ways to Get "Free" or Near-Free SMS

```
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  OPTION A: Android Phone as Gateway     ← CHEAPEST (truly free)        │
│  ──────────────────────────────────                                      │
│  Cost: $0 (uses existing SIM's unlimited SMS plan)                      │
│  How: Android phone + app acts as SMS gateway                           │
│  Limit: ~200 SMS/hour, needs physical phone                             │
│  Best for: Development, small stores, single tenant                     │
│                                                                          │
│  OPTION B: Jasmin + SMPP Provider       ← PRODUCTION GRADE             │
│  ──────────────────────────────────                                      │
│  Cost: $0.001-$0.01 per SMS (bulk rates)                               │
│  How: Open-source Jasmin gateway + SMPP connection to telecom           │
│  Limit: 1000+ SMS/second possible                                       │
│  Best for: Production, multi-tenant, high volume                        │
│                                                                          │
│  OPTION C: Free Tier APIs               ← DEVELOPMENT ONLY             │
│  ──────────────────────────────────                                      │
│  Cost: Free up to limit (Twilio: $15 credit, Africa's Talking: sandbox) │
│  How: REST API to send SMS, webhook to receive                          │
│  Limit: Trial messages only, limited numbers                            │
│  Best for: Development and testing                                      │
│                                                                          │
│  OPTION D: USSD Gateway                 ← REAL-TIME INTERACTIVE        │
│  ──────────────────────────────────                                      │
│  Cost: Varies by country, sometimes free for businesses                 │
│  How: USSD codes like *123# — real-time session, no character limit    │
│  Limit: Country-specific, needs telecom agreement                       │
│  Best for: Regions with strong USSD adoption (Africa, South Asia)       │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 2. SMS Architecture Options

### Architecture Comparison

```
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  ┌─── OPTION A: Android Gateway ────────────────────────────────────┐   │
│  │                                                                   │   │
│  │  Customer Phone          Android Phone           Your Server      │   │
│  │  (any phone)             (with SIM)              (Java backend)   │   │
│  │                                                                   │   │
│  │  📱 "MENU"                                                       │   │
│  │    │                                                              │   │
│  │    │ SMS via telecom                                              │   │
│  │    ▼                                                              │   │
│  │  📱 Android Phone                                                 │   │
│  │    │                                                              │   │
│  │    │ Android App (SmsGateway App)                                 │   │
│  │    │ reads incoming SMS                                           │   │
│  │    │                                                              │   │
│  │    │ HTTP POST /webhook                                           │   │
│  │    │ (WiFi/USB tethering)                                         │   │
│  │    ▼                                                              │   │
│  │  💻 Notification Service                                          │   │
│  │    │                                                              │   │
│  │    │ Process command                                              │   │
│  │    │ Build response                                               │   │
│  │    │                                                              │   │
│  │    │ HTTP POST /send                                              │   │
│  │    │ (tell Android to send reply)                                 │   │
│  │    ▼                                                              │   │
│  │  📱 Android Phone                                                 │   │
│  │    │                                                              │   │
│  │    │ SMS via telecom                                              │   │
│  │    ▼                                                              │   │
│  │  📱 Customer receives: "🍕 PIZZA PALACE..."                      │   │
│  │                                                                   │   │
│  │  COST: $0 (uses SIM's SMS plan)                                   │   │
│  │  SETUP: Install Android app, connect to server via WiFi           │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─── OPTION B: Jasmin + SMPP ──────────────────────────────────────┐   │
│  │                                                                   │   │
│  │  Customer Phone          Telecom SMSC        Jasmin Gateway       │   │
│  │  (any phone)             (Carrier)           (Your server)        │   │
│  │                                                                   │   │
│  │  📱 "MENU"                                                       │   │
│  │    │                                                              │   │
│  │    │ SMS via telecom network                                      │   │
│  │    ▼                                                              │   │
│  │  📡 SMSC (Telecom)                                               │   │
│  │    │                                                              │   │
│  │    │ SMPP Protocol (TCP port 2775)                                │   │
│  │    │ (requires SMPP account with telecom/aggregator)              │   │
│  │    ▼                                                              │   │
│  │  🔧 Jasmin SMS Gateway                                           │   │
│  │    │                                                              │   │
│  │    │ HTTP Webhook                                                 │   │
│  │    ▼                                                              │   │
│  │  💻 Notification Service                                          │   │
│  │    │                                                              │   │
│  │    │ Process + Build response                                     │   │
│  │    │                                                              │   │
│  │    │ Jasmin HTTP API (port 8990)                                  │   │
│  │    ▼                                                              │   │
│  │  🔧 Jasmin → SMPP → SMSC → Customer Phone                       │   │
│  │                                                                   │   │
│  │  COST: ~$0.005/SMS (bulk telecom rates)                          │   │
│  │  SETUP: Jasmin Docker + SMPP account                              │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─── OPTION C: Free Tier API (Twilio example) ─────────────────────┐   │
│  │                                                                   │   │
│  │  Customer Phone          Twilio Cloud         Your Server          │   │
│  │                                                                   │   │
│  │  📱 "MENU"                                                       │   │
│  │    │                                                              │   │
│  │    │ SMS via telecom                                              │   │
│  │    ▼                                                              │   │
│  │  ☁️ Twilio SMSC                                                   │   │
│  │    │                                                              │   │
│  │    │ HTTP Webhook (to your server's URL)                          │   │
│  │    ▼                                                              │   │
│  │  💻 Notification Service (webhook endpoint)                       │   │
│  │    │                                                              │   │
│  │    │ Process + Build response                                     │   │
│  │    │                                                              │   │
│  │    │ Twilio REST API (POST /Messages)                             │   │
│  │    ▼                                                              │   │
│  │  ☁️ Twilio → telecom → Customer Phone                            │   │
│  │                                                                   │   │
│  │  COST: Free trial ($15 credit), then ~$0.0079/SMS                │   │
│  │  SETUP: Twilio account + configure webhook URL                    │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Option A: Android Phone as SMS Gateway (100% Free)

### How It Works

This is the **simplest and truly free** approach. You use an Android phone with a SIM card that has an unlimited (or large) SMS plan. An Android app on the phone acts as a bridge between the telecom network and your Java backend.

```
┌──────────────────────────────────────────────────────────────────────────┐
│              ANDROID PHONE AS SMS GATEWAY — FULL FLOW                    │
│                                                                          │
│  PHYSICAL SETUP:                                                         │
│  ┌──────────────────────────────┐                                        │
│  │                              │        ┌──────────────────────┐        │
│  │    📱 Android Phone          │  WiFi  │   💻 Your Server     │        │
│  │                              │◄──────►│                      │        │
│  │  • SIM card with SMS plan    │  HTTP  │  Java Microservices  │        │
│  │  • SCE Gateway App installed │        │  + PostgreSQL        │        │
│  │  • Connected to same WiFi    │        │  + Redis             │        │
│  │    as your server            │        │                      │        │
│  │  • OR: USB tethering         │        │                      │        │
│  │                              │        │                      │        │
│  └──────────────────────────────┘        └──────────────────────┘        │
│                                                                          │
│  MULTIPLE PHONES = MULTIPLE TENANTS:                                     │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐                    │
│  │ Phone 1 │  │ Phone 2 │  │ Phone 3 │  │ Phone 4 │                    │
│  │ SIM:    │  │ SIM:    │  │ SIM:    │  │ SIM:    │                    │
│  │ +91-AAA │  │ +91-BBB │  │ +91-CCC │  │ +91-DDD │                    │
│  │         │  │         │  │         │  │         │                    │
│  │ Tenant: │  │ Tenant: │  │ Tenant: │  │ Tenant: │                    │
│  │ Pizza   │  │ Fresh   │  │ Quick   │  │ Style   │                    │
│  │ Palace  │  │ Market  │  │ Bites   │  │ Hub     │                    │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘                    │
│       │             │             │             │                        │
│       └─────────────┴─────────────┴─────────────┘                        │
│                     │                                                     │
│                     │ HTTP (same network)                                 │
│                     ▼                                                     │
│              ┌──────────────┐                                             │
│              │ SCE Backend  │                                             │
│              │ (all services)│                                            │
│              └──────────────┘                                             │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Android Gateway App — How It Works

```
┌──────────────────────────────────────────────────────────────────────────┐
│              ANDROID APP INTERNALS (SCE SMS Gateway App)                 │
│                                                                          │
│  This is a SIMPLE Android app (not part of your Java backend).           │
│  It's a bridge between the phone's SMS system and your backend.          │
│                                                                          │
│  ┌─ INCOMING SMS HANDLER ──────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │  1. Android BroadcastReceiver detects incoming SMS               │    │
│  │  2. Extract: sender phone number + message body                  │    │
│  │  3. HTTP POST to backend:                                        │    │
│  │                                                                  │    │
│  │     POST http://YOUR_SERVER:8084/api/v1/sms/webhook/inbound      │    │
│  │     {                                                            │    │
│  │       "from": "+919876543210",                                   │    │
│  │       "to": "+919988776655",    ← This is the phone's number     │    │
│  │       "body": "MENU",                                             │    │
│  │       "timestamp": "2026-06-21T12:30:00Z",                       │    │
│  │       "gateway_phone": "+919988776655"                           │    │
│  │     }                                                            │    │
│  │                                                                  │    │
│  └──────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─ OUTGOING SMS HANDLER ──────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │  1. Backend sends HTTP request to Android app:                   │    │
│  │                                                                  │    │
│  │     POST http://ANDROID_PHONE_IP:8080/send                       │    │
│  │     {                                                            │    │
│  │       "to": "+919876543210",                                     │    │
│  │       "body": "🍕 PIZZA PALACE\n1. Menu\n2. Special\n..."       │    │
│  │     }                                                            │    │
│  │                                                                  │    │
│  │  2. Android app uses SmsManager to send SMS:                    │    │
│  │     SmsManager.getDefault().sendTextMessage(to, null, body, ...) │    │
│  │                                                                  │    │
│  │  3. Reports delivery status back to backend:                     │    │
│  │     POST http://YOUR_SERVER:8084/api/v1/sms/webhook/delivery     │    │
│  │     { "messageId": "msg-123", "status": "DELIVERED" }            │    │
│  │                                                                  │    │
│  └──────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─ HEALTH CHECK ──────────────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │  • Heartbeat every 30s to backend                                │    │
│  │  • Reports: battery level, signal strength, SMS queue size       │    │
│  │  • Backend shows gateway health on admin dashboard               │    │
│  │                                                                  │    │
│  └──────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  NOTE: This Android app is a SEPARATE small project.                     │
│  It is NOT part of your Java microservices.                              │
│  It's a lightweight bridge app (~500 lines of Kotlin/Java).              │
│                                                                          │
│  Open-source Android SMS gateway apps that already exist:                │
│  • SMS Gateway API (github.com/capcom6/android-sms-gateway)             │
│  • Android SMS Gateway (github.com/ReactiveDroid/SMSGateway)            │
│  • TextBelt (self-hosted option)                                         │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Android Gateway App — Minimal Code Structure

```
android-gateway-app/                    (Separate Android project)
├── app/
│   ├── src/main/java/com/sce/gateway/
│   │   ├── MainActivity.kt            (App UI: connection status, logs)
│   │   ├── SmsReceiver.kt             (BroadcastReceiver for incoming SMS)
│   │   ├── SmsSender.kt               (Sends outgoing SMS via SmsManager)
│   │   ├── BackendClient.kt           (HTTP client to talk to your backend)
│   │   ├── HealthReporter.kt          (Sends heartbeat to backend)
│   │   ├── GatewayService.kt          (Foreground service, always running)
│   │   └── Config.kt                  (Server URL, auth token)
│   └── AndroidManifest.xml
└── build.gradle.kts
```

**Incoming SMS Handler (Kotlin):**

```kotlin
// SmsReceiver.kt — Receives incoming SMS and forwards to backend
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (message in messages) {
            val senderPhone = message.originatingAddress ?: continue
            val body = message.messageBody ?: continue

            // Forward to backend
            BackendClient.sendInboundSms(
                from = senderPhone,
                to = GatewayConfig.thisPhoneNumber,
                body = body,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}
```

**Outgoing SMS Sender (Kotlin):**

```kotlin
// SmsSender.kt — Sends SMS when backend requests
class SmsSender(private val context: Context) {

    fun sendSms(to: String, body: String, messageId: String) {
        val smsManager = SmsManager.getDefault()

        // Handle long messages (split into parts if > 160 chars)
        val parts = smsManager.divideMessage(body)

        val sentIntents = ArrayList<PendingIntent>()
        val deliveredIntents = ArrayList<PendingIntent>()

        for (i in parts.indices) {
            val sentIntent = PendingIntent.getBroadcast(
                context, messageId.hashCode() + i,
                Intent("SMS_SENT").putExtra("messageId", messageId),
                PendingIntent.FLAG_IMMUTABLE
            )
            val deliveredIntent = PendingIntent.getBroadcast(
                context, messageId.hashCode() + i + 1000,
                Intent("SMS_DELIVERED").putExtra("messageId", messageId),
                PendingIntent.FLAG_IMMUTABLE
            )
            sentIntents.add(sentIntent)
            deliveredIntents.add(deliveredIntent)
        }

        smsManager.sendMultipartTextMessage(
            to, null, parts, sentIntents, deliveredIntents
        )
    }
}
```

---

## 4. Option B: Jasmin SMS Gateway + SMPP Provider

### How Jasmin Works

```
┌──────────────────────────────────────────────────────────────────────────┐
│              JASMIN SMS GATEWAY — PRODUCTION SETUP                       │
│                                                                          │
│  Jasmin is an open-source SMS gateway that:                              │
│  1. Connects to telecom SMSCs via SMPP protocol                         │
│  2. Provides HTTP API for your app to send SMS                           │
│  3. Provides webhooks for your app to receive SMS                        │
│  4. Routes SMS between multiple providers and your app                   │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                     JASMIN SMS GATEWAY                            │    │
│  │                                                                   │    │
│  │  ┌─ Connectors ──────────────────────────────────────────────┐   │    │
│  │  │                                                            │   │    │
│  │  │  SMPP Client Connector ← Connects to Telecom SMSC         │   │    │
│  │  │    host: smpp.telecom-provider.com                         │   │    │
│  │  │    port: 2775                                               │   │    │
│  │  │    username: your_smpp_username                             │   │    │
│  │  │    password: your_smpp_password                             │   │    │
│  │  │    bind_type: transceiver (send + receive)                  │   │    │
│  │  │                                                            │   │    │
│  │  │  HTTP API (for your app to use)                            │   │    │
│  │  │    port: 8990                                               │   │    │
│  │  │    endpoints:                                               │   │    │
│  │  │      POST /send      → Send an SMS                         │   │    │
│  │  │      GET  /balance   → Check SMS balance                   │   │    │
│  │  │      GET  /rate      → Check SMS rate for a destination    │   │    │
│  │  │                                                            │   │    │
│  │  │  Web Dashboard                                              │   │    │
│  │  │    port: 8991                                               │   │    │
│  │  │    Manage connectors, routes, users, logs                   │   │    │
│  │  │                                                            │   │    │
│  │  └────────────────────────────────────────────────────────────┘   │    │
│  │                                                                   │    │
│  │  ┌─ Routing Rules ────────────────────────────────────────────┐   │    │
│  │  │                                                            │   │    │
│  │  │  Route 1: INBOUND (from telecom → your app)               │   │    │
│  │  │    When: SMS received on connector "telecom-smpp"          │   │    │
│  │  │    To: HTTP destination → http://YOUR_SERVER:8084/...      │   │    │
│  │  │                                                            │   │    │
│  │  │  Route 2: OUTBOUND (from your app → telecom)              │   │    │
│  │  │    When: HTTP POST /send received                          │   │    │
│  │  │    To: SMPP connector "telecom-smpp"                       │   │    │
│  │  │                                                            │   │    │
│  │  │  Route 3: MULTI-TENANT routing                             │   │    │
│  │  │    When: SMS to +91-AAA (Pizza Palace's number)            │   │    │
│  │  │    Route to: tenant "pizza-palace"                         │   │    │
│  │  │    When: SMS to +91-BBB (Fresh Market's number)            │   │    │
│  │  │    Route to: tenant "fresh-market"                         │   │    │
│  │  │                                                            │   │    │
│  │  └────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  GETTING AN SMPP CONNECTION:                                             │
│  ───────────────────────────                                             │
│  You need an SMPP account from a telecom aggregator:                     │
│                                                                          │
│  PROVIDERS WITH FREE/LOW-COST SMPP:                                      │
│  ├── Africa's Talking — Free sandbox, $0.008/SMS production             │
│  ├── Twilio — $15 free credit, then ~$0.0079/SMS                        │
│  ├── Vonage/Nexmo — Free trial, then ~$0.0068/SMS                       │
│  ├── Infobip — Free trial available                                      │
│  ├── MSG91 (India) — ₹0.15/SMS (~$0.002)                               │
│  ├── Kaleyra (India) — ₹0.12/SMS (~$0.0015)                            │
│  └── Direct telecom SMPP — Varies (cheapest at scale)                    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Jasmin Docker Setup

```yaml
# docker-compose.yml — Jasmin SMS Gateway
version: '3.9'

services:
  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  jasmin:
    image: jookies/jasmin:latest
    ports:
      - "2775:2775"    # SMPP protocol port
      - "8990:8990"    # HTTP API port
      - "8991:8991"    # Web dashboard port
    depends_on:
      - redis
    environment:
      REDIS_CLIENT_HOST: redis
    volumes:
      - jasmin-store:/var/jasmin/store
      - jasmin-logs:/var/log/jasmin
      - ./jasmin-config/etc/jasmin:/etc/jasmin

volumes:
  jasmin-store:
  jasmin-logs:
```

### Jasmin Configuration — Adding an SMPP Connector

```bash
# Connect to Jasmin CLI
telnet localhost 8990
# Username: jadmin
# Password: jpwd

# Add SMPP connector (connects to your telecom provider)
smppccm -a
cid telecom-connector
host smpp.provider.com
port 2775
username your_smpp_user
password your_smpp_pass
bind_operation transceiver
system_type SMPP
reconnect_on_connection_loss True
reconnect_delay 10

# Start the connector
smppccm -1 telecom-connector

# Add HTTP route (inbound SMS → your backend webhook)
mtrouter -a
type DefaultRoute
connector smppc(telecom-connector)
order 0

# Save configuration
persist
```

### Sending SMS via Jasmin HTTP API

```bash
# Send SMS from your Notification Service to Jasmin
curl -X POST "http://localhost:8990/send" \
  -u "jadmin:jpwd" \
  -d "to=+919876543210" \
  -d "from=+919988776655" \
  -d "content=🍕 PIZZA PALACE\n1. View Menu\n2. Today's Special\n3. My Cart\nReply with number" \
  -d "coding=8"
```

---

## 5. Option C: Free Tier SMS APIs (Development)

### Twilio Free Tier

```
┌──────────────────────────────────────────────────────────────────────────┐
│              TWILIO — FREE TRIAL SETUP                                    │
│                                                                          │
│  WHAT YOU GET FOR FREE:                                                  │
│  ├── $15.50 credit (enough for ~1,900 SMS in US)                        │
│  ├── 1 Twilio phone number (for receiving SMS)                          │
│  ├── Webhook support for inbound SMS                                     │
│  ├── REST API for outbound SMS                                           │
│  └── Delivery status callbacks                                           │
│                                                                          │
│  LIMITATIONS OF FREE TRIAL:                                              │
│  ├── SMS includes "Sent from your Twilio trial account" prefix           │
│  ├── Can only send to verified phone numbers                             │
│  ├── Cannot send to short codes or premium numbers                       │
│  └── Credit expires after 30 days of inactivity                          │
│                                                                          │
│  SETUP STEPS:                                                            │
│  1. Sign up at twilio.com/try-twilio                                    │
│  2. Verify your phone number                                             │
│  3. Get a Twilio phone number (free trial gives you one)                 │
│  4. Configure webhook URL for incoming SMS:                              │
│     Settings → Phone Numbers → Your Number → Messaging →                 │
│     "A MESSAGE COMES IN" → Webhook → https://YOUR_SERVER/api/v1/sms/... │
│  5. Get Account SID + Auth Token from Console Dashboard                  │
│                                                                          │
│  INTEGRATION WITH YOUR BACKEND:                                          │
│                                                                          │
│  Inbound (Customer → Your System):                                       │
│  Customer SMS → Twilio → HTTP POST webhook → Notification Service        │
│                                                                          │
│  Outbound (Your System → Customer):                                      │
│  Notification Service → Twilio REST API → Customer Phone                 │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Africa's Talking (Free Sandbox)

```
┌──────────────────────────────────────────────────────────────────────────┐
│              AFRICA'S TALKING — FREE SANDBOX                              │
│                                                                          │
│  WHAT YOU GET FOR FREE:                                                  │
│  ├── Free sandbox environment (no real SMS, simulated)                   │
│  ├── Full API access (SMS, USSD, Voice, Airtime)                        │
│  ├── Webhook for inbound messages                                        │
│  ├── Shared short code for testing                                       │
│  └── No credit card required                                             │
│                                                                          │
│  LIMITATIONS:                                                            │
│  ├── Sandbox only — no real SMS delivery                                 │
│  ├── Must simulate via their web dashboard                               │
│  ├── Production requires KYC + credit purchase                           │
│  └── Only works in African countries in production                       │
│                                                                          │
│  PRODUCTION COSTS (when ready):                                          │
│  ├── Kenya: KES 0.80/SMS (~$0.006)                                      │
│  ├── Nigeria: NGN 4.00/SMS (~$0.003)                                    │
│  ├── Uganda: UGX 60/SMS (~$0.016)                                       │
│  └── South Africa: ZAR 0.25/SMS (~$0.014)                               │
│                                                                          │
│  SETUP:                                                                  │
│  1. Sign up at account.africastalking.com                                │
│  2. Go to Sandbox (free, instant access)                                 │
│  3. Get API Key from dashboard                                           │
│  4. Configure callback URL for inbound SMS                                │
│  5. Use simulator to send test messages                                   │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Twilio Integration Code (Java — Notification Service)

```java
// SmsGatewayInterface.java — Abstract interface for any SMS provider
public interface SmsGatewayInterface {
    SmsSendResult sendSms(String to, String from, String body);
    void registerWebhook(String callbackUrl);
}

// TwilioGatewayClient.java — Twilio implementation
@Service
public class TwilioGatewayClient implements SmsGatewayInterface {

    @Value("${sce.sms.twilio.account-sid}")
    private String accountSid;

    @Value("${sce.sms.twilio.auth-token}")
    private String authToken;

    private TwilioRestClient client;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        this.client = Twilio.getRestClient();
    }

    @Override
    public SmsSendResult sendSms(String to, String from, String body) {
        Message message = Message.creator(
            new PhoneNumber(to),
            new PhoneNumber(from),
            body
        ).create();

        return SmsSendResult.builder()
            .messageId(message.getSid())
            .status(SmsStatus.SENT)
            .segments(message.getNumSegments())
            .build();
    }
}

// TwilioWebhookController.java — Receives inbound SMS from Twilio
@RestController
@RequestMapping("/api/v1/sms/webhook")
public class TwilioWebhookController {

    @Autowired
    private SmsReceiveService smsReceiveService;

    @PostMapping("/twilio")
    public String handleInboundSms(
            @RequestParam("From") String from,
            @RequestParam("To") String to,
            @RequestParam("Body") String body,
            @RequestParam("MessageSid") String messageSid) {

        InboundSms sms = InboundSms.builder()
            .from(from)
            .to(to)
            .body(body)
            .providerMessageId(messageSid)
            .timestamp(Instant.now())
            .build();

        smsReceiveService.processInboundSms(sms);

        // Twilio expects TwiML response (or empty 200 for no auto-reply)
        return "<Response></Response>";
    }
}
```

### application.properties — SMS Provider Configuration

```properties
# notification-service/src/main/resources/application.properties

# ═══ SMS Provider Selection ═══
# Options: ANDROID_GATEWAY, JASMIN, TWILIO, AFRICAS_TALKING
sce.sms.provider.active=ANDROID_GATEWAY

# ═══ Android Gateway Config (Option A - FREE) ═══
sce.sms.android-gateway.base-url=http://192.168.1.100:8080
sce.sms.android-gateway.auth-token=your-android-app-token
sce.sms.android-gateway.health-check-interval=30000

# ═══ Jasmin Config (Option B - Production) ═══
sce.sms.jasmin.base-url=http://localhost:8990
sce.sms.jasmin.username=jadmin
sce.sms.jasmin.password=${JASMIN_PASSWORD}

# ═══ Twilio Config (Option C - Development) ═══
sce.sms.twilio.account-sid=${TWILIO_ACCOUNT_SID}
sce.sms.twilio.auth-token=${TWILIO_AUTH_TOKEN}
sce.sms.twilio.phone-number=+15551234567

# ═══ Africa's Talking Config (Option C - Development) ═══
sce.sms.africas-talking.api-key=${AT_API_KEY}
sce.sms.africas-talking.username=sandbox
sce.sms.africas-talking.short-code=12345

# ═══ Rate Limiting ═══
sce.sms.rate-limit.inbound.per-phone.per-hour=20
sce.sms.rate-limit.outbound.per-tenant.per-hour=50
sce.sms.rate-limit.outbound.per-tenant.per-day=1000
```

---

## 6. Option D: USSD Gateway (Alternative to SMS)

### How USSD Works (Interactive Sessions)

```
┌──────────────────────────────────────────────────────────────────────────┐
│              USSD — REAL-TIME INTERACTIVE MENUS                          │
│                                                                          │
│  USSD (Unstructured Supplementary Service Data) is like SMS but:        │
│  ├── Real-time session (like a phone call, but text)                     │
│  ├── Interactive menu navigation (no need to remember commands)          │
│  ├── No character limit per "screen" (displays full menus)               │
│  ├── Works on ALL phones (even basic feature phones)                     │
│  ├── No internet required                                                │
│  └── Session times out after ~60 seconds of inactivity                   │
│                                                                          │
│  HOW CUSTOMER SEES USSD:                                                 │
│                                                                          │
│  Customer dials: *123#                                                   │
│                                                                          │
│  ┌─────────────────────────────────┐                                    │
│  │  📱 FEATURE PHONE SCREEN       │                                    │
│  │                                 │                                    │
│  │  ┌───────────────────────────┐  │                                    │
│  │  │ 🍕 PIZZA PALACE          │  │                                    │
│  │  │                           │  │                                    │
│  │  │ 1. View Menu              │  │                                    │
│  │  │ 2. Today's Special        │  │                                    │
│  │  │ 3. My Cart                │  │                                    │
│  │  │ 4. My Orders              │  │                                    │
│  │  │ 5. Store Info             │  │                                    │
│  │  │                           │  │                                    │
│  │  │ Reply with number         │  │                                    │
│  │  └───────────────────────────┘  │                                    │
│  │                                 │                                    │
│  │  [Type: 1] [Send]              │                                    │
│  └─────────────────────────────────┘                                    │
│                                                                          │
│  (Customer types "1" and presses Send)                                   │
│                                                                          │
│  ┌─────────────────────────────────┐                                    │
│  │  📱 FEATURE PHONE SCREEN       │                                    │
│  │                                 │                                    │
│  │  ┌───────────────────────────┐  │                                    │
│  │  │ 📋 OUR MENU               │  │   ← INSTANT response             │
│  │  │                           │  │      (no new SMS, same session)   │
│  │  │ 1. 🍕 Pizzas (12 items)   │  │                                    │
│  │  │ 2. 🍝 Pasta (6 items)     │  │                                    │
│  │  │ 3. 🥗 Salads (4 items)    │  │                                    │
│  │  │ 4. 🥤 Drinks (8 items)    │  │                                    │
│  │  │ 5. 🍰 Desserts (5 items)  │  │                                    │
│  │  │ 0. Back                   │  │                                    │
│  │  └───────────────────────────┘  │                                    │
│  │                                 │                                    │
│  │  [Type: 1] [Send]              │                                    │
│  └─────────────────────────────────┘                                    │
│                                                                          │
│  USSD vs SMS COMPARISON:                                                 │
│  ┌─────────────────────┬────────────────────┬─────────────────────┐     │
│  │ Feature             │ SMS                 │ USSD                │     │
│  ├─────────────────────┼────────────────────┼─────────────────────┤     │
│  │ Response Speed      │ 2-10 seconds        │ <1 second           │     │
│  │ Interactive Menu    │ Via commands         │ Native session      │     │
│  │ Session Persistence │ Redis (manual)       │ Telecom (auto)      │     │
│  │ Character Limit     │ 160 chars/SMS        │ ~182 chars/screen   │     │
│  │ Offline Support     │ ✅ (stored)          │ ❌ (needs signal)   │     │
│  │ Cost                │ Per SMS sent         │ Per session minute  │     │
│  │ Setup Complexity    │ Low                  │ High (telecom deal) │     │
│  │ Works on            │ All phones           │ All phones          │     │
│  │ Async (order notif) │ ✅                   │ ❌ (real-time only) │     │
│  └─────────────────────┴────────────────────┴─────────────────────┘     │
│                                                                          │
│  RECOMMENDATION:                                                         │
│  Use SMS as PRIMARY + USSD as OPTIONAL ENHANCEMENT                       │
│  SMS works for everything (ordering + notifications)                     │
│  USSD is a nice addition for real-time browsing but NOT required         │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Recommended Hybrid Approach

### Development → Production Strategy

```
┌──────────────────────────────────────────────────────────────────────────┐
│              RECOMMENDED APPROACH — PHASE BY PHASE                       │
│                                                                          │
│  ┌─ PHASE 1: DEVELOPMENT (Weeks 1-8) ─────────────────────────────┐    │
│  │                                                                   │    │
│  │  SMS Provider: Android Phone Gateway (Option A)                   │    │
│  │  Cost: $0                                                         │    │
│  │                                                                   │    │
│  │  Setup:                                                           │    │
│  │  ├── 1 Android phone with SIM (unlimited SMS plan)               │    │
│  │  ├── Install SCE Gateway App                                      │    │
│  │  ├── Connect to dev server via WiFi                               │    │
│  │  └── Test with real phones sending SMS                            │    │
│  │                                                                   │    │
│  │  Fallback: Twilio Free Trial ($15 credit)                         │    │
│  │  ├── Use when Android phone is not available                      │    │
│  │  └── Good for automated testing                                   │    │
│  │                                                                   │    │
│  │  Simulation Mode (NO real SMS needed):                            │    │
│  │  ├── Built-in simulator in Notification Service                   │    │
│  │  ├── Web UI to "send" fake inbound SMS                            │    │
│  │  ├── See what response would be sent                              │    │
│  │  └── Perfect for unit tests and CI/CD                             │    │
│  └───────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─ PHASE 2: BETA / SMALL SCALE (Weeks 9-18) ──────────────────────┐    │
│  │                                                                   │    │
│  │  SMS Provider: Multiple Android Phones (1 per tenant)             │    │
│  │  Cost: $0 (SIM SMS plans included)                                │    │
│  │                                                                   │    │
│  │  Setup:                                                           │    │
│  │  ├── 5-10 Android phones (1 per tenant/business)                  │    │
│  │  ├── Each phone has its own SIM + phone number                    │    │
│  │  ├── Phone number = Tenant identifier                             │    │
│  │  └── All connected to server via WiFi network                     │    │
│  │                                                                   │    │
│  │  Capacity: ~200 SMS/hour per phone = 1,000-2,000 SMS/hour total  │    │
│  │  Suitable for: 5-10 tenants with moderate traffic                 │    │
│  └───────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─ PHASE 3: PRODUCTION SCALE (Week 19+) ──────────────────────────┐    │
│  │                                                                   │    │
│  │  SMS Provider: Jasmin Gateway + SMPP Provider                     │    │
│  │  Cost: ~$0.002-$0.008 per SMS (varies by country)                │    │
│  │                                                                   │    │
│  │  Setup:                                                           │    │
│  │  ├── Jasmin SMS Gateway (Docker)                                  │    │
│  │  ├── SMPP account with telecom aggregator                         │    │
│  │  ├── Multiple virtual numbers (1 per tenant)                      │    │
│  │  └── Keep Android phones as backup                                │    │
│  │                                                                   │    │
│  │  Capacity: 1,000+ SMS/second                                      │    │
│  │  Suitable for: 100+ tenants, high traffic                         │    │
│  │                                                                   │    │
│  │  ALSO: Add USSD support for regions that support it               │    │
│  └───────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─ PROVIDER SWITCHING (Built into Notification Service) ──────────┐    │
│  │                                                                   │    │
│  │  The Notification Service uses a STRATEGY PATTERN:                │    │
│  │                                                                   │    │
│  │  SmsGatewayInterface (interface)                                  │    │
│  │    ├── AndroidGatewayClient   (Option A — free)                  │    │
│  │    ├── JasminGatewayClient    (Option B — production)            │    │
│  │    ├── TwilioGatewayClient    (Option C — dev/testing)           │    │
│  │    ├── AfricasTalkingClient   (Option C — Africa)               │    │
│  │    └── SimulatorGatewayClient (No real SMS — testing)            │    │
│  │                                                                   │    │
│  │  Switch providers without changing any business logic!            │    │
│  │  Per-tenant provider selection (tenant A uses Twilio,            │    │
│  │  tenant B uses Android phone, etc.)                               │    │
│  └───────────────────────────────────────────────────────────────────┘    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Complete SMS Flow — Step by Step

### Full Technical Flow (What Happens When Customer Sends "MENU")

```
STEP 1: Customer sends SMS
═══════════════════════════

  Customer Phone                        Telecom Network
  ┌────────────────┐                   ┌────────────────┐
  │                │  "MENU"           │                │
  │  Customer types │ ───────────────►  │  Cell Tower    │
  │  "MENU" and    │  SMS via radio     │                │
  │  presses Send  │                   └───────┬────────┘
  └────────────────┘                           │
                                               ▼
                                       ┌────────────────┐
                                       │  SMSC          │
                                       │  (Telecom's    │
                                       │   SMS Center)  │
                                       └───────┬────────┘
                                               │
                                               ▼
STEP 2: SMS arrives at your gateway
════════════════════════════════════

  (Option A: Android)           (Option B: Jasmin)
  ┌────────────────┐            ┌────────────────┐
  │ Android Phone  │            │ SMPP Connector │
  │ with SIM       │            │ in Jasmin      │
  │                │            │                │
  │ BroadcastReceiver│          │ deliver_sm PDU │
  │ detects new SMS│            │ received       │
  └───────┬────────┘            └───────┬────────┘
          │                             │
          │ HTTP POST                   │ Internal routing
          ▼                             ▼
  ┌────────────────────────────────────────────┐
  │  NOTIFICATION SERVICE (Port 8084)          │
  │  POST /api/v1/sms/webhook/inbound          │
  │                                            │
  │  {                                         │
  │    "from": "+919876543210",                │
  │    "to": "+919988776655",                  │
  │    "body": "MENU",                         │
  │    "timestamp": "2026-06-21T12:30:00Z"     │
  │  }                                         │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 3: Log inbound SMS
════════════════════════

  ┌────────────────────────────────────────────┐
  │  3a. Log raw SMS to database              │
  │      INSERT INTO notification_service      │
  │        .sms_messages (...)                 │
  │                                            │
  │  3b. Check rate limit (Redis)             │
  │      GET rate_limit:+919876543210:inbound  │
  │      → If > 20/hour → REJECT              │
  │                                            │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 4: Resolve tenant
════════════════════════

  ┌────────────────────────────────────────────┐
  │  Lookup: Which business does "+919988..."  │
  │  belong to?                                │
  │                                            │
  │  SELECT tenant_id                          │
  │  FROM shared.tenant_lookup                 │
  │  WHERE phone_number = '+919988776655'      │
  │                                            │
  │  Result: tenant_id = 'T-001'              │
  │          tenant_name = 'Pizza Palace'      │
  │                                            │
  │  SET app.current_tenant = 'T-001'         │
  │  (RLS activates for all subsequent queries)│
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 5: Resolve or create customer
═══════════════════════════════════

  ┌────────────────────────────────────────────┐
  │  SELECT * FROM customer_service.customers  │
  │  WHERE phone = '+919876543210'             │
  │    AND tenant_id = 'T-001'                 │
  │                                            │
  │  If FOUND:                                 │
  │    → Use existing customer profile         │
  │    → customer_id = 'C-042'                 │
  │                                            │
  │  If NOT FOUND:                             │
  │    → Create new customer                   │
  │    → INSERT INTO customers (...)           │
  │    → Send welcome message                  │
  │    → customer_id = 'C-NEW'                 │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 6: Get/Restore session state
══════════════════════════════════

  ┌────────────────────────────────────────────┐
  │  Check Redis for active session:           │
  │                                            │
  │  GET session:C-042:T-001                   │
  │                                            │
  │  If EXISTS:                                │
  │    {                                       │
  │      "state": "BROWSING_CATEGORY",         │
  │      "categoryId": "cat-pizzas",           │
  │      "pageNumber": 1,                      │
  │      "depth": 2,                           │
  │      "lastActivity": "2026-06-21T12:28:00" │
  │    }                                       │
  │    → Customer was in the middle of browsing│
  │    → Use this context to interpret command │
  │                                            │
  │  If NOT EXISTS (or expired > 30 min):      │
  │    → Fresh session                         │
  │    → Interpret "MENU" as main menu request │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 7: Parse the SMS command
══════════════════════════════

  ┌────────────────────────────────────────────┐
  │  SmsCommandParser.parse("MENU", context)   │
  │                                            │
  │  Input:  "MENU"                            │
  │  Context: { fresh session, tenant T-001 }  │
  │                                            │
  │  Parsed result:                            │
  │  {                                         │
  │    commandType: MAIN_MENU,                 │
  │    rawText: "MENU",                        │
  │    confidence: 1.0                         │
  │  }                                         │
  │                                            │
  │  Other examples:                           │
  │  "1"              → NAVIGATE(option=1)     │
  │  "ADD 3 M-T1"     → ADD_TO_CART(...)      │
  │  "CART"           → VIEW_CART              │
  │  "CHECKOUT"       → CHECKOUT               │
  │  "SEARCH pizza"   → SEARCH(keyword=pizza)  │
  │  "TRACK ORD-0042" → TRACK_ORDER(...)       │
  │  "0"              → GO_BACK                │
  │  "*"              → MAIN_MENU              │
  │  "#"              → HELP                   │
  │  "N"              → NEXT_PAGE              │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 8: Route to appropriate service
══════════════════════════════════════

  ┌────────────────────────────────────────────┐
  │  SessionRouter routes based on command:    │
  │                                            │
  │  MAIN_MENU ──► Fetch menu tree config      │
  │                from Tenant Service          │
  │                                            │
  │  NAVIGATE ───► Based on current session:   │
  │                If at MAIN_MENU → show cats  │
  │                If at CATEGORY → show prods  │
  │                If at PRODUCT → customize    │
  │                                            │
  │  ADD_TO_CART ─► Order Service              │
  │  VIEW_CART ───► Order Service              │
  │  CHECKOUT ────► Order Service              │
  │  TRACK_ORDER ─► Order Service              │
  │  SEARCH ──────► OpenSearch + Tenant Svc    │
  │  HELP ─────────► Return help text          │
  │                                            │
  │  All service calls via RabbitMQ events:    │
  │  → Publish event to RabbitMQ               │
  │  → Target service processes and responds   │
  │  → Notification Service builds SMS reply   │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 9: Build SMS response
════════════════════════════

  ┌────────────────────────────────────────────┐
  │  SmsResponseBuilder builds the reply:      │
  │                                            │
  │  Input:                                    │
  │  - Command: MAIN_MENU                      │
  │  - Tenant: Pizza Palace (T-001)           │
  │  - Customer: C-042 (Rahul)                │
  │  - Session: fresh                          │
  │                                            │
  │  Processing:                               │
  │  1. Load SMS template for MAIN_MENU        │
  │  2. Load tenant's menu tree configuration  │
  │  3. Substitute variables:                  │
  │     {{store_name}} → "Pizza Palace"        │
  │     {{store_phone}} → "+919988776655"      │
  │     {{customer_name}} → "Rahul"            │
  │  4. Add clickable links (sms: URIs)        │
  │  5. Check character count (max 160/SMS)    │
  │  6. If > 160 chars → split into parts      │
  │                                            │
  │  Output:                                   │
  │  "👋 Welcome to Pizza Palace!              │
  │   1. 📋 View Menu                          │
  │   2. ⭐ Today's Special                    │
  │   3. 🛒 My Cart                            │
  │   4. 📦 My Orders                          │
  │   5. ℹ️ Store Info                         │
  │   Reply with a number"                     │
  │                                            │
  │  (148 characters — fits in 1 SMS ✅)       │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 10: Update session state
══════════════════════════════

  ┌────────────────────────────────────────────┐
  │  Save new session state to Redis:          │
  │                                            │
  │  SET session:C-042:T-001 '{                │
  │    "state": "MAIN_MENU",                   │
  │    "depth": 0,                             │
  │    "pageNumber": 1,                        │
  │    "lastActivity": "2026-06-21T12:30:00",  │
  │    "history": []                           │
  │  }' EX 1800                                │
  │                                            │
  │  EX 1800 = expires in 30 minutes           │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 11: Send SMS reply
═════════════════════════

  ┌────────────────────────────────────────────┐
  │  SmsSendService sends reply:               │
  │                                            │
  │  1. Log outbound SMS to database           │
  │     INSERT INTO sms_messages (...)          │
  │                                            │
  │  2. Send via active gateway:               │
  │                                            │
  │  (Option A) POST to Android Gateway:       │
  │    http://192.168.1.100:8080/send          │
  │    { to: "+919876543210",                  │
  │      body: "👋 Welcome to Pizza Palace..."}│
  │                                            │
  │  (Option B) POST to Jasmin:                │
  │    http://localhost:8990/send              │
  │    to=+919876543210&from=+919988776655     │
  │    &content=👋 Welcome to Pizza Palace...  │
  │                                            │
  │  (Option C) Twilio API:                    │
  │    POST /2010-04-01/.../Messages.json      │
  │    To=+919876543210&From=+15551234567      │
  │    &Body=👋 Welcome to Pizza Palace...     │
  │                                            │
  └─────────────────────┬──────────────────────┘
                        │
                        ▼
STEP 12: Customer receives SMS
═══════════════════════════════

  ┌────────────────┐
  │ Customer Phone │
  │                │
  │ 📱 NEW SMS     │
  │ From: +91998.. │
  │                │
  │ 👋 Welcome to  │
  │ Pizza Palace!  │
  │ 1. 📋 View Menu│
  │ 2. ⭐ Special  │
  │ 3. 🛒 My Cart  │
  │ 4. 📦 Orders   │
  │ 5. ℹ️ Info     │
  │ Reply with #   │
  │                │
  │ [Reply]        │
  └────────────────┘

  Customer reads SMS and replies with "1"
  → The entire cycle repeats from Step 1
  → This time session state = MAIN_MENU
  → "1" means "View Menu" → show categories

  TOTAL ROUND-TRIP TIME: 2-8 seconds
  (depends on telecom + gateway + processing speed)
```

---

## 9. Inbound SMS Processing Pipeline

### Notification Service — Java Code Flow

```java
// SmsReceiveService.java — Core inbound SMS processor
@Service
public class SmsReceiveService {

    @Autowired private SmsCommandParser commandParser;
    @Autowired private SessionStateService sessionStateService;
    @Autowired private TenantLookupService tenantLookupService;
    @Autowired private CustomerService customerService;
    @Autowired private SmsResponseBuilder responseBuilder;
    @Autowired private SmsSendService smsSendService;
    @Autowired private SmsLogRepository smsLogRepository;
    @Autowired private RateLimitService rateLimitService;
    @Autowired private RabbitTemplate rabbitTemplate;

    @Transactional
    public void processInboundSms(InboundSms inbound) {
        // ── Step 1: Log raw SMS ──
        SmsLog log = SmsLog.builder()
            .direction(SmsDirection.INBOUND)
            .from(inbound.getFrom())
            .to(inbound.getTo())
            .body(inbound.getBody())
            .receivedAt(Instant.now())
            .status(SmsLogStatus.RECEIVED)
            .build();
        smsLogRepository.save(log);

        // ── Step 2: Rate limit check ──
        if (!rateLimitService.isAllowed(inbound.getFrom(), "INBOUND")) {
            log.setStatus(SmsLogStatus.RATE_LIMITED);
            smsLogRepository.save(log);
            return; // Drop the message
        }

        // ── Step 3: Resolve tenant ──
        TenantLookup lookup = tenantLookupService
            .findByPhoneNumber(inbound.getTo())
            .orElseThrow(() -> new TenantNotFoundException(
                "No tenant for phone: " + inbound.getTo()));

        UUID tenantId = lookup.getTenantId();

        // ── Step 4: Resolve/create customer ──
        Customer customer = customerService
            .findOrCreateByPhone(inbound.getFrom(), tenantId);

        // ── Step 5: Get session state ──
        SessionState session = sessionStateService
            .getOrDefault(customer.getId(), tenantId);

        // ── Step 6: Parse command ──
        ParsedCommand command = commandParser.parse(
            inbound.getBody(), session);

        // ── Step 7: Route and process ──
        CommandResult result = routeCommand(command, customer, tenantId, session);

        // ── Step 8: Build SMS response ──
        String responseText = responseBuilder.build(result, tenantId, customer);

        // ── Step 9: Update session ──
        sessionStateService.update(
            customer.getId(), tenantId, result.getNewState());

        // ── Step 10: Send reply ──
        smsSendService.sendSms(
            inbound.getFrom(),
            inbound.getTo(),
            responseText,
            log.getId()
        );

        // ── Step 11: Update log ──
        log.setStatus(SmsLogStatus.PROCESSED);
        log.setCommandType(command.getType());
        log.setResponseText(responseText);
        smsLogRepository.save(log);
    }

    private CommandResult routeCommand(ParsedCommand command,
                                        Customer customer,
                                        UUID tenantId,
                                        SessionState session) {
        return switch (command.getType()) {
            case MAIN_MENU -> handleMainMenu(tenantId, customer);
            case NAVIGATE -> handleNavigate(command, session, tenantId);
            case SEARCH -> handleSearch(command, tenantId);
            case ADD_TO_CART -> handleAddToCart(command, customer, tenantId);
            case VIEW_CART -> handleViewCart(customer, tenantId);
            case CHECKOUT -> handleCheckout(customer, tenantId, session);
            case PLACE_ORDER -> handlePlaceOrder(customer, tenantId, session);
            case TRACK_ORDER -> handleTrackOrder(command, tenantId);
            case ORDER_HISTORY -> handleOrderHistory(customer, tenantId);
            case REORDER -> handleReorder(command, customer, tenantId);
            case STORE_INFO -> handleStoreInfo(tenantId);
            case HELP -> handleHelp();
            case GO_BACK -> handleGoBack(session);
            case NEXT_PAGE -> handleNextPage(session, tenantId);
            case PREV_PAGE -> handlePrevPage(session, tenantId);
            case UNSUBSCRIBE -> handleUnsubscribe(customer);
            case UNKNOWN -> handleUnknown(command);
        };
    }
}
```

---

## 10. Outbound SMS Response Pipeline

### How Replies Are Sent Back

```java
// SmsSendService.java — Sends outbound SMS via active gateway
@Service
public class SmsSendService {

    @Value("${sce.sms.provider.active}")
    private String activeProvider;

    @Autowired private Map<String, SmsGatewayInterface> gateways;
    @Autowired private SmsLogRepository smsLogRepository;
    @Autowired private RateLimitService rateLimitService;
    @Autowired private RabbitTemplate rabbitTemplate;

    public SmsSendResult sendSms(String to, String from, String body, UUID parentLogId) {
        // ── Step 1: Rate limit check ──
        String tenantId = TenantContext.getTenantId();
        if (!rateLimitService.isOutboundAllowed(tenantId)) {
            log.warn("Outbound rate limit exceeded for tenant: {}", tenantId);
            return SmsSendResult.rateLimited();
        }

        // ── Step 2: Log outbound SMS ──
        SmsLog log = SmsLog.builder()
            .direction(SmsDirection.OUTBOUND)
            .to(to)
            .from(from)
            .body(body)
            .parentLogId(parentLogId)
            .status(SmsLogStatus.SENDING)
            .tenantId(UUID.fromString(tenantId))
            .build();
        smsLogRepository.save(log);

        // ── Step 3: Handle long messages (split if > 160 chars) ──
        int estimatedSegments = (int) Math.ceil((double) body.length() / 160.0);
        if (body.contains("🍕") || body.contains("📋")) {
            // Unicode characters use UCS-2 encoding (70 chars per segment)
            estimatedSegments = (int) Math.ceil((double) body.length() / 70.0);
        }
        log.setSegments(estimatedSegments);

        // ── Step 4: Send via active gateway ──
        SmsGatewayInterface gateway = gateways.get(activeProvider);
        SmsSendResult result = gateway.sendSms(to, from, body);

        // ── Step 5: Update log with result ──
        log.setProviderMessageId(result.getMessageId());
        log.setStatus(mapStatus(result.getStatus()));
        log.setSentAt(Instant.now());
        smsLogRepository.save(log);

        // ── Step 6: Publish event (for real-time dashboard updates) ──
        rabbitTemplate.convertAndSend(
            "sms.exchange",
            "sms.sent",
            new SmsSentEvent(log.getId(), tenantId, to, estimatedSegments)
        );

        return result;
    }
}
```

### Gateway Strategy Pattern

```java
// SmsGatewayInterface.java — All providers implement this
public interface SmsGatewayInterface {
    SmsSendResult sendSms(String to, String from, String body);
    boolean isHealthy();
    String getProviderName();
}

// AndroidGatewayClient.java — Option A (FREE)
@Component("ANDROID_GATEWAY")
public class AndroidGatewayClient implements SmsGatewayInterface {

    @Value("${sce.sms.android-gateway.base-url}")
    private String baseUrl;

    @Value("${sce.sms.android-gateway.auth-token}")
    private String authToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public SmsSendResult sendSms(String to, String from, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of(
            "to", to,
            "body", body
        );

        HttpEntity<Map<String, String>> request =
            new HttpEntity<>(payload, headers);

        ResponseEntity<SmsSendResult> response = restTemplate.postForEntity(
            baseUrl + "/send",
            request,
            SmsSendResult.class
        );

        return response.getBody();
    }

    @Override
    public boolean isHealthy() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/health", Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}

// JasminGatewayClient.java — Option B (Production)
@Component("JASMIN")
public class JasminGatewayClient implements SmsGatewayInterface {

    @Value("${sce.sms.jasmin.base-url}")
    private String baseUrl;

    @Value("${sce.sms.jasmin.username}")
    private String username;

    @Value("${sce.sms.jasmin.password}")
    private String password;

    @Override
    public SmsSendResult sendSms(String to, String from, String body) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("to", to);
        params.add("from", from);
        params.add("content", body);
        params.add("coding", "8"); // UCS-2 for Unicode

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);

        HttpEntity<MultiValueMap<String, String>> request =
            new HttpEntity<>(params, headers);

        ResponseEntity<String> response = new RestTemplate().postForEntity(
            baseUrl + "/send",
            request,
            String.class
        );

        // Jasmin returns: "Success \"message-id\""
        String responseBody = response.getBody();
        String messageId = extractMessageId(responseBody);

        return SmsSendResult.builder()
            .messageId(messageId)
            .status(SmsStatus.SENT)
            .build();
    }
}

// SimulatorGatewayClient.java — For testing (NO real SMS)
@Component("SIMULATOR")
public class SimulatorGatewayClient implements SmsGatewayInterface {

    @Autowired private SimpMessagingTemplate webSocket;

    @Override
    public SmsSendResult sendSms(String to, String from, String body) {
        // Don't actually send — just log and show in simulator UI
        SimulatedSms sim = SimulatedSms.builder()
            .to(to)
            .from(from)
            .body(body)
            .timestamp(Instant.now())
            .build();

        // Push to WebSocket for live display in dashboard
        webSocket.convertAndSend("/topic/sms-simulator", sim);

        log.info("📱 SIMULATED SMS to {}: {}", to, body);

        return SmsSendResult.builder()
            .messageId("SIM-" + UUID.randomUUID())
            .status(SmsStatus.DELIVERED)
            .build();
    }
}
```

---

## 11. Session Management in SMS

### How Session State Works (Redis)

```
┌──────────────────────────────────────────────────────────────────────────┐
│              SMS SESSION STATE MANAGEMENT                                │
│                                                                          │
│  Each customer-tenant pair has a session stored in Redis.               │
│  This tracks WHERE the customer is in the menu navigation.               │
│                                                                          │
│  Redis Key Pattern: session:{customerId}:{tenantId}                     │
│  TTL: 1800 seconds (30 minutes) — auto-expires if no activity          │
│                                                                          │
│  SESSION LIFECYCLE:                                                      │
│                                                                          │
│  Customer sends "MENU"                                                   │
│       │                                                                  │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────┐                │
│  │ Redis Key: session:C-042:T-001                      │                │
│  │ {                                                    │                │
│  │   "state": "MAIN_MENU",                              │                │
│  │   "depth": 0,                                        │                │
│  │   "pageNumber": 1,                                   │                │
│  │   "breadcrumb": [],                                  │                │
│  │   "cartItemCount": 2,         ← Quick reference      │                │
│  │   "lastActivity": "2026-06-21T12:30:00Z",            │                │
│  │   "expireAt": "2026-06-21T13:00:00Z"                 │                │
│  │ }                                                    │                │
│  └─────────────────────────────────────────────────────┘                │
│       │                                                                  │
│       │ Customer sends "1" (View Menu)                                   │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────┐                │
│  │ Redis Key: session:C-042:T-001                      │                │
│  │ {                                                    │                │
│  │   "state": "BROWSING_CATEGORIES",                    │                │
│  │   "depth": 1,                                        │                │
│  │   "pageNumber": 1,                                   │                │
│  │   "breadcrumb": ["MAIN_MENU"],                       │                │
│  │   "cartItemCount": 2,                                │                │
│  │   "lastActivity": "2026-06-21T12:31:00Z"             │                │
│  │ }                                                    │                │
│  └─────────────────────────────────────────────────────┘                │
│       │                                                                  │
│       │ Customer sends "1" (Pizzas category)                             │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────┐                │
│  │ Redis Key: session:C-042:T-001                      │                │
│  │ {                                                    │                │
│  │   "state": "BROWSING_PRODUCTS",                      │                │
│  │   "depth": 2,                                        │                │
│  │   "categoryId": "cat-pizzas",                        │                │
│  │   "categoryName": "Pizzas",                          │                │
│  │   "pageNumber": 1,                                   │                │
│  │   "totalPages": 3,                                   │                │
│  │   "breadcrumb": ["MAIN_MENU", "BROWSING_CATEGORIES"],│                │
│  │   "cartItemCount": 2,                                │                │
│  │   "lastActivity": "2026-06-21T12:32:00Z"             │                │
│  │ }                                                    │                │
│  └─────────────────────────────────────────────────────┘                │
│       │                                                                  │
│       │ Customer sends "N" (Next page)                                   │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────┐                │
│  │ Redis Key: session:C-042:T-001                      │                │
│  │ {                                                    │                │
│  │   "state": "BROWSING_PRODUCTS",                      │                │
│  │   "depth": 2,                                        │                │
│  │   "categoryId": "cat-pizzas",                        │                │
│  │   "pageNumber": 2,         ← INCREMENTED             │                │
│  │   "totalPages": 3,                                   │                │
│  │   "breadcrumb": ["MAIN_MENU", "BROWSING_CATEGORIES"],│                │
│  │   "cartItemCount": 2,                                │                │
│  │   "lastActivity": "2026-06-21T12:33:00Z"             │                │
│  │ }                                                    │                │
│  └─────────────────────────────────────────────────────┘                │
│       │                                                                  │
│       │ Customer sends "0" (Go Back)                                     │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────┐                │
│  │ Pop from breadcrumb:                                 │                │
│  │ → Previous state: BROWSING_CATEGORIES                │                │
│  │ → Reset pageNumber: 1                                │                │
│  └─────────────────────────────────────────────────────┘                │
│                                                                          │
│  SPECIAL CASES:                                                          │
│  ├── "*" (asterisk) → Always go to MAIN_MENU (reset session)            │
│  ├── "#" (hash)     → Show HELP (don't change session state)            │
│  ├── Timeout (30m)  → Session deleted, next SMS starts fresh            │
│  └── Max depth (5)  → Auto-navigate back, show "Too deep, back to menu" │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Session State Service (Java)

```java
// SessionStateService.java
@Service
public class SessionStateService {

    @Autowired private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "session:";
    private static final long SESSION_TTL_SECONDS = 1800; // 30 minutes
    private static final int MAX_DEPTH = 5;
    private static final ObjectMapper mapper = new ObjectMapper();

    public SessionState getOrDefault(UUID customerId, UUID tenantId) {
        String key = buildKey(customerId, tenantId);
        String json = redisTemplate.opsForValue().get(key);

        if (json != null) {
            try {
                return mapper.readValue(json, SessionState.class);
            } catch (Exception e) {
                log.warn("Corrupt session for {}: {}", key, e.getMessage());
            }
        }

        return SessionState.fresh(); // Default fresh session
    }

    public void update(UUID customerId, UUID tenantId, SessionState state) {
        String key = buildKey(customerId, tenantId);
        state.setLastActivity(Instant.now());

        try {
            String json = mapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(
                key, json, SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to update session: {}", e.getMessage());
        }
    }

    public void reset(UUID customerId, UUID tenantId) {
        String key = buildKey(customerId, tenantId);
        redisTemplate.delete(key);
    }

    public void goBack(UUID customerId, UUID tenantId) {
        SessionState state = getOrDefault(customerId, tenantId);
        if (state.getBreadcrumb().isEmpty()) {
            // Already at main menu
            reset(customerId, tenantId);
            return;
        }
        // Pop last state from breadcrumb
        String previousState = state.getBreadcrumb()
            .remove(state.getBreadcrumb().size() - 1);
        state.setState(MenuState.valueOf(previousState));
        state.setDepth(state.getDepth() - 1);
        state.setPageNumber(1);
        update(customerId, tenantId, state);
    }

    private String buildKey(UUID customerId, UUID tenantId) {
        return KEY_PREFIX + customerId + ":" + tenantId;
    }
}
```

---

## 12. SMS Command Parser — Full Logic

### Complete Parser with All Commands

```java
// SmsCommandParser.java — The brain of the SMS system
@Component
public class SmsCommandParser {

    // Pre-compiled patterns for performance
    private static final Pattern NUMERIC = Pattern.compile("^(\\d+)$");
    private static final Pattern SEARCH_CMD = Pattern.compile(
        "^SEARCH\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ADD_CMD = Pattern.compile(
        "^ADD\\s+(\\S+)(?:\\s+(\\d+))?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUSTOMIZE_CMD = Pattern.compile(
        "^([A-Z]\\d*)-([A-Z]\\d*(?:,[A-Z]\\d*)*)$");
    private static final Pattern ADDRESS_PAYMENT = Pattern.compile(
        "^(A\\d+)-(P\\d+)$");
    private static final Pattern TRACK_CMD = Pattern.compile(
        "^TRACK\\s+(\\S+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REORDER_CMD = Pattern.compile(
        "^REORDER\\s+(\\S+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_CMD = Pattern.compile(
        "^UPDATE\\s+(\\d+)\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REMOVE_CMD = Pattern.compile(
        "^REMOVE\\s+(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern INFO_CMD = Pattern.compile(
        "^INFO\\s+(\\S+)$", Pattern.CASE_INSENSITIVE);

    public ParsedCommand parse(String rawText, SessionState session) {
        if (rawText == null || rawText.isBlank()) {
            return ParsedCommand.unknown(rawText, "Empty message");
        }

        String input = rawText.trim();
        String upper = input.toUpperCase();

        // ── Navigation shortcuts (always work regardless of state) ──
        if (upper.equals("0") || upper.equals("BACK")) {
            return ParsedCommand.goBack();
        }
        if (upper.equals("*") || upper.equals("MAIN") || upper.equals("HOME")) {
            return ParsedCommand.mainMenu();
        }
        if (upper.equals("#") || upper.equals("HELP")) {
            return ParsedCommand.help();
        }
        if (upper.equals("N") || upper.equals("NEXT")) {
            return ParsedCommand.nextPage();
        }
        if (upper.equals("P") || upper.equals("PREV") || upper.equals("PREVIOUS")) {
            return ParsedCommand.prevPage();
        }
        if (upper.equals("STOP") || upper.equals("UNSUBSCRIBE")) {
            return ParsedCommand.unsubscribe();
        }

        // ── Welcome / Main menu triggers ──
        if (matchesAny(upper, "HI", "HELLO", "START", "MENU", "HEY", "HAI")) {
            return ParsedCommand.mainMenu();
        }

        // ── Search command ──
        Matcher searchMatcher = SEARCH_CMD.matcher(upper);
        if (searchMatcher.matches()) {
            return ParsedCommand.search(searchMatcher.group(1).trim());
        }

        // ── Add to cart ──
        Matcher addMatcher = ADD_CMD.matcher(upper);
        if (addMatcher.matches()) {
            String productId = addMatcher.group(1);
            int quantity = addMatcher.group(2) != null
                ? Integer.parseInt(addMatcher.group(2)) : 1;
            return ParsedCommand.addToCart(productId, quantity);
        }

        // ── Cart operations ──
        if (upper.equals("CART") || upper.equals("3") || upper.equals("MYCART")) {
            return ParsedCommand.viewCart();
        }
        if (upper.equals("CLEAR") || upper.equals("CLEARCART")) {
            return ParsedCommand.clearCart();
        }

        Matcher updateMatcher = UPDATE_CMD.matcher(upper);
        if (updateMatcher.matches()) {
            int itemIndex = Integer.parseInt(updateMatcher.group(1));
            int newQty = Integer.parseInt(updateMatcher.group(2));
            return ParsedCommand.updateCartItem(itemIndex, newQty);
        }

        Matcher removeMatcher = REMOVE_CMD.matcher(upper);
        if (removeMatcher.matches()) {
            int itemIndex = Integer.parseInt(removeMatcher.group(1));
            return ParsedCommand.removeCartItem(itemIndex);
        }

        // ── Checkout flow ──
        if (upper.equals("CHECKOUT") || upper.equals("BUY") || upper.equals("ORDER")) {
            return ParsedCommand.checkout();
        }

        // ── Address-Payment response (e.g., "A1-P1") ──
        Matcher addrPayMatcher = ADDRESS_PAYMENT.matcher(upper);
        if (addrPayMatcher.matches()) {
            return ParsedCommand.selectAddressAndPayment(
                addrPayMatcher.group(1), addrPayMatcher.group(2));
        }

        // ── Order operations ──
        if (upper.equals("ORDERS") || upper.equals("4") || upper.equals("MYORDERS")) {
            return ParsedCommand.orderHistory();
        }

        Matcher trackMatcher = TRACK_CMD.matcher(upper);
        if (trackMatcher.matches()) {
            return ParsedCommand.trackOrder(trackMatcher.group(1));
        }

        Matcher reorderMatcher = REORDER_CMD.matcher(upper);
        if (reorderMatcher.matches()) {
            return ParsedCommand.reorder(reorderMatcher.group(1));
        }

        // ── Store info ──
        if (upper.equals("INFO") || upper.equals("5") || upper.equals("STORE")) {
            return ParsedCommand.storeInfo();
        }

        // ── Product info ──
        Matcher infoMatcher = INFO_CMD.matcher(upper);
        if (infoMatcher.matches()) {
            return ParsedCommand.productInfo(infoMatcher.group(1));
        }

        // ── Customization response (e.g., "M-T1,T2") ──
        Matcher customizeMatcher = CUSTOMIZE_CMD.matcher(upper);
        if (customizeMatcher.matches()) {
            String size = customizeMatcher.group(1);
            String[] toppings = customizeMatcher.group(2).split(",");
            return ParsedCommand.customization(size, Arrays.asList(toppings));
        }

        // ── Numeric navigation (context-dependent) ──
        Matcher numericMatcher = NUMERIC.matcher(upper);
        if (numericMatcher.matches()) {
            int option = Integer.parseInt(numericMatcher.group(1));
            return ParsedCommand.navigate(option, session);
        }

        // ── Fuzzy matching (find closest command) ──
        String suggestion = findClosestCommand(upper);
        return ParsedCommand.unknown(rawText, suggestion);
    }

    private String findClosestCommand(String input) {
        Map<String, String> commands = Map.of(
            "MENU", "MENU",
            "CART", "CART",
            "CHECKOUT", "CHECKOUT",
            "ORDERS", "ORDERS",
            "HELP", "HELP",
            "SEARCH", "SEARCH <keyword>"
        );

        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (String cmd : commands.keySet()) {
            int distance = levenshteinDistance(input, cmd);
            if (distance < minDistance && distance <= 3) {
                minDistance = distance;
                closest = commands.get(cmd);
            }
        }

        return closest;
    }

    private boolean matchesAny(String input, String... options) {
        for (String option : options) {
            if (input.equals(option)) return true;
        }
        return false;
    }
}
```

---

## 13. Multi-Tenant SMS Routing

### How the System Knows Which Store a Customer Is Texting

```
┌──────────────────────────────────────────────────────────────────────────┐
│              MULTI-TENANT SMS ROUTING                                    │
│                                                                          │
│  Each tenant (business) has a UNIQUE phone number.                       │
│  When a customer sends SMS, the TO number identifies the tenant.         │
│                                                                          │
│  ┌─ TENANT LOOKUP TABLE ───────────────────────────────────────────┐   │
│  │                                                                   │   │
│  │  shared.tenant_lookup:                                            │   │
│  │  ┌──────────────────┬──────────┬──────────────────────────────┐   │   │
│  │  │ phone_number      │ tenant_id│ tenant_name                  │   │   │
│  │  ├──────────────────┼──────────┼──────────────────────────────┤   │   │
│  │  │ +91-9988776655    │ T-001    │ Pizza Palace                 │   │   │
│  │  │ +91-9988776656    │ T-002    │ Fresh Market                 │   │   │
│  │  │ +91-9988776657    │ T-003    │ Quick Bites                  │   │   │
│  │  │ +91-9988776658    │ T-004    │ Style Hub                    │   │   │
│  │  │ +1-555-123-4567   │ T-005    │ Mini Stop (USA)              │   │   │
│  │  └──────────────────┴──────────┴──────────────────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ROUTING FLOW:                                                           │
│                                                                          │
│  Customer sends SMS to +91-9988776655                                    │
│       │                                                                  │
│       ▼                                                                  │
│  Notification Service receives:                                          │
│  { from: "+919876543210", to: "+919988776655", body: "MENU" }           │
│       │                                                                  │
│       ▼                                                                  │
│  Lookup: SELECT tenant_id FROM shared.tenant_lookup                      │
│          WHERE phone_number = '+919988776655'                            │
│       │                                                                  │
│       ▼                                                                  │
│  Result: tenant_id = T-001 (Pizza Palace)                                │
│       │                                                                  │
│       ▼                                                                  │
│  SET app.current_tenant = 'T-001'   ← RLS activates                     │
│       │                                                                  │
│       ▼                                                                  │
│  All subsequent queries in this request ONLY see Pizza Palace data       │
│  (products, orders, customers, etc.)                                     │
│                                                                          │
│  PHONE NUMBER ASSIGNMENT OPTIONS:                                        │
│                                                                          │
│  Option 1: One physical phone per tenant (Android Gateway)               │
│  ──────────────────────────────────────────────────                       │
│  ├── Each tenant gets a real SIM card + phone                            │
│  ├── Truly free (uses SIM's SMS plan)                                    │
│  ├── Physical limit: 1 phone per tenant                                  │
│  └── Best for: Small scale (1-10 tenants)                                │
│                                                                          │
│  Option 2: Virtual numbers via SMPP provider                             │
│  ──────────────────────────────────────────────────                       │
│  ├── Buy virtual numbers from telecom/aggregator                         │
│  ├── All routed through single Jasmin gateway                            │
│  ├── Cost: $0.50-$2.00/month per number                                  │
│  ├── Unlimited tenants possible                                          │
│  └── Best for: Production scale (10+ tenants)                            │
│                                                                          │
│  Option 3: Single shared number + keyword routing                        │
│  ──────────────────────────────────────────────────                       │
│  ├── All tenants share ONE phone number                                  │
│  ├── Customer texts: "PIZZA MENU" or "FRESH MENU"                       │
│  ├── First word identifies the tenant                                    │
│  ├── Cheapest option (only 1 number needed)                              │
│  └── Less user-friendly (customer must remember keyword)                 │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 14. SMS Template Engine

### How Dynamic SMS Responses Are Built

```java
// SmsTemplateEngine.java — Renders SMS templates with dynamic data
@Component
public class SmsTemplateEngine {

    @Autowired private TemplateRepository templateRepository;

    /**
     * Renders an SMS template with dynamic variables
     */
    public String render(String templateCode, UUID tenantId, Map<String, Object> variables) {
        // Load tenant-specific template (or fall back to default)
        SmsTemplate template = templateRepository
            .findByTenantIdAndCode(tenantId, templateCode)
            .orElse(templateRepository.findDefaultByCode(templateCode));

        String body = template.getBody();

        // Replace all {{variable}} placeholders
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null
                ? entry.getValue().toString() : "";
            body = body.replace(placeholder, value);
        }

        // Handle loops: {{for each item in items}} ... {{end for}}
        body = processLoops(body, variables);

        // Handle conditionals: {{if condition}} ... {{end if}}
        body = processConditionals(body, variables);

        // Trim and normalize whitespace
        body = body.replaceAll("\\n{3,}", "\n\n").trim();

        // Validate length
        int maxLength = containsUnicode(body) ? 70 : 160;
        int segments = (int) Math.ceil((double) body.length() / maxLength);

        if (segments > 2) {
            // Too long — truncate with "..." and "Reply MORE for rest"
            body = truncateToSegments(body, 2, maxLength);
            body += "\nReply MORE for details";
        }

        return body;
    }

    /**
     * Example template rendering for MAIN_MENU:
     *
     * Template:
     * "👋 Welcome to {{store_name}}, {{customer_name}}!
     *  {{for each item in menu_items}}
     *  {{item.number}}. {{item.emoji}} {{item.label}}
     *  {{end for}}
     *  🛒 Cart: {{cart_count}} items
     *  Reply with a number"
     *
     * Variables:
     * {
     *   "store_name": "Pizza Palace",
     *   "customer_name": "Rahul",
     *   "menu_items": [
     *     {"number": 1, "emoji": "📋", "label": "View Menu"},
     *     {"number": 2, "emoji": "⭐", "label": "Today's Special"},
     *     {"number": 3, "emoji": "🛒", "label": "My Cart"},
     *     {"number": 4, "emoji": "📦", "label": "My Orders"},
     *     {"number": 5, "emoji": "ℹ️", "label": "Store Info"}
     *   ],
     *   "cart_count": 2
     * }
     *
     * Output:
     * "👋 Welcome to Pizza Palace, Rahul!
     *  1. 📋 View Menu
     *  2. ⭐ Today's Special
     *  3. 🛒 My Cart
     *  4. 📦 My Orders
     *  5. ℹ️ Store Info
     *  🛒 Cart: 2 items
     *  Reply with a number"
     */
}
```

### Complete Template Library

```
┌──────────────────────────────────────────────────────────────────────────┐
│              SMS TEMPLATE LIBRARY                                         │
│                                                                          │
│  TEMPLATE: WELCOME (sent on first SMS from new customer)                 │
│  ─────────────────────────────────────────────────────────               │
│  "👋 Welcome to {{store_name}}!                                         │
│   Reply MENU to browse our menu                                          │
│   or SPECIAL for today's deals!"                                         │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: MAIN_MENU                                                     │
│  ─────────────────────────────────────────────────────────               │
│  "🏪 {{store_name}}                                                     │
│   1. 📋 View Menu                                                        │
│   2. ⭐ Today's Special                                                  │
│   3. 🛒 My Cart {{#if cart_count > 0}}({{cart_count}}){{/if}}           │
│   4. 📦 My Orders                                                        │
│   5. ℹ️ Store Info                                                       │
│   Reply with a number"                                                   │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: CATEGORY_LIST                                                 │
│  ─────────────────────────────────────────────────────────               │
│  "📋 {{store_name}} MENU                                                │
│   {{for each cat in categories}}                                         │
│   {{cat.number}}. {{cat.emoji}} {{cat.name}} ({{cat.count}})            │
│   {{end for}}                                                            │
│   0. ← Back | Reply with number"                                         │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: PRODUCT_LIST                                                  │
│  ─────────────────────────────────────────────────────────               │
│  "{{category.emoji}} {{category.name}} (Page {{page}}/{{total_pages}})  │
│   {{for each p in products}}                                             │
│   {{p.number}}. {{p.name}} - {{currency}}{{p.price}}                    │
│   {{end for}}                                                            │
│   ▸ Next: N | Back: 0 | Add: ADD <number>"                              │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: PRODUCT_DETAIL                                                │
│  ─────────────────────────────────────────────────────────               │
│  "{{product.emoji}} {{product.name}} - {{currency}}{{product.price}}    │
│   {{product.description}}                                                │
│   {{#if has_variants}}                                                   │
│   Choose {{variant_type}}:                                               │
│   {{for each v in variants}}                                             │
│   {{v.code}}. {{v.name}} (+{{currency}}{{v.extra}})                     │
│   {{end for}}                                                            │
│   {{/if}}                                                                │
│   Reply: ADD {{product.code}} {{variant_code}}"                          │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: ADDED_TO_CART                                                 │
│  ─────────────────────────────────────────────────────────               │
│  "✅ Added to cart!                                                      │
│   🛒 YOUR CART:                                                          │
│   {{for each item in cart_items}}                                        │
│   {{item.qty}}x {{item.name}}{{#if item.custom}} ({{item.custom}})      │
│   {{/if}} {{currency}}{{item.total}}                                     │
│   {{end for}}                                                            │
│   Total: {{currency}}{{cart_total}}                                      │
│   1. Add more 2. Checkout 3. Clear                                       │
│   Reply with number"                                                     │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: CHECKOUT_SUMMARY                                              │
│  ─────────────────────────────────────────────────────────               │
│  "🛒 CHECKOUT                                                           │
│   Items: {{cart_item_count}}                                             │
│   Subtotal: {{currency}}{{subtotal}}                                     │
│   Delivery: {{currency}}{{delivery_fee}}                                 │
│   ──────────────────                                                     │
│   Total: {{currency}}{{total}}                                           │
│                                                                          │
│   Address:                                                               │
│   {{for each addr in saved_addresses}}                                   │
│   {{addr.code}}. {{addr.short}}                                          │
│   {{end for}}                                                            │
│   A{{new}}. Add new address                                              │
│                                                                          │
│   Payment:                                                               │
│   P1. Cash on Delivery                                                   │
│   {{#if mobile_money}}P2. Mobile Money{{/if}}                           │
│                                                                          │
│   Reply: ADDRESS-PAYMENT (e.g., A1-P1)"                                  │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: ORDER_CONFIRMED                                               │
│  ─────────────────────────────────────────────────────────               │
│  "🎉 ORDER PLACED!                                                      │
│   Order #{{order_number}}                                                │
│   {{for each item in order_items}}                                       │
│   {{item.qty}}x {{item.name}} {{currency}}{{item.total}}                │
│   {{end for}}                                                            │
│   Total: {{currency}}{{total}} ({{payment_method}})                      │
│   Delivering to: {{address_short}}                                       │
│   Est. time: {{eta}} mins                                                │
│   Track: TRACK {{order_number}}"                                         │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: ORDER_STATUS_UPDATE                                           │
│  ─────────────────────────────────────────────────────────               │
│  "📦 Order #{{order_number}}: {{status_emoji}} {{status}}               │
│   {{status_detail}}                                                      │
│   {{#if status == 'OUT_FOR_DELIVERY'}}                                   │
│   Your delivery agent: {{driver_name}} ({{driver_phone}})               │
│   {{/if}}"                                                               │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: UNKNOWN_COMMAND                                               │
│  ─────────────────────────────────────────────────────────               │
│  "❓ Sorry, I didn't understand that.                                    │
│   {{#if suggestion}}                                                     │
│   Did you mean: {{suggestion}}?                                          │
│   {{/if}}                                                                │
│   Reply MENU for main menu or HELP for commands."                        │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: SESSION_EXPIRED                                               │
│  ─────────────────────────────────────────────────────────               │
│  "⏰ Your session expired. Reply MENU to start again."                   │
│                                                                          │
│  ─────────────────────────────────────────────────────────               │
│  TEMPLATE: CLOSED                                                        │
│  ─────────────────────────────────────────────────────────               │
│  "🔒 {{store_name}} is currently closed.                                │
│   We open {{next_open_day}} at {{next_open_time}}.                      │
│   You can still browse our menu: Reply MENU"                             │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 15. Rate Limiting & Security

### SMS Rate Limiting with Redis

```java
// RateLimitService.java — Prevents SMS abuse
@Service
public class RateLimitService {

    @Autowired private StringRedisTemplate redisTemplate;

    @Value("${sce.sms.rate-limit.inbound.per-phone.per-hour}")
    private int inboundPerPhonePerHour;

    @Value("${sce.sms.rate-limit.outbound.per-tenant.per-hour}")
    private int outboundPerTenantPerHour;

    @Value("${sce.sms.rate-limit.outbound.per-tenant.per-day}")
    private int outboundPerTenantPerDay;

    /**
     * Check if inbound SMS from a phone number is allowed
     */
    public boolean isInboundAllowed(String phoneNumber) {
        String key = "rate:inbound:" + phoneNumber + ":" + currentHour();
        return isAllowed(key, inboundPerPhonePerHour, 3600);
    }

    /**
     * Check if outbound SMS for a tenant is allowed
     */
    public boolean isOutboundAllowed(String tenantId) {
        // Hourly limit
        String hourKey = "rate:outbound:" + tenantId + ":hour:" + currentHour();
        if (!isAllowed(hourKey, outboundPerTenantPerHour, 3600)) {
            return false;
        }

        // Daily limit
        String dayKey = "rate:outbound:" + tenantId + ":day:" + currentDay();
        return isAllowed(dayKey, outboundPerTenantPerDay, 86400);
    }

    private boolean isAllowed(String key, int limit, int ttlSeconds) {
        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return current != null && current <= limit;
    }

    private String currentHour() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
    }

    private String currentDay() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
```

### Security Measures for SMS

```
┌──────────────────────────────────────────────────────────────────────────┐
│              SMS SECURITY CHECKLIST                                      │
│                                                                          │
│  1. INPUT SANITIZATION                                                   │
│  ─────────────────────────────                                           │
│  ✅ Strip all control characters from inbound SMS                        │
│  ✅ Max SMS body length: 1000 characters (drop longer)                   │
│  ✅ Escape SQL-sensitive characters (RLS protects, but defense in depth) │
│  ✅ Whitelist-based command matching (not blacklist)                     │
│  ✅ No user input ever concatenated into SQL queries                     │
│                                                                          │
│  2. RATE LIMITING                                                        │
│  ────────────────                                                        │
│  ✅ Inbound: 20 SMS per phone per hour                                   │
│  ✅ Outbound: 50 SMS per tenant per hour                                 │
│  ✅ Outbound: 1000 SMS per tenant per day                                │
│  ✅ Burst protection: max 5 SMS per phone per minute                     │
│  ✅ Broadcast: limited by subscription plan quota                        │
│                                                                          │
│  3. OTP VERIFICATION                                                     │
│  ─────────────────────                                                   │
│  ✅ New customer: Verify phone via OTP on first SMS                      │
│  ✅ Sensitive ops: OTP before checkout (optional, per tenant config)     │
│  ✅ OTP validity: 5 minutes, max 3 attempts                              │
│                                                                          │
│  4. ANTI-SPAM                                                            │
│  ──────────                                                              │
│  ✅ STOP command: Immediately unsubscribe from broadcasts                │
│  ✅ Max 2 promotional SMS per tenant per customer per week               │
│  ✅ Blacklist: Block abusive phone numbers                               │
│                                                                          │
│  5. AUDIT TRAIL                                                          │
│  ─────────────                                                           │
│  ✅ Every inbound SMS logged (raw text, timestamp, phone)                │
│  ✅ Every outbound SMS logged (rendered text, status, segments)          │
│  ✅ Every command parsed (type, result, processing time)                 │
│  ✅ Logs retained for 90 days (configurable per plan)                    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 16. Cost Comparison

### Real Cost Analysis

```
┌──────────────────────────────────────────────────────────────────────────┐
│              COST COMPARISON — PER MONTH                                  │
│                                                                          │
│  Scenario: 10 tenants, avg 50 orders/tenant/day,                        │
│  avg 8 SMS interactions per order = 400 SMS/tenant/day                  │
│  Total: 4,000 SMS/day = 120,000 SMS/month                               │
│                                                                          │
│  ┌──────────────────────┬──────────────┬──────────────┬────────────┐    │
│  │ Approach              │ Setup Cost   │ Monthly Cost │ Per SMS    │    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ A. Android Gateway    │ $0           │ $0*          │ $0         │    │
│  │    (10 phones + SIMs)│              │              │            │    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ B. Jasmin + SMPP     │ $0 (software)│ $240-$960    │ $0.002-.008│    │
│  │    (India rates)     │ + SMPP setup │              │            │    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ B. Jasmin + SMPP     │ $0 (software)│ $600-$2,400  │ $0.005-.02 │    │
│  │    (US/EU rates)     │ + SMPP setup │              │            │    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ C. Twilio             │ $0           │ $948         │ $0.0079    │    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ C. Africa's Talking   │ $0           │ $480-$960    │ $0.004-.008│    │
│  ├──────────────────────┼──────────────┼──────────────┼────────────┤    │
│  │ C. MSG91 (India)      │ $0           │ $240         │ $0.002     │    │
│  └──────────────────────┴──────────────┴──────────────┴────────────┘    │
│                                                                          │
│  * Android Gateway monthly cost:                                         │
│    ├── 10 Android phones: Already owned or ~$50 each (one-time)          │
│    ├── 10 SIM cards with unlimited SMS: $0-$50/month                     │
│    │   (Many plans include unlimited SMS for free)                        │
│    └── Electricity + WiFi: Negligible                                     │
│                                                                          │
│  RECOMMENDATION:                                                         │
│  ├── Development: Simulator (free) + Android phone for real tests       │
│  ├── Beta (1-10 tenants): Android phones ($0)                            │
│  ├── Production (10-50 tenants): Jasmin + MSG91/Africa's Talking        │
│  └── Scale (50+ tenants): Jasmin + direct telecom SMPP                  │
│                                                                          │
│  YOUR SMS COMMERCE PLATFORM COSTS:                                       │
│  ├── Software: $0 (all open source)                                      │
│  ├── Hosting: $20-100/month (VPS for small scale)                        │
│  ├── SMS delivery: $0 (Android) to $240/month (SMPP at 120k SMS)        │
│  └── Total to launch: $20-340/month                                      │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 17. Setup Guide — Free Development Environment

### Step-by-Step: Get SMS Working in Development

```
┌──────────────────────────────────────────────────────────────────────────┐
│              FREE DEVELOPMENT SETUP — STEP BY STEP                       │
│                                                                          │
│  STEP 1: Use Built-in Simulator (Instant, No Hardware)                   │
│  ─────────────────────────────────────────────────────                   │
│                                                                          │
│  Your Notification Service includes a SIMULATOR mode.                    │
│  No real SMS needed. Test via web UI or curl.                            │
│                                                                          │
│  # Set in application.properties:                                        │
│  sce.sms.provider.active=SIMULATOR                                       │
│                                                                          │
│  # Simulate an inbound SMS via curl:                                     │
│  curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \       │
│    -H "Content-Type: application/json" \                                 │
│    -d '{                                                                 │
│      "from": "+919876543210",                                            │
│      "to": "+919988776655",                                              │
│      "body": "MENU"                                                      │
│    }'                                                                    │
│                                                                          │
│  # See what response WOULD be sent:                                      │
│  # → Console log shows the SMS reply                                     │
│  # → Dashboard shows simulated SMS in real-time                          │
│  # → Redis shows updated session state                                   │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────── │
│                                                                          │
│  STEP 2: Connect a Real Android Phone (True SMS Testing)                 │
│  ───────────────────────────────────────────────────────                 │
│                                                                          │
│  What you need:                                                          │
│  ├── 1 Android phone (any old phone works)                               │
│  ├── 1 SIM card with SMS plan                                            │
│  ├── USB cable or WiFi connection to your dev machine                    │
│  └── Install SCE Gateway App (or use open-source alternative)            │
│                                                                          │
│  Steps:                                                                  │
│  1. Install an open-source SMS gateway app on Android:                   │
│     → "SMS Gateway API" from Play Store or GitHub                        │
│  2. Configure app:                                                       │
│     → Server URL: http://YOUR_PC_IP:8084/api/v1/sms/webhook/inbound     │
│     → Auto-start: ON                                                     │
│  3. Set in application.properties:                                       │
│     sce.sms.provider.active=ANDROID_GATEWAY                             │
│     sce.sms.android-gateway.base-url=http://ANDROID_IP:8080             │
│  4. Send a real SMS from another phone to the Android phone              │
│  5. Watch your backend process it and send a reply!                      │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────── │
│                                                                          │
│  STEP 3: Use Twilio Free Trial (Cloud-Based Testing)                     │
│  ─────────────────────────────────────────────────────────────────────── │
│                                                                          │
│  What you need:                                                          │
│  ├── Twilio account (free)                                               │
│  ├── ngrok (free) to expose your local server                            │
│  └── Verified phone numbers                                              │
│                                                                          │
│  Steps:                                                                  │
│  1. Sign up at twilio.com/try-twilio                                    │
│  2. Get your Twilio phone number + credentials                           │
│  3. Expose your local server:                                            │
│     ngrok http 8084                                                       │
│     → You get: https://abc123.ngrok.io                                  │
│  4. In Twilio Console, set webhook URL:                                  │
│     https://abc123.ngrok.io/api/v1/sms/webhook/twilio                   │
│  5. Set in application.properties:                                       │
│     sce.sms.provider.active=TWILIO                                      │
│     sce.sms.twilio.account-sid=ACxxxxxx                                 │
│     sce.sms.twilio.auth-token=your_token                                │
│     sce.sms.twilio.phone-number=+15551234567                            │
│  6. Send SMS to your Twilio number → Your backend processes it!          │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────── │
│                                                                          │
│  STEP 4: Add Jasmin for Production Testing                               │
│  ─────────────────────────────────────────────────────                   │
│                                                                          │
│  # Add to docker-compose.yml:                                            │
│  docker-compose up -d jasmin redis                                       │
│                                                                          │
│  # Configure Jasmin with SMPP sandbox (Africa's Talking):                │
│  # 1. Sign up at africastalking.com (free sandbox)                       │
│  # 2. Get SMPP credentials from sandbox dashboard                        │
│  # 3. Add SMPP connector in Jasmin                                       │
│                                                                          │
│  # Set in application.properties:                                        │
│  sce.sms.provider.active=JASMIN                                          │
│  sce.sms.jasmin.base-url=http://localhost:8990                           │
│  sce.sms.jasmin.username=jadmin                                          │
│  sce.sms.jasmin.password=${JASMIN_PASSWORD}                              │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────── │
│                                                                          │
│  SWITCHING BETWEEN PROVIDERS:                                            │
│  ────────────────────────────────                                        │
│  Just change ONE line in application.properties:                         │
│                                                                          │
│  sce.sms.provider.active=SIMULATOR          # No real SMS               │
│  sce.sms.provider.active=ANDROID_GATEWAY    # Free real SMS              │
│  sce.sms.provider.active=TWILIO             # Free trial SMS             │
│  sce.sms.provider.active=JASMIN             # Production SMS             │
│                                                                          │
│  No code changes needed! The strategy pattern handles everything.         │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Quick Start — Test the Full SMS Flow Right Now

```bash
# 1. Start backend with SIMULATOR mode
# application.properties: sce.sms.provider.active=SIMULATOR
cd backend
mvn spring-boot:run -pl notification-service

# 2. Simulate a customer sending "HI"
curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \
  -H "Content-Type: application/json" \
  -d '{"from": "+919876543210", "to": "+919988776655", "body": "HI"}'

# Expected response in console:
# 📱 INBOUND SMS from +919876543210: "HI"
# 🔄 Resolved tenant: Pizza Palace (T-001)
# 🆕 New customer created: C-NEW
# 📤 OUTBOUND SMS to +919876543210:
#    "👋 Welcome to Pizza Palace!
#     1. 📋 View Menu
#     2. ⭐ Today's Special
#     3. 🛒 My Cart
#     4. 📦 My Orders
#     5. ℹ️ Store Info
#     Reply with a number"

# 3. Simulate customer replying "1" (View Menu)
curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \
  -H "Content-Type: application/json" \
  -d '{"from": "+919876543210", "to": "+919988776655", "body": "1"}'

# Expected response:
# 📱 INBOUND SMS from +919876543210: "1"
# 🔄 Session restored: MAIN_MENU → NAVIGATE(1)
# 📤 OUTBOUND SMS:
#    "📋 PIZZA PALACE MENU
#     1. 🍕 Pizzas (12 items)
#     2. 🍝 Pasta (6 items)
#     3. 🥗 Salads (4 items)
#     4. 🥤 Drinks (8 items)
#     5. 🍰 Desserts (5 items)
#     0. ← Back | Reply with number"

# 4. Continue the conversation...
curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \
  -H "Content-Type: application/json" \
  -d '{"from": "+919876543210", "to": "+919988776655", "body": "1"}'

# → Shows pizza products (page 1)

curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \
  -H "Content-Type: application/json" \
  -d '{"from": "+919876543210", "to": "+919988776655", "body": "ADD 1"}'

# → Adds first pizza to cart

curl -X POST http://localhost:8084/api/v1/sms/simulator/inbound \
  -H "Content-Type: application/json" \
  -d '{"from": "+919876543210", "to": "+919988776655", "body": "CART"}'

# → Shows cart with items
```

---

## Summary

```
┌──────────────────────────────────────────────────────────────────────────┐
│              SMS FUNCTIONALITY — KEY TAKEAWAYS                            │
│                                                                          │
│  1. SMS is NOT truly "free" — telecom networks charge somewhere          │
│  2. BUT you can use Android phones + SIM plans to get $0 SMS             │
│  3. The Notification Service uses STRATEGY PATTERN — switch providers    │
│     without changing code (SIMULATOR / ANDROID / JASMIN / TWILIO)       │
│  4. Start with SIMULATOR for development, Android for beta,              │
│     Jasmin+SMPP for production                                           │
│  5. Each tenant gets a unique phone number (physical or virtual)         │
│  6. Session state stored in Redis (30 min timeout)                       │
│  7. Command parser handles 20+ commands with fuzzy matching              │
│  8. Template engine renders dynamic SMS responses                        │
│  9. Rate limiting protects against abuse (Redis-based)                   │
│  10. Complete audit trail of every SMS sent/received                     │
│                                                                          │
│  DEVELOPMENT PATH:                                                       │
│  Simulator ($0) → Android Phone ($0) → Twilio Trial ($0) → Jasmin+SMPP │
│                                                                          │
│  COST TO LAUNCH:                                                         │
│  Software: $0 | Hosting: $20-100/mo | SMS: $0-240/mo                    │
│  Total: $20-340/month to run a full SMS commerce platform!               │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

> **Document Version:** 1.0.0
> **Last Updated:** June 2026
> **Companion Documents:** VISION.md, work_flow.md
```

Save this at **`d:\love\SMS_FUNCTIONALITY.md`**.

---

### Quick Summary of How Free SMS Works:

| Phase | Approach | Cost | How |
|-------|----------|------|-----|
| **Development** | Built-in Simulator | $0 | No real SMS — simulate via curl/dashboard |
| **Testing** | Android Phone + SIM | $0 | Phone runs a bridge app, uses SIM's free SMS plan |
| **Beta** | Multiple Android phones | $0 | 1 phone per tenant, each with free SMS SIM |
| **Production** | Jasmin + SMPP provider | ~$0.002/SMS | Open-source gateway + bulk telecom rates |

The key insight: **your Java code is the same regardless of provider**. The `SmsGatewayInterface` strategy pattern lets you switch between Simulator → Android → Jasmin → Twilio by changing **one line** in `application.properties`.