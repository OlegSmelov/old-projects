var perk_list = [ "swordnboard", "swordnsword", "shieldnshield", "spear", "twohandsword" ];
var perk_stats_list = { swordnboard: {name: "Sword & Board", stats:"A boring class"}, 
						swordnsword:{name: "Sword & Sword", stats: "Better than one sword"}, 
						shieldnshield:{ name: "Shield & Shield", stats: "When one shield is not enough"},
						spear:{ name: "Spear", stats: "Every stick has two ends"}, 
						twohandsword:{ name: "Zweihander", stats: "Bringen Sie mir Fegelein" }
					};

var background_canvas;
var bgCnvsHeight;
var bgCnvsWidth;
var bgCnvsCtx;
var pressure;


var fancyObjects = []

function generateFancyObjects(){
	for( var i = 0; i < 50; i++ ){
		var opacity = Math.random() * 0.5;
		fancyObjects.push( { born: false, x: Math.random() * bgCnvsWidth, y: Math.random() * bgCnvsHeight, size: Math.random() * 150, currentOpacity:opacity, opacityMax: opacity, color: {r:Math.round(Math.random() * 150), g:Math.round(100), b:Math.round(Math.random() * 255)} } );
	}
}

function draw_fancy_background(){
	bgCnvsCtx.fillStyle = "rgb("+100*pressure+""+100+""+100+")";
	bgCnvsCtx.fillRect(0,0,bgCnvsWidth, bgCnvsHeight);

	for( fancyObjectID in fancyObjects ){
		var fancyObject = fancyObjects[ fancyObjectID ];

		if( fancyObject.currentOpacity <= 0 ){
			fancyObjects.splice( fancyObjectID, 1 );
			fancyObjects.push( { born: true, x: Math.random() * bgCnvsWidth, y: Math.random() * bgCnvsHeight, size: Math.random() * 150 , currentOpacity:0.0001, opacityMax: Math.random() * 0.5, color: {r:Math.round(Math.random() * 150 * pressure ), g:Math.round(100), b:Math.round(Math.random() * 255 / pressure )} } );
			continue;
		}

		bgCnvsCtx.fillStyle = "rgba("+ fancyObject.color.r+","+ fancyObject.color.g +","+ fancyObject.color.b + ","+ fancyObject.currentOpacity +")";
		bgCnvsCtx.fillRect( fancyObject.x, fancyObject.y, fancyObject.size, fancyObject.size );

		if( !fancyObject.born ){
			fancyObject.currentOpacity -= Math.random()*0.15*delta/1000;
		} else {
			fancyObject.currentOpacity += Math.random()*0.4*delta/1000;
			if( fancyObject.currentOpacity >= fancyObject.opacityMax ){
				fancyObject.born = false;
			}
		}
	}

	pressure = players[my_id].hp == 0? 10: players[ my_id ].maxHP / players[ my_id ].hp;
	bgm_sound.audio.volume = Math.min( 0.1 * pressure, 1);
}

window.addEventListener( "load", function(){

	//setup the background effects of page
	background_canvas = document.querySelector(".background-canvas"); 
	background_canvas.height = window.innerHeight;
	background_canvas.width = window.innerWidth;
	bgCnvsHeight = document.querySelector(".background-canvas").height;
	bgCnvsWidth = document.querySelector(".background-canvas").width;
	bgCnvsCtx = background_canvas.getContext("2d");
	generateFancyObjects();

	//populate selection list with classes
	var html_list = document.querySelector( ".perk-list");
	for( perk in perk_list ){
		var perk_selection = document.createElement("li");
		perk_selection.classList.add( "perk-selection" );
		perk_selection.setAttribute( "data-perk", perk_list[ perk ] );

		var perk_name = document.createElement("div");
		perk_name.classList.add( "perk-name" );
		perk_name.innerHTML = perk_stats_list[ perk_list[ perk ] ].name;

		var perk_icon = document.createElement("div");
		perk_icon.classList.add( "perk-icon" );
		perk_icon.style.setProperty( "background", "url('/assets/web/"+perk_list[ perk ]+".png')" );
		perk_icon.style.setProperty( "background-repeat", "no-repeat" );

		perk_selection.appendChild( perk_name );
		perk_selection.appendChild( perk_icon );
		html_list.appendChild( perk_selection );
	}

	var perk_selections = document.querySelectorAll( ".perk-selection" );

	//change info window on hover through the list
	for( var i = 0; i < perk_selections.length; i++ ){
		perk_selections[i].addEventListener( "mouseover", function(){
			var perk_name = this.getAttribute( "data-perk" );
			document.querySelector( ".perk-image" ).style.setProperty( "background", "url('/assets/web/"+perk_name+".png')" );
			document.querySelector( ".perk-image" ).style.setProperty( "background-repeat", "no-repeat" );
			document.querySelector( ".perk-image" ).style.setProperty( "background-position", "center" );
			document.querySelector( ".perk-stats" ).innerHTML = perk_stats_list[ perk_name ].stats;
		});
	}

	//choose character class
	for( var i = 0; i < perk_selections.length; i++ ){
		perk_selections[i].addEventListener( "click", function(){
			var perk_name = this.getAttribute( "data-perk" );
			selected_perk = perk_name;

			document.querySelector( ".perk-select" ).style.setProperty( "display", "none" );
		});
	}

});

