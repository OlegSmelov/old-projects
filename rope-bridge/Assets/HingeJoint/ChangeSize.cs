using UnityEngine;
using System.Collections;

public class ChangeSize : MonoBehaviour {
	
	public Transform bodyA, bodyB;

	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		Vector3 pointA = bodyA.collider.ClosestPointOnBounds(transform.position);
		Vector3 pointB = bodyB.collider.ClosestPointOnBounds(transform.position);
		transform.localScale = new Vector3(2f * (Vector3.Distance(transform.position, pointA) +
			Vector3.Distance(transform.position, pointB)), transform.localScale.y,
			transform.localScale.z);
		transform.
		transform.position = (pointA + pointB) / 2;
	}
}
