import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import PageLayout from "./components/PageLayout";
import HomePage from "./pages/HomePage";
import AuthProvider from "./context/AuthProvider";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<PageLayout />}>
            <Route index element={<HomePage />} />
            <Route path="/wallet" element={<HomePage />} />
            <Route path="/groups" element={<HomePage />} />
            <Route path="/about" element={<HomePage />} />
            <Route path="/account" element={<HomePage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
