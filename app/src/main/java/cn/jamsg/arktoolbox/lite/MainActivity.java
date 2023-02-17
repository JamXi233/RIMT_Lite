package cn.jamsg.arktoolbox.lite;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import cn.jamsg.arktoolbox.lite.databinding.ActivityMainBinding;
import cn.jamsg.arktoolbox.lite.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(binding.topAppBar.getDrawingCacheBackgroundColor());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/")){
            FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/");
        }
        if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext())+"/toolbox/")){
            FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext())+"/toolbox/");
        }
        if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/isLogined")){
            FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/isLogined","0");
        }
        if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/AutoUpdate")){
            FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/AutoUpdate","1");
        }
        if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/AutoUpdateToolBox")){
            FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/AutoUpdateToolBox","1");
        }
        binding.topAppLayout.addLiftOnScrollListener(new AppBarLayout.LiftOnScrollListener() {
            @Override
            public void onUpdate(float elevation, int backgroundColor) {
                getWindow().setStatusBarColor(backgroundColor);
            }
        });
    }
}