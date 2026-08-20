import { Outlet } from 'react-router-dom'
import { Layout, Menu, Button, theme, Badge, Dropdown } from 'antd'
import {
  DashboardOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  UserOutlined,
  MessageOutlined,
  BarChartOutlined,
  GiftOutlined,
  SettingOutlined,
  LogoutOutlined,
  BellOutlined,
  AppstoreOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
} from '@ant-design/icons'
import { useState } from 'react'
import { useAuthStore } from '../store/authStore'
import type { MenuProps } from 'antd'

const { Sider, Header, Content } = Layout

export default function TenantLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const { user, logout } = useAuthStore()
  const { token } = theme.useToken()

  const menuItems: MenuProps['items'] = [
    { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/dashboard/products', icon: <ShoppingOutlined />, label: 'Products' },
    { key: '/dashboard/orders', icon: <ShoppingCartOutlined />, label: 'Orders' },
    { key: '/dashboard/customers', icon: <UserOutlined />, label: 'Customers' },
    { key: '/dashboard/sms', icon: <MessageOutlined />, label: 'SMS' },
    { key: '/dashboard/sms/menu-tree', icon: <AppstoreOutlined />, label: 'Menu Tree' },
    { key: '/dashboard/analytics', icon: <BarChartOutlined />, label: 'Analytics' },
    { key: '/dashboard/promos', icon: <GiftOutlined />, label: 'Promos' },
    { key: '/dashboard/settings', icon: <SettingOutlined />, label: 'Settings' },
  ]

  const userMenuItems: MenuProps['items'] = [
    { key: 'logout', icon: <LogoutOutlined />, label: 'Logout', onClick: logout },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} theme="dark">
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontWeight: 'bold', fontSize: collapsed ? 16 : 20 }}>
          {collapsed ? 'T' : 'thunai'}
        </div>
        <Menu theme="dark" mode="inline" defaultSelectedKeys={['/dashboard']} items={menuItems} />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: token.colorBgContainer, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} style={{ fontSize: 16 }} />
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <Badge count={3}><Button type="text" icon={<BellOutlined />} /></Badge>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Button type="text">{user?.name}</Button>
            </Dropdown>
          </div>
        </Header>
        <Content style={{ margin: 16, padding: 24, background: token.colorBgContainer, borderRadius: token.borderRadiusLG, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
