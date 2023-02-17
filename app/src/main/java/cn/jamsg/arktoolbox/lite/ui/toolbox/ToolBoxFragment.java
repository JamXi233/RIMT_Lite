package cn.jamsg.arktoolbox.lite.ui.toolbox;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.jamsg.arktoolbox.lite.R;
import cn.jamsg.arktoolbox.lite.databinding.FragmentToolboxBinding;

public class ToolBoxFragment extends Fragment {

    private FragmentToolboxBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ToolBoxViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ToolBoxViewModel.class);

        binding = FragmentToolboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}