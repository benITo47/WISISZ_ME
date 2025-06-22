import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import PageLayout from "./components/PageLayout";
import HomePage from "./pages/HomePage";
import AuthProvider from "./context/AuthProvider";
import LoginPage from "./pages/account/LoginPage";
import RegisterPage from "./pages/account/RegisterPage";
import AccountPage from "./pages/AccountPage";
import ProtectedRoute from "./components/ProtectedRoute";
import GroupsPage from "./pages/groups/GroupsPage";
import GroupDetailsPage from "./pages/groups/GroupDetailsPage";
import { ThemeProvider } from "./context/ThemeProvider";
function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<PageLayout />}>
              <Route index element={<HomePage />} />
              <Route path="/about" element={<HomePage />} />

              <Route
                path="/groups"
                element={
                  <ProtectedRoute>
                    <GroupsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/group/:id"
                element={
                  <ProtectedRoute>
                    <GroupDetailsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/account"
                element={
                  <ProtectedRoute>
                    <AccountPage />
                  </ProtectedRoute>
                }
              />

              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
