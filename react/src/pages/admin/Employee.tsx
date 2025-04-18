import React from 'react';
import { Chart } from 'react-google-charts';

const Employee: React.FC = () => {
  const data = [
    ['Employee', 'Hours Worked'],
    ['John', 40],
    ['Jane', 35],
    ['Mike', 30],
    ['Sara', 25],
    ['Tom', 20],
  ];

  const options = {
    title: 'Employee Work Hours',
    pieHole: 0.4,
    is3D: false,
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Employee Chart</h1>
      <Chart
        chartType="PieChart"
        data={data}
        options={options}
        width="100%"
        height="400px"
      />
    </div>
  );
};

export default Employee;