var buffer_ctx,
    canvas_width,
    canvas_height;

/*var mapGenerator = new Maps();
var maps = mapGenerator.generateMaps();
var map = maps[0];
var mapBackground = new Image();
mapBackground.src = map.background;*/

var mapBackgroundImage = new Image();
reloadBackground();

function reloadBackground() {
    mapBackgroundImage = assetManager.getAsset(map.background);
}
    
window.addEventListener('load', startAnimEngine);
function startAnimEngine(){
    var canvas_width = document.getElementById("gameView").width;
    var canvas_height = document.getElementById("gameView").height;

    buffer_canvas = document.createElement("canvas");
    buffer_canvas.width = canvas_width;
    buffer_canvas.height = canvas_height;
    buffer_ctx = buffer_canvas.getContext("2d");
    buffer_ctx.imageSmoothingEnabled = false;
    ctx.imageSmoothingEnabled = false;
}

// var anim_idle = new Animation('/assets/sprites/idle.png', 16,32,1);
// var anim_run = new Animation('/assets/sprites/run.png', 21,32,9);
// var anim_attack = new Animation('/assets/sprites/attack.png', 21,32,1);
// var anim_get_damaged = new Animation('/assets/sprites/get_damaged.png', 19,30,1);
function prepare_animations( perk, color ){
    var anim_idle = new Animation('/assets/sprites/'+ perk +'/knight_idle.png', 50,50,1);
    var anim_run = new Animation('/assets/sprites/'+ perk +'/knight_run.png', 50,50,8);
    var anim_attack = new Animation('/assets/sprites/'+ perk +'/knight_attack.png', 50,50,1);
    var anim_get_damaged = new Animation('/assets/sprites/'+ perk +'/knight_get_damaged.png', 50,50,1);
    
    var animations = new Animations(0,0, anim_idle, anim_run, anim_attack, anim_get_damaged, color=="red"? [2,0.5,0.5]: [1,1,2]);
    return animations;
}


//class that defines the source sprite and parameters of a single frame in it
function Animation(sprite, width, height, frame_count){
    var image_object = assetManager.getAsset(sprite);

    this.image = image_object;
    this.image_width = width;
    this.image_height = height;
    this.frame_count = frame_count;
}



//class that holds data about the state of current animation, deltas of position for flipping, color enhancer, and standart animations
function Animations(start_x, start_y, animation_idle, animation_run, animation_attack, animation_get_damaged, color){
    this.frame = 0;
    this.old_x = start_x? start_x:0;
    this.old_y = start_y? start_y:0;
    this.idle = animation_idle? animation_idle:null;
    this.run = animation_run? animation_run:null;
    this.attack = animation_attack? animation_attack:null;
    this.get_damaged = animation_get_damaged? animation_get_damaged:null;
    this.color = color? color:[2.5,0.5,0.5];
    this.facing_left = false;
}


//runs a frame of selected animation, checks for flipping, enhances color of object; first draws object onto buffer canvas, 
//encahnces color, checks for opaque pixels, replaces them with real pixels, pastes the image to the real canvas
function animate(animation, is_flipped){
    if(!(this.x && this.y))
        return;

    var image_width = this.animations[animation].image_width;
    var image_height = this.animations[animation].image_height;
    this.animations.frame = (this.animations.frame+6*delta/1000)%this.animations[animation].frame_count;
    var offset_x = Math.floor(this.animations.frame)*image_width;
    var offset_y = 0;

    if(typeof is_flipped === 'undefined'){
        if(this.animations.old_x > this.x){
            is_flipped = true;
        } else if(this.animations.old_x < this.x){
            is_flipped = false;
        } else {
            is_flipped = this.animations.facing_left;
        }
    }

    this.animations.facing_left = is_flipped;
    
    //add shadows
    ellipse(ctx, this.x + this.animations.idle.image_width/2, this.y + this.animations.idle.image_height, this.animations.idle.image_width*0.3, this.animations.idle.image_height*0.1);

    //push axis info, flip if needed, draw image, pop axis info
    buffer_ctx.clearRect(0,0,1000,1000);
    buffer_ctx.save();
    if(is_flipped)
        buffer_ctx.transform(-1, 0, 0, 1, canvas_width,0);
    buffer_ctx.drawImage(this.animations[animation].image, offset_x, offset_y, image_width, image_height, is_flipped? (canvas_width-this.x)-image_width: this.x, this.y, image_width, image_height);
    buffer_ctx.restore();

    //change colors if needed, restore image data in opaque sectors, paste it
    var pixels = buffer_ctx.getImageData(this.x, this.y, image_width, image_height);
    var current_pixel_data = ctx.getImageData(this.x, this.y, image_width, image_height).data;
    var buffer_pixel_data = pixels.data;
    for (var i=0; i<buffer_pixel_data.length; i+=4) {
        var r = buffer_pixel_data[i]; var g = buffer_pixel_data[i+1]; var b = buffer_pixel_data[i+2];
        
        if(buffer_pixel_data[i+3] != 255){
            
            buffer_pixel_data[i] = current_pixel_data[i];
            buffer_pixel_data[i+1] = current_pixel_data[i+1];
            buffer_pixel_data[i+2] = current_pixel_data[i+2];
            buffer_pixel_data[i+3] = current_pixel_data[i+3];
            continue;
        }

        buffer_pixel_data[i]    = this.animations.color[0] * r;
        buffer_pixel_data[i+1]  = this.animations.color[1] * g;
        buffer_pixel_data[i+2]  = this.animations.color[2] * b;
    }


    ctx.putImageData(pixels, this.x, this.y);

    //DRAW HP
    // so many magic numbers, lol
    ctx.beginPath();
    ctx.moveTo(this.x, this.y - 4);
    ctx.lineTo(this.x + this.animations.idle.image_width, this.y - 4);
    ctx.lineWidth = 2;

    // set line color
    ctx.strokeStyle = '#ff0000';
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(this.x, this.y - 4);
    ctx.lineTo(this.x + this.hp/this.maxHP * this.animations.idle.image_width, this.y - 4);
    ctx.lineWidth = 2;

    // set line color
    ctx.strokeStyle = '#00ff00';
    ctx.stroke();

    //sprite frame advancement
    this.animations.old_x = this.x;
    this.animations.old_y = this.y;
}

function ellipse(context, cx, cy, rx, ry){
        context.save(); // save state
        context.beginPath();

        context.translate(cx-rx, cy-ry);
        context.scale(rx, ry);
        context.arc(1, 1, 1, 0, 2 * Math.PI, false);

        context.restore(); // restore to original state
        ctx.fillStyle = 'rgba(0,0,0, 0.2)';
        context.fill();
}

function showFPS(fps){
    fps = Math.ceil(fps);
    document.getElementById("fps").innerHTML = fps;
}

function drawLine(from, to) {
    ctx.beginPath();
    ctx.moveTo(from.x, from.y);
    ctx.lineTo(to.x, to.y);
    ctx.stroke();
}

function drawCharge(){
    ctx.lineCap = "round";

    ctx.beginPath();
    ctx.moveTo( 10, 10);
    ctx.lineTo( canvas_width-10, 10);
    ctx.lineWidth = 12;

    // set line color
    ctx.strokeStyle = 'rgba(127, 140, 141,0.5)';
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo( 10, 11);
    ctx.lineTo( (players[my_id].power / 6) * canvas_width-10, 10);
    ctx.lineWidth = 10;

    // set line color
    ctx.strokeStyle = 'rgba('+0+','+ 0 +','+ Math.round(255*players[my_id].power / 6) +',0.7)';
    ctx.stroke();
}

function drawMap() {
    ctx.drawImage(mapBackgroundImage, 0, 0, map.width, map.height);
    
    return;
    for (var polygon_id in map.polygons) {
        var polygon = map.polygons[polygon_id];
        for (var i = 0; i < polygon.length - 1; i++) {
            drawLine(polygon[i], polygon[i + 1]);
        }
        drawLine(polygon[0], polygon[polygon.length - 1]);
    }
}

function draw(){
    ctx.clearRect(0, 0, canvas_width, canvas_height);

    drawMap();

    var player_layers = [];

    for (player_id in players) {
        player_layers.push( [player_id, players[player_id].y] );
    }

    //player layering
    player_layers.sort( function(a,b) { return a[1] - b[1] });

    for( player_id in player_layers ){
        var player = players[ player_layers[player_id][0] ];
        player.damaged? player.animate("get_damaged"):player.attacking? player.animate("attack"): player.x != player.animations.old_x || player.y != player.animations.old_y? player.animate("run"):player.animate("idle");
    }

    drawCharge();
}

function drawTextScreen(text) {
    if (!ctx) {
        return;
    }
    ctx.clearRect(0, 0, canvas_width, canvas_height);

    ctx.font = "20pt Arial";
    ctx.textAlign = "center";
    ctx.fillText(text, canvas_width / 2, canvas_height / 2);
}