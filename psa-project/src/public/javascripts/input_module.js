var keys_down = [];
var arrow_keys = [37, 38, 39, 40];
var position_changed = false;
var inputs = [];
var unprocessed_inputs = [];
var sequence_count = 0;

// MONKEY BOT, enable with console
var run_right = true;
var run_down = false;
var run_left = false;
var run_up = false;
var monkey;
function monkey_king(){
    monkey = setInterval(function(){
        if(players[my_id].x < 300 && run_right){
            inputs.push("right");
        } else if(players[my_id].x >= 300){
            run_right = false;
            run_down = true;
        }

        if(players[my_id].y < 300 && run_down){
            inputs.push("down");
        } else if(players[my_id].y >= 300){
            run_down = false;
            run_left = true;
        }

        if(players[my_id].x > 30 && run_left){
            inputs.push("left");
        } else if(players[my_id].x <= 30){
            run_left = false;
            run_up = true;
        } 

        if(players[my_id].y > 30 && run_up){
            inputs.push("up");
        } else if(players[my_id].y <= 30){
            run_up = false;
            run_right = true;
        }    
        position_changed = true;
    },10);
}

function clearInputs() {
    inputs.splice(0, inputs.length);
    unprocessed_inputs.splice(0, unprocessed_inputs.length);
}

function checkInput(){
    if(players[my_id].attacking){
        interpolate_attack();
        return;
    }

    var player = players[my_id];
    if (37 in keys_down || 65 in keys_down) { //left
        inputs.push("left");
        position_changed = true;
    }
    if (38 in keys_down || 87 in keys_down) { //up
        inputs.push("up");
        position_changed = true;
    }
    if (39 in keys_down || 68 in keys_down) { //right
        inputs.push("right");
        position_changed = true;
    }
    if (40 in keys_down || 83 in keys_down) { //down
        inputs.push("down");
        position_changed = true;
    }

    if(position_changed){
        unprocessed_inputs.push({inputs: inputs, sequence_number: sequence_count});
        predict_movement(inputs, sequence_count);
        send_inputs(inputs, delta, sequence_count);
        sequence_count++;
        inputs = [];
        position_changed = false;
    }

}


function onCanvasMouseDown(event) {
    if (my_id == undefined) {
        return;
    }

    if(players[my_id].attacking)
        return;

    if(players[my_id].power <= 0)
        return;

    players[my_id].attacking = true;

    var attack_point = {};
    var canvas = document.getElementById("gameView");
    var rect = canvas.getBoundingClientRect();
    //25 = character width,height
    attack_point.x = event.clientX - rect.left - 25;
    attack_point.y = event.clientY - rect.top - 25;

    unprocessed_inputs.push({sequence_number: sequence_count, special: "attack"});
    send_attack(attack_point, sequence_count); 
    predict_attack(attack_point, sequence_count);
    sequence_count++;
}