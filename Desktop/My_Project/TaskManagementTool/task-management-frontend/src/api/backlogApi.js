import axiosInstance from "./axiosInstance";

export const getBacklog = async (projectId) => {
  const res = await axiosInstance.get(`/api/backlog/${projectId}`);
  return res.data;
};

// ✅ FIXED - correct URL
export const addIssueToSprint = async (issueId, sprintId) => {
  const res = await axiosInstance.put(
    `/api/backlog/add-to-sprint/${issueId}/${sprintId}`
  );
  return res.data;
};

export const reorderBacklog = async (projectId, orderedIssueIds) => {
  const res = await axiosInstance.post(
    `/api/backlog/${projectId}/record`,
    orderedIssueIds
  );
  return res.data;
};

export const getBacklogHierarchy = async (projectId) => {
  const res = await axiosInstance.get(
    `/api/backlog/${projectId}/hieracrchy`
  );
  return res.data;
};