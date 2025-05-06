import React, { useState } from 'react'
import InputField from '../InputField';

interface AuthFormProps {
  title: string;
  onSubmit: (email: string, password: string) => void;
  submitText: string;
  bottomText?: React.ReactNode;

}

const AuthForm: React.FC<AuthFormProps> = ({
  title,
  onSubmit,
  submitText,
  bottomText
}) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(email, password);
  };
  return (
    
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center">{title}</h2>
        <form onSubmit={handleSubmit}>
          <InputField
          value={email}
          onChange={setEmail}
          validator={(val) => val.includes("@")}
          errorMessage='Please enter a valid email'
          placeholder = "Email"
          type = "email"
          required />
          <InputField />
          
          <Button />
        </form>
        {bottomText && <div className="mt-4 text-sm text-center text-gray-500">{bottomText}</div>}
      </div>
    </div>
  );
}

export default AuthForm