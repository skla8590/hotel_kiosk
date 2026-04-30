/* 특정 ID를 지정해서 모달 열기 */
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('hidden');
}

/* 특정 ID를 지정해서 모달 닫기 */
function closeModal(modalId) {
    // 인자가 없으면 열려있는 모든 모달을 닫도록 처리 (방어 코드)
    if (!modalId) {
        document.querySelectorAll('.modal-overlay').forEach(m => m.classList.add('hidden'));
        return;
    }
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('hidden');
}