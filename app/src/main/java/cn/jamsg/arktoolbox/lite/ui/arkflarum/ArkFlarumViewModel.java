package cn.jamsg.arktoolbox.lite.ui.arkflarum;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArkFlarumViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ArkFlarumViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
