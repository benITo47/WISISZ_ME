import React, { useState } from "react";
import styles from "./Overlay.module.css";
import InputField from "./InputField";
import Button from "./Button";

interface OverlayProps {
  visible: boolean;
  onClose: () => void;
  onSubmit: (input: string) => void;
  title: string;
  placeholder: string;
  submitText: string;
}

const Overlay: React.FC<OverlayProps> = ({
  visible,
  onClose,
  onSubmit,
  title,
  placeholder,
  submitText,
}) => {
  const [value, setValue] = useState("");

  if (!visible) return null;

  const handleSubmit = () => {
    if (value.trim() !== "") {
      onSubmit(value);
      setValue("");
      onClose();
    }
  };

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <h2>{title}</h2>
        <InputField
          value={value}
          onChange={setValue}
          placeholder={placeholder}
          required
        />
        <div className={styles["modal-buttons"]}>
          <Button
            className={styles.cancel}
            style={{ backgroundColor: "rgba(220,11,11,0.5)" }}
            onClick={onClose}
          >
            Cancel
          </Button>
          <Button className={styles.create} onClick={handleSubmit}>
            {submitText}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Overlay;
