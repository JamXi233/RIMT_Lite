package cn.jamsg.arktoolbox.lite.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.SelectionEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import cn.jamsg.arktoolbox.lite.FileUtil;
import cn.jamsg.arktoolbox.lite.MainActivity;
import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView mRandomText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mRandomText = (TextView) root.findViewById(R.id.random_text);
        ProgressBar home_ProgressBar = (ProgressBar) root.findViewById(R.id.home_progress);
        ImageView home_Installed = (ImageView) root.findViewById(R.id.home_installed);
        ImageView home_NotInstall = (ImageView) root.findViewById(R.id.home_notinstall);
        TextView home_Title = (TextView) root.findViewById(R.id.home_title);
        TextView home_Version = (TextView) root.findViewById(R.id.home_version);
        CardView home_CardView_1 = (CardView) root.findViewById(R.id.Home_CardView_1);

        setText();//设置随机文本

        if(getPackageExist("com.suntend.arktoolbox")==1){
            home_ProgressBar.setVisibility(View.GONE);
            home_Installed.setVisibility(View.VISIBLE);
            home_Title.setText("明日方舟已安装");
            home_Version.setText(getVersionName("com.suntend.arktoolbox"));
            home_CardView_1.setCardBackgroundColor(Color.parseColor("#81C784"));
        }
        else {
            home_ProgressBar.setVisibility(View.GONE);
            home_NotInstall.setVisibility(View.VISIBLE);
            home_Title.setText("明日方舟未安装");
            home_Version.setText("工具箱检查无误,但是您好像没有安装游戏呢?");
            home_CardView_1.setCardBackgroundColor(Color.parseColor("#ffd181"));
        }

        return root;
    }

    //随机文本
    private void setText(){
        new Thread(()->{
            String random_text=getRandomText();
            Message message=handler.obtainMessage();
            message.what=1;
            message.obj=random_text;
            handler.sendMessage(message);
        }).start();
    }

    @SuppressLint("RestrictedApi")
    private String getRandomText(){
        StringBuilder stringBuffer=new StringBuilder();
        try{
            URL url=new URL("https://service-o1ddc945-1259078248.sh.apigw.tencentcs.com/release/");
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
        }catch (IOException e){
            return "加载随机文本失败";
        }
        return stringBuffer.toString();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler=new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                if ((String)msg.obj==null){
                    mRandomText.setText("加载随机文本失败");
                }
                else mRandomText.setText((String)msg.obj);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public int getPackageExist(String PackageName){
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo("com.suntend.arktoolbox", PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return 1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getVersionCode(String PackageName){
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo("com.suntend.arktoolbox", PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "NotInstall";
    }

    public String getVersionName(String PackageName){
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo("com.suntend.arktoolbox", PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "NotInstall";
    }
}