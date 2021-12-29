package ltd.evilcorp.atox.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import ltd.evilcorp.atox.R
import ltd.evilcorp.atox.databinding.AvatarImageLayoutBinding

private const val STATUS_INDICATOR_SIZE_RATIO_WITH_AVATAR = 12f / 50

class AvatarImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding = AvatarImageLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    val avatarImage get() = binding.avatarImage

    val statusIndicator get() = binding.statusIndicator

    var statusIndicatorVisibility: Boolean = true
        set(value) = binding.run {
            statusIndicator.visibility = when (value) {
                true -> VISIBLE
                false -> GONE
            }
            field = value
            invalidate()
            requestLayout()
        }

    init {
        binding.run {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.AvatarImageView,
                0, 0
            ).apply {
                // Getting attributes from XML
                try {
                    statusIndicatorVisibility = getBoolean(
                        R.styleable.AvatarImageView_statusIndicatorVisibility,
                        true
                    )
                } finally {
                    recycle()
                }
            }
        }
    }

    private fun redrawStatusIndicator(viewSize: Int) {
        // Calculating status indicator size and margin in relation to the overall size
        val viewSizeFloat = viewSize.toFloat()
        val statusIndicatorSize = viewSizeFloat * STATUS_INDICATOR_SIZE_RATIO_WITH_AVATAR
        val avatarImageRadius = viewSizeFloat / 2
        val avatarImageDiagonal = sqrt(viewSizeFloat.pow(2) + viewSizeFloat.pow(2)) // Pythagorean theorem
        val avatarImageDistanceFromCorner = avatarImageDiagonal / 2 - avatarImageRadius
        val statusIndicatorDiagonal =
            sqrt(statusIndicatorSize.pow(2) + statusIndicatorSize.pow(2)) // Pythagorean theorem
        val statusIndicatorMarginDiagonal = (avatarImageDistanceFromCorner - statusIndicatorDiagonal / 2)
        val statusIndicatorMargin =
            sqrt(statusIndicatorMarginDiagonal.pow(2) / 2).toInt() // Pythagorean theorem

        statusIndicator.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomToBottom = R.id.parent
            endToEnd = R.id.parent
            width = statusIndicatorSize.toInt()
            height = statusIndicatorSize.toInt()
            setMargins(
                statusIndicatorMargin,
                statusIndicatorMargin,
                statusIndicatorMargin,
                statusIndicatorMargin,
            )
        }

        invalidate()
        requestLayout()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        min(w, h).let {
            if (it == 0) return
            else redrawStatusIndicator(it)
        }
    }
}
