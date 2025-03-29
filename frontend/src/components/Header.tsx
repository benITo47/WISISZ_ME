import { useState } from "react";
import { Link } from "react-router-dom";
import styles from "./Header.module.css";
type NavLink = {
  name: string;
  path: string;
};

type HeaderProps = {
  logoUrl: string;
  logoText: string;
  navLinks: NavLink[];
};

export default function Header({ logoUrl, logoText, navLinks }: HeaderProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const closeMenu = () => setIsMenuOpen(false);

  return (
    <header className={styles.headerContainer}>
      <h1 className={styles.headerLogo}>
        <Link to={logoUrl} onClick={closeMenu} className={styles.headerLogoLink}>
          {logoText}
        </Link>
      </h1>

      <button className={styles.headerMenuIcon} onClick={toggleMenu}>
        <span className={styles.headerMenuLine}></span>
        <span className={styles.headerMenuLine}></span>
        <span className={styles.headerMenuLine}></span>
      </button>

      <nav className={styles.headerNavigation} data-open={isMenuOpen}>
        {navLinks.map(({ name, path }, index) => (
          <Link key={index} to={path} className={styles.headerNavLink} onClick={closeMenu}>
            {name}
          </Link>
        ))}
      </nav>
    </header>
  );
}
