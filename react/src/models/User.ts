export interface User {
  id: number;
  username: string;
  password: string;
  name: string;
  phone_number?: string;
  age: number;
  gender: 'male' | 'female' | 'other'; // hoặc string nếu bạn muốn linh hoạt hơn
  role: string;
  is_activated: boolean;

  // Relations
  user_friends: User[];                     // danh sách bạn bè (2 chiều)
  follower: User[];                   // người theo dõi bạn (1 chiều)
  following: User[];                   // bạn đang theo dõi ai (1 chiều)
  user_blocked: User[];              // bạn đã chặn ai
  friend_requests_sent: User[];       // bạn đã gửi yêu cầu kết bạn đến ai
  friend_requests_received: User[];   // bạn nhận yêu cầu từ ai 
}
