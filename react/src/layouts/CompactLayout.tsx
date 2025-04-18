// src/layouts/CompactLayout.tsx
import Navbar from "../components/Navbar";
// import Sidebar from "../components/Sidebar/ShortSidebar";

const CompactLayout = ({ children }: { children: React.ReactNode }) => {
	return (
		<div className="flex flex-col min-h-screen">
			<Navbar />
			<div className="flex flex-1">
				{/* <Sidebar /> */}
				<main className="flex-1 p-4">{children}</main>
			</div>
		</div>
	);
};

export default CompactLayout;
