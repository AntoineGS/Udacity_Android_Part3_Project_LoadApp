package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText: String = ""
    private var myBackgroundColor: Int = 0
    private var progress: Float = 0f
    private var valueAnimator = ValueAnimator()
    private val textRect = Rect()
    private var animationDuration: Long = 0

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, newValue ->
        when (newValue) {
            ButtonState.Loading -> {
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = ValueAnimator.INFINITE
                    duration = animationDuration
                    start()
                }

                setText(context.getString(R.string.downloading))
                setBgColor(context.getColor(R.color.colorPrimaryDark))
                disableLoadingButton()
            }

            ButtonState.Completed -> {
                valueAnimator.cancel()
                resetProgress()
                setText(context.getString(R.string.download))
                setBgColor(context.getColor(R.color.colorPrimary))
                enableLoadingButton()
            }

            ButtonState.Clicked -> {
                // nothing to do
            }
        }
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonText = getString(R.styleable.LoadingButton_text).toString()
                animationDuration = getInt(R.styleable.LoadingButton_animation_duration,
                        1000).toLong()
            } finally {
                recycle()
            }
        }

        setText(buttonText)
    }

    // Used for the styling of the text...
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
        color = Color.WHITE
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    private val downloadingBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    private val downloadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        canvas.drawColor(myBackgroundColor)
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        canvas.drawRect(0f, 0f, backgroundWidth, backgroundHeight, backgroundPaint)

        if (buttonState == ButtonState.Loading) {
            var progressVal = progress * backgroundWidth
            canvas.drawRect(0f, 0f, progressVal, backgroundHeight, downloadingBackgroundPaint)

            val arcRectSize = backgroundHeight - paddingBottom.toFloat() - paddingTop.toFloat()
            progressVal = progress * 360f

            canvas.drawArc( backgroundWidth  - arcRectSize- paddingStart.toFloat(),
                paddingTop.toFloat(),
                backgroundWidth,
                arcRectSize,
                0f,
                progressVal,
                true,
                downloadingArcPaint)
        }
        val centerX = backgroundWidth / 2
        val centerY = backgroundHeight / 2 - textRect.centerY()

        canvas.drawText(buttonText, centerX, centerY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minimumWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun disableLoadingButton() {
        custom_button.isEnabled = false
    }

    private fun enableLoadingButton() {
        custom_button.isEnabled = true
    }

    // Used to provide a way to change the button state from the main activity
    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    //Sets the button text.
    private fun setText(newButtonText: String) {
        buttonText = newButtonText
        invalidate()
        requestLayout()
    }

    private fun setBgColor(newBackgroundColor: Int) {
        myBackgroundColor = newBackgroundColor
        invalidate()
        requestLayout()
    }

    private fun resetProgress() {
        progress = 0f
    }
}