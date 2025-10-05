import api from './api';

const dogService = {
  getDogs: (page = 0, size = 10, search = '', prediction = '') => {
    return api.get('/dogs', {
      params: { page, size, search, prediction }
    });
  },

  getAllDogs: () => {
    return api.get('/dogs', {
      params: { size: 1000 }
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

export default dogService;

