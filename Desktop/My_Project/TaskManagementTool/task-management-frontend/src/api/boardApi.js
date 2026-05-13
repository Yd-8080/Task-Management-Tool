import axiosInstance from './axiosInstance';

export const createBoard = async (boardData) => {
  const res = await axiosInstance.post('/api/boards', boardData);
  return res.data;
};

export const getBoardById = async (id) => {
  const res = await axiosInstance.get(`/api/boards/${id}`);
  return res.data;
};

export const getBoardColumns = async (id) => {
  const res = await axiosInstance.get(`/api/boards/${id}/columns`);
  return res.data;
};

export const getBoardCards = async (id) => {
  const res = await axiosInstance.get(`/api/boards/${id}/cards`);
  return res.data;
};

export const addCardToBoard = async (boardId, columnId, issueId) => {
  const res = await axiosInstance.post(`/api/boards/${boardId}/card`, {
    columnId,
    issueId
  });
  return res.data;
};

export const addColumnToBoard = async (boardId, column) => {
  const res = await axiosInstance.post(`/api/boards/${boardId}/column`, column);
  return res.data;
};