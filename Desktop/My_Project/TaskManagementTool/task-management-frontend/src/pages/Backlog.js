import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getBacklog, addIssueToSprint } from "../api/backlogApi";
import { createIssue, updateIssueStatus, updateIssue } from "../api/issueApi"; // ✅ added updateIssue
import { getSprintsByProject } from "../api/sprintApi";
import "../styles/layout.css";
import "../styles/backlog.css";

function Backlog() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [issues, setIssues] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [title, setTitle] = useState("");
  const [issueType, setIssueType] = useState("STORY");
  const [priyority, setPriyority] = useState("MEDIUM");
  const [showForm, setShowForm] = useState(false);

  // ✅ Assign state
  const [assigningId, setAssigningId] = useState(null);
  const [assignEmail, setAssignEmail] = useState("");

  // ✅ Get role from localStorage
  const role = localStorage.getItem("userRole");
  const isManager = role === "ADMIN" || role === "MANAGER";

  useEffect(() => {
    fetchAll();
  }, [projectId]);

  const fetchAll = async () => {
    try {
      setLoading(true);
      const [backlogData, sprintData] = await Promise.all([
        getBacklog(projectId),
        getSprintsByProject(projectId)
      ]);
      setIssues(backlogData);
      setSprints(sprintData);
    } catch (err) {
      setError("Failed to load backlog");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateIssue = async () => {
    if (!title.trim()) return;
    try {
      await createIssue({
        issueTitle: title,
        issueType,
        priyority,
        projectId: Number(projectId),
        issueStatus: "TODO"
      });
      setTitle("");
      setShowForm(false);
      fetchAll();
    } catch (err) {
      setError("Failed to create issue");
      console.error(err);
    }
  };

  const handleAddToSprint = async (issueId, sprintId) => {
    try {
      await addIssueToSprint(issueId, sprintId);
      fetchAll();
    } catch (err) {
      setError("Failed to add to sprint");
      console.error(err);
    }
  };

  const handleUpdateStatus = async (issueId, newStatus) => {
    try {
      await updateIssueStatus(issueId, newStatus);
      fetchAll();
    } catch (err) {
      setError("Failed to update issue status");
      console.error(err);
    }
  };

  // ✅ Assign handler
  const handleAssign = async (issueId) => {
    if (!assignEmail.trim()) return;
    try {
      await updateIssue(issueId, { assigneeEmail: assignEmail });
      setAssigningId(null);
      setAssignEmail("");
      fetchAll();
    } catch (err) {
      setError("Failed to assign issue");
      console.error(err);
    }
  };

  const getPriorityClass = (priyority) => {
    switch (priyority) {
      case "LOW": return "tag low";
      case "MEDIUM": return "tag medium";
      case "HIGH": return "tag high";
      case "CRITICAL": return "tag critical";
      default: return "tag";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "TODO": return "tag todo";
      case "IN_PROGRESS": return "tag inprogress";
      case "IN_REVIEW": return "tag review";
      case "DONE": return "tag done";
      case "CLOSED": return "tag closed";
      case "BLOCKED": return "tag blocked";
      default: return "tag";
    }
  };

  if (loading) return <div className="loading">Loading backlog...</div>;

  return (
    <div className="main-content">
      <div className="backlog-header">
        <h2 className="page-header">📋 Backlog</h2>
        {/* ✅ Only ADMIN/MANAGER can create issues */}
        {isManager && (
          <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
            + Create Issue
          </button>
        )}
      </div>

      {error && <p className="error-msg">{error}</p>}

      {/* ✅ Create form only for managers */}
      {showForm && isManager && (
        <div className="issue-card create-form">
          <h3 className="title">New Issue</h3>
          <input
            placeholder="Issue title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <div className="form-row">
            <select value={issueType} onChange={(e) => setIssueType(e.target.value)}>
              <option value="TASK">📝 Task</option>
              <option value="STORY">📖 Story</option>
              <option value="BUG">🐞 Bug</option>
              <option value="EPIC">🟣 Epic</option>
              <option value="SUBTASK">🔵 Subtask</option>
            </select>
            <select value={priyority} onChange={(e) => setPriyority(e.target.value)}>
              <option value="LOW">🟢 Low</option>
              <option value="MEDIUM">🟡 Medium</option>
              <option value="HIGH">🔴 High</option>
              <option value="CRITICAL">⚠️ Critical</option>
            </select>
          </div>
          <div className="form-actions">
            <button className="btn-primary" onClick={handleCreateIssue}>
              Create
            </button>
            <button className="btn-secondary" onClick={() => setShowForm(false)}>
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="issues-list">
        {issues.length === 0 ? (
          <div className="issue-card empty-state">
            <p>No issues in backlog yet.</p>
            {isManager && (
              <button className="btn-primary" onClick={() => setShowForm(true)}>
                Create your first issue
              </button>
            )}
          </div>
        ) : (
          issues.map((issue) => (
            <div
              key={issue.id}
              className="issue-card"
              onClick={() => navigate(`/project/${projectId}/issue/${issue.id}`)}
              style={{ cursor: "pointer" }}
            >
              <div className="issue-left">
                <span className="issue-id">#{issue.id}</span>
                <span className="issue-title">{issue.issueTitle}</span>
              </div>

              <div className="tags">
                <span className={getPriorityClass(issue.priyority)}>
                  {issue.priyority}
                </span>
                <span className={getStatusClass(issue.issueStatus)}>
                  {issue.issueStatus}
                </span>
              </div>

              {/* Status dropdown */}
              <select
                className="status-select"
                value={issue.issueStatus}
                onClick={(e) => e.stopPropagation()}
                onChange={(e) => {
                  e.stopPropagation();
                  handleUpdateStatus(issue.id, e.target.value);
                }}
              >
                <option value="OPEN">OPEN</option>
                <option value="TODO">TODO</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="IN_REVIEW">IN_REVIEW</option>
                <option value="DONE">DONE</option>
                <option value="CLOSED">CLOSED</option>
                <option value="REOPENED">REOPENED</option>
                <option value="BLOCKED">BLOCKED</option>
                <option value="IN_PROCESS">IN_PROCESS</option>
                <option value="DEPLOYMENT">DEPLOYMENT</option>
                <option value="BLOCKS">BLOCKS</option>
              </select>

              {/* Sprint dropdown */}
              {sprints.filter(s => s.sprintState !== "COMPLETED").length > 0 && (
                <select
                  value={issue.sprintId || ""}
                  onClick={(e) => e.stopPropagation()}
                  onChange={(e) => {
                    e.stopPropagation();
                    if (e.target.value) {
                      handleAddToSprint(issue.id, e.target.value);
                    }
                  }}
                >
                  <option value="">+ Add to Sprint</option>
                  {sprints
                    .filter(s => s.sprintState !== "COMPLETED")
                    .map((sprint) => (
                      <option key={sprint.id} value={sprint.id}>
                        {sprint.sprintName}
                      </option>
                    ))}
                </select>
              )}

              {/* ✅ Assign button - only for ADMIN/MANAGER */}
              {isManager && (
                <div onClick={(e) => e.stopPropagation()}>
                  {assigningId === issue.id ? (
                    <div className="assign-input-row">
                      <input
                        className="assign-input"
                        placeholder="Enter email to assign"
                        value={assignEmail}
                        onChange={(e) => setAssignEmail(e.target.value)}
                        onKeyPress={(e) => e.key === "Enter" && handleAssign(issue.id)}
                        autoFocus
                      />
                      <button
                        className="assign-confirm"
                        onClick={() => handleAssign(issue.id)}
                      >✅</button>
                      <button
                        className="assign-cancel"
                        onClick={() => { setAssigningId(null); setAssignEmail(""); }}
                      >❌</button>
                    </div>
                  ) : (
                    <button
                      className="assign-btn"
                      onClick={() => {
                        setAssigningId(issue.id);
                        setAssignEmail(issue.assigneeEmail || "");
                      }}
                    >
                      {issue.assigneeEmail
                        ? `👤 ${issue.assigneeEmail.split("@")[0]}`
                        : "➕ Assign"}
                    </button>
                  )}
                </div>
              )}

              {/* ✅ Show assignee read-only for DEVELOPER/TESTER */}
              {!isManager && issue.assigneeEmail && (
                <span className="assignee-badge" onClick={(e) => e.stopPropagation()}>
                  👤 {issue.assigneeEmail.split("@")[0]}
                </span>
              )}

              {issue.sprintId && (
                <p
                  className="assigned-sprint"
                  onClick={(e) => e.stopPropagation()}
                >
                  Sprint: {sprints.find((s) => s.id === issue.sprintId)?.sprintName || "Unknown"}
                </p>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default Backlog;