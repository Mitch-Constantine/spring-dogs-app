import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
  TablePagination,
} from '@mui/material';
import { Add, Edit, Delete, Search } from '@mui/icons-material';
import { dogService } from '../services/dogService';
import { useAuth } from '../contexts/AuthContext';

const DogsList = () => {
  const [dogs, setDogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [deleteDialog, setDeleteDialog] = useState({ open: false, dog: null });
  const { user } = useAuth();

  const isAdmin = user?.role === 'ADMIN';
  
  console.log('DogsList component rendering, user:', user);

  const loadDogs = useCallback(async () => {
    try {
      setLoading(true);
      console.log('Loading dogs with params:', { page, rowsPerPage, search });
      const response = await dogService.getDogs(page, rowsPerPage, search);
      console.log('Dogs loaded successfully:', response.data);
      setDogs(response.data.content);
      setTotalCount(response.data.totalElements);
      setError('');
    } catch (err) {
      setError('Failed to load dogs');
      console.error('Error loading dogs:', err);
    } finally {
      setLoading(false);
    }
  }, [page, rowsPerPage, search]);

  useEffect(() => {
    loadDogs();
  }, [loadDogs]);

  const handleDelete = async (dogId) => {
    try {
      await dogService.deleteDog(dogId);
      setDeleteDialog({ open: false, dog: null });
      loadDogs(); // Reload the list
    } catch (err) {
      setError('Failed to delete dog');
      console.error('Error deleting dog:', err);
    }
  };


  const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Dogs</Typography>
        {isAdmin && (
          <Button
            variant="contained"
            startIcon={<Add />}
            component={Link}
            to="/dogs/new"
          >
            Add Dog
          </Button>
        )}
      </Box>

      <Paper sx={{ p: 2, mb: 2 }}>
        <Box display="flex" gap={2} alignItems="center">
          <TextField
            label="Search dogs..."
            variant="outlined"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            InputProps={{
              startAdornment: <Search sx={{ mr: 1 }} />
            }}
            sx={{ flexGrow: 1 }}
          />
        </Box>
      </Paper>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box display="flex" justifyContent="center" p={4}>
          <CircularProgress />
        </Box>
      ) : (
        <Paper>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Breed</TableCell>
                  <TableCell>Age</TableCell>
                  <TableCell>Weight (lbs)</TableCell>
                  <TableCell>Temperament</TableCell>
                  <TableCell>Created</TableCell>
                  {isAdmin && <TableCell>Actions</TableCell>}
                </TableRow>
              </TableHead>
              <TableBody>
                {dogs.map((dog) => (
                  <TableRow key={dog.id} hover>
                    <TableCell>
                      <Link 
                        to={`/dogs/${dog.id}`}
                        style={{ textDecoration: 'none', color: 'inherit' }}
                      >
                        {dog.name}
                      </Link>
                    </TableCell>
                    <TableCell>{dog.breed}</TableCell>
                    <TableCell>{dog.age}</TableCell>
                    <TableCell>{dog.weight ? `${dog.weight} lbs` : 'N/A'}</TableCell>
                    <TableCell style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      <span title={dog.temperament || 'N/A'}>
                        {dog.temperament || 'N/A'}
                      </span>
                    </TableCell>
                    <TableCell>{formatDate(dog.createdAt)}</TableCell>
                    {isAdmin && (
                      <TableCell>
                        <IconButton
                          component={Link}
                          to={`/dogs/${dog.id}`}
                          color="primary"
                          size="small"
                        >
                          <Edit />
                        </IconButton>
                        <IconButton
                          onClick={() => setDeleteDialog({ open: true, dog })}
                          color="error"
                          size="small"
                        >
                          <Delete />
                        </IconButton>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          
          <TablePagination
            component="div"
            count={totalCount}
            page={page}
            onPageChange={(event, newPage) => setPage(newPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(event) => {
              setRowsPerPage(parseInt(event.target.value, 10));
              setPage(0);
            }}
            rowsPerPageOptions={[5, 10, 25]}
          />
        </Paper>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, dog: null })}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          Are you sure you want to delete "{deleteDialog.dog?.name}"? This action cannot be undone.
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, dog: null })}>
            Cancel
          </Button>
          <Button 
            onClick={() => handleDelete(deleteDialog.dog.id)} 
            color="error"
            variant="contained"
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DogsList;

