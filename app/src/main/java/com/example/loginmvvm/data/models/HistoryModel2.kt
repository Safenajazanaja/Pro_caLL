package com.example.loginmvvm.data.models


data class HistoryModel2(
    val datelong:Long?=null,
    val date: String? = null,
    val sumOrderByDate: Int,
    val orders: List<OrderModeldetail>
)
