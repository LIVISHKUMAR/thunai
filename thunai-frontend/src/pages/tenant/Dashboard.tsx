import { Row, Col, Card, Statistic, Table, Tag, Button, message } from 'antd'
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'

interface RecentOrder {
  key: string
  orderId: string
  amount: number
  status: string
  customer: string
  time: string
}

const columns: ColumnsType<RecentOrder> = [
  { title: 'Order ID', dataIndex: 'orderId', key: 'orderId' },
  { title: 'Customer', dataIndex: 'customer', key: 'customer' },
  { title: 'Amount', dataIndex: 'amount', key: 'amount', render: (v: number) => `$${v.toFixed(2)}` },
  { title: 'Status', dataIndex: 'status', key: 'status', render: (status: string) => {
    const color = status === 'DELIVERED' ? 'green' : status === 'CANCELLED' ? 'red' : 'orange'
    return <Tag color={color}>{status}</Tag>
  }},
  { title: 'Time', dataIndex: 'time', key: 'time' },
  { title: 'Action', key: 'action', render: () => <Button type="link" size="small">View</Button> },
]

const data: RecentOrder[] = [
  { key: '1', orderId: '#ORD-0045', amount: 15.99, status: 'PREPARING', customer: '+91***4521', time: '2 mins ago' },
  { key: '2', orderId: '#ORD-0044', amount: 22.50, status: 'DELIVERED', customer: '+91***8834', time: '15 mins ago' },
  { key: '3', orderId: '#ORD-0043', amount: 8.99, status: 'DELIVERED', customer: '+91***2201', time: '23 mins ago' },
  { key: '4', orderId: '#ORD-0042', amount: 31.00, status: 'CANCELLED', customer: '+91***9912', time: '35 mins ago' },
]

export default function Dashboard() {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>Dashboard</h2>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic title="Orders Today" value={45} prefix={<ArrowUpOutlined />} valueStyle={{ color: '#cf1322' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="Revenue Today" value={682.5} precision={2} prefix="$" />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="SMS Sent Today" value={234} prefix={<ArrowUpOutlined />} valueStyle={{ color: '#3f8600' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="Active Customers" value={342} />
          </Card>
        </Col>
      </Row>
      <Card title="Recent Orders">
        <Table columns={columns} dataSource={data} pagination={{ pageSize: 5 }} size="middle" />
      </Card>
    </div>
  )
}
