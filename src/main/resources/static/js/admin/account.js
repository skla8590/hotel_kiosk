/* ══════════════════════════════════════════════════════
   accounts.js  (MVC + Thymeleaf 방식)
   - 초기 목록은 Thymeleaf가 서버에서 렌더링
   - 검색/필터는 URL 파라미터로 페이지 이동
   - 등록/수정/권한·상태 변경은 axios REST 유지
══════════════════════════════════════════════════════ */
const accounts = (() => {

    let isIdValid = false;
    let isEmailValid = false;

    function clearErrors() {
        document.getElementById('msg-userId').textContent = '';
        document.getElementById('msg-email').textContent = '';
    }

    /* 검색 - URL 파라미터로 페이지 재로딩 (Thymeleaf 렌더링) */
    function load() {
        const keyword = document.getElementById('acct-keyword')?.value || '';
        const gradeBtn = document.querySelector('#acct-seg .seg-btn.active');
        const adminGrade = gradeBtn?.dataset?.grade || '';

        const params = new URLSearchParams();
        if (keyword) params.set('keyword', keyword);
        if (adminGrade) params.set('adminGrade', adminGrade);

        window.location.href = '/admin/account' + (params.toString() ? '?' + params.toString() : '');
    }

    /* 필터 버튼 - 버튼에 data-grade 속성 활용 */
    function filterAccounts(el, grade) {
        document.querySelectorAll('#acct-seg .seg-btn')
            .forEach(btn => btn.classList.remove('active'));
        el.classList.add('active');

        const keyword = document.getElementById('acct-keyword')?.value || '';
        const params = new URLSearchParams();
        if (keyword) params.set('keyword', keyword);
        if (grade) params.set('adminGrade', grade);

        window.location.href = '/admin/account' + (params.toString() ? '?' + params.toString() : '');
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        const yyyy = d.getFullYear();
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const dd = String(d.getDate()).padStart(2, '0');
        const hh = String(d.getHours()).padStart(2, '0');
        const min = String(d.getMinutes()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
    }

    let searchTimer = null;

    function bindEvents() {
        document.addEventListener('keyup', function (e) {

            /* 검색 - 300ms 디바운스 후 페이지 이동 */
            if (e.target.id === 'acct-keyword') {
                clearTimeout(searchTimer);
                searchTimer = setTimeout(() => {
                    load();
                }, 300);
            }

            /* 아이디 중복 체크 */
            if (e.target.id === 'reg-userId') {
                const value = e.target.value.trim();
                const msg = document.getElementById('msg-userId');
                if (!msg) return;
                if (!value) { msg.textContent = ''; isIdValid = false; return; }

                axios.get(`/admin/account/check?type=id&value=${value}`)
                    .then(res => {
                        if (res.data.exists) {
                            msg.style.color = 'red';
                            msg.textContent = '이미 존재하는 아이디입니다.';
                            isIdValid = false;
                        } else {
                            msg.style.color = 'blue';
                            msg.textContent = '사용 가능한 아이디입니다.';
                            isIdValid = true;
                        }
                    });
            }

            /* 이메일 중복 체크 */
            if (e.target.id === 'reg-email') {
                const value = e.target.value.trim();
                const msg = document.getElementById('msg-email');
                if (!msg) return;
                if (!value) { msg.textContent = ''; isEmailValid = false; return; }

                axios.get(`/admin/account/check?type=email&value=${value}`)
                    .then(res => {
                        if (res.data.exists) {
                            msg.style.color = 'red';
                            msg.textContent = '이미 존재하는 이메일입니다.';
                            isEmailValid = false;
                        } else {
                            msg.style.color = 'blue';
                            msg.textContent = '사용 가능한 이메일입니다.';
                            isEmailValid = true;
                        }
                    });
            }
        });
    }

    function register() {
        clearErrors();

        if (!isIdValid || !isEmailValid) {
            showAlertModal('중복 값을 확인해주세요.');
            return;
        }

        const payload = {
            adminName: document.querySelector('#reg-username')?.value,
            adminId: document.querySelector('#reg-userId')?.value,
            adminEmail: document.querySelector('#reg-email')?.value,
            adminPhone: document.querySelector('#reg-phone')?.value,
            adminGrade: document.querySelector('input[name="reg-role"]:checked')?.value,
        };

        _ajax('POST', '/admin/account', payload, (err) => {
            if (err) { showAlertModal('등록을 실패하였습니다.'); return; }
            closeModal('modal-acct-reg');
            openModal('modal-acct-reg-done');
        });
    }

    /* 등록 완료 후 확인 버튼 → 페이지 새로고침으로 목록 갱신 */
    function reloadAfterRegister() {
        closeModal('modal-acct-reg-done');
        window.location.reload();
    }

    function update(id) {
        const payload = {
            adminEmail: document.querySelector('#edit-email')?.value,
            adminPhone: document.querySelector('#edit-phone')?.value,
        };

        _ajax('PUT', `/admin/account/${id}`, payload, (err) => {
            if (err) { showAlertModal('수정을 실패하였습니다.'); return; }
            closeModal('modal-acct-edit');
            window.location.reload();
        });
    }

    function togglePerm(el, role) {
        const wrapper = el.closest('.perm-toggle');
        const accountId = wrapper.dataset.accountId;

        _ajax('PATCH', `/admin/account/${accountId}/adminGrade`, { adminGrade: role }, (err) => {
            if (err) { showAlertModal('권한 변경을 실패하였습니다'); return; }

            wrapper.querySelectorAll('.perm-btn').forEach(btn => btn.classList.remove('on'));
            el.classList.add('on');
            wrapper.classList.toggle('super', role === 'SUPER');

            const row = wrapper.closest('tr');
            const gradeCell = row.querySelector('.col-grade');
            gradeCell.innerHTML = `<span class="badge ${role === 'SUPER' ? 'badge-gold' : 'badge-blue'}">${role === 'SUPER' ? '최고관리자' : '일반관리자'}</span>`;
        });
    }

    function updateStatement(adminId, statement) {
        _ajax('PATCH', `/admin/account/${adminId}/statement`, { statement }, (err) => {
            if (err) { showAlertModal('상태 변경을 실패하였습니다'); return; }
            window.location.reload();
        });
    }

    function init() {
        bindEvents();

        /* URL 파라미터로 현재 필터 상태 복원 */
        const params = new URLSearchParams(window.location.search);
        const keyword = params.get('keyword') || '';
        const adminGrade = params.get('adminGrade') || '';

        const keywordInput = document.getElementById('acct-keyword');
        if (keywordInput && keyword) keywordInput.value = keyword;

        document.querySelectorAll('#acct-seg .seg-btn').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.grade === adminGrade) btn.classList.add('active');
            if (!adminGrade && btn.dataset.grade === '') btn.classList.add('active');
        });
    }

    return {
        init,
        load,
        filterAccounts,
        register,
        reloadAfterRegister,
        update,
        togglePerm,
        updateStatement,
    };
})();

(function () {
    if (document.body.dataset.page !== 'account') return;
    accounts.init();
})();