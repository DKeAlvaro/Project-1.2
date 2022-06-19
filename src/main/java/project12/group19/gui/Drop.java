package project12.group19.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.state.Round;
import project12.group19.api.motion.AccelerationCalculator;
import project12.group19.api.motion.AdvancedAccelerationCalculator;
import project12.group19.api.motion.BasicAccelerationCalculator;
import project12.group19.api.motion.Solver;
import project12.group19.engine.EngineFactory;
import project12.group19.engine.GameHandler;
import project12.group19.engine.ScheduledEventLoop;
import project12.group19.engine.StandardThreadFactory;
import project12.group19.incubating.HillClimbing3;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK2;
import project12.group19.math.ode.RK4;
import project12.group19.player.ai.HitCalculator;
import project12.group19.player.ai.NaiveBot;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Drop extends ApplicationAdapter implements ApplicationListener {
    private static final Map<String, AccelerationCalculator> ACCELERATION_CALCULATORS = Map.of(
            "basic", new BasicAccelerationCalculator(),
            "advanced", new AdvancedAccelerationCalculator()
    );
    private static final NumberFormat DECIMAL_PRINT_FORMAT = new DecimalFormat("#.000");
    private static final String SOLVER_SELECTION_MENU = "Select solver:\n1.Euler\n2.Runge-Kutta 2nd Order\n3.Runge-Kutta 4th Order";
    private static final String BOT_SELECTION_MENU = "Press keys:\nR: Rule-based Bot\nB: Hill-Climbing Bot";
    private static final String LAUNCH_MENU = "Press P to continue";

    private static final Map<Integer, ODESolver> SOLVERS = Map.of(
            Input.Keys.NUM_1, new Euler(),
            Input.Keys.NUM_2, new RK2(),
            Input.Keys.NUM_3, new RK4()
    );

    private ODESolver solver;
    private Player bot;

    private boolean launched;

    public PerspectiveCamera camera;
    public CameraInputController camController;
    Environment environment;


    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    ModelBatch modelbatch;
    Model model, ball, hole, water;
    Model wall;

    Model Coor;
    ModelBuilder modelBuilder, modelBuilder2,modelBuilder3;
    ModelInstance modelInstance;
    ModelInstance ballModel;
    ModelInstance modelInstance3;
    ModelInstance modelInstance4;
    ModelInstance modelInstance5;
    ModelInstance modelInstance6;


    //------Here are phase3 parts:
    ModelInstance treeInstance;
    Model treeModel;
    ModelLoader modelLoader = new ObjLoader();
    Model modeltree1;
    Model modelLake;
    Model modelSandPits;
    ModelInstance lakeInstance;
    ModelInstance sandpitsInstance;
    float[] treeList;
    float[] sandpitList;
    Texture sandTexture;
    Material sandpitMaterial;
//-------------
    SpriteBatch spriteBatch;
    BitmapFont font;
    CharSequence menu = SOLVER_SELECTION_MENU;
    boolean showGameInfo = false;


    MeshPartBuilder mb1,mb2;
    Material terrain;

    Texture texture;
    Texture grass;
    float[] ballLocation;
    double velocityX;
    double velocityY;
    float range, accuracy;

    Renderable renderable;
    NodePart blockPart;

    private final Configuration configuration;
    private final AtomicReference<State> gameState = new AtomicReference<>();

    public Drop(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void create() {
        texture = new Texture("water-texture.jpg");
        grass = new Texture("grass-texture.png");
// ------------------------Here phase3 code starts------------------------
        sandTexture = new Texture("sandpitTexture.jpg");
        Material sandpitMaterial = new Material(TextureAttribute.createDiffuse(sandTexture));

        modeltree1 = modelLoader.loadModel(Gdx.files.internal("Lowpoly_tree_sample.obj"));
        treeInstance = new ModelInstance(modeltree1,-10,-10,0.5f);

        treeList = randomPosition(100);
        sandpitList = randomPosition(3);
        treeInstance.transform.scl(0.05f);
  //------------------------phase3 code ends----------------------------
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        camera = new PerspectiveCamera(0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(30f, 10f, 50f);
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
        // here you can adjust the shadow:
        environment.add((shadowLight = new DirectionalShadowLight(500, 500, 60f, 60f, .05f, 100f))
                .set(1f, 1f, 1f, 55.0f, 0f, 5f));

        environment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelBuilder = new ModelBuilder();

        float holeSize = (float) configuration.getHole().getRadius() * 2;
        hole = modelBuilder.createCylinder(holeSize,0.5f,holeSize,100,new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);

        float width = (float) configuration.getDimensions().getWidth();
        float height = (float) configuration.getDimensions().getHeight();
        water = modelBuilder.createBox(width,height,1,new Material(TextureAttribute.createDiffuse(texture)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.TextureCoordinates|VertexAttributes.Usage.Normal);
        wall = modelBuilder.createBox(width,height,15,new Material(ColorAttribute.createDiffuse(new Color(155/255f,118/255f,83/255f,1))),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
        ball = modelBuilder.createSphere(0.3f, 0.3f, 0.3f, 100, 100, new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Coor = modelBuilder.createXYZCoordinates(20, new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        modelLake = modelBuilder.createSphere(5,5,1,100,100, new Material(ColorAttribute.createDiffuse(new Color(0,191/255f,1f,1))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        lakeInstance = new ModelInstance(modelLake,6,6,heightFunction(0,0)-0.5f);

                ballLocation = new float[3];
        ballLocation[0] = (float) configuration.getInitialMotion().getXPosition();
        ballLocation[1] = (float) configuration.getInitialMotion().getYPosition();
        ballLocation[2] = heightFunction(ballLocation[0], ballLocation[1]+0.1f);

        ballModel = new ModelInstance(ball, ballLocation[0], ballLocation[1], ballLocation[2]);
        modelInstance3 = new ModelInstance(Coor, 0, 0, 0);
        setHoleLocation((float) configuration.getHole().getxHole(), (float) configuration.getHole().getyHole());
        modelInstance5 = new ModelInstance(water,0,0,-1);
        modelInstance6 = new ModelInstance(wall,0,0,-10);
		blockPart = hole.nodes.get(0).parts.get(0);

        renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = null;
		renderable.worldTransform.idt();
    }

    public void setBallLocation(float x, float y, float z) {
        // TODO: make sure ball is always non-null
        if (ball != null) {
            ballModel = new ModelInstance(ball, x, y, z);
        }
    }
    public float[] randomPosition(int number){
        float[] list = new float[3*number];
        int count = 0;
        for(int i=0;i<number;i++){
          float  x = (float)(Math.random()*42)-21;
          float  y=(float)(Math.random()*42)-21;
          float z = heightFunction(x,y);
        list[count] = x;
        count++;
        list[count] = y;
        count++;
        list[count]=z;
        count++;
        }
        return list;
    }

    public void renderTrees(){
        int size = treeList.length/3;

        for (int i=0;i<size;i++){
            ModelInstance treeInstance = new ModelInstance(modeltree1,treeList[3*i],treeList[3*i+1],treeList[3*i+2]-0.1f);
            treeInstance.transform.scl(0.05f);
            shadowBatch.render(treeInstance);
            modelbatch.render(treeInstance,environment);
        }
    }

    public void setHoleLocation(float x, float y) {
        modelInstance4 = new ModelInstance(hole, x, y, heightFunction(x, y)-0.1f);
        modelInstance4.transform.rotate(1000,0,0,90);
    }
// method to create the Terrain:
    public void createTerrain() {
        modelBuilder2 = new ModelBuilder();
        modelBuilder2.begin();

        Material currentMaterial = new Material(TextureAttribute.createDiffuse(grass));
        Material terrain = new Material(TextureAttribute.createDiffuse(grass));
        Material sandpit = new Material(TextureAttribute.createDiffuse(sandTexture));
        // TODO: use both width and height
        range = (float) (configuration.getDimensions().getWidth() / 2);
        accuracy = 0.2f;
        float x0 = -1 * range, y0 = -1 * range;
        while (x0 <= range) {
            while (y0 <= range) {
// the terrain is composed by GL_TRIANGLES
                mb1 =modelBuilder2.part("part"+x0+y0, GL30.GL_TRIANGLES, VertexAttributes.Usage.Position|VertexAttributes.Usage.TextureCoordinates |VertexAttributes.Usage.Normal, terrain);
                int count = 0;
                float[] ff = verticeList(x0, y0, x0 + 1, y0 + 1, accuracy);
                while (count < 18 * 2 / accuracy / accuracy) {

                    MeshPartBuilder.VertexInfo va = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                   // here assign Color:
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

        //____________-_______-____-__-__-_-___----- here we add sandpits:
        modelBuilder3 = new ModelBuilder();
        modelBuilder3.begin();
        float x1 = -1 * range, y1 = -1 * range;
        while (x1 <= range) {
            while (y1 <= range) {
                mb2 =modelBuilder3.part("sandPitPart"+x1+y1, GL30.GL_TRIANGLES, VertexAttributes.Usage.Position|VertexAttributes.Usage.TextureCoordinates |VertexAttributes.Usage.Normal, sandpit);
                int count = 0;
                float[] ff = verticeList(x1, y1, x1 + 1, y1 + 1, accuracy);
                while (count < 18 * 2 / accuracy / accuracy) {

                    boolean isgrass = true;
                    for(int i=0; i<sandpitList.length/3;i++){
                        float radius = 3; //r of sandPit
                        float distanceSqr = (ff[count]-sandpitList[3*i])*(ff[count]-sandpitList[3*i])+(ff[count+1]-sandpitList[3*i+1])*(ff[count+1]-sandpitList[3*i+1]);
                        if(distanceSqr<radius*radius){
                            isgrass = false;
                        }
                    }
                    MeshPartBuilder.VertexInfo va = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    MeshPartBuilder.VertexInfo vb = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    MeshPartBuilder.VertexInfo vc = new MeshPartBuilder.VertexInfo().setPos(ff[count],
                            ff[count + 1], ff[count + 2]).setNor(0, 0, 1).setCol(null).setUV(0.0f, 0.0f);
                    count += 3;
                    if(!isgrass) {
                        mb2.triangle(va, vb, vc);
                    }
                }
                y1++;
            }
            x1++;
            y1 = -1 * range;
        }
        modelSandPits = modelBuilder3.end();
        sandpitsInstance = new ModelInstance(modelSandPits, 0, 0, 0.05f);
    }

    @Override
    public void render() {
        shadowLight.begin(camera);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(ballModel);
        shadowBatch.render(modelInstance);
        shadowBatch.render(modelInstance4);     //hole
        shadowBatch.render(modelInstance5);     //water
        shadowBatch.render(modelInstance6);      //wall

          shadowBatch.render(sandpitsInstance);

        camera.update();
        camController.update();
        shadowBatch.end();
        shadowLight.end();


        Optional.ofNullable(gameState.get()).map(State::getBallState).ifPresent(state -> {
            float x = (float) state.getXPosition();
            float y = (float) state.getYPosition();
            float z = heightFunction(x, y);
            setBallLocation(x, y, z + 0.4f);
        });

        if (solver == null) {
            for (Integer key : SOLVERS.keySet()) {
                if (Gdx.input.isKeyJustPressed(key)) {
                    solver = SOLVERS.get(key);
                    menu = BOT_SELECTION_MENU;
                    break;
                }
            }
        } else if (bot == null) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
                bot = new NaiveBot(new HitCalculator.Adjusting());
                System.out.println("Rule based Bot");
                menu = LAUNCH_MENU;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
                HillClimbing3 hillClimbing = new HillClimbing3(new Solver(solver, configuration.getHeightProfile(), configuration.getGroundFriction()), configuration);
                bot = state -> {
                    try {
                        return hillClimbing.hillClimbing(state.getBallState().getXPosition(), state.getBallState().getYPosition());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return Optional.empty();
                };
                System.out.println("Random Bot");
                menu = LAUNCH_MENU;
            }
        } else if (!launched) {
            if (Gdx.input.isKeyPressed(Input.Keys.P)) {
                launched = true;

                ExecutorService containment = Executors.newSingleThreadExecutor(StandardThreadFactory.daemon("main-loop-"));

                containment.submit(() -> {
                    Setup setup = EngineFactory.createSetup(configuration, bot, List.of(gameState::set));

                    new GameHandler(ScheduledEventLoop.standard()).launch(setup).join();

                    System.out.println("That's all, folks!");
                });
            }
        } else {
            showGameInfo = true;
        }

        modelbatch.begin(camera);
        Gdx.gl.glClearColor(0f, 0.28f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camController.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelbatch.render(modelInstance, environment);      //terrain
        modelbatch.render(sandpitsInstance, environment);      //sandPits
        modelbatch.render(ballModel, environment);          //ball
        modelbatch.render(modelInstance4, environment);     //hole
        modelbatch.render(modelInstance5, environment);     //water
        modelbatch.render(modelInstance6,environment);      //wall
        modelbatch.render(treeInstance,environment);
        modelbatch.render(lakeInstance,environment);
        renderTrees();
        camera.update();
        camController.update();
        modelbatch.end();

        spriteBatch.begin();

        font.setColor(Color.RED);

        if (showGameInfo) {
            font.getData().setScale(1.3f);
            font.draw(spriteBatch, getGameInfo(), 20, 880);
        } else {
            font.getData().setScale(1.5f);
            font.draw(spriteBatch, menu, 20, 880);
        }
        spriteBatch.end();

    }

    private String getGameInfo() {
        State state = gameState.get();

        if (state == null) {
            return "";
        }

        OptionalInt allowedFouls = state.getRules().getAllowedFouls();
        String maxFouls = allowedFouls.isPresent() ? Integer.toString(allowedFouls.getAsInt()) : "[no limit]";
        StringBuilder info = new StringBuilder()
                .append("Rounds: ").append(state.getRounds().size()).append("\n")
                .append("Fouls: ").append(state.getFouls())
                .append(" (max: ").append(maxFouls).append(")\n");

        switch (state.getGameStatus()) {
            case ONGOING -> {
                info
                        .append("Position:\n")
                        .append("x: ").append(DECIMAL_PRINT_FORMAT.format(ballLocation[0])).append("\n")
                        .append("y: ").append(DECIMAL_PRINT_FORMAT.format(ballLocation[1])).append("\n")
                        .append("z: ").append(DECIMAL_PRINT_FORMAT.format(ballLocation[2])).append("\n")
                        .append("Velocity:\n")
                        .append("vx: ").append(DECIMAL_PRINT_FORMAT.format(velocityX)).append("\n")
                        .append("vy: ").append(DECIMAL_PRINT_FORMAT.format(velocityY)).append("\n");
            }
            case WON -> info.append("Game won!\n");
            case LOST -> info.append("Too many fouls, game lost!\n");
        }

        List<Round> history = state.getRounds().stream()
                .skip(Math.max(0, state.getRounds().size() - 3))
                .toList();
        if (!history.isEmpty()) {
            info.append("History (up to 3 last rounds):\n");
            for (Round round : history) {
                info.append("Round #").append(round.getIndex()).append(": ");

                if (round.getHit() != null) {
                    info
                            .append('{')
                            .append(DECIMAL_PRINT_FORMAT.format(round.getStartingPosition().getX()))
                            .append(", ")
                            .append(DECIMAL_PRINT_FORMAT.format(round.getStartingPosition().getY()))
                            .append("} + Hit {")
                            .append(DECIMAL_PRINT_FORMAT.format(round.getHit().getXVelocity()))
                            .append(", ")
                            .append(DECIMAL_PRINT_FORMAT.format(round.getHit().getYVelocity()))
                            .append("} -> ");

                    if (round.getTerminationReason() != null) {
                        info
                                .append(round.getTerminationReason())
                                .append(" at {")
                                .append(DECIMAL_PRINT_FORMAT.format(round.getEndingPosition().getX()))
                                .append(", ")
                                .append(DECIMAL_PRINT_FORMAT.format(round.getEndingPosition().getY()))
                                .append('}');
                    } else {
                        info.append("Waiting for round end...");
                    }
                } else {
                    info.append("Waiting for hit...");
                }

                info.append("\n");
            }
        }

        return info.toString();
    }

    @Override
    public void dispose() {}

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
        return (float) configuration.getHeightProfile().getHeight(x, y);
    }
}
