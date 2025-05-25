import React, { useEffect, useState } from "react";
import styles from "./Overlay.module.css";
import Button from "./Button";
import api from "../api/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { CategoryMap, CategoryKey } from "../utils/categories";

interface Participant {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  share: number;
  paidAmount: number;
  currencyCode: string;
}

interface OperationDetails {
  title: string;
  description: string;
  totalAmount: string;
  operationDate: string;
  categoryName: string;
  participants: Participant[];
}

interface OverlayOperationProps {
  teamId: string;
  operationId: number | null;
  visible: boolean;
  onClose: () => void;
}

const OverlayOperation: React.FC<OverlayOperationProps> = ({
  teamId,
  operationId,
  visible,
  onClose,
}) => {
  const [operation, setOperation] = useState<OperationDetails | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!visible || operationId === null || !teamId) return;

    const fetchOperation = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await api.get<OperationDetails>(
          `/me/teams/${teamId}/operations/${operationId}`,
        );
        setOperation(res.data);
      } catch (err: any) {
        setError("Failed to load operation detials.");
      } finally {
        setLoading(false);
      }
    };

    fetchOperation();
  }, [teamId, operationId, visible]);

  if (!visible) return null;

  let category = CategoryMap.MISC; // fallback
  if (operation?.categoryName) {
    const key = operation.categoryName.trim().toUpperCase();
    if (key in CategoryMap) {
      category = CategoryMap[key as CategoryKey];
    }
  }

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        {loading ? (
          <p>⏳ Ładowanie...</p>
        ) : error ? (
          <p className={styles.error}>{error}</p>
        ) : operation ? (
          <>
            <div
              style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}
            >
              <FontAwesomeIcon icon={category.icon} />
              <span style={{ fontWeight: "bold", fontSize: "1.1rem" }}>
                {category.label}
              </span>
            </div>
            <h2>{operation.title}</h2>
            <p style={{ fontStyle: "italic", color: "#bbb" }}>
              {new Date(operation.operationDate).toLocaleString("pl-PL")}
            </p>
            <p>{operation.description}</p>
            <p>
              <strong>Suma:</strong>{" "}
              {parseFloat(operation.totalAmount).toFixed(2)} zł
            </p>
            <div>
              <strong>Uczestnicy:</strong>
              <ul style={{ marginTop: "0.5rem" }}>
                {operation.participants.map((p) => (
                  <li key={p.personId} style={{ marginBottom: "0.25rem" }}>
                    {p.fname} {p.lname} ({p.emailAddr}) – paid:{" "}
                    {p.paidAmount.toFixed(2)} {p.currencyCode}, udział:{" "}
                    {p.share.toFixed(2)}%
                  </li>
                ))}
              </ul>
            </div>
          </>
        ) : (
          <p>Brak danych do wyświetlenia.</p>
        )}

        <div className={styles["modal-buttons"]}>
          <Button
            className={styles.cancel}
            style={{ backgroundColor: "rgba(220,11,11,0.5)" }}
            onClick={onClose}
          >
            Zamknij
          </Button>
        </div>
      </div>
    </div>
  );
};

export default OverlayOperation;
