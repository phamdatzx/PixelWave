from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from typing import List
from PIL import Image
import io
import torch
from transformers import CLIPProcessor, CLIPModel
import uvicorn

app = FastAPI(title="Image Auto-Tagging API")

# Load CLIP model and processor
model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32")
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

# Predefined list of tags for an Instagram-like platform
possible_tags = [
    "person", "selfie", "group", "couple", "child", "baby", "portrait", "smile", 
    "fashion", "fitness", "dancing", "sports", "travel", "party", "wedding",
    "dog", "cat", "bird", "fish", "horse", "rabbit", "pet", "animal", "puppy", "kitten",
    "tree", "flower", "sky", "cloud", "sunset", "sunrise", "beach", "ocean", 
    "mountain", "forest", "river", "lake", "grass", "snow", "rain",
    "city", "building", "house", "street", "car", "vehicle", "bridge", "skyscraper", 
    "road", "park",
    "food", "coffee", "dessert", "pizza", "sushi", "cake", "drink", "breakfast", 
    "dinner", "fruit",
    "phone", "camera", "book", "bag", "jewelry", "art", "music", "instrument", 
    "bicycle", "boat",
    "night", "day", "indoor", "outdoor", "bright", "dark", "colorful", "vintage", 
    "minimal", "urban"
]

# Define tag hierarchy (specific tags imply general tags)
tag_hierarchy = {
    "dog": ["animal", "pet"],
    "cat": ["animal", "pet"],
    "bird": ["animal"],
    "fish": ["animal"],
    "horse": ["animal"],
    "rabbit": ["animal"],
    "puppy": ["animal", "pet", "dog"],
    "kitten": ["animal", "pet", "cat"],
    "car": ["vehicle"],
    "bicycle": ["vehicle"],
    "boat": ["vehicle"],
    "pizza": ["food"],
    "sushi": ["food"],
    "cake": ["food", "dessert"],
    "coffee": ["drink"],
    "breakfast": ["food"],
    "dinner": ["food"],
    "fruit": ["food"],
    "selfie": ["person"],
    "group": ["person"],
    "couple": ["person"],
    "child": ["person"],
    "baby": ["person"],
    "portrait": ["person"]
}

# Pydantic model for tag response
class TagResponse(BaseModel):
    filename: str
    tags: List[str]

# Function to generate tags using CLIP
def generate_tags(image: Image.Image) -> List[str]:
    # Prepare image for CLIP
    inputs = processor(
        text=possible_tags,
        images=image,
        return_tensors="pt",
        padding=True
    )
    
    # Get model outputs
    with torch.no_grad():
        outputs = model(**inputs)
    
    # Get probabilities for each tag
    logits_per_image = outputs.logits_per_image
    probs = logits_per_image.softmax(dim=1).cpu().numpy()[0]
    
    # Select tags with probability above threshold
    threshold = 0.1
    tags = [possible_tags[i] for i, prob in enumerate(probs) if prob > threshold]
    
    # Add implied tags from hierarchy
    final_tags = set(tags)
    for tag in tags:
        if tag in tag_hierarchy:
            final_tags.update(tag_hierarchy[tag])
    
    return list(final_tags) if final_tags else ["unknown"]

# Endpoint to upload an image and generate tags
@app.post("/upload/", response_model=TagResponse)
async def upload_image(file: UploadFile = File(...)):
    # Validate file is an image
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File must be an image")
    
    # Read image
    content = await file.read()
    try:
        image = Image.open(io.BytesIO(content)).convert("RGB")
    except Exception:
        raise HTTPException(status_code=400, detail="Invalid image file")
    
    # Generate tags
    tags = generate_tags(image)
    
    return TagResponse(
        filename=file.filename,
        tags=tags
    )

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)