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
} from '@mui/material';
import { Save, Cancel } from '@mui/icons-material';
import { dogService } from '../services/dogService';
import { useAuth } from '../contexts/AuthContext';

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

  const { register, handleSubmit, formState: { errors }, reset, watch } = useForm({
    defaultValues: {
      name: '',
      breed: '',
      age: '',
      color: '',
      weight: '',
      temperament: ''
    }
  });

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
                  {...register('name', { required: 'Name is required' })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Breed *"
                  error={!!errors.breed}
                  helperText={errors.breed?.message}
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
                  step="0.1"
                  {...register('weight', { min: { value: 0, message: 'Weight must be positive' } })}
                />
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Temperament"
                  multiline
                  rows={2}
                  helperText="One-line descriptive text about the dog's temperament"
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
