window.addEventListener( "load", function() {
	document.querySelector( ".mapCreator-submit-image" ).addEventListener( "click", function(){
		mapURL = document.querySelector( ".mapCreator-image-url" ).value;
		document.querySelector( ".mapCreator-image-select" ).style.setProperty("display","none");
		document.querySelector( ".mapCreator-export" ).style.setProperty("display","block");
		run();
	});

	document.querySelector( ".mapCreator-submit-preset" ).addEventListener( "click", function(){
		var value = document.querySelector( ".mapCreator-load-from-code" ).value;
		map = JSON.parse( value );
		document.querySelector( ".mapCreator-image-select" ).style.setProperty("display","none");
		document.querySelector( ".mapCreator-export" ).style.setProperty("display","block");
		run();
	});

});