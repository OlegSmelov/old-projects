using UnityEngine;
using System.Collections;

public class Collisions : MonoBehaviour {
	public GameObject effectPrefab;
	public AudioClip coinPickupSound;
	public GameObject player;
	
	void OnTriggerEnter(Collider c) {
		if (c.gameObject.tag == "Coin") {
			Destroy(Instantiate(effectPrefab, c.gameObject.transform.position, new Quaternion(0, 0, 0, 0)), 10);
			Destroy(c.gameObject);
			
			player.BroadcastMessage("OnCoinCollected");
			audio.PlayOneShot(coinPickupSound);
		} else if (c.gameObject.tag == "Finish") {
			player.BroadcastMessage("OnFinish");
		}
	}
}
