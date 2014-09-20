using UnityEngine;
using System.Collections;

public class PauseScreen : MonoBehaviour {
	private bool paused = false;
	private float savedTimeScale;

	private GameObject gameStateObject;
	private GameState gameState;

	void Start () {
		Utilities.FindGameStateObject(out gameStateObject, out gameState);
	}

	private void Pause() {
		if (!paused) {
			savedTimeScale = Time.timeScale;
			Time.timeScale = 0.0f;
			paused = true;
		}
	}

	private void Unpause() {
		if (paused) {
			Time.timeScale = savedTimeScale;
			paused = false;
		}
	}

	void Update () {
		if (Input.GetButtonDown("Cancel")) {
			if (paused) {
				Unpause();
			} else {
				Pause();
			}
		}

		if (paused && (Input.GetButtonDown ("Quit") || Input.GetMouseButtonDown (0))) {
			Unpause();
			gameState.LoadMainMenu();
		}
	}

	void OnGUI() {
		if (paused) {
			GUIStyle guiStyle = new GUIStyle(GUI.skin.button);
			guiStyle.fontSize = Screen.height / 10;
			GUI.Label(new Rect(0, 0, Screen.width, Screen.height),
			          "Paused, press Q or tap\nthe screen to quit", guiStyle);
		}
	}
}
