package com.mercadopago.android.px.internal.features.one_tap.slider.pager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import com.mercadopago.android.px.R;

public final class ScrollingPagerIndicator extends View {

    private int infiniteDotCount;

    private final int dotNormalSize;
    private final int dotSelectedSize;
    private final int spaceBetweenDotCenters;
    private int visibleDotCount;
    private final int visibleDotThreshold;

    private float visibleFramePosition;
    private float visibleFrameWidth;

    private float firstDotOffset;
    private SparseArray<Float> dotScale;

    private int itemCount;

    private final Paint paint;
    private final ArgbEvaluator colorEvaluator = new ArgbEvaluator();

    @ColorInt
    private final int dotColor;

    @ColorInt
    private final int selectedDotColor;

    private final boolean looped;

    private Runnable attachRunnable;
    private ViewPagerAttacher currentAttacher;

    private boolean dotCountInitialized;

    public ScrollingPagerIndicator(final Context context) {
        this(context, null);
    }

    public ScrollingPagerIndicator(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingPagerIndicator(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.obtainStyledAttributes(
            attrs, R.styleable.PXScrollingPagerIndicator, defStyleAttr, R.style.PXScrollingPagerIndicator);
        dotColor = attributes.getColor(R.styleable.PXScrollingPagerIndicator_px_spi_dotColor, 0);
        selectedDotColor = attributes.getColor(R.styleable.PXScrollingPagerIndicator_px_spi_dotSelectedColor, dotColor);
        dotNormalSize = attributes.getDimensionPixelSize(R.styleable.PXScrollingPagerIndicator_px_spi_dotSize, 0);
        dotSelectedSize =
            attributes.getDimensionPixelSize(R.styleable.PXScrollingPagerIndicator_px_spi_dotSelectedSize, 0);
        spaceBetweenDotCenters =
            attributes.getDimensionPixelSize(R.styleable.PXScrollingPagerIndicator_px_spi_dotSpacing, 0) +
                dotNormalSize;
        looped = attributes.getBoolean(R.styleable.PXScrollingPagerIndicator_px_spi_looped, false);
        final int visibleDotCount = attributes.getInt(R.styleable.PXScrollingPagerIndicator_px_spi_visibleDotCount, 0);
        setVisibleDotCount(visibleDotCount);
        visibleDotThreshold = attributes.getInt(R.styleable.PXScrollingPagerIndicator_px_spi_visibleDotThreshold, 2);
        attributes.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);

        if (isInEditMode()) {
            setDotCount(visibleDotCount);
            onPageScrolled(visibleDotCount / 2, 0);
        }
    }

    /**
     * Sets visible dot count. Maximum number of dots which will be visible at the same time.
     * If pager has more pages than visible_dot_count, indicator will scroll to show extra dots.
     * Must be odd number.
     *
     * @param visibleDotCount visible dot count
     * @throws IllegalStateException when pager is already attached
     */
    public void setVisibleDotCount(final int visibleDotCount) {
        if (visibleDotCount % 2 == 0) {
            throw new IllegalArgumentException("visibleDotCount must be odd");
        }
        this.visibleDotCount = visibleDotCount;
        infiniteDotCount = visibleDotCount + 2;

        if (attachRunnable != null) {
            reattach();
        } else {
            requestLayout();
        }
    }

    /**
     * Attaches indicator to ViewPager
     *
     * @param pager pager to attach
     */
    public void attachToPager(@NonNull final ViewPager2 pager) {
        attachToPager(pager, new ViewPagerAttacher());
    }

    /**
     * Attaches to any custom pager
     *
     * @param pager pager to attach
     * @param attacher helper which should setup this indicator to work with custom pager
     */
    public void attachToPager(@NonNull final ViewPager2 pager, @NonNull final ViewPagerAttacher attacher) {
        detachFromPager();
        attacher.attachToPager(this, pager);
        currentAttacher = attacher;

        attachRunnable = () -> {
            itemCount = -1;
            attachToPager(pager, attacher);
        };
    }

    /**
     * Detaches indicator from pager.
     */
    public void detachFromPager() {
        if (currentAttacher != null) {
            currentAttacher.detachFromPager();
            currentAttacher = null;
            attachRunnable = null;
        }
        dotCountInitialized = false;
    }

    /**
     * Detaches indicator from pager and attaches it again.
     * It may be useful for refreshing after adapter count change.
     */
    public void reattach() {
        if (attachRunnable != null) {
            attachRunnable.run();
            invalidate();
        }
    }

    /**
     * This method must be called from ViewPager.OnPageChangeListener.onPageScrolled or from some
     * similar callback if you use custom PagerAttacher.
     *
     * @param page index of the first page currently being displayed
     * Page position+1 will be visible if offset is nonzero
     * @param offset Value from [0, 1) indicating the offset from the page at position
     */
    public void onPageScrolled(final int page, final float offset) {
        if (offset < 0 || offset > 1) {
            throw new IllegalArgumentException("Offset must be [0, 1]");
        } else if (page < 0 || page != 0 && page >= itemCount) {
            throw new IndexOutOfBoundsException("page must be [0, adapter.getItemCount())");
        }

        if (!looped || itemCount <= visibleDotCount && itemCount > 1) {
            dotScale.clear();

            scaleDotByOffset(page, offset);

            if (page < itemCount - 1) {
                scaleDotByOffset(page + 1, 1 - offset);
            } else if (itemCount > 1) {
                scaleDotByOffset(0, 1 - offset);
            }

            invalidate();
        }
        adjustFramePosition(offset, page);
        invalidate();
    }

    /**
     * Sets dot count
     *
     * @param count new dot count
     */
    public void setDotCount(final int count) {
        initDots(count);
    }

    /**
     * Sets currently selected position (according to your pager's adapter)
     *
     * @param position new current position
     */
    public void setCurrentPosition(final int position) {
        if (position != 0 && (position < 0 || position >= itemCount)) {
            throw new IndexOutOfBoundsException("Position must be [0, adapter.getItemCount()]");
        }
        if (itemCount == 0) {
            return;
        }
        adjustFramePosition(0, position);
        updateScaleInIdleState(position);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int measuredWidth = measureWidth();
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // Height
        final int measuredHeight = measureHeight(heightMode, heightSize, dotSelectedSize);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureHeight(final int heightMode, final int heightSize, final int desiredHeight) {
        final int measuredHeight;

        switch (heightMode) {
        case MeasureSpec.EXACTLY:
            measuredHeight = heightSize;
            break;
        case MeasureSpec.AT_MOST:
            measuredHeight = Math.min(desiredHeight, heightSize);
            break;
        case MeasureSpec.UNSPECIFIED:
        default:
            measuredHeight = desiredHeight;
            break;
        }
        return measuredHeight;
    }

    private int measureWidth() {
        // Width
        final int measuredWidth;
        // We ignore widthMeasureSpec because width is based on visibleDotCount
        if (isInEditMode()) {
            // Maximum width with all dots visible
            measuredWidth = (visibleDotCount - 1) * spaceBetweenDotCenters + dotSelectedSize;
        } else {
            measuredWidth = itemCount >= visibleDotCount
                ? (int) visibleFrameWidth
                : (itemCount - 1) * spaceBetweenDotCenters + dotSelectedSize;
        }
        return measuredWidth;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final int dotCount = getDotCount();
        if (dotCount < visibleDotThreshold) {
            return;
        }

        // Some empirical coefficients
        final float scaleDistance = (spaceBetweenDotCenters + (dotSelectedSize - dotNormalSize) / 2f) * 0.7f;
        final float smallScaleDistance = dotSelectedSize / 2f;
        final float centerScaleDistance = 6f / 7f * spaceBetweenDotCenters;

        final int firstVisibleDotPos = (int) (visibleFramePosition - firstDotOffset) / spaceBetweenDotCenters;
        int lastVisibleDotPos = firstVisibleDotPos
            + (int) (visibleFramePosition + visibleFrameWidth - getDotOffsetAt(firstVisibleDotPos))
            / spaceBetweenDotCenters;

        // If real dots count is less than we can draw inside visible frame, we move lastVisibleDotPos
        // to the last item
        if (firstVisibleDotPos == 0 && lastVisibleDotPos + 1 > dotCount) {
            lastVisibleDotPos = dotCount - 1;
        }

        for (int i = firstVisibleDotPos; i <= lastVisibleDotPos; i++) {
            final float dot = getDotOffsetAt(i);
            if (dot >= visibleFramePosition && dot < visibleFramePosition + visibleFrameWidth) {
                float diameter;
                final float scale;

                // Calculate scale according to current page position
                if (looped && itemCount > visibleDotCount) {
                    final float frameCenter = visibleFramePosition + visibleFrameWidth / 2;
                    if (dot >= frameCenter - centerScaleDistance
                        && dot <= frameCenter) {
                        scale = (dot - frameCenter + centerScaleDistance) / centerScaleDistance;
                    } else if (dot > frameCenter
                        && dot < frameCenter + centerScaleDistance) {
                        scale = 1 - (dot - frameCenter) / centerScaleDistance;
                    } else {
                        scale = 0;
                    }
                } else {
                    scale = getDotScaleAt(i);
                }
                diameter = dotNormalSize + (dotSelectedSize - dotNormalSize) * scale;

                // Additional scale for dots at corners
                if (itemCount > visibleDotCount) {
                    final float currentScaleDistance;
                    if (!looped && (i == 0 || i == dotCount - 1)) {
                        currentScaleDistance = smallScaleDistance;
                    } else {
                        currentScaleDistance = scaleDistance;
                    }

                    if (dot - visibleFramePosition < currentScaleDistance) {
                        final float calculatedDiameter = diameter * (dot - visibleFramePosition) / currentScaleDistance;
                        if (calculatedDiameter < diameter) {
                            diameter = calculatedDiameter;
                        }
                    } else if (dot - visibleFramePosition > canvas.getWidth() - currentScaleDistance) {
                        final float calculatedDiameter =
                            diameter * (-dot + visibleFramePosition + canvas.getWidth()) / currentScaleDistance;
                        if (calculatedDiameter < diameter) {
                            diameter = calculatedDiameter;
                        }
                    }
                }

                paint.setColor(calculateDotColor(scale));
                canvas.drawCircle(dot - visibleFramePosition,
                    getMeasuredHeight() / 2f,
                    diameter / 2,
                    paint);
            }
        }
    }

    @ColorInt
    private int calculateDotColor(final float dotScale) {
        return (Integer) colorEvaluator.evaluate(dotScale, dotColor, selectedDotColor);
    }

    private void updateScaleInIdleState(final int currentPos) {
        if (!looped || itemCount < visibleDotCount) {
            dotScale.clear();
            dotScale.put(currentPos, 1f);
            invalidate();
        }
    }

    private void initDots(final int itemCount) {
        if (this.itemCount == itemCount && dotCountInitialized) {
            return;
        }
        this.itemCount = itemCount;
        dotCountInitialized = true;
        dotScale = new SparseArray<>();

        if (itemCount < visibleDotThreshold) {
            requestLayout();
            invalidate();
            return;
        }

        firstDotOffset = looped && this.itemCount > visibleDotCount ? 0 : dotSelectedSize / 2f;
        visibleFrameWidth = (visibleDotCount - 1) * spaceBetweenDotCenters + dotSelectedSize;

        requestLayout();
        invalidate();
    }

    private int getDotCount() {
        if (looped && itemCount > visibleDotCount) {
            return infiniteDotCount;
        } else {
            return itemCount;
        }
    }

    private void adjustFramePosition(final float offset, final int pos) {
        if (itemCount <= visibleDotCount) {
            // Without scroll
            visibleFramePosition = 0;
        } else if (!looped) {
            // Not looped with scroll
            final float center = getDotOffsetAt(pos) + spaceBetweenDotCenters * offset;
            visibleFramePosition = center - visibleFrameWidth / 2;

            // Block frame offset near start and end
            final int firstCenteredDotIndex = visibleDotCount / 2;
            final float lastCenteredDot = getDotOffsetAt(getDotCount() - 1 - firstCenteredDotIndex);
            if (visibleFramePosition + visibleFrameWidth / 2 < getDotOffsetAt(firstCenteredDotIndex)) {
                visibleFramePosition = getDotOffsetAt(firstCenteredDotIndex) - visibleFrameWidth / 2;
            } else if (visibleFramePosition + visibleFrameWidth / 2 > lastCenteredDot) {
                visibleFramePosition = lastCenteredDot - visibleFrameWidth / 2;
            }
        } else {
            // Looped with scroll
            final float center = getDotOffsetAt(infiniteDotCount / 2) + spaceBetweenDotCenters * offset;
            visibleFramePosition = center - visibleFrameWidth / 2;
        }
    }

    private void scaleDotByOffset(final int position, final float offset) {
        if (dotScale == null || getDotCount() == 0) {
            return;
        }
        setDotScaleAt(position, 1 - Math.abs(offset));
    }

    private float getDotOffsetAt(final int index) {
        return firstDotOffset + index * spaceBetweenDotCenters;
    }

    private float getDotScaleAt(final int index) {
        final Float scale = dotScale.get(index);
        if (scale != null) {
            return scale;
        }
        return 0;
    }

    private void setDotScaleAt(final int index, final float scale) {
        if (scale == 0) {
            dotScale.remove(index);
        } else {
            dotScale.put(index, scale);
        }
    }
}
