package com.junkfood.seal.util

import androidx.room.Room
import com.junkfood.seal.App.Companion.applicationScope
import com.junkfood.seal.App.Companion.context
import com.junkfood.seal.database.AppDatabase
import com.junkfood.seal.database.CommandTemplate
import com.junkfood.seal.database.CookieProfile
import com.junkfood.seal.database.DownloadedVideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DatabaseUtil {
    val format = Json { prettyPrint = true }
    private const val DATABASE_NAME = "app_database"
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, DATABASE_NAME
    ).build()
    private val dao = db.videoInfoDao()
    fun insertInfo(vararg infoList: DownloadedVideoInfo) {
        applicationScope.launch(Dispatchers.IO) {
            infoList.forEach { dao.deleteInfoByPathAndInsert(it) }
        }
    }

    fun getMediaInfo() = dao.getAllMedia()

    fun getTemplateFlow() = dao.getTemplateFlow()

    fun getCookiesFlow() = dao.getCookieProfileFlow()

    suspend fun getCookieById(id: Int) = dao.getCookieById(id)
    suspend fun deleteCookieProfile(profile: CookieProfile) = dao.deleteCookieProfile(profile)

    suspend fun insertCookieProfile(profile: CookieProfile) = dao.insertCookieProfile(profile)

    suspend fun updateCookieProfile(profile: CookieProfile) = dao.updateCookieProfile(profile)
    private suspend fun getTemplateList() = dao.getTemplateList()

    suspend fun deleteInfoListByIdList(idList: List<Int>, deleteFile: Boolean = false) =
        dao.deleteInfoListByIdList(idList, deleteFile)

    suspend fun getInfoById(id: Int): DownloadedVideoInfo = dao.getInfoById(id)
    suspend fun deleteInfoById(id: Int) = dao.deleteInfoById(id)

    suspend fun insertTemplate(commandTemplate: CommandTemplate): Long =
        dao.insertTemplate(commandTemplate)

    suspend fun updateTemplate(commandTemplate: CommandTemplate) {
        dao.updateTemplate(commandTemplate)
    }

    suspend fun deleteTemplateById(id: Int) = dao.deleteTemplateById(id)
    suspend fun exportTemplatesToJson(): String {
        return format.encodeToString(getTemplateList())
    }

    suspend fun importTemplatesFromJson(json: String): Int {
        val list = getTemplateList()
        var cnt = 0
        try {
            format.decodeFromString<List<CommandTemplate>>(json)
                .filter { !list.contains(it) }.run {
                    dao.importTemplates(this)
                    cnt = size
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cnt
    }

    private const val TAG = "DatabaseUtil"
}