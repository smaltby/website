package com.seanmaltby.website.dto

data class PostDTO(val id: Int, val title: String, val content: String, val createdAt: String, val tags: List<String>)