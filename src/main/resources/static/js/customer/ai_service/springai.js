// 객체 선언
const springAi = {};

/********************
 0. 공통
 ********************/

/* 채팅 패널의 스크롤을 제일 아래로 내려주는 메서드 */
springAi.scrollToHeight = function (chatPanelId) {
    setTimeout(() => {
        const chatPanelElement = document.getElementById(chatPanelId);
        chatPanelElement.scrollTop = chatPanelElement.scrollHeight;
    }, 100);
};

/********************
 1. 텍스트 대화
 ********************/

/* 사용자 질문을 보여줄 html 엘리먼트를 채팅 패널에 추가하는 메서드 */
springAi.addUserQuestion = function (question, chatPanelId) {
    const html = `
    <div class="d-flex justify-content-end m-2">
      <table>
        <tr>
          <td><img src="/image/user.png" width="30" alt=""/></td>
          <td><span>${question}</span></td>
        </tr>
      </table>
    </div>
  `;
    document.getElementById(chatPanelId).innerHTML += html;
    springAi.scrollToHeight(chatPanelId);
};

/* 응답을 보여줄 html 엘리먼트를 채팅 패널에 추가하는 메서드 */
springAi.addAnswerPlaceHolder = function (chatPanelId) {
    const uuid = "id-" + crypto.randomUUID();
    const html = `
    <div class="d-flex justify-content-start border-bottom m-2">
      <table>
        <tr>
          <td><img src="/image/assistant.png" width="50" alt=""/></td>
          <td><span id="${uuid}"></span></td>
        </tr>
      </table>       
    </div>
  `;
    document.getElementById(chatPanelId).innerHTML += html;
    return uuid;
};

/* 텍스트 응답을 출력하는 메서드 */
springAi.printAnswerText = async function (responseBody, targetId, chatPanelId) {
    springAi.printAnswerStreamText(responseBody, targetId, chatPanelId);
}

/* 스트리밍 텍스트 응답을 출력하는 메서드 */
springAi.printAnswerStreamText = async function (responseBody, targetId, chatPanelId) {
    const targetElement = document.getElementById(targetId);
    const reader = responseBody.getReader();
    const decoder = new TextDecoder("utf-8");
    let content = "";
    while (true) {
        const {value, done} = await reader.read();
        if (done) break;
        let chunk = decoder.decode(value);
        content += chunk;
        if (!springAi.isOpenTagIncomplete(chunk)) {
            targetElement.innerHTML = content;
        }
        springAi.scrollToHeight(chatPanelId);
    }
};

/* 태그가 정상적으로 <>으로 구성되어 있는지 체크하는 메서드 */
springAi.isOpenTagIncomplete = function (str) {
    // 1) 문자열 안에 '<'가 하나라도 있는지 확인
    const lastLt = str.lastIndexOf("<");
    if (lastLt === -1) {
        return false;
    }
    // 2) 문자열 안에 '>'가 하나라도 있는지 확인
    const lastGt = str.lastIndexOf(">");
    if (lastGt === -1) {
        // '>'가 아예 없으면, '<'만 있는 상태 → 무조건 미완성
        return true;
    }
    // 3) “마지막 '<' 인덱스”가 “마지막 '>' 인덱스”보다 크면
    //    그 이후로 닫힘 기호가 없다는 의미 -> 미완성
    return lastLt > lastGt;
};

/* JSON을 이쁘게 출력하는 메서드 */
springAi.printAnswerJson = async function (jsonString, uuid, chatPanelId) {
    const jsonObject = JSON.parse(jsonString);
    const prettyJson = JSON.stringify(jsonObject, null, 2);
    document.getElementById(uuid).innerHTML = "<pre>" + prettyJson + "</pre>";
    springAi.scrollToHeight(chatPanelId);
};

/* 진행중임을 표시하는 메서드 */
springAi.setSpinner = function (spinnerId, status) {
    if (status) {
        document.getElementById(spinnerId).classList.remove("d-none");
    } else {
        document.getElementById(spinnerId).classList.add("d-none");
    }
}

/********************
 2. 음성 대화
 ********************/
springAi.voice = {};

springAi.voice.handleMediaError = function (err) {
    switch (err.name) {
        case 'NotFoundError':
            // 장치가 물리적으로 없거나 인식이 안 됨
            alert("연결된 마이크를 찾을 수 없습니다. 마이크가 제대로 연결되어 있는지 확인해 주세요.");
            break;
        case 'NotAllowedError':
            // 사용자가 거부함
            alert("마이크 사용 권한이 거부되었습니다. 주소창의 설정에서 권한을 허용해 주세요.");
            break;
        case 'NotReadableError':
            // 장치가 이미 다른 앱에서 사용 중이거나 하드웨어 오류
            alert("마이크가 다른 프로그램에서 사용 중이거나 응답하지 않습니다.");
            break;
        default:
            alert("오디오 장치 오류가 발생했습니다: " + err.message);
    }
}

/* 마이크를 활성화하고 소리 분석 도구 및 녹화 도구를 준비를 하는 메서드 */
springAi.voice.initMic = async function (handleVoice) {
    //전역 변수 초기화
    springAi.voice.voice = false;               // 사람의 음성이 입력되면 true
    springAi.voice.chatting = false;            // 질문하기 시작할 때부터 답변을 받을 때까지 true
    springAi.voice.silenceStart = null;         // 침묵 시작 시간을 저장
    springAi.voice.silenceDelay = 2000;            // 침묵 지연 시간 2초을 저장하는 상수
    springAi.voice.silenceThreshold = 0.01;        // 침묵인지 판단할 임계상수(0~1 사이의 값)
    springAi.voice.stream = null;               // 마이크 입력 스트림 객체
    springAi.voice.analyser = null;             // 소리 분석기 객체
    springAi.voice.mediaRecorder = null;        // 음성 녹음기 객체
    springAi.voice.recognition = null;          // 음성 인식 객체

    //사용자에게 마이크 접근 권한을 요청하고, 오디오 스트림(MediaStream)을 가져옴
    try {
        const stream = await navigator.mediaDevices.getUserMedia({audio: true});
        springAi.voice.stream = stream;
    } catch (err) {
        this.handleMediaError(err);
    }

    //침묵이 지속되는지 분석을 위한 코드 ---------------
    //오디오 처리를 위한 AudioContext 생성
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    //마이크에서 들어온 오디오 스트림을 MediaStreamAudioSourceNode로 변환
    const source = audioContext.createMediaStreamSource(springAi.voice.stream);
    //오디오 데이터를 실시간으로 분석하는 AnalyserNode를 생성
    springAi.voice.analyser = audioContext.createAnalyser();
    //음성 분석을 위한 FFT(빠른 푸리에 변환) 구간 크기 설정
    //클수록 더 정밀한 주파수 분석이 가능하지만 처리 비용이 증가(보통 512, 1024, 2048 사용)
    springAi.voice.analyser.fftSize = 2048;
    //오디오 소스를 분석기에 연결
    source.connect(springAi.voice.analyser);
    //-----------------------------------------------

    //미디어 녹음기 초기화
    springAi.voice.initMediaRecorder(handleVoice);
    //음성 인식 초기화
    springAi.voice.initRecognitionVoice();
};

/* 미디어 녹음기를 초기화하는 메서드 */
springAi.voice.initMediaRecorder = function (handleVoice) {
    //오디오 녹음을 위한 MediaRecorder 생성
    const mediaRecorder = new MediaRecorder(springAi.voice.stream);
    springAi.voice.mediaRecorder = mediaRecorder;

    //침묵으로 인한 음성 녹화가 중지되었을 때, 자동 호출되는 함수 지정
    mediaRecorder.ondataavailable = async (event) => {
        //음성 확인이 되었고, 녹화 데이터가 있고, 현재 대화중이 아닐 경우
        if (springAi.voice.voice === true && event.data.size > 0 && springAi.voice.chatting === false) {
            console.log("대화 시작");
            springAi.voice.chatting = true;

            //MP3로 변환
            const webmBlob = event.data;
            const mp3Blob = await springAi.voice.convertWebMToMP3(webmBlob);
            //콜백(사용자 로직) 실행 -------------
            handleVoice(mp3Blob);
            //---------------------------------
        }
        //음성 확인이 안되었거나, 녹화 데이터가 없을 경우
        else {
            mediaRecorder.start();
            springAi.voice.checkSilence();
        }
    };

    console.log("음성 녹화 시작");
    mediaRecorder.start();
    console.log("침묵 감시 시작");
    springAi.voice.checkSilence();
};

/* 마이크 입력로부터 음성 인식을 하는 메서드 */
springAi.voice.initRecognitionVoice = function () {
    // 음성 인식 전역 변수 초기화
    springAi.voice.voice = false;
    // 음성 인식을 제공하는 SpeechRecognition 생성
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();
    springAi.voice.recognition = recognition;
    // 음성이 한국어일 것이다를 알려주는 힌트 설정(명확한 영어도 인식될 수 있음)
    recognition.lang = 'ko-KR';
    // true: 음성 확인되면 매번 onresult 콜백
    recognition.interimResults = true;
    // false: 음성 확인 후, 몇초간(브라우저 고정값, 1~2초) 침묵이 되면 인식 자동 종료
    recognition.continuous = false;
    // 인식을 시작할 때 콜백되는 함수
    recognition.onstart = function () {
    };
    // 음성 확인되었을 때 콜백되는 함수
    recognition.onresult = function (event) {
        // 변환된 텍스트 얻기(정식 STT로 사용하기에는 인식 정확도 낮음)
        const transcript = event.results[0][0].transcript;
        // 텍스트가 있고, 한글이 포함되어 있을 경우
        if (transcript.length > 0 && springAi.voice.isKorean(transcript)) {
            console.log("한국어 음성 확인");
            springAi.voice.voice = true;
        }
    };
    // 인식을 종료할 때 콜백되는 함수
    recognition.onend = function () {
        // 브라우저에서 자동 종료시켰을 경우, 재시작 시킴
        if (!springAi.voice.voice) {
            recognition.start();
        }
    };

    console.log("음성 인식 시작");
    recognition.start();
};

/* 한글이 1개라도 포함되어 있는지 체크하는 함수 */
springAi.voice.isKorean = function (text) {
    const koreanRegex = /[가-힣]/;
    const isKorean = koreanRegex.test(text);
    return isKorean;
};

/* 침묵이 지속되는지 체크하는 함수 */
springAi.voice.checkSilence = function () {
    // 분석 결과를 저장할 바이트 배열을 생성
    const dataArray = new Uint8Array(springAi.voice.analyser.fftSize);
    // 오디오 파형 데이터를 dataArray에 복사
    // 각 값은 0~255 범위의 8비트 정수이며, 오디오 신호의 진폭을 나타냄
    // 128이 중심(0에 해당), 0 또는 255는 최대 음파 진폭
    springAi.voice.analyser.getByteTimeDomainData(dataArray);
    // Uint8Array인 dataArray를 일반 배열로 변환한 뒤, 각 값을 정규화된 부동소수점 형태로 변환
    // 즉, 0~255 범위를 -1.0 ~ +1.0 범위로 바꿈
    const normalized = Array.from(dataArray).map(v => v / 128 - 1);
    // RMS(Root Mean Square) = 정규화된 신호의 제곱 평균 제곱근
    // RMS는 음성 볼륨 크기을 나타내며, 값이 클수록 말소리가 크거나 배경 소음이 심하다는 뜻
    // RMS ≈ 0: 침묵
    // RMS ≈ 1: 최대 볼륨
    const rms = Math.sqrt(normalized.reduce((sum, v) => sum + v * v, 0) / normalized.length);
    // 음성 볼륨이 침묵 임계상수 보다 작을 경우
    if (rms < springAi.voice.silenceThreshold) {
        // 침묵 시작 시간 설정이 되어 있지 않은 경우
        if (!springAi.voice.silenceStart) {
            // 침묵 시작 시간 설정
            springAi.voice.silenceStart = Date.now();
        }
        // 침묵이 silenceDelay 동안 지속될 경우
        else if ((Date.now() - springAi.voice.silenceStart) > springAi.voice.silenceDelay) {
            // 음성 녹화 중이라면, 음성 녹화 중지
            if (springAi.voice.mediaRecorder.state === 'recording') {
                springAi.voice.mediaRecorder.stop();
                springAi.voice.recognition.stop();
            }
            // 침묵 시작 시간 없애기
            springAi.voice.silenceStart = null;
            return;
        }
    }
    // 음성 볼륨이 침묵 임계상수와 같거나 클 경우
    else {
        // 침묵 시작 시간 없애기
        springAi.voice.silenceStart = null;
    }

    // 침묵이 지속되는지 계속 체크: 재귀 호출
    springAi.voice.animationId = requestAnimationFrame(springAi.voice.checkSilence);
};

/* WebM Blob을 MP3 Blob으로 변환하는 메서드 */
// OpenAi의 gpt-4o-mini-audio의 입력은 audio/mp3 또는 audio/wav만 가능
springAi.voice.convertWebMToMP3 = async function (webmBlob) {
    // WebM Blob → ArrayBuffer → AudioBuffer(PCM) 디코딩
    const arrayBuffer = await webmBlob.arrayBuffer();
    const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const audioBuf = await audioCtx.decodeAudioData(arrayBuffer);

    // PCM 데이터 추출 (첫 번째 채널만 사용)
    const float32Data = audioBuf.getChannelData(0);
    const sampleRate = audioBuf.sampleRate;

    // LameJS Mp3Encoder 인스턴스 생성 (채널=1, 샘플레이트, 비트레이트=128kbps)
    const mp3Encoder = new lamejs.Mp3Encoder(1, sampleRate, 128);
    const samplesPerFrame = 1152;
    let mp3DataChunks = [];

    // Float32 → Int16 변환 함수
    function floatTo16BitPCM(input) {
        const output = new Int16Array(input.length);
        for (let i = 0; i < input.length; i++) {
            let s = Math.max(-1, Math.min(1, input[i]));
            output[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
        }
        return output;
    }

    // 프레임 단위 인코딩
    for (let i = 0; i < float32Data.length; i += samplesPerFrame) {
        const sliceF32 = float32Data.subarray(i, i + samplesPerFrame);
        const sliceI16 = floatTo16BitPCM(sliceF32);
        const mp3buf = mp3Encoder.encodeBuffer(sliceI16);
        if (mp3buf.length) mp3DataChunks.push(mp3buf);
    }
    // 남은 버퍼 flush
    const endBuf = mp3Encoder.flush();
    if (endBuf.length) mp3DataChunks.push(endBuf);

    // Blob으로 병합해 반환
    return new Blob(mp3DataChunks, {type: 'audio/mp3'});
};

/* 스트리밍 음성 데이터를 재생하는 메서드 */
springAi.voice.playAudioFormStreamingData = async function (response, audioPlayer) {
    try {
        // 스트리밍을 위한 미디어소스 생성과 audioPlaye 소스로 설정
        const mediaSource = new MediaSource();
        audioPlayer.src = URL.createObjectURL(mediaSource);

        // 스트림이 열리면 콜백되는 함수 등록
        mediaSource.addEventListener('sourceopen', async () => {
            // 본문의 오디오 데이터 타입을 알려주고 데이터 버퍼 준비
            // MIME 타입은 서버에서 실제 인코딩한 포맷으로 맞춰야 함
            // 예) MP3: 'audio/mpeg', WAV: 'audio/wav'
            const sourceBuffer = mediaSource.addSourceBuffer('audio/mpeg');
            // 응답 본문을 읽는 리더 얻기
            const reader = response.body.getReader();
            // 스트리밍되는 데이터가 있을 동안 반복
            while (true) {
                // 스트리밍 음성 데이터(청크) 읽기
                const {done, value} = await reader.read();
                //스트리밍이 종료될 경우 스트림을 닫고 반복 중지
                if (done) {
                    mediaSource.endOfStream();
                    break;
                }
                // 스트리밍이 계속 진행 중일 경우
                await new Promise(resolve => {
                    // 버퍼 데이터가 갱신 완료될 때마다 핸들러(resolve) 실행,
                    // { once: true }: 핸들러를 한 번만 실행한 후 자동으로 제거
                    sourceBuffer.addEventListener('updateend', resolve, {once: true});
                    // 버퍼에 데이터 추가
                    sourceBuffer.appendBuffer(value);
                });
            }
        });
        // 재생 시작
        audioPlayer.play();
    } catch (error) {
        console.log(error);
    }
};

/* 사용자의 질문을 보여줄 엘리먼트를 채팅 패널에 추가하는 함수 */
springAi.voice.addUserQuestionPlaceHolder = function (chatPanelId) {
    //id-를 붙이는 이유: 숫자로 시작하면 CSS 선택자 문법 에러 날 수 있음
    const uuid = "id-" + crypto.randomUUID();
    const questionHtml = `
    <div class="d-flex justify-content-end m-2">            
      <table>
        <tr>
          <td><img src="/image/user.png" width="30" alt=""/></td>
          <td>
            <div id="${uuid}-speaker" class="speakerPulse" 
              style="width: 30px; height: 30px; 
              background: url('/image/speaker-yellow.png') no-repeat center center / contain;"></div>
          </td>
          <td><span id="${uuid}"></span></td>
        </tr>
      </table>                      
    </div>
  `;
    document.getElementById(chatPanelId).innerHTML += questionHtml;
    return uuid;
};

/* AI 답변을 보여줄 엘리먼트를 채팅 패널에 추가하는 메서드 */
springAi.voice.addAnswerPlaceHolder = function (chatPanelId) {
    //id-를 붙이는 이유: 숫자로 시작하면 CSS 선택자 문법 에러 날 수 있음
    const uuid = "id-" + crypto.randomUUID();
    const answerHtml = `
    <div class="d-flex justify-content-start border-bottom m-2">         
      <table>
        <tr>
          <td><img src="/image/assistant.png" width="50" alt=""/></td>
          <td>
            <div id="${uuid}-speaker" class="speakerPulse" 
              style="width: 30px; height: 30px; 
              background: url('/image/speaker-green.png') no-repeat center center / contain;"></div>
          </td>
          <td><span id="${uuid}"></span></td>
        </tr>
      </table>            
    </div>
  `;
    document.getElementById(chatPanelId).innerHTML += answerHtml;
    return uuid;
};

/* 스피커 애니메이션 제어 메서드 */
springAi.voice.controlSpeakerAnimation = function (speakerId, flag) {
    if (flag) {
        document.getElementById(speakerId).classList.add("speakerPulse");
    } else {
        document.getElementById(speakerId).classList.remove("speakerPulse");
    }
};


/********************
 3. 웨이크워드 감지
 ********************/
springAi.wake = {};

const WAKE_WORDS = [
    'hi jhotel', 'hi j hotel', 'hi jay hotel',
    '하이 제이호텔', '하이제이호텔', '하이 제이 호텔', '하이제이 호텔', '하 이제이 호텔', '하 이제 이호텔',
    '하이 데이호텔', '하이데이호텔', '하이 데이 호텔', '하이데이 호텔',
    '하이 J 호텔'
];

/* 웨이크워드 감지 시작 */
springAi.wake.start = function () {
    // window.SpeechRecognition : 크롬
    // window.webkitSpeechRecognition : 구버전 크롬
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();

    recognition.lang = 'ko-KR'; // 한국어 감지
    recognition.continuous = false; // 계속해서 듣기
    recognition.interimResults = true;

    recognition.onstart = function () {
        console.log('웨이크워드 인식 실제 시작됨');
    };

    let hasError = false;
    recognition.onerror = function (event) {
        console.log('웨이크워드 에러:', event.error);
        if (event.error === 'not-allowed') {
            hasError = true;
            // 웹 확인용
            alert("마이크 권한을 허용해 주세요!");
        }
    };

    // 음성이 텍스트로 변환될 때 자동 실행될 함수
    recognition.onresult = function (event) {
        // event.results.length - 1 : 가장 최근에 인식된 결과
        const transcript = event.results[event.results.length - 1][0].transcript;
        console.log('들린 말:', transcript);

        const normalized = transcript.toLowerCase().trim();
        const detected = WAKE_WORDS.some(word => normalized.includes(word));

        // 웨이크워드 감지되면 듣기 중단 후 모달 열기
        if (detected) {
            recognition.stop();
            springAi.wake.showModal(recognition);
        }
    };

    // 듣기가 끊겼을 때 자동 실행될 함수
    recognition.onend = function () {
        // 에러(마이크 허용x) 없고 모달이 떠 있지 않을 때만 재시작
        const modal = document.getElementById('aiWakeWord');
        if (!hasError && modal && modal.classList.contains('hidden')) {
            try {
                recognition.start();
            } catch (e) {
                // 재시작 실패하면 100ms 후 재시도
                setTimeout(() => {
                    try {
                        recognition.start();
                    } catch (e2) {
                    }
                }, 100);
            }
        }
    };

    recognition.start();
    console.log('웨이크워드 감지 중...');
};

/* 웨이크워드 모달 열기 */
springAi.wake.showModal = function (recognition) {
    // 타임아웃 실행 중일 시 중단
    if (typeof countdownTimer !== 'undefined') {
        clearInterval(countdownTimer)
    }
    if (typeof closeModal === 'function') {
        closeModal('timeoutModal')
    }
    window.speechSynthesis.cancel() // TTS 중단

    document.getElementById('aiWakeWord').classList.remove('hidden');
    setTimeout(() => {
        springAi.wake.listenQuestion(recognition);
    }, 500);
};

/* 웨이크워드 모달 닫기 */
springAi.wake.closeModal = function (recognition) {
    document.getElementById('aiWakeWord').classList.add('hidden');

    // 타임아웃 재시작
    if (typeof resetIdleTimer === 'function') {
        resetIdleTimer()
    }

    recognition.start(); // 웨이크워드 감지 재시작
};

/* 질문 듣기 */
springAi.wake.listenQuestion = function (recognition) {
    console.log('질문 듣기 시작')
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const qRecognition = new SpeechRecognition();

    qRecognition.lang = 'ko-KR';
    qRecognition.continuous = true;
    qRecognition.interimResults = true;

    let finalQuestion = '';
    let silenceTimer = null;
    const SILENCE_LIMIT = 30000; // 사용자 반응 대기 시간 30초 설정

    function startSilenceTimer() {
        clearTimeout(silenceTimer);
        silenceTimer = setTimeout(() => {
            // 30초 동안 아무 말도 인식되지 않을 경우
            qRecognition.stop();
            const msgEl = document.querySelector('#aiWakeWord .modal-msg');
            if (msgEl) msgEl.textContent = '다시 말씀해 주세요!';
            setTimeout(() => springAi.wake.listenQuestion(recognition), 1000);
        }, SILENCE_LIMIT);
    }

    // 듣기 시작하면 침묵 타이머 시작
    qRecognition.onstart = function () {
        startSilenceTimer();
    };

    qRecognition.onresult = function (event) {
        let interim = '';

        // isFinal = true (확정 텍스트) -> finalQuestion에 저장
        // isFinal = false (발화 중인 텍스트) -> interim에 임시 저장
        for (let i = event.resultIndex; i < event.results.length; i++) {
            if (event.results[i].isFinal) {
                finalQuestion += event.results[i][0].transcript;
            } else {
                interim += event.results[i][0].transcript;
            }
        }

        // 사용자가 말하는 중이면 타이머 리셋
        startSilenceTimer();

        // 모달 안 텍스트 실시간 업데이트
        const msgEl = document.querySelector('#aiWakeWord .modal-msg');
        if (msgEl) msgEl.textContent = finalQuestion + interim || '듣고 있습니다. 궁금하신 점을 말씀해주세요!';

        // 확정된 질문이 있으면 1.5초 후 채팅 페이지로 이동
        if (finalQuestion.trim()) {
            clearTimeout(silenceTimer);
            silenceTimer = setTimeout(() => {
                qRecognition.stop();
                sessionStorage.setItem('finalQuestion', finalQuestion);
                window.location.href = '/JHotel/ai_chat';
            }, 1500); // 1.5초 후 이동
        }
    };

    qRecognition.onerror = function (event) {
        console.log('질문 인식 에러:', event.error);

        if (event.error === 'network') {
            console.log("network error → 재시작");

            try {
                qRecognition.stop();
            } catch (e) {
            }

            setTimeout(() => {
                qRecognition.start();
            }, 300);
        }
    };

    qRecognition.start();
};

/* 모든 마이크 및 인식 자원을 중단하는 함수 */
springAi.voice.stopAll = function () {
    // 1. 미디어 레코더 중지
    if (springAi.voice.mediaRecorder && springAi.voice.mediaRecorder.state !== 'inactive') {
        springAi.voice.mediaRecorder.stop();
    }
    // 2. 음성 인식 중지
    if (springAi.voice.recognition) {
        springAi.voice.recognition.onend = null; // 재시작 방지
        springAi.voice.recognition.stop();
    }
    // 3. 마이크 스트림 트랙 닫기 (마이크 표시등 끄기)
    if (springAi.voice.stream) {
        springAi.voice.stream.getTracks().forEach(track => track.stop());
    }
    // 4. 애니메이션 프레임 중단 (checkSilence 중단)
    if (springAi.voice.animationId) {
        cancelAnimationFrame(springAi.voice.animationId);
    }

    console.log("기존 음성 자원 정리 완료");
};