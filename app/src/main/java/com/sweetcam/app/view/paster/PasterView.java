package com.sweetcam.app.view.paster;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.sweetcam.app.R;
import com.sweetcam.app.view.paster.event.DeleteEvent;
import com.sweetcam.app.view.paster.event.HorizontalFlipEvent;
import com.sweetcam.app.view.paster.event.ZoomEvent;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PasterView extends FrameLayout {

    private final boolean showIcons;
    private final boolean showBorder;
    private final boolean bringToFrontCurrentSticker;

    @IntDef({
            ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON,
            ActionMode.CLICK
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface ActionMode {

        int NONE = 0;
        int DRAG = 1;
        int ZOOM_WITH_TWO_FINGER = 2;
        int ICON = 3;
        int CLICK = 4;
    }

    @IntDef(flag = true, value = {FLIP_HORIZONTALLY, FLIP_VERTICALLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flip {

    }

    private static final String TAG = "StickerView";

    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;

    public static final int FLIP_HORIZONTALLY = 1;
    public static final int FLIP_VERTICALLY = 1 << 1;

    private final List<Paster> pasters = new ArrayList<>();
    private final List<BitmapPaster> icons = new ArrayList<>(4);

    private final Paint borderPaint = new Paint();
    private final RectF stickerRect = new RectF();

    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();

    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    private PointF midPoint = new PointF();
    // endregion
    private final int touchSlop;

    private BitmapPaster currentIcon;
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;

    @ActionMode
    private int currentMode = ActionMode.NONE;

    private Paster handlingPaster;

    private boolean locked;
    private boolean constrained;

    private OnStickerOperationListener onStickerOperationListener;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    public PasterView(Context context) {
        this(context, null);
    }

    public PasterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
            bringToFrontCurrentSticker =
                    a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false);

            borderPaint.setAntiAlias(true);
            borderPaint.setColor(a.getColor(R.styleable.StickerView_borderColor, Color.BLACK));
            borderPaint.setAlpha(a.getInteger(R.styleable.StickerView_borderAlpha, 128));

            configDefaultIcons();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public void configDefaultIcons() {
        BitmapPaster deleteIcon = new BitmapPaster(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_close_white_18dp),
                BitmapPaster.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteEvent());
        BitmapPaster zoomIcon = new BitmapPaster(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp),
                BitmapPaster.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomEvent());
        BitmapPaster flipIcon = new BitmapPaster(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_flip_white_18dp),
                BitmapPaster.RIGHT_TOP);
        flipIcon.setIconEvent(new HorizontalFlipEvent());

        icons.clear();
        icons.add(deleteIcon);
        icons.add(zoomIcon);
        icons.add(flipIcon);
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]]. Does nothing if either of the specified
     * layers doesn't exist.
     */
    public void swapLayers(int oldPos, int newPos) {
        if (pasters.size() >= oldPos && pasters.size() >= newPos) {
            Collections.swap(pasters, oldPos, newPos);
            invalidate();
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]]. Does nothing if either of the specified layers doesn't
     * exist.
     */
    public void sendToLayer(int oldPos, int newPos) {
        if (pasters.size() >= oldPos && pasters.size() >= newPos) {
            Paster s = pasters.get(oldPos);
            pasters.remove(oldPos);
            pasters.add(newPos, s);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawStickers(canvas);
    }

    protected void drawStickers(Canvas canvas) {
        for (int i = 0; i < pasters.size(); i++) {
            Paster paster = pasters.get(i);
            if (paster != null) {
                paster.draw(canvas);
            }
        }

        if (handlingPaster != null && !locked && (showBorder || showIcons)) {

            getStickerPoints(handlingPaster, bitmapPoints);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint);
                canvas.drawLine(x1, y1, x3, y3, borderPaint);
                canvas.drawLine(x2, y2, x4, y4, borderPaint);
                canvas.drawLine(x4, y4, x3, y3, borderPaint);
            }

            //draw icons
            if (showIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (int i = 0; i < icons.size(); i++) {
                    BitmapPaster icon = icons.get(i);
                    switch (icon.getPosition()) {
                        case BitmapPaster.LEFT_TOP:

                            configIconMatrix(icon, x1, y1, rotation);
                            break;

                        case BitmapPaster.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            break;

                        case BitmapPaster.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            break;

                        case BitmapPaster.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            break;
                    }
                    icon.draw(canvas, borderPaint);
                }
            }
        }
    }

    protected void configIconMatrix(@NonNull BitmapPaster icon, float x, float y,
                                    float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) {
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return super.onTouchEvent(event);
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingPaster != null && isInStickerArea(handlingPaster, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingPaster != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingPaster);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }

        return true;
    }

    /**
     * @param event MotionEvent received from {@link #onTouchEvent)
     * @return true if has touch something
     */
    protected boolean onTouchDown(@NonNull MotionEvent event) {
        currentMode = ActionMode.DRAG;

        downX = event.getX();
        downY = event.getY();

        midPoint = calculateMidPoint();
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            currentMode = ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            handlingPaster = findHandlingSticker();
        }

        if (handlingPaster != null) {
            downMatrix.set(handlingPaster.getMatrix());
            if (bringToFrontCurrentSticker) {
                pasters.remove(handlingPaster);
                pasters.add(handlingPaster);
            }
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerTouchedDown(handlingPaster);
            }
        }

        if (currentIcon == null && handlingPaster == null) {
            return false;
        }
        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        long currentTime = SystemClock.uptimeMillis();

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingPaster != null) {
            currentIcon.onActionUp(this, event);
        }

        if (currentMode == ActionMode.DRAG
                && Math.abs(event.getX() - downX) < touchSlop
                && Math.abs(event.getY() - downY) < touchSlop
                && handlingPaster != null) {
            currentMode = ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(handlingPaster);
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(handlingPaster);
                }
            }
        }

        if (currentMode == ActionMode.DRAG && handlingPaster != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(handlingPaster);
            }
        }

        currentMode = ActionMode.NONE;
        lastClickTime = currentTime;
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (currentMode) {
            case ActionMode.NONE:
            case ActionMode.CLICK:
                break;
            case ActionMode.DRAG:
                if (handlingPaster != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingPaster.setMatrix(moveMatrix);
                    if (constrained) {
                        constrainSticker(handlingPaster);
                    }
                }
                break;
            case ActionMode.ZOOM_WITH_TWO_FINGER:
                if (handlingPaster != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                            midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingPaster.setMatrix(moveMatrix);
                }

                break;

            case ActionMode.ICON:
                if (handlingPaster != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(handlingPaster, event);
    }

    public void zoomAndRotateSticker(@Nullable Paster paster, @NonNull MotionEvent event) {
        if (paster != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingPaster.setMatrix(moveMatrix);
        }
    }

    protected void constrainSticker(@NonNull Paster paster) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = getHeight();
        paster.getMappedCenterPoint(currentCenterPoint, point, tmp);
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }

        paster.getMatrix().postTranslate(moveX, moveY);
    }

    @Nullable
    protected BitmapPaster findCurrentIconTouched() {
        for (BitmapPaster icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched Sticker
     **/
    @Nullable
    protected Paster findHandlingSticker() {
        for (int i = pasters.size() - 1; i >= 0; i--) {
            if (isInStickerArea(pasters.get(i), downX, downY)) {
                return pasters.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull Paster paster, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return paster.contains(tmp);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        if (handlingPaster == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        handlingPaster.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * calculate Distance in two fingers
     **/
    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        for (int i = 0; i < pasters.size(); i++) {
            Paster paster = pasters.get(i);
            if (paster != null) {
                transformSticker(paster);
            }
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller This method is to transform it to fit step 1：let the center of
     * the sticker image is coincident with the center of the View. step 2：Calculate the zoom and zoom
     **/
    protected void transformSticker(@Nullable Paster paster) {
        if (paster == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }

        sizeMatrix.reset();

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = paster.getWidth();
        float stickerHeight = paster.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (width < height) {
            scaleFactor = width / stickerWidth;
        } else {
            scaleFactor = height / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);

        paster.getMatrix().reset();
        paster.setMatrix(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingPaster, direction);
    }

    public void flip(@Nullable Paster paster, @Flip int direction) {
        if (paster != null) {
            paster.getCenterPoint(midPoint);
            if ((direction & FLIP_HORIZONTALLY) > 0) {
                paster.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                paster.setFlippedHorizontally(!paster.isFlippedHorizontally());
            }
            if ((direction & FLIP_VERTICALLY) > 0) {
                paster.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                paster.setFlippedVertically(!paster.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(paster);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable Paster paster) {
        return replace(paster, true);
    }

    public boolean replace(@Nullable Paster paster, boolean needStayState) {
        if (handlingPaster != null && paster != null) {
            float width = getWidth();
            float height = getHeight();
            if (needStayState) {
                paster.setMatrix(handlingPaster.getMatrix());
                paster.setFlippedVertically(handlingPaster.isFlippedVertically());
                paster.setFlippedHorizontally(handlingPaster.isFlippedHorizontally());
            } else {
                handlingPaster.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (width - handlingPaster.getWidth()) / 2f;
                float offsetY = (height - handlingPaster.getHeight()) / 2f;
                paster.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    scaleFactor = width / handlingPaster.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = height / handlingPaster.getDrawable().getIntrinsicHeight();
                }
                paster.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = pasters.indexOf(handlingPaster);
            pasters.set(index, paster);
            handlingPaster = paster;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable Paster paster) {
        if (pasters.contains(paster)) {
            pasters.remove(paster);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(paster);
            }
            if (handlingPaster == paster) {
                handlingPaster = null;
            }
            invalidate();

            return true;
        } else {
            Log.d(TAG, "remove: the sticker is not in this StickerView");

            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingPaster);
    }

    public void removeAllStickers() {
        pasters.clear();
        if (handlingPaster != null) {
            handlingPaster.release();
            handlingPaster = null;
        }
        invalidate();
    }

    @NonNull
    public PasterView addSticker(@NonNull Paster paster) {
        return addSticker(paster, Paster.Position.CENTER);
    }

    public PasterView addSticker(@NonNull final Paster paster,
                                 final @Paster.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(paster, position);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    addStickerImmediately(paster, position);
                }
            });
        }
        return this;
    }

    protected void addStickerImmediately(@NonNull Paster paster, @Paster.Position int position) {
        setStickerPosition(paster, position);

        float scaleFactor, widthScaleFactor, heightScaleFactor;

        widthScaleFactor = (float) getWidth() / paster.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) getHeight() / paster.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;

        paster.getMatrix()
                .postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        handlingPaster = paster;
        pasters.add(paster);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(paster);
        }
        invalidate();
    }

    protected void setStickerPosition(@NonNull Paster paster, @Paster.Position int position) {
        float width = getWidth();
        float height = getHeight();
        float offsetX = width - paster.getWidth();
        float offsetY = height - paster.getHeight();
        if ((position & Paster.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & Paster.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & Paster.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & Paster.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }
        paster.getMatrix().postTranslate(offsetX, offsetY);
    }

    @NonNull
    public float[] getStickerPoints(@Nullable Paster paster) {
        float[] points = new float[8];
        getStickerPoints(paster, points);
        return points;
    }

    public void getStickerPoints(@Nullable Paster paster, @NonNull float[] dst) {
        if (paster == null) {
            Arrays.fill(dst, 0);
            return;
        }
        paster.getBoundPoints(bounds);
        paster.getMappedPoints(dst, bounds);
    }

    public void save(@NonNull File file) {
        try {
            PasterUtils.saveImageToGallery(file, createBitmap());
            PasterUtils.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            //
        }
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        handlingPaster = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return pasters.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    @NonNull
    public PasterView setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
        return this;
    }

    @NonNull
    public PasterView setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
        return this;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    @NonNull
    public PasterView setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
        return this;
    }

    @NonNull
    public PasterView setOnStickerOperationListener(
            @Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @Nullable
    public Paster getCurrentSticker() {
        return handlingPaster;
    }

    @NonNull
    public List<BitmapPaster> getIcons() {
        return icons;
    }

    public void setIcons(@NonNull List<BitmapPaster> icons) {
        this.icons.clear();
        this.icons.addAll(icons);
        invalidate();
    }

    public interface OnStickerOperationListener {

        void onStickerAdded(@NonNull Paster paster);

        void onStickerClicked(@NonNull Paster paster);

        void onStickerDeleted(@NonNull Paster paster);

        void onStickerDragFinished(@NonNull Paster paster);

        void onStickerTouchedDown(@NonNull Paster paster);

        void onStickerZoomFinished(@NonNull Paster paster);

        void onStickerFlipped(@NonNull Paster paster);

        void onStickerDoubleTapped(@NonNull Paster paster);
    }
}
