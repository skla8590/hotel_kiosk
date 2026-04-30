/* ══════════════════════════════════════════════════════
   policy.js  (MVC + Thymeleaf 방식)
   - 초기 시즌 목록, 옵션 목록, 요일 정책은 Thymeleaf가 렌더링
   - 등록/수정 후 window.location.reload()로 목록 갱신
   - 요일 정책 저장은 axios 유지
══════════════════════════════════════════════════════ */
const policy = (() => {

    /* 정책 탭 이동 */
    function switchTab(el, targetId) {
        document.querySelectorAll('#policy-tab-bar .tab-item')
            .forEach(t => t.classList.remove('active'));
        el.classList.add('active');

        ['policy-season', 'policy-option']
            .forEach(id => {
                document.getElementById(id).style.display = (id === targetId ? '' : 'none');
            });

        if (targetId === 'policy-season') {
            resetForm();
        }
    }

    let isEditMode = false;

    function toggleButtons() {
        const addBtn = document.getElementById('btn-add');
        const saveBtn = document.getElementById('btn-save');
        addBtn.style.display = isEditMode ? 'none' : '';
        saveBtn.style.display = '';
    }

    function getSeasonData() {
        const name = document.querySelector('#season-name');
        const repeatType = document.querySelector('#season-repeat-type');
        const start = document.querySelector('#season-start');
        const end = document.querySelector('#season-end');
        const extra = document.querySelector('#season-extra');
        const rateValue = parseFloat(extra.value.replace('%', '').replace('+', ''));

        if (!name.value) { showAlertModal('시즌명을 입력하세요.'); name.focus(); return null; }
        if (!repeatType.value) { showAlertModal('반복 주기를 선택하세요.'); repeatType.focus(); return null; }
        if (!start.value) { showAlertModal('시작일을 선택하세요.'); start.focus(); return null; }
        if (!end.value) { showAlertModal('종료일을 선택하세요.'); end.focus(); return null; }
        if (isNaN(rateValue)) { showAlertModal('요금을 숫자로 입력하세요.'); extra.focus(); return null; }
        if (start.value > end.value) { showAlertModal('종료일은 시작일보다 이후여야 합니다.'); return null; }

        return {
            policyName: name.value,
            repeatType: repeatType.value,
            startDate: start.value,
            endDate: end.value,
            discountRate: rateValue
        };
    }

    /* 시즌 등록 - 완료 후 페이지 새로고침으로 목록 갱신 */
    function addSeason() {
        if (isEditMode) { showAlertModal('신규 등록은 초기화 후 사용하세요.'); return; }

        const data = getSeasonData();
        if (!data) return;

        axios.post('/admin/policy/season', data)
            .then(() => {
                showAlertModal('등록 완료되었습니다.', () => window.location.reload());
                resetForm();
            })
            .catch(handleError);
    }

    /* 시즌 수정 - 완료 후 페이지 새로고침으로 목록 갱신 */
    function updateSeason() {
        if (!isEditMode) { showAlertModal('수정할 항목을 선택하세요.'); return; }

        const policyId = document.getElementById('season-id').value;
        if (!policyId) { showAlertModal('정책 ID가 없습니다.'); return; }

        const data = getSeasonData();
        if (!data) return;

        axios.put(`/admin/policy/season/${policyId}`, data)
            .then(() => {
                showAlertModal('저장 완료되었습니다.', () => {
                    resetForm();
                    window.location.reload();
                });
            })
            .catch(handleError);
    }

    /* 요일 정책 */
    let originalWeekdayValues = {};

    function checkWeeklyChanged() {
        const inputs = document.querySelectorAll('.wd-inp');
        let changed = false;
        inputs.forEach(input => {
            const day = input.closest('.wd-cell').dataset.day;
            if (parseRate(input.value) !== parseRate(originalWeekdayValues[day])) changed = true;
        });
        toggleWeeklySaveButton(changed);
    }

    function toggleWeeklySaveButton(enable) {
        const btn = document.getElementById('btn-weekly-save');
        if (!btn) return;
        if (!isSuperAdmin) { btn.disabled = true; return; }
        btn.disabled = !enable;
    }

    function updateWeekly() {
        if (!isSuperAdmin) { showAlertModal('권한이 없습니다.'); return; }

        const inputs = document.querySelectorAll('.wd-inp');
        let changed = false;
        inputs.forEach(input => {
            const day = input.closest('.wd-cell').dataset.day;
            if (parseRate(input.value) !== parseRate(originalWeekdayValues[day])) changed = true;
        });

        if (!changed) { showAlertModal('변경된 내용이 없습니다.'); return; }

        const map = {};
        document.querySelectorAll('.wd-cell').forEach(cell => {
            const day = cell.dataset.day;
            const value = parseRate(cell.querySelector('.wd-inp').value);
            if (!map[value]) map[value] = [];
            map[value].push(day);
        });

        const list = Object.keys(map).map(rate => ({
            policyName: '요일 정책',
            repeatType: 'Weekly',
            repeatValue: map[rate].join(','),
            discountRate: parseFloat(rate),
            startDate: '2000-01-01',
            endDate: '2099-12-31'
        }));

        axios.put('/admin/policy/weekly', list)
            .then(() => {
                showAlertModal('요일 정책 저장 완료되었습니다.');
                inputs.forEach(input => {
                    const day = input.closest('.wd-cell').dataset.day;
                    input.value = formatRate(input.value);
                    originalWeekdayValues[day] = input.value;
                });
                toggleWeeklySaveButton(false);
                resetForm();
            })
            .catch(handleError);
    }

    function formatRate(value) {
        const num = parseFloat(value) || 0;
        if (num > 0) return `+${num.toFixed(1)}%`;
        if (num < 0) return `${num.toFixed(1)}%`;
        return '0%';
    }

    function parseRate(value) {
        return parseFloat(
            value?.toString().replace('%', '').replace('+', '').trim()
        ) || 0;
    }

    function selectSeasonFromRow(row) {
        isEditMode = true;
        toggleButtons();
        document.getElementById('season-id').value = row.dataset.id;
        document.getElementById('season-name').value = row.dataset.name;
        document.getElementById('season-repeat-type').value = row.dataset.repeat;
        document.getElementById('season-start').value = row.dataset.start;
        document.getElementById('season-end').value = row.dataset.end;
        document.getElementById('season-extra').value = row.dataset.rate;
    }

    function resetForm() {
        isEditMode = false;
        toggleButtons();
        document.querySelector('#season-id').value = '';
        document.querySelector('#season-name').value = '';
        document.querySelector('#season-repeat-type').value = 'None';
        document.querySelector('#season-start').value = '';
        document.querySelector('#season-end').value = '';
        document.querySelector('#season-extra').value = '';
    }

    /* ═════ 옵션 영역 ═════ */
    function openOptionModal() {
        if (!isSuperAdmin) { showAlertModal('권한이 없습니다.'); return; }

        document.getElementById("modal-title").innerText = "옵션 등록";
        document.getElementById("opt-id").value = '';
        document.getElementById("opt-name").value = '';
        document.getElementById("opt-category").value = 'Meal';
        document.getElementById("opt-target").value = 'Common';
        document.getElementById("opt-common").value = '';
        document.getElementById("opt-adult").value = '';
        document.getElementById("opt-child").value = '';

        changeTarget();
        openModal('modal-option');
    }

    /* 옵션 저장 - 완료 후 페이지 새로고침으로 목록 갱신 */
    function saveOption() {
        const id = document.getElementById("opt-id").value;
        const name = document.getElementById("opt-name").value.trim();
        const category = document.getElementById("opt-category").value;
        const target = document.getElementById("opt-target").value;

        let price = 0;
        if (target === 'Common') price = document.getElementById("opt-common").value;
        else if (target === 'Adult') price = document.getElementById("opt-adult").value;
        else if (target === 'Child') price = document.getElementById("opt-child").value;

        if (!name) { showAlertModal("옵션명을 입력해주세요"); return; }
        if (!price) { showAlertModal("금액을 입력해주세요"); return; }

        const payload = {
            optionName: name,
            optionCategory: category,
            optionTarget: target,
            optionPrice: Number(price)
        };

        const url = id ? `/admin/policy/options/${id}` : '/admin/policy/options';
        const method = id ? 'put' : 'post';

        axios[method](url, payload)
            .then(() => {
                showAlertModal(id ? "수정 완료되었습니다." : "등록 완료되었습니다.");
                closeModal('modal-option');
                window.location.reload();
            })
            .catch(handleError);
    }

    function openOptionEdit(id, name, category, target, price) {
        document.getElementById("modal-title").innerText = "옵션 수정";
        document.getElementById("opt-id").value = id;
        document.getElementById("opt-name").value = name;
        document.getElementById("opt-category").value = category;
        document.getElementById("opt-target").value = target;
        document.getElementById("opt-common").value = '';
        document.getElementById("opt-adult").value = '';
        document.getElementById("opt-child").value = '';

        // dataset에서 오면 모두 String → Number로 변환
        const priceNum = Number(price);
        if (target === 'Common') document.getElementById("opt-common").value = priceNum;
        else if (target === 'Adult') document.getElementById("opt-adult").value = priceNum;
        else if (target === 'Child') document.getElementById("opt-child").value = priceNum;

        changeTarget();
        openModal('modal-option');
    }

    function changeTarget() {
        const target = document.getElementById("opt-target").value;
        const common = document.getElementById("opt-common");
        const adult = document.getElementById("opt-adult");
        const child = document.getElementById("opt-child");

        common.disabled = true;
        adult.disabled = true;
        child.disabled = true;

        if (target === 'Common') common.disabled = false;
        else if (target === 'Adult') adult.disabled = false;
        else if (target === 'Child') child.disabled = false;
    }

    function initWeekly() {
        const inputs = document.querySelectorAll('.wd-inp');

        inputs.forEach(input => {
            const day = input.closest('.wd-cell').dataset.day;
            input.value = formatRate(input.value);
            originalWeekdayValues[day] = input.value;
            input.addEventListener('input', checkWeeklyChanged);
            input.addEventListener('blur', () => { input.value = formatRate(input.value); });
        });

        toggleWeeklySaveButton(false);
    }

    function init() {
        resetForm();
        initWeekly();
    }

    return {
        init,
        switchTab,
        addSeason,
        updateSeason,
        updateWeekly,
        selectSeasonFromRow,
        resetForm,
        openOptionModal,
        saveOption,
        openOptionEdit,
        changeTarget,
        initWeekly
    };
})();

(function () {
    if (document.body.dataset.page !== 'policy') return;
    policy.init();
})();