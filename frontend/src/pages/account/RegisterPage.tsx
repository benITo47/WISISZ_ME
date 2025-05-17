import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import InputField from "../../components/InputField";
import Button from "../../components/Button";
import api from "../../api/api";
import axios from "axios";
import { useAuth } from "../../context/AuthProvider";
import styles from "./RegisterPage.module.css";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const [fname, setFname] = useState("");
  const [lname, setLname] = useState("");
  const [emailAddr, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const { setToken, setIsLoggedIn } = useAuth();

  const nameValidator = (input: string) => /^[a-zA-Z]{2,}$/.test(input);
  const emailValidator = (input: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input);
  const passwordValidator = (input: string) =>
    /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(input);

  const handleRegister = async () => {
    if (!fname || !lname || !emailAddr || !password || !confirmPassword) {
      setError("All fields are required.");
      return;
    }

    if (!nameValidator(fname) || !nameValidator(lname)) {
      setError("First and last name must be at least 2 letters.");
      return;
    }

    if (!emailValidator(emailAddr)) {
      setError("Please enter a valid email address.");
      return;
    }

    if (!passwordValidator(password)) {
      setError(
        "Password must be at least 8 characters with one letter and one number.",
      );
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      const response = await api.post("/auth/register", {
        fname,
        lname,
        emailAddr,
        password,
      });

      const accessToken = response.headers["accesstoken"];

      if (response.status === 200 && accessToken) {
        setToken(accessToken);
        setIsLoggedIn(true);
        navigate("/account");
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (error: any) {
      if (axios.isAxiosError?.(error)) {
        setError(error.response?.data?.message || "Registration error.");
      } else {
        setError("Unexpected error occurred.");
      }
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.registerBox}>
        <h2 className={styles.heading}>Create an Account</h2>

        <div className={styles.formInputs}>
          <div className={styles.input}>
            <InputField
              value={fname}
              onChange={setFname}
              placeholder="First Name"
              validator={nameValidator}
              errorMessage="First name must be at least 2 letters."
              required
            />
          </div>

          <div className={styles.input}>
            <InputField
              value={lname}
              onChange={setLname}
              placeholder="Last Name"
              validator={nameValidator}
              errorMessage="Last name must be at least 2 letters."
              required
            />
          </div>

          <div className={styles.input}>
            <InputField
              value={emailAddr}
              onChange={setEmail}
              placeholder="Email Address"
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
              validator={passwordValidator}
              errorMessage="Password must be at least 8 characters, with one letter and one number."
              required
            />
          </div>

          <div className={styles.input}>
            <InputField
              value={confirmPassword}
              onChange={setConfirmPassword}
              placeholder="Confirm Password"
              type="password"
              required
            />
          </div>
        </div>

        {error && <p className={styles.errorMessage}>{error}</p>}

        <div className={styles.buttonWrapper}>
          <Button onClick={handleRegister}>Sign Up</Button>
        </div>

        <div className={styles.footer}>
          Already have an account?{" "}
          <Button
            to="/login"
            style={{
              padding: "5px",
              borderRadius: "9px",
              fontSize: "0.65rem",
            }}
          >
            Log In
          </Button>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
