
import api from "./api";

// Student registration
export const registerStudent = async (studentData) => {
  const response = await api.post("/api/students", studentData);
  return response.data;
};

// Student/Admin login
export const loginUser = async (credentials) => {
  const response = await api.post("/api/auth/login", credentials);

  // Save authentication details
  localStorage.setItem("token", response.data.token);
  localStorage.setItem("email", response.data.email);
  localStorage.setItem("role", response.data.role);

  return response.data;
};

// Logout
export const logoutUser = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
};
