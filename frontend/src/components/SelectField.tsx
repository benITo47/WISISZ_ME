import React from "react";
import styles from "./SelectField.module.css";

interface Option {
  value: string;
  label: string;
}

interface SelectFieldProps {
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  placeholder?: string;
  name?: string;
  className?: string;
  required?: boolean;
  disabled?: boolean;
  showLabel?: boolean;
  label?: string;
}

const SelectField: React.FC<SelectFieldProps> = ({
  value,
  onChange,
  options,
  placeholder = "Select an option",
  name,
  className = "",
  required = false,
  disabled = false,
  showLabel = true,
  label,
}) => {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(e.target.value);
  };

  const selectId = name || placeholder.replace(/\s+/g, "-").toLowerCase();

  return (
    <div className={`${styles.selectContainer} ${className}`}>
      {showLabel && label && (
        <label htmlFor={selectId} className={styles.label}>
          {label}
        </label>
      )}
      <select
        id={selectId}
        name={name}
        value={value}
        onChange={handleChange}
        required={required}
        disabled={disabled}
        className={styles.selectField}
      >
        {/* Opcjonalna pusta opcja z placeholderem */}
        <option value="" disabled>
          {placeholder}
        </option>

        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  );
};

export default SelectField;
