package com.downloaduday.downloadudaylibrary;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.DownloadListener;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.List;

/**
 * Created by Uday Chandra on 9/1/2024.
 */
public class DownloadUtils {

    public interface DownloadListener {
        void onDownloadStop(String filePath);

        void onDownloadPush(String filePath);

        void onDownloadComplete(String filePath);
        void onDownloadFailed(String error);
        void onProgressUpdate(int progress);
    }


    public static void downloadPDF(String url, String fileName, Context context, DownloadListener listener) {
        try {
            downloadWithProgress(url, fileName, context, listener);
        } catch (Exception e) {
            listener.onDownloadFailed(e.getMessage());
        }
    }

    @SuppressLint("Range")
    public static void downloadWithProgress(String url, String fileName, Context context, DownloadListener listener) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(fileName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        new Thread(() -> {
            boolean downloading = true;

            while (downloading) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);

                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    @SuppressLint("Range") int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        listener.onDownloadComplete(fileName);
                    }

                    final int dlProgress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                    listener.onProgressUpdate(dlProgress);
                }
                cursor.close();
            }
        }).start();
    }

    public static void showDownloadNotification(Context context, String filePath) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DOWNLOAD_CHANNEL")
                .setSmallIcon(R.drawable.developer)
                .setContentTitle("Download Complete")
                .setContentText("File downloaded: " + filePath)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/pdf");
//        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/png");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        notificationManager.notify(1, builder.build());
    }

    public static void downloadMultipleFiles(List<String> urls, List<String> fileNames, Context context, DownloadListener listener) {
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            String fileName = fileNames.get(i);

            DownloadTask downloadTask = new DownloadTask(context, (DownloadListener) listener);
            downloadTask.execute(url, fileName);
        }
    }

    public static boolean isFileDownloaded(String fileName, Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        return file.exists();
    }
}

