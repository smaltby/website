package com.seanmaltby.website.controller

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import com.seanmaltby.website.model.Post
import com.seanmaltby.website.service.MainService

@Controller
@RequestMapping("/admin")
class AdminController(var mainService: MainService) {
    @GetMapping
    fun index(model: Model): String {
        return "admin-home"
    }

    @GetMapping("/new-post")
    fun newPost(model: Model) : String {
        model.addAttribute("post", Post())

        return "post-editor"
    }

    @GetMapping("/posts/{postId}/edit")
    fun editPost(model: Model, @PathVariable postId: Int) : String {
        val post = mainService.postRepository.findByIdOrNull(postId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        model.addAttribute("post", post)

        return "post-editor"
    }

    @PostMapping("/save-post")
    fun savePost(model: Model, @ModelAttribute post: Post) : String {
        val id = mainService.savePost(post)

        return "redirect:/posts/$id"
    }

    @PostMapping("/posts/{postId}/delete")
    fun deletePost(model: Model, @PathVariable postId: Int) : String {
        if (!mainService.deletePost(postId)) {
            // TODO handle better
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        return "redirect:/posts"
    }

    @GetMapping("/resources")
    fun resources(model: Model) : String {
        model.addAttribute("resources", mainService.getResources())

        return "resources"
    }

    @GetMapping("/new-resource")
    fun resourceUploader(model: Model) : String {
        model.addAttribute("action", "/admin/upload-resource")

        return "resource-uploader"
    }

    @GetMapping("/resources/{resourceId}/re-upload")
    fun resourceReUploader(model: Model, @PathVariable resourceId: String) : String {
        model.addAttribute("action", "/admin/resources/$resourceId/re-upload")

        return "resource-uploader"
    }


    @PostMapping("/upload-resource")
    fun saveResource(model: Model, @RequestParam("file") file: MultipartFile) : String {
        val filename = file.originalFilename ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename required")

        mainService.uploadResource(file.inputStream, filename)

        return "redirect:/admin/resources"
    }

    @PostMapping("/resources/{resourceId}/re-upload")
    fun reUploadResource(model: Model, @PathVariable resourceId: String, @RequestParam("file") file: MultipartFile): String {
        val filename = file.originalFilename ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename required")

        mainService.reUploadResource(resourceId, file.inputStream, filename)

        return "redirect:/admin/resources"
    }

    @PostMapping("/resources/{resourceId}/delete")
    fun deleteResource(model: Model, @PathVariable resourceId: String): String {
        mainService.deleteResource(resourceId)

        return "redirect:/admin/resources"
    }
}