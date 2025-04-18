import React, { useEffect } from "react";
import { Post as PostModel } from "@/models/Post";
import Post from "@/components/Post.tsx";
import PostDetails from "@/components/PostDetails";
import fakePosts from "@/fakeData/fakePost.json";

import "@/styles/pages/_home.scss"; // Import your CSS file

const Home: React.FC = () => {
	const [posts, setPosts] = React.useState<PostModel[]>([]);
	const [postShow, setPosShow] = React.useState<string | null>(null);

	useEffect(() => {
		// Parse JSON và chuyển thành instance của PostModel
		const loadedPosts: PostModel[] = fakePosts.map((post: JSON) => new PostModel(post));
		setPosts(loadedPosts);
		console.log("Loaded posts:", loadedPosts);
		
	}, []);

	const viewPostDetails = (post: PostModel) => {
		setPosShow(post);
	};
	const closePostDetails = () => {
		setPosShow(null);
	};

	return (
		<div className="home">
			{postShow ? (
				<PostDetails post={postShow} onClose={closePostDetails} />
			) : null}
			{posts.map((post) => (
				<Post
					key={post.post_id}
					post={post}
					showDetails={viewPostDetails}
				/>
			))}
		</div>
	);
};

export default Home;
