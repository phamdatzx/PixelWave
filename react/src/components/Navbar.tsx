import { useNavigate } from "react-router-dom";

import "@/styles/components/_navbar.scss"; // Import the CSS file for styling

import logo from "@/assets/images/logo.png";
import { Bell, Search, SquarePlus } from "lucide-react";

const Navbar = () => {
	const navigate = useNavigate();
	const path = window.location.pathname.split("/")[1];

	return (
		<header className="header">
			<nav className="navbar">
				<span
					className="logo"
					onClick={() => {
						navigate(`/${path}`);
					}}
				>
					<img src={logo} alt="Logo for Wavelink" />
				</span>
				<div className="search-bar">
					<button className="search-btn">
						<Search />
					</button>
					<input type="text" placeholder="Search..." className="search-input" />
				</div>
				<div className="user-interaction">
					<div className="create-post">
						<button className="create-btn">
							<SquarePlus />
						</button>
					</div>
					<div className="notifications">
						<button className="notification-btn">
							<Bell />
						</button>
					</div>
					<div className="profile">
						<img src="" alt="logo for user" />
						<ul className="menu">
							<li className="item">Your profile</li>
							<li className="item">Settings</li>
							<li className="item">Logout</li>
						</ul>
					</div>
				</div>
			</nav>
		</header>
	);
};

export default Navbar;
