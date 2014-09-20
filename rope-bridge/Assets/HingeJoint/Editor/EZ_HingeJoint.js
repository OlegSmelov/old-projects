
class EZ_HingeJoint extends EditorWindow {
	
	
	
	//var ConfigurableJointt : Rigidbody;
	var Anchor : Vector3;
	var Axis : Vector3;
	var UseSpring : boolean = false;
	var Spring : float = 0;
	var Damper : float = 0;
	var TargetPosition : float = 0;
	var UseMotor : boolean = false;
	var TargetVelocity : float = 0;
	var Force : float = 0;
	var FreeSpin : boolean = false;
	var UseLimits : boolean = false;
	var min : float = 0;
	var max : float = 0;
	var minBounce : float = 0;
	var maxBounce : float = 0;
	var breakForce : float = Mathf.Infinity;
	var breakTorque : float = Mathf.Infinity;
	/////////////////////////////
	var distance : float = 0.5;
	var radius : float = 0.5;
	var hit : RaycastHit;
	var findery : GameObject;
	var ColiAll : Collider[];
	var alldirection : boolean = false;
	
	
	////////////////////////////////////////////////////
	@MenuItem("Window/EZ_HingeJoint")
	static function Init() {
		var window = GetWindow(EZ_HingeJoint);
		window.Show();
	}
	////////////////////////////////////
	function OnGUI() {
		
		alldirection = EditorGUILayout.Toggle("All Direction",alldirection);
		if(alldirection)
		{
			radius = EditorGUILayout.FloatField("Radius ",radius );
		}
		else
		{
			distance = EditorGUILayout.FloatField("Distance",distance);
			if(!findery)
			{
				findery = GameObject.Find("ArrayVector3");
				if(!findery)
				{
					Debug.Log("not find GameObject, put ArrayVector3 GameObject in Hierarchy window");
				}
			}
			
		}
		
		Anchor = EditorGUILayout.Vector3Field("Anchor",Anchor);
		Axis = EditorGUILayout.Vector3Field("Axis",Axis);
		UseSpring = EditorGUILayout.Toggle("Use Spring",UseSpring);
		if(UseSpring)
		{
			Spring = EditorGUILayout.FloatField("Spring",Spring);
			Damper = EditorGUILayout.FloatField("Damper",Damper);
			TargetPosition = EditorGUILayout.FloatField("Target Position",TargetPosition);
		}
		UseMotor = EditorGUILayout.Toggle("Use Motor",UseMotor);
		if(UseMotor)
		{
			TargetVelocity = EditorGUILayout.FloatField("Target Velocity",TargetVelocity);
			Force = EditorGUILayout.FloatField("Force",Force);
			FreeSpin = EditorGUILayout.Toggle("Free Spin",FreeSpin);
			
		}
		UseLimits = EditorGUILayout.Toggle("Use Limits",UseLimits);
		if(UseLimits)
		{
			min = EditorGUILayout.FloatField("min",min);
			max = EditorGUILayout.FloatField("max",max);
			minBounce = EditorGUILayout.FloatField("min Bounce",minBounce);
			maxBounce = EditorGUILayout.FloatField("max Bounce",maxBounce);
		}
		if(GUILayout.Button("Infinity",GUILayout.Width(80)))
		{
			breakForce = Mathf.Infinity;
			breakTorque = Mathf.Infinity;
		}
		breakForce = EditorGUILayout.FloatField("Break Force",breakForce);
		breakTorque = EditorGUILayout.FloatField("Break Torque",breakTorque);
		
		if(GUILayout.Button("OK"))
		{
			var activeGOs : Object[] = Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep);
			if(activeGOs.length == 0)
			{
				EditorUtility.DisplayDialog("Select GameObject","Select GameObject to Add the HingeJoint","OK");
			}
			
			for (var obj : GameObject in Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep)) {
				var ss : int = 1;
				for(var xx : int = 0;xx<ss;xx++)
				{
					var componentt = obj.GetComponent("HingeJoint");
					if(componentt)
					{
						ss++;
						DestroyImmediate(componentt);
					}
				}
				if (!componentt) {
					if(!alldirection)
					{
						for(var dero : Vector3 in findery.GetComponent(ArrayVector3).ArrayVector3)
						{
							if(Physics.Raycast(obj.transform.position, dero , hit,distance))
							{
								if(hit.collider.gameObject)
								{
									if(!obj.GetComponent("Rigidbody"))
									{
										obj.AddComponent("Rigidbody");
									}
									var addy = obj.AddComponent("HingeJoint");
									if(!hit.collider.gameObject.GetComponent("Rigidbody"))
									{
										hit.collider.gameObject.AddComponent("Rigidbody");
									}
									addy.connectedBody = hit.collider.gameObject.rigidbody;
									addy.anchor = Anchor;
									addy.axis = Axis;
									addy.useSpring = UseSpring;
									addy.spring.spring = Spring;
									addy.spring.damper  = Damper;
									addy.spring.targetPosition = TargetPosition;
									addy.useMotor = UseMotor;
									addy.motor.force = Force;
									addy.motor.targetVelocity  = TargetVelocity;
									addy.motor.freeSpin = FreeSpin;
									addy.useLimits = UseLimits;
									addy.limits.min = min;
									addy.limits.minBounce = minBounce;
									addy.limits.max = max;
									addy.limits.maxBounce = maxBounce;
									addy.breakForce = breakForce;
									addy.breakTorque = breakTorque;
								}
							}
						}
					}
					else
					{
						ColiAll = Physics.OverlapSphere(obj.transform.position, radius);
						Debug.Log(ColiAll.length-1);
						
						if(ColiAll.length > 1)
						{
							for(var colld : Collider in ColiAll)
							{
								if(colld.transform != obj.transform)
								{
									if(colld)
									{
										
										if(!obj.GetComponent("Rigidbody"))
										{
											obj.AddComponent("Rigidbody");
										}
										var addyy = obj.AddComponent("HingeJoint");
										if(!colld.gameObject.GetComponent("Rigidbody"))
										{
											colld.gameObject.AddComponent("Rigidbody");
										}
										addyy.connectedBody = colld.gameObject.rigidbody;
										addyy.anchor = Anchor;
										addyy.axis = Axis;
										addyy.useSpring = UseSpring;
										addyy.spring.spring = Spring;
										addyy.spring.damper  = Damper;
										addyy.spring.targetPosition = TargetPosition;
										addyy.useMotor = UseMotor;
										addyy.motor.force = Force;
										addyy.motor.targetVelocity  = TargetVelocity;
										addyy.motor.freeSpin = FreeSpin;
										addyy.useLimits = UseLimits;
										addyy.limits.min = min;
										addyy.limits.minBounce = minBounce;
										addyy.limits.max = max;
										addyy.limits.maxBounce = maxBounce;
										addyy.breakForce = breakForce;
										addyy.breakTorque = breakTorque;
									}
								}
							}
						}
						
					}
				}
			}
		}
		if(GUILayout.Button("only change setting"))
		{
			var activeGOss : Object[] = Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep);
			if(activeGOss.length == 0)
			{
				EditorUtility.DisplayDialog("Select GameObject","Select GameObject to change the HingeJoint setting","OK");
			}
			
			for (var obj : GameObject in Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep)) {
				
				var addy2 = obj.GetComponent(HingeJoint);
				if (addy2) {
					
					var objs : Component[]  = obj.GetComponents(HingeJoint);
					var goodObjs : Array = new Array();
					for (var i : int = 0; i < objs.Length; i++) {
						com = objs[i];
						if (com != null) {
							goodObjs.Add(com);
						}
					}
					var ran : int = 0;
					for (var ii : int = 0; ii < objs.Length; ii++)
					{
						objs[ran].anchor = Anchor;
						objs[ran].axis = Axis;
						objs[ran].useSpring = UseSpring;
						objs[ran].spring.spring = Spring;
						objs[ran].spring.damper  = Damper;
						objs[ran].spring.targetPosition = TargetPosition;
						objs[ran].useMotor = UseMotor;
						objs[ran].motor.force = Force;
						objs[ran].motor.targetVelocity  = TargetVelocity;
						objs[ran].motor.freeSpin = FreeSpin;
						objs[ran].useLimits = UseLimits;
						objs[ran].limits.min = min;
						objs[ran].limits.minBounce = minBounce;
						objs[ran].limits.max = max;
						objs[ran].limits.maxBounce = maxBounce;
						objs[ran].breakForce = breakForce;
						objs[ran].breakTorque = breakTorque;
						ran++;
					}
					
					
				}
				else
				{
					EditorUtility.DisplayDialog("no HingeJoint","there is no HingeJoint in this GameObject","OK");
				}
			}
			
		}
		
		if(GUILayout.Button("Copy information from GameObject"))
		{
			var activeGOs2 : Object[] = Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep);
			if(activeGOs2.length > 1)
			{
				EditorUtility.DisplayDialog("Error","Please Select one GameObject Only, to copy information","OK");
			}
			
			for (var obj2 : GameObject in Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep))
			{
				var getinfo = obj2.GetComponent("HingeJoint");
				if(getinfo && activeGOs2.length == 1)
				{
					Anchor = getinfo.anchor;
					Axis = getinfo.axis;
					UseSpring = getinfo.useSpring ;
					Spring = getinfo.spring.spring ;
					Damper = getinfo.spring.damper;
					TargetPosition = getinfo.spring.targetPosition;
					UseMotor = getinfo.useMotor;
					Force = getinfo.motor.force;
					TargetVelocity = getinfo.motor.targetVelocity;
					FreeSpin = getinfo.motor.freeSpin;
					UseLimits = getinfo.useLimits;
					min = getinfo.limits.min;
					minBounce = getinfo.limits.minBounce;
					max = getinfo.limits.max;
					maxBounce = getinfo.limits.maxBounce;
					breakForce = getinfo.breakForce;
					breakTorque = getinfo.breakTorque;
				}
				else if(activeGOs2.length == 1)
				{
					EditorUtility.DisplayDialog("Error","There is no information in this GameObject","OK");
				}
			}
		}
		if(GUILayout.Button("Remove All HingeJoint"))
		{
			for (var obj2 : GameObject in Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep))
			{
				var ss2 : int = 1;
				for(var xx2 : int = 0; xx2 < ss2 ;xx2++)
				{
					var componentt2 = obj2.GetComponent("HingeJoint");
					if(componentt2)
					{
						ss2++;
						DestroyImmediate(componentt2);
					}
					
				}
				
			}
		}
		if(GUILayout.Button("Remove All Rigidbody"))
		{
			for (var obj3 : GameObject in Selection.GetFiltered(GameObject,SelectionMode.Editable | SelectionMode.Deep))
			{
				var componentt3 = obj3.GetComponent("Rigidbody");
				if(componentt3)
				{
					DestroyImmediate(componentt3);
				}
			}
		}
		
		EditorGUILayout.SelectableLabel("by FirasDeep");
	}
	
}
