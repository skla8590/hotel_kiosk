/* ═══════════════════════════════════════════════
   mypage.js  (MVC + Thymeleaf 방식)
   - 초기 데이터(아이디, 이메일, 연락처)는 Thymeleaf가 th:value로 렌더링
   - 수정/비밀번호 변경은 axios REST 유지
══════════════════════════════════════════════ */
const mypage = (() => {
    let originalEmail = '';
    let originalPhone = '';
    let emailVerified = false;
    let timerInterval = null;
    let remainSeconds = 300;

    /* Thymeleaf가 렌더링한 초기값을 읽어 기준값으로 저장 */
    function init() {
        originalEmail = document.getElementById('email-input')?.value || '';
        originalPhone = document.getElementById('phone-input')?.value || '';

        const emailInput = document.getElementById('email-input');
        const phoneInput = document.getElementById('phone-input');

        if (emailInput) {
            emailInput.addEventListener('input', () => { emailVerified = false; });
        }
        if (phoneInput) {
            phoneInput.addEventListener('input', () => { emailVerified = false; });
        }

        bindPasswordValidation();
    }

    let resendSeconds = 60;
    let resendTimer = null;

    function sendEmailVerify() {
        const email = document.getElementById('email-input').value.trim();
        const btn = document.getElementById('btn-send-verify');

        if (!email) { showAlertModal('이메일을 입력해주세요.'); return; }

        document.getElementById('verify-code').value = '';
        document.getElementById('verify-timer').textContent = '';

        _ajax('POST', '/admin/email/verify', { email }, (err, res) => {
            if (err || !res.success) {
                showAlertModal(res?.message || '인증번호 발송을 실패하였습니다');
                return;
            }

            emailVerified = false;
            document.getElementById('verify-email-display').value = email;
            openModal('modal-email-verify');
            startTimer();
            startResendTimer(btn);
        });
    }

    function startResendTimer(btn) {
        stopResendTimer();
        resendSeconds = 60;
        btn.disabled = true;
        updateResendText(btn);

        resendTimer = setInterval(() => {
            resendSeconds--;
            updateResendText(btn);
            if (resendSeconds <= 0) {
                stopResendTimer();
                btn.disabled = false;
                btn.textContent = '인증번호 재전송';
            }
        }, 1000);
    }

    function updateResendText(btn) {
        btn.textContent = `재전송 (${resendSeconds}초)`;
    }

    function stopResendTimer() {
        if (resendTimer) { clearInterval(resendTimer); resendTimer = null; }
    }

    function bindPasswordValidation() {
        const newPwInput = document.getElementById('new-pw');
        const confirmPwInput = document.getElementById('confirm-pw');
        const pwMsg = document.getElementById('pw-msg');
        const confirmMsg = document.getElementById('pw-confirm-msg');

        if (!newPwInput || !confirmPwInput) return;

        const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/;

        newPwInput.addEventListener('keyup', () => {
            const pw = newPwInput.value.trim();
            if (!pw) { pwMsg.textContent = ''; pwMsg.className = 'form-msg'; return; }
            if (!pwRegex.test(pw)) {
                pwMsg.textContent = '영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.';
                pwMsg.className = 'form-msg error';
            } else {
                pwMsg.textContent = '사용 가능한 비밀번호입니다';
                pwMsg.className = 'form-msg success';
            }
        });

        confirmPwInput.addEventListener('keyup', () => {
            const pw = newPwInput.value.trim();
            const confirmPw = confirmPwInput.value.trim();
            if (!confirmPw) { confirmMsg.textContent = ''; confirmMsg.className = 'form-msg'; return; }
            if (pw !== confirmPw) {
                confirmMsg.textContent = '비밀번호가 일치하지 않습니다';
                confirmMsg.className = 'form-msg error';
            } else {
                confirmMsg.textContent = '비밀번호가 일치합니다';
                confirmMsg.className = 'form-msg success';
            }
        });
    }

    function checkEmailDuplicate(email) {
        return axios.get(`/admin/account/check?type=email&value=${email}`);
    }

    function confirmEmailVerify() {
        const email = document.getElementById('verify-email-display').value;
        const phone = document.getElementById('phone-input').value;
        const code = document.getElementById('verify-code').value.trim();
        const btn = document.getElementById('btn-send-verify');

        if (!code) { showAlertModal('인증번호를 입력해주세요.'); return; }

        _ajax('POST', '/admin/email/verify/confirm', { email, code }, (err, res) => {
            if (err || !res.success) {
                showAlertModal(res?.message || '인증을 실패하였습니다');
                return;
            }

            emailVerified = true;
            originalEmail = email;
            originalPhone = phone;

            stopTimer();
            stopResendTimer();

            document.getElementById('verify-code').value = '';
            if (btn) { btn.disabled = false; btn.textContent = '인증번호 발송'; }

            closeModal('modal-email-verify');
            showAlertModal('이메일 인증을 완료하였습니다');
        });
    }

    function startTimer() {
        stopTimer();
        remainSeconds = 300;
        updateTimerText();

        timerInterval = setInterval(() => {
            remainSeconds--;
            updateTimerText();
            if (remainSeconds <= 0) {
                stopTimer();
                showAlertModal('인증 시간이 만료되었습니다.');
            }
        }, 1000);
    }

    function stopTimer() {
        if (timerInterval) { clearInterval(timerInterval); timerInterval = null; }
    }

    function updateTimerText() {
        const el = document.getElementById('verify-timer');
        if (!el) return;
        const min = String(Math.floor(remainSeconds / 60)).padStart(2, '0');
        const sec = String(remainSeconds % 60).padStart(2, '0');
        el.textContent = `남은 시간 ${min}:${sec}`;
    }

    async function submitMypage() {
        const email = document.getElementById('email-input').value.trim();
        const phone = document.getElementById('phone-input').value.trim();
        const currentPw = document.getElementById('current-pw').value;
        const newPw = document.getElementById('new-pw').value;
        const confirmPw = document.getElementById('confirm-pw').value;

        const isEmailChanged = email !== originalEmail;
        const isPhoneChanged = phone !== originalPhone;
        const isPasswordChanged = !!newPw;
        const isAnyChanged = isEmailChanged || isPhoneChanged || isPasswordChanged;

        if (!isAnyChanged) { showAlertModal('변경된 내용이 없습니다.'); return; }

        if (isAnyChanged && (!emailVerified || email !== originalEmail)) {
            openModal('modal-mypage-unverified');
            return;
        }

        if (isEmailChanged) {
            const res = await checkEmailDuplicate(email);
            if (res.data.exists) { showAlertModal('이미 사용 중인 이메일입니다.'); return; }
        }

        const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/;

        if (newPw) {
            if (!pwRegex.test(newPw)) { showAlertModal('비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.'); return; }
            if (!currentPw) { showAlertModal('현재 비밀번호를 입력해주세요.'); return; }
            if (newPw !== confirmPw) { showAlertModal('비밀번호가 일치하지 않습니다.'); return; }
        }

        _ajax('PUT', '/admin/mypage', { adminEmail: email, adminPhone: phone }, (err) => {
            if (err) { showAlertModal('정보 수정을 실패하였습니다'); return; }

            if (newPw) {
                _ajax('PATCH', '/admin/mypage/password', {
                    currentPassword: currentPw,
                    newPassword: newPw
                }, (err2) => {
                    if (err2) { showAlertModal('비밀번호 변경을 실패하였습니다'); return; }
                    document.getElementById('current-pw').value = '';
                    document.getElementById('new-pw').value = '';
                    document.getElementById('confirm-pw').value = '';
                    openModal('modal-mypage-done');
                });
            } else {
                emailVerified = false;
                openModal('modal-mypage-done');
            }
        });
    }

    return {
        init,
        sendEmailVerify,
        confirmEmailVerify,
        submitMypage
    };

})();

(function () {
    if (document.body.dataset.page !== 'mypage') return;
    mypage.init();
})();