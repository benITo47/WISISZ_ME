import { Outlet } from "react-router";
import Header from "./Header";
import { CSSProperties } from "react";

type NavLink = {
  name: string;
  path: string;
};

const navLinks: NavLink[] = [
  { name: "wallet", path: "/wallet" },
  { name: "my groups", path: "/groups" },
  { name: "about", path: "/about" },
  { name: "account", path: "/account" },
];

const layoutStyle: CSSProperties = {
  width: "100vw",
  height: "100vh",
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  justifyContent: "start",
  overflowX: "hidden",
};

export default function PageLayout() {
  return (
    <main style={layoutStyle}>
      <Header logoText="wisisz.me" logoUrl="/" navLinks={navLinks} />
      <Outlet />
    </main>
  );
}
