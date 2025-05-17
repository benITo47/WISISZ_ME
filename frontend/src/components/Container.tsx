import React from "react";

type ContainerProps = {
  children: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
};

const defaultContainerStyle: React.CSSProperties = {
  marginLeft: "auto",
  marginRight: "auto",
  padding: "1rem",
  maxWidth: "1440px",
  background: "rgba(14, 14, 14, 0.1)",
  boxShadow: " 0 8px 32px 0 rgba(226, 217, 137, 0.37)",
  backdropFilter: "blur(17.5px)",
  borderRadius: "25px",
  border: " 1px solid rgba(226, 217, 137, 0.18)",
  color: "#fff",
};

const Container: React.FC<ContainerProps> = ({
  children,
  className,
  style,
}) => {
  const combinedStyle: React.CSSProperties = {
    ...defaultContainerStyle,
    ...style,
  };

  return (
    <div style={combinedStyle} className={` ${className || ""}`}>
      {children}
    </div>
  );
};

export default Container;
