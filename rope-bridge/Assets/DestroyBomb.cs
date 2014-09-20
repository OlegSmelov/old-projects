using UnityEngine;
using System.Collections;

public class DestroyBomb : MonoBehaviour {
	public GameObject bomb;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	void OnCollisionEnter(Collision collision){
		if (collision.gameObject.name == "Player"){
			Destroy(bomb);
			Destroy(gameObject);
			
		}
	}
}
