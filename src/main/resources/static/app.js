let stompClient = null;
let username = localStorage.getItem("username") || "";
let roomId = "";

/* ===============================
   THEME MANAGEMENT
================================= */
function loadTheme() {
    const savedTheme = localStorage.getItem("theme") || "dark";
    document.documentElement.setAttribute("data-theme", savedTheme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute("data-theme");
    const newTheme = currentTheme === "dark" ? "light" : "dark";

    document.documentElement.setAttribute("data-theme", newTheme);
    localStorage.setItem("theme", newTheme);
}

/* ===============================
   DOM REFERENCES
================================= */
const joinCard = document.getElementById("joinCard");
const chatCard = document.getElementById("chatCard");

const logoutBtn = document.getElementById("logoutBtn");
const leaveBtn = document.getElementById("leaveBtn");
const sendBtn = document.getElementById("sendBtn");

const createRoomBtn = document.getElementById("createRoomBtn");
const joinRoomBtn = document.getElementById("joinRoomBtn");

const createRoomIdInput = document.getElementById("createRoomId");
const createRoomNameInput = document.getElementById("createRoomName");
const createRoomPasswordInput = document.getElementById("createRoomPassword");

const joinRoomIdInput = document.getElementById("joinRoomId");
const joinRoomPasswordInput = document.getElementById("joinRoomPassword");

const roomMessage = document.getElementById("roomMessage");
const messageInput = document.getElementById("messageInput");

const messagesContainer = document.getElementById("messages");
const roomTitle = document.getElementById("roomTitle");
const userLabel = document.getElementById("userLabel");
const loggedInUser = document.getElementById("loggedInUser");

const themeToggleBtn = document.getElementById("themeToggleBtn");
const chatThemeToggleBtn = document.getElementById("chatThemeToggleBtn");

/* ===============================
   EVENT LISTENERS
================================= */
window.addEventListener("load", () => {
    loadTheme();
    initApp();
});

logoutBtn.addEventListener("click", logoutUser);
createRoomBtn.addEventListener("click", createRoom);
joinRoomBtn.addEventListener("click", joinRoom);
sendBtn.addEventListener("click", handleSendMessage);
leaveBtn.addEventListener("click", leaveChat);

if (themeToggleBtn) {
    themeToggleBtn.addEventListener("click", toggleTheme);
}

if (chatThemeToggleBtn) {
    chatThemeToggleBtn.addEventListener("click", toggleTheme);
}

messageInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
        handleSendMessage();
    }
});

/* ===============================
   APP INIT
================================= */
function initApp() {
    const token = localStorage.getItem("token");
    const savedUsername = localStorage.getItem("username");

    console.log("initApp", { tokenPresent: !!token, savedUsername });

    if (!token || !savedUsername) {
        window.location.href = "/login.html";
        return;
    }

    username = savedUsername;
    loggedInUser.textContent = `Logged in as ${username}`;
}

/* ===============================
   AUTH
================================= */
function logoutUser() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    window.location.href = "/login.html";
}

/* ===============================
   ROOM ACTIONS
================================= */
async function createRoom() {
    const token = localStorage.getItem("token");
    const roomIdValue = createRoomIdInput.value.trim();
    const roomNameValue = createRoomNameInput.value.trim();
    const roomPasswordValue = createRoomPasswordInput.value.trim();

    if (!roomIdValue || !roomPasswordValue) {
        roomMessage.textContent = "Room ID and password are required.";
        return;
    }

    try {
        const response = await fetch("/api/rooms/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                roomId: roomIdValue,
                roomName: roomNameValue,
                password: roomPasswordValue
            })
        });

        const text = await response.text();

        if (!response.ok) {
            roomMessage.textContent = text || "Failed to create room.";
            return;
        }

        roomMessage.textContent = "Room created successfully. Joining now...";
        roomId = roomIdValue;

        createRoomIdInput.value = "";
        createRoomNameInput.value = "";
        createRoomPasswordInput.value = "";

        await openChatRoom();
    } catch (error) {
        console.error("Create room error:", error);
        roomMessage.textContent = "Failed to create room.";
    }
}

async function joinRoom() {
    const roomIdValue = joinRoomIdInput.value.trim();
    const roomPasswordValue = joinRoomPasswordInput.value.trim();

    console.log("Join Room button clicked", { roomIdValue });

    if (!roomIdValue || !roomPasswordValue) {
        roomMessage.textContent = "Room ID and password are required.";
        return;
    }

    try {
        const response = await fetch("/api/rooms/join", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                roomId: roomIdValue,
                password: roomPasswordValue
            })
        });

        console.log("Join response status:", response.status);

        const text = await response.text();
        console.log("Join response body:", text);

        let data;
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }

        if (!response.ok) {
            roomMessage.textContent =
                typeof data === "string" ? data : "Failed to join room.";
            return;
        }

        roomMessage.textContent = "";
        roomId = roomIdValue;

        console.log("Room verified, opening chat room:", roomId);

        joinRoomIdInput.value = "";
        joinRoomPasswordInput.value = "";

        await openChatRoom();
    } catch (error) {
        console.error("Join room error:", error);
        roomMessage.textContent = "Failed to join room.";
    }
}

async function openChatRoom() {
    console.log("Opening chat room UI for:", roomId);

    joinCard.classList.add("hidden");
    chatCard.classList.remove("hidden");

    roomTitle.textContent = `Room: ${roomId}`;
    userLabel.textContent = `Logged in as ${username}`;

    await loadChatHistory();
    connectWebSocket();
}

/* ===============================
   CHAT HISTORY
================================= */
async function loadChatHistory() {
    messagesContainer.innerHTML = "";
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(`/api/messages/${roomId}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            console.error("Failed to load chat history. Status:", response.status);
            return;
        }

        const messages = await response.json();

        messages.forEach((msg) => {
            if (msg.recipient) {
                if (msg.sender === username || msg.recipient === username) {
                    renderPrivateMessage(msg);
                }
            } else {
                renderMessage(msg);
            }
        });

        scrollToBottom();
    } catch (error) {
        console.error("Failed to load chat history:", error);
    }
}

/* ===============================
   WEBSOCKET
================================= */
function connectWebSocket() {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect(
        {},
        function () {
            console.log("WebSocket connected");

            stompClient.subscribe(`/topic/room/${roomId}`, function (message) {
                const chatMessage = JSON.parse(message.body);
                renderMessage(chatMessage);
                scrollToBottom();
            });

            stompClient.subscribe(`/topic/private/${username}`, function (message) {
                const chatMessage = JSON.parse(message.body);
                renderPrivateMessage(chatMessage);
                scrollToBottom();
            });

            stompClient.send(
                "/app/chat.addUser",
                {},
                JSON.stringify({
                    sender: username,
                    roomId: roomId,
                    type: "JOIN"
                })
            );
        },
        function (error) {
            console.error("WebSocket connection error:", error);
        }
    );
}

/* ===============================
   MESSAGING
================================= */
function handleSendMessage() {
    const content = messageInput.value.trim();

    if (!content || !stompClient) {
        return;
    }

    if (content.startsWith("@")) {
        const firstSpace = content.indexOf(" ");

        if (firstSpace > 1) {
            const recipient = content.substring(1, firstSpace).trim();
            const privateText = content.substring(firstSpace + 1).trim();

            if (recipient && privateText) {
                sendPrivateMessage(recipient, privateText);
                messageInput.value = "";
                return;
            }
        }
    }

    const chatMessage = {
        sender: username,
        content: content,
        roomId: roomId,
        type: "CHAT"
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    messageInput.value = "";
}

function sendPrivateMessage(recipient, content) {
    if (!content || !recipient || !stompClient) return;

    const chatMessage = {
        sender: username,
        recipient: recipient,
        content: content,
        roomId: roomId,
        type: "CHAT"
    };

    stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
}

/* ===============================
   MESSAGE RENDERING
================================= */
function renderMessage(message) {
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("message");

    if (message.type === "JOIN" || message.type === "LEAVE") {
        messageDiv.classList.add("system");
        messageDiv.innerHTML = `<div class="text">${escapeHtml(message.content)}</div>`;
        messagesContainer.appendChild(messageDiv);
        return;
    }

    if (message.sender === username) {
        messageDiv.classList.add("self");
    }

    const time = message.timestamp
        ? new Date(message.timestamp).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit"
          })
        : "";

    messageDiv.innerHTML = `
        <div class="meta">${escapeHtml(message.sender)} ${time ? "• " + time : ""}</div>
        <div class="text">${escapeHtml(message.content)}</div>
    `;

    messagesContainer.appendChild(messageDiv);
}

function renderPrivateMessage(message) {
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("message", "private");

    const time = message.timestamp
        ? new Date(message.timestamp).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit"
          })
        : "";

    const label =
        message.sender === username
            ? `Private to ${message.recipient}`
            : `Private from ${message.sender}`;

    messageDiv.innerHTML = `
        <div class="meta">${escapeHtml(label)} ${time ? "• " + time : ""}</div>
        <div class="text">${escapeHtml(message.content)}</div>
    `;

    messagesContainer.appendChild(messageDiv);
}

/* ===============================
   LEAVE CHAT
================================= */
function leaveChat() {
    if (stompClient) {
        stompClient.send(
            "/app/chat.leaveUser",
            {},
            JSON.stringify({
                sender: username,
                roomId: roomId,
                type: "LEAVE"
            })
        );

        setTimeout(() => {
            stompClient.disconnect(() => {
                console.log("Disconnected");
            });
            stompClient = null;
        }, 200);
    }

    chatCard.classList.add("hidden");
    joinCard.classList.remove("hidden");
    messagesContainer.innerHTML = "";
    messageInput.value = "";
    roomMessage.textContent = "";
}

/* ===============================
   HELPERS
================================= */
function scrollToBottom() {
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.innerText = text;
    return div.innerHTML;
}