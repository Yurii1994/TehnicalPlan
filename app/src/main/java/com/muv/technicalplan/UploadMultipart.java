package com.muv.technicalplan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.enterprise.EnterpriseActivity;
import com.muv.technicalplan.enterprise.FragmentPositionMap;
import com.muv.technicalplan.enterprise.FragmentSettings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.Placeholders;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.List;
import java.util.UUID;

public class UploadMultipart extends Activity
{
    private final String UPDATE = "com.muv.action.UPDATE";

    public void uploadImage(final Context context, String path, String url)
    {
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

                        }

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception exception) {

                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                            PicassoTools.clearCache(Picasso.with(context));
                            Intent intent = new Intent(UPDATE);
                            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            intent.putExtra("Update", "image");
                            context.sendBroadcast(intent);
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
            Intent intent = new Intent(UPDATE);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra("Update", "show_dialog");
            context.sendBroadcast(intent);

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
            uploadRequest.setDelegate(new UploadStatusDelegate() {
                @Override
                public void onProgress(UploadInfo uploadInfo) {

                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {
                    Intent intent = new Intent(UPDATE);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("Update", "dismiss_dialog");
                    context.sendBroadcast(intent);
                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse)
                {
                    Intent intent = new Intent(UPDATE);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("Update", "position");
                    context.sendBroadcast(intent);
                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {

                }
            });
            uploadRequest.startUpload();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
