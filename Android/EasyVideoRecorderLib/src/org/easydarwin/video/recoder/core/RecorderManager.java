package org.easydarwin.video.recoder.core;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easydarwin.video.recoder.VideoRecoder;
import org.easydarwin.video.recoder.VideoRecoder.AInfo;
import org.easydarwin.video.recoder.VideoRecoder.VInfo;
import org.easydarwin.video.recoder.conf.RecorderConfig;
import org.easydarwin.video.recoder.view.VideoPreviewView;
import org.easydarwin.video.recoder.view.VideoProgressView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
public class RecorderManager implements PreviewCallback, SurfaceHolder.Callback {

	private static final String TAG = RecorderManager.class.getSimpleName();
	private CameraManager cameraManager = null;
	private long videoStartTime;
	private long videoTimeSingle;
	private long[] times = new long[2];

	private volatile AtomicBoolean recording = new AtomicBoolean(false);
	private volatile AtomicBoolean stopRecord = new AtomicBoolean(false);

	private AudioRecord audioRecorder;

	private String videoFile;
	private String finalVideoFile;
	private volatile boolean inBackspaceMode = true;

	private LinkedList<VideoSegment> recorderFiles = new LinkedList<VideoSegment>();

	private VideoProgressView mProgressView;

	private VideoPreviewView cameraTextureView;

	private String sessionVedioForder;

	private Semaphore semp = new Semaphore(1);

	private VideoEncodeThread videoEncodeThread;
	private AudioRecordThread audioRecordThread;
	private AudioEncodeThread audioEncodeThread;

	private int vieoNameIndexer = 0;
	private SurfaceHolder mSurfaceHolder = null;
	private int[] screenSize;

	public RecorderManager setScreenSize(int[] screenSize) {
		this.screenSize = screenSize;
		return this;
	}

	private RecorderConfig config;

	public RecorderConfig getConfig() {
		return config;
	}

	public RecorderManager setConfig(RecorderConfig config) {
		this.config = config;
		return this;
	}

	public RecorderManager(Activity activity, VideoPreviewView previewView, VideoProgressView progressView, RecorderConfig config) {
		cameraManager = new CameraManager(activity);
		this.config = config;
		this.mProgressView = progressView;
		this.cameraTextureView = previewView;
		mSurfaceHolder = previewView.getHolder();
		mSurfaceHolder.addCallback(this);
	}

	public interface VideoRecordListener {
		public void cameraReady();

		public void timeUpdate(long totalTime);

		public void segmentUpdate(int segment);
	}

	private VideoRecordListener l = new VideoRecordListener() {

		@Override
		public void cameraReady() {
		}

		@Override
		public void timeUpdate(long totalTime) {
		}

		@Override
		public void segmentUpdate(int segment) {
		}
	};

	private String genSegVideoName() {
		return new File(config.getVideoTmpDir(), vieoNameIndexer++ + ".mp4").getAbsolutePath();
	}

	private String genMegVideoName() {
		return new File(config.getVideoTmpDir(), "merge.mp4").getAbsolutePath();
	}

	public int[] getPreviewSize() {
		return cameraManager.getPreviewSize();
	}

	public static final byte NO_CAMERA_PERMISSION = 0x01;
	public static final byte NO_AUDIO_PERMISSION = 0x02;

	private byte getPermission = 0x00;

	public boolean getCheckPermissionReslut(byte type) {
		return (getPermission & type) == type;
	}

	public boolean checkHasStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File baseDir = new File(config.getBaseDir());
			if (!baseDir.exists()) {
				return baseDir.mkdirs();
			}
			return baseDir.canWrite();
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		}
		return false;
	}

	public long getSDFreeSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		return (freeBlocks * blockSize) / 1024 / 1024; //单位MB  
	}

	public byte checkPermission(Context context) {

		Thread vThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Camera testCamera = Camera.open();
					if (testCamera != null) {
						try {
							testCamera.getParameters();//recheck 
						} catch (Exception e) {
							getPermission |= NO_CAMERA_PERMISSION;
						}
						testCamera.release();
						testCamera = null;
					} else {
						getPermission |= NO_CAMERA_PERMISSION;
					}
				} catch (Exception e) {
					getPermission |= NO_CAMERA_PERMISSION;
				}
			}
		});
		Thread aThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int samp_rate = 44100;
					int minSize = android.media.AudioRecord.getMinBufferSize(samp_rate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
					AudioRecord testAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, samp_rate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
					if (testAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
						try {
							testAudioRecord.release();
							testAudioRecord = null;
						} catch (Exception e) {
						}
						getPermission |= NO_AUDIO_PERMISSION;
						return;
					}
					testAudioRecord.startRecording();
					byte[] testByte = new byte[100];
					int bufferReadResult = testAudioRecord.read(testByte, 0, 100);
					if (bufferReadResult <= 0) {
						getPermission |= NO_AUDIO_PERMISSION;
					} else {
					}
					testAudioRecord.stop();
					testAudioRecord.release();
					testAudioRecord = null;
				} catch (Exception e) {
					e.printStackTrace();
					getPermission |= NO_AUDIO_PERMISSION;
				}
			}
		});
		vThread.start();
		aThread.start();
		try {
			vThread.join();
			aThread.join();
		} catch (Exception e) {
			//ignore
		}
		return getPermission;
	}

	public synchronized void initRecorder() {
		videoFile = genSegVideoName();
		int size[] = cameraManager.getPreviewSize();
		int rotate = 0;
		if (cameraManager.isUseBackCamera()) {
			rotate = 90;
		}
		if (cameraManager.isUseFrontCamera()) {
			rotate = 270;
		}
		AInfo ainfo = new AInfo((short) 1, 44100, 2);
		VInfo vinfo = new VInfo(size[0], size[1], 480, 480, (short) config.getFrameRate(), 400000, rotate);

		@SuppressWarnings("unused")
		int rst = VideoRecoder.newFile(ainfo, vinfo, videoFile);
		Log.i(TAG, "recorder initialize success");

		videoEncodeThread = new VideoEncodeThread();
		audioRecordThread = new AudioRecordThread();
		audioEncodeThread = new AudioEncodeThread();
		audioRecordThread.start();
		videoEncodeThread.start();
		audioEncodeThread.start();
	}

	public void startRecord() {

		try {
			semp.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		recording.set(true);
		stopRecord.set(false);
		initRecorder();
		times[0] = times[1];
		videoStartTime = System.currentTimeMillis();
		videoStatusChangeListener.onVideoStatusChange(2);
		mProgressView.setCurrentState(VideoProgressView.State.START);
	}

	public void pauseRecord() {

		if (recording.get() && !stopRecord.get()) {
			try {
				stopRecord.set(true);
				recording.set(false);
				mProgressView.setCurrentState(VideoProgressView.State.PAUSE);
				mProgressView.putTimeList((int) times[1]);

				videoEncodeThread.join();
				audioRecordThread.join();
				audioEncodeThread.join();

				VideoRecoder.endStream();
				recorderFiles.add(new VideoSegment(videoTimeSingle, videoFile));
				l.segmentUpdate(recorderFiles.size());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				semp.release();
			}
		}
	}

	public void stopRecord(final VideoMergeListener l) {
		finalVideoFile = genMegVideoName();
		LinkedList<String> videos = new LinkedList<String>();
		for (VideoSegment se : recorderFiles) {
			videos.add(se.name);
		}
		final String[] videoArr = new String[videos.size()];
		videos.toArray(videoArr);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (videoArr.length == 0) {
						if (l != null) {
							l.onComplete(-1, finalVideoFile);
						}
					} else if (videoArr.length == 1) {
						if (l != null) {
							l.onComplete(1, videoArr[0]);
						}
					} else {
						int rst = VideoRecoder.mergeVideo(videoArr, finalVideoFile);
						if (l != null) {
							l.onComplete(rst, finalVideoFile);//TODO notice here
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static interface VideoMergeListener {
		public void onComplete(int status, String file);
	}

	public void release() {
		cameraManager.closeCamera();
		videoFrameQ.clear();
	}

	public void stopPreview() {
		cameraManager.stopPreview();
	}

	public void changeCamera() {
		cameraManager.changeCamera();
		l.cameraReady();
		cameraManager.updateParameters();

		cameraManager.setPreviewDisplay(mSurfaceHolder);
//		cameraManager.setPreviewTexture(videoSurface);
//		int[] previewSize = cameraManager.getPreviewSize();
//		cameraManager.setPreviewCallBackWithBuffer(previewSize[0], previewSize[1], this);
		cameraManager.setPreviewCallBack(this);
		cameraManager.startPreview();
	}

	public interface VideoStatusChangeListener {
		public void onVideoStatusChange(int status);
	}

	private VideoStatusChangeListener videoStatusChangeListener = new VideoStatusChangeListener() {

		@Override
		public void onVideoStatusChange(int totalTime) {

		}
	};

	public void backspace() {
		if (recorderFiles.size() > 0) {
			if (inBackspaceMode) {
				videoStatusChangeListener.onVideoStatusChange(1);
				inBackspaceMode = false;
				mProgressView.setCurrentState(VideoProgressView.State.BACKSPACE);
			} else {
				videoStatusChangeListener.onVideoStatusChange(2);
				inBackspaceMode = true;
				VideoSegment se = recorderFiles.pollLast();
				times[1] -= se.during;
				mProgressView.setCurrentState(VideoProgressView.State.DELETE);
				//deleteFileThreadStart(se.name);
				l.segmentUpdate(recorderFiles.size());
				l.timeUpdate(times[1]);
			}
		}
	}

	public static void deleteFileThreadStart(final String name) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File tmp = new File(name);
				if (tmp.exists()) {
					tmp.delete();
				}
			}
		}).start();
	}

	public void clear() {
		vieoNameIndexer = 0;
		if (recorderFiles.size() > 0) {
			deleteFileDir(sessionVedioForder);
			mProgressView.clearTimeList();
			recorderFiles.clear();
			Arrays.fill(times, 0);
			videoFrameQ.clear();
		}
	}

	long lastTs;

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		try {
			long now = System.currentTimeMillis();
			if (recording.get()) {
				videoTimeSingle = now - videoStartTime;
				times[1] = times[0] + videoTimeSingle;
				byte[] dataCopy = new byte[data.length];
				System.arraycopy(data, 0, dataCopy, 0, data.length);
				videoFrameQ.add(new Frame(now, dataCopy));
				l.timeUpdate(times[1]);
				lastTs = now;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LinkedBlockingQueue<Frame> videoFrameQ = new LinkedBlockingQueue<Frame>();

	private class VideoEncodeThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (recording.get()) {
					if (!videoFrameQ.isEmpty()) {
						Frame v = videoFrameQ.poll();
						VideoRecoder.writeVFrame(v.ts, v.data);
					}
				}
				if (stopRecord.get()) {
					if (!videoFrameQ.isEmpty()) {
						Frame v = videoFrameQ.poll();
						VideoRecoder.writeVFrame(v.ts, v.data);
					} else {
						break;

					}
				}
			}
		}
	}

	private LinkedBlockingQueue<Frame> audioFrameQ = new LinkedBlockingQueue<Frame>();

	private class AudioRecordThread extends Thread {

		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			int sampleAudioRateInHz = 44100;
			int minBufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			int bufferSize = 6114 >= minBufferSize ? 6114 : minBufferSize;
			audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

			while (audioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			ByteBuffer audioData = ByteBuffer.allocate(bufferSize);
			audioRecorder.startRecording();

			while (recording.get()) {
				audioData.position(0).limit(0);
				int bufferReadResult = audioRecorder.read(audioData.array(), 0, 2048);
				audioData.limit(bufferReadResult);
				if (bufferReadResult > 0) {
					long ts = System.currentTimeMillis();
					byte[] dataCopy = new byte[audioData.array().length];
					System.arraycopy(audioData.array(), 0, dataCopy, 0, audioData.array().length);
					audioFrameQ.add(new Frame(ts, dataCopy));
				}
			}
			if (audioRecorder != null) {
				try {
					audioRecorder.stop();
					audioRecorder.release();
					audioRecorder = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class AudioEncodeThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (recording.get()) {
					if (!audioFrameQ.isEmpty()) {
						Frame v = audioFrameQ.poll();
						VideoRecoder.writeAFrame(v.ts, v.data);
					}
				}
				if (stopRecord.get()) {
					if (!audioFrameQ.isEmpty()) {
						Frame v = audioFrameQ.poll();
						VideoRecoder.writeAFrame(v.ts, v.data);
					} else {
						break;
					}
				}
			}
		}
	}

	public String getFinalVideoFile() {
		return finalVideoFile;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		cameraManager.openCamera();
		cameraManager.updateParameters();
		cameraManager.setPreviewDisplay(holder);
		int[] previewSize = cameraManager.getPreviewSize();

		cameraTextureView.setAspectRatio(previewSize[1], previewSize[0], screenSize[0]);
		l.cameraReady();

		cameraManager.setPreviewCallBack(this);
		cameraManager.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		cameraManager.closeCamera();
	}

	public void setVideoRecordListener(VideoRecordListener l) {
		this.l = l;
	}

	private class Frame {
		long ts;
		byte[] data;

		public Frame(long ts, byte[] data) {
			this.ts = ts;
			this.data = data;
		}
	}

	private class VideoSegment {
		long during;
		String name;

		public VideoSegment(long during, String name) {
			this.during = during;
			this.name = name;
		}
	}

	public CameraManager cameraManager() {
		return cameraManager;
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	public static void deleteFileDir(String dir) {
		deleteFileDir(new File(dir));
	}

	public static void deleteFileDir(File dir) {
		try {
			if (dir.exists() && dir.isDirectory()) {
				if (dir.listFiles().length == 0) {
					dir.delete();
				} else {
					File delFile[] = dir.listFiles();
					int len = dir.listFiles().length;
					for (int j = 0; j < len; j++) {
						if (delFile[j].isDirectory()) {
							deleteFileDir(delFile[j]);
						} else {
							delFile[j].delete();
						}
					}
					delFile = null;
				}
				deleteFileDir(dir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVideoStatusChangeListener(VideoStatusChangeListener videoStatusChangeListener) {
		this.videoStatusChangeListener = videoStatusChangeListener;
	}
}
