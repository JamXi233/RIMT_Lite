package cn.jamsg.arktoolbox.lite.ui.settings;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.jamsg.arktoolbox.lite.FileUtil;
import cn.jamsg.arktoolbox.lite.JSGWareUtil;
import cn.jamsg.arktoolbox.lite.LoginActivity;
import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.databinding.FragmentSettingsBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private String responseE = "";
    private String releaseNote = "";
    private String releaseLink = "";
    private String releaseVersion = "";

    private CardView settings_CardView_1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel notificationsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //登录卡片
        settings_CardView_1 = (CardView) root.findViewById(R.id.settings_CardView_1);
        LinearLayout settings_UserProfile = (LinearLayout) root.findViewById(R.id.settings_userprofile);
        ProgressBar settings_ProgressBar = (ProgressBar) root.findViewById(R.id.settings_progress);
        ImageView settings_Logined = (ImageView) root.findViewById(R.id.settings_logined);
        ImageView settings_NotLogin = (ImageView) root.findViewById(R.id.settings_notlogin);
        TextView settings_Username = (TextView) root.findViewById(R.id.settings_username);
        TextView settings_Uid = (TextView) root.findViewById(R.id.settings_uid);
        Button settings_LoginButton = (Button) root.findViewById(R.id.settings_loginbutton);
        Button settings_SignOutButton = (Button) root.findViewById(R.id.settings_signoutbutton);
        //用户信息
        TextView settings_UserProfile_Username = (TextView) root.findViewById(R.id.settings_userprofile_username);
        TextView settings_UserProfile_Email = (TextView) root.findViewById(R.id.settings_userprofile_email);
        TextView settings_UserProfile_JoinTime = (TextView) root.findViewById(R.id.settings_userprofile_jointime);
        TextView settings_UserProfile_Type = (TextView) root.findViewById(R.id.settings_userprofile_type);
        //自动检查更新
        SwitchCompat settings_AutoUpdate = (SwitchCompat) root.findViewById(R.id.settings_autoupdate);
        TextView settings_AutoUpdate_Hint = (TextView) root.findViewById(R.id.settings_autoupdate_hint);
        SwitchCompat settings_AutoUpdate_ToolBox = (SwitchCompat) root.findViewById(R.id.settings_autoupdate_toolbox);
        TextView settings_AutoUpdate_Toolbox_Hint = (TextView) root.findViewById(R.id.settings_autoupdate_toolbox_hint);
        //手动检查更新
        Button settings_CheckUpdate_Button = (Button) root.findViewById(R.id.settings_checkupdate_button);
        Button settings_CheckUpdate_ToolBox_Button = (Button) root.findViewById(R.id.settings_checkupdate_toolbox_button);
        TextView settings_CheckUpdate_Title = (TextView) root.findViewById(R.id.settings_checkupdate_title);
        TextView settings_CheckUpdate_Hint = (TextView) root.findViewById(R.id.settings_checkupdate_hint);
        TextView settings_CheckUpdate_ToolBox_Title = (TextView) root.findViewById(R.id.settings_checkupdate_toolbox_title);
        TextView settings_CheckUpdate_Toolbox_Hint = (TextView) root.findViewById(R.id.settings_checkupdate_toolbox_hint);


        Log.e("isLogined_File", FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/isLogined"));

        settings_AutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(settings_AutoUpdate.isChecked()){
                    settings_AutoUpdate_Hint.setText("将会在主页提示更新");
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdate", "1");
                }else {
                    settings_AutoUpdate_Hint.setText("将不会在主页提示更新");
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdate", "0");
                }
            }
        });

        settings_AutoUpdate_ToolBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(settings_AutoUpdate_ToolBox.isChecked()){
                    settings_AutoUpdate_Toolbox_Hint.setText("将会在启动时自动更新工具箱页面\n⚠将会大幅降低程序启动速度⚠");
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdateToolBox", "1");
                }else {
                    settings_AutoUpdate_Toolbox_Hint.setText("将不会在启动时自动更新工具箱页面");
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdateToolBox", "0");
                }
            }
        });

        if(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdate").equals("1")){
            settings_AutoUpdate.setChecked(true);
            settings_AutoUpdate_Hint.setText("将会在主页提示更新");
        }else{
            settings_AutoUpdate.setChecked(false);
            settings_AutoUpdate_Hint.setText("将不会在主页提示更新");
        }

        if(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdateToolBox").equals("1")){
            settings_AutoUpdate_ToolBox.setChecked(true);
            settings_AutoUpdate_Toolbox_Hint.setText("将会在启动时自动更新工具箱页面");
        }else {
            settings_AutoUpdate_ToolBox.setChecked(false);
            settings_AutoUpdate_Toolbox_Hint.setText("将不会在启动时自动更新工具箱页面");
        }

        settings_CheckUpdate_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(10);//休眠10毫秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        CheckUpdate();
                        if (releaseVersion.equals("Err_0")) {
                            //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                        } else if(!releaseVersion.equals(getVersionName("cn.jamsg.arktoolbox.lite"))) {
                            CheckUpdate_Link();
                            if (releaseLink.equals("Err_0")) {
                                //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                            } else {
                                CheckUpdate_Note();
                                if (releaseNote.equals("Err_0")) {
                                    //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                                } else {
                                    settings_CheckUpdate_Title.setText("检查到更新！");
                                    settings_CheckUpdate_Hint.setText("当前版本:"+getVersionName("cn.jamsg.arktoolbox.lite")+" | "+"最新版本"+releaseVersion);
                                    settings_CheckUpdate_Button.setText("详情");
                                    settings_CheckUpdate_Button.setBackgroundColor(Color.parseColor("#F50057"));
                                    settings_CheckUpdate_Button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            AlertDialog.Builder Update_Dialog = new AlertDialog.Builder(getContext());
                                            Update_Dialog.setTitle("有可用更新");
                                            Update_Dialog.setMessage("当前版本为:" + getVersionName("cn.jamsg.arktoolbox.lite") +"\n" +"\uD83C\uDD95最新版本为:" + releaseVersion +"\n\n更新日志:" + "\n" + releaseNote);
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
                            }
                        }else {
                            settings_CheckUpdate_Title.setText("已是最新版本");
                            settings_CheckUpdate_Hint.setText("当前版本:"+getVersionName("cn.jamsg.arktoolbox.lite")+" | "+"最新版本"+releaseVersion);
                            settings_CheckUpdate_Button.setEnabled(false);
                        }
                    }
                }.run();
            }
        });

        settings_CheckUpdate_ToolBox_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(0);//休眠0毫秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ToolBox_Name();
                        if (responseE.equals("Err_0")) {
                            //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                        } else {
                            FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/toolbox/toolbox_name",responseE);
                            ToolBox_URL();
                            if (responseE.equals("Err_0")) {
                                //JSGWareUtil.showMessage(getContext(),"更新工具箱失败");
                            } else {
                                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/toolbox/toolbox_url",responseE);
                            }
                        }
                    }
                }.run();
                if (responseE.equals("Err_0")){
                    settings_CheckUpdate_ToolBox_Title.setText("❌更新工具箱失败");
                }else settings_CheckUpdate_ToolBox_Title.setText("✅工具箱更新完成");settings_CheckUpdate_ToolBox_Button.setEnabled(false);settings_CheckUpdate_ToolBox_Button.setText("完成");
            }
        });

        //获取是否登录
        if (FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/isLogined").equals("0")) {
            Log.e("isLogined", "NotLogined");
            settings_ProgressBar.setVisibility(View.GONE);
            settings_SignOutButton.setVisibility(View.GONE);
            settings_LoginButton.setVisibility(View.VISIBLE);
            settings_CardView_1.setCardBackgroundColor(Color.parseColor("#A0A0A0"));
            //隐藏未登录图片显示头像
            settings_NotLogin.setVisibility(View.VISIBLE);
            settings_Logined.setVisibility(View.GONE);
            settings_Uid.setVisibility(View.GONE);
            settings_Username.setText("未登录");
            //不显示用户设置区块
            settings_UserProfile.setVisibility(View.GONE);
        } else {
            //设置头像和模糊
            Bitmap Avatar = FileUtil.decodeSampleBitmapFromPath(FileUtil.getPackageDataDir(getContext()) + "/userdata/avatar.png",128,128);
            Glide.with(this).load(Avatar).into(settings_Logined);
            setCardViewColor_1(Avatar);
            Log.e("isLogined", "Logined");
            settings_ProgressBar.setVisibility(View.GONE);
            settings_SignOutButton.setVisibility(View.VISIBLE);
            settings_LoginButton.setVisibility(View.GONE);
            //隐藏未登录图片显示头像
            settings_NotLogin.setVisibility(View.GONE);
            settings_Logined.setVisibility(View.VISIBLE);
            settings_Uid.setVisibility(View.VISIBLE);
            //设置用户名
            settings_Username.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/displayname"));
            settings_Uid.setText("UID:" + FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/uid"));
            //显示用户设置区块
            settings_UserProfile.setVisibility(View.VISIBLE);
            settings_UserProfile_Username.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/displayname"));
            settings_UserProfile_Email.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/email"));
            settings_UserProfile_JoinTime.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/group"));
            settings_UserProfile_Type.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/type"));
            settings_UserProfile_JoinTime.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/jointime"));
        }

        settings_LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        settings_SignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/isLogined", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/email", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/password", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/token", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/uid", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/badge", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/type", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/username", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/displayname", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/avatarurl", "0");
                FileUtil.writeFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/jointime", "0");
                settings_ProgressBar.setVisibility(View.GONE);
                settings_Logined.setVisibility(View.GONE);
                settings_NotLogin.setVisibility(View.VISIBLE);
                settings_Username.setText("未登录");
                settings_Uid.setVisibility(View.GONE);
                settings_LoginButton.setVisibility(View.VISIBLE);
                settings_SignOutButton.setVisibility(View.GONE);
                settings_UserProfile.setVisibility(View.GONE);
                settings_CardView_1.setCardBackgroundColor(Color.parseColor("#a0a0a0"));
            }
        });

        return root;
    }

    public void setCardViewColor_1(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getLightVibrantSwatch();
                if (swatch != null) {
                    settings_CardView_1.setCardBackgroundColor(swatch.getRgb());
                } else {
                    swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        settings_CardView_1.setCardBackgroundColor(swatch.getRgb());
                    } else {
                        swatch = palette.getMutedSwatch();
                        if (swatch != null) {
                            settings_CardView_1.setCardBackgroundColor(swatch.getRgb());
                        } else {
                            settings_CardView_1.setCardBackgroundColor(Color.parseColor("#A0A0A0"));
                        }
                    }
                }
            }
        });
    }

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

    private String ToolBox_Name() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url("https://api.craftsic.cn/toolbox/ToolBox_Name")
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
        Request request = new Request.Builder().url("https://api.craftsic.cn/toolbox/ToolBox_URL")
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        View root = binding.getRoot();
        LinearLayout settings_UserProfile = (LinearLayout) getActivity().findViewById(R.id.settings_userprofile);
        TextView settings_UserProfile_Username = (TextView) getActivity().findViewById(R.id.settings_userprofile_username);
        TextView settings_UserProfile_Email = (TextView) getActivity().findViewById(R.id.settings_userprofile_email);
        TextView settings_UserProfile_JoinTime = (TextView) getActivity().findViewById(R.id.settings_userprofile_jointime);
        TextView settings_UserProfile_Type = (TextView) getActivity().findViewById(R.id.settings_userprofile_type);
        ProgressBar settings_ProgressBar = (ProgressBar) getActivity().findViewById(R.id.settings_progress);
        ImageView settings_Logined = (ImageView) getActivity().findViewById(R.id.settings_logined);
        ImageView settings_NotLogin = (ImageView) getActivity().findViewById(R.id.settings_notlogin);
        TextView settings_Username = (TextView) getActivity().findViewById(R.id.settings_username);
        TextView settings_Uid = (TextView) getActivity().findViewById(R.id.settings_uid);
        Button settings_LoginButton = (Button) getActivity().findViewById(R.id.settings_loginbutton);
        Button settings_AutoUpdate = (Button) getActivity().findViewById(R.id.settings_autoupdate);
        Button settings_AutoUpdate_ToolBox = (Button) getActivity().findViewById(R.id.settings_autoupdate_toolbox);
        settings_CardView_1 = (CardView) getActivity().findViewById(R.id.settings_CardView_1);
        Button settings_SignOutButton = (Button) getActivity().findViewById(R.id.settings_signoutbutton);
        if (FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/isLogined").equals("0")) {
            Log.e("isLogined", "NotLogined");
            settings_ProgressBar.setVisibility(View.GONE);
            settings_SignOutButton.setVisibility(View.GONE);
            settings_LoginButton.setVisibility(View.VISIBLE);
            //隐藏未登录图片显示头像
            settings_NotLogin.setVisibility(View.VISIBLE);
            settings_Logined.setVisibility(View.GONE);
            settings_Uid.setVisibility(View.GONE);
            settings_Username.setText("未登录");
            //不显示用户设置区块
            settings_UserProfile.setVisibility(View.GONE);
        } else {
            //设置头像和模糊
            Bitmap Avatar = FileUtil.decodeSampleBitmapFromPath(FileUtil.getPackageDataDir(getContext()) + "/userdata/avatar.png",128,128);
            Glide.with(this).load(Avatar).into(settings_Logined);
            setCardViewColor_1(Avatar);
            Log.e("isLogined", "Logined");
            settings_ProgressBar.setVisibility(View.GONE);
            settings_SignOutButton.setVisibility(View.VISIBLE);
            settings_LoginButton.setVisibility(View.GONE);
            //隐藏未登录图片显示头像
            settings_NotLogin.setVisibility(View.GONE);
            settings_Logined.setVisibility(View.VISIBLE);
            settings_Uid.setVisibility(View.VISIBLE);
            //设置用户名
            settings_Username.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/displayname"));
            settings_Uid.setText("UID:" + FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/uid"));
            //显示用户设置区块
            settings_UserProfile.setVisibility(View.VISIBLE);
            settings_UserProfile_Username.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/displayname"));
            settings_UserProfile_Email.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/email"));
            settings_UserProfile_JoinTime.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/group"));
            settings_UserProfile_Type.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/type"));
            settings_UserProfile_JoinTime.setText(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/jointime"));
        }
    }
}