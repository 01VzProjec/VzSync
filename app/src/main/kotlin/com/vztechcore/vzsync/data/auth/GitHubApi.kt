package com.vztechcore.vzsync.data.auth

import com.vztechcore.vzsync.data.repos.CreateRepoRequest
import com.vztechcore.vzsync.data.repos.GitHubRepoDto
import com.vztechcore.vzsync.data.repos.GitHubTreeDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authHeader: String,
    ): GitHubUserDto

    @GET("user/repos")
    suspend fun getUserRepos(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "updated",
        @Query("affiliation") affiliation: String = "owner,collaborator,organization_member",
    ): List<GitHubRepoDto>

    @GET("repos/{owner}/{repo}/git/trees/{branch}")
    suspend fun getRepoTree(
        @Header("Authorization") authHeader: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String,
        @Query("recursive") recursive: Int = 1,
    ): GitHubTreeDto

    @POST("user/repos")
    suspend fun createRepo(
        @Header("Authorization") authHeader: String,
        @Body body: CreateRepoRequest,
    ): GitHubRepoDto

    @DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepo(
        @Header("Authorization") authHeader: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    )
}