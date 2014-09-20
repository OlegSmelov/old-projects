var mapGenerator  = new Maps(),
    maps          = mapGenerator.generateMaps(),
    mapNumber     = 0,
    map           = maps[mapNumber],
    map_collider  = new MapCollider(map),
    game_ended    = false;

function respawnPlayers() {
    for (player_id in players) {
        var player = players[player_id];
        if (player.team == "blue") {
            player.x = map.spawn_one.x;
            player.y = map.spawn_one.y;
        } else {
            player.x = map.spawn_two.x;
            player.y = map.spawn_two.y;
        }

        player.destination_point.x = player.x;
        player.destination_point.y = player.y;
    }
}

function loadNextMap() {
    mapNumber++;
    if (mapNumber < maps.length) {
        map = maps[mapNumber];
        map_collider = new MapCollider(map);
        respawnPlayers();

        return true;
    }

    return false;
}

function predict_movement(inputs, seq_num){
    var direction_x = 0;
    var direction_y = 0;

    for(input in inputs){
        if(inputs[input] == "left"){
            direction_x = -1.0;
        }
        if(inputs[input] == "right"){
            direction_x = +1.0;
        }
        if(inputs[input] == "up"){
            direction_y = -1.0;
        }
        if(inputs[input] == "down"){
            direction_y = +1.0;
        }
    }

    // vector normalization
    var vector_length = Math.sqrt(direction_x * direction_x + direction_y * direction_y);
    if (vector_length > 0.0001) {
        direction_x /= vector_length;
        direction_y /= vector_length;
    }

    var new_position = {
        x: players[my_id].x + direction_x * players[my_id].speed * delta / 1000,
        y: players[my_id].y + direction_y * players[my_id].speed * delta / 1000
    };

    // FIXME: this depends on the sprites used
    var legs_position = {
        x: new_position.x + 25,
        y: new_position.y + 50
    };

    if (map_collider.inside(legs_position)) {
        players[my_id].x = new_position.x;
        players[my_id].y = new_position.y;
    }

    //set the coordinates for a point in time
    for(unprocessed_input in unprocessed_inputs){
        if(unprocessed_inputs[unprocessed_input].sequence_number == seq_num){
            unprocessed_inputs[unprocessed_input].pos = {x : players[my_id].x, y : players[my_id].y};
        }
    }
}

function predict_attack(attack_point, seq_num){
    var destination_point = {};
    var vector = {};
    vector.x = attack_point.x - players[my_id].x;
    vector.y = attack_point.y - players[my_id].y;

    var vector_length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    if (vector_length > 0.0001) {
        vector.x /= vector_length;
        vector.y /= vector_length;
    }

    destination_point.x = players[my_id].x;
    destination_point.y = players[my_id].y;

    for (var i = 0; i <= 100; i++) {
        var new_position = {
            x: players[my_id].x + vector.x * players[my_id].attack_distance * (i / 100.0),
            y: players[my_id].y + vector.y * players[my_id].attack_distance * (i / 100.0)
        };

        var legs_position = {
            x: new_position.x + 25,
            y: new_position.y + 50
        };

        if (map_collider.inside(legs_position)) {
            destination_point = new_position;
        } else {
            break;
        }
    }

    players[my_id].destination_point = destination_point;

    for(unprocessed_input in unprocessed_inputs){
        if(unprocessed_inputs[unprocessed_input].sequence_number == seq_num){
            unprocessed_inputs[unprocessed_input].pos = {x : destination_point.x, y : destination_point.y};
        }
    }
}

function interpolate_attack() {
    var pl = players[my_id];
    var time_ratio;

    if(pl.attack.time_passed >= pl.attack.time_total){
        pl.attack.time_passed = 0;
        pl.attack.time_total = Math.sqrt(Math.pow(pl.destination_point.x - pl.x, 2) + Math.pow(pl.destination_point.y - pl.y, 2))/pl.attack_speed;
    }

    pl.attack.time_passed += delta/1000;
    time_ratio = Math.min(pl.attack.time_passed / pl.attack.time_total, 1);
    pl.x = pl.x + time_ratio *  (pl.destination_point.x - pl.x);
    pl.y = pl.y + time_ratio *  (pl.destination_point.y - pl.y);

    if(pl.attack.time_passed >= pl.attack.time_total){
        pl.attacking = false;
    }
}


//see what to do with received data
function evaluate(player){
    if(player.id == my_id){
        //if self, see whether your data is legit
        reconcile(player);
    } else {
        //if not, add the data to the position history of other players
        if(player.special){
            if(player.special == "attack"){
                players[player.id].position_buffer.push({x : player.x, y : player.y, special: "attack"});
            }
        } else {
            players[player.id].position_buffer.push({x : player.x, y : player.y});
        }
    }
}


function take_damage(id){
    players[id].damaged = true;
    players[id].hp -= 1;
    setTimeout(function(){
        players[id].damaged = false;
    }, 100);
}


//check if your data is legit, don't care for everything below servers sequence number; if not legit, recalculate from the okay spot
function reconcile(player){
    var remove_to = 0;
    for(input in unprocessed_inputs){
        if(unprocessed_inputs[input].sequence_number == player.sequence_approved){
            remove_to = input;
            if(unprocessed_inputs[input].pos.x != player.x || unprocessed_inputs[input].pos.y != player.y ){
                players[my_id].x = player.x;
                players[my_id].y = player.y;
            } else {
                break;
            }
        } else if(unprocessed_inputs[input].sequence_number > player.sequence_approved){
            if(unprocessed_inputs[input].special){
                if(unprocessed_inputs[input].special == "attack"){
                    predict_attack(unprocessed_input[input].pos, unprocessed_inputs[input].sequence_number);
                }
            } else {
                predict_movement(unprocessed_inputs[input].inputs, unprocessed_inputs[input].sequence_number);
            }
        }
    }
    unprocessed_inputs.splice(0, remove_to);
}


//simulate gradual movement between points in other players history
function interpolate_others(){
    var time_ratio = 0;
    for(player in players){
        if(player != my_id){
            var pl = players[player];

            pl.attacking = false;
            if(typeof pl.position_buffer != 'undefined'){
                if(pl.position_buffer.length > 5) {
                    pl.position_buffer.splice(0, pl.position_buffer.length - 5);
                }
                if(pl.position_buffer.length){
                    pl.attacking = pl.position_buffer[0].special? true:false;
                    //if distance passed is higher then total distance, then restart this calculation
                    if(pl.time_passed >= pl.time_total){
                        pl.time_passed = 0;
                        pl.time_total = Math.sqrt(Math.pow(pl.position_buffer[0].x - pl.x, 2) + Math.pow(pl.position_buffer[0].y - pl.y, 2))/ (pl.position_buffer[0].special? pl.attack_speed:pl.speed);
                    }

                    //increment the traveled distance(S = v*t), interpolate between two points based on percental distance passed
                    pl.time_passed += delta/1000;
                    time_ratio = Math.min(pl.time_passed / pl.time_total, 1);
                    pl.x = pl.x + time_ratio *  (pl.position_buffer[0].x - pl.x);
                    pl.y = pl.y + time_ratio *  (pl.position_buffer[0].y - pl.y);

                    //if distance passed is higher then total distance, then remove the previous point from history of points
                    if(pl.time_passed >= pl.time_total){
                        pl.position_buffer.splice(0,1);
                    }
                }
            }
        }
    }
}