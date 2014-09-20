using UnityEngine;
using System.Collections;

public class GameOver : MonoBehaviour {
	
	private delegate void MenuGameOver();
	private MenuGameOver menuFunction;

	private GameObject gameStateObject;
	private GameState gameState;
	private bool showContinueButton;

	private float screenHeight, screenWidth;
	private float buttonHeight, buttonWidth;

	private int levelToLoadQueueInt = -1;
	private string levelToLoadQueueStr = null;
	private bool loadingScreenDrawn = false;

	public Texture backgroundTexture;

	private void ContinueGame() {
		levelToLoadQueueInt = gameState.LastLevel;
		loadingScreenDrawn = false;
	}

	private void EndGame() {
		levelToLoadQueueStr = "Menu";
		loadingScreenDrawn = false;
	}

	void Update() {
		if (showContinueButton && Input.GetButtonDown("Confirm")) {
			ContinueGame();
		}
		if (Input.GetButtonDown("Cancel")) {
			EndGame();
		}
	}
	
	void Start() {
		screenHeight = Screen.height;
		screenWidth = Screen.width;
		
		buttonHeight = screenHeight * 0.15f;
		buttonWidth = screenWidth * 0.4f;
		menuFunction = mainMenu;

		Utilities.FindGameStateObject(out gameStateObject, out gameState);
		// show continue button only if there are lives left and the last level wasn't the menu
		showContinueButton = gameState != null && gameState.LivesLeft > 0 && gameState.LastLevel != 0;
	}
	
	void OnGUI() {
		if (levelToLoadQueueStr != null || levelToLoadQueueInt != -1 || loadingScreenDrawn) {
			GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), backgroundTexture, ScaleMode.ScaleAndCrop);

			GUIStyle guiLabelStyle = new GUIStyle(GUI.skin.label);
			guiLabelStyle.alignment = TextAnchor.MiddleCenter;
			guiLabelStyle.fontSize = (int) buttonHeight;
			GUI.Label(new Rect(0,0, screenWidth, screenHeight), "Loading", guiLabelStyle);
			
			if (loadingScreenDrawn) {
				if (levelToLoadQueueStr != null) {
					Application.LoadLevel(levelToLoadQueueStr);
					levelToLoadQueueStr = null;
				}
				if (levelToLoadQueueInt != -1) {
					Application.LoadLevel(levelToLoadQueueInt);
					levelToLoadQueueInt = -1;
				}
			}
			
			loadingScreenDrawn = true;
		} else {
			menuFunction();
		}
	}
	
	void mainMenu() {
		GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), backgroundTexture, ScaleMode.ScaleAndCrop);

		GUIStyle guiLabelStyle = new GUIStyle(GUI.skin.label);
		guiLabelStyle.alignment = TextAnchor.MiddleCenter;
		guiLabelStyle.fontSize = (int) buttonHeight;
		GUI.Label(new Rect(0,0, screenWidth, screenHeight * 0.5f), "Game Over", guiLabelStyle);

		GUIStyle guiStyle = new GUIStyle(GUI.skin.button);
		guiStyle.fontSize = (int) (buttonHeight / 3);

		if (showContinueButton) {
			if (GUI.Button(new Rect((screenWidth - buttonWidth) * 0.5f, screenHeight * 0.5f,
			    buttonWidth, buttonHeight), string.Format("Continue ({0} remaining)", gameState.LivesLeft),
			    guiStyle)) {
				ContinueGame();
			}
		}
		
		if (GUI.Button(new Rect((screenWidth - buttonWidth) * 0.5f, screenHeight * 0.7f,
		    buttonWidth, buttonHeight), "Main menu", guiStyle)) {
			EndGame();
		}
	}
}
