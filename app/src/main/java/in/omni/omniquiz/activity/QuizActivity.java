package in.omni.omniquiz.activity;

import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import in.omni.omniquiz.R;
import in.omni.omniquiz.databinding.ActivityQuizBinding;
import in.omni.omniquiz.event.CloseQuizEvent;
import in.omni.omniquiz.event.DoneQuizesEvent;
import in.omni.omniquiz.event.PlayQuizAgainEvent;
import in.omni.omniquiz.event.QuizesLoadedEvent;
import in.omni.omniquiz.fragment.QuizFragment;
import in.omni.omniquiz.fragment.QuizResultsFragment;
import in.omni.omniquiz.intent.IntentUtil;
import in.omni.omniquiz.session.GameSession;
import in.omni.omniquiz.viewmodel.SessionViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class QuizActivity extends AppCompatActivity implements QuizFragment.QuizNavigation{
    //Tag
    private static final String TAG = QuizActivity.class.getName();

    //Variables
    private SessionViewModel mSessionViewModel;
    private ActivityQuizBinding mBinding;
    private EventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz);

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        if(savedInstanceState != null){
            mSessionViewModel = savedInstanceState.getParcelable(IntentUtil.SESSION_VM);
        }else{
            mSessionViewModel = new SessionViewModel();
        }

        //Data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        mBinding.setSessionViewModel(mSessionViewModel); //Session VM is special as it binds only with singleton instance.

        if(savedInstanceState == null){
            setupQuizProcess();
        }
    }

    private void setupQuizProcess() {
        //Check if we need to load quizes
        if(GameSession.getInstance().needToLoadQuizes()){
            //Ensure no fragments in manager
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(fragment != null){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            mSessionViewModel.loadQuizes();
        }else{
            //Initial insert
            showCurrentQuizFragment(null, null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentUtil.SESSION_VM, mSessionViewModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mEventBus.unregister(this);
    }

    @Override
    public void OnNextQuiz(View closeView, View topPanelView) {
        if(GameSession.getInstance().nextQuiz()){
            showCurrentQuizFragment(closeView, topPanelView);
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, QuizResultsFragment.newInstance(), QuizResultsFragment.class.getName())
                    .commit();
        }
    }

    /**
     * Convenince function to show a new quiz fragment
     * @param closeView
     * @param topPanelView
     */
    private void showCurrentQuizFragment(View closeView, View topPanelView){
        //Quiz fragment instance
        QuizFragment fragment = QuizFragment.newInstance(GameSession.getInstance().getCurrentQuiz());

        //Basic fragment transaction with animation
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        //If supported, apply shared element (close btuton, top panel)
        //Also, add some transition for all other elements in the fragment (the new fragment)
        //Only for lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Inflate transitions to apply
            Transition noTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition);
            Transition slideRightTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right);

            //Setup enter transition on fragment
            fragment.setSharedElementEnterTransition(noTransform); //Transition for shared elements
            fragment.setEnterTransition(slideRightTransform); //Transition for fragment entering

            //Get close image and top panel view
            if(closeView != null && topPanelView != null) {
                ViewCompat.setTransitionName(closeView, getString(R.string.transition_close_button));
                ft.addSharedElement(closeView, getString(R.string.transition_close_button));
                ft.addSharedElement(topPanelView, getString(R.string.transition_top_panel));
            }
        }

        ft.replace(R.id.fragment_container, fragment, QuizFragment.class.getName());
        ft.commit();
    }

    @Subscribe
    public void onQuizesLoaded(QuizesLoadedEvent event){
        setupQuizProcess();
    }

    @Subscribe
    public void OnCloseQuiz(CloseQuizEvent event) {
        finish();
    }

    @Subscribe
    public void OnDoneQuiz(DoneQuizesEvent event) {
        finish();
    }

    @Subscribe
    public void OnPlayNewQuiz(PlayQuizAgainEvent event) {
        //Reset game session
        GameSession.getInstance().resetSession();

        setupQuizProcess();
    }
}
