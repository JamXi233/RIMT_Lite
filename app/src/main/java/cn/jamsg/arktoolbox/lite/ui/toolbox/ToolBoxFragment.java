package cn.jamsg.arktoolbox.lite.ui.toolbox;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.jamsg.arktoolbox.lite.FileUtil;
import cn.jamsg.arktoolbox.lite.MainActivity;
import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.databinding.FragmentToolboxBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ToolBoxFragment extends Fragment {

    private FragmentToolboxBinding binding;
    private Handler handler = null;
    private String responseE = "";
    ListView ToolBox_List;
    ArrayAdapter<String> ToolBox_Data_Adapter;
    String[] ToolBox_Data_Name;
    String[] ToolBox_Data_URL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ToolBoxViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ToolBoxViewModel.class);

        binding = FragmentToolboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ToolBox_List = root.findViewById(R.id.toolbox_List_Tools);
        handler = new Handler();

        responseE = FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/toolbox/toolbox_name");
        ToolBox_Data_Name = responseE.split(",");
        ToolBox_Data_Adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ToolBox_Data_Name);
        ToolBox_List.setAdapter(ToolBox_Data_Adapter);
        responseE = FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/toolbox/toolbox_url");
        ToolBox_Data_URL = responseE.split(",");
        TextView ToolBox_Title = (TextView) root.findViewById(R.id.toolbox_title);
        TextView ToolBox_SubTitle = (TextView) root.findViewById(R.id.toolbox_subtitle);
        ProgressBar ToolBox_Progress = (ProgressBar) root.findViewById(R.id.toolbox_progress);
        ImageView ToolBox_Updated = (ImageView) root.findViewById(R.id.toolbox_updated);
        ToolBox_Title.setVisibility(View.VISIBLE);
        ToolBox_SubTitle.setVisibility(View.VISIBLE);
        ToolBox_Updated.setVisibility(View.VISIBLE);
        ToolBox_Progress.setVisibility(View.GONE);
        ToolBox_Title.setText("明日方舟工具箱");
        if(FileUtil.readFile(FileUtil.getPackageDataDir(getContext()) + "/userdata/AutoUpdateToolBox").equals("1")){
            ToolBox_SubTitle.setText(ToolBox_Data_Name.length + "个工具\n自动更新已开启");
        }else ToolBox_SubTitle.setText(ToolBox_Data_Name.length + "个工具\n自动更新已关闭");

        ToolBox_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(ToolBox_Data_URL[i]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}