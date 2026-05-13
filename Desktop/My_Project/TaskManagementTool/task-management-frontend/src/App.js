import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from "react-router-dom";
import { useEffect } from "react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ProjectLayout from "./pages/ProjectLayout";
import Backlog from "./pages/Backlog";
import Board from "./pages/Board";
import Sprints from "./pages/Sprints";
import Reports from "./pages/Reports";
import CreateSprint from "./pages/CreateSprint";
import Register from "./pages/Register";
import IssueDetail from './pages/IssueDetail';
import MyIssues from './pages/MyIssues'; // ✅ ADDED
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const NavigationGuard = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (token && (location.pathname === "/login" ||
                  location.pathname === "/register")) {
      navigate("/dashboard", { replace: true });
    }
    if (!token && location.pathname !== "/login" &&
        location.pathname !== "/register") {
      navigate("/login", { replace: true });
    }
  }, [location.pathname, token]);

  return null;
};

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" replace />;
};

function App() {
  return (
    <BrowserRouter>
      <NavigationGuard />
      <ToastContainer position="top-right" autoClose={3000} />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/dashboard" element={
          <PrivateRoute><Dashboard /></PrivateRoute>
        } />

        <Route path="/project/:projectId" element={
          <PrivateRoute><ProjectLayout /></PrivateRoute>
        }>
          <Route path="backlog" element={<Backlog />} />
          <Route path="board" element={<Board />} />
          <Route path="sprints" element={<Sprints />} />
          <Route path="reports" element={<Reports />} />
          <Route path="issue/:issueId" element={<IssueDetail />} />
          <Route path="sprints/create" element={<CreateSprint />} />
          <Route path="my-issues" element={<MyIssues />} /> {/* ✅ ADDED */}
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;