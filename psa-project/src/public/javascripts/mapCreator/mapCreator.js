var
    canvas,
    ctx,
    canvas_width,
    canvas_height,
    mapBackgroundImage,
    map,
    mapURL,
    rect,
    exportArea;

var mousePosition = { x: 0, y: 0 };

var draggingMarker = false;
var draggingSpawnOne = false;
var draggingSpawnTwo = false;
var marker = {};

var lineOuterColor = "#ecf0f1";
var lineInnerColor = "#3498db";
var lineOuterWidth = 4;
var lineInnerWidth = 2;

var dotMarkerOuterColor = "#ecf0f1";
var dotMarkerInnerColor = "#3498db";
var dotMarkerOuterSize = 5;
var dotMarkerInnerSize = 3;


function say(text){
    console.log(text);
}

function run(){
    canvas = document.getElementById("gameView");
    canvas.onselectstart = function () { return false; };
    canvas.addEventListener('mousedown', function( event ){ onCanvasMouseDown( event ); } );
    canvas.addEventListener('mouseup', function( event ){ onCanvasMouseUp( event ); } );
    canvas.addEventListener('mousemove', function( event ){ onCanvasMouseMove( event ); } );

    addEventListener("keydown", function (event) {
        onKeyDown( event );
    });

    ctx = canvas.getContext("2d");
    canvas_width = document.getElementById("gameView").width;
    canvas_height = document.getElementById("gameView").height;
    rect = canvas.getBoundingClientRect();

    if( !map ){
        map = new Map(
                [], 
                mapURL,
                {x: 200, y: 300},
                {x: 600, y: 300}
                );
    }

    mapBackgroundImage = new Image();
    mapBackgroundImage.src = map.background;

    exportArea = document.querySelector( ".mapCreator-export" );

    requestAnimationFrame( refresh );
}

function refresh(){
    drawMap();
    exportArea.value = JSON.stringify( map );
    requestAnimationFrame( refresh ); 
}

function drawLine(from, to, color, width) {
    ctx.beginPath();
    ctx.moveTo(from.x, from.y);
    ctx.lineTo(to.x, to.y);
    ctx.strokeStyle = color;
    ctx.lineWidth = width;
    ctx.stroke();
}

function drawDot( point, color, size ) {
    ellipse( ctx, point.x, point.y, size, size, color);
}

function drawSquare( point, color, size ) {
    ctx.fillStyle = color;
    ctx.fillRect( point.x-size/2, point.y-size/2, size, size );
}

function ellipse(context, cx, cy, rx, ry, color){
        context.save(); // save state
        context.beginPath();

        context.translate(cx-rx, cy-ry);
        context.scale(rx, ry);
        context.arc(1, 1, 1, 0, 2 * Math.PI, false);

        context.restore(); // restore to original state
        ctx.fillStyle = color;
        context.fill();
}

function drawMap() {
    ctx.drawImage(mapBackgroundImage, 0, 0, map.width, map.height);
    
    for (var polygon_id in map.polygons) {
        var polygon = map.polygons[polygon_id];
        for (var i = 0; i < polygon.length; i++) {
            drawLine(polygon[i], polygon[ (i + 1)%polygon.length ], lineOuterColor, lineOuterWidth);
            drawLine(polygon[i], polygon[ (i + 1)%polygon.length ], lineInnerColor, lineInnerWidth);
        }
    }

    for (var polygon_id in map.polygons) {
        var polygon = map.polygons[polygon_id];
        for (var i = 0; i < polygon.length; i++) {
            drawDot(polygon[i], dotMarkerOuterColor, dotMarkerOuterSize );
            drawDot(polygon[i], dotMarkerInnerColor, dotMarkerInnerSize);
            if( marker ){
                if( polygon_id == marker.polygon && i == marker.point ){
                    drawDot(polygon[i], "yellow", dotMarkerOuterSize );
                    drawDot(polygon[i], "green", dotMarkerInnerSize);
                }
            }
        }

    }

    drawSquare( map.spawn_one, "#AA3333", 50 );
    drawSquare( map.spawn_one, "#AA5555", 40 );

    drawSquare( map.spawn_two, "#3333AA", 50 );
    drawSquare( map.spawn_two, "#5555AA", 40 );
}

function onCanvasMouseDown( event ){
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;

    for ( polygon_id in map.polygons) {
        var polygon = map.polygons[polygon_id];
        for ( point_id in polygon) {
            var point = polygon[ point_id ];

            if( Math.abs(point.x - x) < 5 && Math.abs(point.y - y) < 5 ){
                draggingMarker = true;
                marker = { polygon: parseInt(polygon_id), point: parseInt(point_id) };
                return;
            }
        }
    }

    if( Math.abs(map.spawn_one.x - x) < 25 && Math.abs(map.spawn_one.y - y) < 25 ){
        draggingSpawnOne = true;
        return;
    }

    if( Math.abs(map.spawn_two.x - x) < 25 && Math.abs(map.spawn_two.y - y) < 25 ){
        draggingSpawnTwo = true;
        return;
    }

    marker = null;
}

function onCanvasMouseUp( event ){
    draggingMarker = false;
    draggingSpawnOne = false;
    draggingSpawnTwo = false;
}

function onCanvasMouseMove( event ){
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;
    mousePosition = { x: x, y: y };
    
    if( draggingMarker ){
        map.polygons[ marker.polygon ][ marker.point ].x = x;
        map.polygons[ marker.polygon ][ marker.point ].y = y;
    }

    if( draggingSpawnOne ){
        map.spawn_one.x = x;
        map.spawn_one.y = y;
    }

    if( draggingSpawnTwo ){
        map.spawn_two.x = x;
        map.spawn_two.y = y;
    }
}

function onKeyDown( event ){
    if( String.fromCharCode( event.keyCode ).toLowerCase() == "d" ){
        if( marker ){
            removeSelectedMarker();
        }
    }

    if( String.fromCharCode( event.keyCode ).toLowerCase() == "a" ){
        if( marker ){
            addNearSelectedMarker();
        } else {
            createNewPolygon();
        }
    }
}

function removeSelectedMarker(){
    map.polygons[ marker.polygon ].splice( marker.point, 1 );
    if( map.polygons[ marker.polygon ].length == 0 ){
        map.polygons.splice( marker.polygon, 1 );
    }

    marker = null;
}

function addNearSelectedMarker(){
    var cur = marker.point;
    var prev = cur==0? map.polygons[ marker.polygon ].length - 1: cur-1; 
    var next = cur==(map.polygons[ marker.polygon ].length - 1)? 0: cur+1;
    var pos;

    var distancePrev = Math.sqrt( Math.pow( map.polygons[ marker.polygon ][ prev ].x - mousePosition.x, 2 ) + Math.pow( map.polygons[ marker.polygon ][ prev ].y - mousePosition.y, 2 ) );
    var distanceNext = Math.sqrt( Math.pow( map.polygons[ marker.polygon ][ next ].x - mousePosition.x, 2 ) + Math.pow( map.polygons[ marker.polygon ][ next ].y - mousePosition.y, 2 ) );
    var distancePrevCur = Math.sqrt( Math.pow( map.polygons[ marker.polygon ][ prev ].x - map.polygons[ marker.polygon ][ cur ].x, 2 ) + Math.pow( map.polygons[ marker.polygon ][ prev ].y - map.polygons[ marker.polygon ][ cur ].y, 2 ) );
    var distanceNextCur = Math.sqrt( Math.pow( map.polygons[ marker.polygon ][ next ].x - map.polygons[ marker.polygon ][ cur ].x, 2 ) + Math.pow( map.polygons[ marker.polygon ][ next ].y - map.polygons[ marker.polygon ][ cur ].y, 2 ) );

    distancePrev = distancePrev / distancePrevCur;
    distanceNext = distanceNext / distanceNextCur;


    // var distancePrev = computeDistanceToLine( computeLine( map.polygons[ marker.polygon ][ prev ], map.polygons[ marker.polygon ][ cur ]) , mousePosition );
    // var distanceNext = computeDistanceToLine( computeLine( map.polygons[ marker.polygon ][ next ], map.polygons[ marker.polygon ][ cur ]) , mousePosition );

    if( distancePrev < distanceNext ){
        pos = cur;
    } else {
        pos = cur + 1;
    }

    var buff = [];
    buff = buff.concat( map.polygons[ marker.polygon ].slice( 0, pos ) );
    buff = buff.concat( [{x: mousePosition.x, y: mousePosition.y }] );
    buff = buff.concat( map.polygons[ marker.polygon ].slice( pos ) );
    map.polygons[ marker.polygon ] = buff;
    marker.point = pos;

}

// function computeLine( point1, point2 ){
//     var x1 = point1.x;
//     var y1 = point1.y;
//     var x2 = point2.x;
//     var y2 = point2.y;
    
//     var a = y1-y2;
//     var b = x2-x1;
//     var c = (x1-x2)*y1 + (y2-y1)*x1;

//     return {a: a, b: b, c: c};
// }

// function computeDistanceToLine( line, point ){
//     var x = point.x;
//     var y = point.y;

//     var distance = Math.abs(line.a * x + line.b * y + line.c) / Math.sqrt( Math.pow( line.a, 2 ) + Math.pow( line.b, 2 ) );

//     return distance;
// }

function createNewPolygon(){
    map.polygons.push( [ { x: mousePosition.x, y: mousePosition.y } ] );
    marker = { polygon: map.polygons.length - 1, point: 0 };
}
