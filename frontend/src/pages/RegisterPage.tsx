import React, { useState } from "react";
import InputField from "../components/InputField";
import Button from "../components/Button";
import Container from "../components/Container";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthProvider";

const API_URL = import.meta.env.VITE_API_URL;

const Register = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [emailAddr, setEmail] = useState("");
  const [fname, setFname] = useState("");
  const [lname, setLname] = useState("");
  const { setToken, setIsLoggedIn, setUser } = useAuth();
  
  const navigate = useNavigate();

  const handleRegister = async () => {
    if (!username || !emailAddr || !password || !fname || !lname) return;

    try {
      const response = await api.post("/auth/register", {
        username,
        emailAddr,
        password,
        fname,
        lname
      });

      
      navigate("/account");
    } catch (error: any) {
      if (error.response) {
        alert(
          `Błąd: ${error.response.data.message || error.response.statusText}`
        );
      } else {
        alert("Nie udało się połączyć z serwerem.");
      }
      console.error(error);
    }
  };

  return (
    <Container className="my-3 flex flex-col">
      <h2 className="mb-5">Sign up</h2>
      <InputField
        value={fname}
        onChange={setFname}
        placeholder="Name"
        required
      />
      <InputField
        value={lname}
        onChange={setLname}
        placeholder="Surname"
        required
      />
      <InputField
        value={username}
        onChange={setUsername}
        placeholder="Username"
        required
      />
      <InputField
        value={emailAddr}
        onChange={setEmail}
        placeholder="Email"
        required
      />
      <InputField
        value={password}
        onChange={setPassword}
        placeholder="Password"
        type="password"
        required
      />
      <div className="flex flex-col justify-end ml-auto">
        <Button
          onClick={handleRegister}
          style={{ fontSize: "12px", margin: "auto" }}
        >
          Sign up
        </Button>
      </div>
      <div className="mt-5">
        <p>
          Already have an account?{" "}
          <Button
            to="/login"
            style={{ fontSize: "10px", margin: "auto", padding: "6px" }}
          >
            Log in
          </Button>
        </p>
      </div>
    </Container>
  );
};

export default Register;
