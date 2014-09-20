using UnityEngine;
using System.Collections;

public class LevelData : MonoBehaviour {

	public float scoreReward = 0f;
	public float timeLimit = 100f;

	private GameObject gameStateObject;
	private GameState gameState;

	void Start () {
		Utilities.FindGameStateObject(out gameStateObject, out gameState);

		gameState.CurrentScore += scoreReward;
		gameState.TimeLeft = timeLimit;
		gameState.IsTimerOn = timeLimit > 0f;
	}
}
