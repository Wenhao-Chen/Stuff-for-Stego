package info.guardianproject.pixelknot;

import android.app.Activity;
import android.content.res.Resources;

import info.guardianproject.f5android.plugins.PluginNotificationListener;


public class Dummy extends Activity implements PluginNotificationListener {

    private Resources res;
    public Dummy(IStegoThreadHandler threadHandler)
    {
        res = threadHandler.getContext().getResources();
    }

    @Override
    public Resources getResources() {
        return res;
    }

    @Override
    public void onUpdate(String with_message) {

    }

    @Override
    public void onFailure() {

    }
}
