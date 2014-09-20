var game_player = function(id, game, socket) {
    this.id = id;
    this.game = game;
    this.socket = socket;
    this.inputs = [];
    this.sequence = 0;
    this.attack = {timer:0};
    this.power = 3;
    this.simulation = {timer:0, x:0, y:0};
    this.destination_point = {x:0, y:0};
    this.last_sent_data = {sequence:0, x:0, y:0, id: 0};
    this.pos = { x: 50, y: 50 };
    this.hp = 10;
    this.damageDealt = 0;
    this.score = 0;
    this.scoreSaved = false;
    this.team = 'blue';
    this.ready = false;
    this.set_perk = function( perk_name ){
        this.hp = perks[ perk_name ].hp;
        this.speed = perks[ perk_name ].speed;
        this.attack_speed = perks[ perk_name ].attack_speed;
        this.attack_distance = perks[ perk_name ].attack_distance;
    }
};

var perks = {
    swordnboard: { speed: 150, attack_distance: 300, attack_speed: 200, hp: 15 },
    shieldnshield: { speed: 150, attack_distance: 100, attack_speed: 200, hp: 30 },
    spear: { speed: 200, attack_distance: 400, attack_speed: 200, hp: 12 },
    twohandsword: { speed: 100, attack_distance: 400, attack_speed: 150, hp: 12 },
    swordnsword: { speed: 150, attack_distance: 100, attack_speed: 250, hp: 10 }
};

module.exports = game_player;
