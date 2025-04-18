import React from 'react';

const Profile: React.FC = () => {
  return (
    <div className="profile-container">
      <h1>User Profile</h1>
      <div className="profile-info">
        {/* User information will be displayed here */}
      </div>
      <div className="uploaded-images">
        <h2>Uploaded Images</h2>
        {/* List of uploaded images will be displayed here */}
      </div>
    </div>
  );
};

export default Profile;