/* ══════════════════════════════════════════════════════
   customer.js
══════════════════════════════════════════════════════ */
window.customer = (() => {
    window.movePage = function(page) {
        customer.search(page)
    }

    function search(page = 1) {
        const keyword = document.querySelector('#cust-keyword')?.value.trim();

        _ajax('GET', `/admin/customer/search`, { page, keyword }, (err, cum) => {
            if (err) {
                console.log(err);
                return;
            }

            console.log(cum); // 반드시 확인

            const data = cum?.data ?? cum;

            if (!data || !data.dtoList) {
                console.error('응답 구조 이상', cum);
                return;
            }

            renderList(data.dtoList);
            renderPagination(data);
        });
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

    function loadDetail(id) {
        _ajax('GET', `/admin/customer/${id}`, {}, (err, data) => {
            if (err) return;
            const set = (sel, val) => {
                const el = document.querySelector(sel);
                if (el) el.textContent = val ?? '-';
            };
            set('#cust-name',    data.memberName);
            set('#cust-phone', formatPhone(data.memberPhone));
            set('#cust-birth',   data.memberBirth);
            set('#cust-cnt',     data.reservationCount + '회');
            set('#cust-pay',     '₩' + Number(data.totalPayment).toLocaleString());
            set('#cust-point',   data.memberPoint + ' pt');
            set('#cust-visit',   data.lastVisit);
        });
    }

    function formatPhone(phone) {
        if (!phone) return '-';
        const cleaned = phone.replace(/\D/g, '');

        if (cleaned.startsWith('02')) {
            return cleaned.replace(/(\d{2})(\d{3,4})(\d{4})/, '$1-$2-$3');
        } else if (cleaned.length === 11) {
            return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        } else if (cleaned.length === 10) {
            return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
        }
        return phone;
    }

    function renderList(list) {
        const tbody = document.getElementById('tbody-customers');
        if (!tbody) return;
        tbody.innerHTML = list.map(c => `
      <tr onclick="selectRow(this);customer.loadDetail('${c.memberNo}')">
        <td>${c.memberName}</td>
        <td>${formatPhone(c.memberPhone)}</td>
        <td>${c.reservationCount}회</td>
      </tr>`).join('');
    }

    function init() {
        const firstRow = document.querySelector('#tbody-customers tr[data-id]');

        if (firstRow) {
            const id = firstRow.dataset.id;

            // 상세 조회
            loadDetail(id);
        }
    }

    return {
        init,
        search,
        loadDetail,
        movePage
    };
})();

(function () {
    if (document.body.dataset.page !== 'customer') return;

    customer.init();
})();