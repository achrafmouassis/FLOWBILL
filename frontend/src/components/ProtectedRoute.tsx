import { Navigate, Outlet } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
    sub: string;
    roles: string[];
    exp: number;
}

interface ProtectedRouteProps {
    allowedRoles?: string[];
}

const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
    const token = localStorage.getItem('token');

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    try {
        const decoded = jwtDecode<DecodedToken>(token);
        // Check if token is expired
        if (decoded.exp * 1000 < Date.now()) {
            localStorage.removeItem('token');
            return <Navigate to="/login" replace />;
        }

        // Check for allowed roles if specified
        if (allowedRoles && allowedRoles.length > 0) {
            const hasRole = allowedRoles.some(role => decoded.roles.includes(role));
            if (!hasRole) {
                return <Navigate to="/login" replace />;
            }
        } else if (!decoded.roles || !decoded.roles.includes('ROLE_SUPER_ADMIN')) {
            // Fallback default protection for Super Admin if no allowedRoles passed
            return <Navigate to="/login" replace />;
        }

    } catch (error) {
        // Invalid token
        localStorage.removeItem('token');
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;
