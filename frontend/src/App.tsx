import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import PageLayout from "./components/PageLayout";
import HomePage from "./pages/HomePage";
import AuthProvider from "./context/AuthProvider";
import LoginPage from "./pages/account/LoginPage";
import RegisterPage from "./pages/account/RegisterPage";
import AccountPage from "./pages/AccountPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<PageLayout />}>
            <Route index element={<HomePage />} />
            <Route path="wallet" element={<HomePage />} />
            <Route path="groups" element={<HomePage />} />
            <Route path="about" element={<HomePage />} />

            <Route
              path="account"
              element={
                <ProtectedRoute>
                  <AccountPage />
                </ProtectedRoute>
              }
            />

            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
