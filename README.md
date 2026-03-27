📌 Real-Time Chat Application

A full-stack real-time chat application with authentication, private messaging, and room-based communication built using Spring Boot, WebSockets, and MongoDB.

🚀 Features
🔐 User Authentication (JWT)
        Register & Login
        Secure password handling
        Token-based session management
🏠 Room-Based Chat
        Create rooms with ID + password
        Join rooms securely
        Multiple users per room
💬 Real-Time Messaging
        WebSocket (STOMP over SockJS)
        Instant message delivery
        Broadcast messages within room
👥 User Presence
        Join/Leave notifications
        System-generated events
🔒 Private Messaging
        Send messages using @username
        One-to-one communication
🗄️ Chat Persistence
        Messages stored in MongoDB
        Load chat history on join
🎨 Modern UI
        Clean chat interface
        Dark/Light theme toggle
        Responsive design
🏗️ Tech Stack
        Backend
        Java 21
        Spring Boot 3
        Spring WebSocket (STOMP)
        Spring Security (JWT)
        MongoDB
        Frontend
        HTML5
        CSS3 (Custom UI)
        Vanilla JavaScript
        SockJS + STOMP
        Tools
        Maven
        Git & GitHub
        VS Code / Eclipse
⚙️ Project Structure
src/
 └── main/
     ├── java/com/example/chatapp/
     │   ├── config/
     │   ├── controller/
     │   ├── dto/
     │   ├── model/
     │   ├── repository/
     │   ├── security/
     │   └── service/
     │
     └── resources/
         ├── static/
         │   ├── index.html
         │   ├── login.html
         │   ├── register.html
         │   ├── app.js
         │   └── style.css
         │
         └── application.properties


🔄 How It Works
1. Authentication Flow
        User → Login/Register → JWT Token → Stored in Browser
2. Room Flow
        Create Room → Join Room → Validate Password → Enter Chat
3. Real-Time Messaging
        Client → WebSocket → Server → Broadcast → All Users
4. Private Messaging
        @username message → Routed to specific user


🔌 WebSocket Endpoints
        Action	Endpoint
        Connect	/ws
        Send Message	/app/chat.sendMessage
        Private Message	/app/chat.privateMessage
        Join	/app/chat.addUser
        Leave	/app/chat.leaveUser
        Subscriptions
        /topic/room/{roomId}
        /topic/private/{username}


🗄️ Database Schema
ChatMessage
{
  "sender": "xxxx",
  "recipient": "yyyy",
  "content": "hello",
  "roomId": "java-room",
  "type": "CHAT",
  "timestamp": "2026-03-24T19:58:33.274"
}


🛠️ Setup & Run
1. Clone Repository
        git clone https://github.com/Madan686/RealTime-ChatApp.git
        cd RealTime-ChatApp
2. Start MongoDB
        mongod --dbpath C:\projects\realtime-chat-app\data\db
3. Configure application.properties
        spring.application.name=chatapp
        server.port=8080

        spring.data.mongodb.uri=mongodb://localhost:27017/chatappdb
4. Run Application
        mvn spring-boot:run
5. Open in Browser
        http://localhost:8080/login.html


🌐 Deployment
        Backend
        Railway / Render
        Database
        MongoDB Atlas
        Env Variables
        SPRING_DATA_MONGODB_URI=<your_atlas_url>
        PORT=8080


💡 Future Improvements
        WebSocket JWT authentication (secure handshake)
        Online/offline user status
        Message read receipts
        Typing indicators
        File/image sharing
        Group chat enhancements
        Notifications system




## Live Demo (Railway)
[Open the app](https://realtime-chatapp-production-2616.up.railway.app/login.html)


## Live Demo (Render)
[Open the app](https://realtime-chat-app-sdhk.onrender.com/login.html)