using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class GenerateBridge : MonoBehaviour {
	
	public Rigidbody boardPrefab, jointPrefab, pillarPrefab, ropePartPrefab;
	public float boardMass = 10f, jointMass = 1f;
	public float breakForce = 40f, lastJointBreakForce = 200f, ropeBreakForce = 40f;
	public int bridgeLength = 10;
	public int numberOfRopeParts = 3;
	public float bridgeAreaRadius = 10f;

	// bad programming, lol:
	private List<Rigidbody> rigidbodies = new List<Rigidbody>();

	private void createBridgeActiveArea(GameObject parent, Rigidbody[] rigidbodies) {
		GameObject activeArea = new GameObject();
		activeArea.transform.parent = parent.transform;
		activeArea.transform.localPosition = Vector3.zero;

		SphereCollider collider = activeArea.AddComponent<SphereCollider>();
		collider.isTrigger = true;
		collider.radius = bridgeAreaRadius;

		BridgeActivator activator = activeArea.AddComponent<BridgeActivator>();
		activator.Rigidbodies = rigidbodies;
	}
	
	private HingeJoint createHingeJoint(GameObject parent) {
		HingeJoint hingeJoint = parent.AddComponent(typeof(HingeJoint)) as HingeJoint;
		hingeJoint.anchor = new Vector3(0f, 0f, 0f);
		hingeJoint.axis = new Vector3(0f, 0f, 1f);
		hingeJoint.breakForce = breakForce;
		hingeJoint.useLimits = true;
		
		JointLimits jointLimits = new JointLimits();
		jointLimits.min = 0f;
		jointLimits.max = 0f;
		jointLimits.minBounce = 0f;
		jointLimits.maxBounce = 0f;
		
		hingeJoint.limits = jointLimits;
		
		return hingeJoint;
	}
	
	private HingeJoint updateHingeParameters(HingeJoint joint, int i) {
		if (i == 1 || i == bridgeLength - 1)
			joint.breakForce = lastJointBreakForce;
		return joint;
	}
	
	private Rigidbody createPillar(Rigidbody newBoard, bool left) {
		Rigidbody pillar = Instantiate(pillarPrefab) as Rigidbody;
				
		Vector3 tempPosition = newBoard.transform.position
			+ (pillar.transform.up * pillar.transform.localScale.y)
			- (newBoard.transform.up * newBoard.transform.localScale.y) / 2f;
		
		Vector3 moveVector = (newBoard.transform.forward * newBoard.transform.localScale.z) / 2f
			+ (pillar.transform.forward * pillar.transform.localScale.z) / 2f;
		
		if (left)
			tempPosition += moveVector;
		else
			tempPosition -= moveVector;

		pillar.transform.rotation = Quaternion.Euler(0f, transform.rotation.eulerAngles.y, 0f);
		pillar.transform.position = tempPosition;
		
		ConfigurableJoint pillarJoint = pillar.gameObject.AddComponent(typeof(ConfigurableJoint)) as ConfigurableJoint;
		
		pillarJoint.anchor = new Vector3(0f, -1f, 0f);
		pillarJoint.xMotion = ConfigurableJointMotion.Locked;
		pillarJoint.yMotion = ConfigurableJointMotion.Locked;
		pillarJoint.zMotion = ConfigurableJointMotion.Locked;
		
		pillarJoint.angularXMotion = ConfigurableJointMotion.Locked;
		pillarJoint.angularYMotion = ConfigurableJointMotion.Locked;
		//pillarJoint.angularZMotion = ConfigurableJointMotion.Locked;
		
		pillarJoint.swapBodies = true;
		
		pillarJoint.connectedBody = newBoard;
		
		return pillar;
	}
	
	void LookAtUp(Transform transform, Vector3 target) {
		//Set the rotation of the destination
		Quaternion rotationA = Quaternion.LookRotation(target - transform.position);		
	 
		//Set the rotation of the custom normal and up vectors. 
		//When using the default LookRotation function, this would be hard coded to the forward and up vector.
		Quaternion rotationB = Quaternion.LookRotation(Vector3.up, Vector3.forward);
	 
		//Calculate the rotation
		transform.rotation = rotationA * Quaternion.Inverse(rotationB);
	}
	
	void CreateRope(Rigidbody lastPillar, Rigidbody nextPillar) {
		JointLimits myLimits = new JointLimits();
		myLimits.min = -1f;
		myLimits.max = 1f;
		myLimits.maxBounce = 0f;
		myLimits.minBounce = 0f;
		
		Vector3 pointA = lastPillar.transform.position + ((lastPillar.transform.up * lastPillar.transform.localScale.y * 0.95f) / 1f)
			+ ((lastPillar.transform.right * lastPillar.transform.localScale.x) / 2f);
		
		Vector3 pointB = nextPillar.transform.position + ((nextPillar.transform.up * nextPillar.transform.localScale.y * 0.95f) / 1f)
			- ((nextPillar.transform.right * nextPillar.transform.localScale.x) / 2f);
		
		Vector3 nextPartVector = (pointB - pointA) / numberOfRopeParts;
		Vector3 startPoint = pointA;
		float partWidth = (nextPartVector.magnitude / 2f) * 1.3f;
		
		Rigidbody lastPart = null;
		for (int j = 0; j < numberOfRopeParts; j++) {
			Vector3 centerPoint = startPoint + (nextPartVector / 2f);
			
			Rigidbody newRope = Instantiate(ropePartPrefab) as Rigidbody;
			newRope.isKinematic = true;
			newRope.transform.position = centerPoint;

			rigidbodies.Add(newRope);
			
			float myScale = newRope.transform.localScale.y / partWidth;
			newRope.transform.localScale /= myScale;
			
			startPoint += nextPartVector;
			LookAtUp(newRope.transform, startPoint);
			
			HingeJoint ropePartJoint = createHingeJoint(newRope.gameObject);
			ropePartJoint.useLimits = true;
			ropePartJoint.limits = myLimits;
			ropePartJoint.axis = new Vector3(1f, 0f, 0f);
			ropePartJoint.anchor = new Vector3(0f, -1f, 0f);
			ropePartJoint.breakForce = ropeBreakForce;
			ropePartJoint.connectedBody = lastPart ?? lastPillar;
			
			lastPart = newRope;
		}
		
		HingeJoint lastRopePartJoint = createHingeJoint(lastPart.gameObject);
		lastRopePartJoint.useLimits = true;
		lastRopePartJoint.limits = myLimits;
		lastRopePartJoint.axis = new Vector3(1f, 0f, 0f);
		lastRopePartJoint.anchor = new Vector3(0f, 1f, 0f);
		lastRopePartJoint.breakForce = ropeBreakForce;
		lastRopePartJoint.connectedBody = nextPillar;
	}
	
	void Start () {
		rigidbody.isKinematic = true;
		
		GameObject lastBoard = gameObject;
		Rigidbody lastPillarLeft = null, lastPillarRight = null;

		// Activator stuff
		GameObject bridgeCenterBoard = null;
		rigidbodies.Clear();

		lastPillarLeft = createPillar(rigidbody, true);
		lastPillarRight = createPillar(rigidbody, false);

		lastPillarLeft.isKinematic = true;
		lastPillarRight.isKinematic = true;
		
		for (int i = 1; i < bridgeLength; i++) {
			Rigidbody joint1 = Instantiate (jointPrefab) as Rigidbody;
			Rigidbody joint2 = Instantiate (jointPrefab) as Rigidbody;
			Rigidbody newBoard = Instantiate (boardPrefab) as Rigidbody;

			newBoard.rigidbody.mass = boardMass;
			newBoard.isKinematic = true;
			newBoard.transform.position = lastBoard.transform.position
				+ lastBoard.transform.right * (joint1.transform.localScale.x + newBoard.transform.localScale.x);
			newBoard.transform.rotation = lastBoard.transform.rotation;
			
			joint1.rigidbody.mass = jointMass;
			joint1.isKinematic = true;
			joint1.transform.position = lastBoard.transform.position
				+ lastBoard.transform.right   * ((joint1.transform.localScale.x + newBoard.transform.localScale.x) / 2f)
				- lastBoard.transform.forward * ((joint1.transform.localScale.z + newBoard.transform.localScale.z) / 2f) * 0.7f;
			joint1.transform.rotation = lastBoard.transform.rotation;
			
			HingeJoint hingeJoint = updateHingeParameters(createHingeJoint(joint1.gameObject), i);
			hingeJoint.anchor = new Vector3(-0.5f, 0f, 0f);
			hingeJoint.connectedBody = lastBoard.rigidbody;
			
			hingeJoint = updateHingeParameters(createHingeJoint(joint1.gameObject), i);
			hingeJoint.anchor = new Vector3(0.5f, 0f, 0f);
			hingeJoint.connectedBody = newBoard;
			
			joint2.rigidbody.mass = jointMass;
			joint2.isKinematic = true;
			joint2.transform.position = lastBoard.transform.position
				+ transform.right   * ((joint1.transform.localScale.x + newBoard.transform.localScale.x) / 2f)
				+ transform.forward * ((joint1.transform.localScale.z + newBoard.transform.localScale.z) / 2f) * 0.7f;
			joint2.transform.rotation = lastBoard.transform.rotation;
			
			hingeJoint = updateHingeParameters(createHingeJoint(joint2.gameObject), i);
			hingeJoint.connectedBody = lastBoard.rigidbody;
			
			hingeJoint = updateHingeParameters(createHingeJoint(joint2.gameObject), i);
			hingeJoint.connectedBody = newBoard;
			
			hingeJoint = createHingeJoint(lastBoard);
			hingeJoint.connectedBody = newBoard;
			
			hingeJoint = createHingeJoint(newBoard.gameObject);
			hingeJoint.connectedBody = lastBoard.rigidbody;
			
			if (pillarPrefab != null && ropePartPrefab != null && i % 3 == 0) {
				Rigidbody leftPillar = createPillar(newBoard, true);
				Rigidbody rightPillar = createPillar(newBoard, false);
				
				if (lastPillarLeft != null)
					CreateRope(lastPillarLeft, leftPillar);
				if (lastPillarRight != null)
					CreateRope(lastPillarRight, rightPillar);

				leftPillar.isKinematic = true;
				rightPillar.isKinematic = true;

				if (lastPillarLeft != null)
					rigidbodies.Add(leftPillar);

				if (lastPillarRight != null)
					rigidbodies.Add(rightPillar);
				
				lastPillarLeft = leftPillar;
				lastPillarRight = rightPillar;
			}
			
			lastBoard = newBoard.gameObject;

			rigidbodies.Add(newBoard);
			rigidbodies.Add(joint1);
			rigidbodies.Add(joint2);

			if (i == bridgeLength / 2) {
				bridgeCenterBoard = newBoard.gameObject;
			}
		}
		
		if (lastPillarLeft != null)
			lastPillarLeft.isKinematic = true;
		if (lastPillarRight != null)
			lastPillarRight.isKinematic = true;

		lastBoard.rigidbody.isKinematic = true;

		rigidbodies.Remove(lastPillarLeft);
		rigidbodies.Remove(lastPillarRight);
		rigidbodies.Remove(lastBoard.rigidbody);
		createBridgeActiveArea(bridgeCenterBoard, rigidbodies.ToArray());
	}
}
