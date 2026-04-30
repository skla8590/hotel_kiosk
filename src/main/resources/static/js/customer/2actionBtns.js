const rplStyle = document.createElement('style');
rplStyle.textContent = '@keyframes rpl{to{transform:scale(2.8);opacity:0}}';
document.head.appendChild(rplStyle);

/* 버튼 클릭 시 물결(ripple) 효과 생성 함수 */
function ripple(e) {
    const btn = e.currentTarget;
    const d = Math.max(btn.offsetWidth, btn.offsetHeight);
    const rect = btn.getBoundingClientRect();
    const r = document.createElement('span');
    r.style.cssText = `
      position:absolute;border-radius:50%;background:rgba(255,255,255,0.2);
      width:${d}px;height:${d}px;
      left:${e.clientX - rect.left - d / 2}px;top:${e.clientY - rect.top - d / 2}px;
      transform:scale(0);animation:rpl 0.45s ease-out forwards;pointer-events:none;
    `;
    btn.appendChild(r);
    setTimeout(() => r.remove(), 450);
}