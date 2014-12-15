package org.spatialia.santa.util;

import android.os.AsyncTask;

public class JobRunner {

	public static abstract class Job {
		public void doWork() {
		}

		public void doUIBefore() {
		}

		public void doUIAfter() {
		}
	}

	public JobRunner() {
	}

	public static void run(Job job) {
		new AsyncServiceTask(job).execute();
	}

	/**
	 * Task used in services.
	 */
	private static class AsyncServiceTask extends
			AsyncTask<Object, Integer, Long> {

		private Job job;

		public AsyncServiceTask(Job job) {
			super();
			this.job = job;
		}

		@Override
		protected void onPreExecute() {
			job.doUIBefore();
		}

		@Override
		protected Long doInBackground(Object... params) {
			job.doWork();
			return 0L;
		}

		@Override
		protected void onPostExecute(Long result) {
			job.doUIAfter();
		}
	}
}
