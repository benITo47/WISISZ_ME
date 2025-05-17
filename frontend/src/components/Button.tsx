import React, { ReactNode, useState } from "react";
import { Link } from "react-router-dom";

interface ButtonProps {
  onClick?: () => void;
  to?: string;
  style?: React.CSSProperties;
  disabled?: boolean;
  className?: string;
  children: ReactNode;
}

const defaultButtonStyle: React.CSSProperties = {
  display: "inline-block",
  width: "fit-content",
  height: "fit-content",
  padding: "10px 20px",
  fontSize: "16px",
  border: "solid",
  borderWidth: "1px",
  borderColor: "white",
  borderRadius: "15px",
  cursor: "pointer",
  backgroundColor: "#0e0e0e",
  color: "white",
  transition: "0.4s ease",
  outline: "none",
  textDecoration: "none",
};

const hoverButtonStyle: React.CSSProperties = {
  backgroundColor: "#404040",
  borderColor: "#e2f989",
  transform: "scale(1.05)",
  borderWidth: "1px",
};

const disabledButtonStyle: React.CSSProperties = {
  backgroundColor: "#555",
  color: "#aaa",
  cursor: "not-allowed",
  borderColor: "#777",
  transform: "none",
  fontSize: "16px",
};

const Button: React.FC<ButtonProps> = ({
  onClick,
  to,
  style,
  disabled = false,
  className = "",
  children,
}) => {
  const [isHovered, setIsHovered] = useState(false);

  const combinedStyle: React.CSSProperties = {
    ...defaultButtonStyle,
    ...(disabled ? disabledButtonStyle : isHovered ? hoverButtonStyle : {}),
    ...style,
  };

  if (to) {
    return (
      <Link
        to={disabled ? "#" : to}
        className={`${className}`}
        style={combinedStyle}
        onMouseEnter={() => !disabled && setIsHovered(true)}
        onMouseLeave={() => !disabled && setIsHovered(false)}
        onClick={(e) => disabled && e.preventDefault()}
      >
        {children}
      </Link>
    );
  }

  return (
    <button
      onClick={disabled ? undefined : onClick}
      className={`custom-button ${className}`}
      style={combinedStyle}
      disabled={disabled}
      onMouseEnter={() => !disabled && setIsHovered(true)}
      onMouseLeave={() => !disabled && setIsHovered(false)}
    >
      {children}
    </button>
  );
};

export default Button;
