import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getSprintsByProject, startSprint, endSprint } from "../api/sprintApi";
import { searchIssues } from "../api/issueApi";
import "../styles/layout.css";
import "../styles/sprints.css";

function Sprints() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [sprints, setSprints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchSprints = async () => {
    try {
      setLoading(true);
      const sprintData = await getSprintsByProject(projectId);
      const issueData = await searchIssues({ projectId });

      // ✅ attach issues to each sprint
      const sprintsWithIssues = sprintData.map((s) => ({
        ...s,
        issues: issueData.filter((i) => i.sprintId === s.id),
      }));

      setSprints(sprintsWithIssues);
    } catch (err) {
      setError("Failed to load sprints");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSprints();
  }, [projectId]);

  const handleStartSprint = async (id) => {
    try {
      await startSprint(id);
      fetchSprints();
    } catch (err) {
      setError("Failed to start sprint");
    }
  };

  const handleEndSprint = async (id) => {
    try {
      await endSprint(id);
      fetchSprints();
    } catch (err) {
      setError("Failed to end sprint");
    }
  };

  const calculateProgress = (issues) => {
    if (!issues || issues.length === 0) return 0;
    const done = issues.filter((i) => i.issueStatus === "DONE").length;
    return Math.round((done / issues.length) * 100);
  };

  // ✅ summary counts
  const plannedCount = sprints.filter((s) => s.sprintState === "PLANNED").length;
  const activeCount = sprints.filter((s) => s.sprintState === "ACTIVE").length;
  const completedCount = sprints.filter((s) => s.sprintState === "COMPLETED").length;

  if (loading) return <div className="loading">Loading sprints...</div>;

  return (
    <div className="main-content">
      <div className="page-header-row">
        <h2 className="page-header">🏃‍♂️ Sprint Management</h2>
        <button
          className="btn-create"
          onClick={() => navigate(`/project/${projectId}/sprints/create`)}
        >
          ➕ Create Sprint
        </button>
      </div>

      {error && <p className="error-msg">{error}</p>}

      {/* ✅ summary bar */}
      <div className="summary-bar">
        <div className="summary-card">
          <h4>Planned</h4>
          <p>{plannedCount}</p>
        </div>
        <div className="summary-card">
          <h4>Active</h4>
          <p>{activeCount}</p>
        </div>
        <div className="summary-card">
          <h4>Completed</h4>
          <p>{completedCount}</p>
        </div>
      </div>

      <div className="sprints-list">
        {sprints.length === 0 ? (
          <div className="sprint-card empty-state">
            <p>No sprints created yet.</p>
          </div>
        ) : (
          sprints.map((sprint) => {
            const progress = calculateProgress(sprint.issues);
            return (
              <div key={sprint.id} className="sprint-card">
                <div className="sprint-header">
                  <h3 className="sprint-title">{sprint.sprintName}</h3>
                  <span className={`badge ${sprint.sprintState.toLowerCase()}`}>
                    {sprint.sprintState}
                  </span>
                </div>
                <p className="sprint-goal">🎯 {sprint.sprintGoal}</p>
                <p className="sprint-dates">
                  {sprint.startDate} → {sprint.endDate}
                </p>

                <div className="progress-bar">
                  <div
                    className="progress-fill"
                    style={{ width: `${progress}%` }}
                  ></div>
                </div>
                <p className="progress-text">{progress}% complete</p>

                {/* ✅ show issues */}
                <div className="sprint-issues">
                  <h4>Issues</h4>
                  {sprint.issues && sprint.issues.length > 0 ? (
                    <ul>
                      {sprint.issues.map((i) => (
                        <li key={i.issueKey}>
                          {i.issueTitle} —{" "}
                          <span className={`badge ${i.issueStatus.toLowerCase()}`}>
                            {i.issueStatus}
                          </span>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p>No issues assigned</p>
                  )}
                </div>

                <div className="sprint-actions">
                  {sprint.sprintState === "PLANNED" && (
                    <button
                      className="btn-primary"
                      onClick={() => handleStartSprint(sprint.id)}
                    >
                      Start Sprint
                    </button>
                  )}
                  {sprint.sprintState === "ACTIVE" && (
                    <button
                      className="btn-danger"
                      onClick={() => handleEndSprint(sprint.id)}
                    >
                      End Sprint
                    </button>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

export default Sprints;
