import { Outlet, useNavigate, useParams } from "react-router-dom";
import "../styles/layout.css";

function ProjectLayout() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  // ✅ Get role and email from localStorage
  const role = localStorage.getItem("userRole");
  const email = localStorage.getItem("userEmail");
  const isManager = role === "ADMIN" || role === "MANAGER";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("projectId");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userEmail");
    navigate("/login", { replace: true });
  };

  return (
    <div className="layout">
      {/* Sidebar */}
      <aside className="sidebar">
        {/* Logo block */}
        <div className="sidebar-logo">
          <span>Trackify</span>
        </div>

        {/* Project title */}
        <h2>Project {projectId}</h2>

        {/* ✅ Role badge */}
        <div className="role-badge">
          <span className={`role-tag role-${role?.toLowerCase()}`}>
            {role === "ADMIN" && "👑 Admin"}
            {role === "MANAGER" && "🗂️ Manager"}
            {role === "DEVELOPER" && "💻 Developer"}
            {role === "TESTER" && "🧪 Tester"}
          </span>
          <span className="role-email">{email?.split("@")[0]}</span>
        </div>

        {/* Navigation */}
        <nav>
          <button onClick={() => navigate(`/project/${projectId}/backlog`)}>
            📋 Backlog
          </button>
          <button onClick={() => navigate(`/project/${projectId}/board`)}>
            🗂️ Board
          </button>

          {/* ✅ My Issues - visible to all but most useful for developers */}
          <button onClick={() => navigate(`/project/${projectId}/my-issues`)}>
            👤 My Issues
          </button>

          {/* ✅ Sprints and Reports - visible to all */}
          <button onClick={() => navigate(`/project/${projectId}/sprints`)}>
            🏃 Sprints
          </button>
          <button onClick={() => navigate(`/project/${projectId}/reports`)}>
            📊 Reports
          </button>
        </nav>

        {/* ✅ Back to Dashboard */}
        <button
          className="dashboard-btn"
          onClick={() => navigate("/dashboard")}
        >
          🏠 Dashboard
        </button>

        {/* Logout */}
        <button className="logout-btn" onClick={handleLogout}>
          🚪 Logout
        </button>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}

export default ProjectLayout;