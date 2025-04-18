import React from 'react';
import { Link } from 'react-router-dom';

const NotFoundPage: React.FC = () => {
  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h1>404</h1>
      <p>Oops! The page you are looking for does not exist.</p>
      <Link to="/" style={{ color: '#007bff', textDecoration: 'none' }}>
        Go back to Home
      </Link>
    </div>
  );
};

export default NotFoundPage;