import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Container from "../components/Container";
import InputField from "../components/InputField";
import Button from "../components/Button";
import api from "../api/api";
import axios from "axios";
import { useAuth } from "../context/AuthProvider";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const [fname, setFname] = useState("");
  const [lname, setLname] = useState("");
  const [emailAddr, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const { setToken, setIsLoggedIn, setUser } = useAuth();

  const handleRegister = async () => {
    if (password !== confirmPassword) {
      setError("Hasła nie są takie same.");
      return;
    }

    try {
      const response = await api.post("/auth/register", {
        fname: fname,
        lname: lname,
        emailAddr: emailAddr,
        password: password,
      });

      const accessToken = response.headers["accesstoken"];
      setToken(accessToken);
      setIsLoggedIn(true);

    } catch (error: any) {
      if (axios.isAxiosError?.(error)) {
        setError(error.response?.data?.message || "Błąd rejestracji.");
      } else {
        setError("Nieoczekiwany błąd.");
      }
    }

    console.log("Rejestracja użytkownika:", {
      fname,
      lname,
      emailAddr,
      password,
    });

    
    navigate("/account");
  };

  return (
    <Container
      style={{ marginTop: "50px", padding: "2rem", maxWidth: "500px" }}
    >
      <h2
        style={{ textAlign: "center", marginBottom: "2rem", color: "#e2f989" }}
      >
        Załóż konto
      </h2>

      <InputField
        value={fname}
        onChange={setFname}
        placeholder="Imię"
        required
      />

      <InputField
        value={lname}
        onChange={setLname}
        placeholder="Nazwisko"
        required
      />

      <InputField
        value={emailAddr}
        onChange={setEmail}
        placeholder="Adres e-mail"
        type="email"
        required
      />

      <InputField
        value={password}
        onChange={setPassword}
        placeholder="Hasło"
        type="password"
        required
      />

      <InputField
        value={confirmPassword}
        onChange={setConfirmPassword}
        placeholder="Powtórz hasło"
        type="password"
        required
      />

      {error && (
        <p style={{ color: "red", textAlign: "center", margin: "1rem 0" }}>
          {error}
        </p>
      )}

      <div
        style={{ display: "flex", justifyContent: "center", marginTop: "2rem" }}
      >
        <Button onClick={handleRegister}>Zarejestruj się</Button>
      </div>

      <p style={{ textAlign: "center", marginTop: "1rem" }}>
        Masz już konto?{" "}
        <Button to="/login" style={{ fontSize: "14px" }}>
          Zaloguj się
        </Button>
      </p>
    </Container>
  );
};

export default RegisterPage;
