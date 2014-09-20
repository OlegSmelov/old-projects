using UnityEngine;
using System.Collections;

public class ExplodeOnCollision : MonoBehaviour {
	public GameObject explosionObject;

	void OnTriggerEnter(Collider collider) {
		if (collider.gameObject.name == "Player") {
			Instantiate(explosionObject, transform.position, transform.rotation);
			Destroy(gameObject);
		}
	}
}
