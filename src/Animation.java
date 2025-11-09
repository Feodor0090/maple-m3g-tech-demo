import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;

public class Animation {

	private final Transformable t;
	private final long period;

	private final Transform idleTr;
	private final float[] idleRotation;
	private final float[] idlePosition;

	public Animation(Transformable t, long period) {
		this.t = t;
		idleTr = new Transform();
		idlePosition = new float[3];
		idleRotation = new float[4];
		t.getTransform(idleTr);
		t.getTranslation(idlePosition);
		t.getOrientation(idleRotation);
		this.period = period;
	}

	private static float normalizedSine(float x) {
		return (float) ((Math.sin(2 * Math.PI * x) + 1) / 2);
	}

	private float GetProgress() {
		float time = (System.currentTimeMillis() % period) / (float) period;
		return normalizedSine(time);
	}

	private float Lerp(float min, float max) {
		return min + (max - min) * GetProgress();
	}

	public void Begin() {
		t.setTransform(idleTr);
		t.setTranslation(idlePosition[0], idlePosition[1], idlePosition[2]);
		t.setScale(1, 1, 1);
		t.setOrientation(idleRotation[0], idleRotation[1], idleRotation[2], idleRotation[3]);
	}

	public void ScaleX(float min, float max) {
		t.scale(Lerp(min, max), 1, 1);
	}

	public void ScaleY(float min, float max) {
		t.scale(1, Lerp(min, max), 1);
	}

	public void TranslateX(float min, float max) {
		t.translate(Lerp(min, max), 0, 0);
	}

	public void TranslateY(float min, float max) {
		t.translate(0, Lerp(min, max), 0);
	}

	public void Rotate(float min, float max) {
		t.postRotate(Lerp(min, max), 0, 0, -1);
	}

	private float simpleLerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	public void OpenedEyes() {
		float time = (System.currentTimeMillis() % (2 * period)) / (float) period;
		if (time < 0.8f) {
			return;
		} else if (time <= 0.88f) {
			float progress = (time - 0.8f) / (0.88f - 0.8f);
			float scaleValue = simpleLerp(1.0f, 0.2f, progress);
			t.scale(1, scaleValue, 1);
		} else if (time <= 0.92f) {
			t.scale(1, 1, 1);
			t.translate(0, 0.5f, 0);
		} else if (time <= 1f) {
			float progress = (time - 0.92f) / (1.0f - 0.92f);
			float scaleValue = simpleLerp(0.2f, 1.0f, progress);
			t.scale(1, scaleValue, 1);
		}
	}

	public void ClosedEyes() {
		float time = (System.currentTimeMillis() % (2 * period)) / (float) period;
		if (time <= 0.88f) {
			t.translate(0, 0.5f, 0);
		} else if (time <= 0.92f) {
			t.scale(1, 1, 1);
		} else {
			t.translate(0, 0.5f, 0);
		}
	}

}
