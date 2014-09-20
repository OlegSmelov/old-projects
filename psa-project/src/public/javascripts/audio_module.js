var blue_attack_sound = new Sound( "/assets/sounds/attack.mp3" );
var blue_run_sound = new Sound( "/assets/sounds/run.mp3" );
var blue_damage_sound = new Sound( "/assets/sounds/damage.mp3" );

var red_attack_sound = new Sound( "/assets/sounds/attack.mp3" );
var red_run_sound = new Sound( "/assets/sounds/run.mp3" );
var red_damage_sound = new Sound( "/assets/sounds/damage.mp3" );

var red_player_sounds = new Sounds( red_attack_sound, red_run_sound, red_damage_sound );
var blue_player_sounds = new Sounds( blue_attack_sound, blue_run_sound, blue_damage_sound );

var bgm_sound = new Sound( "/assets/sounds/bgm.mp3" );
bgm_sound.audio.play();
bgm_sound.audio.volume = 0.1;

var idle = true;

function Sound ( url ){
	this.audio = assetManager.getAsset(url);
}

function Sounds ( attack_sound, run_sound, damage_sound ){
	this.attack = attack_sound;
	this.is_attack_playing = false;
	this.run = run_sound;
	this.is_run_playing = false;
	this.damage = damage_sound;
	this.is_damage_playing = false;
}


function play_sound ( sound ){
	this.sounds[ sound ].audio.play();
	this.sounds[ "is_"+ sound +"_playing" ] = true;
	this.sounds[ sound ].audio.addEventListener( 'ended', set_flag_off( this.sounds, sound ) );
}


function set_flag_off( sounds, sound ){
	sounds[ "is_"+ sound +"_playing" ] = false;
}


function handle_sound (){
    for (player_id in players) {
    	if( !players[ player_id ].sounds ) return;
    	
        var player = players[player_id];
        player.damaged && !player.sounds.is_damage_playing? player.play_sound( "damage" ):player.attacking && !player.sounds.is_attack_playing? player.play_sound( "attack" ): (player.x != player.animations.old_x || player.y != player.animations.old_y) && !player.sounds.is_run_playing? player.play_sound( "run" ):idle = true;
    }
}