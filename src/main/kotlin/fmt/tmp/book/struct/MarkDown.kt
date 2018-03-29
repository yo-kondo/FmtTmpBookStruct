package fmt.tmp.book.struct

import java.io.File
import java.time.LocalDate

/**
 * Markdownファイルクラス
 */
internal class MarkDown {
    /** 読了日 */
    var readingData: LocalDate = LocalDate.MIN
        private set

    /** 読了日（年） */
    var readingYear: String = ""
        private set

    /** ISBN */
    var isbn: String = ""
        private set

    /** 本のタイトル */
    var bookTitle: String = ""
        private set

    /** Markdownのリンク */
    var linkMarkdown: String = ""
        private set

    /** Markdownファイルのパス */
    var markdownPath: File? = null
        private set

    /** 画像ディレクトリのパス */
    var imageFile: File? = null
        private set

    /** 著者 */
    var author: String = ""
        private set

    /**
     * Markdownのデータを作成します。
     */
    fun create(file: File) {
        val lines = file.readLines().filter { it.startsWith("|") }

        var isbn13 = ""
        var isbn10 = ""
        var asin = ""
        for (li in lines) {
            // 読了日
            if (li.contains("読了日")) {
                readingData = li.split("|")[2].toDate()
            }

            // 読了年
            readingYear = readingData.year.toString()

            // 各ISBN
            if (li.contains("ISBN-13")) {
                val sp = li.split("|")
                isbn13 = if (sp[2] != "－") sp[2] else ""
            }
            if (li.contains("ISBN-10")) {
                val sp = li.split("|")
                isbn10 = if (sp[2] != "－") sp[2] else ""
            }
            if (li.contains("ASIN")) {
                val sp = li.split("|")
                asin = if (sp[2] != "－") sp[2] else ""
            }

            // 本のタイトル
            if (li.contains("Amazon")) {
                bookTitle = li.split("|")[2]
                        .replace("[", "")
                        .replace("]", "")
            }

            // 著者
            if (li.contains("著者")) {
                author = li.split("|")[2]
                        .split(",").first()
                        .replace("(著)", "")
            }
        }

        // ISBN
        isbn = when {
            isbn13.isNotEmpty() -> isbn13
            isbn10.isNotEmpty() -> isbn10
            else -> asin
        }

        // Markdownへのリンク

        // Markdownファイルのパス
        // 画像ディレクトリのパス
        TODO()
    }
}
