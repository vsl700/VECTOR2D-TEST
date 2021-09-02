package com.vasciie.justatest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class MyGdxGame extends ApplicationAdapter {
	
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	Vector2[] points, renderPoints;
	
	private static final float tempMul = 311.139f;
	
	
	@Override
	public void create () {
		shape = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		
		// points = new Vector2[] {new Vector2(0, 0), new Vector2(0, 75), new Vector2(-15, 140), new Vector2(50, 140), new Vector2(55, 210), new Vector2(55, 280)};
		
		points = new Vector2[6];
		points[0] = new Vector2(0, 0);
		points[1] = new Vector2(points[0].x, 75);
		points[2] = new Vector2(points[1].x, 140).sub(points[1]).rotateDeg(40).add(points[1]);
		points[3] = new Vector2(120, points[2].y);
		points[4] = new Vector2(points[3].x + 5, 210);
		points[5] = new Vector2(points[4].x, 280);
		
		/*for(Vector2 p : points) {
			p.scl(10);
		}*/
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		
		Vector2 input = new Vector2(Gdx.input.getX(), cam.viewportHeight - Gdx.input.getY()).scl(1 / tempMul);
		
		
		for(int i = 0; i < renderPoints.length - 1; i++) {
			shape.rectLine(renderPoints[i], renderPoints[i + 1], 5);
			shape.circle(renderPoints[i].x - 0.5f, renderPoints[i].y, 6);
			
			if(i == renderPoints.length - 2)
				shape.circle(renderPoints[i + 1].x - 0.5f, renderPoints[i + 1].y, 6);
			
			
			Vector2 closestPoint1 = renderPoints[i].cpy().scl(1 / tempMul), closestPoint2 = renderPoints[i + 1].cpy().scl(1 / tempMul);

			float inputCl1Dst = closestPoint1.dst(input);
			float inputCl2Dst = closestPoint2.dst(input);
			float cl1Cl2Dst = closestPoint1.dst(closestPoint2);
			
			Vector2 inputCl1 = input.cpy().sub(closestPoint1);
			Vector2 inputCl2 = input.cpy().sub(closestPoint2);
			Vector2 cl1Cl2 = closestPoint2.cpy().sub(closestPoint1);
			Vector2 cl2Cl1 = closestPoint1.cpy().sub(closestPoint2);
			
			float constant = 0.75f;//Bigger constant helps for further checking (even if the input is a bit furhter from the line it will still account it as it is in the route)
			float constant2 = 7 / tempMul;
			if(inputCl1Dst <= cl1Cl2Dst && inputCl2Dst <= cl1Cl2Dst && inputCl1.nor().dst(cl1Cl2.nor()) < constant && inputCl2.nor().dst(cl2Cl1.nor()) < constant || inputCl1Dst <= constant2 || inputCl2Dst <= constant2) {
				System.out.println("Input in the route");
			}
			
			
			//Relative to input point points
			//Vector2 inputToPoint = renderPoints[i].cpy().sub(input).scl(0.02645833f);
			//System.out.println("Point " + i + " is " + inputToPoint.len() + " cm away from the input! " + inputToPoint);
			
			/*
			 * float dstOld = input.dst(closestPoint1) + input.dst(closestPoint2); float
			 * dstNew = input.dst(renderPoints[i]) + input.dst(renderPoints[i + 1]); float
			 * dstNewPoints = renderPoints[i].dst(renderPoints[i + 1]);
			 * 
			 * if(dstNew < dstOld && renderPoints[i].dst(input) < dstNewPoints &&
			 * renderPoints[i + 1].dst(input) < dstNewPoints) { closestPoint1 =
			 * renderPoints[i]; closestPoint2 = renderPoints[i + 1];
			 * 
			 * System.out.println("Closest points: " + i + " and " + (i + 1)); }
			 */
		}
		
		shape.end();
		
		
		
	}
	
	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		renderPoints = new Vector2[points.length];
		for(int i = 0; i < points.length; i++) {
			renderPoints[i] = points[i].cpy().add(width / 2, 10);
		}
		
		for(int i = /*0*/1; i < renderPoints.length - 1; i++) {
			//if(i > 0) {
				System.out.print("Render points " + (i - 1) + ", " + i + " and " + (i + 1) + " ");
				
				Vector2 firstToMiddle = renderPoints[i].cpy().sub(renderPoints[i - 1]);
				Vector2 middleToLast = renderPoints[i + 1].cpy().sub(renderPoints[i]);
				Vector2 firstToLast = renderPoints[i + 1].cpy().sub(renderPoints[i - 1]);
				
				
				Vector2 fmNorVec = firstToMiddle.cpy().nor();
				Vector2 flNorVec2 = firstToLast.cpy().nor();
				
				Vector2 tempRightVec = fmNorVec.cpy().rotateDeg(-45);
				Vector2 tempLeftVec = fmNorVec.cpy().rotateDeg(45);
				
				float flRightDst = tempRightVec.dst(flNorVec2);
				float flLeftDst = tempLeftVec.dst(flNorVec2);
				
				float checkVal = Math.abs(flRightDst - flLeftDst);
				
				if(checkVal > 0.45f) {
					System.out.print("make a ");
					
					float fmlTotalLen = firstToMiddle.len() + middleToLast.len();
					float flLen = firstToLast.len();
					if(flLen < fmlTotalLen * 0.67f) {
						System.out.print("strong "); //97 to 177 Deg (or less)
					}else if(flLen < fmlTotalLen * 0.93f) {
						System.out.print("normal ");
					}else {
						System.out.print("light "); //31 to 43 Deg
					}
						
					
					if(flRightDst < flLeftDst) {
						System.out.println("right turn!");
					}
					else {
						System.out.println("left turn!");
					}
				}else {
					System.out.println("don't make a turn!");
				}
			//}
			
			/*
			 * Vector2 tempVec = renderPoints[i + 1].cpy().sub(renderPoints[i]);
			 * System.out.println(tempVec.cpy().nor()); System.out.println(tempVec.scl(1 /
			 * tempVec.len())); // = nor()
			 */		
		}
	}
	
	@Override
	public void dispose () {
		shape.dispose();
	}
}
