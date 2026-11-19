import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../features/auth/AuthContext';
import LoginPage from '../features/auth/LoginPage';
import ProtectedRoute from '../features/auth/ProtectedRoute';
import AdminLayout from '../layouts/AdminLayout';
import ProductsPage from '../features/products/ProductsPage';
import CustomersPage from '../features/customers/CustomersPage';
import DeliveriesPage from '../features/deliveries/DeliveriesPage';

export default function App() {
  return <AuthProvider><Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route element={<ProtectedRoute />}>
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<Navigate to="products" replace />} />
        <Route path="products" element={<ProductsPage />} />
        <Route path="customers" element={<CustomersPage />} />
        <Route path="deliveries" element={<DeliveriesPage />} />
      </Route>
    </Route>
    <Route path="*" element={<Navigate to="/admin/products" replace />} />
  </Routes></AuthProvider>;
}
