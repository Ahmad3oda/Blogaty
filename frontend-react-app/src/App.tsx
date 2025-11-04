import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import BlogList from "./pages/BlogList";
import BlogView from "./pages/BlogView";
import Layout from "./components/Layout";
import Follow from "./pages/FollowPage";
import Profile from "./pages/Profile";
import NotificationPopup from "./components/NotificationPopup";
import NotificationPage from "./pages/NotificationPage";
import { useEffect, useState } from "react";

function PrivateRoute({ children }: { children: JSX.Element }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" />;
}

function App() {
  const [userId, setUserId] = useState<number | null>(null);

  // Load userId from sessionStorage on mount
  useEffect(() => {
    console.log('🔍 Checking for user in sessionStorage...');
    const userId = Number(sessionStorage.getItem("userId"));
    
    if (userId) {
      try {
        console.log('✅ User found:', userId);
        console.log('📌 Setting userId to:', userId);
        setUserId(userId);
      } catch (error) {
        console.error('❌ Failed to parse user:', error);
      }
    } else {
      console.log('⚠️ No user in sessionStorage');
    }
  }, []);

  // Debug: Log whenever userId changes
  useEffect(() => {
    console.log('🔄 userId changed to:', userId);
  }, [userId]);

  return (
    <BrowserRouter>
      

      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Private routes */}
        <Route
          path="/blogs"
          element={
            <PrivateRoute>
              <Layout>
                <BlogList />
              </Layout>
            </PrivateRoute>
          }
        />
        <Route
          path="/blogs/:blogId"
          element={
            <PrivateRoute>
              <Layout>
                <BlogView />
              </Layout>
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <Layout>
                <Profile />
              </Layout>
            </PrivateRoute>
          }
        />
        <Route
          path="/profile/:userId"
          element={
            <PrivateRoute>
              <Layout>
                <Profile />
              </Layout>
            </PrivateRoute>
          }
        />
        <Route
          path="/follow"
          element={
            <PrivateRoute>
              <Layout>
                <Follow />
              </Layout>
            </PrivateRoute>
          }
        />
        <Route
          path="/notifications"
          element={
            <PrivateRoute>
              <Layout>
                <NotificationPage />
              </Layout>
            </PrivateRoute>
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>

      {/* SSE popup listener — only render when logged in */}
      {userId !== null && (
        <>
          <div style={{ 
            position: 'fixed', 
            top: 10, 
            right: 10, 
            background: 'lightblue', 
            padding: '5px 10px',
            borderRadius: 5,
            fontSize: 12,
            zIndex: 9999
          }}>
            👤 User ID: {userId} | SSE Active
          </div>
          <NotificationPopup userId={userId} />
        </>
      )}
    </BrowserRouter>
  );
}

export default App;