package com.downloaduday.downloadudaylibrary;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnDownload,btnDownload1,btnDownload2,btnDownload3;
    private ProgressBar progressBar;

    String Pdf_Url = "https://example.com/sample.pdf";
    String photoLink = "https://images.pexels.com/photos/235986/pexels-photo-235986.jpeg?auto=compress&cs=tinysrgb&w=600";
    String video_Url = "https://mp4videos.fusionbd.com/All_Files/320x240_Pixels/Kolkata_Movie_Songs/Girlfriend/Ekta_Girlfriend_Amar_Chai-Girlfriend_FusionBD.Com.mp4";

    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btnDownload = findViewById(R.id.btnDownload);
    btnDownload1 = findViewById(R.id.btnDownload1);
    btnDownload2 = findViewById(R.id.btnDownload2);
    btnDownload3 = findViewById(R.id.btnDownload3);
    progressBar = findViewById(R.id.progressBar);
    progressBar.setMax(100); // Set max value for progress bar

    // Setup for Button for downloading .
        /*
      You must declare the file extension in the file name of the file you want to download.
        If you don't declare that file extension, your downloaded file won't work.

      আপনি যে ফাইলটি ডাউনলোড করতে চান আপনাকে অবশ্যই এই ফাইলের নামটিতে সেই ফাইল এক্সটেনশনটি ঘোষণা করতে হবে।
        আপনি যদি  সেই ফাইল এক্সটেনশনটি ঘোষণা না করেন তবে আপনার ডাউনলোড করা ফাইল কাজ করবে না।
         */

//    Button btnDownload1 = findViewById(R.id.btnDownload1);
    setupDownloadButton(btnDownload, progressBar, Pdf_Url, "sample1.pdf");

    // Setup for Button 2
//    Button btnDownload2 = findViewById(R.id.btnDownload2);
    setupDownloadButton(btnDownload1, progressBar, Pdf_Url, "sample2.pdf");
    // Setup for Button 3

//    Button btnDownload3 = findViewById(R.id.btnDownload3);
    setupDownloadButton(btnDownload2, progressBar, photoLink, "Test Image.png");

    // Setup for Button 3

//    Button btnDownload3 = findViewById(R.id.btnDownload3);
    setupDownloadButton(btnDownload3, progressBar, video_Url, "Test Video.mp4");
    // Repeat for more buttons if needed
}


    private void setupDownloadButton(Button downloadButton, ProgressBar progressBar, String url, String fileName) {
        progressBar.setMax(100); // Set max value for progress bar

        downloadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            DownloadUtils.downloadPDF(url, fileName, MainActivity.this, new DownloadUtils.DownloadListener() {
                @Override
                public void onDownloadComplete(String filePath) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Download Complete: " + filePath, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onDownloadFailed(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Download Failed: " + error, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onProgressUpdate(int progress) {
                    runOnUiThread(() -> progressBar.setProgress(progress));
                }

                @Override
                public void onDownloadStop(String filePath) {
                    progressBar.setVisibility(View.GONE);
                    // Optional method: implement if necessary
                }

                @Override
                public void onDownloadPush(String filePath) {

                    // Optional method: implement if necessary
                }
            });
        });
    }
}

