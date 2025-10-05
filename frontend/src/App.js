import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import DogsCardGrid from './components/DogsCardGrid';
import DogDetail from './pages/DogDetail';
import './App.css';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  console.log('App component rendering');
  
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <div className="App">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<PrivateRoute><Layout><DogsCardGrid /></Layout></PrivateRoute>} />
              <Route path="/dogs" element={<PrivateRoute><Layout><DogsCardGrid /></Layout></PrivateRoute>} />
              <Route path="/dogs/:id" element={<PrivateRoute><Layout><DogDetail /></Layout></PrivateRoute>} />
              <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;

