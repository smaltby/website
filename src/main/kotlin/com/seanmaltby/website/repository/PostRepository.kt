package com.seanmaltby.website.repository

import org.springframework.data.repository.PagingAndSortingRepository
import com.seanmaltby.website.model.Post

interface PostRepository : PagingAndSortingRepository<Post, Int>