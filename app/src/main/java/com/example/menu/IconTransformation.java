package com.example.menu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.service.autofill.Transformation;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.O_MR1)
public class IconTransformation implements Transformation {

    public Bitmap transform(Bitmap source) {

        Path path = new Path();
        path.addCircle(source.getWidth() / 2, source.getHeight() / 2, source.getWidth() / 2, Path.Direction.CCW);
        Bitmap answerBitMap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(answerBitMap);
        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return answerBitMap;
    }


    public String key() {
        return "icon";
    }
}