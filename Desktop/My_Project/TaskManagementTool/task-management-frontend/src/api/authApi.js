import axiosInstance from './axiosInstance';

export const loginUser = async (email, password) => {
  const res = await axiosInstance.post('/api/user-auth/login', {
    userOfficialEmail: email,
    password
  });
  return res.data; // plain JWT string
};

export const registerUser = async (data) => {
  const res = await axiosInstance.post('/api/user-auth/register', data);
  return res.data;
};

export const forgotPassword = async (email) => {
  const res = await axiosInstance.post(
    `/api/user-auth/forgot-password?userOfficialEmail=${email}`
  );
  return res.data;
};

export const resetPassword = async (token, newPassword) => {
  const res = await axiosInstance.post(
    `/api/user-auth/reset-password?token=${token}&newPassword=${newPassword}`
  );
  return res.data;
};

export const logoutUser = async () => {
  const res = await axiosInstance.post('/api/user-auth/logout');
  localStorage.removeItem('token');
  return res.data;
};