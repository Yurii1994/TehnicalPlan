package com.muv.technicalplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleImage {
    Context context;

    public CircleImage(Context context)
    {
        this.context = context;
    }

    public Bitmap transform(Bitmap source)
    {
        try {

            int width = source.getWidth();
            int height = source.getHeight();

            int resizeWidth = 0;
            int resizeHeight = 0;

            if (width > 1000 || height > 1000)
            {
                int count = 0;
                while (true)
                {
                    if (count > 0)
                    {
                        resizeWidth = resizeWidth / 2;
                        resizeHeight = resizeHeight / 2;
                    }
                    else
                    {
                        resizeWidth = width / 2;
                        resizeHeight = height / 2;
                    }
                    count++;
                    if (resizeWidth < 1000 & resizeHeight < 1000)
                    {
                        break;
                    }
                }
            }
            else
            {
                resizeWidth = width;
                resizeHeight = height;
            }

            Bitmap resizeBm = Bitmap.createScaledBitmap(source, resizeWidth,
                    resizeHeight, false);

            int size = Math.min(resizeBm.getWidth(), resizeBm.getHeight());

            int x = (resizeBm.getWidth() - size) / 2;
            int y = (resizeBm.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap
                    .createBitmap(resizeBm, x, y, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    squaredBitmap.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            return bitmap;
        }
        catch (Exception e)
        {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_img);
        }
    }

}
