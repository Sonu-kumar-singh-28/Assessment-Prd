package com.thethirdeye.esport.assessment

data class ResumeModel(
    val name: String? = "",
    val skills: List<String>? = emptyList(),
    val projects: List<String>? = emptyList()
)