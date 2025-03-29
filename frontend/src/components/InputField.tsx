import React from "react";
import styles from "./InputField.module.css";

interface InputFieldProps {
  value: string;
  onChange: (value: string) => void;
  validator?: (input: string) => boolean;
  errorMessage?: string;
  placeholder?: string;
  type?: string;
  name?: string;
  className?: string;
  showLabel?: boolean;
  required?: boolean;
}

const InputField: React.FC<InputFieldProps> = ({
  value,
  onChange,
  validator,
  errorMessage = "Invalid input",
  placeholder = "Enter text...",
  type = "text",
  name,
  className = "",
  showLabel = true,
  required = false,
}) => {
  const [error, setError] = React.useState("");

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    onChange(newValue);

    if (validator && !validator(newValue)) {
      setError(errorMessage);
    } else {
      setError("");
    }
  };

  const inputId = name || placeholder.replace(/\s+/g, "-").toLowerCase();
  const shouldFloat = value !== "";

  return (
    <>
      <div className={`${styles.inputContainer} ${className}`}>
        <input
          id={inputId}
          type={type}
          name={name}
          value={value}
          onChange={handleChange}
          className={styles.inputField}
          required={required}
        />
        {showLabel && (
          <label
            htmlFor={inputId}
            className={`${styles.floatingLabel} ${shouldFloat ? styles.floating : ""}`}
          >
            <span className={styles.labelText}>{placeholder}</span>
          </label>
        )}
      </div>
      <div className={styles.errorWrapper}>
        {error && <span className={styles.errorMessage}>{error}</span>}
      </div>
    </>
  );
};

export default InputField;
