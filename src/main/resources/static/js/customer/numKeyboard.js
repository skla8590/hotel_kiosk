/* 숫자 터치 키보드 */

;(function () {
    'use strict'

    // DOM 생성
    const overlay = document.createElement('div')
    overlay.className = 'np-overlay'

    // 숫자 버튼 생성
    const panel = document.createElement('div')
    panel.className = 'np-panel'
    panel.innerHTML = `
        <div class="np-handle"></div>
        <div class="np-input-bar" id="npInputBar"></div>
        <div class="np-grid">
            <button class="np-key" data-v="1">1</button>
            <button class="np-key" data-v="2">2</button>
            <button class="np-key" data-v="3">3</button>
            <button class="np-key" data-v="4">4</button>
            <button class="np-key" data-v="5">5</button>
            <button class="np-key" data-v="6">6</button>
            <button class="np-key" data-v="7">7</button>
            <button class="np-key" data-v="8">8</button>
            <button class="np-key" data-v="9">9</button>
            <button class="np-key np-aux" data-a="clear">전체<br>삭제</button>
            <button class="np-key" data-v="0">0</button>
            <button class="np-key np-aux" data-a="del">⌫</button>
        </div>
        <button class="np-confirm" data-a="confirm">확 인</button>
    `

    const kioskFrame = document.querySelector('.kiosk'); // 키오스크 프레임 선택
    if (kioskFrame) {
        kioskFrame.appendChild(panel)
        kioskFrame.appendChild(overlay)
    }

    const npInputBar = panel.querySelector('#npInputBar')

    // 상태
    let activeInput = null
    let groupInputs = null  // { front, back } | null

    // 미러 바
    function renderDisplay() {
        npInputBar.innerHTML = ''

        if (groupInputs) {
            const frontEl = document.createElement('span')
            frontEl.className = 'np-val' + (activeInput === groupInputs.front ? ' np-active' : '')
            frontEl.innerHTML = esc(groupInputs.front.value) +
                (activeInput === groupInputs.front ? '<span class="np-cursor"></span>' : '')

            const dash = document.createElement('span')
            dash.className = 'np-dash'
            dash.textContent = '-'

            const backEl = document.createElement('span')
            backEl.className = 'np-val' + (activeInput === groupInputs.back ? ' np-active' : '')
            backEl.innerHTML = esc(groupInputs.back.value) +
                (activeInput === groupInputs.back ? '<span class="np-cursor"></span>' : '')

            frontEl.dataset.npPart = 'front'
            backEl.dataset.npPart = 'back'
            frontEl.style.cursor = 'pointer'
            backEl.style.cursor = 'pointer'

            npInputBar.appendChild(frontEl)
            npInputBar.appendChild(dash)
            npInputBar.appendChild(backEl)
        } else {
            const el = document.createElement('span')
            el.className = 'np-val np-active'
            el.innerHTML = esc(activeInput.value) + '<span class="np-cursor"></span>'
            npInputBar.appendChild(el)
        }
    }

    function esc(str) {
        return (str || '').replace(/&/g, '&amp;').replace(/</g, '&lt;')
    }

    // 열기
    function open(input) {
        activeInput = input

        const groupName = input.dataset.numpadGroup
        if (groupName) {
            const all = document.querySelectorAll(`[data-numpad-group="${groupName}"]`)
            const front = [...all].find(el => el.dataset.numpadPart === 'front')
            const back = [...all].find(el => el.dataset.numpadPart === 'back')
            groupInputs = (front && back) ? {front, back} : null
        } else {
            groupInputs = null
        }

        renderDisplay()
        overlay.classList.add('np-open')
        requestAnimationFrame(() => panel.classList.add('np-open'))
    }

    // 닫기
    function close() {
        panel.classList.remove('np-open')
        overlay.classList.remove('np-open')
        if (activeInput) activeInput.blur()
        activeInput = null
        groupInputs = null
    }

    npInputBar.addEventListener('click', function (e) {
        const val = e.target.closest('[data-np-part]')
        if (!val || !groupInputs) return
        activeInput = val.dataset.npPart === 'front' ? groupInputs.front : groupInputs.back
        renderDisplay()
    })

    // 키 입력
    panel.addEventListener('click', function (e) {
        const btn = e.target.closest('[data-v],[data-a]')
        if (!btn || !activeInput) return

        const v = btn.dataset.v
        const a = btn.dataset.a

        if (v !== undefined) {
            const max = parseInt(activeInput.maxLength) || 999
            if (activeInput.value.length < max) {
                activeInput.value += v
                activeInput.dispatchEvent(new Event('input', {bubbles: true}))

                if (activeInput.value.length >= max) {
                    if (groupInputs) {
                        const frontMax = parseInt(groupInputs.front.maxLength)
                        const backMax = parseInt(groupInputs.back.maxLength)
                        const frontFull = groupInputs.front.value.length >= frontMax
                        const backFull = groupInputs.back.value.length >= backMax
                        if (frontFull && backFull) {
                            renderDisplay()
                            close()
                            return
                        }
                    }
                    // 앞 칸 입력 완료되면 뒷 칸 자동 이동
                    const nextId = activeInput.dataset.numpadNext
                    if (nextId) {
                        const next = document.getElementById(nextId)
                        if (next) activeInput = next
                    }
                }
            }

        } else if (a === 'del') {
            activeInput.value = activeInput.value.slice(0, -1)
            activeInput.dispatchEvent(new Event('input', {bubbles: true}))

        } else if (a === 'clear') {
            if (groupInputs) {
                groupInputs.front.value = ''
                groupInputs.back.value = ''
                activeInput = groupInputs.front
                groupInputs.front.dispatchEvent(new Event('input', {bubbles: true}))
                groupInputs.back.dispatchEvent(new Event('input', {bubbles: true}))
            } else {
                activeInput.value = ''
                activeInput.dispatchEvent(new Event('input', {bubbles: true}))
            }

        } else if (a === 'confirm') {
            close()
            return
        }

        renderDisplay()
    })

    overlay.addEventListener('click', close)

    document.addEventListener('focusin', function (e) {
        const input = e.target.closest('[data-numpad]')
        if (!input) return
        if (activeInput === input && panel.classList.contains('np-open')) return
        open(input)
    })

    window.NumPad = {open, close}
})()