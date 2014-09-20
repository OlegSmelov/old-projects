using UnityEngine;
using System.Collections;


public class PowerUpDown : MonoBehaviour {

	public Rigidbody player;
	public float deltaMass;
	public GUIText messageLabel;

	void OnTriggerEnter(Collider collider) {
		if (collider.gameObject == player.gameObject) {
			Debug.Log(deltaMass);
			player.rigidbody.mass += deltaMass;
			Debug.Log(player.rigidbody.mass);
			Destroy(gameObject);
			
			string message = "";
			if (deltaMass >= 0f)
				message = "+";
			message += deltaMass.ToString() + " kg";
			
			messageLabel.BroadcastMessage("UpdateText", message);
		}
	}	
}
