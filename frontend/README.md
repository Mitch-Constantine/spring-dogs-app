# Spring Dogs Frontend

This is the React frontend for the Spring Dogs application.

## Features

- **Authentication**: Login and registration with JWT
- **Dog Management**: View, add, edit, and delete dogs (Admin only)
- **Search**: Find dogs by name, breed, or owner
- **Responsive Design**: Built with Material-UI
- **Role-based Access**: Admin vs Guest user permissions

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm start
   ```

3. Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

## Environment Variables

Create a `.env` file in the frontend directory:

```
REACT_APP_API_URL=http://localhost:8080/api
```

## Available Scripts

- `npm start` - Runs the app in development mode
- `npm run build` - Builds the app for production
- `npm test` - Launches the test runner
- `npm run eject` - Removes the single build dependency (one-way operation)

## Tech Stack

- React 18
- Material-UI (MUI)
- React Router
- React Hook Form
- React Query
- Axios
- Context API for state management

