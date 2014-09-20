using UnityEngine;
using System.Collections;

public class GameState : MonoBehaviour {
	
	private GUIText livesLabel, scoreLabel, timerLabel;

	public int defaultLivesLeft = 4;

	public int   LivesLeft    { get; set; }
	public float CurrentScore { get; set; }
	public float TimeLeft     { get; set; }
	public bool  IsTimerOn	  { get; set; }

	public int LastLevel { get; set; }

	private static bool created = false;
	private static bool levelWon = false;

	private GUIText loadingGUIText;

	public void Start() {
		Screen.sleepTimeout = SleepTimeout.NeverSleep;
	}
	
	public GameState() {
		LivesLeft = defaultLivesLeft;
		LastLevel = 0;
		CurrentScore = 0.0f;
		TimeLeft = 0.0f;
		IsTimerOn = false;
	}

	private void CreateGUITexture() {
		GameObject guiTextureObject = new GameObject();
		guiTextureObject.transform.position = new Vector3(0.5f, 0.5f, 0.0f);
		
		loadingGUIText = guiTextureObject.AddComponent<GUIText>();
		loadingGUIText.enabled = false;
		loadingGUIText.fontSize = (int)(.15f * Screen.height);
		loadingGUIText.anchor = TextAnchor.MiddleCenter;
		loadingGUIText.text = "Loading...";
	}

	public void ResetLivesLeft() {
		LivesLeft = defaultLivesLeft;
	}

	private void FindGUIText(out GUIText variable, string gameObjectName, string errorMessage) {
		GameObject gameObject = GameObject.Find (gameObjectName);
		if (gameObject) {
			variable = gameObject.guiText;
			variable.fontSize = (int)(Screen.height * 0.10f);
		} else {
			variable = null;
			Debug.Log (errorMessage, gameObject);
		}
	}

	private void UpdateGameObjects() {
		FindGUIText(out livesLabel, "LivesLabel", "Lives label not found");
		FindGUIText(out scoreLabel, "ScoreLabel", "Score label not found");
		FindGUIText(out timerLabel, "TimerLabel", "Timer label not found");
	}

	private void UpdateLivesLabel() {
		if (livesLabel)
			livesLabel.text = "Lives left: " + LivesLeft;
	}

	private void UpdateScoreLabel() {
		if (scoreLabel)
			scoreLabel.text = string.Format("Score: {0:0.0}", CurrentScore);
	}

	private void UpdateTimerLabel() {
		if (scoreLabel)
			timerLabel.text = string.Format("Time left: {0:0.0}s", TimeLeft);
	}

	void Update() {
		//UpdateLivesLabel();

		if (IsTimerOn) {
			TimeLeft -= Time.deltaTime;
		}

		if (IsTimerOn && TimeLeft < 0f) {
			TimeOutEvent();
			IsTimerOn = false;
		} else {
			UpdateScoreLabel();
			UpdateTimerLabel();
		}
	}
	
	void Awake() {
		CreateGUITexture();

		if (!created) {
			DontDestroyOnLoad(this.gameObject);
			created = true;
		} else {
			// we want to have only one copy of this object, so we destroy
			// clones of this object
			Destroy(this.gameObject);
		}
	}
	
	void OnLevelWasLoaded(int level) {
		// 0 - main menu
		// 1 - game over screen
		// 2 onwards - game levels
	    if (level >= 2) {
			CreateGUITexture();

			if (!levelWon) {
	        	LivesLeft -= 1;
			}

			UpdateGameObjects();
	        UpdateLivesLabel();

			LastLevel = level;
	    } else {
			// disable game timer in menus and such
			IsTimerOn = false;
		}

		if (levelWon) {
			levelWon = false;
		}
	}

	// win, load next level
	public void LoadNextLevel() {
		levelWon = true;

		if (Application.loadedLevel < Application.levelCount - 1) {
			loadingGUIText.enabled = true;
			Application.LoadLevel(Application.loadedLevel + 1);
		}
		else
			LoadMainMenu();
	}

	public void LoadGameOver() {
		loadingGUIText.enabled = true;
		Application.LoadLevel("GameOver");
	}

	public void LoadMainMenu() {
		loadingGUIText.enabled = true;
		Application.LoadLevel("Menu");
	}

	// Events
	private void TimeOutEvent() {
		LoadGameOver();
	}
}
