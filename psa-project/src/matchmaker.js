// Matchmaker is a class that creates games

var
    game        = require('./game'),
    game_player = require('./game.player'),
    UUID        = require('node-uuid');

// "Constants"
var
    GAME_PLAYERS = 2;
    PLAYER_COLORS = ['blue', 'red'];

var matchmaker = function () {
    this.games = [];
    this.game_started = null; // callback function that is called when a new match is found (function game_started(game))
};

matchmaker.prototype.update_user_status = function (game) {
    for (player_id in game.players) {
        game.players[player_id].socket.emit('update_status',
            { status: 'Wating for other players, players in the lobby: ' + Object.keys(game.players).length });
    }
};

matchmaker.prototype.update_game_status = function (game) {
    for (var player_id in game.players) {
        if (!game.players[player_id].ready) {
            return;
        }
    }

    var number_of_players = Object.keys(game.players).length;
    if (number_of_players >= GAME_PLAYERS) {
        // report that the game was started
        if (typeof this.game_started == 'function') {
            this.game_started(game);
        }
        // the game was started, remove the game from matchmaker
        this.remove_game(game);
    }
}

// finds a room that a player can join OR
// creates a new game and adds it to the games
matchmaker.prototype.find_game = function () {
    for (var i = 0; i < this.games.length; i++) {
        if (Object.keys(this.games[i].players).length < GAME_PLAYERS) {
            return this.games[i];
        }
    }

    // no game found, we need to create a new one
    var new_game = new game(UUID());
    this.games.push(new_game);
    return new_game;
};

// returns true if the game was successfully removed
matchmaker.prototype.remove_game = function (game) {
    for (var i = this.games.length - 1; i >= 0; i--) {
        if (this.games[i] == game) {
            this.games.splice(i, 1);
            return true;
        }
    }
    return false;
};

matchmaker.prototype.add_player = function (player) {
    var game_to_join = this.find_game();
    game_to_join.add_player(player);
    player.game = game_to_join;

    this.update_user_status(game_to_join);

    var number_of_players = Object.keys(game_to_join.players).length;
    if (PLAYER_COLORS.length > number_of_players - 1) {
        player.team = PLAYER_COLORS[number_of_players - 1];
    }
    //console.log("Assigned color: " + player.team);

    this.update_game_status(game_to_join);
};

// Tries to remove player from the queue
// returns true if the player was found and successfully removed
matchmaker.prototype.remove_player = function (player) {
    for (var i = 0; i < this.games.length; i++) {
        var players = this.games[i].players;
        for (player_id in players) {
            if (player_id == player.id) {
                delete players[player_id];
                this.update_user_status(this.games[i]);
                return true;
            }
        }
    }
    return false;
};

matchmaker.prototype.playerIsReady = function (player) {
    for (var i = 0; i < this.games.length; i++) {
        var players = this.games[i].players;
        for (player_id in players) {
            if (player_id == player.id) {
                this.update_game_status(this.games[i]);
                return true;
            }
        }
    }
    return false;
};

module.exports = matchmaker;