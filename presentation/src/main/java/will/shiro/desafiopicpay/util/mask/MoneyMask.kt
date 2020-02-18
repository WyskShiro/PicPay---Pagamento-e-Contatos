package will.shiro.desafiopicpay.util.mask

import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import will.shiro.desafiopicpay.R
import will.shiro.desafiopicpay.util.watcher.SimpleTextWatcher
import will.shiro.domain.util.extension.onlyNumbers
import java.lang.IndexOutOfBoundsException

class MoneyMask(
    private val editText: EditText,
    private val textView: TextView
) : SimpleTextWatcher() {
    var oldText = editText.text.toString()
    var isUpdating = false

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isUpdating) {
            isUpdating = false
        } else {
            val newText = formatAsMoney(s.toString().onlyNumbers())
            isUpdating = true
            editText.setText(newText)
            oldText = newText
            try {
                editText.setSelection(newText.length)
            } catch (e: IndexOutOfBoundsException) {
                editText.setSelection(editText.length())
            }
            changeInputColor(newText)
        }
    }

    private fun formatAsMoney(text: String): String {
        var newText = text
        when {
            newText.isEmpty() -> {
                newText = DEFAULT_NO_MONEY
            }
            newText.length == 1 -> {
                newText = "0,0$newText"
            }
            newText.length < 3 -> {
                newText = "0,$newText"
            }
            else -> {
                if (newText.startsWith("0")) {
                    newText = newText.removeRange(0, 1)
                }
                var symbolPosition = newText.length - DECIMAL_SEPARATOR_SIZE
                newText = insertSymbol(newText, symbolPosition)
                symbolPosition -= GROUP_SEPARATOR_SIZE
                while (symbolPosition > 0) {
                    newText = insertSymbol(newText, symbolPosition, GROUP_SEPARATOR)
                    symbolPosition -= GROUP_SEPARATOR_SIZE
                }
            }
        }
        return newText
    }

    private fun insertSymbol(
        text: String,
        position: Int,
        symbol: String = DECIMAL_SEPARATOR
    ): String {
        return text.substring(0 until position) +
                symbol + text.substring(position until text.length)
    }

    private fun changeInputColor(text: String) {
        val resourceColor = if (text == DEFAULT_NO_MONEY) {
            R.color.colorWhite40
        } else {
            R.color.colorGreen
        }
        editText.setTextColor(ContextCompat.getColor(editText.context, resourceColor))
        textView.setTextColor(ContextCompat.getColor(textView.context, resourceColor))
    }

    companion object {
        private const val DEFAULT_NO_MONEY = "0,00"
        private const val GROUP_SEPARATOR = "."
        private const val GROUP_SEPARATOR_SIZE = 3
        private const val DECIMAL_SEPARATOR = ","
        private const val DECIMAL_SEPARATOR_SIZE = 2
    }
}