import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./../hooks/useAuth";

import "@/styles/pages/_login.scss";

import bg from "@/assets/images/bg.png";
import gg from "@/assets/images/gg.png";
import logo from "@/assets/images/logo.png";
import { Eye, EyeOff } from "lucide-react";
import { login, register } from "@/utils/auth";

const Login = () => {
	type LoginFormState = {
		email: string;
		password: string;
		showPassword: boolean;
	};

	type RegisterFormState = {
		email: string;
		password: string;
		fullName: string;
		age: number;
		showPassword: boolean;
	};

	//Login form
	const [loginForm, setLoginForm] = useState<LoginFormState>({
		email: "",
		password: "",
		showPassword: false,
	});

	const handleLoginChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { name, value } = e.target;
		setLoginForm((prev) => ({ ...prev, [name]: value }));
	};

	const toggleLoginPassword = () => {
		setLoginForm((prev) => ({ ...prev, showPassword: !prev.showPassword }));
	};

	//register Modal
	const [showModal, setShowModal] = useState<boolean>(false);
	const [registerForm, setRegisterForm] = useState<RegisterFormState>({
		email: "",
		password: "",
		fullName: "",
		age: NaN,
		showPassword: false,
	});

	const handleRegisterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { name, value } = e.target;
		setRegisterForm((prev) => ({ ...prev, [name]: value }));
	};

	const toggleRegisterPassword = () => {
		setRegisterForm((prev) => ({ ...prev, showPassword: !prev.showPassword }));
	};

	// State local manager pages state
	const [error, setError] = useState<string>("");
	const [loading, setLoading] = useState<boolean>(false);
	console.log(loading);

	//Hooks navigation between pages
	const navigate = useNavigate();
	// Hooks to storage and provide user data throught components
	const { setUserLogin } = useAuth();

	const handleRegister = async (e: React.FormEvent) => {
		e.preventDefault();
		const { email, password, fullName, age } = registerForm;
		if (!email || !password || !fullName || !age) {
			setError("Please fill in all fields");
			return;
		}
		setLoading(true);
		try {
			const response = await register({ email, password, fullName, age });
			console.log(response);
			setShowModal(false);
		} catch (err: unknown) {
			if (err instanceof Error) setError(err.message);
			else setError("An unexpected error occurred. Please try again.");
			console.log(error);
		} finally {
			setLoading(false);
		}
	};

	// Login
	const handleLogin = async (e: React.FormEvent) => {
		e.preventDefault();
		const { email, password } = loginForm;
		if (!email || !loginForm.email) {
			setError("Please fill in all fields");
			return;
		}
		setLoading(true);
		try {
			const response = await login({ email, password }); // gọi API
			localStorage.setItem("accessToken", response.accessToken);
			localStorage.setItem("refreshToken", response.refreshToken);
			setUserLogin({
				user: response.user,
				accessToken: response.accessToken,
			});
			if (response.user.role === "ADMIN") {
				navigate("/admin/");
			} else {
				navigate("/user/");
			}
		} catch (err: unknown) {
			if (err instanceof Error) setError(err.message);
			else setError("An unexpected error occurred. Please try again.");
			console.log(error);
		} finally {
			setLoading(false);
		}
	};

	const handleSocialLogin = async () => {};

	return (
		<div className="login">
			{/* Modal for Register */}
			{showModal && (
				<div className="modal">
					<div className="content">
						<span className="close" onClick={() => setShowModal(false)}>
							&times;
						</span>
						<h2 className="title">Create WaveLink account</h2>
						<form onSubmit={handleRegister}>
							<div className="input-group">
								<label>Your email</label>
								<input
									type="text"
									name="email"
									value={registerForm.email}
									onChange={handleRegisterChange}
									placeholder="Enter your email"
									required
								/>
							</div>
							<div className="input-group password">
								<label>Your password </label>
								<input
									type={registerForm.showPassword ? "text" : "password"}
									name="password"
									value={registerForm.password}
									onChange={handleRegisterChange}
									placeholder="Enter your password"
									required
								/>
								<button
									type="button"
									className="toggle"
									onClick={toggleRegisterPassword}
								>
									{registerForm.showPassword ? <Eye /> : <EyeOff />}
								</button>
							</div>
							<div className="input-group">
								<label>Your name</label>
								<input
									type="text"
									name="fullName"
									value={registerForm.fullName}
									onChange={handleRegisterChange}
									placeholder="Enter your full name"
								/>
							</div>
							<div className="input-group">
								<label>Your age</label>
								<input
									type="number"
									name="age"
									value={registerForm.age}
									onChange={handleRegisterChange}
									placeholder="Enter your age"
								/>
							</div>
							<button type="submit" className="send-btn">
								Send
							</button>
						</form>
					</div>
				</div>
			)}
			{/* Left Panel */}
			<div className="left">
				<span className="text">
					Let’s Explore <br />
					the world together.
					<br />
					<button
						className="join-btn"
						onClick={() => {
							document.getElementById("email")?.focus();
						}}
					>
						Join now!
					</button>
				</span>
				<img src={bg} alt="Login" className="image" />
			</div>

			{/* Right Panel */}
			<div className="right">
				<div className="form-container">
					<span className="title">Login</span>
					<form className="form" onSubmit={handleLogin}>
						<div className="input-group">
							<label htmlFor="email">Email</label>
							<input
								type="text"
								name="email"
								value={loginForm.email}
								onChange={handleLoginChange}
								placeholder="Enter your email"
								required
							/>
						</div>
						<div className="input-group password">
							<label htmlFor="password">Password</label>
							{/* <button
								onClick={(e) => {
									e.preventDefault();
									setShowModalFP(true);
								}}
								className="forgot-btn"
							>
								forgot password ?
							</button> */}

							<input
								type={loginForm.showPassword ? "text" : "password"}
								name="password"
								value={loginForm.password}
								onChange={handleLoginChange}
								placeholder="Enter your password"
								required
							/>
							<button
								type="button"
								className="toggle"
								onClick={toggleLoginPassword}
							>
								{loginForm.showPassword ? <Eye /> : <EyeOff />}
							</button>
						</div>
						<button type="submit" className="submit-btn">
							Login
						</button>
					</form>

					<div className="divider">
						<span>Easy create account with</span>
					</div>

					<div className="socials">
						<button
							className="social-btn google"
							onClick={() => handleSocialLogin()}
						>
							<img src={gg} alt="Google" />
							Google
						</button>
						<button
							className="social-btn wavelink"
							onClick={() => setShowModal(true)}
						>
							<img src={logo} alt="" />
						</button>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Login;
