/* 숙박 기간 설정 달력 */
document.addEventListener('DOMContentLoaded', function () {
    flatpickr('#kiosk-calendar', {
        inline: true, // 페이지 로드 시 바로 보이게
        mode: "range", // 기간 선택 모드
        locale: "ko", // 한국어 설정
        dateFormat: "Y-m-d", // 데이터 포맷
        minDate: "today", // 오늘 기준 이전 날짜 선택 불가
        monthSelectorType: "static",

        // 날짜 선택될 때마다 콜백 실행
        onChange: function (selectedDates, dateStr, instance) {
            // 체크인, 체크아웃 날짜 저장
            const checkinInput = document.getElementById('checkinDate');
            const checkoutInput = document.getElementById('checkoutDate');

            if (selectedDates.length === 2) {
                // 1. 체크인, 체크아웃 날짜가 모두 선택된 경우
                const checkinDate = instance.formatDate(selectedDates[0], "Y-m-d");
                const checkoutDate = instance.formatDate(selectedDates[1], "Y-m-d");

                checkinInput.value = checkinDate;
                checkoutInput.value = checkoutDate;

                // 페이지가 /onsite/selection일 경우 (객실 선택 페이지 대응)
                if (document.getElementById('calendarOverlay')) {
                    document.getElementById('checkinDisplay').textContent = checkinDate;
                    document.getElementById('checkoutDisplay').textContent = checkoutDate;
                    document.getElementById('calendarOverlay').style.display = 'none';
                    fetchRooms(checkinDate, checkoutDate);
                }
            } else {
                // 2. 날짜가 하나만 선택되었거나 다시 클릭해서 초기화된 경우
                checkinInput.value = "";
                checkoutInput.value = "";
            }
        }

    })

    function fetchRooms(checkinDate, checkoutDate) {
        const regPeople = sessionStorage.getItem('regPeople')

        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        fetch('/JHotel/onsite/rooms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                checkinDate: checkinDate,
                checkoutDate: checkoutDate,
                regPeople: parseInt(regPeople)
            })
        })
            .then(res => res.json())
            .then(function (newRooms) {
                sessionStorage.setItem('rooms', JSON.stringify(newRooms));
                sessionStorage.setItem('checkinDate', checkinDate);
                sessionStorage.setItem('checkoutDate', checkoutDate);

                const generalPeriod = (new Date(checkoutDate) - new Date(checkinDate)) / (1000 * 60 * 60 * 24);
                sessionStorage.setItem('generalPeriod', generalPeriod);

                rooms = newRooms
                renderRooms()
            })
    }
})