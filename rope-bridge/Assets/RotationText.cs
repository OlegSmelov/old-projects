using UnityEngine;
using System.Collections;

public class RotationText : MonoBehaviour {

	public Transform target;
	
	void LookAt(Transform target) {
		//Set the rotation of the destination
		Quaternion rotationA = Quaternion.LookRotation(target.transform.position - transform.position);		
	 
		//Set the rotation of the custom normal and up vectors. 
		//When using the default LookRotation function, this would be hard coded to the forward and up vector.
		Quaternion rotationB = Quaternion.LookRotation(Vector3.up, Vector3.forward);
	 
		//Calculate the rotation
		transform.rotation =  rotationA * Quaternion.Inverse(rotationB);
	}
	
	void Update () {
		LookAt(target);
	}
}
