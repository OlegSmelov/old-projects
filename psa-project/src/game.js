var game = function(id) {
    this.id = id;
    this.players = {};
    this.map = null;
    this.mapCollider = null;
    this.mapNumber = -1;
    this.totalDamageDealt = 0;
    this.status = 'waiting';
};

game.prototype.add_player = function (player) {
    this.players[player.id] = player;
}

game.prototype.remove_player = function (player) {
    delete this.players[player.id];
}

game.prototype.broadcast_message = function (message, data) {
    for (player_id in this.players) {
        this.players[player_id].socket.emit(message, data);
    }
};

module.exports = game;