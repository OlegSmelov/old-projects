var
    prev_time,
    cur_time,
    canvas,
    ctx,
    canvas_width,
    canvas_height,
    delta,
    selected_perk;

var my_id;
var players = {};

function say(text){
    console.log(text);
}

function run(){
    canvas = document.getElementById("gameView");
    canvas.onselectstart = function () { return false; };
    canvas.addEventListener('mousedown', onCanvasMouseDown);

    addEventListener("keydown", function (event) {
        keys_down[event.keyCode] = true;
        if (arrow_keys.indexOf(event.keyCode) > -1) {
            event.preventDefault();
        }
    });

    addEventListener("keyup", function (event) {
        delete keys_down[event.keyCode];
    });

    ctx = canvas.getContext("2d");
    canvas_width = document.getElementById("gameView").width;
    canvas_height = document.getElementById("gameView").height;

    prev_time = Date.now();

    is_loaded = setInterval(function(){
        if (assetManager.isDone() && selected_perk ) {
            clearInterval(is_loaded);
            drawTextScreen("Waiting for the other player");
            send_ready( selected_perk );
        } else {
            drawTextScreen("Loading...");
        }
    },500);

    updateScores();
}

function frame() {
    if (!game_ended) {
        cur_time = Date.now();
        delta = cur_time - prev_time;
        showFPS(1000/delta);
        checkInput();
        interpolate_others();
        handle_sound();
        draw();
        draw_fancy_background();
        prev_time = cur_time;
        if (my_id && players[my_id]) {
            document.getElementById("hp").innerHTML = players[my_id].hp;
        }
    } else {
        drawTextScreen("GG");
    }

    requestAnimationFrame(frame);
}

window.addEventListener('load', run);