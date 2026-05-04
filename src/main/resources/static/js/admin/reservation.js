/* ══════════════════════════════════════════════════════
   reservation.js
══════════════════════════════════════════════════════ */
window.reservation = (() => {

    function STATUS_MAP(status) {
        const map = {
            'Reserved': {text: '예약', class: 'badge-blue'},
            'In': {text: '체크인', class: 'badge-yellow'},
            'Out': {text: '체크아웃', class: 'badge-orange'},
            'Cancelled': {text: '취소', class: 'badge-red'}
        };

        const s = map[status] || {text: '기타', class: 'badge-gray'};

        return `<span class="badge ${s.class}">${s.text}</span>`;
    }

    let currentTab = 'RESERVATION';

    function updateTitle() {
        const el = document.getElementById('page-title');
        if (!el) return;

        el.textContent = (currentTab === 'REFUND')
            ? '환불 관리'
            : '예약 관리';
    }

    function changeTab(tab, e) {
        currentTab = tab;

        // 1. 탭 활성화 UI 처리
        document.querySelectorAll('.tab-item').forEach(t => t.classList.remove('active'));
        e.target.classList.add('active');

        // 2. 필터 요소 초기화
        const keywordInput = document.querySelector('#res-keyword');
        const dateInput = document.querySelector('#res-date');
        const statusSelect = document.querySelector('#res-status');

        if (keywordInput) keywordInput.value = '';  // 검색어 초기화
        if (dateInput) dateInput.value = '';        // 날짜 초기화
        if (statusSelect) statusSelect.value = '';  // 상태 필터 초기화

        // 3. 환불 내역 탭일 때 상태 필터 숨기기 (선택 사항)
        if (statusSelect) {
            statusSelect.style.display = (tab === 'REFUND') ? 'none' : 'inline-block';
        }

        // 4. 제목 변경 및 재조회
        updateTitle();
        search();
    }

    function renderModeUI() {
        const titleEl = document.getElementById('page-title');
        if (!titleEl) return;

        if (currentTab === 'REFUND') {
            titleEl.textContent = '환불 관리';
        } else {
            titleEl.textContent = '예약 관리';
        }
    }

    window.movePage = function (page) {
        reservation.search(page)
    }

    /** 예약 목록 조회 */
    async function search(page = 1) {
        const keyword = document.querySelector('#res-keyword')?.value.trim();
        const date = document.querySelector('#res-date')?.value;

        // 수정: 'Refund' 대신 'Cancelled'로 변경하여 취소된 내역을 불러오도록 설정
        const status = (currentTab === 'REFUND') ? 'Cancelled' : document.querySelector('#res-status')?.value;

        const params = {page};
        if (keyword) params.keyword = keyword;
        if (date) params.date = date;
        if (status) params.payStatus = status;

        try {
            const res = await axios.get('/admin/reservation/search', {params});
            const data = res.data;

            renderTable(data.dtoList);
            renderPagination(data);
        } catch (e) {
            console.error(e);
        }
    }

    function renderPagination(pageData) {
        const container = document.querySelector('.pagination');

        let html = '';

        if (pageData.prev) {
            html += `<a onclick="movePage(${pageData.start - 1})">이전</a>`;
        }

        for (let i = pageData.start; i <= pageData.end; i++) {
            html += `
            <a onclick="movePage(${i})"
               class="${i === pageData.page ? 'active' : ''}">
               ${i}
            </a>
        `;
        }

        if (pageData.next) {
            html += `<a onclick="movePage(${pageData.end + 1})">다음</a>`;
        }

        container.innerHTML = html;
    }

    /** 예약 상세 로드 → 모달 */
    function loadDetail(id) {
        currentResId = id;
        updatePreviewPrice();
        _ajax('GET', `/admin/reservation/${id}`, {}, (err, data) => {
            if (err) return;

            currentStatus = data.payStatus;

            document.querySelector('#res-edit-checkin').value = data.checkinDate;
            document.querySelector('#res-edit-checkout').value = data.checkoutDate;

            const set = (sel, val) => {
                const el = document.querySelector(sel);
                if (el) el.textContent = val ?? '-';
            };

            set('#detail-id', data.reservationId);
            set('#detail-guest', data.memberName);
            set('#detail-room', data.roomNo);
            set('#detail-checkin', data.checkinDate);
            set('#detail-checkout', data.checkoutDate);
            set('#detail-people', data.regPeople);

            const opt = data.options || {};

            const setWithUnit = (sel, val, unit) => {
                const el = document.querySelector(sel);
                if (!el) return;

                if (val === null || val === undefined) {
                    el.textContent = '-';
                } else {
                    el.textContent = `${val}${unit}`;
                }
            };

            setWithUnit('#opt-breakfast', opt.breakfast ?? 0, '명');
            setWithUnit('#opt-dinner', opt.dinner ?? 0, '명');
            setWithUnit('#opt-facility', opt.facility ?? 0, '회');
            setWithUnit('#opt-amenity', opt.amenity ?? 0, '개');

            loadRoomOptions(data.roomNo);

            // _fillDetailModal(data);
            openModal('modal-res-detail');

            loadRoomOptions(data.roomNo, () => {
                updatePreviewPrice(); // 여기서 실행
            });

            bindPriceWatcher();
        });
    }

    /* 모달 내 변경 값 탐지 */
    const bindPriceWatcher = () => {
        const checkin = document.querySelector('#res-edit-checkin');
        const checkout = document.querySelector('#res-edit-checkout');
        const room = document.querySelector('#res-edit-room');

        const handler = () => updatePreviewPrice();

        checkin.addEventListener('change', handler);
        checkout.addEventListener('change', handler);
        room.addEventListener('change', handler);
    };

    /* 요금 계산 */
    function updatePreviewPrice() {

        const checkin = document.querySelector('#res-edit-checkin').value;
        const checkout = document.querySelector('#res-edit-checkout').value;
        const roomNo = document.querySelector('#res-edit-room').value;

        if (!checkin || !checkout || !roomNo) return;

        const payload = {
            reservationId: currentResId,
            checkinDate: checkin,
            checkoutDate: checkout,
            roomNo: roomNo
        };

        _ajax('POST', '/admin/reservation/preview-price', payload, (err, data) => {
            if (err) return;

            document.querySelector('#detail-changed-amount').textContent =
                formatCurrency(data);
        });
    }

    /* 금액 단위 */
    function formatCurrency(val) {
        if (!val && val !== 0) return '-';
        return val.toLocaleString() + '원';
    }

    /** 저장 */
    function save(id) {
        const payload = {
            checkinDate: document.querySelector('#res-edit-checkin')?.value,
            checkoutDate: document.querySelector('#res-edit-checkout')?.value,
            roomNo: Number(document.querySelector('#res-edit-room')?.value),
            payStatus: selectedStatus
        };
        _ajax('PUT', `/admin/reservation/${id}`, payload, (err) => {
            if (err) {
                showAlertModal('저장이 실패되었습니다');
                return;
            }
            showAlertModal('변경 사항이 저장되었습니다.');
            closeModal('modal-res-detail');
            loadReservations();
        });
    }

    /** 상태 변경 */
    let currentStatus = null;
    let selectedStatus = null;

    function renderTable(list) {
        const tbody = document.getElementById('tbody-reservations');
        // .tbl 클래스 내부의 thead 요소를 선택 (tr까지 선택하지 말고 head에서 멈춤)
        const thead = document.querySelector('.tbl thead');

        if (!tbody || !thead) {
            console.error("테이블 요소를 찾을 수 없습니다.");
            return;
        }

        if (!list || list.length === 0) {
            tbody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align:center;padding:28px;color:var(--c-txt-3)">
                    조회된 데이터가 없습니다.
                </td>
            </tr>
        `;
            return;
        }

        // 탭에 따라 헤더 행(tr)을 생성하여 삽입
        if (currentTab === 'RESERVATION') {
            thead.innerHTML = `
            <tr>
                <th>예약번호</th>
                <th>고객명</th>
                <th>객실</th>
                <th>체크인</th>
                <th>체크아웃</th>
                <th>상태</th>
                <th>상세</th>
            </tr>
        `;

            tbody.innerHTML = list.map(r => `
            <tr>
                <td class="fw700">${r.reservationId}</td>
                <td>${r.memberName}</td>
                <td>${r.roomNo}</td>
                <td>${r.checkinDate}</td>
                <td>${r.checkoutDate}</td>
                <td>${STATUS_MAP(r.status)}</td>
                <td>
                    <button class="btn btn-ghost btn-xs" onclick="reservation.loadDetail('${r.reservationId}')">
                        상세
                    </button>
                </td>
            </tr>
        `).join('');
        } else if (currentTab === 'REFUND') {
            // 헤더에서 '환불금액'과 '처리' 제거
            thead.innerHTML = `
            <tr>
                <th>예약번호</th>
                <th>고객명</th>
                <th>객실</th>
                <th>체크인</th>
                <th>결제금액</th>
                <th>상태</th>
            </tr>
            `;

            // 데이터 행에서 해당 컬럼들 제거
            tbody.innerHTML = list.map(r => `
            <tr>
                <td class="fw700">${r.reservationId}</td>
                <td>${r.memberName}</td>
                <td>${r.roomNo}</td>
                <td>${r.checkinDate}</td>
                <td>₩${Number(r.totalAmount ?? 0).toLocaleString()}</td>
                <td><span class="badge badge-red">취소 완료</span></td>
            </tr>
            `).join('');
        }
    }

    async function cancel(id) {
        if (!confirm('환불을 처리하시겠습니까?')) return;

        try {
            const res = await axios.put(`/admin/reservation/${id}/cancel`);

            if (!res.data.success) {
                showAlertModal(res.data.message);
                return;
            }

            showAlertModal(res.data.message);

            // 모달 닫기
            closeModal('modal-res-detail');

            // 탭 active 변경
            const refundTabBtn = document.querySelectorAll('.tab-item')[1];
            reservation.changeTab('REFUND', {target: refundTabBtn});

            updateTitle();
            search();
        } catch (e) {
            showAlertModal('서버 오류입니다.');
        }
    }

    function loadRoomOptions(selectedRoomNo = null, callback) {
        _ajax('GET', '/admin/reservation/rooms', {}, (err, list) => {
            if (err) return;

            const select = document.getElementById('res-edit-room');
            if (!select) return;

            select.innerHTML = list.map(roomNo => `
            <option value="${roomNo}" ${roomNo === selectedRoomNo ? 'selected' : ''}>
                ${roomNo}호
            </option>
        `).join('');

            if (callback) callback();
        });
    }

    function loadReservations() {
        _ajax('GET', '/admin/reservation/list', {}, (err, data) => {
            if (err) return;
            renderTable(data);
        });
    }

    function init() {
        search();
    }

    return {
        init,
        updateTitle,
        changeTab,
        search,
        cancel,
        loadDetail,
        save,
        movePage
    };
})();

document.addEventListener('DOMContentLoaded', () => {
    reservation.updateTitle();
});

(function () {
    if (document.body.dataset.page !== 'reservation') return;

    reservation.init();
})();