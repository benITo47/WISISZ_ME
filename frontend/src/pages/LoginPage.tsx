import { useState } from "react";
import InputField from "../components/InputField";
import Button from "../components/Button";
import Container from "../components/Container";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthProvider";

const Login = () => {
  const [emailAddr, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const { setToken, setIsLoggedIn } = useAuth();

  const handleLogin = async () => {
    if (!emailAddr || !password) return;

    try {
      const response = await api.post("/auth/login", {
        emailAddr: emailAddr,
        password: password,
      });

      const accessToken = response.headers["accesstoken"];
      if (!accessToken) throw new Error("Brak accessTokena w nagłówku");

      setToken(accessToken);
      setIsLoggedIn(true);

      navigate("/account");
    } catch (error: any) {
      if (error.response) {
        alert(
          `Błąd logowania: ${
            error.response.data.message || error.response.statusText
          }`
        );
      } else {
        alert("Nie udało się połączyć z serwerem.");
      }
      console.error(error);
    }
  };

  return (
    <Container className="my-3 flex flex-col">
      <h2 className="mb-5 text-xl text-center">Log in</h2>

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

      <div className="flex justify-end mt-4">
        <Button onClick={handleLogin} style={{ fontSize: "14px" }}>
          Log in
        </Button>
      </div>

      <div className="mt-5 text-sm text-center">
        <p>
          Don’t have an account?{" "}
          <Button to="/register" style={{ fontSize: "12px" }}>
            Sign up
          </Button>
        </p>
      </div>
    </Container>
  );
};

export default Login;
