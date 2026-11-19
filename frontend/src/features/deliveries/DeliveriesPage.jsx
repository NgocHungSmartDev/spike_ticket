import ResourcePage from '../../components/ResourcePage';
import { useResource } from '../../hooks/useResource';

const fields = [
  { key: 'customerId', label: 'Customer', type: 'customer' },
  { key: 'destination', label: 'Destination' },
  { key: 'status', label: 'Status', type: 'status', defaultValue: 'Pending' },
  { key: 'trackingCode', label: 'Tracking code', required: false },
];

export default function DeliveriesPage() {
  const { items: customers, loading, error } = useResource('/customers');
  return <>
    {error && <div className="content"><div className="alert" role="alert">Could not load customers: {error}</div></div>}
    <ResourcePage title="Delivery" singular="delivery" endpoint="/deliveries" fields={fields}
      customers={customers} canCreate={!loading && customers.length > 0}
      toPayload={form => ({ ...form, customerId: Number(form.customerId) })}
      renderCell={(item, field) => {
        if (field.key === 'customerId') return customers.find(customer => customer.id === item.customerId)?.name || `#${item.customerId}`;
        if (field.key === 'status') return <span className={`badge ${item.status.toLowerCase().replace(' ', '-')}`}>{item.status}</span>;
        return item[field.key] || '—';
      }} />
  </>;
}
