import ResourcePage from '../../components/ResourcePage';

const fields = [
  { key: 'name', label: 'Name' },
  { key: 'price', label: 'Price', type: 'number' },
  { key: 'description', label: 'Description', required: false },
];

export default function ProductsPage() {
  return <ResourcePage title="Products" singular="product" endpoint="/products" fields={fields}
    toPayload={form => ({ ...form, price: Number(form.price) })}
    renderCell={(item, field) => field.key === 'price' ? `$${Number(item.price).toFixed(2)}` : item[field.key] || '—'} />;
}
