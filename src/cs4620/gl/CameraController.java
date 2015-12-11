package cs4620.gl;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import cs4620.common.Projectile;
import cs4620.common.SceneLight;
import cs4620.splines.CatmullRom;
public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	protected final RenderEnvironment rEnv;

	protected boolean prevFrameButtonDown = false;
	protected int prevMouseX, prevMouseY;
	protected boolean mouseCentered = false;
	protected boolean hasClicked = false;
	protected int centerMouseX = 400;
	protected int centerMouseY = 300;
	protected int minMove = 1;
	protected int maxMove = 200;

	protected Projectile fling = new Projectile("Lamp1");

	protected boolean orbitMode = false;

	private float step = .1f;
	private float speed = 1;
	private int pathno = -1;
	private Vector2 start = new Vector2(8,7);
	private Vector2 pos = start;

	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
	}

	/**
	 * Update the camera's transformation matrix in response to user input.
	 * 
	 * Pairs of keys are available to translate the camera in three direction
	 * oriented to the camera, and to rotate around three axes oriented to the
	 * camera. Mouse input can also be used to rotate the camera around the
	 * horizontal and vertical axes. All effects of these controls are achieved
	 * by altering the transformation stored in the SceneCamera that is
	 * referenced by the RenderCamera this controller is associated with.
	 * 
	 * @param et
	 *            time elapsed since previous frame
	 */
	public void update(double et) {
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();

//		if (Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
//		if (Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }
//		if (Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(-1, 0, 0); }
//		if (Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(1, 0, 0); }

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotation.add(0.5f, 0, 0); }
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotation.add(-0.5f, 0, 0); }
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotation.add(0, -0.5f, 0); }
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotation.add(0, 0.5f, 0); }

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && pathno < 0) { pathno++; }
		else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { speed = 5; }
		else { speed = 1; }
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) { 
			fling.activate(camera.mWorldTransform.getZ().clone().normalize().negate());
		}

		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || 
				Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));

		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
		prevMouseX = thisMouseX;
		prevMouseY = thisMouseY;

//		if (!mouseCentered) {
//			Mouse.setCursorPosition(centerMouseX, centerMouseY);
//			mouseCentered = true;
//		} else {
//			int moveX = Mouse.getX() - centerMouseX, moveY = (Mouse.getY() - centerMouseY);
//			int move = Math.abs(moveX) > Math.abs(moveY) ? 0 : 1; // 0 is X, 1
//			// is Y
//			switch (move) {
//			case 0:
//				if (Math.abs(moveX) < maxMove && Math.abs(moveX) > minMove) {
//					// if (moveX > 0) {
//					// rotation.add(0, -1, 0);
//					// } else {
//					// rotation.add(0, 1, 0);
//				}
//				int sign = moveX / Math.abs(moveX);
//				for (int i = 0; i < Math.abs(moveY); i++) {
//					rotation.add(0,0.1f*-sign, 0);
//				}
//				//Mouse.setCursorPosition(centerMouseX, centerMouseY);
//				mouseCentered = false;
//			}
//			break;
//		case 1:
//			if (Math.abs(moveY) < maxMove && Math.abs(moveY) > minMove) {
//				// if (moveY > 0) {
//				// rotation.add(1, 0, 0);
//				// } else {
//				// rotation.add(-1, 0, 0);
//				// }
//				int sign = moveY / Math.abs(moveY);
//				for (int i = 0; i < Math.abs(moveY); i++) {
//					rotation.add(0.1f *sign, 0, 0);
//				}
//				//Mouse.setCursorPosition(centerMouseX, centerMouseY);
//				mouseCentered = false;
//			}
//			break;
//		}

		switch (pathno) {
		case 0:
			if (pos.x <= -6) { pathno++; } 
			else { motion.add(-step, 0, 0); }
			break;
		case 1:
			if (pos.y <= -7) { pathno++; } 
			else { motion.add(0, 0, -step); }
			break;
		case 2:
			if (pos.y >= -3 || pos.x >= -4) { pathno++; } 
			else { 
				motion.add(step, 0, 0);
				motion.add(0, 0, step);
			}
			break;
		case 3:
			if (pos.y <= -7) { pathno++; }
			else { 
				motion.add(step, 0, 0);
				motion.add(0, 0, -step);
			}
			break;
		case 4:
			if (pos.x >= 5) { pathno++; } 
			else { motion.add(step, 0, 0); }
			break;
		case 5:
			if (pos.y >= -1) { pathno++; } 
			else { motion.add(0, 0, step); }
			break;
		case 6:
			if (pos.x <= 3) { pathno++; } 
			else { motion.add(-step, 0, 0); }
			break;
		case 7:
			if (pos.y >= 4) { pathno++; } 
			else { motion.add(0, 0, step); }
			break;
		}

		RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
		Matrix4 pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		if (motion.lenSq() > 0.01) {
			motion.normalize();
			motion.mul(speed * (float) et);
			translate(pMat, camera.sceneObject.transformation, motion);
		}
		if (rotation.lenSq() > 0.01) {
			rotation.mul((float) (100.0 * et));
			rotate(pMat, camera.sceneObject.transformation, rotation);
		}
		shoot(fling);
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
	}

	/**
	 * Apply a rotation to the camera.
	 * 
	 * Rotate the camera about one ore more of its local axes, by modifying
	 * <b>transformation</b>. The camera is rotated by rotation.x about its
	 * horizontal axis, by rotation.y about its vertical axis, and by rotation.z
	 * around its view direction. The rotation is about the camera's viewpoint,
	 * if this.orbitMode is false, or about the world origin, if this.orbitMode
	 * is true.
	 * 
	 * @param parentWorld
	 *            The frame-to-world matrix of the camera's parent
	 * @param transformation
	 *            The camera's transformation matrix (in/out parameter)
	 * @param rotation
	 *            The rotation in degrees, as Euler angles (rotation angles
	 *            about x, y, z axes)
	 */
	protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
		// TODO#A3 SOLUTION START

		rotation = (rotation).mul((float) (Math.PI / 180.0));
		Matrix4 mRotx = new Matrix4();
		mRotx = Matrix4.createRotationX(rotation.x);
		Matrix4 mRoty = Matrix4.createRotationY(rotation.y);
		mRoty.mulBefore(Matrix4.createTranslation(camera.mWorldTransform.getTrans().negate()));
		mRoty.mulAfter(Matrix4.createTranslation(camera.mWorldTransform.getTrans()));

		transformation.mulBefore(mRotx);

		transformation.mulAfter(mRoty);

		// SOLUTION END
	}

	/**
	 * Apply a translation to the camera.
	 * 
	 * Translate the camera by an offset measured in camera space, by modifying
	 * <b>transformation</b>.
	 * 
	 * @param parentWorld
	 *            The frame-to-world matrix of the camera's parent
	 * @param transformation
	 *            The camera's transformation matrix (in/out parameter)
	 * @param motion
	 *            The translation in camera-space units
	 */
	protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
		// TODO#A3 SOLUTION START

		Matrix4 mTrans = Matrix4.createTranslation(motion);

		transformation.mulAfter(mTrans);

		Vector3 pos3 = transformation.getTrans();
		pos.set(pos3.x, pos3.z);

		// SOLUTION END
	}
	protected void shoot(Projectile P){
		//if P is active: set intensity and update position
		if (P.isActive()){
			if (scene.objects.get(P.getLink()) != null){
				SceneObject linked = scene.objects.get(P.getLink());
				//if the age is 0, set the mesh position to just above the camera
				if (P.getAge() == 0){
					linked.transformation.mulBefore(Matrix4.createTranslation(linked.transformation.getTrans().clone().negate()));
					Vector3 offset = camera.mWorldTransform.getX().clone().negate().div(2);
					offset.add(camera.mWorldTransform.getZ().clone().negate().mul(0.7f));
					offset.add(camera.mWorldTransform.getY().clone().negate().mul(0.2f));
					linked.transformation.mulBefore(Matrix4.createTranslation(camera.mWorldTransform.getTrans().clone().add(offset)));
				}
				if (scene.objects.get("Light1") != null){
					((SceneLight) scene.objects.get("Light1")).setIntensity(new Vector3d(-0.8/100.0*P.getAge() +1));
				}
				scene.objects.get(P.getLink()).transformation.mulBefore(Matrix4.createTranslation(P.getDirection()));
				P.age();
			}
		}
		//if P is not active: set intensity to zero and hide the mesh
		else{
			if (scene.objects.get("Light1") != null){
				((SceneLight) scene.objects.get("Light1")).setIntensity(new Vector3d(.2));
			}
		}
	}
}
