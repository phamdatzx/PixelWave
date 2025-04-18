export class Comment {
  public comment_id: string;
  public user_id: string;
  public post_id: string;
  public text: string | null;
  public img_url: string | null;
  public createAt: string;
  public updateAt: string;

  constructor(params: {
    comment_id: string;
    user_id: string;
    post_id: string;
    text?: string | null;
    img_url?: string | null;
    createAt?: string;
    updateAt?: string;
  }) {
    const { comment_id, user_id, post_id, text = null, img_url = null } = params;

    // Bắt lỗi nếu cả text và img_url đều null
    if (!text && !img_url) {
      throw new Error("At least one of text or img_url must be provided.");
    }

    const now = new Date().toISOString();

    this.comment_id = comment_id;
    this.user_id = user_id;
    this.post_id = post_id;
    this.text = text;
    this.img_url = img_url;
    this.createAt = params.createAt ?? now;
    this.updateAt = params.updateAt ?? now;
  }

  updateText(newText: string | null) {
    if (!newText && !this.img_url) {
      throw new Error("Comment must have at least text or an image.");
    }
    this.text = newText;
    this.updateAt = new Date().toISOString();
  }

  updateImage(newImageUrl: string | null) {
    if (!newImageUrl && !this.text) {
      throw new Error("Comment must have at least text or an image.");
    }
    this.img_url = newImageUrl;
    this.updateAt = new Date().toISOString();
  }
}
