import { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthContext';
import { errorMessage } from '../services/api';

const navigation = [
  { path: 'products', label: 'Products', icon: '▦' },
  { path: 'customers', label: 'Customers', icon: '♙' },
  { path: 'deliveries', label: 'Delivery', icon: '⇢' },
];

export default function AdminLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function signOut() {
    setBusy(true); setError('');
    try { await logout(); navigate('/login', { replace: true }); }
    catch (err) { setError(errorMessage(err)); }
    finally { setBusy(false); }
  }

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark">S</div><div><strong>Spike Admin</strong><small>Management console</small></div></div>
      <div className="nav-caption">WORKSPACE</div>
      <nav aria-label="Main navigation">{navigation.map(item => <NavLink key={item.path} to={item.path}
        className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
        <span className="nav-icon">{item.icon}</span><span className="nav-label">{item.label}</span>
      </NavLink>)}</nav>
      <div className="sidebar-bottom"><div className="avatar">{user[0]?.toUpperCase()}</div>
        <div className="sidebar-user"><strong>{user}</strong><small>Administrator</small></div>
        <button className="logout" onClick={signOut} disabled={busy} title="Log out">↪</button>
      </div>
    </aside>
    <main className="main-area">
      <header className="topbar">Workspace <span className="crumb">/</span> Admin <span className="top-user">{user[0]?.toUpperCase()}</span></header>
      {error && <div className="content"><div className="alert" role="alert">{error}</div></div>}
      <Outlet />
    </main>
  </div>;
}
