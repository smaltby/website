package com.seanmaltby.website.repository

import org.springframework.data.repository.CrudRepository
import com.seanmaltby.website.model.Tag

interface TagRepository : CrudRepository<Tag, Int>