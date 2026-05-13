import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getIssueById, updateIssueStatus, addComment, updateIssue } from "../api/issueApi";
import { uploadAttachment, getAttachmentsByIssue, deleteAttachment } from '../api/attachmentApi';
import "../styles/layout.css";
import "../styles/issueDetail.css";
import { toast } from "react-toastify";

function IssueDetail() {
  const { projectId, issueId } = useParams();
  const navigate = useNavigate();

  const [issue, setIssue] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editing, setEditing] = useState(false);

  const [attachments, setAttachments] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");
  const [uploadSuccess, setUploadSuccess] = useState("");
  const [uploaderEmail, setUploaderEmail] = useState("");

  const [commentBody, setCommentBody] = useState("");
  const [commentAuthor, setCommentAuthor] = useState("");
  const [commentError, setCommentError] = useState("");
  const [commentSuccess, setCommentSuccess] = useState("");

  const [editTitle, setEditTitle] = useState("");
  const [editDescription, setEditDescription] = useState("");

  // ✅ Assignee edit state
  const [editingAssignee, setEditingAssignee] = useState(false);
  const [newAssignee, setNewAssignee] = useState("");

  useEffect(() => {
    fetchIssue();
    fetchAttachments();
  }, [issueId]);

  const fetchIssue = async () => {
    try {
      setLoading(true);
      const data = await getIssueById(issueId);
      setIssue(data);
      setEditTitle(data.issueTitle || "");
      setEditDescription(data.issueDescription || "");
    } catch (err) {
      setError("Failed to load issue");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAttachments = async () => {
    try {
      const data = await getAttachmentsByIssue(issueId);
      setAttachments(data);
    } catch (err) {
      console.error("Failed to fetch attachments:", err);
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      await updateIssueStatus(issueId, newStatus);
      setIssue(prev => ({ ...prev, issueStatus: newStatus }));
    } catch (err) {
      setError("Failed to update status");
    }
  };

  const handleEditSave = async () => {
    if (!editTitle.trim()) return;
    try {
      const updated = await updateIssue(issueId, {
        issueTitle: editTitle,
        issueDescription: editDescription,
      });
      setIssue(prev => ({
        ...prev,
        issueTitle: updated.issueTitle,
        issueDescription: updated.issueDescription,
      }));
      setEditing(false);
      toast.success("Issue updated!");
    } catch (err) {
      toast.error("Failed to update issue");
      console.error(err);
    }
  };

  // ✅ Assignee update handler
  const handleAssigneeUpdate = async () => {
    try {
      await updateIssue(issueId, { assigneeEmail: newAssignee });
      setIssue(prev => ({ ...prev, assigneeEmail: newAssignee }));
      setEditingAssignee(false);
      setNewAssignee("");
      toast.success("Assignee updated!");
    } catch (err) {
      toast.error("Failed to update assignee");
      console.error(err);
    }
  };

  const handleAddComment = async () => {
    if (!commentBody.trim()) return;
    try {
      await addComment(issueId, commentBody, commentAuthor || "anonymous@user.com");
      setCommentBody("");
      setCommentAuthor("");
      setCommentSuccess("Comment added!");
      setTimeout(() => setCommentSuccess(""), 3000);
    } catch (err) {
      setCommentError("Failed to add comment");
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      setUploading(true);
      setUploadError("");
      await uploadAttachment(issueId, file, uploaderEmail || "anonymous@user.com");
      setUploadSuccess("File uploaded successfully!");
      setTimeout(() => setUploadSuccess(""), 3000);
      fetchAttachments();
    } catch (err) {
      setUploadError("Upload failed. Max 5MB. Allowed: PNG, JPG, PDF, TXT");
      console.error(err);
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteAttachment = async (id) => {
    try {
      await deleteAttachment(id);
      fetchAttachments();
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return "—";
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  const getFileIcon = (contentType) => {
    if (!contentType) return "📄";
    if (contentType.includes("image")) return "🖼️";
    if (contentType.includes("pdf")) return "📕";
    if (contentType.includes("text")) return "📝";
    return "📄";
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case "HIGH": return "#e74c3c";
      case "MEDIUM": return "#f39c12";
      case "LOW": return "#27ae60";
      case "CRITICAL": return "#8e44ad";
      default: return "#95a5a6";
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "DONE": return "#27ae60";
      case "IN_PROGRESS": return "#3498db";
      case "TODO": return "#e74c3c";
      case "OPEN": return "#95a5a6";
      default: return "#95a5a6";
    }
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case "EPIC": return "🟣";
      case "STORY": return "🟢";
      case "SUBTASK": return "🔵";
      case "BUG": return "🐞";
      case "TASK": return "📝";
      default: return "⚪";
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleDateString("en-IN", {
      day: "2-digit", month: "short", year: "numeric"
    });
  };

  if (loading) return <div className="loading">Loading issue...</div>;
  if (error) return <div className="error-msg">{error}</div>;
  if (!issue) return <div className="error-msg">Issue not found</div>;

  return (
    <div className="main-content">

      {/* Back button */}
      <button
        className="btn-back"
        onClick={() => navigate(`/project/${projectId}/backlog`)}
      >
        ← Back to Backlog
      </button>

      {/* Issue Header */}
      <div className="issue-detail-header">
        <div className="issue-detail-title-row">
          <span className="issue-type-icon">{getTypeIcon(issue.issueType)}</span>
          <span className="issue-key-badge">{issue.issueKey}</span>
          <h1 className="issue-detail-title">{issue.issueTitle}</h1>
        </div>
        <select
          className="status-select-large"
          value={issue.issueStatus}
          onChange={(e) => handleStatusChange(e.target.value)}
          style={{ borderColor: getStatusColor(issue.issueStatus) }}
        >
          <option value="OPEN">OPEN</option>
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN PROGRESS</option>
          <option value="IN_REVIEW">IN REVIEW</option>
          <option value="DONE">DONE</option>
          <option value="BLOCKED">BLOCKED</option>
          <option value="CLOSED">CLOSED</option>
        </select>
      </div>

      {/* Main Layout */}
      <div className="issue-detail-body">

        {/* Left */}
        <div className="issue-detail-left">

          {/* Description */}
          <div className="detail-section">
            <h3>📝 Description</h3>
            {editing ? (
              <div>
                <input
                  className="edit-input"
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                  placeholder="Issue title"
                />
                <textarea
                  className="edit-textarea"
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  placeholder="Issue description"
                  rows={5}
                />
                <div className="form-actions">
                  <button className="btn-primary" onClick={handleEditSave}>
                    Save Changes
                  </button>
                  <button className="btn-secondary" onClick={() => {
                    setEditing(false);
                    setEditTitle(issue.issueTitle);
                    setEditDescription(issue.issueDescription || "");
                  }}>
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div>
                <p className="issue-description">
                  {issue.issueDescription || "No description provided."}
                </p>
                <button className="btn-edit" onClick={() => setEditing(true)}>
                  ✏️ Edit
                </button>
              </div>
            )}
          </div>

          {/* Labels */}
          {issue.labels && issue.labels.length > 0 && (
            <div className="detail-section">
              <h3>🏷️ Labels</h3>
              <div className="labels-row">
                {issue.labels.map((label, i) => (
                  <span key={i} className="label-badge">{label}</span>
                ))}
              </div>
            </div>
          )}

          {/* Comments */}
          <div className="detail-section">
            <h3>💬 Add Comment</h3>
            <input
              className="edit-input"
              placeholder="Your email (optional)"
              value={commentAuthor}
              onChange={(e) => setCommentAuthor(e.target.value)}
            />
            <textarea
              className="edit-textarea"
              placeholder="Write a comment..."
              value={commentBody}
              onChange={(e) => setCommentBody(e.target.value)}
              rows={3}
            />
            {commentError && <p className="error-msg">{commentError}</p>}
            {commentSuccess && <p className="success-msg">{commentSuccess}</p>}
            <button className="btn-primary" onClick={handleAddComment}>
              Add Comment
            </button>
          </div>

          {/* Attachments */}
          <div className="detail-section">
            <h3>📎 Attachments</h3>
            <div className="upload-area">
              <input
                className="edit-input"
                placeholder="Your email (optional)"
                value={uploaderEmail}
                onChange={(e) => setUploaderEmail(e.target.value)}
              />
              <label className="upload-label">
                {uploading ? "Uploading..." : "📎 Choose File to Upload"}
                <input
                  type="file"
                  accept=".png,.jpg,.jpeg,.pdf,.txt"
                  style={{ display: "none" }}
                  onChange={handleFileUpload}
                  disabled={uploading}
                />
              </label>
              <p className="upload-hint">Max 5MB — PNG, JPG, PDF, TXT</p>
              {uploadError && <p className="error-msg">{uploadError}</p>}
              {uploadSuccess && <p className="success-msg">{uploadSuccess}</p>}
            </div>

            {attachments.length === 0 ? (
              <p className="no-data">No attachments yet</p>
            ) : (
              <div className="attachments-list">
                {attachments.map((attachment) => (
                  <div key={attachment.id} className="attachment-item">
                    <span className="file-icon">
                      {getFileIcon(attachment.fileContentType)}
                    </span>
                    <div className="file-info">
                      <span className="file-name">{attachment.fileName}</span>
                      <span className="file-meta">
                        {formatFileSize(attachment.fileSize)} • {attachment.uplodedBy}
                      </span>
                    </div>
                    <div className="file-actions">
                      
                        href={attachment.storagePath}
                        target="_blank"
                        rel="noreferrer"
                        className="btn-download"
                      <a>
                        ⬇️ Download
                      </a>
                      <button
                        className="btn-delete-file"
                        onClick={() => handleDeleteAttachment(attachment.id)}
                      >
                        🗑️
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Right — Details */}
        <div className="issue-detail-right">
          <div className="detail-card">
            <h3>📋 Details</h3>

            <div className="detail-row">
              <span className="detail-label">Type</span>
              <span className="detail-value">
                {getTypeIcon(issue.issueType)} {issue.issueType}
              </span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Priority</span>
              <span className="detail-value"
                style={{ color: getPriorityColor(issue.priority) }}>
                ● {issue.priority || "—"}
              </span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Status</span>
              <span className="detail-value"
                style={{ color: getStatusColor(issue.issueStatus) }}>
                {issue.issueStatus}
              </span>
            </div>

            {/* ✅ Assignee with edit */}
            <div className="detail-row">
              <span className="detail-label">Assignee</span>
              <div className="detail-value">
                {editingAssignee ? (
                  <div className="inline-edit">
                    <input
                      className="edit-input-small"
                      placeholder="Enter email"
                      value={newAssignee}
                      onChange={(e) => setNewAssignee(e.target.value)}
                      onKeyPress={(e) => e.key === "Enter" && handleAssigneeUpdate()}
                    />
                    <button className="btn-save-small" onClick={handleAssigneeUpdate}>✅</button>
                    <button className="btn-cancel-small" onClick={() => setEditingAssignee(false)}>❌</button>
                  </div>
                ) : (
                  <div className="inline-edit">
                    <span>{issue.assigneeEmail || "Unassigned"}</span>
                    <button
                      className="btn-edit-small"
                      onClick={() => {
                        setNewAssignee(issue.assigneeEmail || "");
                        setEditingAssignee(true);
                      }}
                    >✏️</button>
                  </div>
                )}
              </div>
            </div>

            <div className="detail-row">
              <span className="detail-label">Reporter</span>
              <span className="detail-value">
                {issue.reporterEmail || "—"}
              </span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Sprint</span>
              <span className="detail-value">
                {issue.sprintId ? `Sprint ${issue.sprintId}` : "Backlog"}
              </span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Epic</span>
              <span className="detail-value">
                {issue.epicId ? `Epic ${issue.epicId}` : "—"}
              </span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Created</span>
              <span className="detail-value">{formatDate(issue.createdAt)}</span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Updated</span>
              <span className="detail-value">{formatDate(issue.updatedAt)}</span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Due Date</span>
              <span className="detail-value">{formatDate(issue.dueDate)}</span>
            </div>

          </div>
        </div>

      </div>
    </div>
  );
}

export default IssueDetail;