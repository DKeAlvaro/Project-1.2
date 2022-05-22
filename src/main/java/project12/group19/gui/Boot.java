package project12.group19.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.Color;

/**
 * Here we create a new PerectiveCamera with a field of view of 67 degrees (which is common to use) and we set the
 * aspect ratio to the current width and height. Then we set the camera 10 units to the right, 10 units up and 10 units
 * back. The Z axis is pointing towards the viewer, so for the viewer a positive Z value of the camera is moving the
 * viewer back. We set the camera to look at (0,0,0) because that is where we are placing our 3D object to view. We set
 * the near and far values to make sure we would always see our object. And finally we update the camera so all changes
 * we made are reflected by the camera.
 */
public class Boot extends ApplicationAdapter implements ApplicationListener {
    public PerspectiveCamera camera;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public Environment environment;
    public CameraInputController camController;

    @Override
    public void create() {

        /**
         * Here we add an Environment instance. We construct it and set the ambient light (0.4, 0.4, 0.4), note that
         * the alpha value is ignored. Then we add a DirectionalLight with the color of (0.8, 0.8, 0.8) and the
         * direction of (-1.0, -0.8f, 0.2). I assume that you’re familiar with lights in general. Finally we pass the
         * environment to the modelBatch when rendering the instance.
         */
        //adding some lightning
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        /**
         * Here we add the ModelBatch, which is responsable for rendering and we initialize it in the create method.
         * In the render method we clear the screen, call modelBatch.begin(cam), render our ModelInstance and then call
         * modelBatch.end() to finish rendering. Finally we need to dispose the modelBatch to make sure all resources
         * (like the shaders it uses) are properly disposed.
         */
        modelBatch = new ModelBatch();
        /**
         *
         * camera for 3D
         *
         * public PerspectiveCamera(float fieldOfViewY,
         *                          float viewportWidth,
         *                          float viewportHeight)
         *
         * Constructs a new PerspectiveCamera with the given field of view and viewport size. The aspect ratio is
         * derived from the viewport size.
         *
         * Parameters:
         * fieldOfViewY - the field of view of the height, in degrees, the field of view for the width will be
         * calculated according to the aspect ratio.
         * viewportWidth - the viewport width
         * viewportHeight - the viewport height
         *
         * VIEWPORT - a polygon viewing region in computer graphics.
         *          - the visible area of a webpage on a display device
         *          - represents a polygonal (normally rectangular) area in computer graphics that is currently being
         *          viewed
         */
        camera = new PerspectiveCamera(100, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // setting the position of the camera
        camera.position.set(10f, 10f, 10f);
        /**
         * public void lookAt(float x,
         *                    float y,
         *                    float z)
         *
         * Recalculates the direction of the camera to look at the point (x, y, z). This function assumes the up vector
         * is normalized.
         *
         * Parameters:
         * x - the x-coordinate of the point to look at
         * y - the y-coordinate of the point to look at
         * z - the z-coordinate of the point to look at
         */
        camera.lookAt(0, 0, 0);
        // the near clipping plane distance, has to be positive
        camera.near = 1f;
        // the far clipping plane distance, has to be positive
        camera.far = 300f;
        // Recalculates the projection and view matrix of this camera and the Frustum planes.
        camera.update();

        /**
         * Helper class to create Models from code. To start building use the begin() method, when finished building
         * use the end() method. The end method returns the model just build. Building cannot be nested, only one model
         * (per ModelBuilder) can be build at the time. The same ModelBuilder can be used to build multiple models
         * sequential. Use the node() method to start a new node. Use one of the #part(...) methods to add a part
         * within a node. The part(String, int, VertexAttributes, Material) method will return a MeshPartBuilder which
         * can be used to build the node part.
         *
         * Here we instantiate a ModelBuilder, which can be used to create models on code. Then we create a simple model
         * box with a size of 5x5x5. We also add a material with a green diffuse color to it and add position and
         * normal components to the model. When creating a model at least Usage.Position is required. Usage.Normal
         * adds normals to the box, so e.g. lighting works correctly. Usage is a subclass of VertexAttributes.
         *
         * A model contains everything on what to render and it keeps track of the resources. It doesn’t contain
         * information like where to render the model. Therefor we need to create a ModelInstance. A ModelInstance
         * contains the location, rotation and scale the model should be rendered at. By default this is at (0,0,0),
         * so we just create a ModelInstance which should be rendered at (0,0,0).
         */
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);

        /**
         * Here we add a CameraInputController which we create with our cam as argument. We also set
         * Gdx.input.setInputProcessor to this camController and make sure to update it in the render call. That’s all
         * there is to add the basic camera controller. You can now drag to make the camera rotate.
         */
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }

    @Override
    public void resize(int i, int i1) {}

    @Override
    public void render() {
        camController.update();
        Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(instance, environment);
        modelBatch.end();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    // The model needs to be disposed, so we added a line to our Dispose() method.
    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
}
