using UnityEngine;
using System.Collections;

public class Utilities {

	public static void FindGameStateObject(out GameObject gameStateObject, out GameState gameState)
	{
		// default values
		gameStateObject = null;
		gameState = null;

		gameStateObject = GameObject.Find ("Game State Object");
		if (gameStateObject == null) {
			Debug.LogError ("Could not find game state object", gameStateObject);
			return;
		}
		
		gameState = (GameState) gameStateObject.GetComponent(typeof(GameState));
		if (gameState == null) {
			Debug.LogError ("Could not find lives left script", gameState);
			return;
		}
	}
}
