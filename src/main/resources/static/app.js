window.tds = document.getElementsByTagName("td");
window.score = {
    O: 0,
    X: 0
};

window.addEventListener("load", function() {
    console.log("Window loaded");
    var stompClient = null;

    window.socket = new SockJS("/game");

    window.init = function() {
        for (var i = 0; i < window.tds.length; i++) {
            window.tds[i].innerHTML = "";
        }
        document.getElementById("scoreboard").innerHTML = "<p>Score O : " + window.score.O + "</p><p>Score X : " + window.score.X + "</p>";
    };

    window.socket.onmessage = function(e) {
        var move = JSON.parse(e.data);
        console.dir(move);
        if (move.type == "start") {
            document.getElementById("game").className = "gameStarted"
        }
        else if (move.type == "move") {
            var el = "r" + move.row + "c" + move.col;
            document.getElementById(el).innerHTML = move.piece;
        } else if (move.type == "win") {
            window.score = {
                O: move.row,
                X: move.col
            };
            window.init();
        }
    };


    for (var i = 0; i < window.tds.length; i++) {
        window.tds[i].onclick = function(e){
            var id = this.id;
            var matches = id.match(/^r([0-9])c([0-9])$/);
            socket.send(
                JSON.stringify(
                    {
                        r: matches[1],
                        c: matches[2]
                    }
                )
            );
        };
    }
})
