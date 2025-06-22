import React from "react";
import styles from "./InputField.module.css";

interface TextAreaFieldProps {
  value: string;
  onChange: (value: string) => void;
  validator?: (input: string) => boolean;
  errorMessage?: string;
  placeholder?: string;
  name?: string;
  className?: string;
  showLabel?: boolean;
  required?: boolean;
  rows?: number;
  maxLength?: number;
}

const TextAreaField: React.FC<TextAreaFieldProps> = ({
  value,
  onChange,
  validator,
  errorMessage = "Invalid input",
  placeholder = "Enter text...",
  name,
  className = "",
  showLabel = true,
  required = false,
  rows = 3,
  maxLength = 250,
}) => {
  const [error, setError] = React.useState("");

  const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = event.target.value;

    const newLinesCount = (newValue.match(/\n/g) || []).length;
    if (maxLength && newValue.length > maxLength) return;
    if (newLinesCount >= rows) return;

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
        <textarea
          id={inputId}
          name={name}
          value={value}
          onChange={handleChange}
          className={styles.inputField}
          required={required}
          rows={rows}
          style={{
            resize: "none",
            overflow: "hidden",
            height: `${rows * 1.5}em`, // 1.5em line-height x ilość wierszy
          }}
          maxLength={maxLength}
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

export default TextAreaField;
