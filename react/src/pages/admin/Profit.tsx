import React from "react";
import { Line } from "react-chartjs-2";
import {
	Chart as ChartJS,
	CategoryScale,
	LinearScale,
	PointElement,
	LineElement,
	Title,
	Tooltip,
	Legend,
} from "chart.js";

ChartJS.register(
	CategoryScale,
	LinearScale,
	PointElement,
	LineElement,
	Title,
	Tooltip,
	Legend
);

const Profit: React.FC = () => {
	const data = {
		labels: ["January", "February", "March", "April", "May", "June", "July"],
		datasets: [
			{
				label: "Profit",
				data: [1200, 1900, 3000, 5000, 2300, 3400, 4200],
				borderColor: "rgba(75, 192, 192, 1)",
				backgroundColor: "rgba(75, 192, 192, 0.2)",
				tension: 0.4,
			},
		],
	};

	const options = {
		responsive: true,
		plugins: {
			legend: {
				position: "top" as const,
			},
			title: {
				display: true,
				text: "Profit Chart",
			},
		},
	};

	return (
		<div style={{ width: "80%", margin: "0 auto", padding: "20px" }}>
			<h1>Profit Page</h1>
			<Line data={data} options={options} />
		</div>
	);
};

export default Profit;
