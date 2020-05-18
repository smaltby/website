package com.seanmaltby.website.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException
import com.seanmaltby.website.service.MainService

@Controller
class MainController(var mainService: MainService) {
    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("postSummaries", mainService.getPostSummaries(0, 5))

        return "home"
    }

    @GetMapping("/posts")
    fun posts(model: Model) : String {
        // TODO: Either handle pages or return all posts
        model.addAttribute("postSummaries", mainService.getPostSummaries(0, 100))

        return "posts"
    }

    @GetMapping("/posts/{id}")
    fun post(model: Model, @PathVariable id: Int): String {
        val post = mainService.getPost(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        model.addAttribute("post", post)

        return "post"
    }

    @GetMapping("/login")
    fun auth() : String {
        return "login"
    }
}