import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { createSprint } from "../api/sprintApi";
import "../styles/layout.css";
import "../styles/sprints.css";

function CreateSprint() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [sprintName, setSprintName] = useState("");
  const [sprintGoal, setSprintGoal] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [error, setError] = useState("");

  const handleCreate = async () => {
    if (!sprintName.trim()) {
      setError("Sprint name is required");
      return;
    }
    if (!startDate || !endDate) {
      setError("Start and end dates are required");
      return;
    }
    try {
      await createSprint(Number(projectId), {  // ✅ two arguments
        sprintName,
        sprintGoal,
        startDate,
        endDate,
        projectId: Number(projectId)
      });
      navigate(`/project/${projectId}/sprints`);
    } catch (err) {
      setError("Failed to create sprint");
      console.error(err);
    }
  };

  return (
    <div className="main-content">
      <h2 className="page-header">➕ Create Sprint</h2>
      <div className="create-sprint-form">
        <input
          type="text"
          placeholder="Sprint name"
          value={sprintName}
          onChange={(e) => setSprintName(e.target.value)}
        />
        <input
          type="text"
          placeholder="Sprint goal"
          value={sprintGoal}
          onChange={(e) => setSprintGoal(e.target.value)}
        />
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
        <button className="btn-primary" onClick={handleCreate}>
          Create
        </button>
        {error && <p className="error-msg">{error}</p>}
      </div>
    </div>
  );
}

export default CreateSprint;