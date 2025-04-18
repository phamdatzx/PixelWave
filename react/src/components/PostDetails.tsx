import React, { useEffect, useState } from "react";
import { CircleX, Share2 } from "lucide-react";

import { Post as PostModel } from "@/models/Post";
import { Comment as CommentModel } from "@/models/Comment"; // Import the CommentType model
import CarouselImage from "./CarouselImage";

import "@/styles/components/_postDetail.scss";

type PostDetailsProps = {
	post: PostModel | null; // Sử dụng model Post
	onClose: () => void; // Hàm để đóng modal
};

const PostDetails: React.FC<PostDetailsProps> = ({ post, onClose }) => {
	const {
		user_name,
		user_avatar,
		post_description,
		img_urls,
		reacted,
		likes,
		comments,
		createAt,
	} = post;

	const [isReacted, setIsReacted] = useState(false);

	// State to track if the post is reacted
	useEffect(() => {
		setIsReacted(reacted); // Set the initial state based on the reacted prop
	}, [reacted]);

	return (
		<div className="post-details-modal">
			<div className="modal-content">
				<button className="close-btn" onClick={onClose}>
					<CircleX />
				</button>
				<div className="post-body">
					<div className="post-header">
						<img src={user_avatar} alt={user_name} className="user-avatar" />
						<div className="user-data">
							<span className="user-name">{user_name}</span>
							<span className="timestamp">{createAt}</span>
						</div>
					</div>
					<p className="post-description">{post_description}</p>
					<CarouselImage img_urls={img_urls} />
					<div className="post-actions">
						<button
							className="like-btn"
							onClick={() => setIsReacted(!isReacted)}
						>
							{isReacted ? (
								<svg
									xmlns="http://www.w3.org/2000/svg"
									width="24px"
									height="22px"
									viewBox="0 0 36 40"
								>
									<path
										fill="#ff0000"
										d="M35.885 11.833c0-5.45-4.418-9.868-9.867-9.868c-3.308 0-6.227 1.633-8.018 4.129c-1.791-2.496-4.71-4.129-8.017-4.129c-5.45 0-9.868 4.417-9.868 9.868c0 .772.098 1.52.266 2.241C1.751 22.587 11.216 31.568 18 34.034c6.783-2.466 16.249-11.447 17.617-19.959c.17-.721.268-1.469.268-2.242"
										stroke-width="4"
										stroke="#ff0000"
									/>
								</svg>
							) : (
								<svg
									xmlns="http://www.w3.org/2000/svg"
									width="24px"
									height="22px"
									viewBox="0 0 36 40"
								>
									<path
										fill="#fff"
										d="M35.885 11.833c0-5.45-4.418-9.868-9.867-9.868c-3.308 0-6.227 1.633-8.018 4.129c-1.791-2.496-4.71-4.129-8.017-4.129c-5.45 0-9.868 4.417-9.868 9.868c0 .772.098 1.52.266 2.241C1.751 22.587 11.216 31.568 18 34.034c6.783-2.466 16.249-11.447 17.617-19.959c.17-.721.268-1.469.268-2.242"
										stroke-width="4"
										stroke="#000"
									/>
								</svg>
							)}
							<span>{likes}</span>
						</button>
						<button className="share-btn">
						<Share2 />
						</button>
					</div>
				</div>
				<div className="post-comments">
					<h3>Comments</h3>
					{comments.map((comment: CommentModel) => (
						<div key={comment.id} className="comment">
							<span className="comment-user">{comment.user}:</span>
							<span className="comment-text">{comment.text}</span>
						</div>
					))}
				</div>
			</div>
		</div>
	);
};

export default PostDetails;
