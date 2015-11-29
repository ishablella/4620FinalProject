package cs4620.gl;

import cs4620.common.SceneCamera;
import cs4620.common.SceneObject;
import egl.math.Matrix4;
import egl.math.Vector2;

public class RenderCamera extends RenderObject {
	/**
	 * Reference to Scene counterpart of this camera
	 */
	public final SceneCamera sceneCamera;
	
	/**
	 * The view transformation matrix
	 */
	public final Matrix4 mView = new Matrix4();
	
	/**
	 * The projection matrix
	 */
	public final Matrix4 mProj = new Matrix4();
	
	/**
	 * The viewing/projection matrix (The product of the view and projection matrices)
	 */
	public final Matrix4 mViewProjection = new Matrix4();
	
	/**
	 * The size of the viewport, in pixels.
	 */
	public final Vector2 viewportSize = new Vector2();
	
	public RenderCamera(SceneObject o, Vector2 viewSize) {
		super(o);
		sceneCamera = (SceneCamera)o;
		viewportSize.set(viewSize);
	}

	/**
	 * Update the camera's viewing/projection matrix.
	 * 
	 * Update the camera's viewing/projection matrix in response to any changes in the camera's transformation
	 * or viewing parameters.  The viewing and projection matrices are computed separately and multiplied to 
	 * form the combined viewing/projection matrix.  When computing the projection matrix, the size of the view
	 * is adjusted to match the aspect ratio (the ratio of width to height) of the viewport, so that objects do 
	 * not appear distorted.  This is done by increasing either the height or the width of the camera's view,
	 * so that more of the scene is visible than with the original size, rather than less.
	 *  
	 * @param viewportSize
	 */
	public void updateCameraMatrix(Vector2 viewportSize) {
		this.viewportSize.set(viewportSize);
		
		// The camera's transformation matrix is found in this.mWorldTransform (inherited from RenderObject).
		// The other camera parameters are found in the scene camera (this.sceneCamera).
		// Look through the methods in Matrix4 before you type in any matrices from the book or the OpenGL specification.
		
		// TODO#A3 SOLUTION START
		// TODO#A3 SOLUTION START

		// Create viewing matrix
		mView.set(mWorldTransform.clone().invert());
		
		// Correct Image Aspect Ratio By Enlarging Image
		double aratio = viewportSize.x/viewportSize.y;
		double imgx = sceneCamera.imageSize.x;
		double imgy = sceneCamera.imageSize.y;
		double iratio = imgx/imgy;
		if (iratio < aratio) {
			imgx = aratio*imgy;
//			sceneCamera.imageSize.x = aratio*imgy;;
		} else {
			imgy = imgx/aratio;
//			sceneCamera.imageSize.y = imgx/aratio;
		}

		// Create Projection
		if (sceneCamera.isPerspective) {
			mProj.set(Matrix4.createPerspective((float)imgx,(float)imgy,(float)sceneCamera.zPlanes.x,(float)sceneCamera.zPlanes.y));
		} else {
			mProj.set(Matrix4.createOrthographic((float)sceneCamera.imageSize.x,(float)sceneCamera.imageSize.y,(float)sceneCamera.zPlanes.x,(float)sceneCamera.zPlanes.y));
		}
		
		// Set the view projection matrix using the view and projection matrices
		mViewProjection.set(mProj.clone().mulBefore(mView));
	
		// SOLUTION END
	}	
}