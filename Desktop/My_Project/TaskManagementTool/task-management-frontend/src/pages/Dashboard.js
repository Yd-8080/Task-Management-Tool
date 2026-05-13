import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProject, getMyProjects, addMember } from "../api/projectApi";
import { getIssuesByAssignee } from "../api/issueApi"; // ✅ ADDED
import "../styles/dashboard.css";

function Dashboard() {
  const navigate = useNavigate();
  const email = localStorage.getItem("userEmail");
  const role = localStorage.getItem("userRole"); // ✅ ADDED
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [showJoinForm, setShowJoinForm] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [joinProjectId, setJoinProjectId] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [myIssuesCount, setMyIssuesCount] = useState(0); // ✅ ADDED

  useEffect(() => {
    fetchProjects();
    fetchMyIssuesCount(); // ✅ ADDED
  }, []);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      const data = await getMyProjects(email);
      setProjects(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // ✅ ADDED - fetch count of open assigned issues
  const fetchMyIssuesCount = async () => {
    try {
      const data = await getIssuesByAssignee(email);
      const openCount = data.filter(i => i.issueStatus !== "DONE").length;
      setMyIssuesCount(openCount);
    } catch (err) {
      console.error("Failed to fetch assigned issues:", err);
    }
  };

  const handleCreate = async () => {
    if (!name.trim()) {
      setError("Project name is required");
      return;
    }
    try {
      await createProject({ name, description }, email);
      setName("");
      setDescription("");
      setShowForm(false);
      setSuccess("Project created!");
      setTimeout(() => setSuccess(""), 3000);
      fetchProjects();
    } catch (err) {
      setError("Failed to create project");
    }
  };

  const handleJoin = async () => {
    if (!joinProjectId.trim()) {
      setError("Please enter a Project ID");
      return;
    }
    try {
      await addMember(joinProjectId, email);
      setJoinProjectId("");
      setShowJoinForm(false);
      setSuccess("Joined project successfully!");
      setTimeout(() => setSuccess(""), 3000);
      fetchProjects();
    } catch (err) {
      setError("Failed to join project — check the Project ID");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userRole"); // ✅ ADDED
    localStorage.removeItem("projectId");
    navigate("/login");
  };

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>🔷 Task Management Tool</h1>
        <div className="header-right">
          {/* ✅ Show role badge in header */}
          <span className={`header-role-badge role-${role?.toLowerCase()}`}>
            {role === "ADMIN" && "👑 Admin"}
            {role === "MANAGER" && "🗂️ Manager"}
            {role === "DEVELOPER" && "💻 Developer"}
            {role === "TESTER" && "🧪 Tester"}
          </span>
          <span className="user-email">👤 {email}</span>
          <button className="btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <div className="dashboard-body">

        {/* ✅ My work banner - shows open assigned issues */}
        {myIssuesCount > 0 && (
          <div className="my-work-banner">
            <div>
              <span>👤 You have </span>
              <strong>{myIssuesCount}</strong>
              <span> open issue{myIssuesCount !== 1 ? "s" : ""} assigned to you</span>
            </div>
            <span style={{ fontSize: "12px", color: "#718096" }}>
              Open a project → click "My Issues" to view them
            </span>
          </div>
        )}

        {/* Action buttons */}
        <div className="dashboard-actions">
          <div>
            <h2>My Projects</h2>
            <p>Click a project to open it</p>
          </div>
          <div className="action-btns">
            <button
              className="btn-secondary"
              onClick={() => { setShowJoinForm(!showJoinForm); setShowForm(false); }}
            >
              🔗 Join Project
            </button>
            <button
              className="btn-primary"
              onClick={() => { setShowForm(!showForm); setShowJoinForm(false); }}
            >
              + New Project
            </button>
          </div>
        </div>

        {error && <p className="error-msg">{error}</p>}
        {success && <p className="success-msg">{success}</p>}

        {/* Create Project Form */}
        {showForm && (
          <div className="form-card">
            <h3>✨ Create New Project</h3>
            <input
              placeholder="Project name (e.g. Food Delivery App)"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            <input
              placeholder="Description (optional)"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            <div className="form-actions">
              <button className="btn-primary" onClick={handleCreate}>
                Create
              </button>
              <button className="btn-secondary" onClick={() => setShowForm(false)}>
                Cancel
              </button>
            </div>
          </div>
        )}

        {/* Join Project Form */}
        {showJoinForm && (
          <div className="form-card">
            <h3>🔗 Join Existing Project</h3>
            <p className="form-hint">
              Ask your team member for the Project ID
            </p>
            <input
              type="number"
              placeholder="Enter Project ID"
              value={joinProjectId}
              onChange={(e) => setJoinProjectId(e.target.value)}
            />
            <div className="form-actions">
              <button className="btn-primary" onClick={handleJoin}>
                Join
              </button>
              <button className="btn-secondary" onClick={() => setShowJoinForm(false)}>
                Cancel
              </button>
            </div>
          </div>
        )}

        {/* Projects Grid */}
        {loading ? (
          <div className="loading">Loading projects...</div>
        ) : projects.length === 0 ? (
          <div className="empty-state">
            <h3>No projects yet!</h3>
            <p>Create your first project or join an existing one</p>
            <button className="btn-primary" onClick={() => setShowForm(true)}>
              + Create Project
            </button>
          </div>
        ) : (
          <div className="projects-grid">
            {projects.map((project) => (
              <div
                key={project.id}
                className="project-card"
                onClick={() => navigate(`/project/${project.id}/backlog`, { replace: true })}
              >
                <div className="project-id-badge">ID: {project.id}</div>
                <h3>{project.name}</h3>
                <p>{project.description || "No description"}</p>
                <div className="project-footer">
                  <span>👥 {project.memberEmails?.length || 0} members</span>
                  <span className="owner-badge">
                    {project.ownerEmail === email ? "👑 Owner" : "👤 Member"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}

      </div>
    </div>
  );
}

export default Dashboard;