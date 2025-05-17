import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/api";
import styles from "./GroupDetailsPage.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";

interface Member {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  defaultShare: number;
}

interface Balance {
  firstName: string;
  lastName: string;
  balance: number;
}

interface OperationItem {
  fname: string;
  lname: string;
  emailAddr: string;
  amount: number;
  currencyCode: string;
}

interface Operation {
  operationId: number;
  teamName: string;
  categoryName: string;
  operations: OperationItem[];
}

const GroupDetailsPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [groupName, setGroupName] = useState("");
  const [members, setMembers] = useState<Member[]>([]);
  const [balances, setBalances] = useState<Balance[]>([]);
  const [operations, setOperations] = useState<Operation[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    const fetchData = async () => {
      try {
        const groupRes = await api.get(`/me/teams/${id}`);
        setGroupName(groupRes.data.teamName);
        setMembers(groupRes.data.members);

        const [balanceRes, opsRes] = await Promise.all([
          api.get<Balance[]>(`/me/teams/${id}/operations/balance`),
          api.get<Operation[]>(`/me/teams/${id}/operations`),
        ]);

        setBalances(balanceRes.data);
        setOperations(opsRes.data);
      } catch (err: any) {
        console.error(err);
        setError("Failed to load group data.");
      }
    };

    fetchData();
  }, [id]);

  if (error) return <div className={styles.error}>{error}</div>;

  const handleTransactionClick = (id: number) => {
    console.log("Clicked transaction ID:", id);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.headerRow}>
        <h1 className={styles.groupName}>{groupName}</h1>
        <div
          className={styles["addButton"]}
          role="button"
          tabIndex={0}
          onClick={() => setShowCreateOverlay(true)}
          onKeyDown={(e) => e.key === "Enter" && setShowCreateOverlay(true)}
        >
          <FontAwesomeIcon icon={faPlus} className={styles.icon} />
        </div>
      </div>

      <div className={styles.members}>
        {members.map((m) => (
          <div
            key={m.personId}
            className={styles.circle}
            title={`${m.fname} ${m.lname}`}
          >
            {m.fname.charAt(0).toUpperCase()}
          </div>
        ))}
      </div>

      <div className={styles.summaryBox}>
        <p>
          <strong>Rozliczenia:</strong>
        </p>
        {balances.map((b, i) => (
          <p key={i}>
            {b.firstName} {b.lastName}:{" "}
            <span style={{ color: b.balance < 0 ? "red" : "lime" }}>
              {b.balance.toFixed(2)} zł
            </span>
          </p>
        ))}
      </div>

      <div className={styles.transactionList}>
        {operations.map((op) => (
          <div
            key={op.operationId}
            className={styles.transactionItem}
            onClick={() => handleTransactionClick(op.operationId)}
          >
            <div>
              <strong>{op.categoryName}</strong>
              <span
                style={{
                  marginLeft: "0.5rem",
                  fontSize: "0.85rem",
                  color: "#bbb",
                }}
              >
                ({op.operations[0]?.fname})
              </span>
            </div>
            <span>
              {op.operations.reduce((sum, o) => sum + o.amount, 0).toFixed(2)}{" "}
              {op.operations[0]?.currencyCode}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default GroupDetailsPage;
