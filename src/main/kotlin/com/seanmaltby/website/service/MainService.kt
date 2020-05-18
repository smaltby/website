package com.seanmaltby.website.service

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.seanmaltby.website.dto.PostDTO
import com.seanmaltby.website.dto.PostSummaryDTO
import com.seanmaltby.website.dto.ResourceDTO
import com.seanmaltby.website.model.Post
import com.seanmaltby.website.model.Tag
import com.seanmaltby.website.repository.PostRepository
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList


@Service
@Transactional
class MainService(var postRepository: PostRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(MainService::class.java)
    }

    @Value("\${dynamic-resource-location}")
    private lateinit var dynamicResourceLocation: String

    fun getPostSummaries(page: Int, pageSize: Int): List<PostSummaryDTO> {
        val pageOfPosts = postRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdAt").descending()))

        val postSummaries = LinkedList<PostSummaryDTO>()
        for (post in pageOfPosts) {
            val formattedCreatedAt = SimpleDateFormat("yyyy-MM-dd").format(post.createdAt)
            val tags = post.tags.stream().map(Tag::name).collect(Collectors.toList())

            postSummaries.add(PostSummaryDTO(post.id, post.title, formattedCreatedAt, tags))
        }

        return postSummaries
    }

    fun getPost(id: Int): PostDTO? {
        val post = postRepository.findByIdOrNull(id) ?: return null

        val formattedCreatedAt = SimpleDateFormat("yyyy-MM-dd").format(post.createdAt)
        val tags = post.tags.stream().map(Tag::name).collect(Collectors.toList())

        return PostDTO(post.id, post.title, post.content, formattedCreatedAt, tags)
    }

    fun savePost(post: Post) : Int {
        // If the post exists, edit
        val existingPost = postRepository.findByIdOrNull(post.id)
        if (existingPost != null) {
            existingPost.title = post.title
            existingPost.content = post.content

            postRepository.save(existingPost)

            logger.info("Edited post {}, titled {}", post.id, post.title)

            return post.id
        }

        // Otherwise, save a new post
        val savedPost = postRepository.save(post)

        logger.info("Saved new post {}, titled {}", savedPost.id, savedPost.title)

        return savedPost.id
    }

    fun deletePost(postId: Int) : Boolean {
        val post = postRepository.findByIdOrNull(postId) ?: return false

        postRepository.delete(post)

        logger.info("Post {} deleted, titled {}", post.id, post.title)

        return true
    }

    fun uploadResource(resourceInputStream: InputStream, fileName: String) {
        val id = UUID.randomUUID().toString()

        uploadResourceInternal(id, resourceInputStream, fileName)
    }

    private fun uploadResourceInternal(resourceId: String, resourceInputStream: InputStream, fileName: String) {
        // Generate an unique identifier to store the resource under (prevents file name collisions)
        val resourcePath = Paths.get(dynamicResourceLocation).resolve(resourceId)

        // Buffer input stream and mark to allow for a call to reset if it is not a zip file
        val bufferedInputStream = BufferedInputStream(resourceInputStream)
        bufferedInputStream.mark(128)

        // Create zip input stream, which will be used both to check if the file is a zip file and to extract it's contents if it is
        val zipInputStream = ZipInputStream(bufferedInputStream)
        try {
            // If nextEntry is null on the first call, it's not a zip file, otherwise it's just the end of the zip file
            var firstEntry = true
            while (true) {
                val entry = zipInputStream.nextEntry
                if (entry == null) {
                    if (firstEntry) {
                        bufferedInputStream.reset()

                        val targetPath = resourcePath.resolve(fileName)
                        Files.createDirectories(targetPath.parent)
                        Files.copy(bufferedInputStream, targetPath)
                        logger.info("Resource uploaded to {}", targetPath.toAbsolutePath())
                    }
                    break
                }
                firstEntry = false

                if (entry.isDirectory) {
                    continue
                }

                val targetPath = resourcePath.resolve(entry.name)
                Files.createDirectories(targetPath.parent)
                Files.copy(zipInputStream, targetPath)
                logger.info("Resource uploaded to {}", targetPath.toAbsolutePath())
            }
        } catch (e: IOException) {
            logger.error("Error while uploading resource", e)
        } finally {
            bufferedInputStream.close()
        }
    }

    fun deleteResource(resourceId: String) {
        // TODO handle resourceId not found
        val resourceDirectory = File(dynamicResourceLocation).resolve(resourceId)

        FileUtils.deleteDirectory(resourceDirectory)

        logger.info("Resource {} deleted", resourceId)
    }

    fun reUploadResource(resourceId: String, resourceInputStream: InputStream, fileName: String) {
        deleteResource(resourceId)

        uploadResourceInternal(resourceId, resourceInputStream, fileName)
    }

    fun getResources(): List<ResourceDTO> {
        val baseResourceDirectory = File(dynamicResourceLocation)

        val resourceDirectories = baseResourceDirectory.listFiles(File::isDirectory) ?: emptyArray()

        val resources = ArrayList<ResourceDTO>()
        for (resourceDirectory in resourceDirectories) {
            val items = ArrayList<String>()

            FileUtils.listFiles(resourceDirectory, TrueFileFilter.TRUE, TrueFileFilter.TRUE).forEach {
                items.add(it.relativeTo(baseResourceDirectory).invariantSeparatorsPath)
            }

            resources.add(ResourceDTO(resourceDirectory.name, items))
        }

        return resources
    }
}