import axiosInstance from './axiosInstance';

export const uploadAttachment = async (issueId, file, uploadedBy) => {
  const formData = new FormData();
  formData.append('File', file);          // ✅ capital F - matches backend
  formData.append('uploadedBy', uploadedBy);

  const res = await axiosInstance.post(
    `/api/attachments/upload/${issueId}`,
    formData,
    {
      headers: { 'Content-Type': 'multipart/form-data' }
    }
  );
  return res.data;
};

export const getAttachmentsByIssue = async (issueId) => {
  const res = await axiosInstance.get(`/api/attachments/issue/${issueId}`);
  return res.data;
};

export const downloadAttachment = async (id) => {
  const res = await axiosInstance.get(`/api/attachments/download/${id}`);
  return res.data;
};

export const deleteAttachment = async (id) => {
  const res = await axiosInstance.delete(`/api/attachments/delete/${id}`);
  return res.data;
};