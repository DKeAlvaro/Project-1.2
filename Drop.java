package project12.group19.gui;//package com.badlogic.drop;
//todo: connect to physical engine.
// go to line 224, there're 5 if statements,
// we need to connect different physical engine& Bot depends on the users choics,
// first user chooses bot, then user chooses physical engine,
// Try to run the program you can see the red color guidance,
// If the user want to change the choice afterwards, we need to restart.


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.MotionState;

import java.util.Optional;
import java.util.function.Supplier;
public class Drop extends ApplicationAdapter implements ApplicationListener {

    public PerspectiveCamera camera;
    public CameraInputController camController;
    Environment environment;
    ModelBatch modelbatch;
    Model model, ball, hole, water;
    Model wall;

    Model Coor;
    ModelBuilder modelBuilder, modelBuilder2;
    ModelInstance modelInstance;
    ModelInstance ballModel;
    ModelInstance modelInstance3;
    ModelInstance modelInstance4;
    ModelInstance modelInstance5;
    ModelInstance modelInstance6;

    SpriteBatch spriteBatch;
    BitmapFont font;
    CharSequence bots = "Press keys:\nR: Rule-based Bot\nB: AI Bot";

    MeshPartBuilder mb1;
    Material terrain;

    Texture texture;
    Texture grass;
//	float[] ff;
    float[] ballLocation;
    float range, accuracy;

	Renderable renderable;
	NodePart blockPart;

    private final HeightProfile surface;
    private final Supplier<MotionState> ballStateProvider;

    public Drop(HeightProfile surface, Supplier<MotionState> ballStateProvider) {
        this.surface = surface;
        this.ballStateProvider = ballStateProvider;
    }

    @Override
    public void create() {
        texture = new Texture("water002.jpeg");
        grass = new Texture("img.png");

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        camera = new PerspectiveCamera(0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 30f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 1f;
        camera.far = 100f;
        camera.fieldOfView = 300;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        createTerrain();

        modelbatch = new ModelBatch();
        DirectionalLight dLight = new DirectionalLight();
        Color lightColor = new Color(0.9f, 0.9f, 0.9f, 1);
        Vector3 lightVector = new Vector3(-1.0f, -0.75f, -0.25f);
        dLight.set(lightColor, lightVector);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 0.5f));
        environment.add(dLight);

        modelBuilder = new ModelBuilder();

        hole = modelBuilder.createCylinder(1,0.1f,1,100,new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
        //hole.set;//

        water = modelBuilder.createBox(100,100,1,new Material(TextureAttribute.createDiffuse(texture)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.TextureCoordinates|VertexAttributes.Usage.Normal);
        wall = modelBuilder.createBox(100,100,15,new Material(ColorAttribute.createDiffuse(new Color(155/255f,118/255f,83/255f,1))),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
        ball = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 100, 100, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Coor = modelBuilder.createXYZCoordinates(20, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ballLocation = new float[3];
        ballLocation[0] = 5f;
        ballLocation[1] = 6f;
        ballLocation[2] = heightFunction(5, 6);


        ballModel = new ModelInstance(ball, ballLocation[0], ballLocation[1], ballLocation[2]);
        modelInstance3 = new ModelInstance(Coor, 0, 0, 0);
        setHoleLoaction(4.7f,4.7f);
       // modelInstance4 = new ModelInstance(hole, 0, 0, heightFunction(4, 4) + 0.001f);
        modelInstance5 = new ModelInstance(water,0,0,-1);
        modelInstance6 = new ModelInstance(wall,0,0,-10);
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
    public void setHoleLoaction (float x, float y){
        modelInstance4 = new ModelInstance(hole, x, y, heightFunction(x, y) + 0.1f+0.3f);
        Vector3 direction = new Vector3(0,0,10);
        Vector3 up = new Vector3(0,0,10);

        modelInstance4.transform.rotate(1000,0,0,90);
    }

    public void createTerrain() {

        modelBuilder2 = new ModelBuilder();
        modelBuilder2.begin();

        Material terrain = new Material(TextureAttribute.createDiffuse(grass));

        range = 50f;
        accuracy = 0.2f;
        float x0 = -1 * range, y0 = -1 * range;

        while (x0 <= range) {
            while (y0 <= range) {
                mb1 =modelBuilder2.part("part"+x0+y0, GL30.GL_TRIANGLES, VertexAttributes.Usage.Position|VertexAttributes.Usage.TextureCoordinates |VertexAttributes.Usage.Normal, terrain);

                int count = 0;
                float[] ff = verticeList(x0, y0, x0 + 1, y0 + 1, accuracy);
                while (count < 18 * 2 / accuracy / accuracy) {
                    MeshPartBuilder.VertexInfo va = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 0).setCol(null).setUV(0.0f, 0.0f);
                   // here assign Color:

                    float z = heightFunction(ff[count],ff[count+1]);

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


    public Color assignColor(float z) {
        float para = 0.5f+Math.abs(z)*10;
        Color color = new Color(0,para,0,100);

            return color;
    }




    @Override
    public void render() {
        Optional.ofNullable(ballStateProvider.get()).ifPresent(state -> {
            float x = (float) state.getXPosition();
            float y = (float) state.getYPosition();
            float z = heightFunction(x, y);
            setBallLocation(x, y, z+0.4f);
        });
// click 'P'  to start
        if(Gdx.input.isKeyPressed(Input.Keys.P)){

            System.out.println("MOVE the Ball");
            // to do: here add the code to start the bot
        }
        modelbatch.begin(camera);


        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            System.out.println("Rule based Bot");
            bots = "Press keys\n1: Euler\n2: RK2\n4: RK4";
            // todo: connect to Rule Based Bot
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
            System.out.println("Ai Bot");
            bots = "Press keys\n1: Euler\n2: RK2\n4: RK4";
            // todo: connect to Ai Bot;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            System.out.println("Euler");
            //todo: 1.connect to Euler
            // 2.make game start
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            System.out.println("RK2");
            //todo: make game starttodo: 1.connect to RK2
            //	2.make game start
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            System.out.println("RK4");
            //todo: make game starttodo: 1.connect to RK4
            //	2.make game start

        }




        Gdx.gl.glClearColor(0f, 0.28f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camController.update();
        //Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
       // modelbatch,render(font,environment);
        modelbatch.render(modelInstance, environment);      //terrain
        modelbatch.render(ballModel, environment);          //ball
  //      modelbatch.render(modelInstance3, environment);     //coor
        modelbatch.render(modelInstance4, environment);     //hole
        modelbatch.render(modelInstance5, environment);     //water
        modelbatch.render(modelInstance6,environment);      //wall

        camera.update();
        camController.update();
        modelbatch.end();

        spriteBatch.begin();
        font.setColor(Color.RED);
        font.getData().setScale(1.5f);
        font.draw(spriteBatch, bots, 20, 880);
        spriteBatch.end();

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