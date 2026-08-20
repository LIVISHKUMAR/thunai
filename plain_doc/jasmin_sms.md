# 🔧 Jasmin SMS Gateway + SMPP Provider — Complete Implementation Guide

> **Document Type:** Jasmin SMS Gateway Implementation Guide for thunai (SMS Commerce Engine)
> **Version:** 1.0.0
> **Audience:** Backend developers, DevOps engineers, system architects
> **Related Documents:** SMS_FUNCTIONALITY.md, VISION.md, work_flow.md

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [What is Jasmin?](#2-what-is-jasmin)
3. [What is SMPP?](#3-what-is-smpp)
4. [Architecture Overview](#4-architecture-overview)
5. [Complete Data Flow](#5-complete-data-flow)
6. [Setup & Installation](#6-setup--installation)
7. [Jasmin Configuration](#7-jasmin-configuration)
8. [Java Integration with Notification Service](#8-java-integration-with-notification-service)
9. [Multi-Tenant Routing](#9-multi-tenant-routing)
10. [Webhook Handling](#10-webhook-handling)
11. [Error Handling & Retry Logic](#11-error-handling--retry-logic)
12. [Rate Limiting & Throttling](#12-rate-limiting--throttling)
13. [Monitoring & Health Checks](#13-monitoring--health-checks)
14. [Scaling Considerations](#14-scaling-considerations)
15. [Cost Optimization](#15-cost-optimization)
16. [Troubleshooting Guide](#16-troubleshooting-guide)
17. [Migration from Development to Production](#17-migration-from-development-to-production)

---

## 1. Executive Summary

### What You'll Build

This guide walks you through implementing **Jasmin SMS Gateway** as your production SMS delivery system for the thunai SMS Commerce Engine. Jasmin is an open-source SMS gateway that acts as a bridge between your Java backend and telecom networks worldwide.

### Key Benefits of Jasmin

| Feature | Benefit |
|---------|---------|
| **Open Source** | Free, no licensing costs |
| **SMPP Protocol** | Connect to ANY telecom provider globally |
| **Horizontal Scaling** | Multiple instances for high availability |
| **Flexible Routing** | Route different messages to different providers |
| **HTTP API** | Easy integration with your Java backend |
| **Web Dashboard** | Manage connectors, routes, and logs visually |
| **Production Grade** | Used by telecom companies worldwide |

### Cost Model

```
┌──────────────────────────────────────────────────────────────────────┐
│                   JASMIN COST BREAKDOWN                              │
│                                                                      │
│  WHAT IS FREE ($0):                                                  │
│  ├── ✅ Jasmin Software               (Open source)                  │
│  ├── ✅ Jasmin Configuration/Setup   (One-time, no cost)            │
│  └── ✅ Your Java Integration Code    (Your development time)        │
│                                                                      │
│  WHAT COSTS MONEY:                                                   │
│  ├── 💰 Server to run Jasmin          $5-50/month (VPS hosting)     │
│  ├── 💰 Per-SMS Telecom Charge        $0.001-$0.01/SMS             │
│  └── 💰 SMPP Setup/Account Fee        $0-200 (one-time, varies)     │
│                                                                      │
│  ╔════════════════════════════════════════════════════════════════╗ │
│  ║  IMPORTANT: You ALWAYS pay telecom for SMS delivery             ║ │
│  ║  ═══════════════════════════════════════════════════════════   ║ │
│  ║  Whether using Twilio, Jasmin, or direct SMPP:                 ║ │
│  ║  • SMS must go through telecom networks                        ║ │
│  ║  • Telecom charges per SMS (unavoidable cost)                  ║ │
│  ║  • You can only choose the middleman (aggregator)              ║ │
│  ╚════════════════════════════════════════════════════════════════╝ │
│                                                                      │
│  SCENARIO 1: Using Jasmin with Aggregator (MSG91, Kaleyra, etc)    │
│  ────────────────────────────────────────────────────────────────   │
│  Monthly volume: 1M SMS                                             │
│  ├── Jasmin software               $0                              │
│  ├── Server (VPS)                  $20/month                       │
│  ├── Aggregator charge             $0.005/SMS × 1M = $5,000        │
│  ├── Aggregator setup fee          $50 (one-time)                  │
│  │                                                                  │
│  └── **MONTHLY TOTAL: $5,020**                                      │
│      Per SMS: 0.5 cents                                             │
│                                                                      │
│                                                                      │
│  SCENARIO 2: Direct SMPP from Telecom (If volume > 10M SMS/month)  │
│  ────────────────────────────────────────────────────────────────   │
│  Requirements:                                                       │
│  ├── Negotiate directly with telecom (e.g., Airtel, Jio, etc.)     │
│  ├── Typically need: Minimum volume commitment (10M-50M/month)      │
│  ├── Requires: Business registration, tax ID, KYC                  │
│  │                                                                  │
│  Setup:                                                             │
│  ├── You run YOUR OWN SMPP server (or use Jasmin)                  │
│  ├── Jasmin software cost              $0                          │
│  ├── Your server cost                  $20-50/month                │
│  ├── Direct telecom SMS rate           $0.0005-$0.002/SMS          │
│  ├── SMPP setup with telecom           $200-1000 (one-time)        │
│  │                                                                  │
│  Example: 10M SMS/month @ $0.001/SMS                                │
│  └── **MONTHLY TOTAL: $10,050**                                     │
│      Per SMS: 0.1 cents (cheaper than aggregator!)                 │
│                                                                      │
│  BUT CATCH:                                                          │
│  ├── Minimum 10M SMS/month requirement                              │
│  ├── Complex setup & compliance                                     │
│  ├── You manage SMPP connection reliability                         │
│  └── No backup if telecom connection fails                          │
│                                                                      │
│                                                                      │
│  SCENARIO 3: Twilio (Cloud API - Simplest)                         │
│  ────────────────────────────────────────────────────────────────   │
│  ├── Jasmin software                   N/A (use Twilio instead)     │
│  ├── Your server                       $0 (Twilio handles)          │
│  ├── Twilio SMS charge                 $0.0079/SMS × 1M = $7,900    │
│  ├── No setup fees                     $0                          │
│  │                                                                  │
│  └── **MONTHLY TOTAL: $7,900**                                      │
│      Per SMS: 0.79 cents (most expensive but easiest)              │
│                                                                      │
│                                                                      │
│  COST COMPARISON:                                                    │
│  ────────────────────────────────────────────────────────────────   │
│                  At 1M SMS/month   At 10M SMS/month                 │
│  ─────────────────────────────────────────────────────────────      │
│  Twilio           $7,900            $79,000                         │
│  Jasmin+Aggreg.   $5,020            $50,200                         │
│  Jasmin+Direct    N/A (min 10M)     $10,050  ← Best rate!          │
│  ─────────────────────────────────────────────────────────────      │
│                                                                      │
│  BREAKEVEN: Direct SMPP saves money ONLY above 10M SMS/month        │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 1.5 The Truth About "Free" SMS

### Is Jasmin End-to-End Free? NO — Here's Why

```
┌──────────────────────────────────────────────────────────────────────┐
│              THE SMS DELIVERY CHAIN (Unavoidable Costs)              │
│                                                                      │
│  Your App                                                            │
│     ↓                                                                │
│  Jasmin Gateway (FREE - open source)                                 │
│     ↓                                                                │
│  Internet                                                            │
│     ↓                                                                │
│  ╔════════════════════════════════════════════════════════════════╗ │
│  ║  SMPP Provider / Telecom SMSC                                  ║ │
│  ║  ═════════════════════════════════════════                    ║ │
│  ║  THIS PART ALWAYS COSTS MONEY                                 ║ │
│  ║  You cannot bypass this cost                                  ║ │
│  ║  SMS must physically travel through telecom networks          ║ │
│  ║  Telecom operators charge for every SMS                       ║ │
│  ╚════════════════════════════════════════════════════════════════╝ │
│     ↓                                                                │
│  Telecom Network (Cell towers, routing)                              │
│     ↓                                                                │
│  Customer's Phone receives SMS                                       │
│                                                                      │
│                                                                      │
│  THE THREE OPTIONS FOR PAYING TELECOM:                              │
│  ─────────────────────────────────────                              │
│                                                                      │
│  OPTION A: Pay Aggregator (Most Common)                             │
│  └── Aggregator (MSG91, Kaleyra) ──handles─→ Telecom               │
│      ├── You pay aggregator $0.005/SMS                              │
│      ├── Aggregator already negotiated bulk rates with telecom      │
│      ├── Aggregator handles routing/failover                        │
│      └── YOU pay: $0.005/SMS                                        │
│                                                                      │
│  OPTION B: Pay Telecom Directly (Large Volume Only)                 │
│  └── Your Jasmin ─direct SMPP─→ Telecom                            │
│      ├── You negotiate with telecom yourself                        │
│      ├── Minimum volume: usually 10M-50M SMS/month                  │
│      ├── Better rates: $0.0005-$0.002/SMS                           │
│      ├── More complexity: You manage connection                     │
│      └── YOU pay: $0.0005-$0.002/SMS (cheaper!)                    │
│                                                                      │
│  OPTION C: Pay Cloud Service (Easiest)                              │
│  └── Your App ─HTTP─→ Twilio ─SMPP─→ Telecom                      │
│      ├── Twilio handles everything                                  │
│      ├── No setup needed                                            │
│      ├── Automatic failover                                         │
│      └── YOU pay: $0.0079/SMS (most expensive)                     │
│                                                                      │
│                                                                      │
│  FINAL ANSWER TO YOUR QUESTION:                                     │
│  ───────────────────────────────                                    │
│  "Is it end-to-end free?"                                            │
│                                                                      │
│  NO. The SMS delivery itself (the telecom part) costs money.         │
│                                                                      │
│  What IS free:                                                       │
│  ✓ Jasmin software                                                   │
│  ✓ Your Java code                                                    │
│  ✓ Configuration                                                     │
│                                                                      │
│  What COSTS money:                                                   │
│  ✗ Actual SMS delivery (unavoidable)                                │
│  ✗ Server to host Jasmin ($5-50/month)                             │
│                                                                      │
│  You CAN'T avoid SMS costs - they're built into telecom networks.   │
│  You can only choose WHO collects that cost (aggregator vs direct).  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### When to Choose Each Option

```
┌─────────────────────────────────────────────────────────────────────┐
│           WHICH OPTION SHOULD YOU USE?                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  PHASE 1: DEVELOPMENT (0-1 month, < 100K SMS)                      │
│  ─────────────────────────────────────────────                     │
│  Use: Simulator (NO REAL SMS, completely free)                      │
│  ├── SMS are faked in-memory                                        │
│  ├── Perfect for testing                                            │
│  ├── No SMS costs                                                   │
│  ├── No telecom account needed                                      │
│  └── Switch to real SMS later                                       │
│                                                                     │
│  PHASE 2: BETA / TESTING (1-3 months, 100K-1M SMS)                 │
│  ──────────────────────────────────────────────                    │
│  Use: Twilio Free Trial OR Jasmin + MSG91 Aggregator               │
│  ├── Twilio: $15 free credit (enough for testing)                  │
│  ├── Jasmin+MSG91: $0.005/SMS, no minimum                          │
│  ├── Cost: $50-500/month                                            │
│  ├── Fast setup (no paperwork)                                      │
│  └── Good for beta with real users                                  │
│                                                                     │
│  PHASE 3: PRODUCTION (3+ months, 1M-10M SMS)                       │
│  ────────────────────────────────────────────────                  │
│  Use: Jasmin + MSG91 / Kaleyra Aggregator                          │
│  ├── Cost: $0.003-$0.008/SMS (cheaper than Twilio)                │
│  ├── High reliability                                               │
│  ├── Fast setup (1-2 days)                                          │
│  ├── 24/7 support                                                   │
│  ├── Easy to manage                                                 │
│  └── Good for scaling to 10M+ SMS/month                             │
│                                                                     │
│  PHASE 4: SCALE-UP (6+ months, 10M-50M+ SMS)                       │
│  ─────────────────────────────────────────                         │
│  Use: Jasmin + Direct Telecom SMPP                                 │
│  ├── You negotiate with Airtel/Jio/etc directly                    │
│  ├── Cost: $0.0005-$0.002/SMS (best rates!)                       │
│  ├── Savings at 10M: $5,000-$8,000/month vs aggregators           │
│  ├── Setup time: 2-4 weeks (paperwork)                              │
│  ├── Requirements: Business registration, volume commitment        │
│  ├── More complexity: You manage SMPP connection                   │
│  └── Should have backup aggregator for failover                    │
│                                                                     │
│  EDGE CASE: Android Phone Gateway                                   │
│  ────────────────────────────────                                   │
│  Use: Only for very small MVP (< 10K SMS/month)                    │
│  ├── Cost: $0 (uses your SIM's SMS plan)                           │
│  ├── Pros: Truly free for small scale                               │
│  ├── Cons: Unreliable, needs physical phone                        │
│  ├── Throughput: Max ~200 SMS/hour per phone                       │
│  └── Limited to development/MVP only                                │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Real-World Recommendation for thunai

```
Since you're building a SaaS platform with multiple tenants:

DEVELOPMENT PHASE (Now):
  Provider: Simulator (100% free, perfect for coding)
  Cost: $0
  Time: Start immediately

BETA PHASE (Month 2):
  Provider: Jasmin + MSG91 Aggregator
  Cost: ~$20 server + $0.005/SMS
  Time to setup: 1 day
  Benefit: Test with real SMS, real numbers

PRODUCTION PHASE (Month 4+):
  Provider: Jasmin + Direct Telecom (if volume justifies)
               OR Jasmin + MSG91 (if volume < 10M/month)
  Cost: $20 server + $0.0015-0.005/SMS
  Benefit: Lower costs, higher reliability

RECOMMENDATION:
  Start with Simulator → MSG91 → Direct SMPP as you scale
  This way you don't pay for SMS you don't need
```


### Definition

**Jasmin** is an open-source SMS gateway and SMPP server written in Python. It sits between your application and telecom networks, handling the complexity of the SMPP protocol so you can send/receive SMS via simple HTTP API calls.

### Jasmin vs Competitors

```
┌────────────────────────────────────────────────────────────────────┐
│            JASMIN vs OTHER SMS GATEWAYS                            │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  JASMIN (Open Source)                                              │
│  ├── Cost: $0 (software) + SMPP fees                              │
│  ├── Control: 100% (self-hosted)                                  │
│  ├── Scaling: Unlimited (horizontal scaling)                      │
│  ├── Vendor Lock-in: None                                         │
│  ├── Setup Complexity: Medium (requires sysadmin skills)          │
│  └── Best for: Production, high volume, cost-sensitive            │
│                                                                    │
│  TWILIO (Cloud API)                                                │
│  ├── Cost: $0.0079/SMS + infrastructure                           │
│  ├── Control: Limited (cloud provider controls)                   │
│  ├── Scaling: Automatic (no setup needed)                         │
│  ├── Vendor Lock-in: High (tied to Twilio)                       │
│  ├── Setup Complexity: Low (REST API only)                        │
│  └── Best for: Development, small scale, rapid prototyping       │
│                                                                    │
│  VONAGE/NEXMO (Cloud API)                                          │
│  ├── Cost: $0.0068/SMS + infrastructure                           │
│  ├── Control: Limited                                              │
│  ├── Scaling: Automatic                                            │
│  ├── Vendor Lock-in: High                                         │
│  ├── Setup Complexity: Low                                         │
│  └── Best for: Development, SaaS providers                        │
│                                                                    │
│  AFRICA'S TALKING (Regional)                                       │
│  ├── Cost: $0.008/SMS (varies by country)                         │
│  ├── Control: Limited                                              │
│  ├── Scaling: Automatic                                            │
│  ├── Vendor Lock-in: High                                         │
│  ├── Setup Complexity: Low                                         │
│  └── Best for: African market development                         │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### Recommended Strategy for thunai

```
Phase          Provider              Why                 Timeline
────────────────────────────────────────────────────────────────────
1. Development Simulator/Twilio      Quick, no setup     Week 1-2
2. Beta        Jasmin+Local SMPP     Test production     Week 3-4
3. Production  Jasmin+SMPP Provider  Full scale          Week 5+
```

---

## 3. What is SMPP?

### Understanding SMPP

**SMPP** = Short Message Peer-to-Peer

It's a **protocol** (like HTTP or FTP) used to communicate between:
- Your SMS Gateway (Jasmin)
- Telecom carriers' SMS Centers (SMSC)

### How SMPP Works

```
┌─────────────────────────────────────────────────────────────────────┐
│              SMPP PROTOCOL — SIMPLIFIED                             │
│                                                                     │
│  Your Application                 Your Server              Telecom  │
│  (thunai backend)                 (Jasmin)                (SMSC)    │
│         │                            │                      │       │
│         │                            │                      │       │
│  1. Send SMS via HTTP               │                      │       │
│  POST /send {                        │                      │       │
│    to: "+919876543210"              │                      │       │
│    from: "+919988776655"            │                      │       │
│    body: "Hello"                    │                      │       │
│  }                                  │                      │       │
│  ├─────────────────────────────────>│                      │       │
│                                     │                      │       │
│  2. Jasmin converts to SMPP PDU     │                      │       │
│  (Protocol Data Unit)               │                      │       │
│                                     │ SMPP Protocol        │       │
│                                     │ over TCP (port 2775) │       │
│                                     │                      │       │
│  3. Jasmin connects to SMSC         │                      │       │
│  via SMPP and sends SMS             │                      │       │
│                                     │ ────────────────────>│       │
│                                     │ submit_sm PDU        │       │
│                                     │                      │       │
│  4. SMSC confirms receipt           │                      │       │
│                                     │<────────────────────│       │
│                                     │ submit_sm_resp       │       │
│  5. Jasmin gets receipt ID          │                      │       │
│                                     │                      │       │
│  6. Return to your app              │                      │       │
│  {                                  │                      │       │
│    messageId: "msg-12345"           │                      │       │
│    status: "SENT"                   │                      │       │
│  }                                  │                      │       │
│  <──────────────────────────────────│                      │       │
│                                     │                      │       │
│  7. SMS travels through network     │                      │       │
│                                     │                      │       │
│  8. Delivery report                 │                      │       │
│  (optional, if configured)          │                      │       │
│                                     │<────────────────────│       │
│                                     │ deliver_sm PDU       │       │
│                                     │                      │       │
│  9. Jasmin sends webhook to app     │                      │       │
│  POST http://yourserver:8084        │                      │       │
│  /api/v1/sms/webhook/delivery       │                      │       │
│  {                                  │                      │       │
│    messageId: "msg-12345"           │                      │       │
│    status: "DELIVERED"              │                      │       │
│  }                                  │                      │       │
│  ├─────────────────────────────────>│                      │       │
│                                     │                      │       │
└─────────────────────────────────────────────────────────────────────┘
```

### SMPP Key Concepts

| Term | Meaning |
|------|---------|
| **SMSC** | Short Message Service Center (telecom's SMS system) |
| **PDU** | Protocol Data Unit (SMPP message format) |
| **Bind** | Connection between Jasmin and SMSC |
| **Transceiver** | Bind type that allows both sending AND receiving |
| **Bind Mode** | TX (send only), RX (receive only), TRX (both) |
| **Message ID** | Unique identifier assigned by SMSC for tracking |
| **Delivery Report** | Confirmation that SMS was delivered to customer phone |

### SMPP Bind Types

```
┌────────────────────────────────────────────────────────────┐
│         SMPP BIND MODES                                    │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  TRANSMITTER (TX) — Send Only                             │
│  ├── Can send SMS to customers                            │
│  ├── Cannot receive SMS from customers                    │
│  ├── Use for: Notifications, alerts, promotions          │
│  └── Example: "Your order #123 is confirmed"             │
│                                                            │
│  RECEIVER (RX) — Receive Only                             │
│  ├── Can receive SMS from customers                       │
│  ├── Cannot send SMS                                      │
│  ├── Use for: Listening to inbound messages               │
│  └── Example: Receiving "MENU" from customers             │
│                                                            │
│  TRANSCEIVER (TRX) — Send AND Receive                     │
│  ├── Can both send and receive SMS                        │
│  ├── Single connection for bidirectional communication    │
│  ├── Use for: Interactive SMS applications                │
│  └── Most efficient for e-commerce (Recommended!)         │
│                                                            │
│  For thunai, use TRANSCEIVER because:                     │
│  ├── Customers send SMS ("MENU", "1", "CHECKOUT")       │
│  ├── System sends SMS (menu options, order status)        │
│  ├── Single connection saves resources                    │
│  └── Reduces latency and complexity                       │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 4. Architecture Overview

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                   THUNAI + JASMIN ARCHITECTURE                      │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │              TELECOM NETWORK                                 │   │
│  │  (Customer phones & SMSC)                                    │   │
│  │                                                              │   │
│  │  📱 Customer Phone                                           │   │
│  │   Sends: "MENU"                                              │   │
│  │   Receives: Menu options                                     │   │
│  │                                                              │   │
│  │                   ┌── SMPP (port 2775) ──┐                  │   │
│  │                   │  (Telecom provides)   │                  │   │
│  │                   ▼                       │                  │   │
│  │                📡 SMSC                    │                  │   │
│  │           (Telecom SMS Center)           │                  │   │
│  │                   ▲                       │                  │   │
│  │                   └───────────────────────┘                  │   │
│  └───────────────┬──────────────────────────────────────────────┘   │
│                  │                                                   │
│                  │ SMPP Protocol                                     │
│                  │ (TCP Connection)                                  │
│                  │ Port 2775                                         │
│                  ▼                                                   │
│  ┌───────────────────────────────────────────────────────────────┐   │
│  │              JASMIN SMS GATEWAY                                │   │
│  │           (Your Docker Container)                              │   │
│  │                                                               │   │
│  │  ┌─ SMPP Connectors (External) ──────────────────────────┐   │   │
│  │  │                                                       │   │   │
│  │  │  Connector 1: Primary SMPP Provider                   │   │   │
│  │  │  ├── Provider: MSG91 (India)                          │   │   │
│  │  │  ├── host: smpp.msg91.com                             │   │   │
│  │  │  ├── port: 2775                                        │   │   │
│  │  │  ├── bind_type: TRX (transceiver)                      │   │   │
│  │  │  └── status: Connected ✅                             │   │   │
│  │  │                                                       │   │   │
│  │  │  Connector 2: Backup SMPP Provider                    │   │   │
│  │  │  ├── Provider: Kaleyra (Backup)                       │   │   │
│  │  │  ├── host: smpp.kaleyra.com                           │   │   │
│  │  │  ├── port: 2775                                        │   │   │
│  │  │  ├── bind_type: TX (send only)                         │   │   │
│  │  │  └── status: Connected ✅                             │   │   │
│  │  │                                                       │   │   │
│  │  └───────────────────────────────────────────────────────┘   │   │
│  │                                                               │   │
│  │  ┌─ HTTP API Server (Internal) ──────────────────────────┐   │   │
│  │  │  Port: 8990                                            │   │   │
│  │  │  Endpoints:                                            │   │   │
│  │  │  ├── POST /send              (send SMS)               │   │   │
│  │  │  ├── GET  /balance           (check credits)          │   │   │
│  │  │  ├── GET  /rate              (get SMS rate)           │   │   │
│  │  │  └── POST /batch             (batch operations)       │   │   │
│  │  │                                                        │   │   │
│  │  │  Authentication: Basic Auth (username:password)        │   │   │
│  │  │  User: jadmin                                          │   │   │
│  │  │  Password: (configured)                                │   │   │
│  │  └───────────────────────────────────────────────────────┘   │   │
│  │                                                               │   │
│  │  ┌─ Routing Engine ───────────────────────────────────────┐   │   │
│  │  │                                                        │   │   │
│  │  │  Route 1: INBOUND SMS                                 │   │   │
│  │  │  ├── When SMS received on SMPP connector              │   │   │
│  │  │  ├── To: Webhook → http://notification-svc:8084/...  │   │   │
│  │  │                                                        │   │   │
│  │  │  Route 2: OUTBOUND SMS                                │   │   │
│  │  │  ├── When HTTP POST /send received                    │   │   │
│  │  │  ├── To: SMPP connector → Telecom → Customer          │   │   │
│  │  │                                                        │   │   │
│  │  │  Route 3: DELIVERY REPORTS                            │   │   │
│  │  │  ├── When SMSC sends delivery_sm                      │   │   │
│  │  │  ├── To: Webhook → http://notification-svc:8084/...  │   │   │
│  │  │                                                        │   │   │
│  │  │  Route 4: MULTI-TENANT ROUTING                        │   │   │
│  │  │  ├── Route by phone number (+91-AAA → tenant-1)       │   │   │
│  │  │  ├── Route by customer number                         │   │   │
│  │  │  ├── Different SMPP connectors for different regions  │   │   │
│  │  │                                                        │   │   │
│  │  └────────────────────────────────────────────────────────┘   │   │
│  │                                                               │   │
│  │  ┌─ Web Dashboard (Port 8991) ───────────────────────────┐   │   │
│  │  │  ├── Monitor SMS traffic in real-time                 │   │   │
│  │  │  ├── Manage connectors & routes                        │   │   │
│  │  │  ├── View logs and statistics                          │   │   │
│  │  │  ├── Manage users (jadmin access)                      │   │   │
│  │  │  └── System health monitoring                          │   │   │
│  │  └────────────────────────────────────────────────────────┘   │   │
│  │                                                               │   │
│  └─────────────────┬──────────────────────────────────────────────┘   │
│                    │                                                   │
│                    │ HTTP                                              │
│                    │ Port 8990                                         │
│                    │ (Internal network)                                │
│                    ▼                                                   │
│  ┌───────────────────────────────────────────────────────────────┐   │
│  │         THUNAI MICROSERVICES                                  │   │
│  │                                                               │   │
│  │  Notification Service (Port 8084)                             │   │
│  │  ├── Receives inbound SMS via webhook                         │   │
│  │  ├── Processes SMS commands (MENU, 1, CHECKOUT, etc)        │   │
│  │  ├── Calls other services (Order, Customer, Catalog)         │   │
│  │  ├── Builds SMS response                                      │   │
│  │  ├── Sends SMS via HTTP to Jasmin                             │   │
│  │  └── Receives delivery reports via webhook                    │   │
│  │                                                               │   │
│  │  Other Services                                               │   │
│  │  ├── Catalog Service (Port 8081)                              │   │
│  │  ├── Customer Service (Port 8082)                             │   │
│  │  ├── Order Service (Port 8083)                                │   │
│  │  └── Shared/Admin Service (Port 8085)                         │   │
│  │                                                               │   │
│  │  Database                                                     │   │
│  │  └── PostgreSQL (with RLS for multi-tenancy)                 │   │
│  │                                                               │   │
│  │  Cache                                                        │   │
│  │  └── Redis (session management, rate limiting)                │   │
│  │                                                               │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Network Configuration

```
┌──────────────────────────────────────────────────────────────────┐
│             NETWORK TOPOLOGY                                     │
│                                                                  │
│  EXTERNAL (Internet)                                             │
│  ├── Telecom SMSC (MSG91, Kaleyra, etc.)                         │
│  │   └── SMPP connections on port 2775                           │
│  │                                                               │
│  └── External Monitoring (optional)                              │
│                                                                  │
│  ────────────────────────────────────────────────────────────   │
│  FIREWALL                                                        │
│  ────────────────────────────────────────────────────────────   │
│                                                                  │
│  INTERNAL (Docker Network / Private Network)                     │
│  ├── Jasmin Container                                            │
│  │   ├── Port 2775 (to SMSC, outbound via firewall)             │
│  │   ├── Port 8990 (HTTP API, internal only)                    │
│  │   ├── Port 8991 (Dashboard, internal only)                   │
│  │   └── Redis (connection, internal only)                      │
│  │                                                               │
│  ├── Notification Service Container                              │
│  │   ├── Port 8084 (HTTP API, receives webhooks from Jasmin)    │
│  │   └── HTTP to Jasmin:8990 (sends SMS)                        │
│  │                                                               │
│  ├── PostgreSQL Container                                        │
│  └── Redis Container                                             │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 5. Complete Data Flow

### Scenario: Customer Sends "MENU"

```
STEP 1: Inbound SMS to Jasmin
═════════════════════════════

Customer Phone                Telecom SMSC           Jasmin Gateway
┌───────────────┐            ┌──────────────┐        ┌──────────────┐
│               │  SMS Text  │              │        │              │
│  📱 Customer  │─"MENU"────>│  📡 SMSC     │        │   Jasmin     │
│  +919876..    │            │ (Telecom)    │        │              │
│               │            │              │        │              │
└───────────────┘            └──────────────┘        └──────────────┘
                                   │
                                   │ SMPP Protocol
                                   │ deliver_sm PDU
                                   ▼
                            Jasmin Receives SMS:
                            ├── from: +919876543210
                            ├── to: +919988776655
                            ├── body: "MENU"
                            └── message_id: abc123


STEP 2: Jasmin Routes to Notification Service
═════════════════════════════════════════════

Jasmin finds inbound route:
├── Route type: HTTP Webhook
├── Destination: http://notification-service:8084/api/v1/sms/webhook/inbound
└── Forwards:
    {
      "from": "+919876543210",
      "to": "+919988776655",
      "body": "MENU",
      "message_id": "abc123"
    }


STEP 3: Notification Service Processes SMS
═════════════════════════════════════════════

POST /api/v1/sms/webhook/inbound
│
├─ 1. Log raw SMS to PostgreSQL
│
├─ 2. Identify Tenant
│    SELECT tenant_id FROM tenant_lookup
│    WHERE phone_number = '+919988776655'
│    Result: tenant_id = 'T-001' (Pizza Palace)
│
├─ 3. Identify/Create Customer
│    SELECT * FROM customers WHERE phone = '+919876543210'
│    Result: customer_id = 'C-042' (returning customer)
│
├─ 4. Get/Restore Session State
│    GET session:C-042:T-001 (from Redis)
│    Result: fresh session (no active session found)
│
├─ 5. Parse Command
│    Input: "MENU"
│    Parsed: { type: "MAIN_MENU" }
│
├─ 6. Build Response
│    Template: sms_template_main_menu.txt
│    Substitute variables, format menu options
│    Result: "👋 Welcome to Pizza Palace!
│             1. View Menu
│             2. Today's Special
│             3. My Cart
│             (160 chars - fits in 1 SMS)"
│
├─ 7. Save Session State to Redis
│    SET session:C-042:T-001 '{
│      "state": "MAIN_MENU",
│      "lastActivity": "2026-06-21T12:30:00",
│      "history": []
│    }' EX 1800
│
└─ 8. Send Outbound SMS


STEP 4: Notification Service Sends SMS to Jasmin
═════════════════════════════════════════════════

POST http://jasmin:8990/send
Authorization: Basic jadmin:password
Content-Type: application/x-www-form-urlencoded

to=+919876543210
&from=+919988776655
&content=👋 Welcome to Pizza Palace!
1. View Menu
2. Today's Special
3. My Cart
&coding=8

(coding=8 means UTF-8 for emoji support)


STEP 5: Jasmin Converts to SMPP
════════════════════════════════

Jasmin takes HTTP POST and:
├── Parses parameters
├── Converts to SMPP Protocol Data Unit (PDU)
│   └── PDU type: submit_sm
│       ├── destination_addr: +919876543210
│       ├── source_addr: +919988776655
│       ├── short_message: "👋 Welcome to Pizza Palace!..."
│       └── message_id: xyz789 (assigned by Jasmin)
└── Sends via SMPP connection to SMSC


STEP 6: SMPP to Telecom SMSC
═════════════════════════════

Jasmin (SMPP Client)        Telecom SMSC
┌──────────────────┐        ┌──────────────┐
│  Jasmin          │        │              │
│  SMPP Connector  │──────> │  📡 SMSC     │
│  (TRX bind)      │  SMPP  │ (Telecom)    │
└──────────────────┘        └──────────────┘
   Connected via
   TCP port 2775


STEP 7: SMSC Routes to Customer
════════════════════════════════

Telecom SMSC:
├── Validates message
├── Routes via network
└── Delivers to Customer Phone


STEP 8: Customer Receives SMS
══════════════════════════════

Customer Phone
┌───────────────────────────────────┐
│  📱 iPhone/Android                │
│                                   │
│  New SMS                          │
│  From: Pizza Palace               │
│        +919988776655              │
│                                   │
│  👋 Welcome to Pizza Palace!      │
│  1. View Menu                     │
│  2. Today's Special               │
│  3. My Cart                       │
│  Reply with number                │
│                                   │
│  [Reply] [Delete]                 │
└───────────────────────────────────┘
│
│ Total time: 2-5 seconds


STEP 9: Delivery Report (Optional)
═══════════════════════════════════

SMSC sends back SMPP deliver_sm:
├── message_id: xyz789 (matches original)
├── final_date: timestamp
├── message_state: DELIVERED
└── error_code: 0


STEP 10: Jasmin Forwards Delivery Report
═════════════════════════════════════════

Jasmin converts deliver_sm to HTTP:

POST http://notification-service:8084/api/v1/sms/webhook/delivery
Content-Type: application/json

{
  "message_id": "xyz789",
  "status": "DELIVERED",
  "timestamp": "2026-06-21T12:30:05Z"
}


STEP 11: Notification Service Logs Delivery
═════════════════════════════════════════════

UPDATE sms_messages
SET status = 'DELIVERED',
    delivered_at = now()
WHERE message_id = 'xyz789'
```

---

## 6. Setup & Installation

### Prerequisites

```bash
# What you need before starting:

1. Server/VPS with:
   ├── OS: Linux (Ubuntu 20.04 or later recommended)
   ├── CPU: 2+ cores
   ├── RAM: 2+ GB
   ├── Storage: 20+ GB
   ├── Internet: Stable, can handle SMPP connections
   └── Firewall: Allow outbound to SMPP ports (usually 2775)

2. SMPP Account from Telecom Provider:
   ├── Example: MSG91, Kaleyra, Africa's Talking, Twilio
   ├── You'll get: host, port, username, password
   ├── Decide: Which country/region?
   ├── Cost: Ranges from $0.001-$0.01 per SMS
   └── Setup time: 1-3 business days typically

3. Docker and Docker Compose:
   ├── Docker 20.10+
   ├── Docker Compose 1.29+
   └── (if deploying with Docker)

4. Your thunai Backend:
   ├── Notification Service running
   ├── HTTP endpoint for receiving webhooks (port 8084)
   ├── Firewall allows Jasmin to HTTP POST to it
   └── PostgreSQL + Redis running
```

### Installation Option 1: Docker Compose (Recommended)

```bash
# Create directory for Jasmin configuration
mkdir -p /opt/jasmin-gateway
cd /opt/jasmin-gateway

# Create docker-compose.yml
cat > docker-compose.yml << 'EOF'
version: '3.9'

services:
  # Redis - Required by Jasmin for state management
  redis:
    image: redis:7-alpine
    container_name: jasmin-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    restart: unless-stopped
    networks:
      - jasmin-network

  # Jasmin SMS Gateway
  jasmin:
    image: jookies/jasmin:latest
    container_name: jasmin-gateway
    depends_on:
      - redis
    ports:
      - "2775:2775"    # SMPP port (external to SMSC)
      - "8990:8990"    # HTTP API port (internal)
      - "8991:8991"    # Web dashboard (internal)
    environment:
      REDIS_CLIENT_HOST: redis
      REDIS_CLIENT_PORT: 6379
      JASMIN_LISTEN_ADDR: 0.0.0.0
    volumes:
      - jasmin-store:/var/jasmin/store
      - jasmin-logs:/var/log/jasmin
      - ./jasmin-init.py:/docker-entrypoint-init.d/10-init.py
    restart: unless-stopped
    networks:
      - jasmin-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8990/"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

volumes:
  redis-data:
  jasmin-store:
  jasmin-logs:

networks:
  jasmin-network:
    driver: bridge
EOF

# Start Jasmin
docker-compose up -d

# Check logs
docker-compose logs -f jasmin

# Verify running
docker-compose ps
```

### Installation Option 2: Standalone (Ubuntu/Debian)

```bash
# Install dependencies
sudo apt-get update
sudo apt-get install -y python3 python3-pip redis-server

# Install Jasmin from pip
sudo pip3 install jasmin-sms

# Create Jasmin user
sudo useradd -m -s /bin/bash jasmin

# Create directories
sudo mkdir -p /opt/jasmin/{config,store,logs}
sudo chown -R jasmin:jasmin /opt/jasmin

# Start Redis
sudo systemctl start redis-server
sudo systemctl enable redis-server

# Create systemd service for Jasmin
sudo cat > /etc/systemd/system/jasmin.service << 'EOF'
[Unit]
Description=Jasmin SMS Gateway
After=network.target redis-server.service
Requires=redis-server.service

[Service]
Type=simple
User=jasmin
WorkingDirectory=/opt/jasmin
ExecStart=/usr/local/bin/jasmin-cli.py
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Enable and start Jasmin
sudo systemctl daemon-reload
sudo systemctl enable jasmin
sudo systemctl start jasmin

# Check status
sudo systemctl status jasmin
```

---

## 7. Jasmin Configuration

### Initial Setup - Connecting SMPP Provider

```bash
# Connect to Jasmin telnet CLI
telnet localhost 8990
# Username: jadmin
# Password: jpwd

# (You're now in Jasmin CLI)
```

### Step 1: Add SMPP Connector (Example: MSG91)

```bash
# Add new SMPP connector
smppccm -a

# When prompted, enter:
cid msg91-connector
description "MSG91 SMPP Connection"
host smpp.msg91.com
port 2775
username your_msg91_username
password your_msg91_password
bind_type TRX
bind_ton INTERNATIONAL
bind_npi ISDN
system_type SMPP
timeout_response 30
connect_timeout 10
reconnect_on_connection_loss True
reconnect_delay 10
submit_with_udhi False

# Save configuration
persist

# List connectors to verify
smppccm -l

# Start the connector
smppccm -1 msg91-connector

# Check connector status
smppccm -s msg91-connector
# Should show: "Bound" or "Connected"
```

### Step 2: Add HTTP Route for Inbound SMS

```bash
# Add inbound route (SMS received -> webhook to your backend)
mtrouter -a

# When prompted:
type DefaultRoute
description "Inbound SMS to Notification Service"
connector smppc(msg91-connector)
order 0

# Alternative: If you need to route by phone number
# (Instead of DefaultRoute, use MTRoute)
type MTRoute
description "Route by destination number"
connector smppc(msg91-connector)
destination_matching .*
order 10

# Save
persist
```

### Step 3: Add HTTP Callback Route for Delivery Reports

```bash
# Add delivery report route
# (When SMSC sends delivery confirmations)

# In Jasmin web dashboard (port 8991):
# Go to Settings → Routes → Add New Route
# Type: DeliverSMRoute
# Destination: http://notification-service:8084/api/v1/sms/webhook/delivery
# Method: POST
# Backup URL: http://notification-service:8084/api/v1/sms/webhook/delivery-fallback
```

### Step 4: Configure Webhooks

```bash
# Configuration file: /opt/jasmin/config/jasmin.conf (or in Docker volume)

# Add to config:
[http_client]
default_timeout = 30
workers_threads = 4

# Webhook for inbound SMS
[http_inbound]
url = http://notification-service:8084/api/v1/sms/webhook/inbound
method = POST
timeout = 10

# Webhook for delivery reports
[http_delivery]
url = http://notification-service:8084/api/v1/sms/webhook/delivery
method = POST
timeout = 10

# Webhook for errors
[http_error]
url = http://notification-service:8084/api/v1/sms/webhook/error
method = POST
timeout = 10
```

### Step 5: Add HTTP Users (for API authentication)

```bash
# Connect to telnet CLI
telnet localhost 8990

# Add user for your application
httpccm -a

# When prompted:
username notification-service
password secure-random-password-here
group admin
# or
group default

# Save
persist
```

### Step 6: Test Configuration

```bash
# From telnet CLI:

# Test SMPP connection
smppccm -s msg91-connector
# Expected: "Connected" or "Bound"

# Check active users
httpccm -l
# Should show your notification-service user

# Check routes
mtrouter -l
# Should show your inbound and delivery routes

# View recent logs
tail -f /var/log/jasmin/jasmin.log
```

### Complete Jasmin Configuration File Example

```yaml
# /etc/jasmin/jasmin.conf or in Docker volume

[http_client]
default_timeout = 30
workers_threads = 8

[sms_routing]
# Route all SMS to http webhook first, then to SMPP if not handled
default_routing = http

[http_server]
host = 0.0.0.0
port = 8990
bind_addr = 0.0.0.0
backlog = 256
pool_size = 8

[manager_http]
host = 0.0.0.0
port = 8991
authentication = basic
auth_file = /etc/jasmin/users.txt

[smpp_server]
port = 2775
interface = 0.0.0.0
timeout = 60

[redis]
host = localhost
port = 6379
db = 0
password =

[log_level]
# DEBUG, INFO, WARNING, ERROR
console_level = INFO
file_level = INFO
```

### Verify Configuration

```bash
# Check if Jasmin is accepting SMPP connections
netstat -tlnp | grep jasmin
# Should show:
# - Port 2775 (SMPP)
# - Port 8990 (HTTP API)
# - Port 8991 (Dashboard)

# Test HTTP API
curl -X POST "http://localhost:8990/send" \
  -u "notification-service:password" \
  -d "to=+919876543210" \
  -d "from=+919988776655" \
  -d "content=Test message" \
  -d "coding=0"

# Should return something like:
# {"message_id": "msg-12345", "status": "SENT"}
```

---

## 8. Java Integration with Notification Service

### Architecture

```
Notification Service
├── SmsGatewayInterface (Abstract interface)
├── JasminGatewayClient (Implementation)
├── SmsSendService (Uses gateway to send)
├── SmsReceiveController (Receives webhooks from Jasmin)
└── SmsMessageRepository (Logs all SMS)
```

### Data Models

```java
// SmsMessage.java — Represents SMS in database
@Entity
@Table(name = "sms_messages", schema = "notification_service")
public class SmsMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @TenantId  // Multi-tenancy support
    private UUID tenantId;
    
    private UUID customerId;
    private String fromPhone;      // Gateway phone number
    private String toPhone;        // Customer phone number
    private String body;
    
    @Enumerated(EnumType.STRING)
    private SmsDirection direction; // INBOUND or OUTBOUND
    
    @Enumerated(EnumType.STRING)
    private SmsStatus status;       // PENDING, SENT, DELIVERED, FAILED
    
    private String providerMessageId;     // Message ID from Jasmin
    private String providerName;          // "jasmin", "twilio", etc.
    
    @Column(columnDefinition = "TEXT")
    private String metadata;        // JSON with extra info
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    private String failureReason;
}

// SmsSendRequest.java
@Data
@Builder
public class SmsSendRequest {
    private UUID tenantId;
    private UUID customerId;
    private String toPhone;
    private String fromPhone;  // Tenant's assigned phone number
    private String body;
    private String templateId; // Optional: if using SMS template
    private Map<String, Object> templateVars;
    private Integer priority;  // 0 (low) to 100 (high)
    private Integer retryCount;
    private LocalDateTime scheduledAt; // For scheduled SMS
}

// SmsSendResponse.java
@Data
@Builder
public class SmsSendResponse {
    private String messageId;
    private SmsStatus status;
    private String providerMessageId;
    private Integer segments;
    private LocalDateTime sentAt;
}

// SmsReceiveWebhookPayload.java
@Data
public class SmsReceiveWebhookPayload {
    private String from;           // Customer phone
    private String to;             // Tenant's phone number
    private String body;           // SMS body
    private String messageId;      // Jasmin's message ID
    private Long timestamp;
    private String gatewayPhone;
}

// SmsDeliveryReportWebhookPayload.java
@Data
public class SmsDeliveryReportWebhookPayload {
    private String messageId;      // Original message ID
    private String status;         // DELIVERED, FAILED, EXPIRED
    private Long timestamp;
    private String errorCode;
    private String errorDescription;
}
```

### Implementation: JasminGatewayClient

```java
// JasminGatewayClient.java
@Service
@Slf4j
public class JasminGatewayClient implements SmsGatewayInterface {
    
    @Value("${sce.sms.jasmin.base-url}")
    private String jasminBaseUrl;
    
    @Value("${sce.sms.jasmin.username}")
    private String jasminUsername;
    
    @Value("${sce.sms.jasmin.password}")
    private String jasminPassword;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private SmsMessageRepository smsMessageRepository;
    
    @Autowired
    private SmsDeliveryReportService deliveryReportService;
    
    @Override
    public SmsSendResponse sendSms(SmsSendRequest request) {
        try {
            // 1. Validate inputs
            validateSmsRequest(request);
            
            // 2. Prepare HTTP request to Jasmin
            String url = jasminBaseUrl + "/send";
            HttpHeaders headers = createBasicAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // 3. Build request body (form-encoded)
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("to", normalizePhoneNumber(request.getToPhone()));
            body.add("from", normalizePhoneNumber(request.getFromPhone()));
            body.add("content", request.getBody());
            body.add("coding", "8");  // UTF-8 for emoji support
            body.add("priority", String.valueOf(request.getPriority() != null ? 
                    request.getPriority() : 5));
            
            HttpEntity<MultiValueMap<String, String>> httpEntity = 
                    new HttpEntity<>(body, headers);
            
            // 4. Send to Jasmin
            ResponseEntity<JasminSendResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    JasminSendResponse.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new SmsGatewayException(
                    "Jasmin returned " + response.getStatusCode()
                );
            }
            
            // 5. Extract message ID
            JasminSendResponse jasminResponse = response.getBody();
            if (jasminResponse == null || jasminResponse.getMessageId() == null) {
                throw new SmsGatewayException("No message ID returned by Jasmin");
            }
            
            // 6. Log to database
            SmsMessage smsMessage = SmsMessage.builder()
                    .tenantId(request.getTenantId())
                    .customerId(request.getCustomerId())
                    .fromPhone(request.getFromPhone())
                    .toPhone(request.getToPhone())
                    .body(request.getBody())
                    .direction(SmsDirection.OUTBOUND)
                    .status(SmsStatus.SENT)
                    .providerMessageId(jasminResponse.getMessageId())
                    .providerName("jasmin")
                    .metadata(buildMetadata(request, response))
                    .build();
            
            SmsMessage saved = smsMessageRepository.save(smsMessage);
            
            // 7. Return success response
            return SmsSendResponse.builder()
                    .messageId(saved.getId().toString())
                    .providerMessageId(jasminResponse.getMessageId())
                    .status(SmsStatus.SENT)
                    .segments(jasminResponse.getSegments())
                    .sentAt(saved.getCreatedAt())
                    .build();
            
        } catch (RestClientException e) {
            log.error("Failed to send SMS via Jasmin", e);
            
            // Log failed attempt
            SmsMessage failedMessage = SmsMessage.builder()
                    .tenantId(request.getTenantId())
                    .customerId(request.getCustomerId())
                    .fromPhone(request.getFromPhone())
                    .toPhone(request.getToPhone())
                    .body(request.getBody())
                    .direction(SmsDirection.OUTBOUND)
                    .status(SmsStatus.FAILED)
                    .providerName("jasmin")
                    .failureReason(e.getMessage())
                    .build();
            
            smsMessageRepository.save(failedMessage);
            
            // Retry logic (if not exceeded)
            if (request.getRetryCount() < 3) {
                // Schedule retry using scheduler
                scheduleRetry(request);
            }
            
            throw new SmsGatewayException("Failed to send SMS", e);
        }
    }
    
    /**
     * Receive inbound SMS from Jasmin webhook
     */
    public void handleInboundSms(SmsReceiveWebhookPayload payload) {
        try {
            log.info("Received inbound SMS from: {}, to: {}, body: {}", 
                    payload.getFrom(), payload.getTo(), payload.getBody());
            
            // 1. Resolve tenant from phone number
            UUID tenantId = resolveTenantFromPhoneNumber(payload.getTo());
            
            // 2. Create or get customer
            UUID customerId = getOrCreateCustomer(tenantId, payload.getFrom());
            
            // 3. Log SMS to database
            SmsMessage smsMessage = SmsMessage.builder()
                    .tenantId(tenantId)
                    .customerId(customerId)
                    .fromPhone(payload.getFrom())
                    .toPhone(payload.getTo())
                    .body(payload.getBody())
                    .direction(SmsDirection.INBOUND)
                    .status(SmsStatus.RECEIVED)
                    .providerMessageId(payload.getMessageId())
                    .providerName("jasmin")
                    .metadata(buildMetadata(payload))
                    .build();
            
            SmsMessage saved = smsMessageRepository.save(smsMessage);
            
            // 4. Publish event for processing
            publishSmsReceivedEvent(saved);
            
        } catch (Exception e) {
            log.error("Failed to handle inbound SMS", e);
            // Still return 200 to Jasmin to avoid retry loops
        }
    }
    
    /**
     * Receive delivery report from Jasmin webhook
     */
    public void handleDeliveryReport(SmsDeliveryReportWebhookPayload payload) {
        try {
            log.info("Received delivery report for: {} - {}", 
                    payload.getMessageId(), payload.getStatus());
            
            // 1. Find original SMS by provider message ID
            Optional<SmsMessage> smsOpt = smsMessageRepository
                    .findByProviderMessageId(payload.getMessageId());
            
            if (smsOpt.isEmpty()) {
                log.warn("Delivery report received for unknown message: {}", 
                        payload.getMessageId());
                return;
            }
            
            SmsMessage sms = smsOpt.get();
            
            // 2. Update status
            SmsStatus newStatus;
            if ("DELIVERED".equals(payload.getStatus())) {
                newStatus = SmsStatus.DELIVERED;
                sms.setDeliveredAt(LocalDateTime.now());
            } else if ("FAILED".equals(payload.getStatus())) {
                newStatus = SmsStatus.FAILED;
                sms.setFailedAt(LocalDateTime.now());
                sms.setFailureReason(payload.getErrorDescription());
            } else if ("EXPIRED".equals(payload.getStatus())) {
                newStatus = SmsStatus.FAILED;
                sms.setFailedAt(LocalDateTime.now());
                sms.setFailureReason("Message expired");
            } else {
                newStatus = SmsStatus.PENDING;
            }
            
            sms.setStatus(newStatus);
            smsMessageRepository.save(sms);
            
            // 3. Publish delivery event (for customer notifications, analytics, etc)
            publishDeliveryReportEvent(sms);
            
        } catch (Exception e) {
            log.error("Failed to handle delivery report", e);
        }
    }
    
    // Helper methods
    
    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = jasminUsername + ":" + jasminPassword;
        String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }
    
    private void validateSmsRequest(SmsSendRequest request) {
        if (request.getToPhone() == null || request.getToPhone().isEmpty()) {
            throw new IllegalArgumentException("toPhone is required");
        }
        if (request.getFromPhone() == null || request.getFromPhone().isEmpty()) {
            throw new IllegalArgumentException("fromPhone is required");
        }
        if (request.getBody() == null || request.getBody().isEmpty()) {
            throw new IllegalArgumentException("body is required");
        }
        if (request.getBody().length() > 1600) {  // 10 SMS max (160 chars each)
            throw new IllegalArgumentException("SMS body too long");
        }
    }
    
    private String normalizePhoneNumber(String phone) {
        // Remove spaces, dashes, brackets
        // Ensure starts with +
        // Example: "9876543210" -> "+919876543210"
        phone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        if (!phone.startsWith("+")) {
            // Assume India if no country code
            if (phone.startsWith("0")) {
                phone = phone.substring(1);
            }
            phone = "+91" + phone;
        }
        return phone;
    }
    
    private UUID resolveTenantFromPhoneNumber(String phoneNumber) {
        // Lookup in tenant_lookup table
        // SELECT tenant_id FROM shared.tenant_lookup 
        // WHERE phone_number = phoneNumber
        // Throw exception if not found
        throw new TenantNotFoundException(
            "No tenant configured for phone: " + phoneNumber
        );
    }
    
    private UUID getOrCreateCustomer(UUID tenantId, String phoneNumber) {
        // SELECT * FROM customers WHERE phone = phoneNumber AND tenant_id = tenantId
        // If not found, create new customer
        // Return customer UUID
        throw new NotImplementedException("Implement via CustomerService");
    }
    
    private String buildMetadata(SmsSendRequest request, 
                                 ResponseEntity<JasminSendResponse> response) {
        // Build JSON metadata for storage
        Map<String, Object> meta = new HashMap<>();
        meta.put("priority", request.getPriority());
        meta.put("scheduled", request.getScheduledAt());
        // ... etc
        return new JSONObject(meta).toString();
    }
    
    private void publishSmsReceivedEvent(SmsMessage smsMessage) {
        // Publish to RabbitMQ for async processing
        // Topic: sms.received
        // Message: SmsReceivedEvent
    }
    
    private void publishDeliveryReportEvent(SmsMessage smsMessage) {
        // Publish to RabbitMQ
        // Topic: sms.delivered
        // Message: SmsDeliveryEvent
    }
    
    private void scheduleRetry(SmsSendRequest request) {
        // Use Spring Scheduler or scheduled task
        // Wait exponential backoff (5s, 10s, 30s)
        // Then retry sending
    }
}

// JasminSendResponse.java — Response from Jasmin HTTP API
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JasminSendResponse {
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("status")
    private String status;  // "sent", "failed", etc
    
    @JsonProperty("segments")
    private Integer segments;
    
    @JsonProperty("error")
    private String error;
}
```

### Implementation: Webhook Controller

```java
// SmsWebhookController.java
@RestController
@RequestMapping("/api/v1/sms/webhook")
@Slf4j
public class SmsWebhookController {
    
    @Autowired
    private JasminGatewayClient jasminClient;
    
    /**
     * Webhook endpoint for inbound SMS
     * Called by Jasmin when customer sends SMS
     */
    @PostMapping("/inbound")
    public ResponseEntity<Map<String, String>> handleInboundSms(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String body,
            @RequestParam String messageId,
            @RequestParam(required = false) Long timestamp) {
        
        try {
            log.info("Received webhook: from={}, to={}, body={}", from, to, body);
            
            SmsReceiveWebhookPayload payload = SmsReceiveWebhookPayload.builder()
                    .from(from)
                    .to(to)
                    .body(body)
                    .messageId(messageId)
                    .timestamp(timestamp != null ? timestamp : System.currentTimeMillis())
                    .build();
            
            // Process async (don't block webhook)
            jasminClient.handleInboundSms(payload);
            
            // Return 200 OK to Jasmin immediately
            return ResponseEntity.ok(Map.of("status", "received"));
            
        } catch (Exception e) {
            log.error("Error handling inbound SMS webhook", e);
            // Still return 200 to prevent Jasmin retry loops
            return ResponseEntity.ok(Map.of("status", "error", 
                    "message", e.getMessage()));
        }
    }
    
    /**
     * Webhook endpoint for delivery reports
     * Called by Jasmin when SMS is delivered or failed
     */
    @PostMapping("/delivery")
    public ResponseEntity<Map<String, String>> handleDeliveryReport(
            @RequestParam(name = "message_id") String messageId,
            @RequestParam String status,
            @RequestParam(required = false, name = "error_code") String errorCode,
            @RequestParam(required = false, name = "error_message") String errorMessage,
            @RequestParam(required = false) Long timestamp) {
        
        try {
            log.info("Received delivery report: messageId={}, status={}", 
                    messageId, status);
            
            SmsDeliveryReportWebhookPayload payload = 
                    SmsDeliveryReportWebhookPayload.builder()
                    .messageId(messageId)
                    .status(status)
                    .errorCode(errorCode)
                    .errorDescription(errorMessage)
                    .timestamp(timestamp != null ? timestamp : System.currentTimeMillis())
                    .build();
            
            // Process async
            jasminClient.handleDeliveryReport(payload);
            
            return ResponseEntity.ok(Map.of("status", "processed"));
            
        } catch (Exception e) {
            log.error("Error handling delivery report webhook", e);
            return ResponseEntity.ok(Map.of("status", "error"));
        }
    }
}
```

### Service: SmsSendService

```java
// SmsSendService.java — High-level SMS sending service
@Service
@Slf4j
public class SmsSendService {
    
    @Autowired
    private SmsGatewayInterface smsGateway;  // Strategy pattern
    
    @Autowired
    private SmsTemplateEngine templateEngine;
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @Autowired
    private SmsQueueService smsQueueService;
    
    /**
     * Send SMS using active gateway
     */
    public SmsSendResponse sendSms(SmsSendRequest request) {
        // 1. Check rate limits
        if (!rateLimitService.allowSms(request.getTenantId(), request.getToPhone())) {
            throw new RateLimitExceededException(
                "Rate limit exceeded for this customer"
            );
        }
        
        // 2. Render template if needed
        if (request.getTemplateId() != null) {
            String body = templateEngine.render(
                request.getTemplateId(),
                request.getTemplateVars()
            );
            request.setBody(body);
        }
        
        // 3. Send via gateway
        return smsGateway.sendSms(request);
    }
    
    /**
     * Send SMS asynchronously using queue
     */
    public void sendSmsAsync(SmsSendRequest request) {
        smsQueueService.enqueue(request);
    }
    
    /**
     * Send SMS batch
     */
    public List<SmsSendResponse> sendSmsBatch(List<SmsSendRequest> requests) {
        return requests.stream()
                .map(this::sendSms)
                .collect(Collectors.toList());
    }
    
    /**
     * Send scheduled SMS
     */
    public void sendSmsScheduled(SmsSendRequest request) {
        // Schedule using Spring Scheduler
        if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "scheduledAt must be in the future"
            );
        }
        
        long delay = ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            request.getScheduledAt()
        );
        
        // Schedule send
        scheduleTask(request, delay);
    }
}
```

---

## 9. Multi-Tenant Routing

### Tenant Phone Number Mapping

```
Tenant Configuration:
┌────────────────────────────────────────────────────────────────┐
│  tenant_phone_numbers table                                    │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  Tenant ID          Phone Number    Gateway     SMS Balance    │
│  ────────────────────────────────────────────────────────────  │
│  T-001 (Pizza)      +919988776655   Jasmin      10,000 SMS    │
│  T-002 (Fresh)      +919988776666   Jasmin      5,000 SMS     │
│  T-003 (Quick)      +919988776677   Jasmin      8,000 SMS     │
│  T-004 (Style)      +919988776688   Jasmin      12,000 SMS    │
│                                                                │
│  When customer sends SMS to +919988776655:                    │
│  ├── Jasmin receives inbound SMS                              │
│  ├── Looks up in tenant_phone_numbers table                   │
│  ├── Finds tenant_id = T-001                                  │
│  └── Routes webhook to Notification Service with tenant_id   │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Multi-Tenant Routing Implementation

```java
// TenantPhoneNumberService.java
@Service
public class TenantPhoneNumberService {
    
    @Autowired
    private TenantPhoneNumberRepository repository;
    
    public UUID resolveTenantFromPhoneNumber(String phoneNumber) {
        TenantPhoneNumber mapping = repository
            .findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new TenantNotFoundException(
                "No tenant for phone: " + phoneNumber
            ));
        
        return mapping.getTenantId();
    }
    
    public List<String> getPhoneNumbersForTenant(UUID tenantId) {
        return repository.findAllByTenantId(tenantId)
            .stream()
            .map(TenantPhoneNumber::getPhoneNumber)
            .collect(Collectors.toList());
    }
    
    public void assignPhoneToTenant(UUID tenantId, String phoneNumber) {
        TenantPhoneNumber mapping = TenantPhoneNumber.builder()
            .tenantId(tenantId)
            .phoneNumber(phoneNumber)
            .status(PhoneStatus.ACTIVE)
            .smppConnector("msg91-connector")  // Or whichever connector
            .build();
        
        repository.save(mapping);
    }
}

// TenantPhoneNumber Entity
@Entity
@Table(name = "tenant_phone_numbers", schema = "shared")
public class TenantPhoneNumber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private UUID tenantId;
    
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    private PhoneStatus status;  // ACTIVE, INACTIVE, DISABLED
    
    private String smppConnector;  // Which Jasmin connector to use
    
    private Long smsBalance;  // Running balance for pre-paid model
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

// When processing inbound SMS:
@Service
public class SmsReceiveService {
    
    @Autowired
    private TenantPhoneNumberService tenantPhoneService;
    
    public void processInboundSms(SmsReceiveWebhookPayload payload) {
        // 1. Resolve tenant from phone number
        UUID tenantId = tenantPhoneService
            .resolveTenantFromPhoneNumber(payload.getTo());
        
        // 2. Set tenant context (for RLS)
        TenantContext.setTenantId(tenantId);
        
        // 3. Process SMS in tenant context
        // All database queries will be filtered by tenant_id
        // Due to RLS (Row Level Security) in PostgreSQL
        
        // ... rest of processing
    }
}
```

### Jasmin Multi-Tenant Routing

```bash
# In Jasmin, if using multiple SMPP connectors:

# Add multiple connectors (one per provider or region)
smppccm -a
cid connector-msg91       # India provider
cid connector-kaleyra     # Backup provider
cid connector-africas     # Africa provider

# Add routes that pick the right connector based on destination
mtrouter -a
type MTRoute
destination_matching ^(\+91).*          # India numbers
connector smppc(connector-msg91)

mtrouter -a
type MTRoute
destination_matching ^(\+234).*         # Nigeria numbers
connector smppc(connector-africas)

mtrouter -a
type MTRoute
destination_matching ^(\+1).*           # US/Canada numbers
connector smppc(connector-kaleyra)
```

---

## 10. Webhook Handling

### Webhook Security

```java
// WebhookSecurityConfig.java
@Configuration
public class WebhookSecurityConfig {
    
    @Bean
    public WebSecurityFilterChain webhookSecurityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/sms/webhook/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf().disable()  // Jasmin doesn't send CSRF tokens
            .httpBasic().disable()
            .formLogin().disable();
        
        return http.build();
    }
}

// WebhookValidationFilter.java
@Component
public class WebhookValidationFilter extends OncePerRequestFilter {
    
    @Value("${sce.sms.webhook.secret-key:}")
    private String webhookSecretKey;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (!request.getRequestURI().startsWith("/api/v1/sms/webhook")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Option 1: Validate IP address (if Jasmin on same network)
        String clientIp = getClientIp(request);
        if (!isAllowedJasminIp(clientIp)) {
            response.setStatus(403);
            response.getWriter().write("Forbidden");
            return;
        }
        
        // Option 2: Validate HMAC signature (if using signed webhooks)
        String signature = request.getHeader("X-Webhook-Signature");
        if (signature != null) {
            validateSignature(request, signature);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAllowedJasminIp(String ip) {
        // Allow Jasmin container IPs
        Set<String> allowedIps = Set.of(
            "172.17.0.0/16",      // Docker default network
            "192.168.1.0/24",     // Local network
            "127.0.0.1"           // Localhost
        );
        
        return allowedIps.stream()
            .anyMatch(range -> isIpInRange(ip, range));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP",
            "True-Client-IP"
        };
        
        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty()) {
                return value.split(",")[0];
            }
        }
        
        return request.getRemoteAddr();
    }
}
```

### Webhook Retry & Idempotency

```java
// SmsWebhookIdempotencyService.java
@Service
public class SmsWebhookIdempotencyService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * Ensure webhook is only processed once
     * Jasmin might retry on timeout
     */
    public boolean isProcessed(String webhookId) {
        String key = "webhook:processed:" + webhookId;
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(key)
        );
    }
    
    public void markProcessed(String webhookId) {
        String key = "webhook:processed:" + webhookId;
        // Expire after 24 hours
        redisTemplate.opsForValue().set(key, "processed", 
            Duration.ofHours(24));
    }
}

// In WebhookController:
@PostMapping("/inbound")
public ResponseEntity<Map<String, String>> handleInboundSms(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam String body,
        @RequestParam String messageId) {
    
    // Check idempotency
    if (idempotencyService.isProcessed(messageId)) {
        log.info("Webhook already processed: {}", messageId);
        return ResponseEntity.ok(Map.of("status", "already_processed"));
    }
    
    try {
        // Process SMS
        jasminClient.handleInboundSms(...);
        
        // Mark as processed
        idempotencyService.markProcessed(messageId);
        
        return ResponseEntity.ok(Map.of("status", "received"));
    } catch (Exception e) {
        log.error("Error", e);
        // Return 500 to trigger Jasmin retry
        return ResponseEntity.status(500)
            .body(Map.of("status", "error"));
    }
}
```

### Webhook Timeout Handling

```java
// async/AsyncSmsProcessing.java
@Service
public class AsyncSmsProcessingService {
    
    @Autowired
    private JasminGatewayClient jasminClient;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * Process webhook asynchronously
     * Returns 200 immediately to Jasmin
     */
    @Async
    public void processInboundSmsAsync(SmsReceiveWebhookPayload payload) {
        try {
            jasminClient.handleInboundSms(payload);
        } catch (Exception e) {
            log.error("Async SMS processing failed", e);
            // Publish to dead letter queue for manual review
            publishToErrorQueue(payload, e);
        }
    }
    
    private void publishToErrorQueue(SmsReceiveWebhookPayload payload, Exception e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
            .payload(payload)
            .error(e.getMessage())
            .timestamp(LocalDateTime.now())
            .retryCount(0)
            .build();
        
        rabbitTemplate.convertAndSend(
            "sms.errors.exchange",
            "sms.errors.key",
            errorMessage
        );
    }
}

// In Controller:
@PostMapping("/inbound")
public ResponseEntity<Map<String, String>> handleInboundSms(...) {
    // Parse webhook
    SmsReceiveWebhookPayload payload = parseWebhook(...);
    
    // Process async (returns immediately)
    asyncSmsProcessingService.processInboundSmsAsync(payload);
    
    // Return 200 to Jasmin immediately
    return ResponseEntity.ok(Map.of("status", "received"));
}
```

---

## 11. Error Handling & Retry Logic

### Error Types

```java
// SmsErrorTypes.java
public enum SmsErrorType {
    
    // Network errors
    GATEWAY_UNREACHABLE("Jasmin or SMSC unreachable"),
    SMPP_CONNECTION_FAILED("SMPP connection failed"),
    NETWORK_TIMEOUT("Network timeout"),
    
    // Validation errors
    INVALID_PHONE_NUMBER("Invalid phone number format"),
    INVALID_MESSAGE_BODY("Message body invalid"),
    UNSUPPORTED_ENCODING("Unsupported character encoding"),
    
    // Delivery errors
    HANDSET_ERROR("Handset error"),
    INVALID_DEST_ADDR("Invalid destination address"),
    MESSAGE_QUEUE_FULL("SMSC message queue full"),
    THROTTLING_ERROR("Too many messages, throttling"),
    
    // Account errors
    INSUFFICIENT_BALANCE("Insufficient SMS balance"),
    INVALID_ACCOUNT("Invalid account"),
    ACCOUNT_DISABLED("Account disabled"),
    
    // Rate limit errors
    RATE_LIMIT_EXCEEDED("Rate limit exceeded"),
    
    // Other errors
    UNKNOWN_ERROR("Unknown error");
    
    private final String description;
    
    SmsErrorType(String description) {
        this.description = description;
    }
}

// SmsException.java
public class SmsException extends RuntimeException {
    
    private final SmsErrorType errorType;
    private final String messageId;
    private final boolean retryable;
    
    public SmsException(SmsErrorType errorType, String message, boolean retryable) {
        super(message);
        this.errorType = errorType;
        this.retryable = retryable;
    }
}
```

### Retry Strategy

```java
// SmsRetryStrategy.java
@Service
@Slf4j
public class SmsRetryStrategy {
    
    @Autowired
    private SmsMessageRepository smsRepository;
    
    @Autowired
    private SmsGatewayInterface smsGateway;
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Scheduled(fixedDelay = 60000)  // Every 1 minute
    public void retryFailedSms() {
        // Find SMS that failed and are retryable
        List<SmsMessage> failedSms = smsRepository
            .findByStatusAndRetryCountLessThan(SmsStatus.FAILED, 3);
        
        for (SmsMessage sms : failedSms) {
            if (isRetryable(sms)) {
                retrySmsSend(sms);
            }
        }
    }
    
    private boolean isRetryable(SmsMessage sms) {
        // Only retry transient errors, not permanent ones
        String failureReason = sms.getFailureReason();
        
        // Transient errors (retryable)
        Set<String> transientErrors = Set.of(
            "THROTTLING_ERROR",
            "NETWORK_TIMEOUT",
            "MESSAGE_QUEUE_FULL",
            "SMPP_CONNECTION_FAILED"
        );
        
        // Permanent errors (don't retry)
        Set<String> permanentErrors = Set.of(
            "INVALID_DEST_ADDR",
            "UNSUPPORTED_ENCODING",
            "INVALID_PHONE_NUMBER",
            "INSUFFICIENT_BALANCE"
        );
        
        return transientErrors.stream()
            .anyMatch(error -> failureReason.contains(error))
            && !permanentErrors.stream()
            .anyMatch(error -> failureReason.contains(error));
    }
    
    private void retrySmsSend(SmsMessage sms) {
        try {
            log.info("Retrying SMS send: {}", sms.getId());
            
            // Calculate backoff: exponential with jitter
            // Attempt 1: 5 seconds
            // Attempt 2: 10-30 seconds
            // Attempt 3: 60-300 seconds
            long backoffMs = calculateBackoff(sms.getRetryCount());
            
            SmsSendRequest request = SmsSendRequest.builder()
                .tenantId(sms.getTenantId())
                .customerId(sms.getCustomerId())
                .toPhone(sms.getToPhone())
                .fromPhone(sms.getFromPhone())
                .body(sms.getBody())
                .retryCount(sms.getRetryCount() + 1)
                .build();
            
            // Schedule retry
            taskScheduler.schedule(
                () -> {
                    try {
                        SmsSendResponse response = smsGateway.sendSms(request);
                        sms.setStatus(SmsStatus.SENT);
                        sms.setProviderMessageId(response.getProviderMessageId());
                        sms.setRetryCount(sms.getRetryCount() + 1);
                        smsRepository.save(sms);
                    } catch (Exception e) {
                        sms.setRetryCount(sms.getRetryCount() + 1);
                        sms.setFailureReason(e.getMessage());
                        if (sms.getRetryCount() >= 3) {
                            sms.setStatus(SmsStatus.FAILED);
                            sms.setFailedAt(LocalDateTime.now());
                        }
                        smsRepository.save(sms);
                    }
                },
                Instant.now().plusMillis(backoffMs)
            );
            
        } catch (Exception e) {
            log.error("Failed to retry SMS", e);
        }
    }
    
    private long calculateBackoff(int retryCount) {
        // Exponential backoff with jitter
        // 1st retry: 5 seconds
        // 2nd retry: 10-20 seconds  
        // 3rd retry: 30-60 seconds
        
        long baseDelay = (long) Math.pow(2, retryCount + 1) * 1000;
        long jitter = new Random().nextLong(baseDelay / 2);
        return baseDelay + jitter;
    }
}
```

---

## 12. Rate Limiting & Throttling

### Rate Limiting Implementation

```java
// SmsRateLimitService.java
@Service
@Slf4j
public class SmsRateLimitService {
    
    @Value("${sce.sms.rate-limit.per-customer.per-hour:20}")
    private int rateLimitPerHour;
    
    @Value("${sce.sms.rate-limit.per-tenant.per-day:1000}")
    private int rateLimitPerDay;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public boolean allowInboundSms(UUID tenantId, String phoneNumber) {
        String key = String.format("sms:inbound:limit:%s:%s:hour", 
            tenantId, phoneNumber);
        
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            // First request in this window
            redisTemplate.expire(key, Duration.ofHours(1));
        }
        
        if (count > rateLimitPerHour) {
            log.warn("Rate limit exceeded: {} from {}", tenantId, phoneNumber);
            return false;
        }
        
        return true;
    }
    
    public boolean allowOutboundSms(UUID tenantId) {
        String dayKey = String.format("sms:outbound:limit:%s:day:%s",
            tenantId, LocalDate.now());
        
        String hourKey = String.format("sms:outbound:limit:%s:hour:%s",
            tenantId, LocalDateTime.now().getHour());
        
        // Check daily limit
        Long dailyCount = redisTemplate.opsForValue().increment(dayKey);
        if (dailyCount == 1) {
            redisTemplate.expire(dayKey, Duration.ofDays(1));
        }
        
        if (dailyCount > rateLimitPerDay) {
            log.warn("Daily rate limit exceeded for tenant: {}", tenantId);
            return false;
        }
        
        // Check hourly limit (soft limit at 50% of daily)
        Long hourlyCount = redisTemplate.opsForValue().increment(hourKey);
        if (hourlyCount == 1) {
            redisTemplate.expire(hourKey, Duration.ofHours(1));
        }
        
        int hourlyLimit = rateLimitPerDay / 24;
        if (hourlyCount > hourlyLimit * 2) {  // Warning threshold
            log.warn("Hourly rate limit approaching for tenant: {}", tenantId);
        }
        
        return true;
    }
}
```

### Throttling Implementation

```java
// SmsThrottlingService.java
@Service
@Slf4j
public class SmsThrottlingService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * Ensure SMS are sent at controlled pace
     * Example: max 100 SMS/second
     */
    public void throttleSend(UUID tenantId) throws InterruptedException {
        String key = "sms:throttle:" + tenantId;
        
        // Token bucket algorithm
        // If no tokens available, wait
        
        Long lastSendTime = (Long) redisTemplate.opsForValue().get(key);
        long now = System.currentTimeMillis();
        
        long minIntervalMs = 10;  // 100 SMS/second = 10ms between each
        
        if (lastSendTime != null) {
            long elapsed = now - lastSendTime;
            if (elapsed < minIntervalMs) {
                long waitTime = minIntervalMs - elapsed;
                log.debug("Throttling SMS send for {}ms", waitTime);
                Thread.sleep(waitTime);
            }
        }
        
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),
            Duration.ofSeconds(1));
    }
}

// Usage in SmsSendService:
public SmsSendResponse sendSms(SmsSendRequest request) {
    // Check rate limits
    if (!rateLimitService.allowOutboundSms(request.getTenantId())) {
        throw new RateLimitExceededException("Rate limit exceeded");
    }
    
    // Throttle if needed
    try {
        throttlingService.throttleSend(request.getTenantId());
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    // Send SMS
    return smsGateway.sendSms(request);
}
```

---

## 13. Monitoring & Health Checks

### Health Check Implementation

```java
// JasminHealthIndicator.java
@Component
public class JasminHealthIndicator extends AbstractHealthIndicator {
    
    @Value("${sce.sms.jasmin.base-url}")
    private String jasminUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            // Try to hit Jasmin HTTP API
            ResponseEntity<String> response = restTemplate.getForEntity(
                jasminUrl + "/",
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                builder.up()
                    .withDetail("jasmin_url", jasminUrl)
                    .withDetail("status", "connected");
            } else {
                builder.down()
                    .withDetail("jasmin_url", jasminUrl)
                    .withDetail("http_status", response.getStatusCode());
            }
            
        } catch (RestClientException e) {
            builder.down()
                .withDetail("jasmin_url", jasminUrl)
                .withDetail("error", e.getMessage());
        }
    }
}

// SmsMetricsService.java — Collect SMS metrics
@Service
@Slf4j
public class SmsMetricsService {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private SmsMessageRepository smsRepository;
    
    public void recordSmsSent(SmsMessage sms) {
        Counter.builder("sms.sent")
            .tag("tenant", sms.getTenantId().toString())
            .tag("provider", sms.getProviderName())
            .register(meterRegistry)
            .increment();
    }
    
    public void recordSmsDelivered(SmsMessage sms) {
        Timer.builder("sms.delivery_time")
            .tag("tenant", sms.getTenantId().toString())
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry)
            .record(Duration.between(sms.getCreatedAt(), sms.getDeliveredAt()));
    }
    
    public void recordSmsFailed(SmsMessage sms) {
        Counter.builder("sms.failed")
            .tag("tenant", sms.getTenantId().toString())
            .tag("reason", sms.getFailureReason())
            .register(meterRegistry)
            .increment();
    }
    
    @Scheduled(fixedRate = 60000)  // Every minute
    public void updateMetrics() {
        // Total SMS sent (today)
        long totalSentToday = smsRepository.countByDirectionAndCreatedAtAfter(
            SmsDirection.OUTBOUND,
            LocalDateTime.now().minusDays(1)
        );
        
        Gauge.builder("sms.total_sent_today", () -> totalSentToday)
            .register(meterRegistry);
        
        // SMS delivery rate
        long deliveredToday = smsRepository
            .countByStatusAndCreatedAtAfter(SmsStatus.DELIVERED, 
                LocalDateTime.now().minusDays(1));
        
        double deliveryRate = (double) deliveredToday / totalSentToday * 100;
        
        Gauge.builder("sms.delivery_rate_today", () -> deliveryRate)
            .baseUnit("percent")
            .register(meterRegistry);
    }
}

// Actuator endpoint: /actuator/health
// Prometheus endpoint: /actuator/prometheus
```

### Monitoring Dashboard

```yaml
# Grafana Dashboard JSON snippet
{
  "dashboard": {
    "title": "SMS Gateway Monitoring",
    "panels": [
      {
        "title": "SMS Sent Per Minute",
        "targets": [
          {
            "expr": "rate(sms_sent_total[1m])"
          }
        ]
      },
      {
        "title": "SMS Delivery Rate",
        "targets": [
          {
            "expr": "sms_delivery_rate_today"
          }
        ]
      },
      {
        "title": "Failed SMS",
        "targets": [
          {
            "expr": "increase(sms_failed_total[1h])"
          }
        ]
      },
      {
        "title": "Jasmin API Latency",
        "targets": [
          {
            "expr": "sms_send_duration_seconds"
          }
        ]
      },
      {
        "title": "SMPP Connection Status",
        "targets": [
          {
            "expr": "jasmin_smpp_connector_connected"
          }
        ]
      }
    ]
  }
}
```

---

## 14. Scaling Considerations

### Horizontal Scaling

```yaml
# docker-compose-production.yml — Multi-instance Jasmin

version: '3.9'

services:
  # Redis Cluster (for session sharing across instances)
  redis-node-1:
    image: redis:7-alpine
    command: redis-server --port 6379
    networks:
      - sms-network

  redis-node-2:
    image: redis:7-alpine
    command: redis-server --port 6380
    networks:
      - sms-network

  # HAProxy for load balancing
  haproxy:
    image: haproxy:2.8-alpine
    volumes:
      - ./haproxy.cfg:/usr/local/etc/haproxy/haproxy.cfg
    ports:
      - "8990:8990"  # HTTP API
      - "8991:8991"  # Dashboard
    networks:
      - sms-network

  # Jasmin Instance 1
  jasmin-1:
    image: jookies/jasmin:latest
    environment:
      REDIS_CLIENT_HOST: redis-node-1
      JASMIN_LISTEN_ADDR: 0.0.0.0
    volumes:
      - jasmin-store-1:/var/jasmin/store
    networks:
      - sms-network

  # Jasmin Instance 2
  jasmin-2:
    image: jookies/jasmin:latest
    environment:
      REDIS_CLIENT_HOST: redis-node-2
      JASMIN_LISTEN_ADDR: 0.0.0.0
    volumes:
      - jasmin-store-2:/var/jasmin/store
    networks:
      - sms-network

  # Jasmin Instance 3
  jasmin-3:
    image: jookies/jasmin:latest
    environment:
      REDIS_CLIENT_HOST: redis-node-1
      JASMIN_LISTEN_ADDR: 0.0.0.0
    volumes:
      - jasmin-store-3:/var/jasmin/store
    networks:
      - sms-network

networks:
  sms-network:
    driver: bridge

volumes:
  jasmin-store-1:
  jasmin-store-2:
  jasmin-store-3:
```

### Load Balancing Configuration

```haproxy
# haproxy.cfg — Load balance across Jasmin instances

global
    log stdout local0
    log stdout local1 notice

defaults
    log     global
    mode    http
    option  httplog
    option  dontlognull
    timeout connect 5000
    timeout client  50000
    timeout server  50000

frontend http_front
    bind *:8990
    default_backend http_back

backend http_back
    balance roundrobin
    server jasmin1 jasmin-1:8990 check
    server jasmin2 jasmin-2:8990 check
    server jasmin3 jasmin-3:8990 check

frontend dashboard_front
    bind *:8991
    default_backend dashboard_back

backend dashboard_back
    balance roundrobin
    server jasmin1 jasmin-1:8991 check
    server jasmin2 jasmin-2:8991 check
    server jasmin3 jasmin-3:8991 check
```

### Performance Tuning

```java
// SmsPerformanceTuning.java

// 1. Connection pooling
@Configuration
public class HttpClientConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpClientBuilder builder = HttpClientBuilder.create()
            .setMaxConnTotal(200)
            .setMaxConnPerRoute(50)
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(10000)
                    .build()
            );
        
        return new RestTemplate(
            new HttpComponentsClientHttpRequestFactory(builder.build())
        );
    }
}

// 2. Batch SMS operations
@Service
public class SmsBatchService {
    
    public List<SmsSendResponse> sendSmsBatch(
            List<SmsSendRequest> requests) {
        
        // Send in batches of 100
        return Lists.partition(requests, 100)
            .parallelStream()
            .flatMap(batch -> batch.stream()
                .map(smsGateway::sendSms)
                .collect(Collectors.toList())
                .stream()
            )
            .collect(Collectors.toList());
    }
}

// 3. Async processing with virtual threads (Java 19+)
@Service
public class SmsAsyncService {
    
    @Autowired
    private SmsGatewayInterface smsGateway;
    
    public void sendSmsAsync(SmsSendRequest request) {
        Thread.ofVirtual()
            .name("sms-send-" + request.getTenantId())
            .start(() -> {
                try {
                    smsGateway.sendSms(request);
                } catch (Exception e) {
                    log.error("Async SMS send failed", e);
                }
            });
    }
}

// 4. Database query optimization
@Repository
public interface SmsMessageRepository extends JpaRepository<SmsMessage, UUID> {
    
    @Query("""
        SELECT m FROM SmsMessage m
        WHERE m.tenantId = :tenantId
        AND m.direction = :direction
        AND m.createdAt >= :since
        """)
    List<SmsMessage> findRecentByTenant(
        @Param("tenantId") UUID tenantId,
        @Param("direction") SmsDirection direction,
        @Param("since") LocalDateTime since
    );
    
    // Add indexes on frequently queried columns
    // CREATE INDEX idx_sms_tenant_created ON sms_messages(tenant_id, created_at DESC);
    // CREATE INDEX idx_sms_status_created ON sms_messages(status, created_at DESC);
}
```

---

## 15. Cost Optimization

### SMS Cost Calculator

```
┌────────────────────────────────────────────────────────────┐
│             JASMIN + SMPP COST ANALYSIS                    │
│                                                            │
│  Scenario: 1 Million SMS per month                         │
│                                                            │
│  COSTS:                                                    │
│  ├── Jasmin Software          $0/month (open source)      │
│  ├── VPS/Server               $20/month                    │
│  │   (2 CPU, 4GB RAM, SSD)                               │
│  ├── SMPP Connection Fee      $50-100/month                │
│  │   (Varies by provider)                                  │
│  ├── Per-SMS charge           $0.005/SMS (bulk rate)      │
│  │   = 1M × $0.005            $5,000/month                │
│  │                                                        │
│  TOTAL:                        ~$5,070-5,120/month        │
│                                                            │
│  COMPARISON WITH TWILIO:                                   │
│  ├── 1M SMS × $0.0079/SMS     $7,900/month                │
│  ├── SAVE vs Jasmin           $2,780/month                │
│  ├── Savings/year             $33,360                      │
│  │                                                        │
│  BREAK-EVEN:                  ~2M SMS/month                │
│  After 2M SMS, Jasmin is cheaper                          │
│                                                            │
│  COST PER SMS:                                             │
│  ├── Below 1M:                $0.006/SMS (including fees) │
│  ├── At 1M:                   $0.0051/SMS                 │
│  ├── At 5M:                   $0.00501/SMS                │
│  └── At 10M:                  $0.005001/SMS               │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### Provider Cost Comparison

```
Provider              Per-SMS    Setup    Monthly Fee    Best For
──────────────────────────────────────────────────────────────────
Jasmin+MSG91         $0.005     $0       $50-100       High volume
Jasmin+Kaleyra       $0.002     $0       $75-150       Best rate
Jasmin+Vonage        $0.003     $0       $100          Worldwide
Twilio               $0.0079    $0       Free tier     Dev/testing
Africa's Talking     $0.008     $0       Free sandbox  Africa market
Vonage               $0.0068    $0       Free trial    Enterprise
```

---

## 16. Troubleshooting Guide

### Common Issues

```
ISSUE: Jasmin not connecting to SMSC

SYMPTOMS:
├── "Connection refused" error
├── SMPP connector status shows "Disconnected"
└── No SMS being sent

DIAGNOSIS:
1. Check if Jasmin is running
   docker ps | grep jasmin
   
2. Check SMPP connection
   telnet localhost 8990
   smppccm -s msg91-connector
   
3. Check firewall
   sudo ufw allow 2775  (outbound to SMSC)
   
4. Check credentials
   Verify host, port, username, password with provider
   
5. Check logs
   docker logs jasmin-gateway
   tail -f /var/log/jasmin/jasmin.log

SOLUTION:
├── Verify SMPP credentials with provider
├── Check firewall allows outbound port 2775
├── Restart SMPP connector
│   smppccm -2 msg91-connector  (stop)
│   smppccm -1 msg91-connector  (start)
└── Check provider's SMPP server status


ISSUE: Inbound SMS not reaching backend

SYMPTOMS:
├── Customer sends SMS but no webhook received
├── Jasmin dashboard shows SMS received
└── No activity in Notification Service logs

DIAGNOSIS:
1. Check webhook URL
   telnet localhost 8990
   mtrouter -l
   
2. Test webhook manually
   curl -X POST http://notification-svc:8084/api/v1/sms/webhook/inbound \
     -d "from=+919876543210&to=+919988776655&body=TEST&messageId=123"
   
3. Check network connectivity
   docker exec jasmin-gateway ping notification-service
   
4. Check Notification Service logs
   docker logs notification-service | grep "webhook"

SOLUTION:
├── Verify webhook URL is correct
├── Ensure Notification Service is running
├── Check firewall rules allow internal traffic
└── Restart webhook routes
    mtrouter -2 <route-id>  (stop)
    mtrouter -1 <route-id>  (start)


ISSUE: Rate limiting errors (too many messages)

SYMPTOMS:
├── "THROTTLING_ERROR" from SMSC
├── SMS queue backing up
└── High latency in SMS delivery

DIAGNOSIS:
1. Check current SMS send rate
   redis-cli
   GET sms:throttle:*
   
2. Check Jasmin queue
   telnet localhost 8990
   smsq -l
   
3. Check provider's rate limits
   Contact SMS provider for rate limit details

SOLUTION:
├── Implement exponential backoff in retry logic
├── Reduce concurrent SMS sends
├── Batch SMS instead of single send
└── Upgrade SMPP account for higher rate limit


ISSUE: SMS not being delivered to customer phone

SYMPTOMS:
├── SMS sent successfully (no error)
├── But customer didn't receive it
├── Delivery report shows "DELIVERED" but customer says not received

DIAGNOSIS:
1. Check delivery reports
   SELECT * FROM sms_messages
   WHERE status='DELIVERED' AND created_at > now() - interval '1 hour'
   
2. Verify phone number format
   Ensure phone includes country code (+)
   
3. Check with provider
   Provide message ID to SMS provider for investigation
   
4. Check SMSC logs
   Some providers have message tracking in dashboard

SOLUTION:
├── Verify phone number format with customer
├── Check provider's delivery status page
├── Ask provider to check SMSC logs
└── Request full message trace from provider
```

---

## 17. Migration from Development to Production

### Production Checklist

```
PRE-DEPLOYMENT
├── ☐ SMPP account obtained from provider
├── ☐ SMPP credentials tested
├── ☐ Firewall rules configured (port 2775 outbound)
├── ☐ SSL/TLS certificates obtained (if required)
├── ☐ Load balancer/HAProxy configured
├── ☐ Monitoring/Alerting setup (Prometheus, Grafana)
├── ☐ Backup plan tested
├── ☐ Disaster recovery plan documented

DEPLOYMENT
├── ☐ Deploy Jasmin to production server
├── ☐ Configure SMPP connectors for production
├── ☐ Update Notification Service configuration
├── ☐ Run smoke tests
│   ├── ☐ Send test SMS to real number
│   ├── ☐ Receive test SMS from test number
│   └── ☐ Verify delivery reports
├── ☐ Enable monitoring and alerting
├── ☐ Verify rate limiting is in place
└── ☐ Document production endpoints

PRODUCTION DEPLOYMENT SCRIPT
├── docker-compose -f docker-compose-prod.yml up -d
├── Configure SMPP connector (via telnet or config file)
├── Register inbound webhook URL in Jasmin
├── Update Notification Service with production config
├── Run integration tests
├── Monitor logs for 24 hours
└── Enable auto-scaling if using Kubernetes

POST-DEPLOYMENT
├── ☐ Monitor SMS delivery rate (should be > 99%)
├── ☐ Monitor latency (should be < 5 seconds end-to-end)
├── ☐ Verify webhook reliability
├── ☐ Test failover to backup provider (if any)
└── ☐ Weekly review of SMS metrics
```

### Configuration Migration

```java
// application.properties (Development)
sce.sms.provider.active=SIMULATOR

// application.properties (Production)
sce.sms.provider.active=JASMIN
sce.sms.jasmin.base-url=http://jasmin-1.prod.internal:8990
sce.sms.jasmin.username=${JASMIN_USERNAME}
sce.sms.jasmin.password=${JASMIN_PASSWORD}
sce.sms.rate-limit.inbound.per-phone.per-hour=50
sce.sms.rate-limit.outbound.per-tenant.per-day=10000

// Externalize sensitive values
JASMIN_USERNAME: (from secret management)
JASMIN_PASSWORD: (from AWS Secrets Manager / HashiCorp Vault)
```

### Zero-Downtime Migration

```
Step 1: Setup dual SMS providers
├── Configure both old provider (Twilio) and new (Jasmin)
└── Route 10% of SMS to Jasmin, 90% to Twilio

Step 2: Monitor for 48 hours
├── Verify Jasmin delivery rate matches Twilio
├── Monitor error rates
└── Verify webhook reliability

Step 3: Gradual migration
├── Day 1: 10% Jasmin, 90% Twilio
├── Day 2: 25% Jasmin, 75% Twilio
├── Day 3: 50% Jasmin, 50% Twilio
├── Day 4: 75% Jasmin, 25% Twilio
├── Day 5: 100% Jasmin

Step 4: Decommission old provider
├── Stop sending to Twilio
├── Keep Twilio account active for 1 week
└── Can revert if issues occur

CODE:
@Service
public class SmsProviderRouter {
    
    public SmsSendResponse sendSms(SmsSendRequest request) {
        // Determine routing percentage
        int random = new Random().nextInt(100);
        
        if (random < migrationPercentage) {
            return jasminGateway.sendSms(request);
        } else {
            return twilioGateway.sendSms(request);
        }
    }
}
```

---

## Conclusion

You now have a complete guide to implementing Jasmin SMS Gateway + SMPP for your thunai SMS Commerce Engine. 

### Next Steps:

1. **Obtain SMPP account** from telecom provider (MSG91, Kaleyra, etc.)
2. **Test Jasmin in development** using Docker Compose
3. **Integrate with Notification Service** using provided Java code
4. **Configure webhooks** for inbound SMS and delivery reports
5. **Setup monitoring** with Prometheus and Grafana
6. **Plan production deployment** using the checklist above
7. **Test failover scenarios** before going live

### Key Files to Create:

- `docker-compose.yml` (Jasmin setup)
- `JasminGatewayClient.java` (SMS gateway implementation)
- `SmsWebhookController.java` (Webhook handlers)
- `SmsRateLimitService.java` (Rate limiting)
- `SmsRetryStrategy.java` (Retry logic)

### Support Resources:

- Jasmin Documentation: https://docs.jasminsms.com/
- SMPP Protocol Spec: https://www.smpp.org/
- Provider Documentation: Check your SMPP provider's docs
- This guide & SMS_FUNCTIONALITY.md companion docs

Good luck with your production SMS deployment!

---

**Document Version:** 1.0.0  
**Last Updated:** 2026-06-22  
**Author:** thunai Development Team  
**License:** MIT
