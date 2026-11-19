import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function ProtectedRoute() {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <div className="auth-screen"><div className="auth-card">Checking session…</div></div>;
  return user ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />;
}
