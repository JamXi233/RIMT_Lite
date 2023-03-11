package cn.jamsg.arktoolbox.lite.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.SelectionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.jamsg.arktoolbox.lite.JSGWareUtil;
import cn.jamsg.arktoolbox.lite.MainActivity;
import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.WebActivity;
import cn.jamsg.arktoolbox.lite.databinding.FragmentHomeBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView mRandomText;
    private String responseE = "";
    private String releaseNote = "";
    private String releaseLink = "";
    private String releaseVersion = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mRandomText = (TextView) root.findViewById(R.id.random_text);
        LinearLayout home_Start_Game_Layout = (LinearLayout) root.findViewById(R.id.StartGame_Layout);
        View home_Start_Game_Space = (View) root.findViewById(R.id.StartGame_Space);
        Button home_Start_Game_Official = (Button) root.findViewById(R.id.StartGame_Official);
        Button home_Start_Game_Bilibili = (Button) root.findViewById(R.id.StartGame_Bilibili);

        ProgressBar home_ProgressBar = (ProgressBar) root.findViewById(R.id.home_progress);
        ImageView home_Installed = (ImageView) root.findViewById(R.id.home_installed);
        ImageView home_NotInstall = (ImageView) root.findViewById(R.id.home_notinstall);
        TextView home_Title = (TextView) root.findViewById(R.id.home_title);
        TextView home_Version = (TextView) root.findViewById(R.id.home_version);
        CardView home_CardView_1 = (CardView) root.findViewById(R.id.Home_CardView_1);

        ImageView home_Image_Official = (ImageView) root.findViewById(R.id.Home_Image_Official);
        ImageView home_Image_Weibo = (ImageView) root.findViewById(R.id.Home_Image_Weibo);
        ImageView home_Image_Bilibili = (ImageView) root.findViewById(R.id.Home_Image_Bilibili);
        ImageView home_Image_Terra = (ImageView) root.findViewById(R.id.Home_Image_Terra);
        ImageView home_Image_Siren = (ImageView) root.findViewById(R.id.Home_Image_Siren);

        WebActivity webActivity = new WebActivity();

        setText();//设置随机文本

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10);//休眠10毫秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdate").equals("1")) {
                    CheckUpdate();
                    if (releaseVersion.equals("Err_0")) {
                        //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                    } else if (!releaseVersion.equals(getVersionName("cn.jamsg.arktoolbox.lite"))) {
                        CheckUpdate_Link();
                        if (releaseLink.equals("Err_0")) {
                            //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                        } else {
                            CheckUpdate_Note();
                            if (releaseNote.equals("Err_0")) {
                                //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                            } else {
                                handler.post(Update);
                            }
                        }
                    }
                }
            }
        }.start();

        home_Start_Game_Official.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent ActivityIntent = new Intent();
                ActivityIntent.setComponent(new ComponentName("com.hypergryph.arknights", "com.u8.sdk.U8UnityContext"));
                startActivity(ActivityIntent);
            }
        });

        home_Start_Game_Bilibili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent ActivityIntent = new Intent();
                ActivityIntent.setComponent(new ComponentName("com.hypergryph.arknights.bilibili", "com.u8.sdk.U8UnityContext"));
                startActivity(ActivityIntent);
            }
        });

        home_Image_Bilibili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("WebView_URL","https://space.bilibili.com/161775300");
                startActivity(intent);
            }
        });

        home_Image_Official.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("WebView_URL","https://ak.hypergryph.com/");
                startActivity(intent);
            }
        });

        home_Image_Weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("WebView_URL","https://weibo.com/u/6279793937");
                startActivity(intent);
            }
        });

        home_Image_Terra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("WebView_URL","https://terra-historicus.hypergryph.com/");
                startActivity(intent);
            }
        });

        home_Image_Siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("WebView_URL","https://monster-siren.hypergryph.com/");
                startActivity(intent);
            }
        });

        if (getPackageExist("com.hypergryph.arknights") == 1) {
            if (getPackageExist("com.hypergryph.arknights.bilibili") == 1) {
                home_ProgressBar.setVisibility(View.GONE);
                home_Installed.setVisibility(View.VISIBLE);
                home_Title.setText("明日方舟已安装");
                home_Version.setText("官服:"+getVersionName("com.hypergryph.arknights")+" | "+"B服:"+getVersionName("com.hypergryph.arknights.bilibili"));
                home_CardView_1.setCardBackgroundColor(Color.parseColor("#81C784"));
            } else {
                home_Start_Game_Bilibili.setVisibility(View.GONE);
                home_Start_Game_Space.setVisibility(View.GONE);
                home_ProgressBar.setVisibility(View.GONE);
                home_Installed.setVisibility(View.VISIBLE);
                home_Title.setText("明日方舟已安装");
                home_Version.setText("官服:"+getVersionName("com.hypergryph.arknights"));
                home_CardView_1.setCardBackgroundColor(Color.parseColor("#81C784"));
            }
        } else if (getPackageExist("com.hypergryph.arknights.bilibili") == 1){
            home_Start_Game_Official.setVisibility(View.GONE);
            home_Start_Game_Space.setVisibility(View.GONE);
            home_ProgressBar.setVisibility(View.GONE);
            home_Installed.setVisibility(View.VISIBLE);
            home_Title.setText("明日方舟已安装");
            home_Version.setText("B服:"+getVersionName("com.hypergryph.arknights.bilibili"));
            home_CardView_1.setCardBackgroundColor(Color.parseColor("#81C784"));
        }else{
            home_Start_Game_Layout.setVisibility(View.GONE);
            home_ProgressBar.setVisibility(View.GONE);
            home_NotInstall.setVisibility(View.VISIBLE);
            home_Title.setText("明日方舟未安装");
            home_Version.setText("工具箱检查无误,但是您好像没有安装游戏呢?");
            home_CardView_1.setCardBackgroundColor(Color.parseColor("#ffd181"));
        }

        return root;
    }

    Runnable Update = () -> {
        if(isVisible()) {
            View root = binding.getRoot();
            Button home_Update_Btn = (Button) root.findViewById(R.id.home_update_btn);
            CardView home_CardView_2 = (CardView) root.findViewById(R.id.Home_CardView_2);
            home_CardView_2.setVisibility(View.VISIBLE);
            home_Update_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder Update_Dialog = new AlertDialog.Builder(getContext());
                    Update_Dialog.setTitle("有可用更新");
                    Update_Dialog.setMessage("当前版本为:" + getVersionName("cn.jamsg.arktoolbox.lite") + "\n" + "\uD83C\uDD95最新版本为:" + releaseVersion + "\n\n更新日志:" + "\n" + releaseNote);
                    Update_Dialog.setPositiveButton("更新", (dialog, which) -> {
                        Uri releaseLinkUri = Uri.parse(releaseLink);
                        Intent intent = new Intent(Intent.ACTION_VIEW, releaseLinkUri);
                        startActivity(intent);
                    });
                    Update_Dialog.setNegativeButton("取消", (dialog, which) -> JSGWareUtil.showMessage(getContext(), "未执行更新"));
                    Update_Dialog.setCancelable(false);
                    if (!getActivity().isFinishing()) {
                        Update_Dialog.show();
                    }
                }
            });
        }
    };

    //随机文本
    private void setText() {
        new Thread(() -> {
            String random_text = getRandomText();
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = random_text;
            handler.sendMessage(message);
        }).start();
    }

    @SuppressLint("RestrictedApi")
    private String getRandomText() {
        StringBuilder stringBuffer = new StringBuilder();
        try {
            URL url = new URL("https://service-o1ddc945-1259078248.sh.apigw.tencentcs.com/release/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            return "加载随机文本失败";
        }
        return stringBuffer.toString();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if ((String) msg.obj == null) {
                    mRandomText.setText("加载随机文本失败");
                } else mRandomText.setText((String) msg.obj);
            }
        }
    };

    private String CheckUpdate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/toolbox/release/releaseVersion")
                .method("GET", null)
                .addHeader("Authorization", "Token " + FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/token"))
                .build();
        try {
            Response response = client.newCall(request).execute();
            //检查是否登陆成功
            releaseVersion = response.body().string();
            return releaseVersion;
        } catch (IOException e) {
            releaseVersion = "Err_0";
            return "Err_0";
        }
    }

    private String CheckUpdate_Note() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/toolbox/release/releaseNote")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            //检查是否登陆成功
            releaseNote = response.body().string();
            return releaseNote;
        } catch (IOException e) {
            releaseNote = "Err_0";
            return "Err_0";
        }
    }

    private String CheckUpdate_Link() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/toolbox/release/releaseLink")
                .method("GET", null)
                .addHeader("Authorization", "Token " + FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/token"))
                .build();
        try {
            Response response = client.newCall(request).execute();
            //检查是否登陆成功
            releaseLink = response.body().string();
            return releaseLink;
        } catch (IOException e) {
            releaseLink = "Err_0";
            return "Err_0";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public int getPackageExist(String PackageName) {
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return 1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getVersionCode(String PackageName) {
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "NotInstall";
    }

    public String getVersionName(String PackageName) {
        PackageManager manager = getContext().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            Log.i("VersionName", packageInfo.versionName);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "NotInstall";
    }
}