import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { searchIssuesByProject, updateIssueStatus } from "../api/issueApi";
import { addIssueToSprint } from "../api/backlogApi";
import { getSprintsByProject } from "../api/sprintApi";
import "../styles/layout.css";
import "../styles/board.css";

function Board() {
  const { projectId } = useParams();
  const [issues, setIssues] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [boardType, setBoardType] = useState("KANBAN"); // default KANBAN
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, [projectId]);

  const fetchData = async () => {
    try {
      const [issueData, sprintData] = await Promise.all([
        searchIssuesByProject(projectId),
        getSprintsByProject(projectId),
        // ✅ REMOVED getProjectById - doesn't exist in backend
      ]);
      setIssues(issueData);
      setSprints(sprintData);
    } catch (err) {
      console.error("Failed to fetch board data:", err);
    } finally {
      setLoading(false);
    }
  };

  const onDragEnd = async (result) => {
    const { destination, source, draggableId } = result;
    if (!destination) return;

    if (boardType === "KANBAN") {
      if (destination.droppableId !== source.droppableId) {
        try {
          await updateIssueStatus(draggableId, destination.droppableId);
          setIssues((prev) =>
            prev.map((issue) =>
              issue.issueKey === draggableId
                ? { ...issue, issueStatus: destination.droppableId }
                : issue
            )
          );
        } catch (err) {
          console.error("Failed to update issue status:", err);
        }
      }
    } else {
      const sprintId = destination.droppableId === "unassigned"
        ? null
        : destination.droppableId;
      try {
        await addIssueToSprint(draggableId, sprintId);
        fetchData();
      } catch (err) {
        console.error("Failed to assign sprint:", err);
      }
    }
  };

  const statusColumns = ["OPEN", "TODO", "IN_PROGRESS", "DONE"];

  if (loading) return <div className="loading">Loading board...</div>;

  return (
    <div className="main-content">
      <div className="board-header">
        <h2 className="page-header">📋 Board</h2>
        {/* Toggle between KANBAN and SCRUM */}
        <div className="view-toggle">
          <button
            className={boardType === "KANBAN" ? "btn-primary" : "btn-secondary"}
            onClick={() => setBoardType("KANBAN")}
          >
            🗂 Kanban
          </button>
          <button
            className={boardType === "SCRUM" ? "btn-primary" : "btn-secondary"}
            onClick={() => setBoardType("SCRUM")}
          >
            🏃 Scrum
          </button>
        </div>
      </div>

      <DragDropContext onDragEnd={onDragEnd}>
        <div className="board-grid">
          {boardType === "KANBAN" ? (
            // ✅ KANBAN - group by status
            statusColumns.map((col) => (
              <Droppable key={col} droppableId={col}>
                {(provided) => (
                  <div
                    className="board-column"
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                  >
                    <h3>
                      {col.replace("_", " ")}
                      <span className="col-count">
                        {issues.filter(i => i.issueStatus === col).length}
                      </span>
                    </h3>
                    {issues
                      .filter((i) => i.issueStatus === col)
                      .map((issue, index) => (
                        <Draggable
                          key={issue.issueKey}
                          draggableId={issue.issueKey.toString()}
                          index={index}
                        >
                          {(provided) => (
                            <div
                              className="issue-card"
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                            >
                              <h4>{issue.issueTitle}</h4>
                              <p>{issue.issueDescription || ""}</p>
                              <span className={`badge ${issue.priyority?.toLowerCase()}`}>
                                {issue.priyority}
                              </span>
                            </div>
                          )}
                        </Draggable>
                      ))}
                    {issues.filter(i => i.issueStatus === col).length === 0 && (
                      <p className="empty-msg">No issues</p>
                    )}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            ))
          ) : (
            // ✅ SCRUM - group by sprint
            <>
              <Droppable droppableId="unassigned">
                {(provided) => (
                  <div
                    className="board-column"
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                  >
                    <h3>
                      Backlog
                      <span className="col-count">
                        {issues.filter(i => !i.sprintId).length}
                      </span>
                    </h3>
                    {issues.filter(i => !i.sprintId).map((issue, index) => (
                      <Draggable
                        key={issue.issueKey}
                        draggableId={issue.issueKey.toString()}
                        index={index}
                      >
                        {(provided) => (
                          <div
                            className="issue-card"
                            ref={provided.innerRef}
                            {...provided.draggableProps}
                            {...provided.dragHandleProps}
                          >
                            <h4>{issue.issueTitle}</h4>
                            <span className={`badge ${issue.priyority?.toLowerCase()}`}>
                              {issue.priyority}
                            </span>
                          </div>
                        )}
                      </Draggable>
                    ))}
                    {issues.filter(i => !i.sprintId).length === 0 && (
                      <p className="empty-msg">No issues</p>
                    )}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>

              {sprints.map((sprint) => (
                <Droppable key={sprint.id} droppableId={String(sprint.id)}>
                  {(provided) => (
                    <div
                      className="board-column"
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                    >
                      <h3>
                        {sprint.sprintName}
                        <span className="col-count">
                          {issues.filter(i => i.sprintId === sprint.id).length}
                        </span>
                      </h3>
                      {issues.filter(i => i.sprintId === sprint.id).map((issue, index) => (
                        <Draggable
                          key={issue.issueKey}
                          draggableId={issue.issueKey.toString()}
                          index={index}
                        >
                          {(provided) => (
                            <div
                              className="issue-card"
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                            >
                              <h4>{issue.issueTitle}</h4>
                              <span className={`badge ${issue.priyority?.toLowerCase()}`}>
                                {issue.priyority}
                              </span>
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {issues.filter(i => i.sprintId === sprint.id).length === 0 && (
                        <p className="empty-msg">No issues</p>
                      )}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              ))}
            </>
          )}
        </div>
      </DragDropContext>
    </div>
  );
}

export default Board;