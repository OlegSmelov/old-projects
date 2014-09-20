using UnityEngine;
using System.Collections;

public class MoveBall : MonoBehaviour {
	
	public float moveCoefficient = 100f;
	public float rotateCoefficient = 7f;
	public float defaultDrag = 0f;
	public float brakeDrag = 10f;
	public float maxAngularVelocity = 25f;
	
	private Vector3 moveDirection = Vector3.right;
	private float score = 0f;
	private int collisions = 0;
	private bool brake = false;

	private GameObject gameStateObject;
	private GameState gameState;

	private bool useAccelerometer = true;

	float ApplyInputCurve(float value) {
		if (value > 0f)
			return value * value;
		else
			return -value * value;
	}

	float ApplyInputOffset(float value) {
		value += 0.75f;
		if (value > 1f)
			value -= 1f;
		return value;
	}

	float ApplyInputCoefficient(float value) {
		value *= 2f;
		if (value > 1f)
			value = 1f;
		else if (value < -1f)
			value = -1f;
		return value;
	}
	
	void OnCollisionEnter() {
		collisions++;
	}
	
	void OnCollisionExit() {
		if (collisions > 0)
			collisions--;
	}
	
	void Start() {
		Utilities.FindGameStateObject(out gameStateObject, out gameState);

		rigidbody.drag = defaultDrag;
		rigidbody.maxAngularVelocity = maxAngularVelocity;
	}
	
	void FixedUpdate () {
		if (collisions > 0) {
			float verticalAxis = ApplyInputCurve(useAccelerometer ? ApplyInputCoefficient(ApplyInputOffset(Input.acceleration.y)) : Input.GetAxis("Vertical"));
			if (verticalAxis > 0f) {
				rigidbody.AddTorque(moveCoefficient * Vector3.Cross(Vector3.up, moveDirection) * verticalAxis, ForceMode.Acceleration);
			}
				//rigidbody.AddForce(moveCoefficient * moveDirection);
			//if (Input.GetKey(KeyCode.S))
			//	rigidbody.AddForce((-1f) * moveCoefficient * moveDirection);
		}
	}
	
	void Update() {
		if (transform.position.y < 5f) {
			OnDie();
			//Respawn();
		}

		/*GUIText guiText = GameObject.Find("Message label").guiText;
		if (guiText != null) {
			guiText.text = string.Format("{0}\n{1}", Input.acceleration.y, ApplyInputCoefficient(ApplyInputOffset(Input.acceleration.y)));
		}*/

		//if (Input.GetKey(KeyCode.D)) {
		float horizontalAxis = ApplyInputCurve(useAccelerometer ? ApplyInputCoefficient(Input.acceleration.x) : Input.GetAxis("Horizontal"));
		moveDirection -= Vector3.Cross(moveDirection, Vector3.up) * rotateCoefficient * Time.deltaTime * horizontalAxis;
		moveDirection.Normalize();
		//}
		//if (Input.GetKey(KeyCode.A)) {
		//	moveDirection += Vector3.Cross(moveDirection, Vector3.up) * rotateCoefficient * Time.deltaTime;
		//	moveDirection.Normalize();
		//}
		float verticalAxis = ApplyInputCurve(useAccelerometer ? ApplyInputCoefficient(ApplyInputOffset(Input.acceleration.y)) : Input.GetAxis("Vertical"));
		brake = verticalAxis < 0f;
		
		if (brake && (collisions > 0))
			rigidbody.drag = brakeDrag;
		else
			rigidbody.drag = defaultDrag;
		
		Camera.main.transform.position = transform.position - 5 * moveDirection + 3 * Vector3.up;
		Camera.main.transform.LookAt(transform);
	}
	
/*	private void Respawn() {
		transform.position = respawnPoint.position;
		transform.rotation = respawnPoint.rotation;
		rigidbody.angularVelocity = new Vector3(0f, 0f, 0f);
		rigidbody.velocity = new Vector3(0f, 0f, 0f);
	} */
	
	private void OnCoinCollected() {
		gameState.CurrentScore += 10f;
	}
	
	private void OnDie() {
		gameState.LoadGameOver();
	}

	private void OnFinish() {
		gameState.LoadNextLevel();
	}
}
