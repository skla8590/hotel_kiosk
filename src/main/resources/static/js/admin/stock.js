/* ══════════════════════════════════════════════════════
   stock.js
══════════════════════════════════════════════════════ */
const stock = (() => {
    function load() {
        _ajax('GET', `/admin/stock/list`, {}, (err, data) => {
            if (err) return;
            renderList(data);
        });
    }
    function search() {
        const keyword = document.querySelector('#search-keyword')?.value.trim();
        const status = document.querySelector('#search-status')?.value;

        const params = new URLSearchParams();

        if (keyword) params.append("keyword", keyword);

        const statusMap = {
            WARNING: "Shortage",
            NORMAL: "Clear"
        };

        if (status) params.append("status", statusMap[status]);

        _ajax('GET', `/admin/stock/search?${params.toString()}`, {}, (err, data) => {
            if (err) {
                console.log(err)
                return;
            }
            renderList(data);
        });
    }

    function openEdit(id, name, qty, min, loc) {
        document.getElementById('inv-edit-id').value = id || 0;
        document.getElementById('inv-edit-title').value = name || '';
        document.getElementById('inv-edit-qty').value = qty || 0;
        document.getElementById('inv-edit-min').value = min || 0;

        openModal('modal-inv-edit');
    }

    function register() {
        const nameEl = document.querySelector('#inv-name');
        const qtyEl = document.querySelector('#inv-qty');
        const minEl = document.querySelector('#inv-min');

        const payload = {
            stockName: nameEl?.value,
            stockCount: qtyEl?.value,
            minStock: minEl?.value,
        };
        if (!payload.stockName || !payload.stockCount || !payload.minStock) {
            showAlertStockModal('입력 칸이 비어 있습니다.', () => {
                if (!payload.stockName) {
                    console.log("확인2")
                    nameEl.focus();
                } else if (!payload.stockCount) {
                    console.log("확인3")
                    qtyEl.focus();
                } else {
                    console.log("확인4")
                    minEl.focus();
                }
            });
            return;
        }
        _ajax('POST', '/admin/stock', payload, (err) => {
            if (err) { showAlertModal('등록이 실패되었습니다. 다시 시도해주세요.'); return; }
            document.querySelector('#inv-name').value = '';
            document.querySelector('#inv-qty').value = '';
            document.querySelector('#inv-min').value = '';
            showAlertModal("새로운 재고가 등록되었습니다.");
            load();
        });
    }

    function showAlertStockModal(message, onClose) {
        const msgEl = document.getElementById('modal-stock-alert-msg');
        msgEl.innerHTML = message;

        window.__alertCloseCallback = onClose;

        openModal('modal-stock-alert');
    }

    window.closeStockModal = function(id) {
        const modal = document.getElementById(id);

        modal.classList.remove('open');

        if (window.__alertCloseCallback) {
            setTimeout(window.__alertCloseCallback, 0);
            window.__alertCloseCallback = null;
        }
    };

    function update() {
        const id = document.querySelector('#inv-edit-id')?.value;

        if (!id) {
            alert("잘못된 접근입니다. (ID 없음)");
            return;
        }
        const payload = {
            stockName: document.querySelector('#inv-edit-title')?.value,
            stockCount: document.querySelector('#inv-edit-qty')?.value,
            minStock: document.querySelector('#inv-edit-min')?.value,
        };
        _ajax('PUT', `/admin/stock/${id}`, payload, (err) => {
            if (err) {
                console.log(err);
                showAlertModal('수정이 실패되었습니다.');
                return;
            }
            showAlertModal('수정이 완료되었습니다.')
            closeModal('modal-inv-edit');
            load();
        });
    }

    function renderList(list) {
        const container = document.getElementById('inv-list');
        if (!container) return;

        container.innerHTML = list.map(item => {
            const pct = Math.min(100, Math.round(item.stockCount / item.minStock * 100));

            const { color, badge, badgeCls } =
                item.stockCount < item.minStock
                    ? { color: 'var(--c-red)', badge: '부족', badgeCls: 'badge-red' }
                    : { color: 'var(--c-green)', badge: '정상', badgeCls: 'badge-green' };

            return `
        <div class="inv-item">
          <div class="inv-ico">📦</div>

          <div class="inv-info">
            <div class="inv-name">${item.stockName}</div>
          </div>

          <div class="inv-bar-col">
            <div class="inv-bar-labels">
              <span>${item.stockCount}개</span>
              <span>최소 ${item.minStock}개</span>
            </div>
            <div class="inv-bar-bg">
              <div class="inv-bar-fill" style="width:${pct}%;background:${color}"></div>
            </div>
          </div>

          <div class="inv-qty" style="color:${color}">
            ${item.stockCount} / ${item.minStock}<br>
            <span class="badge ${badgeCls}">${badge}</span>
          </div>

          <div class="inv-actions">
            <button class="btn btn-ghost btn-sm"
                data-id="${item.stockId}"
                data-name="${item.stockName}"
                data-qty="${item.stockCount}"
                data-min="${item.minStock}"
                onclick="stock.openEditFromBtn(this)">
              수정
            </button>
          </div>
        </div>`;
        }).join('');
    }

    function openEditFromBtn(btn) {
        const id = btn.dataset.id;
        const name = btn.dataset.name;
        const qty = btn.dataset.qty;
        const min = btn.dataset.min;

        openEdit(id, name, qty, min);
    }

    function init() {
        load();
    }

    return {
        init,
        load,
        register,
        update,
        search,
        openEditFromBtn,
        closeStockModal
    };
})();

(function () {
    if (document.body.dataset.page !== 'stock') return;

    stock.init();
})();