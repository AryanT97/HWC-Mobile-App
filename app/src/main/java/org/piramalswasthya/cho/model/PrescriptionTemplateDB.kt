package org.piramalswasthya.cho.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "Prescription_Template_DB")
@JsonClass(generateAdapter = true)
data class PrescriptionTemplateDB(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "user_id") val userID: Int,
    @ColumnInfo(name = "template_name") val templateName: String,
    @ColumnInfo(name = "drugId") val drugId: Int,
    @ColumnInfo(name = "frequency") val frequency: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "instruction") val instruction: String,
)
