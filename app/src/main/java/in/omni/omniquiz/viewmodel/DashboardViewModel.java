package in.omni.omniquiz.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.databinding.BaseObservable;

import in.omni.omniquiz.event.StartNewGameEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bundee on 9/14/16.
 */
public class DashboardViewModel extends BaseObservable implements ViewModel, Parcelable{
    //Tag
    private static final String TAG = DashboardViewModel.class.getName();

    //Variables

    /**
     * Constructor
     */
    public DashboardViewModel(){

    }

    protected DashboardViewModel(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DashboardViewModel> CREATOR = new Creator<DashboardViewModel>() {
        @Override
        public DashboardViewModel createFromParcel(Parcel in) {
            return new DashboardViewModel(in);
        }

        @Override
        public DashboardViewModel[] newArray(int size) {
            return new DashboardViewModel[size];
        }
    };

    /**
     * On click for starting new game
     * @param view
     */
    public void onStartGameClicked(View view){
        EventBus.getDefault().post(new StartNewGameEvent());
    }

    @Override
    public void destroy() {

    }

}
