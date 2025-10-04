import api from './api';

export const dogService = {
  getDogs: (page = 0, size = 10, search = '') => {
    return api.get('/dogs', {
      params: { page, size, search }
    });
  },
  
  getDogById: (id) => {
    return api.get(`/dogs/${id}`);
  },
  
  createDog: (dogData) => {
    return api.post('/dogs', dogData);
  },
  
  updateDog: (id, dogData) => {
    return api.put(`/dogs/${id}`, dogData);
  },
  
  deleteDog: (id) => {
    return api.delete(`/dogs/${id}`);
  },
  
  getDogStats: () => {
    return api.get('/dogs/stats');
  }
};

