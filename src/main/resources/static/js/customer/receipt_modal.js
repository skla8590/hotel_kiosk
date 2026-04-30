let timerInterval = null;

function openReceipt() {
    document.getElementById('receiptModal').classList.remove('hidden');
    startTimer(10);
}

function closeReceipt() {
    document.getElementById('receiptModal').classList.add('hidden');
    clearInterval(timerInterval);
}

function startTimer(sec) {
    let remaining = sec;
    const bar = document.getElementById('timerBar');
    const countEl = document.getElementById('timerCount');
    const secEl = document.getElementById('timerSec');

    bar.style.width = '100%';
    countEl.textContent = remaining;
    secEl.textContent = remaining;

    clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        remaining--;
        countEl.textContent = remaining;
        secEl.textContent = remaining;
        bar.style.width = (remaining / sec * 100) + '%';
        if (remaining <= 0) {
            clearInterval(timerInterval);
            closeReceipt();
        }
    }, 1000);
}