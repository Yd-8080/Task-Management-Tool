import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/auth.css";
import { toast } from "react-toastify";

function Register() {
  const [userName, setUserName] = useState("");
  const [userOfficialEmail, setUserOfficialEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("USER"); // default role
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    if (!userName || !userOfficialEmail || !password || !role) {
      setError("All fields are required");
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(userOfficialEmail)) {
      setError("Invalid email format");
      return false;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters");
      return false;
    }
    return true;
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    if (!validateForm()) return;

    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/api/user-auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userName,
          userOfficialEmail,
          password,
          role,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Registration failed");
      }

      toast.success("Login successful!");
      navigate("/login");
    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <h2>Register</h2>

        <form onSubmit={handleRegister}>
          <input
            placeholder="Name"
            value={userName}
            onChange={(e) => setUserName(e.target.value)}
          />

          <input
            type="email"
            placeholder="Email"
            value={userOfficialEmail}
            onChange={(e) => setUserOfficialEmail(e.target.value)}
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="ADMIN">Admin</option>
            <option value="MANAGER">Manager</option>
            <option value="DEVELOPER">Developer</option>
            <option value="TESTER">Tester</option>
          </select>


          {error && <p className="error-msg">{error}</p>}

          <button type="submit" disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        <p className="switch-link" onClick={() => navigate("/login")}>
          Already have an account? Login
        </p>
      </div>
    </div>
  );
}

export default Register;
