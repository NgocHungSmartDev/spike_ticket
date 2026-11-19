export default function ResourceForm({ title, fields, values, onChange, onSubmit, onCancel, busy, customers }) {
  return <section className="panel form-panel">
    <div className="panel-heading"><h2>{title}</h2><button className="icon-button" type="button" onClick={onCancel} aria-label="Close form">×</button></div>
    <form className="record-form" onSubmit={onSubmit}>
      {fields.map(field => <label key={field.key}>{field.label}
        {field.type === 'status' ? <select value={values[field.key]} onChange={event => onChange(field.key, event.target.value)}>
          {['Pending', 'In transit', 'Delivered', 'Cancelled'].map(status => <option key={status}>{status}</option>)}
        </select> : field.type === 'customer' ? <select required value={values[field.key]} onChange={event => onChange(field.key, event.target.value)}>
          <option value="">Select customer</option>{customers.map(customer => <option key={customer.id} value={customer.id}>{customer.name}</option>)}
        </select> : <input required={field.required !== false} type={field.type || 'text'}
          min={field.type === 'number' ? '0' : undefined} step={field.type === 'number' ? '0.01' : undefined}
          value={values[field.key]} onChange={event => onChange(field.key, event.target.value)} />}
      </label>)}
      <div className="form-actions"><button disabled={busy}>{busy ? 'Saving…' : 'Save'}</button>
        <button type="button" className="secondary" onClick={onCancel}>Cancel</button></div>
    </form>
  </section>;
}
