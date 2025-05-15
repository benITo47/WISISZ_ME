import React from "react";
import Container from "../components/Container";
import Button from "../components/Button";
import { useAuth } from "../context/AuthProvider";// Zakładam, że masz taki kontekst
import { useNavigate } from "react-router-dom";

const AccountPage: React.FC = () => {
  const { user, logOut } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logOut();
    navigate("/login");
  };

  return (
    <Container style={{ marginTop: "50px", padding: "2rem", maxWidth: "500px" }}>
      <h2 style={{ textAlign: "center", marginBottom: "2rem", color: "#e2f989" }}>
        Moje Konto
      </h2>

      <div style={{ marginBottom: "1rem", fontSize: "18px" }}>
        <strong>Imię:</strong> {user?.fname || "Nie podano"}
      </div>
      <div style={{ marginBottom: "1rem", fontSize: "18px" }}>
        <strong>Nazwisko:</strong> {user?.lname || "Nie podano"}
      </div>
      <div style={{ marginBottom: "2rem", fontSize: "18px" }}>
        <strong>Email:</strong> {user?.emailAddr || "Nie podano"}
      </div>

      <div style={{ display: "flex", justifyContent: "center" }}>
        <Button onClick={handleLogout}>Wyloguj się</Button>
      </div>
    </Container>
  );
};

export default AccountPage;
