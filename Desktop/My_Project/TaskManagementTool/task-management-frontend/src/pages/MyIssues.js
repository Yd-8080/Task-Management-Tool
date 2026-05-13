import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getIssuesByAssignee, searchIssues } from "../api/issueApi";
import "../styles/backlog.css";

function MyIssues() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const email = localStorage.getItem("userEmail");
  const role = localStorage.getItem("userRole");

  const [issues, setIssues] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");

  useEffect(() => {
    fetchMyIssues();
  }, [projectId]);

  const fetchMyIssues = async () => {
    try {
      setLoading(true);

      // ✅ fetch all project issues then filter by assignee
      // This ensures we get the numeric ID field
      const data = await searchIssues({ projectId, assignee: email });
      const myIssues = data.filter(
        (i) => i.assigneeEmail === email
      );
      setIssues(myIssues);
    } catch (err) {
      // ✅ fallback: try getByAssigneeEmail
      try {
        const data = await getIssuesByAssignee(email);
        const projectIssues = data.filter(
          (i) => String(i.projectId) === String(projectId)
        );
        setIssues(projectIssues);
      } catch (err2) {
        console.error("Failed to fetch my issues:", err2);
        setIssues([]);
      }
    } finally {
      setLoading(false);
    }
  };

  // ✅ get the correct ID to navigate with
  const getIssueId = (issue) => {
    return issue.id || issue.issueId || issue.issueKey;
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "DONE": return "#27ae60";
      case "IN_PROGRESS": return "#3498db";
      case "IN_REVIEW": return "#f39c12";
      case "TODO": return "#e74c3c";
      case "BLOCKED": return "#8e44ad";
      default: return "#95a5a6";
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case "HIGH": return "#e74c3c";
      case "CRITICAL": return "#8e44ad";
      case "MEDIUM": return "#f39c12";
      case "LOW": return "#27ae60";
      default: return "#95a5a6";
    }
  };

  const counts = {
    ALL: issues.length,
    TODO: issues.filter(i => i.issueStatus === "TODO").length,
    IN_PROGRESS: issues.filter(i => i.issueStatus === "IN_PROGRESS").length,
    IN_REVIEW: issues.filter(i => i.issueStatus === "IN_REVIEW").length,
    DONE: issues.filter(i => i.issueStatus === "DONE").length,
    BLOCKED: issues.filter(i => i.issueStatus === "BLOCKED").length,
  };

  const filteredIssues = filter === "ALL"
    ? issues
    : issues.filter((i) => i.issueStatus === filter);

  if (loading) return <div className="loading">Loading your issues...</div>;

  return (
    <div className="main-content">

      {/* Header */}
      <div className="backlog-header">
        <div>
          <h2 className="page-header">👤 My Issues</h2>
          <p style={{ fontSize: "13px", color: "#718096", marginTop: "4px" }}>
            Assigned to: <strong style={{ color: "#63b3ed" }}>{email}</strong>
            &nbsp;•&nbsp;
            <span style={{ color: "#a0aec0" }}>{role}</span>
          </p>
        </div>
        <div style={{ fontSize: "13px", color: "#718096" }}>
          {issues.filter(i => i.issueStatus !== "DONE").length} open
          &nbsp;/&nbsp;
          {issues.length} total
        </div>
      </div>

      {/* Filter tabs */}
      <div className="filter-tabs">
        {["ALL", "TODO", "IN_PROGRESS", "IN_REVIEW", "DONE", "BLOCKED"].map((tab) => (
          <button
            key={tab}
            className={`filter-tab ${filter === tab ? "active" : ""}`}
            onClick={() => setFilter(tab)}
          >
            {tab.replace(/_/g, " ")}
            <span className="tab-count">{counts[tab]}</span>
          </button>
        ))}
      </div>

      {/* Issues list */}
      <div className="issues-list" style={{ marginTop: "16px" }}>
        {filteredIssues.length === 0 ? (
          <div className="issue-card empty-state">
            <p style={{ color: "#718096" }}>
              {filter === "ALL"
                ? "No issues assigned to you in this project yet."
                : `No ${filter.replace(/_/g, " ")} issues.`}
            </p>
          </div>
        ) : (
          filteredIssues.map((issue) => (
            <div
              key={getIssueId(issue)}
              className="issue-card"
              onClick={() => {
                const id = getIssueId(issue);
                if (id) navigate(`/project/${projectId}/issue/${id}`);
              }}
              style={{ cursor: "pointer" }}
            >
              <div className="issue-left">
                {/* ✅ show issueKey if id is missing */}
                <span className="issue-id">
                  {issue.issueKey || `#${issue.id}` || "—"}
                </span>
                <span className="issue-title">{issue.issueTitle}</span>
              </div>

              <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
                {/* Priority */}
                <span style={{
                  fontSize: "12px",
                  color: getPriorityColor(issue.priyority),
                  fontWeight: 600
                }}>
                  ● {issue.priyority || "—"}
                </span>

                {/* Status badge */}
                <span style={{
                  fontSize: "12px",
                  padding: "3px 10px",
                  borderRadius: "12px",
                  border: `1px solid ${getStatusColor(issue.issueStatus)}`,
                  color: getStatusColor(issue.issueStatus),
                  fontWeight: 600,
                  background: "transparent"
                }}>
                  {issue.issueStatus}
                </span>

                {/* Sprint info */}
                {issue.sprintId && (
                  <span style={{
                    fontSize: "11px",
                    color: "#4a5568",
                    background: "#1a2332",
                    padding: "2px 8px",
                    borderRadius: "4px"
                  }}>
                    Sprint {issue.sprintId}
                  </span>
                )}

                {/* Issue type */}
                <span style={{ fontSize: "12px", color: "#4a5568" }}>
                  {issue.issueType}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default MyIssues;