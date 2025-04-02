import React, { useState } from "react";;
import Button from '../components/Button';
import Container from '../components/Container';
import InputField from "../components/InputField";

export default function HomeApp() {

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const validateUsername = (input: string) => input.length >= 4;

  return (
    <>
      <Container style={{ display: "flex", height: "600px", gap: "20px" }}>
        <Button onClick={() => alert("Button Clicked!")}>Click Me</Button>
        <Button to="/about">Im secretly a link </Button>
        <Button to="/about" disabled>Im secretly a disabled link :)</Button>
        <Button style={{ backgroundColor: "red", fontSize: "20px", height: "199px" }}>
          Custom Styled Button
        </Button>
        <Button className="custom-shadow">Styled with Class</Button>
        <Button disabled onClick={() => alert("You can't click this!")}>
          Disabled Button
        </Button>
        <Button onClick={() => console.log("Icon Button Clicked")}>
          🚀 Launch Rocket - check console logs
        </Button>
        <InputField
          value={username}
          onChange={setUsername}
          validator={(val) => val.includes("@")}
          errorMessage="Please enter a valid email"
          placeholder="Email"
          type="email"
          required
          className="custom-input"
        />


      </Container>
      <Container style={{ display: "flex", flexDirection: "column", height: "400px", width: "800px", gap: "10px" }}>
        <InputField
          value={username}
          onChange={setUsername}
          validator={validateUsername}
          errorMessage="Please enter a valid username"
          placeholder="Username"
          type="username"
          required
        />
        <InputField
          value={email}
          onChange={setEmail}
          validator={(val) => val.includes("@")}
          errorMessage="Please enter a valid email"
          placeholder="Email"
          type="email"
          required
        />
        <InputField
          value={password}
          onChange={setPassword}
          validator={(val) => val.includes("$")}
          errorMessage="Please enter a valid password"
          placeholder="Password"
          type="password"
          required
        />
      </Container>
    </>
  );
}
