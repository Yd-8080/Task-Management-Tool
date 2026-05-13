import axiosInstance from './axiosInstance';

export const createSprint = async (projectId, sprintData) => {
  const res = await axiosInstance.post(
    `/api/sprints/create/${Number(projectId)}`,  // ✅ force number
    sprintData
  );
  return res.data;
};

export const getSprintsByProject = async (projectId) => {
  const res = await axiosInstance.get(
    `/api/sprints/project/${Number(projectId)}`
  );
  return res.data;
};

export const startSprint = async (sprintId) => {
  const res = await axiosInstance.put(
    `/api/sprints/start/${sprintId}`
  );
  return res.data;
};

export const endSprint = async (sprintId) => {
  const res = await axiosInstance.put(
    `/api/sprints/end/${sprintId}`
  );
  return res.data;
};