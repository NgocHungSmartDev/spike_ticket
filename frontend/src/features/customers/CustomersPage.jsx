import ResourcePage from '../../components/ResourcePage';

const fields = [
  { key: 'name', label: 'Name' },
  { key: 'email', label: 'Email', type: 'email' },
  { key: 'phone', label: 'Phone' },
  { key: 'address', label: 'Address' },
];

export default function CustomersPage() {
  return <ResourcePage title="Customers" singular="customer" endpoint="/customers" fields={fields} />;
}
