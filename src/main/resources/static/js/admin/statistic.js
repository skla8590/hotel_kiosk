/* ══════════════════════════════════════════════════════
   statistic.js
══════════════════════════════════════════════════════ */
const statistic = (() => {
    function switchTab(el, targetId) {
        document.querySelectorAll('#stats-tab-bar .tab-item').forEach(t => t.classList.remove('active'));
        el.classList.add('active');
        ['stats-revenue', 'stats-occupancy', 'stats-options'].forEach(id => {
            document.getElementById(id).style.display = id === targetId ? '' : 'none';
        });
    }

    function search() {
        const startDate = document.querySelector('#stats-start')?.value;
        const endDate   = document.querySelector('#stats-end')?.value;
        const unit      = document.querySelector('#stats-unit')?.value;

        updateChartTitle(unit);

/*        // 1. 유효성 체크
        if (!startDate || !endDate) {
            alert("기간을 선택하세요");
            return;
        }*/

        if (startDate > endDate) {
            alert("시작일이 종료일보다 클 수 없습니다");
            return;
        }

        // 2. API 호출
        const url = `/admin/statistic/search?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&unit=${encodeURIComponent(unit)}`;

        _ajax('GET', url, null, (err, data) => {
            if (err) {
                console.log(err);
                return;
            }

            // 3. 화면 갱신
            renderStatSummary(data);
        });
    }

    function updateChartTitle(unit) {
        const paymentTitle = document.getElementById('revenueTitle');

        if (unit === 'DAY') {
            paymentTitle.innerText = '일별 매출 추이';
        } else if (unit === 'WEEK') {
            paymentTitle.innerText = '주별 매출 추이';
        } else if (unit === 'MONTH') {
            paymentTitle.innerText = '월별 매출 추이';
        }
    }

    function renderStatSummary(data) {
        if (!data) return;

        document.getElementById('totalRevenue').innerText =
            `₩${Math.floor((data.totalRevenue || 0) / 10000)}만`;

        document.getElementById('avgOccupancy').innerText =
            `${data.avgOccupancy || 0}%`;

        document.getElementById('totalCheckIn').innerText =
            `${data.totalCheckIn || 0}건`;

        document.getElementById('revenueGrowth').innerText =
            `↑ 전월 대비 +${Math.floor((data.revenueGrowth || 0))}%`;

        document.getElementById('occupancyGoal').innerText =
            `목표 ${data.occupancyGoal || 0}%`;

        document.getElementById('checkInGrowth').innerText =
            `↑ 전월 대비 +${(data.checkInGrowth || 0)}%`;


        renderPaymentChart(data.paymentRevenues);
        renderRoomChart(data.roomRates);
        renderOptionChart(data.optionRates);
    }

    function renderRoomChart(roomRates) {
        const container = document.querySelector('#stats-occupancy .donut-legend');

        if (!container) return;

        // 기존 내용 제거
        container.innerHTML = '';

        if (!roomRates || roomRates.length === 0) {
            container.innerHTML = `
                <div class="legend-row">
                    <span class="legend-dot" style="background:#ccc"></span>
                    <span>조회된 정보를 찾을 수 없습니다</span>
                    <span class="legend-val">-</span>
                </div>
        `;
            return;
        }

        roomRates.forEach(item => {
            const color = item.color || '#ccc';
            const label = item.label || '-';
            const rate = item.rate || 0;

            const row = document.createElement('div');
            row.className = 'legend-row';

            row.innerHTML = `
            <span class="legend-dot" style="background:${color}"></span>
            <span>${label}</span>
            <span class="legend-val">${rate}%</span>
        `;

            container.appendChild(row);
        });
    }

    function renderOptionChart(optionRates) {
        const container = document.querySelector('#stats-options .chart-bars');

        if (!container) return;

        // 기존 내용 제거
        container.innerHTML = '';

        if (!optionRates || optionRates.length === 0) {
            container.innerHTML = `
            <div class="bar-col">
                <div class="bar-fill gold" style="height:0%"></div>
                    <div>조회된 정보를 찾을 수 없습니다</div>
                <div class="bar-lbl">-</div>
            </div>
        `;
            return;
        }

        optionRates.forEach(item => {
            const label = item.label || '-';
            const rate = item.rate || 0;

            const col = document.createElement('div');
            col.className = 'bar-col';

            col.innerHTML = `
            <div class="bar-fill gold" style="height:${rate}%"></div>
            <div class="bar-lbl">${label}</div>
        `;

            container.appendChild(col);
        });
    }

    function renderPaymentChart(data) {
        const container = document.getElementById('paymentChart');
        // 기존 제거 (중요)
        container.innerHTML = '';

        if (!data || data.length === 0) {
            container.innerHTML = `
            <div class="bar-col">
                <div class="bar-fill" style="height:0%"></div>
                    <div>조회된 정보를 찾을 수 없습니다</div>
                <div class="bar-lbl">-</div>
            </div>
        `;
            return;
        }

        data.forEach(bar => {
            const col = document.createElement('div');
            col.className = 'bar-col';
            col.innerHTML = `
            <div class="bar-fill ${bar.isGold ? 'gold' : ''}"
                 style="height:${bar.height || 0}%"></div>
            <div class="bar-lbl">${bar.label || '-'}</div>
        `;

            container.appendChild(col);
        });
    }

    function load() {
        const startDate = document.querySelector('#stats-start')?.value;
        const endDate = document.querySelector('#stats-end')?.value;
        const unit = document.querySelector('#stats-unit')?.value;

        fetch(`/admin/statistic/search?unit=${unit}&startDate=${startDate}&endDate=${endDate}`)
            .then(res => res.json())
            .then(data => {
                renderPaymentChart(data.paymentRevenues);

                // 필요 시 다른 차트도 같이 갱신 가능
                renderRoomChart(data.roomRates);
                renderOptionChart(data.optionRates);

            })
            .catch(err => console.error(err));
    }

    function downloadExcel() {
        const startDate = document.querySelector('#stats-start')?.value;
        const endDate = document.querySelector('#stats-end')?.value;
        window.location.href = `/admin/statistic/excel?startDate=${startDate}&endDate=${endDate}`;
    }

    function init() {
        load();
    }

    return {
        init,
        search,
        switchTab,
        load,
        downloadExcel
    };
})();

(function () {
    if (document.body.dataset.page !== 'statistic') return;

    statistic.init();
})();