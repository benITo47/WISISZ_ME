import React from "react";
import styles from "./HomePage.module.css";
import pieniadzImage from "../assets/images/pieniadz.png";
import Button from "../components/Button";
import { useAuth } from "../context/AuthProvider";

const HomePage: React.FC = () => {
  const { isLoggedIn, user, logOut } = useAuth();

  return (
    <div className={styles.heroContainer}>
      <div className={styles.heroContent}>
        <h1 className={styles.heroTitle}>
          {isLoggedIn
            ? `Witaj, ${user?.fname}!`
            : "Wszystkie wydatki w jednym miejscu."}
        </h1>
        <div className={styles.heroButtonsSection}>
          {isLoggedIn ? (
            <Button onClick={logOut}>Log out</Button>
          ) : (
            <>
              <Button to="register">Create an account</Button>
              <Button to="login">Sign in</Button>
            </>
          )}
        </div>
      </div>
      <img src={pieniadzImage} alt="pieniadz" className={styles.heroImage} />
    </div>
  );
};

export default HomePage;
