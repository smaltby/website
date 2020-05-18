package com.seanmaltby.website.model

import javax.persistence.*

@Entity
@Table
class Tag {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int = -1

    lateinit var postId: String

    lateinit var name: String
}