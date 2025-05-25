import React, { useEffect, useState, useMemo } from "react";
import styles from "./OverlayOperation.module.css";
import Button from "./Button";
import api from "../api/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { CategoryMap, CategoryKey } from "../utils/categories";

interface Participant {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  share: number | null;
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
        setError("Failed to load operation details.");
      } finally {
        setLoading(false);
      }
    };

    fetchOperation();
  }, [teamId, operationId, visible]);

  const payer = useMemo(() => {
    if (!operation) return null;
    return operation.participants.find((p) => p.paidAmount > 0) ?? null;
  }, [operation]);

  if (!visible) return null;

  let category = CategoryMap.MISC;
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
          <p>⏳ Loading...</p>
        ) : error ? (
          <p className={styles.error}>{error}</p>
        ) : operation ? (
          <>
            <div className={styles.header}>
              <FontAwesomeIcon icon={category.icon} className={styles.icon} />
              <h2>{operation.title}</h2>
            </div>

            <p className={styles.date}>
              {new Date(operation.operationDate).toLocaleString("en-GB")}
            </p>

            <p className={styles.description}>
              {operation.description || "No description provided."}
            </p>

            <p className={styles.total}>
              <strong>Total:</strong>{" "}
              {parseFloat(operation.totalAmount).toFixed(2)} zł
            </p>

            {payer && (
              <p className={styles.payer}>
                <strong>Paid by:</strong> {payer.fname} {payer.lname} -{" "}
                {operation.totalAmount.toFixed(2)} {payer.currencyCode}
              </p>
            )}

            <div className={styles.participants}>
              <strong>Participants & Balances:</strong>
              <ul>
                {operation.participants.map((p) => (
                  <li key={p.personId} className={styles.participantItem}>
                    <span>
                      {p.fname} {p.lname} ({p.emailAddr})
                    </span>
                    <span
                      className={
                        p.paidAmount > 0
                          ? styles.balancePositive
                          : p.paidAmount < 0
                            ? styles.balanceNegative
                            : styles.balanceZero
                      }
                    >
                      {p.paidAmount > 0 && "+"}
                      {p.paidAmount.toFixed(2)} {p.currencyCode}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          </>
        ) : (
          <p>No data available to display.</p>
        )}

        <div className={styles.modalButtons}>
          <Button className={styles.cancel} onClick={onClose}>
            Close
          </Button>
        </div>
      </div>
    </div>
  );
};

export default OverlayOperation;
