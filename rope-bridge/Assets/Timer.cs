using UnityEngine;
using System.Collections;

public class Timer : MonoBehaviour {
	
	public float levelTime = 30f;
	public GameObject player;
	public GUIText timerLabel;
	
	private float timeLeft;
	
	private void RestartTimer() {
		timeLeft = levelTime;
	}
	
	void Start() {
		RestartTimer();
	}
	
	void Update() {
		timeLeft -= Time.deltaTime;
		
		if (timeLeft < 0f) {
			player.BroadcastMessage("OnTimeout");
			RestartTimer();
		}
		
		timerLabel.text = "Time left: " + string.Format("{0:0.0}", timeLeft) + "s";
	}
}
