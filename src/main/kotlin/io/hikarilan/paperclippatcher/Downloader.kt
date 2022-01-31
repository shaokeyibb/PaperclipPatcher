package io.hikarilan.paperclippatcher

sealed interface Downloader {

    fun getDownloadLink(version: String): String

}

object BMCLAPIOriginDownloader : Downloader {

    override fun getDownloadLink(version: String): String {
        return "https://bmclapi2.bangbang93.com/version/$version/server"
    }

}

object BMCLAPIMCBBSDownloader : Downloader {

    override fun getDownloadLink(version: String): String {
        return "https://download.mcbbs.net/version/$version/server"
    }

}