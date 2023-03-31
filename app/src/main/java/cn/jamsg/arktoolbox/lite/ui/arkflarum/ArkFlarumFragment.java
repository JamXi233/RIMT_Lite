package cn.jamsg.arktoolbox.lite.ui.arkflarum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.databinding.FragmentArkflarumBinding;

public class ArkFlarumFragment extends Fragment {

    int Page = 1;
    private static final String API_URL = "https://bbs.arktoolbox.jamsg.cn/api/discussions?s=%2Fapi%2Fdiscussions";

    private ListView listView;
    private ArrayAdapter<Discussion> adapter;
    private FragmentArkflarumBinding binding;
    private String url;

    private TextView ToolBox_Title;
    private TextView ToolBox_SubTitle;
    private ProgressBar ToolBox_Progress;
    private ImageView ToolBox_Updated;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArkFlarumViewModel arkflarumViewModel =
                new ViewModelProvider(this).get(ArkFlarumViewModel.class);

        binding = FragmentArkflarumBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ToolBox_Title = (TextView) root.findViewById(R.id.toolbox_title);
        ToolBox_SubTitle = (TextView) root.findViewById(R.id.toolbox_subtitle);
        ToolBox_Progress = (ProgressBar) root.findViewById(R.id.toolbox_progress);
        ToolBox_Updated = (ImageView) root.findViewById(R.id.toolbox_updated);
        ToolBox_Progress.setVisibility(View.VISIBLE);
        ToolBox_Updated.setVisibility(View.GONE);
        ToolBox_SubTitle.setVisibility(View.GONE);

        listView = root.findViewById(R.id.discussion_list);

        GetDiscussion("https://bbs.arktoolbox.jamsg.cn/api/discussions?s=%2Fapi%2Fdiscussions");

        return root;
    }

    private void GetDiscussion(String url) {
        List<Map<String, String>> list = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, android.R.layout.simple_list_item_2, new String[] {"title", "subtitle"}, new int[] {android.R.id.text1, android.R.id.text2});
        ToolBox_Progress.setVisibility(View.VISIBLE);
        ToolBox_Updated.setVisibility(View.GONE);
        ToolBox_SubTitle.setVisibility(View.GONE);
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                response -> {
                    Gson gson = new Gson();
                    JSONArray data = response.optJSONArray("data");
                    JSONObject links = response.optJSONObject("links");
                    Map<String, String> item = null;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject discussion = data.optJSONObject(i);
                        String title = discussion.optJSONObject("attributes").optString("title");
                        String createat = discussion.optJSONObject("attributes").optString("createdAt");
                        Discussion Title = new Discussion(title);
                        Discussion Createdat = new Discussion(createat);
                        Log.e("E.", title);
                        item = new HashMap<>();
                        item.put("title", Title.toString());
                        item.put("subtitle", Createdat.toString());
                        list.add(item);
                        ToolBox_Title.setVisibility(View.VISIBLE);
                        ToolBox_SubTitle.setVisibility(View.VISIBLE);
                        ToolBox_Updated.setVisibility(View.VISIBLE);
                        ToolBox_Progress.setVisibility(View.GONE);
                        ToolBox_Title.setText("第"+Page+"页：" + data.length() + "个帖子");
                        if(i+1==data.length()){
                            item = new HashMap<>();
                            item.put("title", "下一页");
                            item.put("subtitle", "前往第"+(Page+1)+"页");
                            list.add(item);
                            if((Page-1)!=0) {
                                item = new HashMap<>();
                                item.put("title", "上一页");
                                item.put("subtitle", "回到第" + (Page - 1) + "页");
                                list.add(item);
                                item = new HashMap<>();
                                item.put("title", "回到首页");
                                item.put("subtitle", "回到第1页");
                                list.add(item);
                            }
                        }
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Log.e("E.", links.toString());
                            //Log.e("E.", String.valueOf(i));
                            if (i == 20){
                                GetDiscussion(links.optString("next"));
                                Page = Page + 1;
                            }else if (i == 21) {
                                GetDiscussion(links.optString("prev"));
                                Page = Page - 1;
                            }else if (i == 22) {
                                GetDiscussion(links.optString("first"));
                                Page = 1;
                            }
                        }
                    });
                    listView.setAdapter(adapter);
                },
                error -> Log.e("Volley", "Error: " + error.getMessage())
        );
        queue.add(request);


    }

    public class Discussion {
        private String title;

        public Discussion(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
