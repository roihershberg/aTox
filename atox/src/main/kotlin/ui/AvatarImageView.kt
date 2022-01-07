package ltd.evilcorp.atox.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import ltd.evilcorp.atox.R
import ltd.evilcorp.atox.databinding.AvatarImageLayoutBinding
import ltd.evilcorp.core.vo.Contact

private const val STATUS_INDICATOR_SIZE_RATIO_WITH_AVATAR = 12f / 50

class AvatarImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val binding = AvatarImageLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var contact: Contact? = null

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

    fun setFrom(contact: Contact) {
        this.contact = contact
        assignInfo()
    }

    private fun assignInfo() = binding.run {
        contact?.run {
            statusIndicator.setColorFilter(colorByContactStatus(context, this))

            if (avatarUri.isNotEmpty()) {
                avatarImage.setImageURI(Uri.parse(avatarUri))
                invalidate()
                requestLayout()
            } else {
                doOnLayout {
                    min(width, height).let { size ->
                        if (size != 0) {
                            avatarImage.setImageBitmap(AvatarFactory.create(resources, name, publicKey, size))
                            invalidate()
                            requestLayout()
                        }
                    }
                }
            }
        }
    }

    private fun redrawStatusIndicator(viewSize: Int) = binding.run {
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

        doOnLayout {
            min(width, height).let { size ->
                if (size != 0) {
                    redrawStatusIndicator(size)
                }
            }
        }
    }
}

// Class for creating an avatar for contact and assigning it into an ImageView
private object AvatarFactory {

    private fun getInitials(name: String): String {
        val segments = name.split(" ")
        if (segments.size == 1) return segments.first().take(1)
        return segments.first().take(1) + segments[1][0]
    }

    // Method will create an avatar based on the initials of a name and a public key for the background color.
    fun create(resources: Resources, name: String, publicKey: String, size: Int): Bitmap {
        val defaultAvatarSize = resources.getDimension(R.dimen.default_avatar_size).toInt()
        val textScale = size.toFloat() / defaultAvatarSize

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val colors = resources.getIntArray(R.array.contactBackgrounds)
        val backgroundPaint = Paint().apply { color = colors[abs(publicKey.hashCode()).rem(colors.size)] }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = resources.getDimension(R.dimen.contact_avatar_placeholder_text) * textScale
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        }

        val textBounds = Rect()
        val initials = getInitials(name)
        textPaint.getTextBounds(initials, 0, initials.length, textBounds)
        canvas.drawRoundRect(rect, rect.bottom, rect.right, backgroundPaint)
        canvas.drawText(initials, rect.centerX(), rect.centerY() - textBounds.exactCenterY(), textPaint)

        return bitmap
    }
}
