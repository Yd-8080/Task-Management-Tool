import axiosInstance from './axiosInstance';

export const getBurnDownReport = async (sprintId) => {
  const res = await axiosInstance.get(`/api/report/burndownReport/${sprintId}`);
  return res.data;
};

export const getVelocityReport = async (projectId) => {
  const res = await axiosInstance.get(`/api/report/velocityReport/${projectId}`);
  return res.data;
};

export const getSprintReport = async (sprintId) => {
  const res = await axiosInstance.get(`/api/report/sprintReport/${sprintId}`);
  return res.data;
};

export const getEpicReport = async (epicId) => {
  const res = await axiosInstance.get(`/api/report/epicReport/${epicId}`);
  return res.data;
};

export const getCumulativeFlow = async (sprintId) => {
  const res = await axiosInstance.get(`/api/report/cumultaive/${sprintId}`);
  return res.data;
};

export const getWorkloadReport = async (sprintId) => {
  const res = await axiosInstance.get(`/api/report/workLoadReport/${sprintId}`);
  return res.data;
};