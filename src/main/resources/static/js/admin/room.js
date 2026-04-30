/* ══════════════════════════════════════════════════════
   room.js
══════════════════════════════════════════════════════ */
const room = (() => {
    function load(id) {
        _ajax('GET', `/admin/room/list/${id}`, {}, (err, data) => {
            if (err) return;
            renderGrid(data)
        });
    }
    const CHIP = {
        CHECKIN: {
            class: 'chip-checkin',
            label: '체크인',
            status: 'Occupied'
        },
        CHECKOUT: {
            class: 'chip-checkout',
            label: '체크아웃대기',
            status: 'Cleaning Required'
        },
        CLEANING: {
            class: 'chip-cleaning',
            label: '청소중',
            status: 'Cleaning'
        },
        INSPECT: {
            class: 'chip-inspect',
            label: '점검중',
            status: 'Maintenance'
        },
        EMPTY: {
            class: 'chip-empty',
            label: '공실',
            status: 'Available'
        }
    };

    const STATUS_MAP = {
        Cleaning: 'CLEANING',
        Maintenance: 'INSPECT',
        Occupied: 'CHECKIN',
        'Cleaning Required': 'CHECKOUT'
    };

    function switchFloor(btn) {
        document.querySelectorAll('.floor-tab').forEach(t => t.classList.remove('active'));
        btn.classList.add('active');
        const floor = btn.dataset.floor;
        _ajax('GET', `/admin/room/${floor}`, { floor }, (err, data) => {
            if (err) return;
            renderGrid(data);
        });
    }

    function renderGrid(list) {
        const grid = document.getElementById('room-grid');
        if (!grid) return;

        grid.innerHTML = list.map(r => {
            const key = STATUS_MAP[r.currentStatus] || 'EMPTY';
            const chip = CHIP[key];

            return `
      <div class="room-card ${chip.card}" onclick="room.openDetail('${r.roomFloor}', '${r.roomNo}')">
        <div class="room-num">${r.roomNo}</div>
        <div class="room-type">${r.roomName}</div>
        <div class="room-price">인원 ${r.maxPeople}명 · ₩${Number(r.basePrice).toLocaleString()}</div>
        <div class="room-status">
          <span class="room-chip ${chip.class}">${chip.label}</span>
        </div>
      </div>`;
        }).join('');
    }

    function openDetail(id, no) {
        _ajax('GET', `/admin/room/${id}/${no}`, {}, (err, data) => {
            if (err) return;
            currentRoomId = no;

            document.getElementById('rd-no').textContent = data.roomNo;
            document.getElementById('rd-name').value = data.roomName;
            document.getElementById('rd-type').value = data.roomType;
            document.getElementById('rd-view').value = data.roomView;
            document.getElementById('rd-price').value = data.basePrice;
            document.getElementById('rd-people').value = data.maxPeople;
            document.getElementById('rd-bed').value = data.bedType;
            document.getElementById('rd-area').value = data.area;
            document.getElementById('rd-status').value = data.currentStatus;

            openModal('modal-room-detail');
        });
    }

    function getCurrentFloor() {
        const activeTab = document.querySelector('.floor-tab.active');
        return activeTab ? activeTab.dataset.floor : null;
    }

    function search() {
        const keyword = document.querySelector('#room-keyword')?.value.trim();
        const status = document.querySelector('#room-type-filter')?.value;
        const floor = getCurrentFloor();

        const params = new URLSearchParams();

        if (keyword) {
            params.append("keyword", keyword);
        }

        if (status !== null && status !== "") {
            params.append("type", status);
        }

        if (floor != null) {
            params.append("floor", floor);
        }

        _ajax('GET', `/admin/room/search?${params.toString()}`, {}, (err, data) => {
            if (err) return;
            renderGrid(data);
        });
    }

    function register() {
        const payload = {
            roomNo: document.querySelector('#reg-room-no')?.value,
            roomName: document.querySelector('#reg-room-name')?.value,
            roomType: document.querySelector('#reg-room-type')?.value,
            roomView: document.querySelector('#reg-room-view')?.value,
            basePrice: document.querySelector('#reg-room-price')?.value,
            maxPeople: document.querySelector('#reg-room-people')?.value,
            bedType: document.querySelector('#reg-room-bed')?.value,
            area: document.querySelector('#reg-room-area')?.value,
            currentStatus: document.querySelector('#reg-room-status')?.value,
        };
        _ajax('POST', '/admin/room', payload, (err) => {
            if (err) { showAlertModal('등록이 실패되었습니다.'); return; }
            resetForm();
            closeModal('modal-room-reg');
            openModal('modal-room-reg-done');
            const floor = getCurrentFloor();
            load(floor);
        });
    }

    function resetForm() {
        document.querySelector('#reg-room-no').value = '';
        document.querySelector('#reg-room-name').value = '';
        document.querySelector('#reg-room-price').value = '';
        document.querySelector('#reg-room-people').value = '';
        document.querySelector('#reg-room-area').value = '';

        // select는 기본값으로 리셋
        document.querySelector('#reg-room-type').value = 'Standard';
        document.querySelector('#reg-room-view').value = 'City';
        document.querySelector('#reg-room-bed').selectedIndex = 0;
        document.querySelector('#reg-room-status').value = 'Available';
    }

    function modify() {
        const payload = {
            roomNo: currentRoomId,
            roomName: document.getElementById('rd-name').value,
            roomType: document.getElementById('rd-type').value,
            roomView: document.getElementById('rd-view').value,
            basePrice: document.getElementById('rd-price').value,
            maxPeople: document.getElementById('rd-people').value,
            bedType: document.getElementById('rd-bed').value,
            area: document.getElementById('rd-area').value,
            currentStatus: document.getElementById('rd-status').value
        };

        const floor = getCurrentFloor(); // 현재 층 필요
        _ajax('PUT', `/admin/room/${floor}/${currentRoomId}`, payload, (err) => {
            if (err) {showAlertModal('수정이 실패하였습니다. 다시 시도해주세요.');return;}

            showAlertModal('수정이 성공되었습니다.');

            closeModal('modal-room-detail');

            // 새로고침
            load(floor);
        });
    }

    function remove(id) {
        if (id == null) {
            alert("삭제할 객실 ID가 없습니다.");
            return;
        }
        _ajax('DELETE', `/admin/room/${id}`, {}, (err, data) => {
            if (err?.status === 409) { openModal('modal-room-delete-check'); return; }
            if (err) { showAlertModal('삭제가 실패되었습니다. 다시 시도해주세요.'); return; }
            showAlertModal("객실 삭제가 완료되었습니다.")
            closeModal('modal-room-detail');
            const floor = getCurrentFloor();
            load(floor);
        });
    }

    function init() {
        const floor = getCurrentFloor();
        load(floor);
    }

    return {
        init,
        switchFloor,
        search,
        modify,
        openDetail,
        register,
        remove
    };
})();

(function () {
    if (document.body.dataset.page !== 'room') return;

    room.init();
})();