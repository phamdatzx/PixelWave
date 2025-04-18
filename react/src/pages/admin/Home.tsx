import React from 'react';

const AdminHome: React.FC = () => {
  return (
    <div style={{ padding: '20px' }}>
      <h1>Welcome to Admin Dashboard</h1>
      <p>Manage your application efficiently from here.</p>
      <div style={{ marginTop: '20px' }}>
        <button style={{ marginRight: '10px' }}>Manage Users</button>
        <button style={{ marginRight: '10px' }}>View Reports</button>
        <button>Settings</button>
      </div>
    </div>
  );
};

export default AdminHome;