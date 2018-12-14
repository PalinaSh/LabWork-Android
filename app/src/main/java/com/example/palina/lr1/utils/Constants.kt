package com.example.palina.lr1.utils

class Constants {
    companion object {
        const val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
        const val PERMISSIONS_REQUEST_CAMERA: Int = 2
        const val CAMERA_REQUEST_CODE: Int = 3
        const val GALLERY_REQUEST_CODE: Int = 4
        const val STORAGE_FILENAME: String = "links"
        const val SQLITEDATABASE_NAME: String = "RssCash"

        fun getTableName(url: String): String{
            if (url == "")
                return "default"
            return url.split(':')[1].split('/')[2].split('.')[0]
        }
    }
}