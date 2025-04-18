import React from "react";
import { Route, Navigate, RouteProps } from "react-router-dom";
import { getUserRole } from "../utils/auth";

interface ProtectedRouteProps extends RouteProps {
  allowedRoles: string[]; // Các role được phép truy cập
  element: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  allowedRoles,
  element,
  ...rest
}) => {
  const role = getUserRole(); // Lấy vai trò người dùng

  if (!role && location.pathname.includes("/post")) {
    return <Route {...rest} element={element} />;
  }

  if (!role || !allowedRoles.includes(role)) {
    return <Navigate to="/login" />;
  }

  return <Route {...rest} element={element} />;
};

export default ProtectedRoute;
