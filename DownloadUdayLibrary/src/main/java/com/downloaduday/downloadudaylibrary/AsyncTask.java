package com.downloaduday.downloadudaylibrary;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Uday Chandra on 9/1/2024.
 */
class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private DownloadUtils.DownloadListener listener;

    public DownloadTask(Context context, DownloadUtils.DownloadListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String fileName = params[1];
        try {
            DownloadUtils.downloadWithProgress(url, fileName, context, new DownloadUtils.DownloadListener() {
                @Override
                public void onDownloadStop(String filePath) {

                }

                @Override
                public void onDownloadPush(String filePath) {

                }

                @Override
                public void onDownloadComplete(String filePath) {
                    publishProgress(100); // Notify progress as 100% when download completes
                }

                @Override
                public void onDownloadFailed(String error) {
                    publishProgress(-1); // Notify failure
                }

                @Override
                public void onProgressUpdate(int progress) {
                    publishProgress(progress); // Update progress
                }
            });
            return fileName;
        } catch (Exception e) {
            listener.onDownloadFailed(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress >= 0) {
            listener.onProgressUpdate(progress);
        } else {
            listener.onDownloadFailed("Download failed");
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onDownloadComplete(result);
        }
    }
}
