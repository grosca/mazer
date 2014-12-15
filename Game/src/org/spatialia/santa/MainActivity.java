package org.spatialia.santa;

import java.io.PrintStream;

import org.spatialia.santa.AlertDialog.Handler;
import org.spatialia.santa.logic.Level;
import org.spatialia.santa.logic.LevelManager;
import org.spatialia.santa.util.JobRunner;
import org.spatialia.santa.util.JobRunner.Job;
import org.spatialia.santa.util.Post;

import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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

		Level level = model.getCurrLevel();
		loadLevel(level);

		pauseView = findViewById(R.id.paused);
		pauseView.startAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.animator.hide_out));
	}

	public void loadMoreLevels(View view) {
		JobRunner.run(new Job() {

			private String lastError;
			private boolean noMore;

			@Override
			public void doUIBefore() {
				showLoading(true);
				TextView txt = (TextView) findViewById(R.id.message);
				txt.setText(R.string.app_downloading_more_levels);
			}

			@Override
			public void doWork() {
				try {
					String level = Post.doPost("https://spatialia.com/santa/?",
							"level=" + (model.getCurrentLevel() - 1));
					noMore = level.length() == 0;
					if (!noMore) {
						String levelPath = getFilesDir().getAbsolutePath();
						levelPath += "/level." + (model.getCurrentLevel() - 1);
						PrintStream out = new PrintStream(levelPath);
						out.println(level);
						out.close();
					}
				} catch (Exception ex) {
					lastError = ex.toString();
				}
			}

			@Override
			public void doUIAfter() {
				TextView txt = (TextView) findViewById(R.id.message);
				txt.setText("");

				if (noInternet()) {
					ImageView image = (ImageView) findViewById(R.id.icon);
					image.setImageResource(R.drawable.internet);
					image.setVisibility(View.VISIBLE);

					findViewById(R.id.progress).setVisibility(View.GONE);

					txt.setText(R.string.app_no_internet);
					txt.setClickable(true);
				} else if (noMore) {
					ImageView image = (ImageView) findViewById(R.id.icon);
					image.setImageResource(R.drawable.more);
					image.setVisibility(View.VISIBLE);

					findViewById(R.id.progress).setVisibility(View.GONE);
					txt.setText(R.string.app_no_more_levels);
				} else {
					findViewById(R.id.icon).setVisibility(View.GONE);
					findViewById(R.id.progress).setVisibility(View.VISIBLE);
					showLoading(false);
					txt.setClickable(false);
					loadLevel(model.getLevel(model.getCurrentLevel() - 1));
				}
			}

			private boolean noInternet() {
				if (lastError != null) {
					return lastError.contains("UnknownHostException")
							|| lastError.contains("Hostname")
							|| lastError.contains("ConnectException");
				}
				return false;
			}
		});
	}

	public void loadLevel(Level level) {
		if (level == null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					loadMoreLevels(null);
				}
			});
		} else {
			engine.init(level);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					initViewSize();
				}
			});
		}
	}

	public void loadNextLevel() {
		Level level = model.getNextLevel();
		loadLevel(level);
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

	private void showLoading(boolean show) {
		findViewById(R.id.gameView).setVisibility(
				show ? View.GONE : View.VISIBLE);
		findViewById(R.id.loadingView).setVisibility(
				show ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		initViewSize();
	}

	private void initViewSize() {
		JobRunner.run(new Job() {
			DisplayMetrics metrics;

			@Override
			public void doUIBefore() {
				showLoading(true);

				metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
			}

			@Override
			public void doWork() {
				engine.initScreenSize(metrics.widthPixels, metrics.heightPixels);
			}

			@Override
			public void doUIAfter() {
				showLoading(false);
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
