package org.spatialia.santa;

import org.spatialia.santa.AlertDialog.Handler;
import org.spatialia.santa.logic.LevelManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

	private LevelManager model;
	private GameEngine engine;
	private View pauseView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		GameView view = (GameView) findViewById(R.id.gameBoard);

		model = new LevelManager();
		model.init(this);

		engine = new GameEngine(view, model);

		// TODO: load from settings
		engine.init(getResources(), model.getCurrLevel());

		pauseView = findViewById(R.id.paused);
		pauseView.startAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.animator.hide_out));
	}

	@Override
	protected void onDestroy() {
		model.uninit();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pauseView.getVisibility() != View.VISIBLE) {
			engine.onResume();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// TODO: make this once
		ViewTreeObserver observer = findViewById(R.id.gameBoard)
				.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				engine.onRotate();

				findViewById(R.id.gameBoard).getViewTreeObserver()
						.removeOnGlobalLayoutListener(this);
			}
		});
	}

	@Override
	protected void onPause() {
		engine.onPause();
		super.onPause();
	}

	public void onClickPauseResume(final View view) {
		if (engine.isPaused()) {
			Animation slide = AnimationUtils.loadAnimation(
					getApplicationContext(), R.animator.hide_out);
			slide.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					pauseView.setVisibility(View.GONE);
				}
			});
			pauseView.startAnimation(slide);
			((Button) view).setBackgroundResource(R.drawable.pause);
			engine.onResume();
		} else {

			TextView stats = (TextView) findViewById(R.id.stats);
			stats.setText(String.format(getString(R.string.app_game_stats_fmt),
					Math.max(0, model.getGiftsCollected()),
					model.getCurrentLevel()));

			Animation slide = AnimationUtils.loadAnimation(
					getApplicationContext(), R.animator.show_up);
			pauseView.setVisibility(View.VISIBLE);
			pauseView.startAnimation(slide);
			((Button) view).setBackgroundResource(R.drawable.play);
			engine.onPause();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (pauseView.getVisibility() == View.VISIBLE) {
				onClickPauseResume(findViewById(R.id.pause));
			} else {
				AlertDialog.show(this,
						getString(R.string.app_confirm_exit_message),
						getString(R.string.app_confirm_exit_title),
						getString(R.string.app_yes),
						getString(R.string.app_no), new Handler() {

							@Override
							public void onOK() {
								onBackPressed();
							}
						});
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
