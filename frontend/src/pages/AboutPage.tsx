import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faUsers,
  faScaleBalanced,
  faCoins,
  faHandshake,
} from "@fortawesome/free-solid-svg-icons";
import styles from "./AboutPage.module.css";

export default function AboutPage() {
  return (
    <section className={styles.aboutSection}>
      <h1 className={styles.title}>
        About <span className={styles.accent}>wisisz.me</span>
      </h1>

      <p className={styles.description}>
        <strong>wisisz.me</strong> is a smart and minimalistic app built to help
        groups of people split expenses fairly. Whether you're organizing a
        trip, sharing rent with roommates, or managing a group project budget,
        this app helps track who paid for what and who owes whom — no
        spreadsheets, no hassle.
      </p>

      <div className={styles.featuresGrid}>
        <div className={styles.card}>
          <FontAwesomeIcon icon={faUsers} className={styles.icon} />
          <h3 className={styles.cardTitle}>Groups Made Easy</h3>
          <p className={styles.cardText}>
            Create private groups and invite your friends to collaborate on
            shared expenses in real time.
          </p>
        </div>

        <div className={styles.card}>
          <FontAwesomeIcon icon={faScaleBalanced} className={styles.icon} />
          <h3 className={styles.cardTitle}>Automatic Settlements</h3>
          <p className={styles.cardText}>
            Automatically calculates the payments needed to settle all debts.
          </p>
        </div>

        <div className={styles.card}>
          <FontAwesomeIcon icon={faCoins} className={styles.icon} />
          <h3 className={styles.cardTitle}>Clear Overview</h3>
          <p className={styles.cardText}>
            Get a transparent breakdown of who paid, how much, and who owes what
            — at a glance.
          </p>
        </div>

        <div className={styles.card}>
          <FontAwesomeIcon icon={faHandshake} className={styles.icon} />
          <h3 className={styles.cardTitle}>Built on Trust</h3>
          <p className={styles.cardText}>
            We don’t deal with actual payments — just help you stay on the same
            page with the people you trust.
          </p>
        </div>
      </div>
    </section>
  );
}
