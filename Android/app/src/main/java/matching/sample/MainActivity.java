package matching.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import matching.sample.Fragment.Common.Dialog;
import matching.sample.Fragment.Common.Loading;
import matching.sample.Fragment.Common.Toast;
import matching.sample.Fragment.Splash.SplashFragment;
import matching.sample.Http.ImageStorage;
import matching.sample.System.DeviceUtility;
import matching.sample.System.GalleryManager;
import matching.sample.System.SaveData;
import matching.sample.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SaveData.getInstance().initialize(this);
        Loading.initialize(this);
        Dialog.initialize(this);
        Toast.initialize(this);
        DeviceUtility.initialize(this);
        GalleryManager.initialize(this);
        ImageStorage.initialize(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rootContainer, new SplashFragment());
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // ギャラリー
        GalleryManager.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public int getSubContainerId() {
        return R.id.subContainer;
    }
}