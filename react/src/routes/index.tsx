import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import MainLayout from "@/layouts/MainLayout";
import AuthLayout from "@/layouts/AuthLayout";
import ProtectedRoute from "@/components/ProtectedRoute";

// Unlimited permit
import Login from "@/pages/Login";
// import Post from "@/pages/Post"; 

// User pages
import Home from "@/pages/user/Home";
import Explore from "@/pages/user/Explore";
import Collections from "@/pages/user/Collections";
import Messenger from "@/pages/user/Messenger";
import Settings from "@/pages/user/Settings";
import Profile from "@/pages/user/Profile";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        {/* Các route cho đăng nhập */}
        <Route
					index
          path="/login"
          element={
            <AuthLayout>
              <Login />
            </AuthLayout>
          }
        />
        {/* Route cho bài viết công cộng, không yêu cầu đăng nhập */}
        {/* <Route
          path="/post/:id"
          element={<Post />} // Đây là trang bài viết công cộng
        /> */}

        {/* Các route cần xác thực với role */}
        <Route path="/user/*">
          <Route
            index
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user"
                element={
                  <MainLayout>
                    <Home />
                  </MainLayout>
                }
              />
            }
          />
          <Route
            path="explore"
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user/explore"
                element={
                  <MainLayout>
                    <Explore />
                  </MainLayout>
                }
              />
            }
          />
          <Route
            path="profile"
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user/profile"
                element={
                  <MainLayout>
                    <Profile />
                  </MainLayout>
                }
              />
            }
          />
          <Route
            path="settings"
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user/settings"
                element={
                  <MainLayout>
                    <Settings />
                  </MainLayout>
                }
              />
            }
          />
          <Route
            path="collections"
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user/collections"
                element={
                  <MainLayout>
                    <Collections />
                  </MainLayout>
                }
              />
            }
          />
          <Route
            path="messenger"
            element={
              <ProtectedRoute
                allowedRoles={'user'}
                path="/user/messenger"
                element={
                  <MainLayout>
                    <Messenger />
                  </MainLayout>
                }
              />
            }
          />
        </Route>
      </Routes>
    </Router>
  );
};

export default AppRoutes;
