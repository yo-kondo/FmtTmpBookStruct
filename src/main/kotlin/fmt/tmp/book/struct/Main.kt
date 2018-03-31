package fmt.tmp.book.struct

import com.google.gson.Gson
import java.io.File

/**
 * エントリポイント
 */
fun main(args: Array<String>) {
    val conf = readConfig()
    val mdFileList = getMdFileList(conf.repositoryPath)
    changeImageLink(mdFileList)
    copyFile(mdFileList, conf.repositoryPath)
}

/**
 * 設定ファイルを読み込んで返します。
 */
private fun readConfig(): ConfigData {
    // https://qiita.com/devneko/items/93ee1212ce189f910891
    val source = File("config.json").readText(Charsets.UTF_8)
    return Gson().fromJson(source, ConfigData::class.java)
}

/**
 * Markdownファイルの一覧を取得して返します。
 */
private fun getMdFileList(repPath: String): List<File> {
    val dir = File(repPath)

    // ファイル名の条件を指定してファイルの一覧を取得
    val list = dir.listFiles().filter { it.name.endsWith(".md") }

    // File.listFiles().filter{}で、List<File!>が返ってくるので、nullチェックを行う。
    // Type!はPlatformTypeと言う。
    val rtnList = mutableListOf<File>()
    for (l in list) {
        if (l != null) {
            rtnList.add(l)
        }
    }
    return rtnList
}

/**
 * Markdown内の画像ファイルへのリンクを修正します。
 */
private fun changeImageLink(mdFileList: List<File>) {
    for (mdFile in mdFileList) {
        // 画像のディレクトリ
        val imageDir = Regex("""^[0-9]{4}""").find(mdFile.name)?.value
                ?: throw Exception("Markdownファイルの先頭に連番がついていません。filename=${mdFile.name}")

        val text = mdFile.readText()
                .replace("![](/img/$imageDir/", "![](")
        mdFile.writeText(text)
    }
}

/**
 * ファイルをコピーします。
 */
private fun copyFile(mdFileList: List<File>, repPath: String) {
    for (mdFile in mdFileList) {
        val lines = mdFile.readLines()
                .filter { it.startsWith("|") }

        // 読了日（年）
        val readingYear = lines
                .first { it.contains("読了日") }
                .split("|")[2]
                .toDate().year
        // ISBN
        val isbn = getIsbn(lines)
        // 画像のディレクトリ
        val imageDir = Regex("""^[0-9]{4}""").find(mdFile.name)?.value
                ?: throw Exception("Markdownファイルの先頭に連番がついていません。filename=${mdFile.name}")

        // 画像コピー
        val sourceImagePath = File("$repPath/$imageDir")
        val targetImagePath = File("$repPath/md/$readingYear/$isbn/")
        sourceImagePath.copyRecursively(targetImagePath, true)

        // Markdownコピー
        // ファイル名の先頭についている9999_を消す
        val renameFile = mdFile.name.replace("${imageDir}_", "")
        val targetPath = File("$repPath/md/$readingYear/$isbn/$renameFile")
        mdFile.copyTo(targetPath, true)
    }
}

/**
 * ISBNを取得します。
 */
private fun getIsbn(lines: List<String>): String {
    val isbn13 = lines
            .first { it.contains("ISBN-13") }
            .split("|")[2]
            .let { if (it != "－") it else "" }
    val isbn10 = lines
            .first { it.contains("ISBN-10") }
            .split("|")[2]
            .let { if (it != "－") it else "" }
    val asin = lines
            .firstOrNull { it.contains("ASIN") }
            ?.split("|")?.get(2)
            .let { if (it != "－") it else "" }
            ?: ""
    return when {
        isbn13.isNotEmpty() -> isbn13
        isbn10.isNotEmpty() -> isbn10
        else -> asin
    }
}
