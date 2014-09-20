using UnityEngine;
using System.Collections;

public class DelegateMenu : MonoBehaviour {
	
	private delegate void MenuDelegate();
	private MenuDelegate menuFunction;

	private GameObject gameStateObject;
	private GameState gameState;
	
	private float screenHeight, screenWidth;
	private float buttonHeight, buttonWidth;

	private string levelToLoadQueue = null;
	private bool loadingScreenDrawn = false;

	public Texture bgTexture;
	private void LoadGame() {
		if (gameState) {
			gameState.ResetLivesLeft();
			gameState.CurrentScore = 0f;
		}
		levelToLoadQueue = "Bridge";
	}

	private void ExitGame() {
		Application.Quit();
	}

	void Update() {
		if (Input.GetButtonDown("Confirm")) {
			LoadGame();
		}
		if (Input.GetButtonDown("Cancel")) {
			ExitGame();
		}
	}
	
	void Start() {
		screenHeight = Screen.height;
		screenWidth = Screen.width;
		
		buttonHeight = screenHeight * 0.15f;
		buttonWidth = screenWidth * 0.4f;
		menuFunction = mainMenu;

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
	
	void OnGUI() {
		if (levelToLoadQueue != null || loadingScreenDrawn) {
			GUI.DrawTexture(new Rect(0, 0, screenWidth, screenHeight), bgTexture, ScaleMode.ScaleAndCrop);
			GUIStyle guiLabelStyle = new GUIStyle(GUI.skin.label);
			guiLabelStyle.alignment = TextAnchor.MiddleCenter;
			guiLabelStyle.fontSize = (int) buttonHeight;
			GUI.Label(new Rect(0,0, screenWidth, screenHeight), "Loading", guiLabelStyle);

			if (loadingScreenDrawn) {
				Application.LoadLevel(levelToLoadQueue);
				levelToLoadQueue = null;
			}

			loadingScreenDrawn = true;
		} else if (!loadingScreenDrawn) {
			menuFunction();
		}
	}

	void mainMenu() {
		screenWidth = Screen.width;
		screenHeight = Screen.height;

		GUI.DrawTexture(new Rect(0, 0, screenWidth, screenHeight), bgTexture, ScaleMode.ScaleAndCrop);
		GUIStyle guiLabelStyle = new GUIStyle(GUI.skin.label);
		guiLabelStyle.alignment = TextAnchor.MiddleCenter;
		guiLabelStyle.fontSize = (int) buttonHeight;
		GUI.Label(new Rect(0,0, screenWidth, screenHeight * 0.5f), "Bridge", guiLabelStyle);


		GUIStyle guiStyle = new GUIStyle(GUI.skin.button);
		guiStyle.fontSize = (int) (buttonHeight / 3);
		if (GUI.Button(new Rect((screenWidth - buttonWidth) * 0.5f, screenHeight * 0.5f,
		    buttonWidth, buttonHeight), "Start", guiStyle)) {
			LoadGame();
		}
		
		if (GUI.Button(new Rect((screenWidth - buttonWidth) * 0.5f, screenHeight * 0.7f,
		    buttonWidth, buttonHeight), "Quit", guiStyle)) {
			ExitGame();
		}
	}
}
