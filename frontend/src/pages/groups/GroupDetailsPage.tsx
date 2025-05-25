import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/api";
import styles from "./GroupDetailsPage.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { CategoryMap, CategoryKey } from "../../utils/categories";

// Import Twojego overlaya (dostosuj ścieżkę jeśli trzeba)
import OverlayOperation from "../../components/OverlayOperation";

interface Member {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  defaultShare: number;
}

interface SummaryTransaction {
  fromFirstName: string;
  fromLastName: string;
  fromEmailAddr: string;
  toFirstName: string;
  toLastName: string;
  toEmailAddr: string;
  amount: number;
}

interface OperationSummary {
  operationId: number;
  title: string;
  totalAmount: string;
  categoryName: string;
}

const GroupDetailsPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [groupName, setGroupName] = useState("");
  const [members, setMembers] = useState<Member[]>([]);
  const [summary, setSummary] = useState<SummaryTransaction[]>([]);
  const [operations, setOperations] = useState<OperationSummary[]>([]);
  const [selectedOperationId, setSelectedOperationId] = useState<number | null>(
    null,
  );
  const [error, setError] = useState<string | null>(null);

  const handleTransactionClick = (operationId: number) => {
    setSelectedOperationId(operationId);
  };

  useEffect(() => {
    if (!id) return;

    const fetchData = async () => {
      try {
        const groupRes = await api.get(`/me/teams/${id}`);
        setGroupName(groupRes.data.teamName);
        setMembers(groupRes.data.members);
      } catch (err: any) {
        console.error("[group] ❌ Failed to fetch group details", err);
        setError("Failed to load group info.");
        return;
      }

      try {
        const opsRes = await api.get<OperationSummary[]>(
          `/me/teams/${id}/operations/summary`,
        );
        setOperations(opsRes.data);
      } catch (err: any) {
        console.error("[group] ❌ Failed to fetch operations summary", err);
        setError("Failed to load operations.");
        return;
      }

      try {
        const summaryRes = await api.get<SummaryTransaction[]>(
          `/me/teams/${id}/operations/transactions`,
        );
        setSummary(summaryRes.data);
      } catch (err: any) {
        console.warn("[group] ⚠️ No summary found (likely empty group)");
        setSummary([]);
      }
    };

    fetchData();
  }, [id, navigate]);

  if (error) return <div className={styles.error}>{error}</div>;

  return (
    <div className={styles.wrapper}>
      <div className={styles.headerRow}>
        <h1 className={styles.groupName}>{groupName}</h1>
        <div
          className={styles["addButton"]}
          role="button"
          tabIndex={0}
          onClick={() => {
            /* jakiś handler np. do tworzenia nowej operacji */
          }}
          onKeyDown={(e) =>
            e.key === "Enter" &&
            {
              /* ten sam handler */
            }
          }
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
        {summary.map((tx, index) => (
          <p key={index}>
            {tx.fromFirstName} {tx.fromLastName} ➜ {tx.toFirstName}{" "}
            {tx.toLastName}:{" "}
            <span style={{ color: "orange" }}>{tx.amount.toFixed(2)} zł</span>
          </p>
        ))}
      </div>

      <div className={styles.transactionList}>
        {operations.map((op) => {
          const normalizedKey = op.categoryName.trim().toUpperCase();
          const category =
            CategoryMap[normalizedKey as CategoryKey] ?? CategoryMap.MISC;

          return (
            <div
              key={op.operationId}
              className={styles.transactionItem}
              onClick={() => handleTransactionClick(op.operationId)}
            >
              <div>
                <FontAwesomeIcon
                  icon={category.icon}
                  className={styles.categoryIcon}
                />
                <strong style={{ marginLeft: "0.5rem" }}>
                  {category.label}
                </strong>
              </div>
              <span>{parseFloat(op.totalAmount).toFixed(2)} zł</span>
            </div>
          );
        })}
      </div>

      {selectedOperationId && id && (
        <OverlayOperation
          teamId={id}
          operationId={selectedOperationId}
          visible={selectedOperationId !== null}
          onClose={() => setSelectedOperationId(null)}
        />
      )}
    </div>
  );
};

export default GroupDetailsPage;
