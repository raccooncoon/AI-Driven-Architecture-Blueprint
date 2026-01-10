import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const getRequirements = async () => {
    const response = await api.get('/requirements');
    return response.data;
};
