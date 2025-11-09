import l2d.MeshData;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.*;
import javax.microedition.midlet.MIDlet;
import java.io.IOException;

public class MainCanvas extends GameCanvas implements Runnable {
	private final MIDlet midlet;
	private boolean running = true;
	private Image2D bgImage;
	private Background bg;
	private Image2D mapleBody;
	private SkinnedMesh mapleMesh;
	private Transform mapleRoot;
	private Camera cam;
	private Transform camTr;

	private Group eyesBone;
	private Group closedEyesBone;
	private Animation tailA;
	private Animation skirtA;
	private Animation faceA;
	private Animation bodyA;
	private Animation frontA;
	private Animation backA;
	private Animation earLA;
	private Animation earRA;
	private Animation brA;
	private Animation bellyA;
	private Animation neckA;
	private Animation eyesA;
	private Animation closedEyesA;


	protected MainCanvas(MIDlet midlet) {
		super(false);
		setFullScreenMode(true);
		this.midlet = midlet;
	}

	protected void keyPressed(int i) {
		if (i == -6 || i == -7)
			running = false;
	}

	protected void pointerPressed(int x, int y) {
		if (y < 100)
			running = false;
	}

	private void load() throws IOException {
		bgImage = new Image2D(Image2D.RGB, Image.createImage("/bg.png"));
		bg = new Background();
		bg.setDepthClearEnable(true);
		bg.setColorClearEnable(true);
		bg.setColor(0xffffffff);
		bg.setImage(bgImage);

		mapleBody = new Image2D(Image2D.RGBA, Image.createImage("/tex1024.png"));
		MeshData l2dData = MeshData.loadMeshes3D2("/Maple6.3d2", mapleBody, 1, false, true)[0];
		mapleMesh = (SkinnedMesh) l2dData.getM3GMesh();
		mapleRoot = new Transform();
		mapleRoot.postTranslate(0, 0, -3);

		Group legsBone = (Group) mapleMesh.getSkeleton().getChild(0);
		Group tailBone = (Group) legsBone.getChild(0);

		Group bellyBone = (Group) legsBone.getChild(1);
		Group bodyBone = (Group) bellyBone.getChild(0);
		Group skirtBone = (Group) bellyBone.getChild(1);
		Group neckBone = (Group) bodyBone.getChild(0);
		Group breastsBone = (Group) bodyBone.getChild(1);
		Group faceBone = (Group) neckBone.getChild(0);

		Group earLBone = (Group) faceBone.getChild(0);
		Group earRBone = (Group) faceBone.getChild(1);
		Group frontBone = (Group) faceBone.getChild(2);
		eyesBone = (Group) faceBone.getChild(3);
		Group backBone = (Group) faceBone.getChild(4);
		closedEyesBone = (Group) faceBone.getChild(5);

		tailA = new Animation(tailBone, 5900);
		skirtA = new Animation(skirtBone, 7900);
		bodyA = new Animation(bodyBone, 5300);
		bellyA = new Animation(bellyBone, 6700);

		neckA = new Animation(neckBone, 8900);
		faceA = new Animation(faceBone, 7100);
		frontA = new Animation(frontBone, 4700);
		backA = new Animation(backBone, 4700);

		earLA = new Animation(earLBone, 9700);
		earRA = new Animation(earRBone, 9700);

		brA = new Animation(breastsBone, 4100);

		eyesA = new Animation(eyesBone, 2500);
		closedEyesA = new Animation(closedEyesBone, 2500);

		cam = new Camera();
		camTr = new Transform();
		camTr.setIdentity();
		camTr.postTranslate(0, 0.5f, 0);
	}

	public void run() {
		try {
			long time = System.currentTimeMillis();
			load();
			long loadTime = System.currentTimeMillis() - time;
			Thread.yield();
			time = System.currentTimeMillis();
			int frames = 0;
			while (running) {
				Pass();
				frames++;
			}
			if(frames == 0)
				midlet.notifyDestroyed();
			Form f = new Form("Results");
			f.append("Load time: " + loadTime + "ms\n");
			f.append("Total frames: " + frames + "\n");
			long frameTime = ((System.currentTimeMillis() - time) / frames);
			f.append("Avg frame time: " + frameTime + "ms\n");
			Display.getDisplay(midlet).setCurrent(f);
		} catch (Throwable e) {
			e.printStackTrace();
			Form f = new Form("");
			f.append(e.toString());
			Display.getDisplay(midlet).setCurrent(f);
			return;
		}

	}

	private void Pass() {
		Graphics g = getGraphics();
		Graphics3D g3 = Graphics3D.getInstance();
		g3.bindTarget(g, false, Graphics3D.OVERWRITE | Graphics3D.ANTIALIAS);
		cam.setParallel(1, (float) getWidth() / getHeight(), 1f, 6f);
		g3.setCamera(cam, camTr);
		if (getWidth() < getHeight()) {
			// portait
			bg.setCrop(0, 0, (int) (640f * getWidth() / getHeight()), 640);
		} else {
			// landscape
			bg.setCrop(0, 0, 640, (int) (640f * getHeight() / getWidth()));
		}
		{
			tailA.Begin();
			tailA.Rotate(2, 0);

			skirtA.Begin();
			skirtA.ScaleX(1, 1.05f);
			skirtA.TranslateY(0, 0.005f);

			neckA.Begin();
			neckA.Rotate(-0.5f, 1f);

			faceA.Begin();
			faceA.TranslateY(0, 0.005f);

			bodyA.Begin();
			bodyA.Rotate(0, 2f);

			bellyA.Begin();
			bellyA.TranslateY(0, 0.008f);

			backA.Begin();
			backA.ScaleX(1, 1.1f);
			backA.TranslateY(0, 0.01f);

			frontA.Begin();
			frontA.ScaleX(1, 1.1f);
			frontA.TranslateY(0, -0.01f);

			earLA.Begin();
			earLA.TranslateX(0, -0.01f);
			earLA.TranslateY(0, -0.005f);
			earLA.Rotate(0, -5);

			earRA.Begin();
			earRA.TranslateX(0, 0.01f);
			earRA.TranslateY(0, -0.005f);
			earRA.Rotate(0, 5);

			brA.Begin();
			brA.TranslateY(0, -0.005f);
			brA.ScaleX(1.02f, 1f);

			eyesA.Begin();
			eyesA.OpenedEyes();

			closedEyesA.Begin();
			closedEyesA.ClosedEyes();
		}

		{
			g3.clear(bg);
			g3.render(mapleMesh, mapleRoot);
		}
		g3.releaseTarget();
		flushGraphics();
	}
}
