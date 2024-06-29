$(document).ready(function (){
    connect();
});
function makeRoomName(str1, str2) {
    // 두 문자열을 알파벳 순서로 정렬하여 합친다.
    if (str1 < str2) {
        return str1 + '-' + str2;
    } else {
        return str2 + '-' + str1;
    }
}
let roomName = makeRoomName(userNickname, sendToUserNickname);

function connect() {
    var socket = new SockJS("/chatting");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
            stompClient.subscribe('/room/' + roomName, function (e) {
                showMessage(JSON.parse(e.body));
            });
            $('#title_room').text(sendToUserNickname);
        },
        function(e){
            alert('에러발생!!!!!!');
        }
    );
}

$sendBtn.click(function(e){
    let content = $contentBox.val();
    send(content);
    $contentBox.val('');
});


$contentBox.keypress(function(e) {
    if(e.keyCode === 13) {
        let content = $(this).val();
        send(content);
        $(this).val('');

        // 잠시 후 포커스를 다시 설정하여 모바일 키보드가 유지되도록 함
        setTimeout(() => {
            $(this).focus();
        }, 0);
    }
});

function showMessage(data){
    if(data.sender === userNickname){
        $('#chatting').prepend(
            `
            <div class="chat ch2">
                <div class="icon">
                    <img style="width: 100%; height: 100%" src='${userImgUrl}' alt="Profile" class="rounded-circle">
                </div>
                <div class="textbox">${data.content}</div>
            </div>
            `
        );
    } else {
        $('#chatting').prepend(
            `
            <div class="chat ch1">
                <div class="icon">
                    <img style="width: 100%; height: 100%" src='${receiverImgUrl}' alt="Profile" class="rounded-circle">
                </div>
                <div class="textbox">${data.content}</div>
            </div>
            `
        );
    }
}

function send(content){
    data = {
        'sender': userNickname,
        'receiver' : sendToUserNickname,
        'content': content
    };
    stompClient.send("/app/chat/send/" + roomName, {}, JSON.stringify(data));
}