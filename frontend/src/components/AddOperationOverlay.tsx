import React, { useState, useEffect } from "react";
import styles from "./AddOperationOverlay.module.css";
import Button from "./Button";
import InputField from "./InputField";
import TextAreaField from "./TextArea";
import SelectField from "./SelectField";
import api from "../api/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlusCircle } from "@fortawesome/free-solid-svg-icons";
import { CategoryMap } from "../utils/categories";
import { useTheme } from "../context/ThemeProvider";

interface Participant {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  defaultShare?: number;
}

interface AddOperationOverlayProps {
  teamId: string;
  visible: boolean;
  onClose: () => void;
  participants: Participant[];
  currencyCode: string;
}

const AddOperationOverlay: React.FC<AddOperationOverlayProps> = ({
  teamId,
  visible,
  onClose,
  participants,
  currencyCode = "PLN",
}) => {
  const [title, setTitle] = useState("");
  const [totalAmount, setTotalAmount] = useState("");
  const [category, setCategory] = useState<string>(Object.keys(CategoryMap)[0]);
  const [description, setDescription] = useState("");
  const [selectedParticipants, setSelectedParticipants] = useState<
    Participant[]
  >([]);
  const [shares, setShares] = useState<Record<number, number>>({});
  const [fixedAmounts, setFixedAmounts] = useState<Record<number, number>>({});
  const [editSplitsActive, setEditSplitsActive] = useState(false);
  const [fixedEnabled, setFixedEnabled] = useState<Record<number, boolean>>({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { theme, toggleTheme } = useTheme();
  const categoryOptions = Object.entries(CategoryMap).map(
    ([key, category]) => ({
      value: key,
      label: category.label,
    }),
  );

  useEffect(() => {
    if (participants.length > 0) {
      setSelectedParticipants(participants);
      const defaultShares: Record<number, number> = {};
      const defaultFixed: Record<number, number> = {};
      const defaultFixedEnabled: Record<number, boolean> = {};

      participants.forEach((p) => {
        defaultShares[p.personId] = 1;
        defaultFixed[p.personId] = 0;
        defaultFixedEnabled[p.personId] = false;
      });

      setShares(defaultShares);
      setFixedAmounts(defaultFixed);
      setFixedEnabled(defaultFixedEnabled);
    }
  }, [participants]);

  useEffect(() => {
    const amount = parseFloat(totalAmount);
    if (!amount || amount <= 0 || selectedParticipants.length === 0) {
      setError(null);
      return;
    }
    let sumFixed = 0;
    selectedParticipants.forEach((p) => {
      sumFixed += fixedAmounts[p.personId] ?? 0;
    });
    if (sumFixed > amount) {
      setError("Sum of fixed amounts exceeds total amount");
    } else {
      setError(null);
    }
  }, [totalAmount, fixedAmounts, selectedParticipants]);

  const calculateSplits = (): Record<number, number> => {
    const amount = parseFloat(totalAmount);
    if (!amount || amount <= 0 || selectedParticipants.length === 0) return {};

    let sumFixed = 0;
    selectedParticipants.forEach((p) => {
      sumFixed += fixedAmounts[p.personId] ?? 0;
    });

    const remaining = amount - sumFixed;
    if (remaining < 0) return {};

    let sumShares = 0;
    selectedParticipants.forEach((p) => {
      sumShares += shares[p.personId] ?? 0;
    });

    const result: Record<number, number> = {};
    let runningSum = 0;
    selectedParticipants.forEach((p, i) => {
      const fixed = +(fixedAmounts[p.personId] ?? 0);
      const share = +(shares[p.personId] ?? 0);

      let sharePortion = 0;
      if (sumShares > 0 && remaining > 0) {
        sharePortion = +(share / sumShares) * remaining;
      }

      const total = fixed + sharePortion;
      const rounded = Math.round(total * 100) / 100;

      result[p.personId] = rounded;
      runningSum += rounded;
    });

    // Correct rounding errors on the last participant
    const delta = Math.round((amount - runningSum) * 100) / 100;
    const lastId =
      selectedParticipants[selectedParticipants.length - 1]?.personId;
    if (lastId !== undefined) {
      result[lastId] = +(result[lastId] + delta).toFixed(2);
    }

    return result;
  };

  const currentSplits = calculateSplits();

  const handleShareChange = (personId: number, value: number) => {
    setShares((prev) => ({
      ...prev,
      [personId]: prev[personId] === value ? 0 : value,
    }));
  };

  const handleFixedAmountChange = (personId: number, value: number) => {
    if (value < 0) return;
    setFixedAmounts((prev) => ({
      ...prev,
      [personId]: value,
    }));
  };

  const toggleParticipant = (p: Participant) => {
    const exists = selectedParticipants.find(
      (sp) => sp.personId === p.personId,
    );
    if (exists) {
      setSelectedParticipants(
        selectedParticipants.filter((sp) => sp.personId !== p.personId),
      );
      // Usuń też z udziałów i fixedów
      setShares((prev) => {
        const copy = { ...prev };
        delete copy[p.personId];
        return copy;
      });
      setFixedAmounts((prev) => {
        const copy = { ...prev };
        delete copy[p.personId];
        return copy;
      });
    } else {
      setSelectedParticipants([...selectedParticipants, p]);
      setShares((prev) => ({
        ...prev,
        [p.personId]: 1,
      }));
      setFixedAmounts((prev) => ({
        ...prev,
        [p.personId]: 0,
      }));
      setFixedEnabled((prev) => {
        const copy = { ...prev };
        delete copy[p.personId];
        return copy;
      });
    }
  };

  const handleSubmit = async () => {
    setError(null);
    if (!title.trim()) {
      setError("Title is required");
      return;
    }
    const amountNum = parseFloat(totalAmount);
    if (isNaN(amountNum) || amountNum <= 0) {
      setError("Total Amount must be positive");
      return;
    }
    if (selectedParticipants.length === 0) {
      setError("Select at least one participant");
      return;
    }
    if (error) {
      return;
    }

    const category_dummy = 1;
    setLoading(true);
    try {
      const payload = {
        title,
        totalAmount: parseFloat(totalAmount),
        categoryName: CategoryMap[category].label,
        currencyCode: "USD",
        description,
        operationType: "expense",
        participants: selectedParticipants.map((p) => ({
          personId: p.personId,
          owedAmount: currentSplits[p.personId] ?? 0,
        })),
      };
      await api.post(`/me/teams/${teamId}/operations`, payload);
      onClose();
    } catch (err: any) {
      setError("Failed to add operation.");
    } finally {
      setLoading(false);
    }
  };

  const getInitials = (p: Participant) =>
    (p.fname[0] + p.lname[0]).toUpperCase();

  if (!visible) return null;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <div className={styles.header}>
          <FontAwesomeIcon icon={faPlusCircle} className={styles.icon} />
          <h2>Add Operation</h2>
        </div>

        {error && <p className={styles.error}>{error}</p>}

        <InputField
          value={title}
          onChange={setTitle}
          placeholder="Title"
          required
        />

        <InputField
          value={totalAmount}
          onChange={setTotalAmount}
          placeholder="Total Amount"
          type="number"
          validator={(v) => parseFloat(v) > 0}
          errorMessage="Amount must be a positive number"
          required
        />

        <div className={styles.participantsSelector}>
          <label>Participants (click to select):</label>
          <div className={styles.participantsCircles}>
            {participants.map((p) => {
              const selected = selectedParticipants.some(
                (sp) => sp.personId === p.personId,
              );
              return (
                <div
                  key={p.personId}
                  className={`${styles.participantCircleWrapper} ${
                    selected ? styles.selectedWrapper : ""
                  }`}
                  onClick={() => toggleParticipant(p)}
                  title={`${p.fname} ${p.lname}`}
                  tabIndex={0}
                  role="button"
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ")
                      toggleParticipant(p);
                  }}
                >
                  <div className={styles.participantCircle}>
                    {getInitials(p)}
                  </div>
                  <div className={styles.participantAmount}>
                    {currentSplits[p.personId] !== undefined
                      ? currentSplits[p.personId].toFixed(2)
                      : ""}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
        {selectedParticipants.length > 0 && (
          <Button
            onClick={() => setEditSplitsActive((prev) => !prev)}
            className={styles.editSplitsToggle}
          >
            {editSplitsActive ? "Close Edit Splits" : "Edit Splits"}
          </Button>
        )}

        {editSplitsActive && (
          <div className={styles.editSplits}>
            <label>Edit shares and fixed amounts:</label>

            <div className={styles.splitsInputs}>
              {selectedParticipants.map((p) => {
                const fixed = fixedAmounts[p.personId] ?? 0;
                const share = shares[p.personId] ?? 1;
                const isFixedOn = fixedEnabled[p.personId] ?? false;
                return (
                  <div
                    key={p.personId}
                    className={`
                        ${styles.splitRow} 
    ${share > 0 && fixed === 0 ? styles.hasShare : ""} 
    ${fixed >= 0 && share === 0 ? styles.hasFixed : ""} 
    ${fixed > 0 && share > 0 ? styles.hasBoth : ""}
  `}
                  >
                    <span>
                      {p.fname} {p.lname}
                    </span>

                    <div style={{ display: "flex", gap: "0.5rem" }}>
                      {[0.5, 1, 2].map((val) => (
                        <Button
                          key={val}
                          onClick={() => handleShareChange(p.personId, val)}
                          style={{
                            padding: "6px 12px",
                            borderRadius: "6px",
                            backgroundColor:
                              share === val
                                ? theme === "dark"
                                  ? "#e2f989"
                                  : "#747bff"
                                : theme === "dark"
                                  ? "#3e3e3e"
                                  : "#f0f0f0", // ← more visible unselected color
                            color:
                              share === val
                                ? "#000"
                                : theme === "dark"
                                  ? "#ccc"
                                  : "#333", // ← improved unselected text color
                            fontWeight: share === val ? 700 : 500,
                            border: "1px solid #999", // optional: add a border for clarity
                            transition: "all 0.2s ease", // optional: for smoother feedback
                          }}
                        >
                          {val}
                        </Button>
                      ))}
                    </div>
                    <label className={styles.fixedAmountLabel}>
                      <input
                        type="checkbox"
                        checked={isFixedOn}
                        onChange={(e) => {
                          const enabled = e.target.checked;
                          setFixedEnabled((prev) => ({
                            ...prev,
                            [p.personId]: enabled,
                          }));

                          if (!enabled) {
                            handleFixedAmountChange(p.personId, 0); // reset when disabling
                          }
                        }}
                      />
                      {isFixedOn ? "" : `Fixed: ${fixed.toFixed(2)}`}
                    </label>

                    {isFixedOn && (
                      <InputField
                        value={fixed.toString()}
                        onChange={(val) => {
                          if (val === "") {
                            handleFixedAmountChange(p.personId, 0);
                            return;
                          }
                          const numVal = parseFloat(val);
                          if (!isNaN(numVal)) {
                            handleFixedAmountChange(p.personId, numVal);
                          }
                        }}
                        placeholder="Amount"
                        type="number"
                        step="any"
                        className={`${styles.fixedAmountInput} ${fixed < 0 ? styles.negative : ""}`}
                      />
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        <SelectField
          value={category}
          onChange={setCategory}
          options={categoryOptions}
          placeholder="Select category"
          label="Category"
          required
        />

        <TextAreaField
          value={description}
          onChange={setDescription}
          placeholder="Description"
          rows={3}
          maxRows={3}
          maxLength={300}
        />

        <div className={styles.modalButtons}>
          <Button
            className={styles.cancel}
            style={{ backgroundColor: "rgba(220,11,11,0.5)" }}
            onClick={onClose}
          >
            Cancel
          </Button>
          <Button
            className={styles.confirm}
            onClick={handleSubmit}
            disabled={loading}
          >
            {loading ? "Adding..." : "Add Operation"}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default AddOperationOverlay;
