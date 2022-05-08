package sylkay.gdxtest;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
	
	private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Rectangle bucket;
	private Array<Rectangle> raindrops;

	private long lastDropTime;

	private final Vector3 touchPos = new Vector3();

	private int raindropsCaught = 0;
	private int score = 0;

	private BitmapFont font;
	private Label.LabelStyle labelStyle;

	private Stage stage;
	private Label label;

	@Override
	public void show() {
		stage = new Stage(new ScreenViewport());
		dropImage = new Texture(Gdx.files.internal("images/drop.png"));
		bucketImage = new Texture(Gdx.files.internal("images/bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"));
	    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/ambientRain.mp3"));


	  	rainMusic.setLooping(true);
		rainMusic.play();

	  	camera = new OrthographicCamera();
	  	camera.setToOrtho(false, 800, 480);

	  	batch = new SpriteBatch();

	    bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
	    bucket.height = 64;

		raindrops = new Array<Rectangle>();
   		spawnRaindrop();

		font = new BitmapFont(Gdx.files.internal("plt.fnt"));
		labelStyle = new Label.LabelStyle();
    	labelStyle.font = font;

		label = new Label("Sample Text",labelStyle);
    	label.setPosition(10,480 - 46);
		stage.addActor(label);
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	 }
	 
	@Override
	public void render(float delta) {
		// Draw your screen here. "delta" is the time since last render in seconds.
		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
    	batch.begin();
    	batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
    	batch.end();

		if(Gdx.input.isKeyPressed(Input.Keys.A)) bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D)) bucket.x += 400 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isTouched()) {	
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000 - raindropsCaught*10000000) spawnRaindrop();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= (200 + raindropsCaught) * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) {
				iter.remove();
				score -= 5;
				if(score < 0) score = 0;
			}
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
				raindropsCaught++;
				score++;
			}
		}
		label.setText("Score: " + score);
		stage.act();
    	stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// Resize your screen here. The parameters represent the new window size.
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		// Destroy screen's assets here.
		dropImage.dispose();
      	bucketImage.dispose();
      	dropSound.dispose();
      	rainMusic.dispose();
      	batch.dispose();
		stage.dispose();
	}
}