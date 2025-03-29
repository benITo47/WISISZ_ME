import './App.css';
import { Route, Routes } from "react-router-dom";
import PageLayout from './components/PageLayout';
import HomePage from "./pages/HomePage";



function App() {

  return (
    <Routes>
      <Route path="/" element={<PageLayout />}>
        <Route index element={<HomePage />} />
        <Route path="/wallet" element={<HomePage />} />
        <Route path="/groups" element={<HomePage />} />
        <Route path="/about" element={<HomePage />} />
        <Route path="/account" element={<HomePage />} />
      </Route>
    </Routes>
  );
}

export default App
