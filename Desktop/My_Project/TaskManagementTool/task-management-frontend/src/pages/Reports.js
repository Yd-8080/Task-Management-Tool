import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getSprintsByProject } from "../api/sprintApi";
import { searchIssues } from "../api/issueApi";
import {
  getBurnDownReport,
  getVelocityReport,
  getSprintReport,
  getEpicReport,
  getCumulativeFlow,
  getWorkloadReport
} from "../api/reportApi";
import "../styles/layout.css";
import "../styles/reports.css";

import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
} from "chart.js";
import { Pie, Bar, Line, Doughnut } from "react-chartjs-2";

ChartJS.register(
  ArcElement, Tooltip, Legend,
  CategoryScale, LinearScale,
  BarElement, LineElement, PointElement
);

function Reports() {
  const { projectId } = useParams();
  const [sprints, setSprints] = useState([]);
  const [issues, setIssues] = useState([]);
  const [selectedSprint, setSelectedSprint] = useState(null);
  const [selectedEpic, setSelectedEpic] = useState(null);

  // Report data
  const [burndown, setBurndown] = useState(null);
  const [velocity, setVelocity] = useState(null);
  const [sprintReport, setSprintReport] = useState(null);
  const [epicReport, setEpicReport] = useState(null);
  const [cumulativeFlow, setCumulativeFlow] = useState(null);
  const [workload, setWorkload] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchBaseData();
  }, [projectId]);

  useEffect(() => {
    if (selectedSprint) fetchSprintReports(selectedSprint);
  }, [selectedSprint]);

  useEffect(() => {
    if (selectedEpic) fetchEpicReport(selectedEpic);
  }, [selectedEpic]);

  const fetchBaseData = async () => {
    try {
      setLoading(true);
      const [sprintData, issueData, velocityData] = await Promise.all([
        getSprintsByProject(projectId),
        searchIssues({ projectId }),
        getVelocityReport(projectId)
      ]);
      setSprints(sprintData);
      setIssues(issueData);
      setVelocity(velocityData);

      // auto select active sprint
      const active = sprintData.find(s => s.sprintState === "ACTIVE");
      const first = sprintData[0];
      const sprint = active || first;
      if (sprint) setSelectedSprint(sprint.id);

      // auto select first epic
      const epics = issueData.filter(i => i.issueType === "EPIC");
      if (epics.length > 0) setSelectedEpic(epics[0].id);

    } catch (err) {
      setError("Failed to load reports");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchSprintReports = async (sprintId) => {
    try {
      const [burndownData, sprintData, cfdData, workloadData] = await Promise.all([
        getBurnDownReport(sprintId),
        getSprintReport(sprintId),
        getCumulativeFlow(sprintId),
        getWorkloadReport(sprintId)
      ]);
      setBurndown(burndownData);
      setSprintReport(sprintData);
      setCumulativeFlow(cfdData);
      setWorkload(workloadData);
    } catch (err) {
      console.error("Sprint report error:", err);
    }
  };

  const fetchEpicReport = async (epicId) => {
    try {
      const data = await getEpicReport(epicId);
      setEpicReport(data);
    } catch (err) {
      console.error("Epic report error:", err);
    }
  };

  // Summary
  const totalIssues = issues.length;
  const completedIssues = issues.filter(i => i.issueStatus === "DONE").length;
  const inProgressIssues = issues.filter(i => i.issueStatus === "IN_PROGRESS").length;
  const activeSprints = sprints.filter(s => s.sprintState === "ACTIVE").length;
  const epics = issues.filter(i => i.issueType === "EPIC");

  // Issue distribution pie
  const issueDistData = {
    labels: ["Open", "Todo", "In Progress", "Done"],
    datasets: [{
      data: [
        issues.filter(i => i.issueStatus === "OPEN").length,
        issues.filter(i => i.issueStatus === "TODO").length,
        issues.filter(i => i.issueStatus === "IN_PROGRESS").length,
        issues.filter(i => i.issueStatus === "DONE").length,
      ],
      backgroundColor: ["#95a5a6", "#e74c3c", "#f1c40f", "#2ecc71"],
    }]
  };

  // Burndown chart
  const burndownData = burndown?.burnDown ? {
    labels: Object.keys(burndown.burnDown),
    datasets: [{
      label: "Remaining Issues",
      data: Object.values(burndown.burnDown),
      borderColor: "#e74c3c",
      backgroundColor: "rgba(231,76,60,0.1)",
      tension: 0.3,
      fill: true,
      pointRadius: 4,
    }]
  } : null;

  // Velocity chart
  const velocityData = velocity?.velocity &&
    Object.keys(velocity.velocity).length > 0 ? {
    labels: Object.keys(velocity.velocity),
    datasets: [{
      label: "Completed Issues",
      data: Object.values(velocity.velocity),
      backgroundColor: "#3498db",
      borderRadius: 6,
    }]
  } : null;

  // Sprint report doughnut
  const sprintDonutData = sprintReport ? {
    labels: ["Completed", "Not Completed"],
    datasets: [{
      data: [
        sprintReport.CompletedIssue,
        sprintReport["Not CompletedIssue"]
      ],
      backgroundColor: ["#2ecc71", "#e74c3c"],
    }]
  } : null;

  // Cumulative flow
  const cfdData = cumulativeFlow?.CumulativeFlow &&
    Object.keys(cumulativeFlow.CumulativeFlow).length > 0 ? {
    labels: Object.keys(cumulativeFlow.CumulativeFlow),
    datasets: [{
      label: "Issues",
      data: Object.values(cumulativeFlow.CumulativeFlow),
      backgroundColor: [
        "#3498db", "#2ecc71", "#f1c40f",
        "#e74c3c", "#9b59b6", "#1abc9c"
      ],
      borderRadius: 6,
    }]
  } : null;

  // Workload chart
  const workloadData = workload?.workload &&
    Object.keys(workload.workload).length > 0 ? {
    labels: Object.keys(workload.workload),
    datasets: [{
      label: "Issues Assigned",
      data: Object.values(workload.workload),
      backgroundColor: "#9b59b6",
      borderRadius: 6,
    }]
  } : null;

  if (loading) return <div className="loading">Loading reports...</div>;

  return (
    <div className="main-content">
      <h2 className="page-header">📊 Reports</h2>

      {error && <p className="error-msg">{error}</p>}

      {/* Summary Cards */}
      <div className="summary-bar">
        <div className="summary-card">
          <h4>Total Issues</h4>
          <p>{totalIssues}</p>
        </div>
        <div className="summary-card">
          <h4>Completed</h4>
          <p>{completedIssues}</p>
        </div>
        <div className="summary-card">
          <h4>In Progress</h4>
          <p>{inProgressIssues}</p>
        </div>
        <div className="summary-card">
          <h4>Active Sprints</h4>
          <p>{activeSprints}</p>
        </div>
        <div className="summary-card">
          <h4>Total Sprints</h4>
          <p>{sprints.length}</p>
        </div>
      </div>

      {/* Sprint Selector */}
      {sprints.length > 0 && (
        <div className="selector-row">
          <div className="selector">
            <label>Sprint Reports:</label>
            <select
              value={selectedSprint || ""}
              onChange={(e) => setSelectedSprint(e.target.value)}
            >
              {sprints.map(s => (
                <option key={s.id} value={s.id}>
                  {s.sprintName} ({s.sprintState})
                </option>
              ))}
            </select>
          </div>

          {/* Epic Selector */}
          {epics.length > 0 && (
            <div className="selector">
              <label>Epic Report:</label>
              <select
                value={selectedEpic || ""}
                onChange={(e) => setSelectedEpic(e.target.value)}
              >
                {epics.map(e => (
                  <option key={e.id} value={e.id}>
                    {e.issueTitle}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
      )}

      {/* Charts Grid */}
      <div className="reports-grid">

        {/* Issue Distribution */}
        <div className="report-card">
          <h3>📊 Issue Distribution</h3>
          <div className="chart-wrapper">
            <Pie data={issueDistData} />
          </div>
        </div>

        {/* Sprint Summary */}
        <div className="report-card">
          <h3>🏃 Sprint Summary</h3>
          {sprintReport ? (
            <>
              <div className="sprint-summary-stats">
                <div className="stat">
                  <span className="stat-label">Total</span>
                  <span className="stat-value">{sprintReport.TotalIssues}</span>
                </div>
                <div className="stat">
                  <span className="stat-label">Completed</span>
                  <span className="stat-value green">{sprintReport.CompletedIssue}</span>
                </div>
                <div className="stat">
                  <span className="stat-label">Remaining</span>
                  <span className="stat-value red">{sprintReport["Not CompletedIssue"]}</span>
                </div>
              </div>
              {sprintDonutData && (
                <div className="chart-wrapper small">
                  <Doughnut data={sprintDonutData} />
                </div>
              )}
            </>
          ) : (
            <p className="no-data">No sprint data available</p>
          )}
        </div>

        {/* Burndown Chart */}
        <div className="report-card wide">
          <h3>📉 Burndown Chart</h3>
          {burndownData ? (
            <div className="chart-wrapper">
              <Line data={burndownData} options={{
                scales: {
                  y: { beginAtZero: true, ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } },
                  x: { ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } }
                },
                plugins: { legend: { labels: { color: "#a0aec0" } } }
              }} />
            </div>
          ) : (
            <p className="no-data">No burndown data — sprint needs start date</p>
          )}
        </div>

        {/* Cumulative Flow */}
        <div className="report-card">
          <h3>🌊 Cumulative Flow</h3>
          {cfdData ? (
            <div className="chart-wrapper">
              <Bar data={cfdData} options={{
                scales: {
                  y: { beginAtZero: true, ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } },
                  x: { ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } }
                },
                plugins: { legend: { labels: { color: "#a0aec0" } } }
              }} />
            </div>
          ) : (
            <p className="no-data">No flow data available</p>
          )}
        </div>

        {/* Workload Distribution */}
        <div className="report-card">
          <h3>👥 Workload Distribution</h3>
          {workloadData ? (
            <div className="chart-wrapper">
              <Bar data={workloadData} options={{
                scales: {
                  y: { beginAtZero: true, ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } },
                  x: { ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } }
                },
                plugins: { legend: { labels: { color: "#a0aec0" } } }
              }} />
            </div>
          ) : (
            <p className="no-data">No workload data — assign issues to users</p>
          )}
        </div>

        {/* Velocity Chart */}
        <div className="report-card">
          <h3>⚡ Velocity</h3>
          {velocityData ? (
            <div className="chart-wrapper">
              <Bar data={velocityData} options={{
                scales: {
                  y: { beginAtZero: true, ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } },
                  x: { ticks: { color: "#a0aec0" }, grid: { color: "#2d3748" } }
                },
                plugins: { legend: { labels: { color: "#a0aec0" } } }
              }} />
            </div>
          ) : (
            <p className="no-data">No velocity data — complete some sprints first</p>
          )}
        </div>

        {/* Epic Progress */}
        {epicReport && (
          <div className="report-card">
            <h3>🟣 Epic Progress</h3>
            <div className="epic-stats">
              <div className="stat">
                <span className="stat-label">Total Stories</span>
                <span className="stat-value">{epicReport.TotalStories}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Completed</span>
                <span className="stat-value green">{epicReport["completed Stories"]}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Progress</span>
                <span className="stat-value blue">{epicReport["Progress Percent"]}%</span>
              </div>
            </div>
            <div className="progress-bar">
              <div
                className="progress-fill"
                style={{ width: `${epicReport["Progress Percent"]}%` }}
              />
            </div>
          </div>
        )}

      </div>
    </div>
  );
}

export default Reports;