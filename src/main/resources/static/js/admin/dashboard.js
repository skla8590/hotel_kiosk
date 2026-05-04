/* ══════════════════════════════════════════════════════
   dashboard.js
══════════════════════════════════════════════════════ */
const dashboard = (() => {
    function load() {
        // _ajax('GET', '/admin/dashboard/kpi', {}, (err, data) => {
        //     if (!err || data) {
        //         document.querySelector('.kpi-total-rooms').textContent   = data.totalRooms;
        //         document.querySelector('.kpi-current-guests').textContent = data.currentGuests;
        //         document.querySelector('.kpi-today-checkin').textContent  = data.todayCheckIn;
        //         document.querySelector('.kpi-stock-alert').textContent    = data.stockAlert;
        //     }
        // });
        _ajax('GET', '/admin/dashboard/stock/alerts', {}, (err, data) => {
            if (!err || data) {
                renderAlertList(data);
            }
        });
    }

    function renderAlertList(list) {
        const container = document.querySelector('.alert-list');
        container.innerHTML = '';

        if (!list || list.length === 0) {
            container.innerHTML = '<div style="text-align:center;padding:20px;">재고 경고 없음</div>';
            return;
        }

        list.forEach(item => {
            const row = document.createElement('div');
            row.className = 'alert-row ' + (item.stockStatus === 'Shortage' ? 'warn' : 'danger');

            row.innerHTML = `
            <div class="alert-left">
                <span class="alert-name">${item.stockName}</span>
                <span class="alert-detail">잔량 ${item.stockCount}개 / 최소 ${item.minStock}개</span>
            </div>
            <span class="badge ${item.level === 'DANGER' ? 'badge-red' : 'badge-yellow'}">
                ${item.level === 'DANGER' ? '경고' : '부족'}
            </span>
        `;

            container.appendChild(row);
        });
    }

    function init() {
        load();
    }

    return {
        init,
        load
    };
})();

(function () {
    if (document.body.dataset.page !== 'dashboard') return;

    dashboard.init();
})();

// document.addEventListener('DOMContentLoaded', () => {
//     if (document.getElementById('page-dashboard')) dashboard.load();
// });