package project12.group19.gui;//package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import project12.group19.api.geometry.space.HeightProfile;
//import jdk.internal.icu.text.NormalizerBase;
//import sun.security.provider.certpath.Vertex;

//import java.util.ArrayList;
//import java.util.Arrays;

public class Drop extends ApplicationAdapter implements ApplicationListener {

    public PerspectiveCamera camera;
    public CameraInputController camController;
    Environment environment;
    ModelBatch modelbatch;
    Model model, ball, hole, water;
    Model Coor;
    ModelBuilder modelBuilder, modelBuilder2;
    ModelInstance modelInstance;
    ModelInstance ballModel;
    ModelInstance modelInstance3;
    ModelInstance modelInstance4;
    ModelInstance modelInstance5;
    MeshPartBuilder mb1;
    Material terrain;
    //	Texture texture;
//	float[] ff;
    float[] ballLocation;
    float range, accuracy;

	Renderable renderable;
	NodePart blockPart;

    private final HeightProfile surface;

    public Drop(HeightProfile surface) {
        this.surface = surface;
    }

    @Override
    public void create() {
//		ff =  verticeList();
//		texture = new Texture("badlogic.jpg");
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 85f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 1f;
        camera.far = 300f;
        camera.fieldOfView = 1000;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        createTerrain();

        modelbatch = new ModelBatch();
        DirectionalLight dLight = new DirectionalLight();
        Color lightColor = new Color(0.75f, 0.75f, 0.75f, 1);
        Vector3 lightVector = new Vector3(-1.0f, -0.75f, -0.25f);
        dLight.set(lightColor, lightVector);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 0.5f));
        environment.add(dLight);

        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder mbHole;


        Material material = new Material(ColorAttribute.createDiffuse(Color.RED));
//		Material material2 = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        mbHole = modelBuilder.part("targetHole", GL30.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        Vector3 vectorHole = new Vector3(1, 1, 1);
        Vector3 normalHole = new Vector3(5, 5, 5);
        mbHole.circle(0.2f, 100, vectorHole, normalHole);
        hole = modelBuilder.end();
        water = modelBuilder.createRect(-50, -50, 0, 50, 50, 0, -50, 50, 0, 50, -50, 0, 0, 0, 100, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ball = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 100, 100, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Coor = modelBuilder.createXYZCoordinates(20, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ballLocation = new float[3];
        ballLocation[0] = 5f;
        ballLocation[1] = 6f;
        ballLocation[2] = heightFunction(5, 6);

        ballModel = new ModelInstance(ball, ballLocation[0], ballLocation[1], ballLocation[2]);
        modelInstance3 = new ModelInstance(Coor, 0, 0, 0);
        modelInstance4 = new ModelInstance(hole, 0, 0, heightFunction(4, 4) + 1);
        modelInstance5 = new ModelInstance(water, 0, 0, heightFunction(4, 4) + 1);

		blockPart = hole.nodes.get(0).parts.get(0);

		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = null;
		renderable.worldTransform.idt();
//		camera.lookAt(ballLocation[0],ballLocation[1],ballLocation[2]);
    }

    public void setBallLocation(float x, float y, float z) {
        // TODO: make sure ball is always non-null
        if (ball != null) {
            ballModel = new ModelInstance(ball, x, y, z);
        }
    }

    public void createTerrain() {

        modelBuilder2 = new ModelBuilder();
        modelBuilder2.begin();
//		MeshPartBuilder mb1;
//		MeshPartBuilder mb2;
		terrain = new Material(ColorAttribute.createDiffuse(Color.GREEN));
//		Material water = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        range = 50f;
        accuracy = 0.1f;
        int units = (int) (range * 2 * range * 2);
        float x0 = -1 * range, y0 = -1 * range;

        while (x0 <= range) {
            while (y0 <= range) {
                mb1 = modelBuilder2.part("part" + x0 + y0, GL30.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, terrain);
                //	mb2 =modelBuilder2.part("part"+x0+y0, GL30.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, water);
                int count = 0;
                float[] ff = verticeList(x0, y0, x0 + 1, y0 + 1, accuracy);
                while (count < 18 * 2 / accuracy / accuracy) {
                    MeshPartBuilder.VertexInfo va = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 0).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    MeshPartBuilder.VertexInfo vb = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    MeshPartBuilder.VertexInfo vc = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    mb1.triangle(va, vb, vc);
                }
                y0++;
            }
            x0++;
            y0 = -1 * range;
        }

        model = modelBuilder2.end();
        modelInstance = new ModelInstance(model, 0, 0, 0);
    }

    @Override
    public void render() {
        modelbatch.begin(camera);

        Gdx.gl.glClearColor(0.5f, 0.5f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camController.update();
        //Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelbatch.render(modelInstance, environment);
        modelbatch.render(ballModel, environment);
        modelbatch.render(modelInstance3, environment);
        modelbatch.render(modelInstance4, environment);
        modelbatch.render(modelInstance5, environment);

        camera.update();
        camController.update();

        modelbatch.end();
        //Gdx.input.setInputProcessor(this);	//this is important for updating camera, based on keyboard command.
    }

    @Override
    public void dispose() {
    }

    public float[] verticeList(float xStart, float yStart, float xEnd, float yEnd, float accuracy) {
        float[] ff = new float[(int) (3600)];
        int count = 0;
        float x0 = xStart;
        float y0 = yStart;
        while (x0 <= xEnd) {
            while (y0 <= yEnd) {
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;

                x0 += accuracy;
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;

                x0 -= accuracy;
                y0 += accuracy;
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;
            }
            y0 = yStart;
            x0 += accuracy;
        }
        y0 = yEnd;
        x0 = xEnd;
        while (x0 >= xStart) {
            while (y0 >= yStart) {
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;

                x0 -= accuracy;
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;

                x0 += accuracy;
                y0 -= accuracy;
                ff[count] = x0;
                count++;
                ff[count] = y0;
                count++;
                ff[count] = heightFunction(x0, y0);
                count++;
            }
            y0 = yEnd;
            x0 -= accuracy;
        }
        return ff;
    }

    public float heightFunction(float x, float y) {
        return (float) surface.getHeight(x, y);
    }
/*
	@Override
	public boolean keyDown(int keycode) {


		//in the real world , do not creat NEW variable over and over,
		// a temporary static member instead;
		if(keycode == Input.Keys.LEFT)
			camera.rotateAround(new Vector3(0f,0f,0f),new Vector3(0f,1f,0f),10f );

		if(keycode == Input.Keys.RIGHT)
			camera.rotateAround(new Vector3(0f,0f,0f),new Vector3(0f,1f,0f),-10f );

		if(keycode == Input.Keys.UP)
			camera.rotateAround(new Vector3(0f,0f,0f),new Vector3(1f,0f,0f),10f );
		if(keycode == Input.Keys.DOWN)
			camera.rotateAround(new Vector3(0f,0f,0f),new Vector3(1f,0f,0f),-10f );
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (character == 'a' || character == 'A') {
			camera.position.set(camera.position.x - 1, camera.position.y, camera.position.z);
		}
		if (character == 'd' || character == 'D') {
			camera.position.set(camera.position.x + 1, camera.position.y, camera.position.z);
		}
		if (character == 'w' || character == 'W') {
			camera.position.set(camera.position.x, camera.position.y + 1, camera.position.z);
		}
		if (character == 's' || character == 'S') {
			camera.position.set(camera.position.x, camera.position.y - 1, camera.position.z);
		}
		if (character == 'q' || character == 'Q') {
			camera.position.set(camera.position.x, camera.position.y , camera.position.z-1);
		}
		if (character == 'e' || character == 'E') {
			camera.position.set(camera.position.x, camera.position.y , camera.position.z+1);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//Vector3 Vball = ball.
		//camera.lookAt(ball);

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//camera.position.set(screenX,screenY,(screenX*screenX+screenY*screenY)/10+5);
		//camera.lookAt(screenX,screenY,(screenX*screenX+screenY*screenY)/10);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
	*/

}
