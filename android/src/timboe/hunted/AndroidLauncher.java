package timboe.hunted;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import timboe.hunted.HuntedGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 0;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGyroscope = false;
		config.useWakelock = true;
		initialize(new HuntedGame(), config);
	}
}
