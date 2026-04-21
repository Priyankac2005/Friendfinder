const API = 'http://localhost:8085/api';
let currentUser = null;

// UI State Management
function switchTab(tab) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
    
    if (tab === 'login') {
        document.querySelectorAll('.tab')[0].classList.add('active');
        document.getElementById('login-form').classList.add('active');
    } else if (tab === 'register') {
        document.querySelectorAll('.tab')[1].classList.add('active');
        document.getElementById('register-form').classList.add('active');
    } else if (tab === 'admin') {
        document.querySelectorAll('.tab')[2].classList.add('active');
        document.getElementById('admin-form').classList.add('active');
    }
}

function showMessage(msg, color = '#10b981') {
    const el = document.getElementById('auth-message');
    el.style.color = color;
    el.innerText = msg;
}

// Authentication
async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const res = await fetch(`${API}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        
        if (res.ok) {
            showMessage("Registration successful! You can now log in.");
            document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
            switchTab('login');
        } else {
            const errorObj = await res.json();
            showMessage(errorObj.message || "Registration Failed", '#ef4444');
        }
    } catch (err) { showMessage("Registration Failed", '#ef4444'); }
}

async function handleOtp(e) {
    e.preventDefault();
    const email = document.getElementById('otp-email').value;
    const otp = document.getElementById('otp-code').value;

    try {
        const res = await fetch(`${API}/auth/verify-otp`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, otp })
        });
        if (res.ok) {
            showMessage("Verified! Please log in.");
            document.querySelector('.tabs').style.display = 'flex';
            switchTab('login');
        } else {
            showMessage("Invalid OTP", '#ef4444');
        }
    } catch (err) { showMessage("Error verifying", '#ef4444'); }
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (res.ok) {
            currentUser = await res.json();
            document.getElementById('auth-container').classList.remove('active');
            document.getElementById('auth-container').classList.add('hidden');
            document.getElementById('dashboard-container').classList.remove('hidden');
            document.getElementById('dashboard-container').classList.add('active');
            loadDashboard();
        } else {
            showMessage("Invalid Credentials or not verified", '#ef4444');
        }
    } catch (err) { showMessage("Login Failed", '#ef4444'); }
}

function logout() {
    currentUser = null;
    document.getElementById('dashboard-container').classList.remove('active');
    document.getElementById('dashboard-container').classList.add('hidden');
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('auth-container').classList.add('active');
}

// ------------------------------------------
// ADMIN CONSOLIDATION
// ------------------------------------------

async function handleAdminLogin(e) {
    e.preventDefault();
    const email = document.getElementById('admin-email').value;
    const password = document.getElementById('admin-password').value;

    if (email === 'admin@test.com' && password === 'admin') {
        document.getElementById('auth-container').classList.remove('active');
        document.getElementById('auth-container').classList.add('hidden');
        document.getElementById('admin-container').classList.remove('hidden');
        document.getElementById('admin-container').classList.add('active');
        loadAdminUsers();
    } else {
        showMessage("Invalid Admin Credentials", '#ef4444');
    }
}

function logoutAdmin() {
    document.getElementById('admin-container').classList.remove('active');
    document.getElementById('admin-container').classList.add('hidden');
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('auth-container').classList.add('active');
}

const ADMIN_API = '/api/admin';

async function loadAdminUsers() {
    try {
        const res = await fetch(`${ADMIN_API}/users`);
        const users = await res.json();
        
        const reportedUsers = users.filter(u => u.reportCount > 0);
        
        if (reportedUsers.length > 0) {
            document.getElementById('admin-notifications-section').style.display = 'block';
            document.getElementById('admin-notifications-section').innerHTML = reportedUsers.map(u => `
                <div style="padding: 15px; margin-bottom: 10px; background: rgba(239, 68, 68, 0.1); border-left: 4px solid #ef4444; border-radius: 5px; display: flex; justify-content: space-between; align-items: center;">
                    <span style="color: white; font-size: 1rem;">🚨 <strong>User Alert:</strong> ${u.name} (<span style="color:#fca5a5;">${u.email}</span>) has been reported <strong>${u.reportCount} time(s)!</strong></span>
                    <button class="primary-btn" style="background:#ef4444; border:none; padding: 8px 15px; margin: 0;" onclick="deleteAdminUser(${u.id})">Delete User</button>
                </div>
            `).join('');
        } else {
            document.getElementById('admin-notifications-section').style.display = 'none';
        }

        const grid = document.getElementById('admin-user-grid');
        grid.innerHTML = users.map(u => `
            <div class="user-card" style="flex-direction: column; align-items: center; gap: 10px; padding: 20px;">
                <img src="${u.profilePicture || 'https://ui-avatars.com/api/?name=' + u.name.replace(/'/g, "\\'") + '&background=random'}" alt="User Image" style="width:100px; height:100px; border-radius:50%; object-fit:cover; border:2px solid var(--primary);">
                <h3 style="margin:5px 0;">${u.name}</h3>
                <p style="margin:0; color:#94a3b8; font-size:0.9rem;">${u.email}</p>
                <div class="action-btns" style="width:100%; display:flex; gap:10px; margin-top:15px;">
                    <button class="secondary-btn" style="flex:1; background:#f59e0b; color:black; border:none;" onclick="deleteAdminImage(${u.id})">Del Image</button>
                    <button class="primary-btn" style="flex:1; background:#ef4444; border:none;" onclick="deleteAdminUser(${u.id})">Del User</button>
                </div>
            </div>
        `).join('');
    } catch(e) {
        alert("Error loading admin users.");
    }
}

async function deleteAdminImage(userId) {
    if(!confirm("Are you sure you want to delete this user's image?")) return;
    try {
        const res = await fetch(`${ADMIN_API}/users/${userId}/image`, { method: 'DELETE' });
        if(res.ok) loadAdminUsers();
    } catch(e) { alert("Error deleting image."); }
}

async function deleteAdminUser(userId) {
    if(!confirm("Are you sure you want to permanently delete this user?")) return;
    try {
        const res = await fetch(`${ADMIN_API}/users/${userId}`, { method: 'DELETE' });
        if(res.ok) loadAdminUsers();
    } catch(e) { alert("Error deleting user."); }
}


// Dashboard Logic
function loadDashboard() {
    document.getElementById('user-name-display').innerText = currentUser.name;
    document.getElementById('my-activity-score').innerText = currentUser.activityScore || 0;
    if (currentUser.profilePicture) {
        document.getElementById('my-profile-pic').src = currentUser.profilePicture;
    }
    renderInterests(currentUser.interests);
    fetchSuggestions();
    fetchPendingRequests();
    fetchFriends();
    fetchLeaderboard();
    
    // Live notification polling mechanism
    if (window.pendingInterval) clearInterval(window.pendingInterval);
    window.pendingInterval = setInterval(() => {
        fetchPendingRequests();
        fetchFriends(); // Auto-refresh unread message badges
    }, 3000);
}

async function fetchFriends() {
    const res = await fetch(`${API}/friends/list?userId=${currentUser.id}`);
    if (res.ok) {
        let friends = await res.json();
        
        for(let f of friends) {
            f.unreadCount = 0;
            try {
                const msgRes = await fetch(`${API}/messages/history?user1Id=${currentUser.id}&user2Id=${f.id}`);
                if(msgRes.ok) {
                    const messages = await msgRes.json();
                    const receivedMsgs = messages.filter(m => m.receiverId === currentUser.id);
                    const lastReadCount = parseInt(localStorage.getItem(`chat_${currentUser.id}_${f.id}`) || 0);
                    
                    if (activeChatFriendId === f.id) {
                        localStorage.setItem(`chat_${currentUser.id}_${f.id}`, receivedMsgs.length);
                    } else {
                        f.unreadCount = Math.max(0, receivedMsgs.length - lastReadCount);
                    }
                }
            } catch(e) {}
        }

        const div = document.getElementById('my-friends');
        if (friends.length === 0) div.innerHTML = '<p class="subtitle fade" style="font-size:0.8rem">No friends yet.</p>';
        else {
            div.innerHTML = friends.map(f => {
                const badgeHtml = f.unreadCount > 0 
                    ? `<span style="background: red; color: white; border-radius: 50%; padding: 2px 6px; position:absolute; top:-5px; right:-5px; font-size: 0.65rem; font-weight: bold; box-shadow: 0 0 5px rgba(0,0,0,0.5);">${f.unreadCount}</span>` 
                    : '';
                return `
                <div class="user-card" style="padding: 10px; position:relative;">
                    <div class="user-info" style="display:flex; align-items:center; gap: 8px;">
                        ${f.profilePicture ? `<img src="${f.profilePicture}" style="width:25px;height:25px;border-radius:50%;object-fit:cover;">` : '👤'}
                        <div style="position:relative;">
                            <strong>${f.name}</strong>
                            ${badgeHtml}
                        </div>
                    </div>
                    <div class="action-btns">
                        <button class="primary-btn small" style="margin:0; padding:4px 10px; font-size:0.8rem; border-radius:5px;" onclick="openChat(${f.id}, '${f.name.replace(/'/g, "\\'")}', '${f.profilePicture || ''}')">Chat</button>
                        <button class="btn-decline" onclick="removeActiveFriend(${f.id})" title="Remove Friend">×</button>
                    </div>
                </div>
            `}).join('');
        }
    }
}

async function removeActiveFriend(friendId) {
    if(!confirm('Are you sure you want to remove this friend?')) return;
    await fetch(`${API}/friends/remove?userId=${currentUser.id}&friendId=${friendId}`, { method: 'DELETE' });
    fetchFriends();
}

async function handleAddInterest(e) {
    e.preventDefault();
    const interest = document.getElementById('new-interest').value;
    
    const res = await fetch(`${API}/interests/add?userId=${currentUser.id}&interestName=${interest}`, { method: 'POST' });
    if (res.ok) {
        currentUser = await res.json();
        renderInterests(currentUser.interests);
        document.getElementById('new-interest').value = '';
        fetchSuggestions(); // Refresh suggestions based on new interest!
    }
}

async function removeInterest(interest) {
    const res = await fetch(`${API}/interests/remove?userId=${currentUser.id}&interestName=${interest}`, { method: 'DELETE' });
    if (res.ok) {
        currentUser = await res.json();
        renderInterests(currentUser.interests);
        fetchSuggestions();
    }
}

function renderInterests(interests) {
    const div = document.getElementById('my-interests');
    div.innerHTML = '';
    interests.forEach(int => {
        div.innerHTML += `<div class="tag">${int} <span onclick="removeInterest('${int}')">×</span></div>`;
    });
}

// Matching & Request Logic
async function fetchSuggestions(keyword = "") {
    let url = `${API}/matches/suggest?userId=${currentUser.id}`;
    if (keyword.trim() !== "") {
        url = `${API}/matches/search?userId=${currentUser.id}&keyword=${encodeURIComponent(keyword)}`;
    }
    
    const res = await fetch(url);
    if (res.ok) {
        const users = await res.json();
        const div = document.getElementById('suggested-friends');
        if (users.length === 0) div.innerHTML = '<p class="subtitle" style="font-size:0.9rem">No matches found!</p>';
        else {
            div.innerHTML = users.map(u => `
                <div class="user-card" style="align-items: flex-start;">
                    <div class="user-info">
                        <strong style="font-size: 1.1rem; color: var(--primary);">${u.name}</strong>
                        <div class="match-score" style="margin-top: 5px; color: white;">⭐ Score: ${u.matchScore}</div>
                        <div style="font-size: 0.8rem; color: #a1a1aa; margin-top: 3px;">
                            ${u.sharedInterests.length > 0 ? `Shared Interests: ${u.sharedInterests.join(', ')} <br>` : ''}
                            ${u.mutualConnections > 0 ? `Mutual Friends: ${u.mutualConnections}` : ''}
                        </div>
                    </div>
                    <button class="secondary-btn" style="margin-top: 5px;" onclick="sendRequest(${u.id})">Add Friend</button>
                </div>
            `).join('');
        }
    }
}

let searchTimeout = null;
function handleSearch() {
    const keyword = document.getElementById('search-input').value;
    if (searchTimeout) clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        fetchSuggestions(keyword);
    }, 300);
}

async function fetchPendingRequests() {
    try {
        const res = await fetch(`${API}/friends/pending?userId=${currentUser.id}`);
        if (res.ok) {
            const requests = await res.json();
            const badge = document.getElementById('notif-badge');
            if (badge) {
                if (requests.length > 0) {
                    badge.innerText = requests.length;
                    badge.style.opacity = '1';
                } else {
                    badge.style.opacity = '0';
                }
            }

            const div = document.getElementById('pending-requests');
            if (!div) return;
            
            if (requests.length === 0) {
                div.innerHTML = '<p class="subtitle" style="font-size:0.9rem">No new requests.</p>';
            } else {
                div.innerHTML = requests.map(req => `
                    <div class="user-card">
                        <div class="user-info">
                            <strong>${req.sender.name}</strong> wants to connect!
                        </div>
                        <div class="action-btns">
                            <button class="btn-accept" onclick="acceptReq(${req.id})">✓</button>
                            <button class="btn-decline" onclick="rejectReq(${req.id})">×</button>
                        </div>
                    </div>
                `).join('');
            }
        }
    } catch (err) {
        console.error("DEBUG pending error:", err);
        const div = document.getElementById('pending-requests');
        if (div) div.innerHTML = `<p style="color:red">Error: ${err.message}</p>`;
    }
}

async function sendRequest(receiverId) {
    await fetch(`${API}/friends/request?senderId=${currentUser.id}&receiverId=${receiverId}`, { method: 'POST' });
    alert('Friend Request Sent!');
}

async function acceptReq(reqId) {
    await fetch(`${API}/friends/accept?requestId=${reqId}&receiverId=${currentUser.id}`, { method: 'POST' });
    fetchPendingRequests();
    fetchFriends();
    alert('Friend Accepted!');
}

async function rejectReq(reqId) {
    await fetch(`${API}/friends/reject?requestId=${reqId}&receiverId=${currentUser.id}`, { method: 'POST' });
    fetchPendingRequests();
}

// ==========================================
// Chat Logic
// ==========================================
let activeChatFriendId = null;
let chatInterval = null;

function openChat(friendId, friendName, profilePic) {
    activeChatFriendId = friendId;
    document.getElementById('chat-user-name').innerText = friendName;
    
    const picObj = document.getElementById('chat-user-pic');
    if (profilePic && profilePic !== 'undefined') {
        picObj.src = profilePic;
        picObj.style.display = 'block';
    } else {
        picObj.style.display = 'none';
    }
    
    document.getElementById('chat-modal').classList.remove('hidden');
    fetchChatHistory();
    // Poll for new messages
    if (chatInterval) clearInterval(chatInterval);
    chatInterval = setInterval(fetchChatHistory, 3000);
}

function closeChat() {
    activeChatFriendId = null;
    document.getElementById('chat-modal').classList.add('hidden');
    if (chatInterval) clearInterval(chatInterval);
}

async function fetchChatHistory() {
    if (!activeChatFriendId) return;
    try {
        const res = await fetch(`${API}/messages/history?user1Id=${currentUser.id}&user2Id=${activeChatFriendId}`);
        if (res.ok) {
            const messages = await res.json();
            const chatBox = document.getElementById('chat-messages');
            chatBox.innerHTML = '';
            
            messages.forEach(msg => {
                const isSentByMe = Boolean(msg.senderId == currentUser.id);
                const cls = isSentByMe ? 'sent' : 'received';
                
                let displayContent = msg.content;
                if (msg.content.startsWith('data:image/')) {
                    displayContent = `<img src="${msg.content}" style="max-width: 100%; border-radius: 10px; margin-top: 5px;">`;
                }
                
                chatBox.innerHTML += `<div class="chat-bubble ${cls}">${displayContent}</div>`;
            });
            chatBox.scrollTop = chatBox.scrollHeight;
        }
    } catch(err) { console.error("Error fetching chat", err); }
}

async function sendMessage(e) {
    e.preventDefault();
    if (!activeChatFriendId) return;
    
    let content = "";
    const input = document.getElementById('chat-input');
    
    if (base64ImagePending) {
        content = base64ImagePending;
    } else {
        content = input.value.trim();
    }
    
    if (!content) return;

    try {
        const res = await fetch(`${API}/messages/send?senderId=${currentUser.id}&receiverId=${activeChatFriendId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content })
        });
        
        if (res.ok) {
            input.value = '';
            input.placeholder = 'Type a message or send image...';
            document.getElementById('chat-image-input').value = "";
            base64ImagePending = null;
            currentUser.activityScore = (currentUser.activityScore || 0) + 5;
            document.getElementById('my-activity-score').innerText = currentUser.activityScore;
            fetchChatHistory();
        }
    } catch(err) { console.error("Error sending message", err); }
}

let base64ImagePending = null;

function handleImageSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(e) {
        base64ImagePending = e.target.result;
        document.getElementById('chat-input').value = "";
        document.getElementById('chat-input').placeholder = "[Image Attached - Press Send]";
    };
    reader.readAsDataURL(file);
}

// ==================== NEW FEATURES ==================== //

function uploadPfp(event) {
    const file = event.target.files[0];
    if(file) {
        const reader = new FileReader();
        reader.onload = async function() {
            const base64 = reader.result;
            const res = await fetch(`${API}/users/${currentUser.id}/upload-pfp`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ profilePicture: base64 })
            });
            if(res.ok) {
                document.getElementById('my-profile-pic').src = base64;
                currentUser.profilePicture = base64;
                showAuthMessage('Profile updated! +10 Points ⭐');
                fetchLeaderboard();
            }
        };
        reader.readAsDataURL(file);
    }
}

async function fetchLeaderboard() {
    const res = await fetch(`${API}/users/leaderboard`);
    if(res.ok) {
        const users = await res.json();
        document.getElementById('leaderboard').innerHTML = users.map(u => `
            <div style="display: flex; justify-content: space-between; padding: 5px 0;">
                <span>${u.profilePicture ? `<img src="${u.profilePicture}" style="width:20px;height:20px;border-radius:50%;object-fit:cover;vertical-align:middle;margin-right:5px;">` : '👤'} ${u.name}</span>
                <strong style="color: gold; text-shadow: 0 0 5px rgba(255,215,0,0.5);">${u.activityScore} ⭐</strong>
            </div>
        `).join('');
    }
}

async function blockActiveUser() {
    if(!activeChatFriendId) return;
    if(!confirm("Are you sure you want to block this user forever?")) return;
    
    await fetch(`${API}/users/${currentUser.id}/block/${activeChatFriendId}`, { method: 'POST' });
    closeChat();
    fetchFriends();
    fetchSuggestions();
    showAuthMessage('User Blocked Successfully.');
}

async function reportActiveUser() {
    if(!activeChatFriendId) return;
    if(!confirm("Are you sure you want to report this user? Admin will be notified.")) return;
    
    try {
        await fetch(`${API}/users/${currentUser.id}/report/${activeChatFriendId}`, { method: 'POST' });
        alert('User has been reported to the administration.');
    } catch(err) {
        console.error("Failed to report user", err);
    }
}

let callInterval = null;
let callSeconds = 0;
let syncCallInterval = null;

function renderEndButton() {
    document.getElementById('call-actions').innerHTML = `
        <button onclick="endSimulatedCall()" style="background: #ef4444; color: white; border: none; padding: 15px 40px; border-radius: 30px; font-size: 1.2rem; cursor: pointer; display: flex; align-items: center; gap: 10px; font-weight: bold; box-shadow: 0 5px 15px rgba(239, 68, 68, 0.4);">
            <span style="transform: rotate(135deg);">📞</span> End Call
        </button>`;
}

function renderAcceptDeclineButtons() {
    document.getElementById('call-actions').innerHTML = `
        <button onclick="answerCall(true)" style="background: #22c55e; color: white; border: none; padding: 15px 30px; border-radius: 30px; font-size: 1.2rem; cursor: pointer; display: flex; align-items: center; gap: 10px; font-weight: bold; box-shadow: 0 5px 15px rgba(34, 197, 94, 0.4);">
            📞 Accept
        </button>
        <button onclick="answerCall(false)" style="background: #ef4444; color: white; border: none; padding: 15px 30px; border-radius: 30px; font-size: 1.2rem; cursor: pointer; display: flex; align-items: center; gap: 10px; font-weight: bold; box-shadow: 0 5px 15px rgba(239, 68, 68, 0.4);">
            <span style="transform: rotate(135deg);">📞</span> Decline
        </button>`;
}

function startTimer() {
    document.getElementById('call-status').innerText = "Connected 🟢";
    document.getElementById('call-status').style.color = "#4ade80";
    document.getElementById('call-ring-anim').style.display = "none";
    document.getElementById('call-timer').style.display = "block";
    
    callSeconds = 0;
    if(callInterval) clearInterval(callInterval);
    callInterval = setInterval(() => {
        callSeconds++;
        const mins = String(Math.floor(callSeconds/60)).padStart(2, '0');
        const secs = String(callSeconds%60).padStart(2, '0');
        document.getElementById('call-timer').innerText = `${mins}:${secs}`;
    }, 1000);
}

function startSimulatedCall() {
    if(!activeChatFriendId) return;
    
    // Set localStorage state to initiate call
    const callData = {
        callerId: currentUser.id,
        callerName: currentUser.name,
        callerPic: currentUser.profilePicture || "https://ui-avatars.com/api/?name=" + currentUser.name,
        receiverId: activeChatFriendId,
        status: "RINGING"
    };
    localStorage.setItem("activeCall", JSON.stringify(callData));

    document.getElementById('call-modal').classList.remove('hidden');
    document.getElementById('call-user-name-display').innerText = document.getElementById('chat-user-name').innerText;
    
    const friendPicSrc = document.getElementById('chat-user-pic').src;
    document.getElementById('call-user-pic').src = (friendPicSrc && friendPicSrc !== window.location.href) ? friendPicSrc : "https://ui-avatars.com/api/?name=Friend";
    
    document.getElementById('call-status').innerText = "Calling...";
    document.getElementById('call-status').style.color = "white";
    document.getElementById('call-ring-anim').style.display = "block";
    document.getElementById('call-timer').style.display = "none";
    
    renderEndButton();

    if(syncCallInterval) clearInterval(syncCallInterval);
    syncCallInterval = setInterval(() => {
        const raw = localStorage.getItem("activeCall");
        if(raw) {
            const data = JSON.parse(raw);
            if(data.status === "ACCEPTED" && data.callerId === currentUser.id) {
                clearInterval(syncCallInterval);
                startTimer();
            } else if(data.status === "REJECTED" || data.status === "ENDED") {
                clearInterval(syncCallInterval);
                document.getElementById('call-status').innerText = data.status === "REJECTED" ? "Call Declined" : "Call Ended";
                document.getElementById('call-ring-anim').style.display = "none";
                setTimeout(endSimulatedCall, 2000);
            }
        } else {
            endSimulatedCall(); // They hung up
        }
    }, 1000);
}

function answerCall(accept) {
    const raw = localStorage.getItem("activeCall");
    if(raw) {
        let data = JSON.parse(raw);
        data.status = accept ? "ACCEPTED" : "REJECTED";
        localStorage.setItem("activeCall", JSON.stringify(data));
        
        if(accept) {
            renderEndButton();
            startTimer();
        } else {
            endSimulatedCall();
        }
    }
}

function endSimulatedCall() {
    localStorage.removeItem("activeCall");
    document.getElementById('call-modal').classList.add('hidden');
    if(callInterval) clearInterval(callInterval);
    if(syncCallInterval) clearInterval(syncCallInterval);
    
    document.getElementById('call-status').innerText = "Call Ended";
    document.getElementById('call-ring-anim').style.display = "block";
    document.getElementById('call-timer').style.display = "none";
}

// Global push notifications for incoming real-time calls across browser tabs
window.addEventListener('storage', (e) => {
    if(e.key === "activeCall") {
        if(!e.newValue) {
            if(!document.getElementById('call-modal').classList.contains('hidden')) endSimulatedCall();
            return;
        }
        
        const data = JSON.parse(e.newValue);
        if(data.status === "RINGING" && data.receiverId == currentUser.id) {
            document.getElementById('call-modal').classList.remove('hidden');
            document.getElementById('call-user-name-display').innerText = data.callerName;
            document.getElementById('call-user-pic').src = data.callerPic;
            
            document.getElementById('call-status').innerText = "Incoming Call...";
            document.getElementById('call-status').style.color = "white";
            document.getElementById('call-ring-anim').style.display = "block";
            document.getElementById('call-timer').style.display = "none";
            
            renderAcceptDeclineButtons();
        }
        
        if(data.status === "ENDED" && data.receiverId == currentUser.id) {
            endSimulatedCall();
        }
    }
});
