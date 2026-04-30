;(function () {
    'use strict'

    // 자음, 모음 배열
    const layout = [
        ['ㅂ', 'ㅈ', 'ㄷ', 'ㄱ', 'ㅅ', 'ㅛ', 'ㅕ', 'ㅑ', 'ㅐ', 'ㅔ'],
        ['ㅁ', 'ㄴ', 'ㅇ', 'ㄹ', 'ㅎ', 'ㅗ', 'ㅓ', 'ㅏ', 'ㅣ', 'ㅖ'],
        ['ㅋ', 'ㅌ', 'ㅊ', 'ㅍ', 'ㅠ', 'ㅜ', 'ㅡ']
    ];

    // 이중모음
    const DOUBLE_VOWEL = {
        'ㅗ+ㅏ': 'ㅘ',
        'ㅗ+ㅐ': 'ㅙ',
        'ㅗ+ㅣ': 'ㅚ',
        'ㅜ+ㅓ': 'ㅝ',
        'ㅜ+ㅔ': 'ㅞ',
        'ㅜ+ㅣ': 'ㅟ',
        'ㅡ+ㅣ': 'ㅢ',
    };

    let activeInput = null;
    let lastVowel = null;

    const panel = document.createElement('div');
    panel.className = 'kb-panel';

    // 키보드 HTML 생성
    let rowsHtml = layout.map(row => {
        return `<div class="kb-row">
            ${row.map(k => `<button class="kb-key" data-k="${k}">${k}</button>`).join('')}
        </div>`;
    }).join('');
    panel.innerHTML = `
        <div class="np-handle"></div>
        <div class="kb-grid">
            ${rowsHtml}
            <div class="kb-row">
                <button class="kb-key kb-func" data-a="clear">전체삭제</button>
                <button class="kb-key kb-confirm" data-a="confirm">확 인</button>
                <button class="kb-key kb-func" data-a="del">⌫</button>
            </div>
        </div>
    `;

    const kioskFrame = document.querySelector('.kiosk');
    if (kioskFrame) {
        kioskFrame.appendChild(panel);
    }

    // 키보드 숨기기
    function hideKoKb() {
        panel.classList.remove('kb-open');
        if (typeof overlay !== 'undefined') overlay.classList.remove('np-open');
        if (activeInput) activeInput.blur();
        activeInput = null;
        lastVowel = null;
    }

    // 모음 판별 세트
    const VOWELS = new Set(['ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ']);
    const COMBINABLE_VOWELS = new Set(['ㅗ','ㅜ','ㅡ']);

    // 한글 조합 로직
    panel.addEventListener('click', (e) => {
        const btn = e.target.closest('button');
        if (!btn || !activeInput) return;

        const k = btn.dataset.k;
        const a = btn.dataset.a;

        if (k) {
            const isVowel = VOWELS.has(k);

            // 이중모음 입력 조건 (이중모음 조합이 가능하고 현재 입력한 값이 모음일 때)
            if (lastVowel && isVowel && COMBINABLE_VOWELS.has(lastVowel)) {
                const combo = `${lastVowel}+${k}`;
                if (DOUBLE_VOWEL[combo]) {
                    // 마지막 문자(lastVowel)를 이중모음으로 교체
                    const currentVal = activeInput.value;

                    // 마지막 글자가 lastVowel 단독인지 확인
                    const lastChar = currentVal.slice(-1);
                    const lastCharDis = Hangul.disassemble(lastChar);

                    // 마지막 글자가 단순 모음(받침 없음)일 때만 교체
                    if (lastCharDis.length === 1 && lastCharDis[0] === lastVowel) {
                        activeInput.value = currentVal.slice(0, -1) + DOUBLE_VOWEL[combo];
                        lastVowel = null; // 이중모음 완성 후 초기화
                        activeInput.dispatchEvent(new Event('input', { bubbles: true }));
                        return;
                    }
                }
            }

            // 일반 입력
            let characters = Hangul.disassemble(activeInput.value);
            characters.push(k);
            activeInput.value = Hangul.assemble(characters);

            // 모음이면 lastVowel 갱신, 자음이면 초기화
            lastVowel = isVowel ? k : null;
        } else if (a === 'del') {
            let characters = Hangul.disassemble(activeInput.value);
            characters.pop();
            activeInput.value = Hangul.assemble(characters);
            lastVowel = null;
        } else if (a === 'clear') {
            activeInput.value = '';
            lastVowel = null;
        } else if (a === 'confirm') {
            hideKoKb();
            return;
        }

        if (activeInput) {
            activeInput.dispatchEvent(new Event('input', { bubbles: true }));
        }
    });

    // 입력창 클릭 시 키보드 띄우기
    document.addEventListener('focusin', (e) => {
        if (e.target.dataset.hangul !== undefined) {
            activeInput = e.target;
            panel.classList.add('kb-open');
        }
    });

    // 키보드 바깥 클릭 시 닫기
    document.addEventListener('pointerdown', (e) => {
        // 키보드가 열려있지 않으면 무시
        if (!panel.classList.contains('kb-open')) return;

        // 클릭 대상이 키보드 패널 안이면 무시
        if (panel.contains(e.target)) return;

        // 클릭 대상이 data-hangul 입력창이면 무시 (입력창 간 전환)
        if (e.target.dataset.hangul !== undefined) return;

        hideKoKb();
    });

    window.KoKey = { close: () => hideKoKb };
})();