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
      ownerName: '',
      ownerPhone: '',
      ownerEmail: '',
      birthDate: '',
      medicalNotes: '',
      status: 'Active'
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
                    <Typography variant="body2" color="text.secondary">Color</Typography>
                    <Typography variant="body1">{dog.color || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Weight</Typography>
                    <Typography variant="body1">{dog.weight ? `${dog.weight} kg` : 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Status</Typography>
                    <Chip label={dog.status} color={dog.status === 'Active' ? 'success' : 'default'} />
                  </Grid>
                </Grid>
                
                <Divider sx={{ my: 2 }} />
                
                <Typography variant="h6" gutterBottom>Owner Information</Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Owner Name</Typography>
                    <Typography variant="body1">{dog.ownerName}</Typography>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Typography variant="body2" color="text.secondary">Phone</Typography>
                    <Typography variant="body1">{dog.ownerPhone || 'N/A'}</Typography>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">Email</Typography>
                    <Typography variant="body1">{dog.ownerEmail || 'N/A'}</Typography>
                  </Grid>
                </Grid>
                
                {dog.medicalNotes && (
                  <>
                    <Divider sx={{ my: 2 }} />
                    <Typography variant="h6" gutterBottom>Medical Notes</Typography>
                    <Typography variant="body1">{dog.medicalNotes}</Typography>
                  </>
                )}
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
                  label="Weight (kg)"
                  type="number"
                  step="0.1"
                  {...register('weight', { min: { value: 0, message: 'Weight must be positive' } })}
                />
              </Grid>
              
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Owner Name *"
                  error={!!errors.ownerName}
                  helperText={errors.ownerName?.message}
                  {...register('ownerName', { required: 'Owner name is required' })}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Owner Phone"
                  {...register('ownerPhone')}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Owner Email"
                  type="email"
                  {...register('ownerEmail')}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Birth Date"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  {...register('birthDate')}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Status"
                  select
                  {...register('status')}
                >
                  <MenuItem value="Active">Active</MenuItem>
                  <MenuItem value="Inactive">Inactive</MenuItem>
                  <MenuItem value="Adopted">Adopted</MenuItem>
                  <MenuItem value="Deceased">Deceased</MenuItem>
                </TextField>
              </Grid>
              
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Medical Notes"
                  multiline
                  rows={4}
                  {...register('medicalNotes')}
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
