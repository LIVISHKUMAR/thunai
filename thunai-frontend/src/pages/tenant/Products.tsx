import { useState } from 'react'
import { Table, Button, Input, Space, Tag, Modal, Form, InputNumber, Select, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'

interface Product {
  key: string
  id: string
  name: string
  category: string
  price: number
  stock: number
  status: string
}

const columns: ColumnsType<Product> = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: 'Name', dataIndex: 'name', key: 'name' },
  { title: 'Category', dataIndex: 'category', key: 'category' },
  { title: 'Price', dataIndex: 'price', key: 'price', render: (v: number) => `$${v.toFixed(2)}` },
  { title: 'Stock', dataIndex: 'stock', key: 'stock' },
  { title: 'Status', dataIndex: 'status', key: 'status', render: (status: string) => <Tag color={status === 'Active' ? 'green' : 'red'}>{status}</Tag> },
  { title: 'Actions', key: 'actions', render: () => (
    <Space>
      <Button type="text" icon={<EditOutlined />} />
      <Button type="text" danger icon={<DeleteOutlined />} />
    </Space>
  )},
]

const initialData: Product[] = [
  { key: '1', id: 'P001', name: 'Margherita', category: 'Pizzas', price: 8.99, stock: 999, status: 'Active' },
  { key: '2', id: 'P002', name: 'Pepperoni', category: 'Pizzas', price: 10.99, stock: 999, status: 'Active' },
  { key: '3', id: 'P003', name: 'Spaghetti', category: 'Pasta', price: 7.99, stock: 50, status: 'Active' },
  { key: '4', id: 'P004', name: 'Coke 500ml', category: 'Drinks', price: 2.49, stock: 200, status: 'Active' },
  { key: '5', id: 'P005', name: 'Tiramisu', category: 'Desserts', price: 5.99, stock: 0, status: 'Inactive' },
]

export default function Products() {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [form] = Form.useForm()

  const handleAdd = () => {
    setIsModalOpen(true)
  }

  const handleSubmit = () => {
    form.validateFields().then(values => {
      message.success('Product created successfully')
      setIsModalOpen(false)
      form.resetFields()
    })
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h2>Products</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>Add Product</Button>
      </div>
      <Table columns={columns} dataSource={initialData} pagination={{ pageSize: 10 }} />
      <Modal title="Add Product" open={isModalOpen} onOk={handleSubmit} onCancel={() => setIsModalOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="Product Name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="category" label="Category" rules={[{ required: true }]}>
            <Select options={[{ value: 'Pizzas', label: 'Pizzas' }, { value: 'Pasta', label: 'Pasta' }, { value: 'Drinks', label: 'Drinks' }, { value: 'Desserts', label: 'Desserts' }]} />
          </Form.Item>
          <Form.Item name="price" label="Price" rules={[{ required: true }]}>
            <InputNumber min={0} prefix="$" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="stock" label="Stock">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
