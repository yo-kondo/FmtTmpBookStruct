package fmt.tmp.book.struct

import com.google.gson.Gson
import java.io.File

/**
 * エントリポイント
 */
fun main(args: Array<String>) {
    val conf = readConfig()
    val mdFileList = getMdFileList(conf.repositoryPath)

    val mdList = mutableListOf<MarkDown>()
    for (file in mdFileList) {
        val md = MarkDown()
        md.create(file)
        mdList.add(md)
    }
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
