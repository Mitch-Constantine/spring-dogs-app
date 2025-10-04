import api from './api';

export const authService = {
  login: (username, password) => {
    return api.post('/auth/login', { username, password });
  },
  
  signup: (userData) => {
    return api.post('/auth/signup', userData);
  },
  
  logout: () => {
    return api.post('/auth/logout');
  }
};

