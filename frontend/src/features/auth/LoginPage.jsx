import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { errorMessage } from '../../services/api';
import { useAuth } from './AuthContext';

export default function LoginPage() {
  const { user, loading, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [credentials, setCredentials] = useState({ username: 'demo', password: 'demo123' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const destination = location.state?.from?.pathname || '/admin/products';

  async function submit(event) {
    event.preventDefault(); setError(''); setBusy(true);
    try { await login(credentials); navigate(destination, { replace: true }); }
    catch (err) { setError(errorMessage(err)); }
    finally { setBusy(false); }
  }

  if (loading) return <div className="auth-screen"><div className="auth-card">Checking session…</div></div>;
  if (user) return <Navigate to={destination} replace />;
  return <div className="auth-screen"><div className="auth-card">
    <div className="brand-mark">S</div><p className="eyebrow">SPIKE ADMIN</p>
    <h1>Welcome back</h1><p>Sign in to manage your workspace.</p>
    {error && <div className="alert" role="alert">{error}</div>}
    <form onSubmit={submit}>
      <label>Username<input required value={credentials.username} onChange={event => setCredentials(value => ({ ...value, username: event.target.value }))} /></label>
      <label>Password<input required type="password" value={credentials.password} onChange={event => setCredentials(value => ({ ...value, password: event.target.value }))} /></label>
      <button disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
    </form><p className="hint">Demo account: demo / demo123</p>
  </div></div>;
}
