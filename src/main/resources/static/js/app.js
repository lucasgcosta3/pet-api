const API_BASE = '/v1/pets';
let currentPage = 0;
let currentTotalPages = 0;
let currentPhotoBase64 = null;

// ===== ROUTING SPA =====
function handleRoute() {
    const hash = window.location.hash || '#home';
    const pages = document.querySelectorAll('.page');
    pages.forEach(p => p.classList.remove('active'));

    const targetPage = document.querySelector(`#page-${hash.replace('#', '')}`);
    if (targetPage) {
        targetPage.classList.add('active');
    } else {
        document.querySelector('#page-home').classList.add('active');
    }

    // Update nav links
    document.querySelectorAll('.navbar-links a').forEach(a => {
        if (a.getAttribute('href') === hash) {
            a.classList.add('active');
        } else {
            a.classList.remove('active');
        }
    });

    // Load data based on route
    if (hash === '#pets') {
        searchPets();
    } else if (hash === '#dashboard') {
        loadDashboard();
    }

    // Show/hide footer (only on home)
    const footer = document.getElementById('main-footer');
    if (footer) {
        footer.style.display = (hash === '#home' || hash === '') ? 'block' : 'none';
    }

    // Scroll to top on route change
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

window.addEventListener('hashchange', handleRoute);
document.addEventListener('DOMContentLoaded', () => {
    handleRoute();
    setupNavbarScroll();
});

// ===== NAVBAR SCROLL EFFECT =====
function setupNavbarScroll() {
    const navbar = document.getElementById('main-navbar');
    if (!navbar) return;
    window.addEventListener('scroll', () => {
        if (window.scrollY > 20) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    }, { passive: true });
}

// ===== API CALLS =====
async function fetchApi(url, options = {}) {
    try {
        const res = await fetch(url, {
            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
            ...options
        });

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || `Erro ${res.status}`);
        }

        if (res.status === 204) return null;
        return await res.json();
    } catch (e) {
        showToast(e.message || 'Erro de conexão com o servidor', 'error');
        throw e;
    }
}

async function searchPets(page = 0) {
    const type = document.getElementById('filter-type').value;
    const name = document.getElementById('filter-name').value.trim();
    const breed = document.getElementById('filter-breed').value.trim();
    const city = document.getElementById('filter-city').value.trim();

    const params = new URLSearchParams({ page, size: 8 });
    if (type) params.append('type', type);
    if (name) params.append('name', name);
    if (breed) params.append('breed', breed);
    if (city) params.append('city', city);
    params.append('sort', 'createdAt,desc');

    showLoading(true);

    try {
        const data = await fetchApi(`${API_BASE}?${params}`);
        currentPage = data.page ? data.page.number : 0;
        currentTotalPages = data.page ? data.page.totalPages : 1;
        renderPets(data.content || []);
        renderPagination();
    } catch (e) {
        renderPets([]);
    } finally {
        showLoading(false);
    }
}

async function loadDashboard() {
    try {
        document.getElementById('dash-total-pets').textContent = '0';
        document.getElementById('dash-total-dogs').textContent = '0';
        document.getElementById('dash-total-cats').textContent = '0';
        document.getElementById('dash-recent-table').innerHTML = '<tr><td colspan="4" style="text-align:center; color:var(--text-muted);">Carregando...</td></tr>';

        const dogs = await fetchApi(`${API_BASE}?type=DOG&size=1`);
        const cats = await fetchApi(`${API_BASE}?type=CAT&size=1`);
        const totalDogs = dogs.page ? dogs.page.totalElements : 0;
        const totalCats = cats.page ? cats.page.totalElements : 0;
        const totalPets = totalDogs + totalCats;

        animateCounter('dash-total-pets', totalPets);
        animateCounter('dash-total-dogs', totalDogs);
        animateCounter('dash-total-cats', totalCats);

        const recent = await fetchApi(`${API_BASE}?size=5&sort=createdAt,desc`);
        renderRecentTable(recent.content || []);
    } catch (e) {
        document.getElementById('dash-recent-table').innerHTML = '<tr><td colspan="4" style="text-align:center; color:var(--coral);">Erro ao carregar dados.</td></tr>';
    }
}

async function getPet(id) {
    return await fetchApi(`${API_BASE}/${id}`);
}

async function createPet(data) {
    const result = await fetchApi(API_BASE, {
        method: 'POST',
        body: JSON.stringify(data)
    });
    showToast('Pet cadastrado com sucesso! 🎉', 'success');
    return result;
}

async function updatePet(id, data) {
    const result = await fetchApi(`${API_BASE}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
    showToast('Pet atualizado com sucesso! ✏️', 'success');
    return result;
}

async function deletePet(id) {
    await fetchApi(`${API_BASE}/${id}`, { method: 'DELETE' });
    showToast('Pet removido com sucesso', 'success');
}

// ===== RENDERING =====
function renderPets(pets) {
    const grid = document.getElementById('pets-grid');
    const empty = document.getElementById('pets-empty');

    if (!pets.length) {
        grid.innerHTML = '';
        empty.style.display = 'block';
        return;
    }

    empty.style.display = 'none';
    grid.innerHTML = pets.map((pet, i) => {
        const cardBg = pet.type === 'CAT' ? '#FFEDD5' : '#DCF0FF';
        const imgStyle = pet.photoBase64 ? `background-image: url(${pet.photoBase64}); background-size: cover; background-position: center;` : `background: ${cardBg};`;
        const icon = pet.photoBase64 ? '' : '<div style="opacity: 0.4; font-size: 3.5rem;">📷</div>';
        return `
        <div class="pet-card" style="animation: slideUp 0.4s var(--ease) forwards; animation-delay: ${i * 0.06}s; opacity: 0; background: ${cardBg};" onclick="openPetDetail('${pet.id}')">
            <div class="pet-card-img" style="${imgStyle}">
                ${icon}
                <div class="card-location-badge">
                    <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                    ${esc(pet.address?.city || 'Local')}
                </div>
                <button class="card-heart-btn" onclick="event.stopPropagation();">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                </button>
            </div>
            <div class="pet-card-content">
                <div class="pet-name">${esc(pet.name)}</div>
                <div class="pet-info-text">
                    ${pet.gender === 'MALE' ? 'Macho' : 'Fêmea'} · ${pet.age != null ? pet.age + ' Anos' : '-'}
                </div>
            </div>
        </div>
    `}).join('');
}

function renderRecentTable(pets) {
    const tbody = document.getElementById('dash-recent-table');
    if (!pets || pets.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color:var(--text-muted); padding: 2rem;">Nenhum registro recente encontrado.</td></tr>';
        return;
    }

    tbody.innerHTML = pets.map(pet => `
        <tr>
            <td>
                <div style="display: flex; align-items: center; gap: 12px;">
                    <div style="width: 36px; height: 36px; border-radius: var(--radius-sm); background: ${pet.type === 'CAT' ? '#FFEDD5' : '#DCF0FF'}; display: flex; align-items: center; justify-content: center;">
                        ${pet.type === 'DOG' ? '🐕' : '🐈'}
                    </div>
                    <span style="font-weight: 500;">${esc(pet.name)}</span>
                </div>
            </td>
            <td>${esc(pet.breed)}</td>
            <td><span class="status-badge status-available">Disponível</span></td>
            <td>${esc(pet.address?.city || '-')}</td>
        </tr>
    `).join('');
}

function renderPagination() {
    const container = document.getElementById('pagination');
    if (currentTotalPages <= 1) { container.innerHTML = ''; return; }

    let html = `<button class="btn btn-outline btn-sm" ${currentPage === 0 ? 'disabled' : ''} onclick="searchPets(${currentPage - 1})">Anterior</button>`;
    html += `<span style="display: flex; align-items: center; padding: 0 1rem; color: var(--text-muted); font-size: 0.875rem;">Página ${currentPage + 1} de ${currentTotalPages}</span>`;
    html += `<button class="btn btn-outline btn-sm" ${currentPage >= currentTotalPages - 1 ? 'disabled' : ''} onclick="searchPets(${currentPage + 1})">Próxima</button>`;

    container.innerHTML = html;
}

// ===== MODALS & FORMS =====
async function openPetDetail(id) {
    try {
        const pet = await getPet(id);
        document.getElementById('modal-pet-name').textContent = pet.name;
        document.getElementById('modal-pet-location').textContent = `📍 ${pet.address?.city || 'Desconhecido'}`;

        const modalHero = document.getElementById('modal-pet-icon');
        const modalPlaceholder = document.getElementById('modal-pet-placeholder');
        if (pet.photoBase64) {
            modalHero.style.backgroundImage = `url(${pet.photoBase64})`;
            modalHero.style.backgroundSize = 'cover';
            modalHero.style.backgroundPosition = 'center';
            if (modalPlaceholder) modalPlaceholder.style.display = 'none';
        } else {
            modalHero.style.backgroundImage = 'none';
            modalHero.style.backgroundColor = pet.type === 'DOG' ? '#DCF0FF' : '#FFEDD5';
            if (modalPlaceholder) modalPlaceholder.style.display = 'block';
        }

        document.getElementById('modal-pet-gender').textContent = pet.gender === 'MALE' ? 'Macho' : 'Fêmea';
        document.getElementById('modal-pet-age').textContent = pet.age != null ? `${pet.age} Anos` : '-';
        document.getElementById('modal-pet-breed').textContent = pet.breed || 'Desconhecida';

        document.getElementById('modal-btn-edit').onclick = () => {
            closeModal();
            openEditForm(pet);
        };

        document.getElementById('modal-btn-delete').onclick = () => {
            if(confirm('Tem certeza que deseja remover este pet?')) {
                deletePet(id).then(() => {
                    closeModal();
                    searchPets(currentPage);
                });
            }
        };

        document.getElementById('pet-detail-modal').classList.add('active');
    } catch (e) { /* silent */ }
}

function closeModal() {
    document.getElementById('pet-detail-modal').classList.remove('active');
}

function openEditForm(pet) {
    window.location.hash = '#register';
    document.querySelector('.register-header h2').textContent = 'Editar Pet';
    document.querySelector('.register-header p').textContent = 'Atualize as informações do seu pet.';

    document.getElementById('form-pet-id').value = pet.id;
    document.getElementById('form-name').value = pet.name || '';
    document.getElementById('form-breed').value = pet.breed || '';
    document.getElementById('form-type').value = pet.type || '';
    document.getElementById('form-gender').value = pet.gender || '';
    document.getElementById('form-weight').value = pet.weight || '';
    document.getElementById('form-city').value = pet.address?.city || '';
    document.getElementById('form-street').value = pet.address?.street || '';
    document.getElementById('form-number').value = pet.address?.number || '';
    document.getElementById('form-birthdate').value = '';

    currentPhotoBase64 = pet.photoBase64 || null;
    if (currentPhotoBase64) {
        document.getElementById('photo-upload-icon').style.display = 'none';
        document.getElementById('photo-upload-text').style.display = 'none';
        document.getElementById('photo-upload-area').style.backgroundImage = `url(${currentPhotoBase64})`;
        document.getElementById('photo-upload-area').style.backgroundSize = 'cover';
        document.getElementById('photo-upload-area').style.backgroundPosition = 'center';
        document.getElementById('photo-upload-area').style.borderStyle = 'solid';
    }

    document.getElementById('form-type').disabled = true;
    document.getElementById('form-gender').disabled = true;
    document.getElementById('form-birthdate').disabled = true;
    document.getElementById('form-birthdate').removeAttribute('required');
}

function resetForm() {
    document.getElementById('pet-form').reset();
    document.getElementById('form-pet-id').value = '';
    document.querySelector('.register-header h2').textContent = 'Cadastre um Pet';
    document.querySelector('.register-header p').textContent = 'Vamos adicionar mais um amigo peludo à família Pawlace.';

    document.getElementById('form-type').disabled = false;
    document.getElementById('form-gender').disabled = false;
    document.getElementById('form-birthdate').disabled = false;
    document.getElementById('form-birthdate').setAttribute('required', '');

    currentPhotoBase64 = null;
    document.getElementById('form-photo').value = '';
    document.getElementById('photo-upload-icon').style.display = 'block';
    document.getElementById('photo-upload-text').style.display = 'block';
    document.getElementById('photo-upload-area').style.backgroundImage = 'none';
    document.getElementById('photo-upload-area').style.borderStyle = 'dashed';
}

async function handleSubmit(e) {
    e.preventDefault();
    const petId = document.getElementById('form-pet-id').value;
    const isEdit = !!petId;
    const btn = document.getElementById('form-submit-btn');
    const originalHtml = btn.innerHTML;
    btn.innerHTML = '<span class="spinner" style="width: 20px; height: 20px; border-width: 2px; margin: 0; display: inline-block; vertical-align: middle;"></span> Salvando...';
    btn.style.pointerEvents = 'none';

    try {
        if (isEdit) {
            const data = {
                name: document.getElementById('form-name').value.trim() || null,
                address: {
                    city: document.getElementById('form-city').value.trim(),
                    street: document.getElementById('form-street').value.trim(),
                    number: document.getElementById('form-number').value.trim() || null
                },
                weight: parseFloat(document.getElementById('form-weight').value) || null,
                photoBase64: currentPhotoBase64
            };
            await updatePet(petId, data);
        } else {
            const data = {
                name: document.getElementById('form-name').value.trim(),
                type: document.getElementById('form-type').value,
                gender: document.getElementById('form-gender').value,
                address: {
                    city: document.getElementById('form-city').value.trim(),
                    street: document.getElementById('form-street').value.trim(),
                    number: document.getElementById('form-number').value.trim() || null
                },
                birthDate: document.getElementById('form-birthdate').value,
                weight: parseFloat(document.getElementById('form-weight').value),
                breed: document.getElementById('form-breed').value.trim(),
                photoBase64: currentPhotoBase64
            };
            await createPet(data);
        }

        resetForm();
        window.location.hash = '#pets';
    } catch (e) {
        // Error toast is already shown by fetchApi
    } finally {
        btn.innerHTML = originalHtml;
        btn.style.pointerEvents = 'auto';
    }
}

// ===== UTILITIES =====
function showLoading(show) {
    document.getElementById('pets-loading').style.display = show ? 'block' : 'none';
    if (show) {
        document.getElementById('pets-grid').innerHTML = '';
        document.getElementById('pets-empty').style.display = 'none';
    }
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <div style="width: 26px; height: 26px; border-radius: 50%; background: ${type==='success'?'#4CAF50':'var(--coral)'}; color: white; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; flex-shrink: 0;">
            ${type === 'success' ? '✓' : '✕'}
        </div>
        <span>${esc(message)}</span>
    `;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        toast.style.transition = 'all 0.3s var(--ease)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function animateCounter(id, target) {
    const el = document.getElementById(id);
    if (!el) return;
    let current = 0;
    const step = Math.max(1, Math.ceil(target / 30));
    const interval = setInterval(() => {
        current = Math.min(current + step, target);
        el.textContent = current;
        if (current >= target) clearInterval(interval);
    }, 30);
}

function esc(str) {
    if (!str) return '';
    const d = document.createElement('div');
    d.textContent = str;
    return d.innerHTML;
}

// Enter key search
document.querySelectorAll('.filter-input').forEach(input => {
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') searchPets();
    });
});

// Photo selection
function handlePhotoSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    if (file.size > 5 * 1024 * 1024) {
        showToast('A imagem deve ter no máximo 5MB.', 'error');
        event.target.value = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        const img = new Image();
        img.onload = function() {
            const canvas = document.createElement('canvas');
            const MAX_WIDTH = 800;
            const MAX_HEIGHT = 800;
            let width = img.width;
            let height = img.height;

            if (width > height) {
                if (width > MAX_WIDTH) {
                    height *= MAX_WIDTH / width;
                    width = MAX_WIDTH;
                }
            } else {
                if (height > MAX_HEIGHT) {
                    width *= MAX_HEIGHT / height;
                    height = MAX_HEIGHT;
                }
            }
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, width, height);

            currentPhotoBase64 = canvas.toDataURL('image/jpeg', 0.8);

            document.getElementById('photo-upload-icon').style.display = 'none';
            document.getElementById('photo-upload-text').style.display = 'none';
            document.getElementById('photo-upload-area').style.backgroundImage = `url(${currentPhotoBase64})`;
            document.getElementById('photo-upload-area').style.backgroundSize = 'cover';
            document.getElementById('photo-upload-area').style.backgroundPosition = 'center';
            document.getElementById('photo-upload-area').style.borderStyle = 'solid';
        };
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
}
