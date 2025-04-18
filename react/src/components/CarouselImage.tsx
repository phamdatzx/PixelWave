import { ChevronLeft, ChevronRight } from "lucide-react";
import React from "react";
import '@/styles/components/_carouselImage.scss'; // Import your CSS file for styling

type CarouselImageProps = {
	img_urls: string[];
};

const CarouselImage: React.FC<CarouselImageProps> = (props) => {
	const { img_urls } = props;

	const [currentImageIndex, setCurrentImageIndex] = React.useState<number>(0);

	const handleNextImage = () => {
		setCurrentImageIndex((prevIndex) =>
			prevIndex === img_urls.length - 1 ? 0 : prevIndex + 1
		);
	};

	const handlePreviousImage = () => {
		setCurrentImageIndex((prevIndex) =>
			prevIndex === 0 ? img_urls.length - 1 : prevIndex - 1
		);
	};

	const getImageClass = (url: string) => {
		const img = new Image();
		img.src = url;

		if (img.width > img.height) return "landscape";
		if (img.height > img.width) return "portrait";
		return "square";
	};

	return (
		<div className="post-images">
			<button className="prev-btn" onClick={handlePreviousImage}>
				<ChevronLeft />
			</button>
			<img
				src={img_urls[currentImageIndex]}
				alt={`Post Image ${currentImageIndex + 1}`}
				className={`post-image ${getImageClass(img_urls[currentImageIndex])}`}
			/>
			<button className="next-btn" onClick={handleNextImage}>
				<ChevronRight />
			</button>
		</div>
	);
};

export default CarouselImage;
