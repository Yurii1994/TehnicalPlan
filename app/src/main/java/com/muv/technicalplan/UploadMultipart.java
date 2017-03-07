package com.muv.technicalplan;

import android.app.Activity;
import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.Placeholders;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;

public class UploadMultipart extends Activity
{
    public void uploadImage(final Context context, String path, String url)
    {
        SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
        saveLoadPreferences.saveBooleanPreferences("IMAGE", "START_UPLOAD", true, context);
        try
        {
            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
            uploadNotificationConfig.setTitle(context.getText(R.string.upload).toString());
            uploadNotificationConfig.setInProgressMessage(context.getText(R.string.uploadind).toString() + " " + Placeholders.PROGRESS);
            uploadNotificationConfig.setCompletedMessage(context.getText(R.string.comleted).toString());
            uploadNotificationConfig.setErrorMessage(context.getText(R.string.error).toString());
            uploadNotificationConfig.setRingToneEnabled(false);

            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(context, uploadId, url)
                    .addFileToUpload(path, "image")
                    .setMaxRetries(2)
                    .setNotificationConfig(uploadNotificationConfig)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(UploadInfo uploadInfo) {
                            SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
                            saveLoadPreferences.saveIntegerPreferences("IMAGE", "UPLOAD_PERCENT", uploadInfo.getProgressPercent(), context);
                        }

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception exception) {
                            SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
                            saveLoadPreferences.saveBooleanPreferences("IMAGE", "STATE_UPLOAD", false, context);
                            saveLoadPreferences.saveIntegerPreferences("IMAGE", "UPLOAD_PERCENT", 0, context);
                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                            PicassoTools.clearCache(Picasso.with(context));
                            SaveLoadPreferences saveLoadPreferences = new SaveLoadPreferences();
                            saveLoadPreferences.saveBooleanPreferences("IMAGE", "STATE_UPLOAD", true, context);
                            saveLoadPreferences.saveIntegerPreferences("IMAGE", "UPLOAD_PERCENT", 100, context);
                        }

                        @Override
                        public void onCancelled(UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void uploadSCV(final Context context, String  path, String url)
    {
        try
        {
            UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
            uploadNotificationConfig.setTitle(context.getText(R.string.upload_scv).toString());
            uploadNotificationConfig.setInProgressMessage(context.getText(R.string.uploadind).toString() + " " + Placeholders.PROGRESS);
            uploadNotificationConfig.setCompletedMessage(context.getText(R.string.comleted_scv).toString());
            uploadNotificationConfig.setErrorMessage(context.getText(R.string.error).toString());
            uploadNotificationConfig.setRingToneEnabled(false);

            String uploadId = UUID.randomUUID().toString();
            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(context, uploadId, url);

            uploadRequest.addFileToUpload(path, "scv");
            uploadRequest.setMaxRetries(2);
            uploadRequest.setNotificationConfig(uploadNotificationConfig);
            uploadRequest.startUpload();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
