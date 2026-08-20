# 🌐 SMS Commerce Engine — Complete Website & Dashboard Flow

> **Document Type:** Frontend Web Application Flow
> **Audience:** Developers, Designers, Product Owners
> **Applies to:** Business Owner Dashboard + Platform Admin Dashboard

---

## Table of Contents

1. [Website Overview](#1-website-overview)
2. [User Roles & Access](#2-user-roles--access)
3. [Application Shell](#3-application-shell)
4. [Authentication Flow](#4-authentication-flow)
5. [Platform Admin Dashboard](#5-platform-admin-dashboard)
6. [Business Owner (Tenant) Dashboard](#6-business-owner-tenant-dashboard)
7. [Complete Page-by-Page Flow](#7-complete-page-by-page-flow)
8. [GraphQL BFF Data Flow](#8-graphql-bff-data-flow)
9. [Real-Time Features](#9-real-time-features)
10. [Responsive Design Strategy](#10-responsive-design-strategy)
11. [Navigation Map](#11-navigation-map)

---

## 1. Website Overview

### What Is the Website For?

The website is **NOT** for end customers (they interact via SMS). The website is a **management dashboard** used by:

```
┌────────────────────────────────────────────────────────────────────────┐
│                                                                        │
│   END CUSTOMERS                     BUSINESS OWNERS & STAFF            │
│   (Use SMS on any phone)            (Use the Website Dashboard)        │
│                                                                        │
│   📱 Send "MENU"                    💻 Login to web dashboard          │
│   📱 Browse products                💻 Manage products & catalog       │
│   📱 Add to cart                    💻 View & process orders           │
│   📱 Place orders                   💻 Configure SMS menus             │
│   📱 Track orders                   💻 View analytics                  │
│   📱 Get SMS replies                💻 Manage staff & roles            │
│                                     💻 Customize SMS templates         │
│                                     💻 Send broadcasts                 │
│                                                                        │
│   PLATFORM ADMINS                                                     │
│   (Use the Website Dashboard)                                         │
│                                                                        │
│   💻 Onboard new tenants                                               │
│   💻 Manage subscriptions                                               │
│   💻 Monitor platform health                                            │
│   💻 View global analytics                                               │
│   💻 Configure SMS providers                                            │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Two Dashboard Views

| Dashboard | URL Path | Who Uses It |
|-----------|----------|-------------|
| **Platform Admin Dashboard** | `/admin/*` | SCE platform owners (you) |
| **Business Owner Dashboard** | `/dashboard/*` | Tenant owners, managers, staff |

---

## 2. User Roles & Access

### What Each Role Sees on the Website

```
┌─────────────────────────────────────────────────────────────────────┐
│                     WEBSITE ACCESS BY ROLE                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  SUPER_ADMIN (Platform Owner)                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✅ Admin Dashboard (global metrics)                        │   │
│  │  ✅ Tenant Management (create, edit, suspend tenants)       │   │
│  │  ✅ Subscription Management                                  │   │
│  │  ✅ SMS Provider Configuration                               │   │
│  │  ✅ Platform Analytics                                       │   │
│  │  ✅ System Health Monitoring                                  │   │
│  │  ✅ Audit Logs                                                │   │
│  │  ✅ Can view any tenant's dashboard (read-only)              │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  TENANT_ADMIN (Business Owner)                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✅ Dashboard (own store metrics)                            │   │
│  │  ✅ Store Configuration                                       │   │
│  │  ✅ Product & Category Management                            │   │
│  │  ✅ SMS Menu Tree Designer                                    │   │
│  │  ✅ Order Management (full access)                           │   │
│  │  ✅ Customer Management                                      │   │
│  │  ✅ SMS Template Editor                                       │   │
│  │  ✅ Analytics & Reports                                       │   │
│  │  ✅ Staff Management & Role Assignment                        │   │
│  │  ✅ Promotions Management                                     │   │
│  │  ✅ SMS Broadcast                                             │   │
│  │  ✅ Subscription & Billing                                    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  MANAGER (Store Manager)                                            │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✅ Dashboard (own store metrics)                            │   │
│  │  ❌ Store Configuration (view only)                          │   │
│  │  ✅ Product & Category Management                            │   │
│  │  ✅ SMS Menu Tree Designer                                    │   │
│  │  ✅ Order Management (full access)                           │   │
│  │  ✅ Customer Management (view + edit)                        │   │
│  │  ✅ SMS Template Editor                                       │   │
│  │  ✅ Analytics & Reports                                       │   │
│  │  ❌ Staff Management                                          │   │
│  │  ✅ Promotions Management                                     │   │
│  │  ✅ SMS Broadcast                                             │   │
│  │  ❌ Subscription & Billing                                    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  STAFF (Cashier / Counter Staff)                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✅ Dashboard (limited — orders today only)                  │   │
│  │  ❌ Store Configuration                                       │   │
│  │  ❌ Product Management (view only)                           │   │
│  │  ❌ SMS Menu Tree                                             │   │
│  │  ✅ Order Management (view + update status only)             │   │
│  │  ✅ Customer Management (view only)                          │   │
│  │  ❌ SMS Templates                                             │   │
│  │  ❌ Analytics                                                  │   │
│  │  ❌ Staff Management                                          │   │
│  │  ❌ Promotions                                                │   │
│  │  ❌ SMS Broadcast                                             │   │
│  │  ❌ Subscription                                              │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  DELIVERY_STAFF (Delivery Agent)                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ✅ Dashboard (only assigned deliveries)                     │   │
│  │  ✅ Order List (only assigned to them)                       │   │
│  │  ✅ Update Delivery Status                                    │   │
│  │  ❌ Everything else                                           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Application Shell

### Layout Structure

```
┌──────────────────────────────────────────────────────────────────────────┐
│  🔔 3   ⚙️   👤 John Doe ▼    [Tenant: Pizza Palace ▼]    [🌙/☀️]     │  ← Header
├────────────┬─────────────────────────────────────────────────────────────┤
│            │                                                             │
│  📊 Dashboard                                                           │
│            │   ┌─────────────────────────────────────────────────────┐  │
│  📦 Products│   │                                                     │  │
│    ├ Categories│ │                                                     │  │
│    └ Items │   │              PAGE CONTENT AREA                       │  │
│            │   │                                                     │  │
│  🛒 Orders │   │   (Changes based on selected route/page)            │  │
│    ├ Active│   │                                                     │  │
│    └ History│   │                                                     │  │
│            │   │                                                     │  │
│  👥 Customers│  │                                                     │  │
│            │   │                                                     │  │
│  📱 SMS    │   │                                                     │  │
│    ├ Menu Tree│ │                                                     │  │
│    ├ Templates│ │                                                     │  │
│    ├ Logs  │   │                                                     │  │
│    └ Broadcast│ │                                                     │  │
│            │   │                                                     │  │
│  📈 Analytics│  │                                                     │  │
│            │   │                                                     │  │
│  🎯 Promos │   │                                                     │  │
│            │   │                                                     │  │
│  ⚙️ Settings│  │                                                     │  │
│    ├ Store │   │                                                     │  │
│    ├ Staff │   │                                                     │  │
│    ├ Roles │   │                                                     │  │
│    └ Billing│  │                                                     │  │
│            │   └─────────────────────────────────────────────────────┘  │
│            │                                                             │
└────────────┴─────────────────────────────────────────────────────────────┘
   Sidebar                           Main Content Area
  (collapsible)                      (scrollable, responsive)
```

### Layout Components

| Component | Description |
|-----------|-------------|
| **Header** | Notifications bell, tenant switcher, user menu, theme toggle |
| **Sidebar** | Collapsible navigation with nested menus, role-based visibility |
| **Main Content** | Dynamic page content with breadcrumbs |
| **Notification Panel** | Slide-in panel showing real-time order updates, SMS alerts |
| **Command Palette** | `Ctrl+K` quick search across orders, products, customers |

---

## 4. Authentication Flow

### Complete Login Journey

```
User opens website (http://localhost:3000)
│
▼
┌──────────────────────────────────────┐
│         LANDING PAGE (/)             │
│                                      │
│  ┌────────────────────────────────┐  │
│  │  📱 SMS Commerce Engine        │  │
│  │                                │  │
│  │  "Turn any phone into a        │  │
│  │   shopping experience"         │  │
│  │                                │  │
│  │  [Login to Dashboard]  ────────┼──┼──► Redirect to Keycloak
│  │  [Register Your Business] ─────┼──┼──► /register
│  │                                │  │
│  │  Features:                     │  │
│  │  ✅ SMS-based ordering          │  │
│  │  ✅ Multi-tenant platform      │  │
│  │  ✅ Works on any phone         │  │
│  │  ✅ Real-time order tracking   │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘

         │
         │ User clicks "Login to Dashboard"
         ▼

┌──────────────────────────────────────┐
│      KEYCLOAK LOGIN PAGE              │
│      (Hosted by Keycloak)             │
│                                      │
│  ┌────────────────────────────────┐  │
│  │  Email: [________________]     │  │
│  │  Password: [______________]    │  │
│  │                                │  │
│  │  [Login]                       │  │
│  │                                │  │
│  │  Forgot Password?              │  │
│  │  Register New Account          │  │
│  │                                │  │
│  │  ─── Or login with ───         │  │
│  │  [Google]  [GitHub]            │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘

         │
         │ Successful login → Keycloak redirects to /auth/callback
         │ with authorization code
         ▼

┌──────────────────────────────────────┐
│    AUTH CALLBACK (/auth/callback)     │
│                                      │
│  Processing login...                 │
│                                      │
│  1. Exchange code for tokens         │
│  2. Decode JWT (get user_id,         │
│     tenant_id, roles)                │
│  3. Store tokens in memory           │
│  4. Fetch user profile via GraphQL   │
│  5. Store user in Zustand store      │
│  6. Determine role → redirect        │
│                                      │
└──────────────────────────────────────┘

         │
         │ Role-based redirect
         ▼

    ┌────┴─────┐
    │          │
    ▼          ▼
/admin/*   /dashboard/*
(SUPER_    (TENANT_ADMIN,
 ADMIN)     MANAGER, STAFF,
            DELIVERY_STAFF)
```

### Registration Flow (New Business Owner)

```
User clicks "Register Your Business"
│
▼
┌──────────────────────────────────────┐
│      REGISTRATION (/register)         │
│                                      │
│  Step 1 of 4: Business Info          │
│  ┌────────────────────────────────┐  │
│  │  Business Type: [▼ Select]     │  │
│  │    ○ Restaurant                │  │
│  │    ○ Supermarket               │  │
│  │    ○ Mini Market               │  │
│  │    ○ Fashion Store             │  │
│  │    ○ Other Retail              │  │
│  │                                │  │
│  │  Business Name: [__________]   │  │
│  │  Phone Number: [+91 _______]   │  │
│  │  Email: [________________]     │  │
│  │  Country: [▼ Select]           │  │
│  │  City: [______________]        │  │
│  │                                │  │
│  │  [Next →]                      │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│  Step 2 of 4: Account Setup          │
│  ┌────────────────────────────────┐  │
│  │  Full Name: [______________]   │  │
│  │  Email: [________________]     │  │
│  │  Password: [______________]    │  │
│  │  Confirm: [________________]   │  │
│  │  Phone: [+91 ____________]     │  │
│  │                                │  │
│  │  [← Back]  [Next →]            │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│  Step 3 of 4: Store Configuration    │
│  ┌────────────────────────────────┐  │
│  │  Store Name: [______________]  │  │
│  │  Store Address: [____________] │  │
│  │  Operating Hours:              │  │
│  │    Open: [09:00 ▼]             │  │
│  │    Close: [22:00 ▼]            │  │
│  │  Delivery Enabled: [✓]         │  │
│  │    Delivery Radius: [5] km     │  │
│  │    Min Order: [$10]            │  │
│  │                                │  │
│  │  [← Back]  [Next →]            │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│  Step 4 of 4: Plan Selection         │
│  ┌────────────────────────────────┐  │
│  │                                │  │
│  │  ┌─────┐ ┌─────┐ ┌──────┐    │  │
│  │  │FREE │ │PRO  │ │ENTER │    │  │
│  │  │ $0  │ │$29  │ │PRISE │    │  │
│  │  │ /mo │ │ /mo │ │custom│    │  │
│  │  │     │ │     │ │      │    │  │
│  │  │50SMS│ │2000 │ │Unlim │    │  │
│  │  │1Store│ SMS │ │Multi │    │  │
│  │  │     │ │3Strs│ │Suprt │    │  │
│  │  │ [✓] │ │[Sel]│ │[Sel] │    │  │
│  │  └─────┘ └─────┘ └──────┘    │  │
│  │                                │  │
│  │  [← Back]  [Create Account →]  │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│      REGISTRATION COMPLETE            │
│                                      │
│  ✅ Account created!                 │
│                                      │
│  Your business "Pizza Palace" is     │
│  now registered.                     │
│                                      │
│  📱 Your SMS Number: +91-9876543210  │
│  📋 Customers text MENU to start     │
│                                      │
│  Next Steps:                         │
│  1. Add your products               │
│  2. Configure your SMS menu         │
│  3. Customize SMS templates         │
│  4. Test by sending SMS              │
│                                      │
│  [Go to Dashboard →]                 │
└──────────────────────────────────────┘

         │
         │ Backend creates:
         │   1. Tenant record (tenant_service)
         │   2. Store record (tenant_service)
         │   3. Default SMS menu tree (tenant_service)
         │   4. Default SMS templates (tenant_service)
         │   5. User account (customer_service)
         │   6. TENANT_ADMIN role assigned (customer_service)
         │   7. Keycloak user created (customer_service)
         │   8. Subscription activated (tenant_service)
         │   9. Phone→Tenant mapping (shared schema)
         ▼

     /dashboard
```

---

## 5. Platform Admin Dashboard

### Admin Pages & Flow

```
SUPER_ADMIN logs in
│
▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ADMIN DASHBOARD (/admin)                                                │
│                                                                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │
│  │ Total       │ │ Active      │ │ SMS Today   │ │ Revenue     │      │
│  │ Tenants     │ │ Tenants     │ │ (Platform)  │ │ (MRR)       │      │
│  │             │ │             │ │             │ │             │      │
│  │    142      │ │    128      │ │   12,847    │ │  $18,420    │      │
│  │   ▲ +5      │ │             │ │   ▲ +12%    │ │   ▲ +8%     │      │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘      │
│                                                                          │
│  ┌──────────────────────────────┐ ┌──────────────────────────────┐     │
│  │  Tenant Growth (Chart)       │ │  SMS Volume (Chart)           │     │
│  │                              │ │                               │     │
│  │  📈                          │ │  📊                           │     │
│  │  ╱                           │ │  ██                           │     │
│  │ ╱                            │ │  ██ ██                        │     │
│  │╱                             │ │  ██ ██ ██                     │     │
│  └──────────────────────────────┘ └──────────────────────────────┘     │
│                                                                          │
│  ┌──────────────────────────────┐ ┌──────────────────────────────┐     │
│  │  Recent Tenants              │ │  System Health                │     │
│  │                              │ │                               │     │
│  │  🟢 Pizza Palace    ACTIVE   │ │  Gateway:     🟢 Healthy     │     │
│  │  🟢 Fresh Market    ACTIVE   │ │  Tenant Svc:  🟢 Healthy     │     │
│  │  🟡 Quick Bites     TRIAL    │ │  Order Svc:   🟢 Healthy     │     │
│  │  🔴 Style Hub      SUSPEND  │ │  Customer Svc:🟡 Degraded    │     │
│  │                              │ │  Notif Svc:   🟢 Healthy     │     │
│  │  [View All →]                │ │  [Details →]                  │     │
│  └──────────────────────────────┘ └──────────────────────────────┘     │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Admin Navigation

```
/admin
├── /admin                         → Dashboard (metrics overview)
├── /admin/tenants                 → Tenant Management
│   ├── /admin/tenants             → List all tenants (table + filters)
│   ├── /admin/tenants/new         → Create new tenant
│   ├── /admin/tenants/:id         → Tenant detail (view their store)
│   └── /admin/tenants/:id/edit    → Edit tenant config
├── /admin/subscriptions           → Subscription Plans
│   ├── /admin/subscriptions       → All plans
│   └── /admin/subscriptions/:id   → Plan detail/edit
├── /admin/sms-providers           → SMS Gateway Configuration
│   ├── /admin/sms-providers       → List providers
│   └── /admin/sms-providers/new   → Add provider
├── /admin/analytics               → Platform Analytics
│   ├── /admin/analytics           → Overview
│   ├── /admin/analytics/sms       → SMS analytics
│   └── /admin/analytics/revenue   → Revenue analytics
├── /admin/audit-logs              → Audit Logs
└── /admin/system                  → System Health
    ├── /admin/system              → Service health
    ├── /admin/system/metrics      → Prometheus metrics
    └── /admin/system/logs         → Centralized logs (Loki)
```

### Admin: Tenant Management Flow

```
┌──────────────────────────────────────────────────────────────────────────┐
│  TENANT MANAGEMENT (/admin/tenants)                                       │
│                                                                          │
│  [+ Add Tenant]  [Export CSV]                                            │
│                                                                          │
│  Search: [🔍 Search tenants...]  Status: [All ▼]  Plan: [All ▼]         │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │ Tenant         │ Plan   │ Status │ SMS Today │ Orders │ Actions   │  │
│  ├────────────────────────────────────────────────────────────────────┤  │
│  │ 🍕 Pizza Palace│ Pro    │ 🟢 ON  │   234     │  45    │ 👁️ ✏️ ⛔ │  │
│  │ 🛒 Fresh Market│ Basic  │ 🟢 ON  │   156     │  32    │ 👁️ ✏️ ⛔ │  │
│  │ 🍔 Quick Bites │ Free   │ 🟡 TRIAL│   42     │   8    │ 👁️ ✏️ ⛔ │  │
│  │ 👗 Style Hub   │ Pro    │ 🔴 OFF │    0      │   0    │ 👁️ ✏️ ✅ │  │
│  │ 🏪 Mini Stop   │ Basic  │ 🟢 ON  │   89      │  18    │ 👁️ ✏️ ⛔ │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  Showing 1-5 of 142  [← 1 2 3 ... 29 →]                                │
│                                                                          │
│  ── Click 👁️ (View) ──────────────────────────────────────────────┐     │
│                                                                    │     │
│  ┌──────────────────────────────────────────────────────────┐     │     │
│  │  TENANT DETAIL: Pizza Palace                              │     │     │
│  │                                                           │     │     │
│  │  Business: Pizza Palace                                   │     │     │
│  │  Owner: John Doe (john@pizzapalace.com)                   │     │     │
│  │  Phone: +91-9876543210                                    │     │     │
│  │  Plan: Pro ($29/mo) — Renews: Jul 15, 2026               │     │     │
│  │  Created: Mar 1, 2026                                     │     │     │
│  │                                                           │     │     │
│  │  📊 Stats:                                                │     │     │
│  │  Total Orders: 1,247  |  Revenue: $18,420                 │     │     │
│  │  Total Customers: 342  |  SMS Sent: 12,847                │     │     │
│  │  Products: 45  |  Categories: 8                           │     │     │
│  │                                                           │     │     │
│  │  [View Store →]  [Edit Config]  [Suspend]  [View Logs]   │     │     │
│  └──────────────────────────────────────────────────────────┘     │     │
│                                                                    │     │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Business Owner (Tenant) Dashboard

### Dashboard Home Page

```
┌──────────────────────────────────────────────────────────────────────────┐
│  DASHBOARD (/dashboard)                    📱 SMS Number: +91-9876543210 │
│                                                                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │
│  │ Orders Today│ │ Revenue     │ │ SMS Sent    │ │ Active      │      │
│  │             │ │ Today       │ │ Today       │ │ Customers   │      │
│  │    45       │ │   $682.50   │ │    234      │ │    342      │      │
│  │   ▲ +12%    │ │   ▲ +8%     │ │   ▲ +15%    │ │   ▲ +3      │      │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘      │
│                                                                          │
│  ┌──────────────────────────────────┐ ┌────────────────────────────┐   │
│  │  📈 Orders (Last 7 Days)         │ │  🏆 Top Products Today     │   │
│  │                                  │ │                             │   │
│  │  60│       █                     │ │  1. Margherita Pizza   12   │   │
│  │  50│   █   █  █                  │ │  2. Pepperoni Pizza     9   │   │
│  │  40│   █ █ █  █                  │ │  3. BBQ Chicken         7   │   │
│  │  30│ █ █ █ █  █                  │ │  4. Garlic Bread        6   │   │
│  │  20│ █ █ █ █  █ █                │ │  5. Coke 500ml          5   │   │
│  │  10│ █ █ █ █  █ █                │ │                             │   │
│  │    └─────────────────            │ │  [View All Products →]      │   │
│  │     M T W T F S S                │ │                             │   │
│  └──────────────────────────────────┘ └────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────┐ ┌────────────────────────────┐   │
│  │  🕐 Recent Orders                │ │  📱 Recent SMS Activity    │   │
│  │                                  │ │                             │   │
│  │  #ORD-0045  $15.99  🟡 PREPARING │ │  IN  +91***4521  "MENU"    │   │
│  │  #ORD-0044  $22.50  🟢 DELIVERED │ │  OUT Menu sent (160 chars) │   │
│  │  #ORD-0043  $8.99   🟢 DELIVERED │ │  IN  +91***8834  "ADD 1"   │   │
│  │  #ORD-0042  $31.00  🔴 CANCELLED │ │  OUT Added to cart         │   │
│  │  #ORD-0041  $12.50  🟢 DELIVERED │ │  IN  +91***2201  "CART"    │   │
│  │                                  │ │  OUT Cart: 2 items, $24.98 │   │
│  │  [View All Orders →]             │ │                             │   │
│  └──────────────────────────────────┘ │  [View SMS Logs →]         │   │
│                                        └────────────────────────────┘   │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Complete Page-by-Page Flow

### 7.1 Product Management Flow

```
/dashboard/products
│
├── PRODUCT LIST PAGE (/dashboard/products)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Products                                     [+ Add Product]    │
│   │                                                                  │
│   │  Category: [All ▼]  Status: [Active ▼]  Search: [🔍________]   │
│   │  View: [📋 List] [📦 Grid]                                      │
│   │                                                                  │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │ 📷 │ Name          │ Category│ Price  │ Stock│Status│Act │   │
│   │  ├──────────────────────────────────────────────────────────┤   │
│   │  │ 🍕 │ Margherita    │ Pizzas  │ $8.99  │  ∞   │ 🟢  │✏️🗑️│   │
│   │  │ 🍕 │ Pepperoni     │ Pizzas  │$10.99  │  ∞   │ 🟢  │✏️🗑️│   │
│   │  │ 🍝 │ Spaghetti     │ Pasta   │ $7.99  │  50  │ 🟢  │✏️🗑️│   │
│   │  │ 🥤 │ Coke 500ml    │ Drinks  │ $2.49  │  200 │ 🟢  │✏️🗑️│   │
│   │  │ 🍰 │ Tiramisu      │ Dessert │ $5.99  │  0   │ 🔴  │✏️🗑️│   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  Showing 1-5 of 45  [← 1 2 3 ... 9 →]                          │
│   └──────────────────────────────────────────────────────────────────┘
│
│   Click [+ Add Product]
│   │
│   ▼
│
├── ADD/EDIT PRODUCT PAGE (/dashboard/products/new)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Add New Product                            [Cancel] [💾 Save]   │
│   │                                                                  │
│   │  ┌─ Basic Info ─────────────────────────────────────────────┐   │
│   │  │  Product Name: [________________________]                │   │
│   │  │  Category: [▼ Select Category    ]  [+ New Category]    │   │
│   │  │  Description: [________________________]                 │   │
│   │  │               [________________________]                 │   │
│   │  │  Base Price: [$________]                                 │   │
│   │  │  SKU/Code: [____________]     (shown in SMS as ID)       │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Stock Management ──────────────────────────────────────┐    │
│   │  │  Track Stock: [✓]                                       │    │
│   │  │  Current Stock: [________]                               │    │
│   │  │  Low Stock Alert: [____] (notify when below)             │    │
│   │  │  Allow Backorder: [ ]                                    │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Variants ───────────────────────────────────────────────┐   │
│   │  │  Has Variants: [✓]                                       │   │
│   │  │                                                          │   │
│   │  │  Variant Type: [Size ▼]                                  │   │
│   │  │  ┌────────────┬─────────┬──────────┐                    │   │
│   │  │  │ Option     │ +Price  │ Default  │                    │   │
│   │  │  ├────────────┼─────────┼──────────┤                    │   │
│   │  │  │ Small      │ +$0.00  │   [✓]    │                    │   │
│   │  │  │ Medium     │ +$3.00  │   [ ]    │                    │   │
│   │  │  │ Large      │ +$6.00  │   [ ]    │                    │   │
│   │  │  │ XLarge     │ +$9.00  │   [ ]    │                    │   │
│   │  │  └────────────┴─────────┴──────────┘                    │   │
│   │  │  [+ Add Option]                                          │   │
│   │  │                                                          │   │
│   │  │  [+ Add Another Variant Type] (e.g., Toppings)           │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ SMS Display ────────────────────────────────────────────┐   │
│   │  │  SMS Short Name: [Margherita______] (max 20 chars)       │   │
│   │  │  SMS Description: [Thin crust, mozzarella, basil]        │   │
│   │  │                   (max 60 chars for SMS)                  │   │
│   │  │  Show in Menu: [✓]                                       │   │
│   │  │  SMS Menu Position: [3] (order in category listing)      │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Preview ───────────────────────────────────────────────┐    │
│   │  │  📱 SMS Preview (how customer sees it):                  │    │
│   │  │  ┌─────────────────────────────────────┐                 │    │
│   │  │  │ 3. Margherita - $8.99               │                 │    │
│   │  │  │    Thin crust, mozzarella, basil    │                 │    │
│   │  │  │    Sizes: S/M/L/XL                  │                 │    │
│   │  │  └─────────────────────────────────────┘                 │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │                                              [Cancel] [💾 Save]  │
│   └──────────────────────────────────────────────────────────────────┘
│
│   Click [💾 Save]
│   │
│   ▼
│   GraphQL Mutation → BFF → REST → Tenant Service → PostgreSQL
│   → Success Toast: "Product 'Margherita' created successfully"
│   → Redirect to /dashboard/products
```

### 7.2 Category Management Flow

```
/dashboard/products/categories
│
├── CATEGORY LIST (/dashboard/products/categories)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Categories                                  [+ Add Category]    │
│   │                                                                  │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │                                                          │   │
│   │  │  📂 Pizzas (12 products)              [✏️ Edit] [🗑️]    │   │
│   │  │     ├── 📂 Classic Pizzas (5)         [✏️ Edit] [🗑️]    │   │
│   │  │     └── 📂 Premium Pizzas (7)         [✏️ Edit] [🗑️]    │   │
│   │  │                                                          │   │
│   │  │  📂 Pasta (6 products)                [✏️ Edit] [🗑️]    │   │
│   │  │  📂 Salads (4 products)               [✏️ Edit] [🗑️]    │   │
│   │  │  📂 Drinks (8 products)               [✏️ Edit] [🗑️]    │   │
│   │  │     ├── 📂 Soft Drinks (5)            [✏️ Edit] [🗑️]    │   │
│   │  │     └── 📂 Juices (3)                 [✏️ Edit] [🗑️]    │   │
│   │  │  📂 Desserts (5 products)             [✏️ Edit] [🗑️]    │   │
│   │  │                                                          │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  Drag & drop to reorder. Order reflects SMS menu position.      │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.3 SMS Menu Tree Designer Flow

```
/dashboard/sms/menu-tree
│
├── VISUAL MENU TREE DESIGNER (/dashboard/sms/menu-tree)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  SMS Menu Flow Designer                    [💾 Save] [Preview 📱]│
│   │                                                                  │
│   │  ┌─ TOOLBAR ────────────────────────────────────────────────┐   │
│   │  │  [+ Menu Node] [+ Action Node] [+ Category Node]        │   │
│   │  │  [Undo] [Redo] [Zoom In] [Zoom Out] [Fit View]          │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ CANVAS (React Flow - drag & drop visual editor) ───────┐   │
│   │  │                                                          │   │
│   │  │   ┌─────────────────┐                                    │   │
│   │  │   │ 🏠 MAIN MENU    │                                    │   │
│   │  │   │ "Welcome to     │                                    │   │
│   │  │   │ Pizza Palace!"  │                                    │   │
│   │  │   └────┬───┬───┬───┘                                    │   │
│   │  │        │   │   │                                         │   │
│   │  │   ┌────▼┐ ┌▼───┐ ┌▼──────┐                              │   │
│   │  │   │1.Menu│ │2.   │ │3. Cart│  ... etc                   │   │
│   │  │   │      │ │Spec.│ │      │                              │   │
│   │  │   └──┬──┘ └──┬──┘ └──┬───┘                              │   │
│   │  │      │       │       │                                    │   │
│   │  │   ┌──▼────┐  │    ┌──▼────────────┐                      │   │
│   │  │   │CATEGS │  │    │ Show cart      │                      │   │
│   │  │   │Pizzas │  │    │ items + total  │                      │   │
│   │  │   │Pasta  │  │    │ Checkout btn   │                      │   │
│   │  │   │Drinks │  │    └───────────────┘                      │   │
│   │  │   └──┬────┘  │                                           │   │
│   │  │      │       │                                           │   │
│   │  │   ┌──▼────┐  │                                           │   │
│   │  │   │PRODUCTS│  │                                           │   │
│   │  │   │per cat│  │                                           │   │
│   │  │   └──┬────┘  │                                           │   │
│   │  │      │       │                                           │   │
│   │  │   ┌──▼────────┐                                          │   │
│   │  │   │CUSTOMIZE  │                                          │   │
│   │  │   │Size/Top   │                                          │   │
│   │  │   └──┬────────┘                                          │   │
│   │  │      │                                                   │   │
│   │  │   ┌──▼────────┐                                          │   │
│   │  │   │ADD TO CART│                                          │   │
│   │  │   └───────────┘                                          │   │
│   │  │                                                          │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ PROPERTIES PANEL (right side) ──────────────────────────┐   │
│   │  │  Selected Node: "CATEGORIES"                               │   │
│   │  │                                                            │   │
│   │  │  Label: [Categories______]                                 │   │
│   │  │  Menu Number: [1]                                          │   │
│   │  │  Action: [SHOW_CATEGORIES ▼]                               │   │
│   │  │  Data Source: [tenant.categories]                          │   │
│   │  │  Items Per Page: [5]                                       │   │
│   │  │  Show Prices: [✓]                                          │   │
│   │  │  Show Counts: [✓]                                          │   │
│   │  │                                                            │   │
│   │  │  SMS Template:                                             │   │
│   │  │  ┌────────────────────────────────────┐                    │   │
│   │  │  │ 📋 OUR MENU                        │                    │   │
│   │  │  │ {for each category}                │                    │   │
│   │  │  │ {num}. {emoji} {name} ({count})    │                    │   │
│   │  │  │ {end for}                          │                    │   │
│   │  │  │ 0. ← Back                          │                    │   │
│   │  │  └────────────────────────────────────┘                    │   │
│   │  │                                                            │   │
│   │  │  [Delete Node]  [Duplicate]                                │   │
│   │  └────────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ SMS PREVIEW (bottom panel) ─────────────────────────────┐   │
│   │  │  📱 Preview (simulate customer view):                     │   │
│   │  │  ┌───────────────────────────────────┐                    │   │
│   │  │  │ 📋 OUR MENU                       │                    │   │
│   │  │  │ 1. 🍕 Pizzas (12 items)           │                    │   │
│   │  │  │ 2. 🍝 Pasta (6 items)             │                    │   │
│   │  │  │ 3. 🥗 Salads (4 items)            │                    │   │
│   │  │  │ 4. 🥤 Drinks (8 items)            │                    │   │
│   │  │  │ 5. 🍰 Desserts (5 items)          │                    │   │
│   │  │  │ 0. ← Back                         │                    │   │
│   │  │  │ Reply with number                 │                    │   │
│   │  │  └───────────────────────────────────┘                    │   │
│   │  │  Characters: 148/160 ✅                                   │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.4 Order Management Flow

```
/dashboard/orders
│
├── ORDER LIST PAGE (/dashboard/orders)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Orders                                                          │
│   │                                                                  │
│   │  Status: [All ▼]  Date: [Today ▼]  Search: [🔍 Order#/Phone]   │
│   │                                                                  │
│   │  ┌── Live Orders ──────────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 🟡 #ORD-0045          $15.99      ⏱️ 12 min ago   │  │    │
│   │  │  │ 👤 +91***4521 (Rahul)                               │  │    │
│   │  │  │ 📦 1x Margherita (Med, +Cheese)                    │  │    │
│   │  │  │    1x Coke 500ml                                    │  │    │
│   │  │  │                                                     │  │    │
│   │  │  │ [✅ Accept] [❌ Reject] [👁️ Details]              │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 🔵 #ORD-0044          $22.50      ⏱️ 25 min ago   │  │    │
│   │  │  │ 👤 +91***8834 (Priya)                               │  │    │
│   │  │  │ 📦 2x Pepperoni (Lrg) + 1x Garlic Bread            │  │    │
│   │  │  │                                                     │  │    │
│   │  │  │ [🍳 Start Preparing] [👁️ Details]                 │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  │                                                          │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌── Completed Today ──────────────────────────────────────┐    │
│   │  │  ✅ #ORD-0043  $8.99   Delivered  11:30 AM              │    │
│   │  │  ✅ #ORD-0042  $31.00  Delivered  10:45 AM              │    │
│   │  │  🔴 #ORD-0041  $12.50  Cancelled  10:20 AM              │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   └──────────────────────────────────────────────────────────────────┘
│
│   Click [👁️ Details] on #ORD-0045
│   │
│   ▼
│
├── ORDER DETAIL PAGE (/dashboard/orders/ORD-0045)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Order #ORD-0045                           Status: 🟡 PREPARING │
│   │                                                                  │
│   │  ┌─ Order Timeline ────────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  ✅ Created        ── 12:30 PM (via SMS)                │    │
│   │  │       │                                                  │    │
│   │  │  ✅ Confirmed      ── 12:31 PM (auto-confirmed)         │    │
│   │  │       │                                                  │    │
│   │  │  🔄 Preparing      ── 12:33 PM (chef started)    ← NOW  │    │
│   │  │       │                                                  │    │
│   │  │  ⬜ Ready          ── ── ──                              │    │
│   │  │       │                                                  │    │
│   │  │  ⬜ Out for Delivery                                     │    │
│   │  │       │                                                  │    │
│   │  │  ⬜ Delivered                                            │    │
│   │  │                                                          │    │
│   │  │  [Update Status → Ready]  [Update → Out for Delivery]   │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ Customer ──────────────┐  ┌─ Delivery ────────────────┐    │
│   │  │  Name: Rahul Sharma      │  │  Type: Delivery            │    │
│   │  │  Phone: +91***4521       │  │  Address: 42 MG Road,      │    │
│   │  │  Orders: 5 (returning)   │  │          Apt 3B, Bangalore │    │
│   │  │  [Send SMS] [View Profile]│ │  Slot: ASAP                 │    │
│   │  │                          │  │  Assigned: Unassigned       │    │
│   │  │                          │  │  [Assign Driver ▼]          │    │
│   │  └──────────────────────────┘  └────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ Items ─────────────────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  1x Margherita Pizza                    $13.99           │    │
│   │  │     Size: Medium (+$3.00)                               │    │
│   │  │     Topping: Extra Cheese (+$2.00)                      │    │
│   │  │                                                          │    │
│   │  │  1x Coke 500ml                          $2.49            │    │
│   │  │                                                          │    │
│   │  │  ────────────────────────────────────────────────────    │    │
│   │  │  Subtotal:                              $16.48           │    │
│   │  │  Delivery:                              $2.00            │    │
│   │  │  Discount:                              -$2.49           │    │
│   │  │  ────────────────────────────────────────────────────    │    │
│   │  │  TOTAL:                                 $15.99           │    │
│   │  │                                                          │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ Payment ───────────────────────────────────────────────┐    │
│   │  │  Method: Cash on Delivery                                │    │
│   │  │  Amount to Collect: $15.99                               │    │
│   │  │  Status: Pending                                         │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ SMS Conversation ──────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  ← IN  (12:28) "MENU"                                    │    │
│   │  │  → OUT (12:28) Main menu sent                            │    │
│   │  │  ← IN  (12:29) "1"                                       │    │
│   │  │  → OUT (12:29) Categories sent                           │    │
│   │  │  ← IN  (12:29) "1"                                       │    │
│   │  │  → OUT (12:29) Pizza list sent                           │    │
│   │  │  ← IN  (12:30) "ADD 1 M-T1"                             │    │
│   │  │  → OUT (12:30) Added to cart                             │    │
│   │  │  ← IN  (12:30) "ADD 4"                                   │    │
│   │  │  → OUT (12:30) Coke added                                │    │
│   │  │  ← IN  (12:30) "CHECKOUT"                                │    │
│   │  │  → OUT (12:30) Checkout summary                          │    │
│   │  │  ← IN  (12:30) "A1-P1"                                   │    │
│   │  │  → OUT (12:30) Order confirmed! #ORD-0045               │    │
│   │  │                                                          │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  [🖨️ Print Order] [❌ Cancel Order] [💬 Send SMS to Customer]  │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.5 Customer Management Flow

```
/dashboard/customers
│
├── CUSTOMER LIST (/dashboard/customers)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Customers                          [Export CSV] [Send Broadcast]│
│   │                                                                  │
│   │  Search: [🔍 Name or phone...]  Sort: [Recent ▼]                │
│   │                                                                  │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │ 👤 Name       │ Phone      │ Orders│ Total  │ Last Order │   │
│   │  ├──────────────────────────────────────────────────────────┤   │
│   │  │ Rahul Sharma  │ +91***4521 │   5   │$89.50  │ Today      │   │
│   │  │ Priya Patel   │ +91***8834 │  12   │$234.00 │ Yesterday  │   │
│   │  │ Amit Kumar    │ +91***2201 │   3   │$45.97  │ 3 days ago │   │
│   │  │ Sara Khan     │ +91***9912 │   8   │$178.50 │ Last week  │   │
│   │  │ Unknown       │ +91***5567 │   1   │$12.99  │ Today      │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  Total: 342 customers  |  Active (30d): 189  |  New (7d): 12   │
│   └──────────────────────────────────────────────────────────────────┘
│
│   Click on customer name
│   │
│   ▼
│
├── CUSTOMER DETAIL (/dashboard/customers/:id)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Customer: Rahul Sharma                                         │
│   │  Phone: +91-9876544521  |  Joined: Mar 15, 2026                │
│   │                                                                  │
│   │  ┌─ Stats ─────────────────────────────────────────────────┐    │
│   │  │  Total Orders: 5  │  Total Spent: $89.50  │  Avg: $17.90 │    │
│   │  │  Favorite Item: Margherita Pizza (ordered 4 times)       │    │
│   │  │  Last Order: Today at 12:30 PM                           │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ Saved Addresses ───────────────────────────────────────┐    │
│   │  │  A1. 42 MG Road, Apt 3B, Bangalore 560001  [Default]    │    │
│   │  │  A2. Office: 100 Brigade Road, Bangalore 560025          │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ Order History ─────────────────────────────────────────┐    │
│   │  │  #ORD-0045  $15.99  🟡 Preparing  Today 12:30 PM       │    │
│   │  │  #ORD-0032  $22.50  ✅ Delivered   Jun 18 07:30 PM     │    │
│   │  │  #ORD-0018  $8.99   ✅ Delivered   Jun 10 01:15 PM     │    │
│   │  │  #ORD-0009  $31.00  ✅ Delivered   Jun 02 08:45 PM     │    │
│   │  │  #ORD-0003  $12.99  ✅ Delivered   May 20 12:00 PM     │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  ┌─ SMS Conversation (Full History) ───────────────────────┐    │
│   │  │  (Complete SMS exchange with this customer)              │    │
│   │  │  [Show All ▾]                                            │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  [💬 Send SMS] [📊 View Analytics] [⛔ Block Customer]          │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.6 SMS Template Editor Flow

```
/dashboard/sms/templates
│
├── SMS TEMPLATE EDITOR (/dashboard/sms/templates)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  SMS Templates                              [💾 Save All Changes]│
│   │                                                                  │
│   │  Template: [Welcome Message ▼]                                   │
│   │                                                                  │
│   │  ┌─ Editor ──────────────────────┐ ┌─ Preview ──────────────┐   │
│   │  │                                │ │                         │   │
│   │  │  Template Name:                │ │  📱 SMS Preview:       │   │
│   │  │  [Welcome Message______]      │ │                         │   │
│   │  │                                │ │  ┌───────────────────┐  │   │
│   │  │  Trigger: ON_FIRST_SMS         │ │  │ 👋 Welcome to     │  │   │
│   │  │  (sent when customer sends     │ │  │ Pizza Palace!     │  │   │
│   │  │   their first SMS)             │ │  │ Reply MENU to     │  │   │
│   │  │                                │ │  │ browse our menu   │   │
│   │  │  Message:                      │ │  │ or SPECIAL for    │  │   │
│   │  │  ┌──────────────────────────┐  │ │  │ today's deals!    │  │   │
│   │  │  │ 👋 Welcome to            │  │ │  │                   │  │   │
│   │  │  │ {{store_name}}!          │  │ │  │ ▸ Menu:           │  │   │
│   │  │  │ Reply MENU to browse     │  │ │  │ sms:+919876543210 │  │   │
│   │  │  │ our menu or SPECIAL      │  │ │  │ ?body=MENU        │  │   │
│   │  │  │ for today's deals!       │  │ │  └───────────────────┘  │   │
│   │  │  │                          │  │ │                         │   │
│   │  │  │ ▸ Menu:                  │  │ │  Characters: 112/160 ✅ │   │
│   │  │  │ sms:{{store_phone}}      │  │ │  Segments: 1 SMS       │   │
│   │  │  │ ?body=MENU               │  │ │  Cost: 1 credit        │   │
│   │  │  └──────────────────────────┘  │ │                         │   │
│   │  │                                │ │                         │   │
│   │  │  Available Variables:          │ │                         │   │
│   │  │  {{store_name}} → Pizza Palace │ │                         │   │
│   │  │  {{store_phone}} → +919876...  │ │                         │   │
│   │  │  {{customer_name}} → Rahul     │ │                         │   │
│   │  │  {{order_id}} → ORD-0045       │ │                         │   │
│   │  │  {{order_total}} → $15.99      │ │                         │   │
│   │  │  {{order_status}} → Preparing  │ │                         │   │
│   │  │  {{eta}} → 30                  │ │                         │   │
│   │  │  {{cart_items}} → 2 items      │ │                         │   │
│   │  │  {{cart_total}} → $15.99       │ │                         │   │
│   │  │                                │ │                         │   │
│   │  └────────────────────────────────┘ └─────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ All Templates ──────────────────────────────────────────┐   │
│   │  │  📝 Welcome Message (ON_FIRST_SMS)          [Edit]       │   │
│   │  │  📝 Main Menu (ON_MENU_COMMAND)             [Edit]       │   │
│   │  │  📝 Category List (ON_BROWSE_CATEGORIES)    [Edit]       │   │
│   │  │  📝 Product List (ON_BROWSE_CATEGORY)       [Edit]       │   │
│   │  │  📝 Product Detail (ON_SELECT_PRODUCT)      [Edit]       │   │
│   │  │  📝 Cart Summary (ON_VIEW_CART)              [Edit]       │   │
│   │  │  📝 Added to Cart (ON_ADD_TO_CART)          [Edit]       │   │
│   │  │  📝 Checkout Summary (ON_CHECKOUT)          [Edit]       │   │
│   │  │  📝 Order Confirmed (ON_ORDER_PLACED)       [Edit]       │   │
│   │  │  📝 Order Status Update (ON_STATUS_CHANGE)  [Edit]       │   │
│   │  │  📝 Order History (ON_VIEW_ORDERS)          [Edit]       │   │
│   │  │  📝 Search Results (ON_SEARCH)              [Edit]       │   │
│   │  │  📝 Store Info (ON_STORE_INFO)              [Edit]       │   │
│   │  │  📝 Help Message (ON_HELP)                  [Edit]       │   │
│   │  │  📝 Unknown Command (ON_UNKNOWN)            [Edit]       │   │
│   │  │  📝 Session Expired (ON_TIMEOUT)            [Edit]       │   │
│   │  │  📝 Promotional (BROADCAST)                 [Edit]       │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.7 SMS Broadcast Flow

```
/dashboard/sms/broadcast
│
├── SMS BROADCAST (/dashboard/sms/broadcast)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Send Broadcast SMS                                               │
│   │                                                                  │
│   │  ┌─ Recipients ─────────────────────────────────────────────┐   │
│   │  │  Send To:                                                │   │
│   │  │  ○ All Customers (342)                                   │   │
│   │  │  ● Customers who ordered in last 30 days (189)           │   │
│   │  │  ○ Customers who haven't ordered in 30+ days (53)        │   │
│   │  │  ○ Custom list                                           │   │
│   │  │    [Enter phone numbers, one per line]                   │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Message ───────────────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  🎉 {{store_name}} SPECIAL OFFER!                       │    │
│   │  │  Get 20% off on all Pizzas today!                       │    │
│   │  │  Use code: PIZZA20                                      │    │
│   │  │  Order now: sms:{{store_phone}}?body=MENU               │    │
│   │  │                                                          │    │
│   │  │  Characters: 128/160 ✅  |  Segments: 1 SMS             │    │
│   │  │  Estimated Cost: 189 SMS credits                         │    │
│   │  │  Remaining Credits: 1,766                                │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Schedule ──────────────────────────────────────────────┐    │
│   │  │  ○ Send Now                                              │    │
│   │  │  ● Schedule for later                                    │    │
│   │  │    Date: [2026-06-22]  Time: [11:00 AM ▼]               │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  [Preview] [Send/Schedule Broadcast]                             │
│   │                                                                  │
│   │  ┌─ Past Broadcasts ───────────────────────────────────────┐    │
│   │  │  Jun 15 "Weekend Special" → 234 sent, 45 orders (19%)  │    │
│   │  │  Jun 08 "New Menu Items"  → 312 sent, 28 orders (9%)   │    │
│   │  │  Jun 01 "Monsoon Offer"   → 298 sent, 67 orders (22%)  │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.8 Analytics Flow

```
/dashboard/analytics
│
├── ANALYTICS DASHBOARD (/dashboard/analytics)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Analytics                      Period: [Last 30 Days ▼]        │
│   │                                                                  │
│   │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐  │
│   │  │ Total Orders│ │ Revenue     │ │ Avg Order   │ │ New     │  │
│   │  │    347      │ │  $5,842     │ │  $16.84     │ │Customers│  │
│   │  │   ▲ +23%    │ │   ▲ +18%    │ │   ▲ +5%     │ │   34    │  │
│   │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘  │
│   │                                                                  │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │  📈 Revenue Over Time                                    │   │
│   │  │                                                          │   │
│   │  │  $300│        █                                          │   │
│   │  │  $250│   █    █  █                                       │   │
│   │  │  $200│   █ █  █  █ █                                     │   │
│   │  │  $150│ █ █ █  █  █ █  █                                  │   │
│   │  │  $100│ █ █ █ █ █ █ █  █ █                                │   │
│   │  │   $50│ █ █ █ █ █ █ █  █ █ █                              │   │
│   │  │      └──────────────────────────────                     │   │
│   │  │       Jun 1                              Jun 30          │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌──────────────────────────┐ ┌──────────────────────────┐     │
│   │  │  🏆 Top Products         │ │  ⏰ Peak Hours            │     │
│   │  │                          │ │                           │     │
│   │  │  1. Margherita     89    │ │  12PM-2PM  ████████  35%  │     │
│   │  │  2. Pepperoni      67    │ │  7PM-9PM   ███████  30%   │     │
│   │  │  3. BBQ Chicken    54    │ │  6PM-7PM   █████   15%    │     │
│   │  │  4. Coke 500ml     48    │ │  2PM-6PM   ████    12%    │     │
│   │  │  5. Garlic Bread   42    │ │  Other     ██       8%    │     │
│   │  └──────────────────────────┘ └──────────────────────────┘     │
│   │                                                                  │
│   │  ┌──────────────────────────┐ ┌──────────────────────────┐     │
│   │  │  📱 SMS Stats            │ │  👥 Customer Insights    │     │
│   │  │                          │ │                           │     │
│   │  │  SMS Sent:    4,234      │ │  Returning:  67%         │     │
│   │  │  SMS Received:1,892      │ │  One-time:   33%         │     │
│   │  │  Response Rate: 45%      │ │  Avg Orders/Customer: 3.2│     │
│   │  │  Conversion:    18%      │ │  Avg Spend/Customer: $52 │     │
│   │  │  Credits Left:  1,766    │ │  Most Active: Tue-Fri    │     │
│   │  └──────────────────────────┘ └──────────────────────────┘     │
│   │                                                                  │
│   │  [📊 Export Report (CSV)]  [📄 Export Report (PDF)]              │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.9 Store Settings Flow

```
/dashboard/settings/store
│
├── STORE SETTINGS (/dashboard/settings/store)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Store Settings                              [💾 Save Changes]   │
│   │                                                                  │
│   │  ┌─ General ───────────────────────────────────────────────┐    │
│   │  │  Store Name: [Pizza Palace_______________]              │    │
│   │  │  Business Type: [Restaurant ▼]                          │    │
│   │  │  Description: [Best pizzas in town since 2020____]      │    │
│   │  │  Logo: [📷 Upload]  (used in WAP pages)                 │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Contact ───────────────────────────────────────────────┐    │
│   │  │  SMS Number: [+91-9876543210] (cannot change)           │    │
│   │  │  Support Phone: [+91-9876543211]                        │    │
│   │  │  Email: [contact@pizzapalace.com________]               │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Location ──────────────────────────────────────────────┐    │
│   │  │  Address: [42 MG Road, Bangalore 560001_________]       │    │
│   │  │  Map: [📍 Pin on Map]                                   │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Operating Hours ───────────────────────────────────────┐    │
│   │  │  Monday:    [✓] Open  [09:00 ▼] to [22:00 ▼]           │    │
│   │  │  Tuesday:   [✓] Open  [09:00 ▼] to [22:00 ▼]           │    │
│   │  │  Wednesday: [✓] Open  [09:00 ▼] to [22:00 ▼]           │    │
│   │  │  Thursday:  [✓] Open  [09:00 ▼] to [22:00 ▼]           │    │
│   │  │  Friday:    [✓] Open  [09:00 ▼] to [23:00 ▼]           │    │
│   │  │  Saturday:  [✓] Open  [10:00 ▼] to [23:00 ▼]           │    │
│   │  │  Sunday:    [✓] Open  [10:00 ▼] to [21:00 ▼]           │    │
│   │  │                                                          │    │
│   │  │  Auto-reply when closed:                                 │    │
│   │  │  [We're currently closed. Open {next_open_time}!        │    │
│   │  │   Reply MENU to place an order for later_________]       │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Delivery Settings ─────────────────────────────────────┐    │
│   │  │  Enable Delivery: [✓]                                    │    │
│   │  │  Delivery Radius: [5] km                                 │    │
│   │  │  Minimum Order: [$10.00]                                 │    │
│   │  │  Delivery Fee: [$2.00] (flat) or [Per km: $0.50]        │    │
│   │  │  Estimated Delivery Time: [30-45] minutes                │    │
│   │  │  Delivery Slots: [✓] Enable                              │    │
│   │  │    Slot Duration: [30] minutes                           │    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Order Settings ────────────────────────────────────────┐    │
│   │  │  Auto-confirm orders: [✓] (no manual accept needed)     │    │
│   │  │  Require address: [✓]                                    │    │
│   │  │  Allow special instructions: [✓]                         │    │
│   │  │  Max items per order: [20]                               │    │
│   │  │  Accept orders when closed: [ ] (pre-order for next open)│    │
│   │  └──────────────────────────────────────────────────────────┘   │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.10 Staff & Role Management Flow

```
/dashboard/settings/staff
│
├── STAFF MANAGEMENT (/dashboard/settings/staff)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Staff & Roles                                    [+ Invite Staff]│
│   │                                                                  │
│   │  ┌─ Team Members ──────────────────────────────────────────┐    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 👤 John Doe (You)                                  │  │    │
│   │  │  │    john@pizzapalace.com  |  Role: TENANT_ADMIN     │  │    │
│   │  │  │    Last login: Today 12:30 PM                      │  │    │
│   │  │  │    [Edit Role]                                     │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 👤 Maria Garcia                                    │  │    │
│   │  │  │    maria@pizzapalace.com  |  Role: MANAGER         │  │    │
│   │  │  │    Last login: Today 11:00 AM                      │  │    │
│   │  │  │    [Edit Role] [Remove]                            │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 👤 Raj Singh                                       │  │    │
│   │  │  │    raj@pizzapalace.com   |  Role: STAFF            │  │    │
│   │  │  │    Last login: Yesterday 8:00 PM                   │  │    │
│   │  │  │    [Edit Role] [Remove]                            │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  │                                                          │    │
│   │  │  ┌────────────────────────────────────────────────────┐  │    │
│   │  │  │ 👤 Delivery Boy 1                                  │  │    │
│   │  │  │    delivery1@pizzapalace.com | Role: DELIVERY_STAFF│  │    │
│   │  │  │    Last login: Today 1:00 PM                       │  │    │
│   │  │  │    [Edit Role] [Remove]                            │  │    │
│   │  │  └────────────────────────────────────────────────────┘  │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   └──────────────────────────────────────────────────────────────────┘
│
│   Click [+ Invite Staff]
│   │
│   ▼
│
├── INVITE STAFF MODAL
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  Invite New Staff Member                                        │
│   │                                                                  │
│   │  Full Name: [________________________]                          │
│   │  Email: [________________________]                              │
│   │  Phone: [+91 ________________]                                  │
│   │                                                                  │
│   │  Role: [▼ Select Role]                                          │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │  TENANT_ADMIN — Full access to everything                │   │
│   │  │  MANAGER      — Products, orders, customers, analytics   │   │
│   │  │  STAFF        — View orders, update order status         │   │
│   │  │  DELIVERY_STAFF — View assigned deliveries only          │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  ┌─ Permission Preview ────────────────────────────────────┐    │
│   │  │  This role can:                                          │    │
│   │  │  ✅ View & manage orders                                  │    │
│   │  │  ✅ View & manage products                                │    │
│   │  │  ✅ View customers                                         │    │
│   │  │  ✅ View analytics                                         │    │
│   │  │  ❌ Manage staff                                           │    │
│   │  │  ❌ Store configuration                                    │    │
│   │  │  ❌ Billing & subscription                                 │    │
│   │  └──────────────────────────────────────────────────────────┘    │
│   │                                                                  │
│   │  [Cancel]  [Send Invitation]                                     │
│   │                                                                  │
│   │  → Sends email with invite link                                  │
│   │  → User registers via Keycloak                                   │
│   │  → Assigned role + tenant mapping created                        │
│   └──────────────────────────────────────────────────────────────────┘
```

### 7.11 SMS Logs Flow

```
/dashboard/sms/logs
│
├── SMS LOGS (/dashboard/sms/logs)
│   ┌──────────────────────────────────────────────────────────────────┐
│   │  SMS Logs                     Direction: [All ▼]  Date: [Today]│
│   │                                                                  │
│   │  ┌──────────────────────────────────────────────────────────┐   │
│   │  │ Time    │ Dir │ Phone      │ Message          │ Status  │   │
│   │  ├──────────────────────────────────────────────────────────┤   │
│   │  │ 12:30 PM│ ←IN │ +91***4521 │ "A1-P1"          │ Parsed  │   │
│   │  │ 12:30 PM│ →OUT│ +91***4521 │ "🎉 Order placed…"│ Sent   │   │
│   │  │ 12:30 PM│ ←IN │ +91***4521 │ "CHECKOUT"        │ Parsed  │   │
│   │  │ 12:30 PM│ →OUT│ +91***4521 │ "🛒 Total: $15.9…"│ Sent   │   │
│   │  │ 12:29 PM│ ←IN │ +91***4521 │ "ADD 1 M-T1"      │ Parsed  │   │
│   │  │ 12:29 PM│ →OUT│ +91***4521 │ "✅ Added! Cart:…"│ Sent   │   │
│   │  │ 12:28 PM│ ←IN │ +91***8834 │ "MENU"            │ Parsed  │   │
│   │  │ 12:28 PM│ →OUT│ +91***8834 │ "📋 Categories…"  │ Sent   │   │
│   │  │ 12:15 PM│ →OUT│ +91***9912 │ "🎉 WEEKEND OFFER"│ Sent   │   │
│   │  │ 11:30 AM│ ←IN │ +91***5567 │ "HI"              │ Parsed  │   │
│   │  └──────────────────────────────────────────────────────────┘   │
│   │                                                                  │
│   │  Summary Today:                                                  │
│   │  Inbound: 89  |  Outbound: 145  |  Failed: 2  |  Total: 234    │
│   │  Credits Used: 145  |  Credits Remaining: 1,766                 │
│   └──────────────────────────────────────────────────────────────────┘
```

---

## 8. GraphQL BFF Data Flow

### How the Frontend Fetches Data

```
┌──────────────────────────────────────────────────────────────────────┐
│                    COMPLETE DATA FLOW                                 │
│                                                                      │
│  React Component                                                     │
│       │                                                              │
│       │ useQuery(GET_ORDERS, { variables: { status: 'ACTIVE' } })   │
│       ▼                                                              │
│  Apollo Client                                                       │
│       │                                                              │
│       │ POST /graphql  { query: "orders(status: ACTIVE) { ... }" }  │
│       ▼                                                              │
│  ┌─ GraphQL BFF (Apollo Server) ─────────────────────────────────┐  │
│  │                                                                 │  │
│  │  orderResolvers.orders(_, { status })                           │  │
│  │       │                                                         │  │
│  │       ├──► REST GET /api/v1/orders?status=ACTIVE                │  │
│  │       │    (calls Order Service via API Gateway)                 │  │
│  │       │    Headers: Authorization, X-Tenant-ID                  │  │
│  │       │                                                         │  │
│  │       ├──► For each order:                                      │  │
│  │       │    REST GET /api/v1/customers/{customerId}               │  │
│  │       │    (calls Customer Service via API Gateway)              │  │
│  │       │                                                         │  │
│  │       └──► Aggregates and returns GraphQL response              │  │
│  │                                                                 │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│       │                                                              │
│       ▼                                                              │
│  ┌─ API Gateway ─────────────────────────────────────────────────┐  │
│  │                                                                 │  │
│  │  1. Validate JWT token                                          │  │
│  │  2. Extract tenant_id from JWT                                  │  │
│  │  3. Set X-Tenant-ID header                                      │  │
│  │  4. Route to correct microservice                               │  │
│  │                                                                 │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│       │                                                              │
│       ▼                                                              │
│  ┌─ Microservice (e.g., Order Service) ──────────────────────────┐  │
│  │                                                                 │  │
│  │  1. TenantContextFilter reads X-Tenant-ID                       │  │
│  │  2. SET app.current_tenant = '{tenant_id}' (PostgreSQL)        │  │
│  │  3. RLS policy activates → only this tenant's orders visible   │  │
│  │  4. Execute query → return results                               │  │
│  │                                                                 │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│       │                                                              │
│       ▼                                                              │
│  ┌─ PostgreSQL ──────────────────────────────────────────────────┐  │
│  │                                                                 │  │
│  │  SELECT * FROM order_service.orders                             │  │
│  │  WHERE status = 'ACTIVE'                                        │  │
│  │  -- RLS automatically adds: AND tenant_id = current_tenant     │  │
│  │                                                                 │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### Example GraphQL Query from Frontend

```typescript
// frontend/react-app/src/graphql/queries/order.queries.ts

import { gql } from '@apollo/client';

export const GET_ORDERS = gql`
  query GetOrders($status: OrderStatus, $page: Int, $size: Int) {
    orders(status: $status, page: $page, size: $size) {
      items {
        id
        orderNumber
        status
        total
        paymentMethod
        createdAt
        customer {
          id
          name
          phone
        }
        items {
          productName
          quantity
          price
          customizations {
            type
            value
          }
        }
      }
      pagination {
        page
        size
        totalElements
        totalPages
      }
    }
  }
`;

export const GET_ORDER_DETAIL = gql`
  query GetOrderDetail($id: ID!) {
    order(id: $id) {
      id
      orderNumber
      status
      total
      subtotal
      deliveryFee
      discount
      paymentMethod
      paymentStatus
      createdAt
      customer {
        id
        name
        phone
        totalOrders
        totalSpent
      }
      deliveryAddress {
        fullAddress
        latitude
        longitude
      }
      items {
        id
        productName
        quantity
        price
        customizations {
          type
          value
          priceAdjustment
        }
      }
      statusHistory {
        status
        timestamp
        note
        updatedBy
      }
      smsConversation {
        direction
        message
        timestamp
        status
      }
    }
  }
`;

export const UPDATE_ORDER_STATUS = gql`
  mutation UpdateOrderStatus($orderId: ID!, $status: OrderStatus!, $note: String) {
    updateOrderStatus(orderId: $orderId, status: $status, note: $note) {
      id
      status
      statusHistory {
        status
        timestamp
      }
    }
  }
`;
```

---

## 9. Real-Time Features

### WebSocket/GraphQL Subscriptions

```
┌──────────────────────────────────────────────────────────────────────┐
│                 REAL-TIME FEATURES (WebSocket)                        │
│                                                                      │
│  1. LIVE ORDER UPDATES                                               │
│     ────────────────────────                                         │
│     When: New order placed via SMS                                   │
│     Who sees: All TENANT_ADMIN, MANAGER, STAFF                       │
│     Effect: New order card appears with sound notification 🔔        │
│     Effect: Dashboard counter updates                                │
│                                                                      │
│  2. ORDER STATUS CHANGES                                             │
│     ────────────────────────                                         │
│     When: Order status updated by staff or delivery                  │
│     Who sees: Customer (via SMS), Dashboard users                    │
│     Effect: Order card color/status updates in real-time             │
│     Effect: Timeline updates                                         │
│                                                                      │
│  3. SMS ACTIVITY FEED                                                │
│     ──────────────────────                                           │
│     When: Any SMS sent or received                                   │
│     Who sees: TENANT_ADMIN, MANAGER                                  │
│     Effect: SMS activity panel updates live                          │
│                                                                      │
│  4. STOCK ALERTS                                                     │
│     ──────────────                                                   │
│     When: Product stock drops below threshold                        │
│     Who sees: TENANT_ADMIN, MANAGER                                  │
│     Effect: Toast notification + product marked low-stock            │
│                                                                      │
│  5. CUSTOMER ACTIVITY                                                │
│     ─────────────────                                                │
│     When: Customer sends SMS (browsing, ordering)                    │
│     Who sees: STAFF on order page                                    │
│     Effect: "Customer is currently ordering..." indicator            │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 10. Responsive Design Strategy

```
┌──────────────────────────────────────────────────────────────────────┐
│                    RESPONSIVE BREAKPOINTS                             │
│                                                                      │
│  Desktop (> 1280px)                                                  │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Sidebar (expanded) │  Full content area with side panels   │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  Tablet (768px - 1280px)                                             │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Sidebar (collapsed │  Content area (full width)            │    │
│  │  icons only)        │  Tables scroll horizontally           │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  Mobile (< 768px) — Staff & Delivery primarily                       │
│  ┌────────────────────┐                                              │
│  │  ☰ Menu            │                                              │
│  ├────────────────────┤                                              │
│  │                    │                                              │
│  │  Cards layout      │                                              │
│  │  (no tables)       │                                              │
│  │                    │                                              │
│  │  Order cards:      │                                              │
│  │  ┌──────────────┐  │                                              │
│  │  │ #ORD-0045    │  │                                              │
│  │  │ $15.99       │  │                                              │
│  │  │ 🟡 Preparing │  │                                              │
│  │  │ [Accept]     │  │                                              │
│  │  └──────────────┘  │                                              │
│  │                    │                                              │
│  └────────────────────┘                                              │
│                                                                      │
│  NOTE: End customers never see the website —                         │
│  they use SMS on any phone (feature phone or smartphone).            │
│  The responsive design is for business staff who may use             │
│  tablets or phones to manage orders on the go.                       │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 11. Navigation Map

### Complete Sitemap

```
SMS COMMERCE ENGINE — WEBSITE SITEMAP
══════════════════════════════════════

PUBLIC (No Auth Required)
├── /                              → Landing Page
├── /pricing                       → Pricing Plans
├── /features                      → Feature Showcase
├── /about                         → About Us
├── /register                      → Business Registration (4-step wizard)
└── /auth/callback                 → OAuth Callback Handler

AUTHENTICATED — BUSINESS OWNER DASHBOARD
├── /dashboard                     → Dashboard Home (metrics + charts)
│
├── /dashboard/products            → Product Management
│   ├── /dashboard/products        → Product List (table/grid)
│   ├── /dashboard/products/new    → Add Product Form
│   ├── /dashboard/products/:id    → Edit Product Form
│   └── /dashboard/products/categories → Category Tree Manager
│
├── /dashboard/orders              → Order Management
│   ├── /dashboard/orders          → Order List (live + history)
│   ├── /dashboard/orders/:id      → Order Detail (timeline + SMS log)
│   └── /dashboard/orders/delivery → Delivery Assignment Board
│
├── /dashboard/customers           → Customer Management
│   ├── /dashboard/customers       → Customer List
│   └── /dashboard/customers/:id   → Customer Detail (history + SMS)
│
├── /dashboard/sms                 → SMS Management
│   ├── /dashboard/sms/menu-tree   → Visual Menu Tree Designer (React Flow)
│   ├── /dashboard/sms/templates   → SMS Template Editor
│   ├── /dashboard/sms/logs        → SMS Logs (inbound + outbound)
│   └── /dashboard/sms/broadcast   → SMS Broadcast Composer
│
├── /dashboard/analytics           → Analytics Dashboard
│   ├── /dashboard/analytics       → Overview (revenue, orders, SMS)
│   ├── /dashboard/analytics/products → Product Performance
│   └── /dashboard/analytics/customers → Customer Insights
│
├── /dashboard/promotions          → Promotion Management
│   ├── /dashboard/promotions      → Active Promotions List
│   └── /dashboard/promotions/new  → Create Promotion
│
└── /dashboard/settings            → Settings
    ├── /dashboard/settings/store  → Store Configuration
    ├── /dashboard/settings/staff  → Staff Management
    ├── /dashboard/settings/roles  → Role & Permission Management
    ├── /dashboard/settings/subscription → Subscription & Billing
    └── /dashboard/settings/profile → My Profile

AUTHENTICATED — PLATFORM ADMIN
├── /admin                         → Admin Dashboard (global metrics)
│
├── /admin/tenants                 → Tenant Management
│   ├── /admin/tenants             → Tenant List
│   ├── /admin/tenants/new         → Create Tenant
│   ├── /admin/tenants/:id         → Tenant Detail
│   └── /admin/tenants/:id/edit    → Edit Tenant
│
├── /admin/subscriptions           → Subscription Plan Management
│   ├── /admin/subscriptions       → All Plans
│   └── /admin/subscriptions/:id   → Edit Plan
│
├── /admin/sms-providers           → SMS Gateway Providers
│   ├── /admin/sms-providers       → Provider List
│   └── /admin/sms-providers/new   → Add Provider
│
├── /admin/analytics               → Platform Analytics
│   ├── /admin/analytics           → Global Overview
│   ├── /admin/analytics/sms       → SMS Volume Analytics
│   └── /admin/analytics/revenue   → Revenue Analytics
│
├── /admin/audit-logs              → Audit Logs Viewer
│
└── /admin/system                  → System Health
    ├── /admin/system              → Service Status
    ├── /admin/system/metrics      → Prometheus/Grafana Embed
    └── /admin/system/logs         → Centralized Logs (Loki)
```

### User Journey Summary

```
┌──────────────────────────────────────────────────────────────────────┐
│                COMPLETE USER JOURNEYS                                 │
│                                                                      │
│  JOURNEY 1: New Business Owner Signs Up                              │
│  ──────────────────────────────────────────                          │
│  Landing → Register → Business Info → Account → Store Config        │
│  → Plan Selection → Dashboard → Add Products → Configure SMS Menu   │
│  → Customize Templates → Test SMS → Go Live!                         │
│                                                                      │
│  JOURNEY 2: Business Owner Manages Daily Operations                  │
│  ──────────────────────────────────────────                          │
│  Login → Dashboard (check metrics) → Orders (process new orders)    │
│  → Update order status → Check SMS logs → Send broadcast promo      │
│  → Check analytics → Logout                                          │
│                                                                      │
│  JOURNEY 3: Staff Processes Orders                                   │
│  ───────────────────────────────────                                 │
│  Login → Dashboard (see today's orders) → Order List                │
│  → Accept new order → Mark as preparing → Mark as ready             │
│  → Assign delivery → Logout                                          │
│                                                                      │
│  JOURNEY 4: Delivery Agent Completes Delivery                        │
│  ──────────────────────────────────────                              │
│  Login → See assigned deliveries → Navigate to address              │
│  → Deliver → Mark as delivered → Collect payment → Next delivery    │
│                                                                      │
│  JOURNEY 5: Platform Admin Onboards New Tenant                       │
│  ──────────────────────────────────────                              │
│  Login → Admin Dashboard → Create Tenant → Configure plan           │
│  → Assign phone number → Set up SMS routing → Verify → Done         │
│                                                                      │
│  JOURNEY 6: Platform Admin Monitors Health                           │
│  ────────────────────────────────────                                │
│  Login → System Health (check all services) → SMS Analytics         │
│  → Check failed SMS → Investigate logs → Resolve → Monitor          │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Summary

| Aspect | Detail |
|--------|--------|
| **Who uses the website?** | Business owners, managers, staff, delivery agents, platform admins |
| **Who does NOT use the website?** | End customers (they use SMS only) |
| **Total pages** | ~35 unique pages across both dashboards |
| **Frontend tech** | React 18 + TypeScript + Vite |
| **Data fetching** | GraphQL via Apollo Client → BFF → REST APIs |
| **Auth** | Keycloak OIDC with JWT |
| **Real-time** | GraphQL Subscriptions (WebSocket) |
| **UI library** | Ant Design 5 + Tailwind CSS |
| **Visual designer** | React Flow (SMS menu tree) |
| **Charts** | Recharts |
| **State management** | Zustand |
| **Forms** | React Hook Form + Zod validation |
| **Responsive** | Desktop + Tablet + Mobile (for staff on the go) |
| **Theme** | Light + Dark mode toggle |

---

> **Document Version:** 1.0.0
> **Last Updated:** June 2026
> **Companion Document:** VISION.md (full system architecture)
```

Save this file at **`d:\thunai \WEBSITE_FLOW.md`**.

---

### Quick Summary of the Website:

| User | What They See | Key Pages |
|------|--------------|-----------|
| **End Customer** | ❌ Never sees the website — uses SMS only | N/A |
| **Business Owner** | Full dashboard with 30+ pages | Products, Orders, SMS Menu Designer, Analytics, Staff |
| **Manager** | Most pages except settings/billing | Orders, Products, Customers, Templates |
| **Staff/Cashier** | Limited — orders only | Order list, Order detail, Update status |
| **Delivery Agent** | Minimal — assigned deliveries only | Delivery board, Update delivery status |
| **Platform Admin** | Admin dashboard — manages all tenants | Tenant CRUD, SMS providers, System health |

The website has **~35 pages** total, uses **GraphQL only on the frontend** (via Apollo Client → BFF that calls REST APIs), **Keycloak for auth**, **React Flow for the visual SMS menu designer**, and **WebSocket subscriptions for real-time order updates**.