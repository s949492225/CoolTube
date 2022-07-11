package com.syiyi.cooltube.api

import com.syiyi.cooltube.model.Channel
import com.syiyi.cooltube.model.CommentsPage
import com.syiyi.cooltube.model.Instances
import com.syiyi.cooltube.model.Login
import com.syiyi.cooltube.model.Message
import com.syiyi.cooltube.model.Playlist
import com.syiyi.cooltube.model.PlaylistId
import com.syiyi.cooltube.model.Playlists
import com.syiyi.cooltube.model.SearchResult
import com.syiyi.cooltube.model.Segments
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.model.Streams
import com.syiyi.cooltube.model.Subscribe
import com.syiyi.cooltube.model.Subscribed
import com.syiyi.cooltube.model.Subscription
import com.syiyi.cooltube.model.Token
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String): List<StreamItem>

    @GET("streams/{videoId}")
    suspend fun getStreams(@Path("videoId") videoId: String): Streams

    @GET("comments/{videoId}")
    suspend fun getComments(@Path("videoId") videoId: String): CommentsPage

    @GET("sponsors/{videoId}")
    suspend fun getSegments(
        @Path("videoId") videoId: String,
        @Query("category") category: String
    ): Segments

    @GET("nextpage/comments/{videoId}")
    suspend fun getCommentsNextPage(
        @Path("videoId") videoId: String,
        @Query("nextpage") nextPage: String
    ): CommentsPage

    @GET("search")
    suspend fun getSearchResults(
        @Query("q") searchQuery: String,
        @Query("filter") filter: String
    ): SearchResult

    @GET("nextpage/search")
    suspend fun getSearchResultsNextPage(
        @Query("q") searchQuery: String,
        @Query("filter") filter: String,
        @Query("nextpage") nextPage: String
    ): SearchResult

    @GET("suggestions")
    suspend fun getSuggestions(@Query("query") query: String): List<String>

    @GET("channel/{channelId}")
    suspend fun getChannel(@Path("channelId") channelId: String): Channel

    @GET("nextpage/channel/{channelId}")
    suspend fun getChannelNextPage(
        @Path("channelId") channelId: String,
        @Query("nextpage") nextPage: String
    ): Channel

    @GET("playlists/{playlistId}")
    suspend fun getPlaylist(@Path("playlistId") playlistId: String): Playlist

    @GET("nextpage/playlists/{playlistId}")
    suspend fun getPlaylistNextPage(
        @Path("playlistId") playlistId: String,
        @Query("nextpage") nextPage: String
    ): Playlist

    @POST("login")
    suspend fun login(@Body login: Login): Token

    @POST("register")
    suspend fun register(@Body login: Login): Token

    @GET("feed")
    suspend fun getFeed(@Query("authToken") token: String?): List<StreamItem>

    @GET("subscribed")
    suspend fun isSubscribed(
        @Query("channelId") channelId: String,
        @Header("Authorization") token: String
    ): Subscribed

    @GET("subscriptions")
    suspend fun subscriptions(@Header("Authorization") token: String): List<Subscription>

    @POST("subscribe")
    suspend fun subscribe(
        @Header("Authorization") token: String,
        @Body subscribe: Subscribe
    ): Message

    @POST("unsubscribe")
    suspend fun unsubscribe(
        @Header("Authorization") token: String,
        @Body subscribe: Subscribe
    ): Message

    @POST("import")
    suspend fun importSubscriptions(
        @Query("override") override: Boolean,
        @Header("Authorization") token: String,
        @Body channels: List<String>
    ): Message

    @POST("import/playlist")
    suspend fun importPlaylist(
        @Header("Authorization") token: String,
        @Body playlistId: PlaylistId
    ): Message

    @GET("user/playlists")
    suspend fun playlists(@Header("Authorization") token: String): List<Playlists>

    @POST("user/playlists/delete")
    suspend fun deletePlaylist(
        @Header("Authorization") token: String,
        @Body playlistId: PlaylistId
    ): Message

    @POST("user/playlists/create")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Body name: Playlists
    ): PlaylistId

    @POST("user/playlists/add")
    suspend fun addToPlaylist(
        @Header("Authorization") token: String,
        @Body playlistId: PlaylistId
    ): Message

    @POST("user/playlists/remove")
    suspend fun removeFromPlaylist(
        @Header("Authorization") token: String,
        @Body playlistId: PlaylistId
    ): Message

    // only for fetching servers list
    @GET
    suspend fun getInstances(@Url url: String): List<Instances>
}
