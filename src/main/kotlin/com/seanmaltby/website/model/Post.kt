package com.seanmaltby.website.model

import java.util.*
import javax.persistence.*

@Entity
class Post {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int = -1

    var title: String = ""

    var content: String = ""

    var createdAt: Date = Date()

    var updatedAt: Date = Date()

    @OneToMany
    @JoinColumn(name = "postId")
    lateinit var tags: List<Tag>
}