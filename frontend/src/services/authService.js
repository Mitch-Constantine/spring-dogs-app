import api from './api';

export const authService = {
  login: (username, password) => {
    return api.post('/auth/login', { username, password });
  },
  
  logout: () => {
    return api.post('/auth/logout');
  }
};

