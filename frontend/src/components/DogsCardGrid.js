import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Typography,
  Grid,
  Card,
  CardContent,
  IconButton,
  Tooltip,
  Button,
  Paper,
  Badge
} from '@mui/material';
import { Search, Edit, Add, FilterList } from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import dogService from '../services/dogService';

const predictionColors = {
  'Yes': '#4caf50', // Green
  'Cautiously': '#ff9800', // Orange
  'No': '#f44336' // Red
};

const predictionIcons = {
  'Yes': '✅',
  'Cautiously': '⚠️',
  'No': '⛔'
};

const DogsCardGrid = () => {
  const { user } = useAuth();
  const [dogs, setDogs] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [predictionFilter, setPredictionFilter] = useState('All');
  const [sortBy, setSortBy] = useState('name');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const isAdmin = user?.role === 'ADMIN';

  useEffect(() => {
    loadDogs();
  }, [searchTerm, predictionFilter, sortBy]);

  const loadDogs = async () => {
    try {
      setLoading(true);
      const response = await dogService.getAllDogs();
      let filteredDogs = response.data.content;

      // Apply search filter
      if (searchTerm) {
        filteredDogs = filteredDogs.filter(dog =>
          dog.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          dog.breed.toLowerCase().includes(searchTerm.toLowerCase())
        );
      }

      // Apply prediction filter
      if (predictionFilter !== 'All') {
        filteredDogs = filteredDogs.filter(dog => dog.isSafeToPet === predictionFilter);
      }

      // Apply sorting
      filteredDogs.sort((a, b) => {
        switch (sortBy) {
          case 'name':
            return a.name.localeCompare(b.name);
          case 'breed':
            return a.breed.localeCompare(b.breed);
          case 'weight':
            return (b.weight || 0) - (a.weight || 0);
          default:
            return 0;
        }
      });

      setDogs(filteredDogs);
    } catch (err) {
      setError('Failed to load dogs');
      console.error('Error loading dogs:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatWeight = (weight) => {
    if (!weight) return 'N/A';
    const kg = Math.round(weight * 0.453592); // Convert lbs to kg
    return `${kg} kg`;
  };

  const getPredictionBadge = (prediction) => {
    const color = predictionColors[prediction] || '#757575';
    const icon = predictionIcons[prediction] || '❓';
    
    return (
      <Box
        sx={{
          backgroundColor: color,
          color: 'white',
          borderRadius: '16px',
          padding: '4px 12px',
          fontSize: '0.875rem',
          fontWeight: 'bold',
          display: 'flex',
          alignItems: 'center',
          gap: 0.5
        }}
      >
        {icon} {prediction}
      </Box>
    );
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <Typography>Loading dogs...</Typography>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header Bar */}
      <Paper 
        elevation={2} 
        sx={{ 
          p: 3, 
          mb: 3, 
          position: 'sticky', 
          top: 0, 
          zIndex: 1,
          backgroundColor: 'background.paper'
        }}
      >
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold' }}>
          Dog List
        </Typography>
        
        <Grid container spacing={2} alignItems="center">
          {/* Search */}
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              placeholder="Search by name or breed..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />
              }}
              size="small"
            />
          </Grid>

          {/* Prediction Filter */}
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Prediction</InputLabel>
              <Select
                value={predictionFilter}
                onChange={(e) => setPredictionFilter(e.target.value)}
                label="Prediction"
              >
                <MenuItem value="All">All Predictions</MenuItem>
                <MenuItem value="Yes">🟢 Yes - Safe to pet</MenuItem>
                <MenuItem value="Cautiously">🟡 Cautiously - Approach carefully</MenuItem>
                <MenuItem value="No">🔴 No - Do not pet</MenuItem>
              </Select>
            </FormControl>
          </Grid>

          {/* Sort */}
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Sort by</InputLabel>
              <Select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                label="Sort by"
              >
                <MenuItem value="name">Name</MenuItem>
                <MenuItem value="breed">Breed</MenuItem>
                <MenuItem value="weight">Weight</MenuItem>
              </Select>
            </FormControl>
          </Grid>

          {/* Add Button */}
          <Grid item xs={12} sm={6} md={3}>
            {isAdmin && (
              <Button
                component={Link}
                to="/dogs/new"
                variant="contained"
                startIcon={<Add />}
                fullWidth
                sx={{ height: '40px' }}
              >
                Add Dog
              </Button>
            )}
          </Grid>
        </Grid>
      </Paper>

      {/* Error */}
      {error && (
        <Paper sx={{ p: 2, mb: 2, backgroundColor: 'error.light' }}>
          <Typography color="error">{error}</Typography>
        </Paper>
      )}

      {/* Dog Cards Grid */}
      <Grid container spacing={3}>
        {dogs.map((dog) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={dog.id}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                borderRadius: 2,
                '&:hover': {
                  boxShadow: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.2s ease-in-out'
                }
              }}
            >
              <CardContent sx={{ flexGrow: 1, p: 2 }}>
                {/* Dog Name */}
                <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
                  {dog.name}
                </Typography>

                {/* Breed */}
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {dog.breed}
                </Typography>

                {/* Weight */}
                <Typography variant="body2" sx={{ mb: 2 }}>
                  {formatWeight(dog.weight)}
                </Typography>

                {/* Safety Prediction Badge */}
                <Box sx={{ mb: 2 }}>
                  <Tooltip title={dog.safetyExplanation || 'No explanation available'}>
                    {getPredictionBadge(dog.isSafeToPet)}
                  </Tooltip>
                </Box>

                {/* Temperament Tags */}
                <Box sx={{ mb: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    <strong>Temperament:</strong>
                  </Typography>
                  <Box sx={{ mt: 0.5 }}>
                    {dog.temperament ? (
                      <Typography variant="body2" sx={{ 
                        fontSize: '0.75rem',
                        color: 'text.secondary',
                        lineHeight: 1.2
                      }}>
                        {dog.temperament.length > 60 
                          ? `${dog.temperament.substring(0, 60)}...` 
                          : dog.temperament}
                      </Typography>
                    ) : (
                      <Typography variant="body2" color="text.disabled">
                        No temperament info
                      </Typography>
                    )}
                  </Box>
                </Box>

                {/* Edit Button */}
                {isAdmin && (
                  <Box sx={{ mt: 'auto', pt: 1 }}>
                    <IconButton
                      component={Link}
                      to={`/dogs/${dog.id}`}
                      size="small"
                      sx={{ 
                        backgroundColor: 'primary.main',
                        color: 'white',
                        '&:hover': {
                          backgroundColor: 'primary.dark'
                        }
                      }}
                    >
                      <Edit />
                    </IconButton>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Empty State */}
      {dogs.length === 0 && !loading && (
        <Box textAlign="center" p={4}>
          <Typography variant="h6" color="text.secondary">
            {searchTerm || predictionFilter !== 'All' 
              ? 'No dogs match your criteria'
              : 'No dogs available'}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            {searchTerm || predictionFilter !== 'All' 
              ? 'Try adjusting your search or filters'
              : 'Add some dogs to get started'}
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default DogsCardGrid;
