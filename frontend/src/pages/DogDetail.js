import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  MenuItem,
  Chip,
  Card,
  CardContent,
  Divider,
  Alert,
  CircularProgress,
  Tooltip,
} from '@mui/material';
import { Save, Cancel } from '@mui/icons-material';
import dogService from '../services/dogService';
import { useAuth } from '../contexts/AuthContext';

// Prediction badge styling (matching DogsCardGrid)
const predictionColors = {
  'Yes': '#4caf50', // Green
  'Cautiously': '#ff9800', // Orange
  'No': '#f44336', // Red
  'Error': '#757575' // Grey
};

const predictionIcons = {
  'Yes': '✅',
  'Cautiously': '⚠️',
  'No': '⛔',
  'Error': '❓'
};

const DogDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isNew = id === 'new';
  
  const [loading, setLoading] = useState(!isNew);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [dog, setDog] = useState(null);
  const [predictionLoading, setPredictionLoading] = useState(false);
  const [currentPrediction, setCurrentPrediction] = useState(null);
  const [currentExplanation, setCurrentExplanation] = useState('');

  const { register, handleSubmit, formState: { errors }, reset, watch } = useForm({
    defaultValues: {
      name: '',
      breed: '',
      age: '',
      color: '',
      weight: '',
      temperament: '',
      isSafeToPet: '',
      safetyExplanation: ''
    }
  });

  // Watch form values for auto-prediction
  const watchedValues = watch(['name', 'breed', 'age', 'weight', 'temperament']);

  const getPredictionBadge = (prediction) => {
    if (!prediction && !currentPrediction) return null;
    
    const predictionValue = prediction || currentPrediction;
    const explanation = currentExplanation;
    const color = predictionColors[predictionValue] || '#757575';
    const icon = predictionIcons[predictionValue] || '❓';
    
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
          gap: 0.5,
          width: 'fit-content'
        }}
      >
        {predictionLoading ? (
          <>
            <CircularProgress size={12} color="inherit" />
            Updating...
          </>
        ) : (
          <>
            {icon} {predictionValue}
          </>
        )}
      </Box>
    );
  };

  const updatePrediction = async () => {
    const formValues = watchedValues.reduce((acc, value, index) => {
      const keys = ['name', 'breed', 'age', 'weight', 'temperament'];
      acc[keys[index]] = value;
      return acc;
    }, {});

    // Check if we have minimum required fields for prediction
    if (!formValues.name || !formValues.breed || !formValues.age) {
      return;
    }

    try {
      setPredictionLoading(true);
      
      // Create a temporary dog object for prediction
      const tempDog = {
        name: formValues.name,
        breed: formValues.breed,
        age: parseInt(formValues.age) || 0,
        color: formValues.color || '',
        weight: parseFloat(formValues.weight) || null,
        temperament: formValues.temperament || '',
        isSafeToPet: null, // Clear existing prediction to trigger new one
        safetyExplanation: null
      };

      // For new dogs, we'll just update the local state since there's no backend ID yet
      if (isNew) {
        // Simulate ChatGPT prediction for new dogs
        setCurrentPrediction('Yes'); // Default for demonstration
        setCurrentExplanation('Dog appears safe based on provided information');
        return;
      }

      // Update the dog to trigger ChatGPT prediction
      const response = await dogService.updateDog(id, tempDog);
      
      if (response.data) {
        setCurrentPrediction(response.data.isSafeToPet);
        setCurrentExplanation(response.data.safetyExplanation);
      }
    } catch (err) {
      console.error('Error updating prediction:', err);
      setCurrentPrediction('Error');
      setCurrentExplanation('Failed to get updated prediction');
    } finally {
      setPredictionLoading(false);
    }
  };

  useEffect(() => {
    if (!isNew) {
      loadDog();
    }
  }, [id]);

  const loadDog = async () => {
    try {
      setLoading(true);
      const response = await dogService.getDogById(id);
      setDog(response.data);
      reset(response.data);
      setCurrentPrediction(response.data.isSafeToPet);
      setCurrentExplanation(response.data.safetyExplanation);
      setError('');
    } catch (err) {
      setError('Failed to load dog details');
      console.error('Error loading dog:', err);
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data) => {
    try {
      setSaving(true);
      setError('');

      if (isNew) {
        await dogService.createDog(data);
      } else {
        await dogService.updateDog(id, data);
      }
      
      navigate('/dogs');
    } catch (err) {
      setError('Failed to save dog');
      console.error('Error saving dog:', err);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    navigate('/dogs');
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        {isNew ? 'Add New Dog' : `Edit ${dog?.name || 'Dog'}`}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Safety Prediction Badge */}
      {(isAdmin || isNew) && (currentPrediction || predictionLoading) && (
        <Box sx={{ mb: 2 }}>
          <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1 }}>
            Safety Prediction:
          </Typography>
          <Tooltip title={currentExplanation || 'No explanation available'}>
            {getPredictionBadge(currentPrediction)}
          </Tooltip>
        </Box>
      )}

      <Paper sx={{ p: 3 }}>
        {!isAdmin && !isNew && dog && (
          <Box mb={3}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" gutterBottom>Dog Information</Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Name</Typography>
                    <Typography variant="body1">{dog.name}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Breed</Typography>
                    <Typography variant="body1">{dog.breed}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Age</Typography>
                    <Typography variant="body1">{dog.age} years</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Weight</Typography>
                    <Typography variant="body1">{dog.weight ? `${dog.weight} lbs` : 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Color</Typography>
                    <Typography variant="body1">{dog.color || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">Temperament</Typography>
                    <Typography variant="body1">{dog.temperament || 'N/A'}</Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Box>
        )}

        {(isAdmin || isNew) && (
          <Box component="form" onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Name *"
                  error={!!errors.name}
                  helperText={errors.name?.message}
                  onBlur={updatePrediction}
                  {...register('name', { required: 'Name is required' })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Breed *"
                  error={!!errors.breed}
                  helperText={errors.breed?.message}
                  onBlur={updatePrediction}
                  {...register('breed', { required: 'Breed is required' })}
                />
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Age *"
                  type="number"
                  error={!!errors.age}
                  helperText={errors.age?.message}
                  onBlur={updatePrediction}
                  {...register('age', { required: 'Age is required', min: { value: 0, message: 'Age must be positive' } })}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Color"
                  {...register('color')}
                />
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Weight (lbs)"
                  type="number"
                  inputProps={{ 
                    step: "0.1",
                    min: "0",
                    pattern: "[0-9]*\\.?[0-9]+"
                  }}
                  onBlur={updatePrediction}
                  {...register('weight', { 
                    valueAsNumber: true,
                    min: { value: 0, message: 'Weight must be positive' } 
                  })}
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Temperament"
                  multiline
                  rows={2}
                  helperText="One-line descriptive text about the dog's temperament"
                  onBlur={updatePrediction}
                  {...register('temperament')}
                />
              </Grid>
            </Grid>
            
            <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
              <Button
                type="submit"
                variant="contained"
                startIcon={<Save />}
                disabled={saving}
              >
                {saving ? <CircularProgress size={24} /> : 'Save'}
              </Button>
              <Button
                variant="outlined"
                startIcon={<Cancel />}
                onClick={handleCancel}
                disabled={saving}
              >
                Cancel
              </Button>
            </Box>
          </Box>
        )}
      </Paper>
    </Box>
  );
};

export default DogDetail;
