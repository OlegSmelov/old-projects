
/**
 * Module dependencies.
 */

var inputs = [];






var express = require('express');
var http = require('http');
var path = require('path');
var socket_io = require('socket.io');
var UUID = require('node-uuid');
var mongodb = require('mongodb');

var routes = require('./routes');
var user = require('./routes/user');
var mapCreator = require('./routes/mapCreator');

var app = express();
var server = http.createServer(app);
var io = socket_io.listen(server);

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.cookieParser('your secret here'));
app.use(express.session());
app.use(app.router);
app.use(require('stylus').middleware(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/mapCreator', mapCreator.mapCreator);
app.get('/users', user.list);

app.get('/maps.js', function(req, res) {
    res.sendfile('./maps.js');
});

app.get('/map.collider.js', function(req, res) {
    res.sendfile('./map.collider.js');
});

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});


var
    matchmaker  = new (require('./matchmaker'))(),
    game_player = require('./game.player'),
    Maps         =require('./maps'),
    MapCollider = require('./map.collider');

var
    mapsGenerator = new Maps(),
    maps          = mapsGenerator.generateMaps();//,
    //mapNumber     = 0,
    //map           = maps[mapNumber],
    //map_collider  = new MapCollider(map);

function withMongodb(func) {
    var serv = new mongodb.Server('localhost', 27017, {});
    var db = new mongodb.Db('myDB', serv, { safe: false });
    db.open(function (error, db) {
        if (error) {
            console.error(error);
            return;
        }
        func(db);
    });
}

// withMongodb(function(db) {
//     var collection = new mongodb.Collection(db, 'scores');
//     // FIXME: sort by score
//     collection.insert({name: "Basement Dweller", score: 1000}, {safe: true}, function(err, docs) {
//         if (err) {
//             console.error(err);
//             return;
//         }
//         db.close();
//     });
// });

function respawnPlayers(game) {
    var respawnData = {};
    for (player_id in game.players) {
        var player = game.players[player_id];
        if (player.team == "blue") {
            player.pos.x = game.map.spawn_one.x;
            player.pos.y = game.map.spawn_one.y;
        } else {
            player.pos.x = game.map.spawn_two.x;
            player.pos.y = game.map.spawn_two.y;
        }
        respawnData[player.id] = player.pos;
    }
    console.log(respawnData);
    game.broadcast_message('respawn', respawnData);
}

function loadNextMap(game) {
    game.mapNumber++;
    if (game.mapNumber < maps.length) {
        for (var playerID in game.players) {
            var player = game.players[playerID];
            if( player.perk )
                player.set_perk( player.perk );
        }

        game.map = maps[game.mapNumber];
        game.mapCollider = new MapCollider(game.map);

        respawnPlayers(game);
    } else {
        for (var playerID in game.players) {
            var player = game.players[playerID];
            var score = Math.round((parseFloat(player.damageDealt) / game.totalDamageDealt) * 100);
            player.score = score;
            player.socket.emit('game_end', { score: score });
        }
    }
}

matchmaker.game_started = function (game) {
    game.status = 'started';

    loadNextMap(game);

    /*for (player_id in game.players) {
        var player = game.players[player_id];
        if (player.team == "blue") {
            player.pos.x = map.spawn_one.x;
            player.pos.y = map.spawn_one.y;
        } else {
            player.pos.x = map.spawn_two.x;
            player.pos.y = map.spawn_two.y;
        }
    }*/

    var players = {};
    for (player_id in game.players) {
        game.players[ player_id ].set_perk( game.players[ player_id ].perk );

        players[player_id] = {
            x: game.players[player_id].pos.x,
            y: game.players[player_id].pos.y,
            team: game.players[player_id].team,
            power: game.players[player_id].power,

            perk: game.players[ player_id ].perk,
            hp: game.players[ player_id ].hp,
            speed: game.players[ player_id ].speed,
            attack_speed: game.players[ player_id ].attack_speed,
            attack_distance: game.players[ player_id ].attack_distance
        };
    }

    for (player_id in game.players) {
        game.players[player_id].socket.emit('update_status', {
            status: "You're matched, your game id is " + game.id,
            players: players
        });
    }
};

io.sockets.on('connection', function (socket) {
    console.log("somebody connected");

    var new_player = new game_player(UUID(), undefined, socket);
    matchmaker.add_player(new_player);

    socket.emit('your_id', { id: new_player.id });

    socket.set('player', new_player);

    socket.on('sendmessage', function (data) {
        socket.get('player', function (err, player) {
            var players = player.game.players;
            for (player_id in players) {
                if (player_id == player.id) {
                    continue;
                }
                players[player_id].socket.emit('receivemessage', data);
            }
        });
    });

    socket.on('player_ready', function () {
        socket.get('player', function (err, player) {
            player.ready = true;
            matchmaker.playerIsReady(player);
            console.log('player ready');
        });
    });

    socket.on('disconnect', function () {
        socket.get('player', function (err, player) {
            if (player.game.status == 'waiting') {
                matchmaker.remove_player(player);
            } else {
                player.game.remove_player(player);
                player.game.broadcast_message('update_status', { status: 'Opponent disconnected' });
                player.game.broadcast_message('game_end');
                player.game = undefined;
            }
        });
    });

    socket.on('player_perk_select', function( data ){
        socket.get('player', function (err, player) {
            player.perk = data.perk;
        });
    });

    socket.on('request_high_scores', function (data) {
        withMongodb(function(db) {
            var collection = new mongodb.Collection(db, 'scores');
            collection.find().sort({score: -1, name: 1}).limit(10).toArray(function(err, docs) {
                if (err) {
                    console.error(err);
                    return;
                }
                socket.emit('high_scores', { scores: docs });
                db.close();
            });
        });
    });

    socket.on('save_high_score', function (data) {
        if (!data.name || data.name == "") {
            return;
        }
        socket.get('player', function (err, player) {
            if (!player.scoreSaved) {
                player.scoreSaved = true;
                withMongodb(function(db) {
                    var collection = new mongodb.Collection(db, 'scores');
                    collection.find({ name: data.name }).toArray(function(err, items) {
                        if (err) {
                            console.error(err);
                            return;
                        }
                        if (items.length > 0) {
                            collection.update({ _id: items[0]._id }, { name: items[0].name, score: items[0].score + player.score }, function(err, docs) {
                                if (err) {
                                    console.error(err);
                                    return;
                                }
                                socket.emit('high_scores_saved');
                                db.close();
                            });
                        } else {
                            collection.insert({name: data.name, score: player.score}, {safe: true}, function(err, docs) {
                                if (err) {
                                    console.error(err);
                                    return;
                                }
                                socket.emit('high_scores_saved');
                                db.close();
                            });
                        }
                    });

                    
                });
            }
        });
    });

    socket.on('player_attack', function (data) {
        socket.get('player', function (err, player) {

            if (player.game.status != 'started') {
                return;
            }

            if(Date.now() - player.attack.timer < 1000*player.attack_distance/player.attack_speed){
                return;
            }

            if( player.power <= 0 ){
                return;
            } else {
                player.power -= 1;
                player.socket.emit('your_power_level', { power: player.power } );

                var other_player;
                var players = player.game.players;
                for(player_id in players){
                    if(player_id != player.id){
                        other_player = players[player_id];
                    }
                }

                other_player.power += 1;
                other_player.socket.emit('your_power_level', { power: other_player.power } );
            }

            player.attack.timer = Date.now();

            var destination_point = {x:0, y:0};
            var vector = {x:0, y:0};
            vector.x = data.attack_point.x - player.pos.x;
            vector.y = data.attack_point.y - player.pos.y;

            var vector_length = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
            if (vector_length > 0.0001) {
                vector.x /= vector_length;
                vector.y /= vector_length;
            }

            destination_point.x = player.pos.x;
            destination_point.y = player.pos.y;

            for (var i = 0; i <= 100; i++) {
                var new_position = {
                    x: player.pos.x + vector.x * player.attack_distance * (i / 100.0),
                    y: player.pos.y + vector.y * player.attack_distance * (i / 100.0)
                };

                var legs_position = {
                    x: new_position.x + 25,
                    y: new_position.y + 50
                };

                if (player.game.mapCollider.inside(legs_position)) {
                    destination_point = new_position;
                } else {
                    break;
                }
            }

            player.destination_point = destination_point;
            
            player.simulation.x = player.pos.x;
            player.simulation.y = player.pos.y;

            player.pos.x = destination_point.x;
            player.pos.y = destination_point.y;
            player.sequence = data.sequence_number;

            // if (player.game.status != 'started') {
            //     return;
            // }


            var players = player.game.players;
            for (player_id in players) {
                    players[player_id].socket.emit('player_moved', { id: player.id, x: destination_point.x, y: destination_point.y, sequence_approved: player.sequence, special: "attack" });
            }

            //collision detection bit derp
            var other_player;
            for(player_id in players){
                if(player_id != player.id){
                    other_player = players[player_id];
                }
            }
            player.simulation.timer = Date.now();
            var new_simulation = setInterval(function(){
                if(Date.now() - player.simulation.timer >= 1000*player.attack_distance/player.attack_speed){
                    clearInterval(new_simulation);
                }
                time_ratio = (Date.now() - player.simulation.timer) / (1000*player.attack_distance/player.attack_speed);
                player.simulation.x += time_ratio * (destination_point.x - player.simulation.x);
                player.simulation.y += time_ratio * (destination_point.y - player.simulation.y);

                if(other_player && (Date.now() - other_player.attack.timer >= 1000*other_player.attack_distance/other_player.attack_speed)){
                    if((Math.abs(player.simulation.x - other_player.pos.x) < 16) && (Math.abs(player.simulation.y - other_player.pos.y) < 30)){
                        player.socket.emit('hit_detected', {id: other_player.id});
                        other_player.socket.emit('hit_detected', {id: other_player.id});
                        other_player.hp -= 1;

                        player.damageDealt += 1;
                        player.game.totalDamageDealt += 1;
                        
                        if (other_player.hp <= 0) {
                            player.socket.emit('player_dead', {id: other_player.id});
                            other_player.socket.emit('player_dead', {id: other_player.id});

                            loadNextMap(player.game);
                        }
                        clearInterval(new_simulation);
                    }
                }
            },10);
        });
    });

    socket.on('player_move', function (data) {
        socket.get('player', function (err, player) {

            if(Date.now() - player.attack.timer < 1000*player.attack_distance/player.attack_speed){
                return;
            }

            if(!(player.pos.x && player.pos.y)){
                player.pos.x = 0;
                player.pos.y = 0;
            }

            if(data.delta > 100){
                return;
            }

            var direction_x = 0;
            var direction_y = 0;

            for(input in data.inputs){
                if(data.inputs[input] == "left"){
                    direction_x = -1.0;
                }
                if(data.inputs[input] == "right"){
                    direction_x = +1.0;
                }
                if(data.inputs[input] == "up"){
                    direction_y = -1.0;
                }
                if(data.inputs[input] == "down"){
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
                x: player.pos.x + direction_x * player.speed * data.delta / 1000,
                y: player.pos.y + direction_y * player.speed * data.delta / 1000
            };

            // FIXME: this depends on the sprites used
            var legs_position = {
                x: new_position.x + 25,
                y: new_position.y + 50
            };

            if (player.game.mapCollider.inside(legs_position)) {
                player.pos = new_position;
            }

            player.sequence = data.sequence_number;

            if (player.game.status != 'started') {
                return;
            }
            var players = player.game.players;
            // if(datasent){
            //     return;
            // }
            // datasent = true;
            for (player_id in players) {
                // playerids.push(player_id);
                // setTimeout(function(){
                    if(players[player_id].last_sent_data.sequence == player.sequence && players[player_id].last_sent_data.id == player.id && players[player_id].last_sent_data.x == player.pos.x && players[player_id].last_sent_data.y == player.pos.y){
                        continue;
                    }
                    players[player_id].socket.emit('player_moved', { id: player.id, x: player.pos.x, y: player.pos.y, sequence_approved: player.sequence });
                    players[player_id].last_sent_data = { id: player.id, x: player.pos.x, y: player.pos.y, sequence: player.sequence };
                //     count++;
                //     datasent = false;
                // },100);
            }
        });
    });
});
// var playerids = [];
// var count = 0;
// var datasent = false;
