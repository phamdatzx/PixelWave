// src/models/Post.ts
export class Post {
  public post_id: string;
  public user_id: string;
  public post_description: string;
  public img_urls: string[];
  public reacted: boolean;
  public likes: number;
  public comment_ids: string[];
  public createAt: string;
  public updateAt: string;

  constructor(params: {
    post_id: string;
    user_id: string;
    post_description: string;
    img_urls?: string[];
    reacted?: boolean;
    likes?: number;
    comment_ids?: string[];
    createAt?: string;
    updateAt?: string;
  }) {
    const now = new Date().toISOString();

    this.post_id = params.post_id;
    this.user_id = params.user_id;
    this.post_description = params.post_description;
    this.img_urls = params.img_urls ?? [];
    this.reacted = params.reacted ?? false;
    this.likes = params.likes ?? 0;
    this.comment_ids = params.comment_ids ?? [];
    this.createAt = params.createAt ?? now;
    this.updateAt = params.updateAt ?? now;
  }

  // Optional: utility methods to update state
  addLike(): void {
    this.likes++;
    this.reacted = true;
    this.updateAt = new Date().toISOString();
  }

  removeLike(): void {
    if (this.likes > 0) this.likes--;
    this.reacted = false;
    this.updateAt = new Date().toISOString();
  }

  addComment(commentId: string): void {
    this.comment_ids.push(commentId);
    this.updateAt = new Date().toISOString();
  }
}
