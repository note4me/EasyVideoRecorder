package org.easydarwin.video.render.template;

import org.easydarwin.video.render.MediaSource;
import org.easydarwin.video.render.core.ParamKeeper;

import android.util.Log;

public class AudioAsset extends Asset {

	// 资源长度（帧数）
	private long duration;

	private MediaSource mediaSource = null;

	public long getDuration() {
		if (mediaSource == null) {
			startDecode();
		}
		if (duration == 0) {
			duration = mediaSource.duration() * ParamKeeper.get().getFrameRate() / 1000;
		}
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public AudioAsset() {
		super();
		setAssetType(AssetType.AUDIO);
	}

	public void startDecode() {
		if (mediaSource != null) {
			return;
		}
		mediaSource = new MediaSource();
		mediaSource.init(getUri().getPath());
		int open = mediaSource.open();
		if (open != 0) {
			// 异常处理
			Log.e("ExcecuteProject", "open audio fail:" + open + "@" + getUri().getPath());
			return;
		}
		Log.i("ExcecuteProject", "start audio Decode:" + getUri().getPath() + "@" + mediaSource);
	}

	public void closeDecode() {
		if (mediaSource != null) {
			mediaSource.close();
			Log.i("ExcecuteProject", "close audio Decode:" + getUri().getPath() + "@" + mediaSource);
			mediaSource = null;
		}
	}

	// /** 把音频文件解码并合并到输出文件 */
	// public void pushAudio(MediaTarget mediaTarget) {
	// if (mediaSource == null) {
	// startDecode();
	// }
	//
	// try {
	// if (mediaSource.getFrame() != 0) {
	// // 异常处理
	// Log.e("ExcecuteProject", "decode audio fail:" + "@" +
	// getUri().getPath());
	// }
	//
	// while (true) {
	//
	// if (mediaTarget != null && mediaSource.isAudio()) {
	// mediaTarget.pushAudio(mediaSource.currentAudio(),
	// mediaSource.audioCurrentNumberOfSamples());
	// }
	//
	// if (mediaSource.getFrame() != 0) {
	// break;
	// }
	// }
	// } catch (Exception e) {
	// Log.e("ExcecuteProject", "push Audio fail:" + getUri().getPath() + "%to%"
	// + mediaTarget);
	// e.printStackTrace();
	// } finally {
	// closeDecode();
	// }
	//
	// }

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeDecode();
	}

	public byte[] getNextAudioSamples() {
		if (mediaSource == null) {
			startDecode();
		}

		if (mediaSource.getFrame() != 0) {
			// 异常处理
			Log.e("ExcecuteProject", "decode audio fail:" + "@" + getUri().getPath());
			closeDecode();
			return null;
		}

		return mediaSource.currentAudio();

	}

	public byte[] mixAudio(byte[] audio2) {
		Log.e("mixAudio", "mixAudio ing");
		byte[] audioSamples = getNextAudioSamples();
		if (audioSamples == null) {
			return audio2;
		}
		for (int i = 0; i < audioSamples.length; i++) {
			audioSamples[i] += audio2[i];
		}

		return audioSamples;
	}

}
