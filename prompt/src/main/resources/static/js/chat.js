'use strict';

window.addEventListener('DOMContentLoaded', function () {

    let currentRoomId = null;
    let currentModel  = 'alan-4.0';
    let isStreaming   = false;

    // 프로그램에서 참조할 DOM 요소를 미리 변수 선언
    const aside          = document.getElementById('aside');
    const btnToggle      = document.getElementById('btnToggle');
    const btnNew         = document.getElementById('btnNew');
    const roomList       = document.getElementById('roomList');
    const empty          = document.getElementById('empty');
    const chatView       = document.getElementById('chatView');
    const messages       = document.getElementById('messages');
    const messagesWrap   = document.getElementById('messagesWrap');
    const userInput      = document.getElementById('userInput');
    const sendBtn        = document.getElementById('sendBtn');
    const emptyInput     = document.getElementById('emptyInput');
    const emptySendBtn   = document.getElementById('emptySendBtn');
    const btnSettings    = document.getElementById('btnSettings');
    const settingsMenu   = document.getElementById('settingsMenu');
    const toast          = document.getElementById('toast');
    const chatModelBtns  = document.querySelectorAll('#chatModelSelector > button');
    const emptyModelBtns = document.querySelectorAll('#emptyModelSelector > button');

    //  모델 선택 (두 선택기 동기화)
    function setModel(model) {
        currentModel = model;
        [...chatModelBtns, ...emptyModelBtns].forEach(function (btn) {
            btn.setAttribute('aria-pressed', btn.dataset.model === model ? 'true' : 'false');
        });
    }

    chatModelBtns.forEach(function (btn) {
        btn.addEventListener('click', function () { setModel(btn.dataset.model); });
    });

    emptyModelBtns.forEach(function (btn) {
        btn.addEventListener('click', function () { setModel(btn.dataset.model); });
    });

    //  사이드바 토글
    btnToggle.addEventListener('click', function () {
        const collapsed = aside.classList.toggle('collapsed');
        btnToggle.setAttribute('aria-expanded', String(!collapsed));
    });

    //  설정 메뉴
    btnSettings.addEventListener('click', function (e) {
        e.stopPropagation();
        const isOpen = !settingsMenu.classList.contains('hidden');
        settingsMenu.classList.toggle('hidden');
        btnSettings.setAttribute('aria-expanded', String(!isOpen));
    });

    document.addEventListener('click', function () {
        if (!settingsMenu.classList.contains('hidden')) {
            settingsMenu.classList.add('hidden');
            btnSettings.setAttribute('aria-expanded', 'false');
        }
    });

    settingsMenu.addEventListener('click', function (e) {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;

        settingsMenu.classList.add('hidden');
        btnSettings.setAttribute('aria-expanded', 'false');

        const action = btn.dataset.action;
        if (action === 'logout') {
            fetch('/logout', {
                method: 'POST',
                headers: { 'X-CSRF-TOKEN': getCsrfToken() }
            }).finally(function () { location.href = '/login'; });

        } else if (action === 'profile') {
            location.href = '/mypage';

        } else if (action === 'theme') {
            location.href = '/settings';

        } else if (action === 'plan') {
            location.href = '/payment';

        } else if (action === 'help') {
            showToast('도움말 페이지 준비 중입니다.');
        }
    });

    //  뷰 전환
    function setView(view) {
        if (view === 'empty') {
            empty.classList.remove('hidden');
            chatView.classList.add('hidden');
            emptyInput.focus();
        } else {
            empty.classList.add('hidden');
            chatView.classList.remove('hidden');
        }
    }

    //  채팅방 목록 로드
    function loadRooms() {
        fetch('/api/chat/rooms')
            .then(function (r) { return r.json(); })
            .then(function (res) {
                roomList.innerHTML = '';
                const rooms = res.data || [];

                if (rooms.length === 0) {
                    const li = document.createElement('li');
                    li.className = 'roomEmpty';
                    li.textContent = '채팅방이 없습니다';
                    roomList.appendChild(li);
                    return;
                }

                rooms.forEach(function (room) {
                    roomList.appendChild(buildRoomItem(room));
                });
            })
            .catch(function () { showToast('채팅방 목록을 불러오지 못했습니다.', true); });
    }

    function buildRoomItem(room) {
        const li  = document.createElement('li');
        const btn = document.createElement('button');
        const del = document.createElement('button');

        li.dataset.roomId  = room.chatRoomId;
        btn.textContent    = room.chatTitle || '새 채팅';
        del.textContent    = '×';
        del.setAttribute('aria-label', '채팅방 삭제');

        btn.addEventListener('click', function () { openRoom(room.chatRoomId); });
        del.addEventListener('click', function (e) {
            e.stopPropagation();
            deleteRoom(room.chatRoomId, li);
        });

        li.appendChild(btn);
        li.appendChild(del);
        return li;
    }

    //  채팅방 열기
    function openRoom(roomId) {
        currentRoomId = roomId;

        document.querySelectorAll('#roomList > li').forEach(function (li) {
            li.classList.toggle('active', li.dataset.roomId == roomId);
        });

        messages.innerHTML = '';
        setView('chat');

        fetch('/api/chat/rooms/' + roomId + '/messages')
            .then(function (r) { return r.json(); })
            .then(function (res) {
                (res.data || []).forEach(function (msg) {
                    appendMsg(msg.role, msg.content);
                });
                scrollToBottom();
            })
            .catch(function () { showToast('메시지를 불러오지 못했습니다.', true); });
    }

    //  새 채팅방 생성
    btnNew.addEventListener('click', function () {
        createRoom(function (roomId) {
            loadRooms();
            openRoom(roomId);
        });
    });

    function createRoom(callback) {
        fetch('/api/chat/rooms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': getCsrfToken()
            },
            body: JSON.stringify({ model: currentModel })
        })
            .then(function (r) { return r.json(); })
            .then(function (res) {
                if (res.success) {
                    callback(res.data.chatRoomId);
                } else {
                    showToast(res.message || '채팅방 생성 실패', true);
                }
            })
            .catch(function () { showToast('채팅방 생성 중 오류가 발생했습니다.', true); });
    }

    //  채팅방 삭제
    function deleteRoom(roomId, li) {
        fetch('/api/chat/rooms/' + roomId, {
            method: 'DELETE',
            headers: { 'X-CSRF-TOKEN': getCsrfToken() }
        })
            .then(function (r) { return r.json(); })
            .then(function (res) {
                if (res.success) {
                    li.remove();
                    if (currentRoomId == roomId) {
                        currentRoomId = null;
                        messages.innerHTML = '';
                        setView('empty');
                    }
                    if (roomList.children.length === 0) {
                        const empty = document.createElement('li');
                        empty.className = 'roomEmpty';
                        empty.textContent = '채팅방이 없습니다';
                        roomList.appendChild(empty);
                    }
                } else {
                    showToast(res.message || '삭제 실패', true);
                }
            })
            .catch(function () { showToast('삭제 중 오류가 발생했습니다.', true); });
    }

    //  메시지 전송
    function sendMessage(text, roomId) {
        if (!text.trim() || isStreaming) return;

        isStreaming = true;
        sendBtn.disabled     = true;
        emptySendBtn.disabled = true;

        appendMsg('user', text);
        scrollToBottom();

        const aiBubble = appendMsg('assistant', '');
        aiBubble.classList.add('streaming');

        const url = '/api/chat/stream'
            + '?roomId='  + encodeURIComponent(roomId)
            + '&content=' + encodeURIComponent(text)
            + '&model='   + encodeURIComponent(currentModel);

        const es = new EventSource(url);

        es.addEventListener('message', function (e) {
            aiBubble.textContent += e.data;
            scrollToBottom();
        });

        es.addEventListener('done', function () {
            es.close();
            aiBubble.classList.remove('streaming');
            isStreaming           = false;
            sendBtn.disabled      = false;
            emptySendBtn.disabled = false;
        });

        es.addEventListener('error', function () {
            es.close();
            aiBubble.classList.remove('streaming');
            if (!aiBubble.textContent) {
                aiBubble.textContent = '응답을 받지 못했습니다.';
                aiBubble.classList.add('error');
            }
            isStreaming           = false;
            sendBtn.disabled      = false;
            emptySendBtn.disabled = false;
        });
    }

    // 빈 화면 전송 (채팅방 자동 생성)
    function sendFromEmpty() {
        const text = emptyInput.value.trim();
        if (!text || isStreaming) return;
        emptyInput.value = '';

        createRoom(function (roomId) {
            loadRooms();
            openRoom(roomId);
            sendMessage(text, roomId);
        });
    }

    // 채팅 뷰 전송
    function sendFromChat() {
        const text = userInput.value.trim();
        if (!text || !currentRoomId || isStreaming) return;
        userInput.value = '';
        sendMessage(text, currentRoomId);
    }

    sendBtn.addEventListener('click', sendFromChat);
    emptySendBtn.addEventListener('click', sendFromEmpty);

    userInput.addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendFromChat();
        }
    });

    emptyInput.addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendFromEmpty();
        }
    });

    //  메시지 DOM 추가
    function appendMsg(role, content) {
        const row    = document.createElement('div');
        const fig    = document.createElement('figure');
        const bubble = document.createElement('div');

        row.className     = 'msg ' + role;
        fig.textContent   = role === 'user' ? 'U' : 'AI';
        fig.setAttribute('aria-hidden', 'true');
        bubble.textContent = content;

        row.appendChild(fig);
        row.appendChild(bubble);
        messages.appendChild(row);
        return bubble;
    }

    function scrollToBottom() {
        messagesWrap.scrollTop = messagesWrap.scrollHeight;
    }

    //  textarea 자동 높이
    [userInput, emptyInput].forEach(function (el) {
        el.addEventListener('input', function () {
            el.style.height = 'auto';
            el.style.height = el.scrollHeight + 'px';
        });
    });

    //  토스트
    let toastTimer = null;

    function showToast(msg, isError) {
        toast.textContent = msg;
        toast.className   = 'show' + (isError ? ' error' : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { toast.className = ''; }, 2800);
    }

    //  CSRF 토큰
    function getCsrfToken() {
        const meta = document.querySelector('meta[name="_csrf"]');
        return meta ? meta.getAttribute('content') : '';
    }

    //  초기화
    loadRooms();
    setView('empty');
});