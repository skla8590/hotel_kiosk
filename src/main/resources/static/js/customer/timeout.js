/* 타임아웃 */
let idleTimer // 유휴 시간
let countdownTimer // 카운트다운 타이머
let remaining = 10 // 카운트다운 타이머의 남은 시간

/* 유휴 시간 리셋 : 활동이 감지되면 유휴 시간 초기화 */
function resetIdleTimer() {
    clearTimeout(idleTimer) // 돌고있는 타이머 취소
    idleTimer = setTimeout(showTimeoutModal, 60 * 1000)  // 1분 후 타임아웃 모달 표시 (setTimeout : 딱 한 번)
    // idleTimer = setTimeout(showTimeoutModal, 10 * 1000) // 타임아웃 테스트용
}

/* 타임아웃 시(1분 경과), 모달 열기 + 카운트다운 */
function showTimeoutModal() {
    remaining = 10
    document.querySelector('#timeoutSec').textContent = remaining
    openModal('timeoutModal')

    // 1초마다 timeoutSec 카운트다운 (setInterval : 지정 시간마다)
    countdownTimer = setInterval(function () {
        remaining--
        document.getElementById('timeoutSec').textContent = remaining // 줄어드는 시간 표시

        if (remaining <= 0) {
            clearInterval(countdownTimer) // 반복 멈추기

            if (window.location.pathname === '/JHotel/main') {
                window.location.href = '/JHotel'
            } else {
                window.location.href = '/JHotel/main'
            }
        }
    }, 1000)  // 1초마다 실행
}

/* 화면 동작 감지 시 유휴 시간 초기화 */
document.addEventListener('click', function () {
    // 모달 창이 열려있을 때 사용자가 클릭한다면, 카운트다운 정지 + 모달 나가기
    if (!document.getElementById('timeoutModal').classList.contains('hidden')) {
        clearInterval(countdownTimer)
        closeModal('timeoutModal')  // modal.js의 closeModal 사용
    }
    resetIdleTimer() // 1분 타이머 재시작
})

resetIdleTimer()  // 페이지 로드 시 1분 타이머 시작