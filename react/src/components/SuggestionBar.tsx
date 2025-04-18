import "@/styles/components/_suggestionBar.scss"; // Import your CSS file here
import { UserPlus } from "lucide-react";

type Suggestion = {
	name: string;
	avatar: string;
	direct: string;
};

const SuggestionBar = () => {
	const fakeData: Suggestion[] = [
		{
			name: "Anonymous",
			avatar: "http://example.com/400x400",
			direct: "/user/id_fake",
		},
		{
			name: "Anonymous",
			avatar: "http://example.com/400x400",
			direct: "/user/id_fake",
		},
		{
			name: "Anonymous",
			avatar: "http://example.com/400x400",
			direct: "/user/id_fake",
		},
		{
			name: "Anonymous",
			avatar: "http://example.com/400x400",
			direct: "/user/id_fake",
		},
	];

	return (
		<div className="suggestion-bar">
			<div className="label">
				<span>Suggested For You</span>
				<span className="sub-label">See all</span>
			</div>
			<div className="list">
				{fakeData.map((item, index) => (
					<div key={index} className="item">
						<img
							src={`https://avatar.iran.liara.run/public`}
							alt={item.name}
							className="avatar"
						/>
						<div className="info">
							<h3 className="name">{item.name}</h3>
							<p className="desc">Followed by 3 people you know</p>
						</div>
						<a href={item.direct} className="direct">
							<UserPlus />
						</a>
					</div>
				))}
			</div>
		</div>
	);
};

export default SuggestionBar;
