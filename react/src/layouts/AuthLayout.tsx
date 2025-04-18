// src/layouts/AuthLayout.tsx
type Props = { children: React.ReactNode };

const AuthLayout = ({ children }: Props) => {
	return <div className="h-screen w-screen">{children}</div>;
};

export default AuthLayout;
