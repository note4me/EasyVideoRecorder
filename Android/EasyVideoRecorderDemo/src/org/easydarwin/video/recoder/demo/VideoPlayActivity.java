package org.easydarwin.video.recoder.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends Activity {

	VideoView player;
	MediaController mediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_play_activity);
		String uri = getIntent().getStringExtra("path");
		player = (VideoView) findViewById(R.id.player);
		player.setVideoPath(uri);
		mediaController = new MediaController(this);
		player.setMediaController(mediaController);
		mediaController.setMediaPlayer(player);
	}

	@Override
	protected void onResume() {
		super.onResume();
		player.requestFocus();
		player.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		player.stopPlayback();
	}
}
