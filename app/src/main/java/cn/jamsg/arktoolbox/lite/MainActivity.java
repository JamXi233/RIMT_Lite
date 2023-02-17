package cn.jamsg.arktoolbox.lite;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

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

import java.io.IOException;

import cn.jamsg.arktoolbox.lite.databinding.ActivityMainBinding;
import cn.jamsg.arktoolbox.lite.ui.home.HomeFragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String responseE = "";

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
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(10);//休眠10毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToolBox_Name();
                    responseE = ToolBox_Name();
                    if (responseE.equals("Err_0")) {
                        JSGWareUtil.showMessage(getApplicationContext(),"更新工具箱失败");
                    } else {
                        FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()) + "/toolbox/toolbox_name",responseE);
                        ToolBox_URL();
                        if (responseE.equals("Err_0")) {
                            JSGWareUtil.showMessage(getApplicationContext(),"更新工具箱失败");
                        } else {
                            FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()) + "/toolbox/toolbox_url",responseE);
                        }
                    }
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext())+"/userdata/AutoUpdateToolBox","0");
                }
            }.run();
        }
        if(FileUtil.readFile(FileUtil.getPackageDataDir(getApplicationContext()) + "/userdata/AutoUpdateToolBox").equals("1")){
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(10);//休眠10毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToolBox_Name();
                    responseE = ToolBox_Name();
                    if (responseE.equals("Err_0")) {
                        JSGWareUtil.showMessage(getApplicationContext(),"更新工具箱失败");
                    } else {
                        FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()) + "/toolbox/toolbox_name",responseE);
                        ToolBox_URL();
                        if (responseE.equals("Err_0")) {
                            JSGWareUtil.showMessage(getApplicationContext(),"更新工具箱失败");
                        } else {
                            FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()) + "/toolbox/toolbox_url",responseE);
                        }
                    }
                }
            }.run();
        }
        binding.topAppLayout.addLiftOnScrollListener(new AppBarLayout.LiftOnScrollListener() {
            @Override
            public void onUpdate(float elevation, int backgroundColor) {
                getWindow().setStatusBarColor(backgroundColor);
            }
        });
    }

    private String ToolBox_Name() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/ToolBox/ToolBox_Name")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            //检查是否登陆成功
            responseE = response.body().string();
            return responseE;
        } catch (IOException e) {
            responseE = "Err_0";
            return "Err_0";
        }
    }

    private String ToolBox_URL() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/ToolBox/ToolBox_URL")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            //检查是否登陆成功
            responseE = response.body().string();
            return responseE;
        } catch (IOException e) {
            responseE = "Err_0";
            return "Err_0";
        }
    }
}