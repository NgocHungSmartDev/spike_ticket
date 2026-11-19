export default function ResourceTable({ fields, items, loading, renderCell, onEdit, onDelete, busy }) {
  return <section className="panel list-panel">
    <div className="panel-heading"><div><h2>All records</h2><p>{loading ? 'Loading…' : `${items.length} total records`}</p></div></div>
    <div className="table-wrap"><table><thead><tr><th>ID</th>{fields.map(field => <th key={field.key}>{field.label}</th>)}<th>Actions</th></tr></thead>
      <tbody>{items.map(item => <tr key={item.id}><td className="id-cell">#{item.id}</td>
        {fields.map(field => <td key={field.key}>{renderCell ? renderCell(item, field) : item[field.key] || '—'}</td>)}
        <td><div className="row-actions"><button className="text-button" disabled={busy} onClick={() => onEdit(item)}>Edit</button>
          <button className="text-button delete" disabled={busy} onClick={() => onDelete(item)}>Delete</button></div></td></tr>)}
        {!loading && items.length === 0 && <tr><td className="empty-state" colSpan={fields.length + 2}>No records yet.</td></tr>}</tbody>
    </table></div>
  </section>;
}
