import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  console.log('PrivateRoute:', { user, loading, pathname: location.pathname });

  if (loading) {
    console.log('PrivateRoute: showing loading spinner');
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    console.log('PrivateRoute: no user, redirecting to login');
    // Redirect them to the login page, but save the attempted location
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  console.log('PrivateRoute: user authenticated, rendering children');
  return children;
};

export default PrivateRoute;
