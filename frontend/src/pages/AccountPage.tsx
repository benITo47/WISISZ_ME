import React from "react";
import Container from "../../components/Container";
import Button from "../components/Button";
import { useAuth } from "../context/AuthProvider";
import { useNavigate } from "react-router-dom";
import { useTheme } from "../context/ThemeProvider";

import styles from "./AccountPage.module.css";

const AccountPage: React.FC = () => {
  const { user, logOut } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleLogout = () => {
    logOut();
    navigate("/login");
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>My account</h2>

      <div className={styles.info}>
        <strong>Name:</strong> {user?.fname || "Nie podano"}
      </div>
      <div className={styles.info}>
        <strong>Surname:</strong> {user?.lname || "Nie podano"}
      </div>
      <div className={styles.info}>
        <strong>Email:</strong> {user?.emailAddr || "Nie podano"}
      </div>

      <div className={styles.actions}>
        <Button onClick={handleLogout}>Log out</Button>
        <Button onClick={toggleTheme}>
          {theme === "dark" ? "🌞 Light Mode" : "🌙 Dark Mode"}
        </Button>
      </div>
    </div>
  );
};

export default AccountPage;
