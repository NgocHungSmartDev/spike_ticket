import { useState } from 'react';
import { useResource } from '../hooks/useResource';
import ResourceForm from './ResourceForm';
import ResourceTable from './ResourceTable';

export default function ResourcePage({ title, singular, endpoint, fields, customers = [], renderCell, toPayload, canCreate = true }) {
  const { items, loading, error, save, remove } = useResource(endpoint);
  const [form, setForm] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [busy, setBusy] = useState(false);
  const empty = () => Object.fromEntries(fields.map(field => [field.key, field.defaultValue ?? '']));

  function startCreate() { setEditingId(null); setForm(empty()); }
  function startEdit(item) { setEditingId(item.id); setForm(Object.fromEntries(fields.map(field => [field.key, String(item[field.key] ?? '')]))); window.scrollTo({ top: 0, behavior: 'smooth' }); }
  async function submit(event) {
    event.preventDefault(); setBusy(true);
    try { if (await save(editingId, toPayload ? toPayload(form) : form)) { setForm(null); setEditingId(null); } }
    finally { setBusy(false); }
  }
  async function deleteItem(item) {
    if (!window.confirm(`Delete this ${singular}?`)) return;
    setBusy(true);
    try { if (await remove(item.id) && editingId === item.id) { setForm(null); setEditingId(null); } }
    finally { setBusy(false); }
  }

  return <div className="content"><div className="page-heading"><div><p className="eyebrow">MANAGEMENT</p><h1>{title}</h1>
    <p>Manage your {title.toLowerCase()} in one place.</p></div>
    <button onClick={startCreate} disabled={busy || loading || !canCreate}>+ Add {singular}</button></div>
    {error && <div className="alert" role="alert">{error}</div>}
    {form && <ResourceForm title={`${editingId == null ? 'Add' : 'Edit'} ${singular}`} fields={fields} values={form}
      onChange={(key, value) => setForm(previous => ({ ...previous, [key]: value }))} onSubmit={submit}
      onCancel={() => setForm(null)} busy={busy} customers={customers} />}
    <ResourceTable fields={fields} items={items} loading={loading} renderCell={renderCell}
      onEdit={startEdit} onDelete={deleteItem} busy={busy} />
  </div>;
}
