package cn.jamsg.arktoolbox.lite.ui.toolbox;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolBoxViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ToolBoxViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}