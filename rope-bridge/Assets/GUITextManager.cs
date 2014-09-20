using UnityEngine;
using System.Collections;
using System.Threading;

public class GUITextManager : MonoBehaviour {
	
	public float defaultTimeout = 3f;
	
	private bool countdownActive = false;
	private float endTime = 0f;

	void Update() {
		if (countdownActive && endTime < Time.time) {
			guiText.text = "";
			countdownActive = false;
		}
	}
	
	void UpdateText(string text) {
		guiText.text = text;
		guiText.transform.position = new Vector3(.05f, .5f, 0f);
		guiText.anchor = TextAnchor.MiddleLeft;
		guiText.fontSize = (int)(Screen.height * 0.10f);

		
		endTime = Time.time + defaultTimeout;
		countdownActive = true;
	}
}
