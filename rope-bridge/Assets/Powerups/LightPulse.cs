using UnityEngine;
using System.Collections;

public class LightPulse : MonoBehaviour {
	
	public float speed = 40.0f;
	public float maxDistance = 30f;
	
	void Update() {
		light.range = Mathf.PingPong(Time.time * speed, maxDistance);
	}
}
