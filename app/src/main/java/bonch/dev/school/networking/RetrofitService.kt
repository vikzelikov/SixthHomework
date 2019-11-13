package bonch.dev.school.networking

import bonch.dev.school.models.DataAlbum
import bonch.dev.school.models.DataPost
import bonch.dev.school.models.DataUser
import bonch.dev.school.realm.Photo
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @GET("/users")
    suspend fun getUsers(): Response<List<DataUser>>

    @GET("/albums")
    suspend fun getAlbums(@Query("userId") userId: Int): Response<List<DataAlbum>>

    @DELETE("/albums/{id}")
    fun deleteAlbum(@Path("id") id: Int): Call<Void>

    @GET("/photos")
    suspend fun getPhotos(): Response<List<Photo>>

    @FormUrlEncoded
    @POST("/posts")
    suspend fun getPost(@Field("title") title: String, @Field("body") body: String): Response<DataPost>
}
