/* app.js - Dashboard Logic */
const API = '';
let currentModal = '', editingId = null;

const userRole = localStorage.getItem('user_role') || 'admin';
const username = localStorage.getItem('username') || '';

// ===== Navigation =====
function showSection(name) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.getElementById('sec-' + name).classList.add('active');
    document.getElementById('nav-' + name).classList.add('active');
    
    checkRoleUI();
    
    if (name === 'dashboard') loadDashboard();
    else if (name === 'students') loadStudents();
    else if (name === 'courses') loadCourses();
    else if (name === 'enrollments') loadEnrollments();
    else if (name === 'lecturers') loadLecturers();
    else if (name === 'schedules') loadSchedules();
    
    else if (name === 'admins') loadAdmins();
    lucide.createIcons();
}

function checkRoleUI() {
    // Hide revenue card for students
    const grid = document.querySelector('.stats-grid');
    if (grid && grid.children.length === 6) {
        grid.children[5].style.display = userRole === 'student' ? 'none' : 'block';
    }
    
    // Manage Add buttons
    const addStuBtn = document.querySelector('#sec-students .btn-primary');
    const addCrsBtn = document.querySelector('#sec-courses .btn-primary');
    const addLecBtn = document.querySelector('#sec-lecturers .btn-primary');
    
    if (userRole === 'student') {
        if (addStuBtn) addStuBtn.style.display = 'none';
        if (addCrsBtn) addCrsBtn.style.display = 'none';
        if (addLecBtn) addLecBtn.style.display = 'none';
    } else {
        if (addStuBtn) addStuBtn.style.display = 'inline-flex';
        if (addCrsBtn) addCrsBtn.style.display = 'inline-flex';
        if (addLecBtn) addLecBtn.style.display = 'inline-flex';
    }
}

// ===== Dashboard =====
async function loadDashboard() {
    try {
        const d = await (await fetch(`${API}/api/dashboard`)).json();
        
        if (userRole === 'student') {
            // Count student's own records
            const studentsList = await (await fetch(`${API}/api/students`)).json();
            const hasProfile = studentsList.some(s => s.studentId === username);
            document.getElementById('d-students').textContent = hasProfile ? '1' : '0';
            
            const enrollList = await (await fetch(`${API}/api/enrollments`)).json();
            const myEnrollCount = enrollList.filter(e => e.studentId === username).length;
            document.getElementById('d-enrollments').textContent = myEnrollCount;
            
            const payList = await (await fetch(`${API}/api/payments`)).json();
            const myPayCount = payList.filter(p => p.studentId === username).length;
            
            
            document.getElementById('d-courses').textContent = d.courses;
            document.getElementById('d-lecturers').textContent = d.lecturers;
        } else {
            document.getElementById('d-students').textContent = d.students;
            document.getElementById('d-courses').textContent = d.courses;
            document.getElementById('d-enrollments').textContent = d.enrollments;
            document.getElementById('d-lecturers').textContent = d.lecturers;
            
            document.getElementById('d-revenue').textContent = 'Rs.' + d.revenue.toLocaleString('en',{minimumFractionDigits:2});
        }
    } catch(e) { console.error(e); }
}

// ===== Data Loaders =====
async function loadStudents() {
    let data = await (await fetch(`${API}/api/students`)).json();
    const tb = document.getElementById('studentTbl');
    
    if (userRole === 'student') {
        data = data.filter(s => s.studentId === username);
    }
    
    if(!data.length) { tb.innerHTML = emptyRow(7,'graduation-cap','No student profiles found'); return; }
    tb.innerHTML = data.map(s => `<tr>
        <td><strong>${s.studentId}</strong></td><td>${s.name}</td><td>${s.email}</td><td>${s.phone}</td>
        <td><span class="badge ${s.studentType==='Undergraduate'?'badge-primary':'badge-warning'}">${s.studentType}</span></td>
        <td>${fmt(s.fee)}</td>
        <td class="actions">
            ${userRole === 'admin' ? `
                <button class="btn btn-ghost btn-sm" onclick='editStudent(${JSON.stringify(s)})'><i data-lucide="pencil" style="width:14px;height:14px"></i></button>
                <button class="btn btn-ghost btn-sm" style="color:var(--danger)" onclick="delRecord('students','${s.studentId}')"><i data-lucide="trash-2" style="width:14px;height:14px"></i></button>
            ` : `<span style="color:var(--text-muted);font-size:12px">Read-only</span>`}
        </td></tr>`).join('');
    lucide.createIcons();
}

async function loadCourses() {
    const data = await (await fetch(`${API}/api/courses`)).json();
    const tb = document.getElementById('courseTbl');
    if(!data.length) { tb.innerHTML = emptyRow(8,'book-open','No courses found'); return; }
    tb.innerHTML = data.map(c => `<tr>
        <td><strong>${c.courseId}</strong></td><td>${c.courseName}</td>
        <td>${c.credits}</td><td>${c.department}</td><td>${c.maxStudents}</td>
        <td>Rs.${c.fee.toLocaleString()}</td>
        <td><span class="badge ${c.courseType==='Core'?'badge-primary':'badge-info'}">${c.courseType}</span></td>
        <td class="actions">
            ${userRole === 'admin' ? `
                <button class="btn btn-ghost btn-sm" onclick='editCourse(${JSON.stringify(c)})'><i data-lucide="pencil" style="width:14px;height:14px"></i></button>
                <button class="btn btn-ghost btn-sm" style="color:var(--danger)" onclick="delRecord('courses','${c.courseId}')"><i data-lucide="trash-2" style="width:14px;height:14px"></i></button>
            ` : `<span style="color:var(--text-muted);font-size:12px">Read-only</span>`}
        </td></tr>`).join('');
    lucide.createIcons();
}

async function loadEnrollments() {
    let data = await (await fetch(`${API}/api/enrollments`)).json();
    const tb = document.getElementById('enrollTbl');
    
    if (userRole === 'student') {
        data = data.filter(e => e.studentId === username);
    }
    
    if(!data.length) { tb.innerHTML = emptyRow(6,'clipboard-check','No enrollments found'); return; }
    tb.innerHTML = data.map(e => `<tr>
        <td><strong>${e.enrollmentId}</strong></td><td>${e.studentId}</td><td>${e.courseId}</td><td>${e.enrollmentDate}</td>
        <td><span class="badge ${e.status==='Active'?'badge-success':e.status==='Pending'?'badge-warning':'badge-danger'}">${e.status}</span></td>
        <td class="actions">
        <td>${e.status==='Active'?`<button class="btn btn-danger btn-sm" onclick="dropEnroll('${e.enrollmentId}')">Drop</button>`:''}</td>
        </tr>`).join('');
    lucide.createIcons();
}

async function loadLecturers() {
    const data = await (await fetch(`${API}/api/lecturers`)).json();
    const tb = document.getElementById('lecturerTbl');
    if(!data.length) { tb.innerHTML = emptyRow(7,'user-check','No lecturers yet'); return; }
    tb.innerHTML = data.map(l => `<tr>
        <td><strong>${l.lecturerId}</strong></td><td>${l.name}</td><td>${l.email}</td><td>${l.department}</td>
        <td><span class="badge ${l.lecturerType==='Permanent'?'badge-success':'badge-warning'}">${l.lecturerType}</span></td>
        <td>${l.workload} hrs/wk</td>
        <td class="actions">
            ${userRole === 'admin' ? `
                <button class="btn btn-ghost btn-sm" onclick='editLecturer(${JSON.stringify(l)})'><i data-lucide="pencil" style="width:14px;height:14px"></i></button>
                <button class="btn btn-ghost btn-sm" style="color:var(--danger)" onclick="delRecord('lecturers','${l.lecturerId}')"><i data-lucide="trash-2" style="width:14px;height:14px"></i></button>
            ` : `<span style="color:var(--text-muted);font-size:12px">Read-only</span>`}
        </td></tr>`).join('');
    lucide.createIcons();
}

/* loadPayments disabled */

async function loadAdmins() {
    const data = await (await fetch(`${API}/api/admins`)).json();
    const tb = document.getElementById('adminTbl');
    if(!data.length) { tb.innerHTML = emptyRow(3,'shield','No admins yet'); return; }
    tb.innerHTML = data.map(a => `<tr>
        <td><strong>${a.username}</strong></td><td>${a.name}</td>
        <td class="actions">
            <button class="btn btn-ghost btn-sm" onclick='editAdmin(${JSON.stringify(a)})'><i data-lucide="pencil" style="width:14px;height:14px"></i></button>
            <button class="btn btn-ghost btn-sm" style="color:var(--danger)" onclick="delRecord('admins','${a.username}')"><i data-lucide="trash-2" style="width:14px;height:14px"></i></button>
        </td></tr>`).join('');
    lucide.createIcons();
}

async function loadSchedules() {
    const data = await (await fetch(`${API}/api/schedules`)).json();
    const tb = document.getElementById('scheduleTbl');
    if(!data.length) { tb.innerHTML = emptyRow(7,'calendar','No schedules yet'); return; }
    tb.innerHTML = data.map(s => `<tr>
        <td><strong>${s.scheduleId}</strong></td><td>${s.courseId}</td><td>${s.lecturerId}</td>
        <td>${s.dateTime.replace('T', ' ')}</td>
        <td><span class="badge ${s.type==='Physical'?'badge-primary':'badge-info'}">${s.type}</span></td><td>${s.location}</td>
        <td class="actions">
            ${userRole === 'admin' ? `
                <button class="btn btn-ghost btn-sm" onclick='editSchedule(${JSON.stringify(s)})'><i data-lucide="pencil" style="width:14px;height:14px"></i></button>
                <button class="btn btn-ghost btn-sm" style="color:var(--danger)" onclick="delRecord('schedules','${s.scheduleId}')"><i data-lucide="trash-2" style="width:14px;height:14px"></i></button>
            ` : `<span style="color:var(--text-muted);font-size:12px">Read-only</span>`}
        </td></tr>`).join('');
    lucide.createIcons();
}

// ===== Modal =====
function openModal(type) {
    currentModal = type; editingId = null;
    const body = document.getElementById('modalBody');
    const title = document.getElementById('modalTitle');

    const forms = {
        student: ()=>{ title.textContent='Add Student'; body.innerHTML=`
            ${field('m-sid','Student ID','text','e.g. S001')}${field('m-sname','Name','text','Full name')}
            ${field('m-semail','Email','email','email@example.com')}${field('m-sphone','Phone','text','Phone')}
            ${field('m-saddr','Address','text','Address')}
            <div class="form-group"><label class="form-label">Type</label><select class="form-select" id="m-stype" onchange="toggleSF()"><option value="Undergraduate">Undergraduate</option><option value="Postgraduate">Postgraduate</option></select></div>
            <div id="sfWrap">${field('m-sf1','Year (1-4)','number','1')}${field('m-sf2','Faculty','text','Faculty')}</div>`; },
        course: ()=>{ title.textContent='Add Course'; body.innerHTML=`
            ${field('m-cid','Course ID','text','e.g. C001')}${field('m-cname','Course Name','text','Name')}
            ${field('m-ccr','Credits','number','3')}${field('m-cdept','Department','text','Department')}
            ${field('m-cmax','Max Students','number','30')}
            ${field('m-cfee','Fee (Rs.)','number','50000')}
            <div class="form-group"><label class="form-label">Type</label><select class="form-select" id="m-ctype" onchange="toggleCF()"><option value="Core">Core</option><option value="Elective">Elective</option></select></div>
            <div id="cfWrap"><div class="form-group"><label class="form-label">Mandatory</label><select class="form-select" id="m-cex"><option value="true">Yes</option><option value="false">No</option></select></div></div>`; },
        enrollment: ()=>{ title.textContent='New Enrollment'; body.innerHTML=`
            ${field('m-esid','Student ID','text','e.g. S001')}${field('m-ecid','Course ID','text','e.g. C001')}
            <div class="form-group"><label class="form-label">Type</label><select class="form-select" id="m-etype"><option value="Full-Time">Full-Time (max 6)</option><option value="Part-Time">Part-Time (max 3)</option></select></div>`; },
        lecturer: ()=>{ title.textContent='Add Lecturer'; body.innerHTML=`
            ${field('m-lid','Lecturer ID','text','e.g. L001')}${field('m-lname','Name','text','Full name')}
            ${field('m-lemail','Email','email','email@example.com')}${field('m-lphone','Phone','text','Phone')}
            ${field('m-ldept','Department','text','Department')}${field('m-lcourse','Assigned Course','text','Course ID or None')}
            <div class="form-group"><label class="form-label">Type</label><select class="form-select" id="m-ltype" onchange="toggleLF()"><option value="Permanent">Permanent</option><option value="Visiting">Visiting</option></select></div>
            <div id="lfWrap">${field('m-lex','Years of Experience','number','5')}</div>`; },
        /* payment modal disabled */
        /* paymentStatus modal disabled */
        admin: ()=>{ title.textContent='Add Admin'; body.innerHTML=`
            ${field('m-ausr','Username','text','e.g. admin2')}
            ${field('m-aname','Full Name','text','Name')}
            ${field('m-apwd','Password','password','Password')}`; },
        schedule: async ()=>{ 
            title.textContent='Schedule Class';
            const courses = await (await fetch(`${API}/api/courses`)).json();
            const lecturers = await (await fetch(`${API}/api/lecturers`)).json();
            let cOpts = courses.map(c=>`<option value="${c.courseId}">${c.courseId} - ${c.courseName}</option>`).join('');
            let lOpts = lecturers.map(l=>`<option value="${l.lecturerId}">${l.lecturerId} - ${l.name}</option>`).join('');
            
            const autoId = 'SCH' + Math.floor(1000 + Math.random() * 9000);
            
            body.innerHTML=`
                <div class="form-group"><label class="form-label">Schedule ID</label><input type="text" class="form-input" id="m-schid" placeholder="e.g. SCH001" value="${autoId}"></div>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
                    <div class="form-group"><label class="form-label">Course</label><select class="form-select" id="m-schcid">${cOpts}</select></div>
                    <div class="form-group"><label class="form-label">Lecturer</label><select class="form-select" id="m-schlid">${lOpts}</select></div>
                </div>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
                    <div class="form-group"><label class="form-label">Date & Time</label><input type="datetime-local" class="form-input" id="m-schdt" required></div>
                    <div class="form-group"><label class="form-label">Type</label><select class="form-select" id="m-schtyp"><option value="Physical">Physical</option><option value="Online">Online</option></select></div>
                </div>
                ${field('m-schloc','Location / Link','text','e.g. Room 402 or Zoom Link')}
            `; 
        },
        confirmDelete: ()=>{ title.textContent='Confirm Deletion'; body.innerHTML=`
            <div style="text-align:center; padding: 20px 0;">
                <div style="width:64px; height:64px; border-radius:50%; background:rgba(239,68,68,0.1); color:var(--danger); display:flex; align-items:center; justify-content:center; margin:0 auto 16px;">
                    <i data-lucide="alert-triangle" style="width:32px; height:32px"></i>
                </div>
                <h3 style="margin-bottom:8px; font-size:18px">Are you sure?</h3>
                <p style="color:var(--text-muted); font-size:14px">Do you really want to delete this record? This process cannot be undone.</p>
            </div>
            <input type="hidden" id="m-delmod"><input type="hidden" id="m-delid">`; }
    };
    forms[type]();
    
    // Auto-fill and lock student ID for student logins
    if (userRole === 'student') {
        if (type === 'enrollment') {
            const sidInput = document.getElementById('m-esid');
            if (sidInput) { sidInput.value = username; sidInput.readOnly = true; }
        }
         {
        document.getElementById('modalOverlay').classList.add('active');
        lucide.createIcons();
    }
}

function closeModal() { 
    document.getElementById('modalOverlay').classList.remove('active'); 
    currentModal=''; 
    editingId=null; 
    document.getElementById('modalSaveBtn').className = 'btn btn-primary';
    document.getElementById('modalSaveBtn').innerHTML = '<i data-lucide="save"></i> Save changes';
    lucide.createIcons();
}
function field(id,label,type,ph) { return `<div class="form-group"><label class="form-label">${label}</label><input type="${type}" class="form-input" id="${id}" placeholder="${ph}"></div>`; }

function toggleSF() {
    const t = document.getElementById('m-stype').value;
    document.getElementById('sfWrap').innerHTML = t==='Undergraduate'
        ? field('m-sf1','Year (1-4)','number','1')+field('m-sf2','Faculty','text','Faculty')
        : field('m-sf1','Research Area','text','Research area')+field('m-sf2','Supervisor','text','Supervisor');
}
function toggleCF() {
    const t = document.getElementById('m-ctype').value;
    document.getElementById('cfWrap').innerHTML = t==='Core'
        ? '<div class="form-group"><label class="form-label">Mandatory</label><select class="form-select" id="m-cex"><option value="true">Yes</option><option value="false">No</option></select></div>'
        : field('m-cex','Prerequisite','text','Prerequisite or None');
}
function toggleLF() {
    const t = document.getElementById('m-ltype').value;
    document.getElementById('lfWrap').innerHTML = t==='Permanent'
        ? field('m-lex','Years of Experience','number','5')
        : field('m-lex','Contract Months','number','6');
}
function togglePF() {
    const t = document.getElementById('m-ptype').value;
    document.getElementById('pfWrap').innerHTML = t==='Online'
        ? field('m-pex','Transaction Ref','text','Reference')
        : field('m-pex','Cashier Location','text','e.g. Main Office');
}

// ===== Save =====
async function saveRecord() {
    try {
        let url, method='POST', data;
        if (currentModal==='student') {
            data = {studentId:v('m-sid'),name:v('m-sname'),email:v('m-semail'),phone:v('m-sphone'),address:v('m-saddr'),studentType:v('m-stype'),extraField1:v('m-sf1'),extraField2:v('m-sf2')};
            url = editingId ? `${API}/api/students/${editingId}` : `${API}/api/students`;
            method = editingId ? 'PUT' : 'POST';
        } else if (currentModal==='course') {
            const ex = v('m-ctype')==='Core' ? v('m-cex') : v('m-cex');
            data = {courseId:v('m-cid'),courseName:v('m-cname'),credits:v('m-ccr'),department:v('m-cdept'),maxStudents:v('m-cmax'),fee:v('m-cfee'),courseType:v('m-ctype'),extraField:ex};
            url = editingId ? `${API}/api/courses/${editingId}` : `${API}/api/courses`;
            method = editingId ? 'PUT' : 'POST';
        } else if (currentModal==='enrollment') {
            data = {studentId:v('m-esid'),courseId:v('m-ecid'),enrollmentType:v('m-etype')};
            url = `${API}/api/enrollments`;
        } else if (currentModal==='lecturer') {
            data = {lecturerId:v('m-lid'),name:v('m-lname'),email:v('m-lemail'),phone:v('m-lphone'),department:v('m-ldept'),assignedCourseId:v('m-lcourse'),lecturerType:v('m-ltype'),extraField:v('m-lex')};
            url = editingId ? `${API}/api/lecturers/${editingId}` : `${API}/api/lecturers`;
            method = editingId ? 'PUT' : 'POST';
        }  if (currentModal==='paymentStatus') {
            data = {status:v('m-upst')};
            url = `${API}/api/payments/${editingId}`;
            method = 'PUT';
        } else if (currentModal==='admin') {
            data = {username:v('m-ausr'),name:v('m-aname'),password:v('m-apwd')};
            url = editingId ? `${API}/api/admins/${editingId}` : `${API}/api/admins`;
            method = editingId ? 'PUT' : 'POST';
        } else if (currentModal==='schedule') {
            data = {scheduleId:v('m-schid'),courseId:v('m-schcid'),lecturerId:v('m-schlid'),dateTime:v('m-schdt'),type:v('m-schtyp'),location:v('m-schloc')};
            url = editingId ? `${API}/api/schedules/${editingId}` : `${API}/api/schedules`;
            method = editingId ? 'PUT' : 'POST';
        } else if (currentModal==='confirmDelete') {
            url = `${API}/api/${v('m-delmod')}/${v('m-delid')}`;
            method = 'DELETE';
        }
        
        const res = await fetch(url, {method, headers:{'Content-Type':'application/json'}, body: data ? JSON.stringify(data) : null});
        const r = await res.json();
        if (r.success) { 
            toast(currentModal==='confirmDelete' ? 'Deleted successfully!' : 'Record saved successfully!','success'); 
            let sec = currentModal + 's';
            if(currentModal === 'enrollment') sec = 'enrollments';
            
            if(currentModal === 'admin') sec = 'admins';
            if(currentModal === 'confirmDelete') sec = v('m-delmod');
            showSection(sec); 
            closeModal(); 
        }
        else toast(r.error||'Error saving record','error');
    } catch(e) { toast('Network error: '+e.message,'error'); }
}

// ===== Edit =====
function editStudent(s) { openModal('student'); editingId=s.studentId; document.getElementById('modalTitle').textContent='Edit Student';
    sv('m-sid',s.studentId);document.getElementById('m-sid').readOnly=true;sv('m-sname',s.name);sv('m-semail',s.email);sv('m-sphone',s.phone);sv('m-saddr',s.address);
    document.getElementById('m-stype').value=s.studentType;toggleSF();sv('m-sf1',s.extraField1);sv('m-sf2',s.extraField2); }
function editCourse(c) { openModal('course'); editingId=c.courseId; document.getElementById('modalTitle').textContent='Edit Course';
    sv('m-cid',c.courseId);document.getElementById('m-cid').readOnly=true;sv('m-cname',c.courseName);sv('m-ccr',c.credits);sv('m-cdept',c.department);sv('m-cmax',c.maxStudents);sv('m-cfee',c.fee); }
function editLecturer(l) { openModal('lecturer'); editingId=l.lecturerId; document.getElementById('modalTitle').textContent='Edit Lecturer';
    sv('m-lid',l.lecturerId);document.getElementById('m-lid').readOnly=true;sv('m-lname',l.name);sv('m-lemail',l.email);sv('m-lphone',l.phone);sv('m-ldept',l.department);sv('m-lcourse',l.assignedCourseId); }
function editAdmin(a) { openModal('admin'); editingId=a.username; document.getElementById('modalTitle').textContent='Edit Admin';
    sv('m-ausr',a.username);document.getElementById('m-ausr').readOnly=true;sv('m-aname',a.name);sv('m-apwd',''); }
function editSchedule(s) { openModal('schedule').then(()=> {
    editingId=s.scheduleId; document.getElementById('modalTitle').textContent='Edit Schedule';
    sv('m-schid',s.scheduleId);document.getElementById('m-schid').readOnly=true;sv('m-schcid',s.courseId);sv('m-schlid',s.lecturerId);sv('m-schdt',s.dateTime);sv('m-schtyp',s.type);sv('m-schloc',s.location);
}); }

// ===== Delete =====
function delRecord(mod,id) {
    openModal('confirmDelete');
    sv('m-delmod', mod);
    sv('m-delid', id);
    document.getElementById('modalSaveBtn').innerHTML = '<i data-lucide="trash-2"></i> Confirm Delete';
    document.getElementById('modalSaveBtn').className = 'btn btn-danger';
    lucide.createIcons();
}
async function dropEnroll(id) {
    if(!confirm('Drop this enrollment?'))return;
    const r = await (await fetch(`${API}/api/enrollments/${id}`,{method:'DELETE'})).json();
    if(r.success){toast('Dropped!','success');loadEnrollments();}else toast('Failed','error');
}
async function updPayStatus(id) {
    
    editingId = id;
}

// ===== Utils =====
function v(id){const el=document.getElementById(id);return el?el.value:'';}
function sv(id,val){const el=document.getElementById(id);if(el)el.value=val;}
function fmt(n){return 'Rs.'+Number(n).toLocaleString('en',{minimumFractionDigits:2});}
function emptyRow(cols,icon,msg){return `<tr><td colspan="${cols}"><div class="empty-state"><i data-lucide="${icon}" style="width:48px;height:48px;margin:0 auto 16px;display:block;opacity:0.2"></i><p>${msg}</p></div></td></tr>`;}

function toast(msg,type) {
    const t = document.createElement('div');
    t.className = `toast toast-${type}`;
    t.innerHTML = `<i data-lucide="${type==='success'?'check-circle':'alert-circle'}"></i><span>${msg}</span>`;
    document.body.appendChild(t); lucide.createIcons();
    setTimeout(()=>{t.style.opacity='0';t.style.transform='translateX(60px)';t.style.transition='all 0.3s ease';setTimeout(()=>t.remove(),300);},3000);
}

document.getElementById('modalOverlay').addEventListener('click',function(e){if(e.target===this)closeModal();});
