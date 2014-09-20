var socket = io.connect();

var my_id;
var players = {};

socket.on('update_status', function (data) {
    var status = document.getElementById('status');
    status.innerHTML = 'Current status: ' + data.status;
    if (data.players != undefined) {
        players = data.players;
        var playerNum = 1;
        for(player in players){
            if(playerNum == 1){
                players[player].animations = prepare_animations( players[ player ].perk, "red" );
                players[player].animate = animate;
                players[player].sounds = red_player_sounds;
                players[player].play_sound = play_sound;
            }
            if(playerNum == 2){
                players[player].animations = prepare_animations( players[ player ].perk, "blue" );
                players[player].animate = animate;
                players[player].sounds = blue_player_sounds;
                players[player].play_sound = play_sound;
            }

            players[player].position_buffer = [];
            players[player].time_passed = 0;
            players[player].time_total = 0;
            players[player].destination_point = {x:0, y:0};
            players[player].attack = {};
            players[player].attacking = false;
            players[player].damaged = false;
            players[player].maxHP = players[player].hp;
            players[player].attack.time_passed = 0;
            players[player].attack.time_total = 0;
            playerNum++;
        }
        if (playerNum > 1) {
            requestAnimationFrame(frame);
        }
    }
});

socket.on('player_moved', function (player) {
    evaluate(player);
});

socket.on('hit_detected', function (who) {
    take_damage(who.id);
});

socket.on('your_power_level', function (data) {
    players[my_id].power = data.power;
});

socket.on('player_dead', function(who) {
    var iLost = who.id == my_id;
    //document.getElementById("message").innerText = iLost ? "You lost!" : "You won!";

    if (loadNextMap()) {
        for (var player_id in players) {
            var player = players[player_id];
            player.hp = player.maxHP;
        }
        reloadBackground();
    } else {
        //alert(iLost ? "You lost!" : "You won!");
    }
});

socket.on('respawn', function(respawnData) {
    for (var playerID in respawnData) {
        if (!players[playerID]) {
            players[playerID] = {};
        }
        players[playerID].x = respawnData[playerID].x;
        players[playerID].y = respawnData[playerID].y;

        players[playerID].destination_point = {};
        players[playerID].destination_point.x = players[playerID].x;
        players[playerID].destination_point.y = players[playerID].y;

        players[playerID].attack = {};
        players[playerID].attacking = false;
        players[playerID].damaged = false;
        players[playerID].attack.time_passed = 0;
        players[playerID].attack.time_total = 0;

        if (players[playerID].position_buffer) {
            players[playerID].position_buffer = [];
        }
    }

    clearInputs();
});

socket.on('game_end', function(data) {
    alert("Your score: " + data.score);
    var name = prompt("Your name", "");
    if (name && name != "") {
        socket.emit('save_high_score', { name: name });
    }
    game_ended = true;
});

socket.on('high_scores_saved', function (data) {
    updateScores();
});

socket.on('your_id', function (data) {
    my_id = data.id;
    if (!(my_id in players)) {
        players[my_id] = {};
    }
    players[my_id].x = players[my_id].x || 50;
    players[my_id].y = players[my_id].y || 50;
});

function ghettoFilter(input) {
    return input.replace(/</g, '&lt;');
}

socket.on('high_scores', function (data) {
    var scores = data.scores;
    var contentTag = document.getElementById('high-scores-content');
    var resultHTML = '';
    for (var i = 0; i < scores.length; i++) {
        resultHTML += ghettoFilter(scores[i].name) + ': ' + scores[i].score + '<br>';
    }
    contentTag.innerHTML = resultHTML;
});

function send_inputs(inputs, delta, sequence_count) {
    socket.emit('player_move', {inputs: inputs, delta: delta, sequence_number: sequence_count});
}

function send_attack(target_point, sequence_count) {
    socket.emit('player_attack', {attack_point: target_point, seq_num: sequence_count});
}

function send_ready( perk_name ) {
    socket.emit('player_perk_select', {perk: perk_name} );
    socket.emit('player_ready');
}

function updateScores() {
    socket.emit('request_high_scores');
}