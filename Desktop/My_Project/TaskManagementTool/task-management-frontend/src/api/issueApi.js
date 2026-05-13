import axiosInstance from "./axiosInstance";

export const searchIssuesByProject = async (projectId) => {
  const res = await axiosInstance.get(`/api/issues/search`, {
    params: { projectId }  // ✅ send projectId as param
  });
  return res.data;
};

export const updateIssueStatus = async (issueKey, status) => {
  // ✅ use PATCH not PUT, and correct URL
  const res = await axiosInstance.patch(
    `/api/issues/${issueKey}/status?issueStatus=${status}`
  );
  return res.data;
};

export const createIssue = async (issueData) => {
  // ✅ correct endpoint
  const res = await axiosInstance.post(`/api/issues/createIssue`, issueData);
  return res.data;
};

export const getIssueById = async (id) => {
  const res = await axiosInstance.get(`/api/issues/${id}`);
  return res.data;
};

export const addComment = async (id, body, authorEmail) => {
  const res = await axiosInstance.post(`/api/issues/${id}/comment`, {
    body,
    authorEmail
  });
  return res.data;
};

export const searchIssues = async (params) => {
  const res = await axiosInstance.get(`/api/issues/search`, { params });
  return res.data;
};

export const updateIssue = async (id, issueData) => {
  const res = await axiosInstance.patch(
    `/api/issues/${id}/update`,
    issueData
  );
  return res.data;
};

// ✅ ADDED - get all issues assigned to a user
export const getIssuesByAssignee = async (email) => {
  const res = await axiosInstance.get(
    `/api/issues/assignee/${encodeURIComponent(email)}`
  );
  return res.data;
};