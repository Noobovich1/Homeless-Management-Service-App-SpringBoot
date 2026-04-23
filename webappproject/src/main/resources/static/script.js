let currentUser = null;

// Startup: Seed data and load initial states
window.onload = async () => {
    await fetch('/api/admin/seed-users', {method: 'POST'});
    await fetch('/api/admin/init-resources', {method: 'POST'});
};

// 1. Navigation & UI Logic
function showView(viewId) {
    document.querySelectorAll('.section-hero, .tab-pane, #login-view, #donate-view, #app-view')
        .forEach(el => el.style.display = 'none');
    const target = document.getElementById(viewId);
    if(target) target.style.display = viewId === 'app-view' ? 'flex' : 'flex';
}

function switchTab(tabId, event) {
    document.querySelectorAll('.tab-pane').forEach(t => t.style.display = 'none');
    document.querySelectorAll('.sidebar-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('tab-' + tabId).style.display = 'block';
    if(event) event.currentTarget.classList.add('active');

    if(tabId === 'dashboard') { loadResources(); loadDonations(); }
    else if(tabId === 'clients') loadClients();
    else if(tabId === 'tasks') loadTasks();
    else if(tabId === 'admin') { loadUsers(); loadAuditLogs(); }
}

// 2. Auth Logic
document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('loginBtn');
    if(loginBtn) {
        loginBtn.addEventListener('click', async () => {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const res = await fetch('/api/login', { 
                method: 'POST', 
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password}) 
            });
            if(res.ok) {
                currentUser = await res.json();
                document.getElementById('user-info').innerText = `Role: ${currentUser.role}`;
                if(currentUser.role === 'Admin') document.getElementById('nav-admin').style.display = 'block';
                showView('app-view');
                switchTab('dashboard');
            } else { alert("Invalid Login"); }
        });
    }
});

// 3. Resource Functions (CRUD)
async function loadResources() {
    const data = await fetch('/api/resources').then(r => r.json());
    document.getElementById('resource-list').innerHTML = data.map(r => `
        <div class="table-row-item">
            <div><strong>${r.category}</strong><br><small class="text-caption">Funding: $${r.publicFundingSource}</small></div>
            <div>Qty: ${r.quantity} <button onclick="deductResource(${r.id})" class="apple-btn-secondary">Deduct</button></div>
        </div>
    `).join('');
}

async function deductResource(id) {
    await fetch(`/api/resources/${id}/deduct`, {method: 'PATCH'});
    loadResources();
}

// 4. Donation Functions
async function loadDonations() {
    const data = await fetch('/api/donations').then(r => r.json());
    document.getElementById('donation-list').innerHTML = data.map(d => `
        <div class="table-row-item">
            <span><strong>+$${d.amount}</strong> to ${d.category}</span>
            <span class="text-caption">${d.nickname}</span>
        </div>
    `).join('');
}

async function submitDonation() {
    const data = {
        nickname: document.getElementById('donName').value,
        category: document.getElementById('donCategory').value,
        amount: parseFloat(document.getElementById('donAmount').value)
    };
    await fetch('/api/donate', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    });
    alert("Thank you!");
    showView('login-view');
}

// 5. Client Functions
async function loadClients() {
    const data = await fetch('/api/clients').then(r => r.json());
    document.getElementById('client-list').innerHTML = data.map(c => `
        <div class="table-row-item">
            <span>${c.firstName} ${c.lastName}</span>
            <span style="color:var(--apple-blue)">${c.status}</span>
        </div>
    `).join('');
}

async function registerClient() {
    const firstName = document.getElementById('clientFN').value;
    const lastName = document.getElementById('clientLN').value;
    await fetch('/api/clients', { 
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({firstName, lastName, status: 'Pending'}) 
    });
    loadClients();
}

// 6. Task Functions
async function loadTasks() {
    const data = await fetch('/api/tasks').then(r => r.json());
    document.getElementById('task-list').innerHTML = data.map(t => `
        <div class="table-row-item">
            <span>${t.title} (${t.taskType})</span>
            <div>
                <button onclick="completeTask(${t.id})" class="apple-btn-secondary">Done</button>
                <button onclick="deleteTask(${t.id})" class="apple-btn-dark">X</button>
            </div>
        </div>
    `).join('');
}

async function addTask() {
    const title = document.getElementById('taskTitle').value;
    const taskType = document.getElementById('taskType').value;
    await fetch('/api/tasks', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({title, taskType}) });
    loadTasks();
}

async function completeTask(id) {
    await fetch(`/api/tasks/${id}/complete`, {method: 'PATCH'});
    loadTasks();
}

async function deleteTask(id) {
    if(confirm('Delete?')) {
        await fetch(`/api/tasks/${id}`, {method: 'DELETE'});
        loadTasks();
    }
}

// 7. Admin Functions
async function loadUsers() {
    const data = await fetch('/api/admin/users').then(r => r.json());
    document.getElementById('user-list').innerHTML = data.map(u => `<div>${u.username} (${u.role})</div>`).join('');
}

async function addUser() {
    const username = document.getElementById('newUName').value;
    const password = document.getElementById('newUPass').value;
    await fetch('/api/admin/users', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({username, password, role:'Volunteer'}) });
    loadUsers();
}

async function loadAuditLogs() {
    const data = await fetch('/api/audit-logs').then(r => r.json());
    document.getElementById('audit-list').innerHTML = data.map(l => `<div>${l.action} on ${l.targetEntity}</div>`).join('');
}

function exportData() {
    const res = document.getElementById('exportResource').value;
    window.location.href = `/api/export?resource=${res}`;
}

function toggleTheme() { document.body.classList.toggle('light-mode'); }
function logout() { location.reload(); }