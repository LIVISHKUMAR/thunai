import { lazy } from 'react'
import { createBrowserRouter } from 'react-router-dom'
import TenantLayout from '../layouts/TenantLayout'
import Dashboard from '../pages/tenant/Dashboard'
import Products from '../pages/tenant/Products'
import Orders from '../pages/tenant/Orders'

const Customers = lazy(() => import('../pages/tenant/Customers'))
const SmsPage = lazy(() => import('../pages/tenant/Sms'))
const Analytics = lazy(() => import('../pages/tenant/Analytics'))

export const router = createBrowserRouter([
  {
    path: '/dashboard',
    element: <TenantLayout />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'products', element: <Products /> },
      { path: 'orders', element: <Orders /> },
      { path: 'customers', element: <Customers /> },
      { path: 'sms', element: <SmsPage /> },
      { path: 'analytics', element: <Analytics /> },
      { path: 'promos', element: <div>Promotions</div> },
      { path: 'settings', element: <div>Settings</div> },
    ],
  },
])
