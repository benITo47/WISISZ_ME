import { useState } from "react";
import InputField from "../../components/InputField";
import Button from "../../components/Button";
import { useNavigate } from "react-router-dom";
import api from "../../api/api";
import { useAuth } from "../../context/AuthProvider";
import styles from "./LoginPage.module.css";

const emailValidator = (input: string) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input);

const LoginPage = () => {
  const [emailAddr, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const { setToken, setIsLoggedIn } = useAuth();

  const handleLogin = async () => {
    // 1. Walidacja przed wysłaniem
    if (!emailAddr || !password) {
      alert("Please fill in both fields.");
      return;
    }

    if (!emailValidator(emailAddr)) {
      alert("Please enter a valid email address.");
      return;
    }

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
          }`,
        );
      } else {
        alert("Nie udało się połączyć z serwerem.");
      }
      console.error(error);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.loginBox}>
        <h2 className={styles.heading}>Log in</h2>

        <div className={styles.formInputs}>
          <div className={styles.input}>
            <InputField
              value={emailAddr}
              onChange={setEmail}
              placeholder="Email"
              type="email"
              validator={emailValidator}
              errorMessage="Please enter a valid email address."
              required
            />
          </div>
          <div className={styles.input}>
            <InputField
              value={password}
              onChange={setPassword}
              placeholder="Password"
              type="password"
              required
            />
          </div>
        </div>

        <div className={styles.buttonWrapper}>
          <Button onClick={handleLogin}>Log in</Button>
        </div>

        <div className={styles.footer}>
          <p>
            Don’t have an account?{" "}
            <Button
              to="/register"
              style={{
                padding: "5px",
                borderRadius: "9px",
                fontSize: "0.65rem",
              }}
            >
              Sign up
            </Button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
