import { Navigate, useLocation } from "react-router-dom";
import { ReactNode } from "react";
import { useAuth} from "../context/AuthProvider.tsx";

interface ProtectedRouteProps {
    children: ReactNode;
    redirectTo?: string;
}

const ProtectedRoute = ({
                            children,
                            redirectTo = "/login",
                        }: ProtectedRouteProps) => {
    const { isLoggedIn} = useAuth();
    const location = useLocation();

    if (!isLoggedIn) {
        return <Navigate to={redirectTo} state={{ from: location }} replace />;
    }



    return children;
};

export default ProtectedRoute;
