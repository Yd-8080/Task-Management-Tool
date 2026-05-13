import axiosInstance from './axiosInstance';

export const createProject = async (projectData, ownerEmail) => {
  const res = await axiosInstance.post(
    `/api/projects/create?ownerEmail=${ownerEmail}`,
    projectData
  );
  return res.data;
};

export const getMyProjects = async (email) => {
  const res = await axiosInstance.get(
    `/api/projects/my-projects?email=${email}`
  );
  return res.data;
};

export const addMember = async (projectId, email) => {
  const res = await axiosInstance.post(
    `/api/projects/${projectId}/add-member?email=${email}`
  );
  return res.data;
};

export const removeMember = async (projectId, email) => {
  const res = await axiosInstance.delete(
    `/api/projects/${projectId}/remove-member?email=${email}`
  );
  return res.data;
};