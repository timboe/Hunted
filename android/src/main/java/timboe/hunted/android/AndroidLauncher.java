package timboe.hunted.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import timboe.hunted.HuntedGame;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        configuration.numSamples = 0;
        configuration.useAccelerometer = false;
        configuration.useCompass = false;
        configuration.useGyroscope = false;
        configuration.useWakelock = true;
        initialize(new HuntedGame(), configuration);
    }
}
