import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import BlogList from "./pages/BlogList";
import BlogView from "./pages/BlogView";
import Layout from "./components/Layout"; 
import Follow from "./pages/FollowPage"; 
import Profile from "./pages/Profile"; 


function PrivateRoute({ children }: { children: JSX.Element }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

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
          path="/blogs/:blogId"
          element={
            <PrivateRoute>
              <Layout>
                <BlogView />
              </Layout>
            </PrivateRoute>
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
