package bonch.dev.school.networking

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitFactory {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com"

    fun makeRetrofitService(): RetrofitService {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        return retrofit.create(RetrofitService::class.java)
    }
}