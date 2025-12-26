import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SuperAdminDashboard from './pages/SuperAdminDashboard';
import TenantAdminDashboard from './pages/TenantAdminDashboard';
import UserDashboard from './pages/UserDashboard';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route element={<ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN']} />}>
          <Route path="/admin" element={<SuperAdminDashboard />} />
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN_ENTREPRISE']} />}>
          <Route path="/tenant-admin" element={<TenantAdminDashboard />} />
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['ROLE_USER']} />}>
          <Route path="/user" element={<UserDashboard />} />
        </Route>

        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
