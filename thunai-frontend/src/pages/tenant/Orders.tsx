import { useState } from 'react'
import { Table, Button, Tag, Space, Select, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'

interface Order {
  key: string
  orderId: string
  customer: string
  items: string
  total: number
  status: string
  time: string
}

const columns: ColumnsType<Order> = [
  { title: 'Order ID', dataIndex: 'orderId', key: 'orderId' },
  { title: 'Customer', dataIndex: 'customer', key: 'customer' },
  { title: 'Items', dataIndex: 'items', key: 'items' },
  { title: 'Total', dataIndex: 'total', key: 'total', render: (v: number) => `$${v.toFixed(2)}` },
  { title: 'Status', dataIndex: 'status', key: 'status', render: (status: string) => {
    const color = status === 'DELIVERED' ? 'green' : status === 'CANCELLED' ? 'red' : status === 'READY' ? 'blue' : 'orange'
    return <Tag color={color}>{status}</Tag>
  }},
  { title: 'Time', dataIndex: 'time', key: 'time' },
  { title: 'Actions', key: 'actions', render: () => (
    <Space>
      <Select defaultValue="PREPARING" style={{ width: 140 }} onChange={(v) => message.success(`Order updated to ${v}`)}>
        <Select.Option value="PREPARING">Preparing</Select.Option>
        <Select.Option value="READY">Ready</Select.Option>
        <Select.Option value="OUT_FOR_DELIVERY">Out for Delivery</Select.Option>
        <Select.Option value="DELIVERED">Delivered</Select.Option>
        <Select.Option value="CANCELLED">Cancelled</Select.Option>
      </Select>
    </Space>
  )},
]

const data: Order[] = [
  { key: '1', orderId: '#ORD-0045', customer: '+91***4521', items: '1x Margherita (Med)', total: 13.99, status: 'PREPARING', time: '2 mins ago' },
  { key: '2', orderId: '#ORD-0044', customer: '+91***8834', items: '2x Pepperoni + Coke', total: 24.48, status: 'DELIVERED', time: '15 mins ago' },
  { key: '3', orderId: '#ORD-0043', customer: '+91***2201', items: '1x Spaghetti', total: 7.99, status: 'DELIVERED', time: '23 mins ago' },
]

export default function Orders() {
  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>Orders</h2>
      <Table columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />
    </div>
  )
}
