package in.omni.omniquiz.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.omni.omniquiz.R;
import in.omni.omniquiz.event.StartNewGameEvent;
import in.omni.omniquiz.session.GameSession;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DashboardActivity extends AppCompatActivity{
    //Tag
    private static final String TAG = DashboardActivity.class.getName();

    //Variables
    private EventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    @Subscribe
    public void OnStartNewGameClicked(StartNewGameEvent event) {
        GameSession.getInstance().resetSession();
        startActivity(new Intent(this, QuizActivity.class));
    }
}
