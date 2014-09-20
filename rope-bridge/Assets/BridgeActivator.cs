using UnityEngine;
using System.Collections;

public class BridgeActivator : MonoBehaviour {

	public Rigidbody[] Rigidbodies { get; set; }

	private void UpdateState(bool isKinematic) {
		foreach (Rigidbody rigidbody in Rigidbodies) {
			rigidbody.isKinematic = isKinematic;

			if (!isKinematic) {
				/* This part of the code looks stupid, I know. It just refreshes the joint, so that
				 * physics starts working again. Let me know if you know a better way to do it */
				HingeJoint joint = rigidbody.GetComponent<HingeJoint>();
				if (joint != null) {
					joint.useSpring = joint.useSpring;
				}
			}
		}
	}

	public void OnTriggerEnter(Collider collider) {
		if (collider.gameObject.name == "Player") {
			UpdateState(false);
		}
	}

	public void OnTriggerExit(Collider collider) {
		if (collider.gameObject.name == "Player") {
			UpdateState(true);
		}
	}
}
