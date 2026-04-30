/* ── axios 기본 설정 ── */
axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

if (csrfToken && csrfHeader) {
  axios.defaults.headers.common[csrfHeader] = csrfToken;
}

// 에러 공통 처리
function handleError(error) {
  console.error(error);

  if (error.response) {
    alert(error.response.data?.message || '서버 오류 발생');
  } else {
    alert('네트워크 오류');
  }
}

/* ══════════════════════════════════════════════════════
   main.js  |  공통 유틸 (모달·시계·권한토글·AJAX 래퍼)
   모든 페이지에서 공통으로 로드
══════════════════════════════════════════════════════ */

/* ── 시계 ── */
function updateClock() {
  const n = new Date(), p = v => String(v).padStart(2,'0');
  const el = document.getElementById('clock');
  if (el) el.textContent =
      `${n.getFullYear()}-${p(n.getMonth()+1)}-${p(n.getDate())} ${p(n.getHours())}:${p(n.getMinutes())}:${p(n.getSeconds())}`;
}

/* ── 모달 ── */
function openModal(id)  { const el = document.getElementById(id); if (el) el.classList.add('open'); }
function closeModal(id) { const el = document.getElementById(id); if (el) el.classList.remove('open'); }
function showAlertModal(message, callback) {
  const el = document.getElementById('modal-alert-msg');
  el.innerHTML = message;
  openModal('modal-alert');

  // 콜백이 있으면 확인 버튼에 등록
  const confirmBtn = document.getElementById('modal-alert-confirm');
  if (confirmBtn) {
    // 기존 이벤트 중복 방지
    const newBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newBtn, confirmBtn);

    if (callback) {
      newBtn.addEventListener('click', () => {
        closeModal('modal-alert');
        callback();
      });
    } else {
      newBtn.addEventListener('click', () => closeModal('modal-alert'));
    }
  }
}

// 오버레이 배경 클릭으로 닫기
document.addEventListener('click', e => {
  if (e.target.classList.contains('overlay')) e.target.classList.remove('open');
});
// ESC 닫기
document.addEventListener('keydown', e => {
  if (e.key === 'Escape')
    document.querySelectorAll('.overlay.open').forEach(o => o.classList.remove('open'));
});

/* ── 테이블 행 선택 ── */
function selectRow(tr) {
  const tbody = tr.closest('tbody');
  if (tbody) tbody.querySelectorAll('tr').forEach(r => r.classList.remove('row-selected'));
  tr.classList.add('row-selected');
}

/* ── 권한 토글 ── */
function togglePerm(btn, type) {
  const wrap = btn.closest('.perm-toggle');
  wrap.querySelectorAll('.perm-btn').forEach(b => b.classList.remove('on'));
  btn.classList.add('on');
}

/* ── 계정 필터 세그먼트 ── */
function filterAccounts(el, type) {
  document.querySelectorAll('#acct-seg .seg-btn').forEach(b => b.classList.remove('active'));
  el.classList.add('active');
  _ajax('GET', '/admin/accounts', { type }, (err, data) => {
    if (!err) renderAccountTable(data?.list ?? []);
  });
}

/* ══════════════════════════════════════════════════════
   공통 AJAX 래퍼 (axios 사용)
══════════════════════════════════════════════════════ */
function _ajax(method, url, data, cb) {
  const config = {
    method: method.toLowerCase(),
    url,
    headers: { 'Content-Type': 'application/json' },
  };

  if (method === 'GET') {
    config.params = data;
  } else {
    config.data = data;
  }

  axios(config)
      .then(res => cb && cb(null, res.data))
      .catch(err => cb && cb(err, null));
}

/* ── 초기화 ── */
document.addEventListener('DOMContentLoaded', () => {
  updateClock();
  setInterval(updateClock, 1000);
});
