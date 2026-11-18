import { useEffect, useState } from 'react';
import { api, errorMessage } from './api';

const empty = { name: '', price: '', description: '' };

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [credentials, setCredentials] = useState({ username: 'demo', password: 'demo123' });
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const res = await api.get('/auth/me');
        if (active) setUser(res.data.username);
      } catch (e) {
        if (active && e?.response?.status !== 401) setError(errorMessage(e));
      } finally { if (active) setLoading(false); }
    })();
    return () => { active = false; };
  }, []);

  useEffect(() => {
    if (!user) return;
    let active = true;
    api.get('/products').then(res => { if (active) setProducts(res.data); })
      .catch(e => { if (active) setError(errorMessage(e)); });
    return () => { active = false; };
  }, [user]);

  async function login(e) {
    e.preventDefault(); setError(''); setBusy(true);
    try {
      const res = await api.post('/auth/login', credentials);
      console.info('[FE AUTH] Login successful. ACCESS_TOKEN is HttpOnly and intentionally unavailable to JavaScript. Check DevTools > Application > Cookies and Network > login > Set-Cookie.');
      setUser(res.data.username);
    } catch (e) { setError(errorMessage(e)); }
    finally { setBusy(false); }
  }

  async function logout() {
    setError(''); setBusy(true);
    try {
      await api.post('/auth/logout');
      setUser(null); setProducts([]); setForm(empty); setEditingId(null);
    } catch (e) { setError(errorMessage(e)); }
    finally { setBusy(false); }
  }

  async function saveProduct(e) {
    e.preventDefault(); setError(''); setBusy(true);
    try {
      const payload = { name: form.name.trim(), price: Number(form.price), description: form.description };
      if (editingId == null) await api.post('/products', payload);
      else await api.put(`/products/${editingId}`, payload);
      const res = await api.get('/products'); setProducts(res.data);
      setForm(empty); setEditingId(null);
    } catch (e) { setError(errorMessage(e)); }
    finally { setBusy(false); }
  }

  async function removeProduct(id) {
    if (!window.confirm('Delete this product?')) return;
    setError(''); setBusy(true);
    try {
      await api.delete(`/products/${id}`);
      setProducts(prev => prev.filter(p => p.id !== id));
      if (editingId === id) { setEditingId(null); setForm(empty); }
    } catch (e) { setError(errorMessage(e)); }
    finally { setBusy(false); }
  }

  function editProduct(product) {
    setEditingId(product.id);
    setForm({ name: product.name, price: String(product.price), description: product.description || '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  return <main className="container">
    <header><div><div className="eyebrow">SPRING SECURITY · NGINX · REACT</div><h1>Products demo</h1><p>JWT in HttpOnly cookie · CSRF protection · Same-origin Gateway</p></div>
      {user && <button className="secondary" onClick={logout} disabled={busy}>Log out ({user})</button>}</header>
    {error && <div className="alert" role="alert">{error}</div>}
    {loading ? <section className="panel">Checking session…</section> : !user ?
      <section className="panel narrow"><h2>Sign in</h2><p>Demo account: <code>demo</code> / <code>demo123</code></p>
        <form onSubmit={login}>
          <label>Username<input required value={credentials.username} onChange={e => setCredentials(v => ({ ...v, username: e.target.value }))}/></label>
          <label>Password<input required type="password" value={credentials.password} onChange={e => setCredentials(v => ({ ...v, password: e.target.value }))}/></label>
          <button disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
        </form>
      </section> : <>
        <section className="panel"><h2>{editingId == null ? 'Create product' : `Edit product #${editingId}`}</h2>
          <form className="product-form" onSubmit={saveProduct}>
            <label>Name<input required value={form.name} onChange={e => setForm(v => ({ ...v, name: e.target.value }))}/></label>
            <label>Price<input required type="number" min="0" step="0.01" value={form.price} onChange={e => setForm(v => ({ ...v, price: e.target.value }))}/></label>
            <label className="full">Description<input value={form.description} onChange={e => setForm(v => ({ ...v, description: e.target.value }))}/></label>
            <div className="actions"><button disabled={busy}>{editingId == null ? 'Add product' : 'Save changes'}</button>
              {editingId != null && <button className="secondary" type="button" onClick={() => { setEditingId(null); setForm(empty); }}>Cancel</button>}</div>
          </form>
        </section>
        <section className="panel"><h2>Products <span className="count">{products.length}</span></h2>
          <div className="table-wrap"><table><thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Description</th><th>Actions</th></tr></thead>
            <tbody>{products.map(p => <tr key={p.id}><td>{p.id}</td><td>{p.name}</td><td>${Number(p.price).toFixed(2)}</td><td>{p.description}</td>
              <td className="table-actions"><button className="secondary small" disabled={busy} onClick={() => editProduct(p)}>Edit</button><button className="danger small" disabled={busy} onClick={() => removeProduct(p.id)}>Delete</button></td></tr>)}
              {products.length === 0 && <tr><td colSpan="5">No products yet</td></tr>}</tbody></table></div>
        </section>
      </>}
    <footer>Open DevTools → Application → Cookies to inspect ACCESS_TOKEN and XSRF-TOKEN.</footer>
  </main>;
}
