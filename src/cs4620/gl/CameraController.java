package cs4620.gl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cs4620.common.Scene;
import cs4620.common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector3;

public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	protected final RenderEnvironment rEnv;
	
	protected boolean mouseCentered = false;
	protected int centerMouseX = 400;
	protected int centerMouseY = 300;
	protected int minMove = 1;
	protected int maxMove = 200;
	
	protected boolean orbitMode = false;
	
	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
	}
	
	/**
	 * Update the camera's transformation matrix in response to user input.
	 * 
	 * Pairs of keys are available to translate the camera in three direction oriented to the camera,
	 * and to rotate around three axes oriented to the camera.  Mouse input can also be used to rotate 
	 * the camera around the horizontal and vertical axes.  All effects of these controls are achieved
	 * by altering the transformation stored in the SceneCamera that is referenced by the RenderCamera
	 * this controller is associated with.
	 * 
	 * @param et  time elapsed since previous frame
	 */
	public void update(double et) {		
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { motion.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { motion.add(0, 1, 0); }

		if(Keyboard.isKeyDown(Keyboard.KEY_E)) { rotation.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) { rotation.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotation.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotation.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotation.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotation.add(0, 1, 0); }
//		
//		if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
//		if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 

		if (!mouseCentered) {
			Mouse.setCursorPosition(centerMouseX,centerMouseY);
			mouseCentered = true;
		}
		int moveX = Mouse.getX() - centerMouseX, moveY = (Mouse.getY() - centerMouseY);
		int move = Math.abs(moveX) > Math.abs(moveY) ? 0 : 1; 	// 0 is X, 1 is Y
		switch (move) {
		case 0:
			if (Math.abs(moveX) < maxMove && Math.abs(moveX) > minMove) {
//				if (moveX > 0) {
//						rotation.add(0, -1, 0);
//				} else {
//						rotation.add(0, 1, 0);
//				}
				int sign = moveX / Math.abs(moveX);
				for (int i = 0; i < Math.abs(moveY); i++) {
					rotation.add(0,-1f * sign, 0);
				}
				Mouse.setCursorPosition(centerMouseX,centerMouseY);
			}
			break;
		case 1:
			if (Math.abs(moveY) < maxMove && Math.abs(moveY) > minMove) {
//				if (moveY > 0) {
//						rotation.add(1, 0, 0);
//				} else {
//						rotation.add(-1, 0, 0);
//				}
				int sign = moveY / Math.abs(moveY);
				for (int i = 0; i < Math.abs(moveY); i++) {
					rotation.add(.5f * sign, 0, 0);
				}
				Mouse.setCursorPosition(centerMouseX,centerMouseY);
			}
			break;
		}
		
		RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
		Matrix4 pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		if(motion.lenSq() > 0.01) {
			motion.normalize();
			motion.mul(5 * (float)et);
			translate(pMat, camera.sceneObject.transformation, motion);
		}
		if(rotation.lenSq() > 0.01) {
			rotation.mul((float)(100.0 * et));
			rotate(pMat, camera.sceneObject.transformation, rotation);
		}
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
	}

	/**
	 * Apply a rotation to the camera.
	 * 
	 * Rotate the camera about one ore more of its local axes, by modifying <b>transformation</b>.  The 
	 * camera is rotated by rotation.x about its horizontal axis, by rotation.y about its vertical axis, 
	 * and by rotation.z around its view direction.  The rotation is about the camera's viewpoint, if 
	 * this.orbitMode is false, or about the world origin, if this.orbitMode is true.
	 * 
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param rotation  The rotation in degrees, as Euler angles (rotation angles about x, y, z axes)
	 */
	protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
		// TODO#A3 SOLUTION START
		
		rotation = (rotation).mul((float)(Math.PI / 180.0));
		//put bounds on how far the camera rotates
//		Matrix4 mRot =  new Matrix4();
//		if (Math.abs(mainDirection.normalize().y - camera.mViewProjection.getZ().normalize().y) <= .5){
//		mRot = Matrix4.createRotationX(rotation.x);
//		System.out.println(mainDirection.normalize() +" "+ camera.mViewProjection.getZ().normalize());
//		}
		Matrix4 mRotx = new Matrix4();
		mRotx = Matrix4.createRotationX(rotation.x);
		Matrix4 mRoty = Matrix4.createRotationY(rotation.y);
		mRoty.mulBefore(Matrix4.createTranslation(camera.mWorldTransform.getTrans().negate()));
		mRoty.mulAfter(Matrix4.createTranslation(camera.mWorldTransform.getTrans()));
		//mRot.mulAfter(Matrix4.createRotationZ(rotation.z));
//		if (orbitMode) {
//			Vector3 rotCenter = new Vector3(0,0,0);
//			transformation.clone().invert().mulPos(rotCenter);
//			parentWorld.clone().invert().mulPos(rotCenter);
//			mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
//			mRot.mulAfter(Matrix4.createTranslation(rotCenter));
//		}
		
		transformation.mulBefore(mRotx);
		
		transformation.mulAfter(mRoty);
		
		// SOLUTION END
	}
	
	/**
	 * Apply a translation to the camera.
	 * 
	 * Translate the camera by an offset measured in camera space, by modifying <b>transformation</b>.
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param motion  The translation in camera-space units
	 */
	protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
		// TODO#A3 SOLUTION START

		Matrix4 mTrans = Matrix4.createTranslation(motion);
		
		transformation.mulBefore(mTrans);
		
		// SOLUTION END
	}
}
