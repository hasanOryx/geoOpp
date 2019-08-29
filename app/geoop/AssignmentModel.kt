package com.oryx.geoop

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//import javax.persistence.Convert

@Entity
data class Assignment(
    @PrimaryKey(autoGenerate = true) val tid: Int,

   // @PrimaryKey val uid: Int,
 //   @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "task") val task: String?,
    @ColumnInfo(name = "details") val details: String?,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "status") var status: String?,
    @ColumnInfo(name = "statusNotes") var statusNotes: String?,

   // @TypeConverters(Converters::class)
    @ColumnInfo(name = "datePicker") val datePicker: String?, // LocalDate?,
    @ColumnInfo(name = "timePicker") val timePicker: String?, //LocalTime?
    @ColumnInfo(name = "lastUpdate") val lastUpdate: String?,
    @ColumnInfo(name = "customer") val customer: String?,
    @ColumnInfo(name = "mobile") val mobile: Long
)