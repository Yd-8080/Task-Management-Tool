import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/auth.css";
import { toast } from "react-toastify";

function Login() {
  const [userOfficialEmail, setUserOfficialEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    if (!userOfficialEmail || !password) {
      setError("Both fields are required");
      return false;
    }
    return true;
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    if (!validateForm()) return;

    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/api/user-auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userOfficialEmail,
          password,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Login failed");
      }

      const token = await res.text(); // backend returns String
      localStorage.setItem("token", token);
      const payload = JSON.parse(atob(token.split('.')[1]));
      localStorage.setItem("userEmail", payload.sub);
      localStorage.setItem("userRole", payload.role || "DEVELOPER"); // ✅ ADDED
      toast.success("Login successful!");
      
      // ✅ To this
      navigate("/dashboard", { replace: true }); // ✅
    } catch (err) {
      
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <h2>Login</h2>

        <form onSubmit={handleLogin}>
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

          {error && <p className="error-msg">{error}</p>}

          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="switch-link" onClick={() => navigate("/register")}>
          Don’t have an account? Register
        </p>
      </div>
    </div>
  );
}

export default Login;
