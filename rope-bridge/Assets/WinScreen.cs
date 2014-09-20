using UnityEngine;
using System.Collections;

public class WinScreen : MonoBehaviour {

	public Texture backgroundTexture;

	void Update () {
		if (Input.GetButtonDown("Confirm") || Input.GetMouseButtonUp(0)) {
			Application.LoadLevel("Menu");
		}
	}

	void OnGUI() {
		GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height),
		                backgroundTexture, ScaleMode.ScaleToFit);

		GUIStyle guiStyle = new GUIStyle(GUI.skin.label);
		guiStyle.fontSize = (int) (Screen.height * .10f);
		guiStyle.alignment = TextAnchor.MiddleCenter;
		GUI.Label(new Rect(0, Screen.height * .7f, Screen.width, Screen.height * .3f),
		                   "Press enter or tap the screen", guiStyle);
	}
}
