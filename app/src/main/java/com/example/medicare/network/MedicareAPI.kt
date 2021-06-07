package com.example.medicare.network

import com.example.medicare.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MedicareAPI {

    @POST("login")
    fun login(@Body user: UserCredential): Call<AuthUser>

    @GET("user")
    fun getUsers(): Call<List<User>>

    @GET("client")
    fun getClients(): Call<List<Client>>

    @GET("client/{id}")
    fun getClientsById(@Path("id") id: Int): Call<Client>


    @GET("product/{id}")
    fun getProductById(@Path("id") id: Int): Call<Product>


    @GET("userClients/{id}")
    fun getUserClientsById(@Path("id") id: Int): Call<List<UserClients>>

    @GET("clientProduct/{id}")
    fun getClientProductById(@Path("id") id: Int): Call<List<ClientProducts>>

    @GET("productDetails/{id}")
    fun getProductDetailsById(@Path("id") id: Int): Call<List<ProductDetail>>

    @GET("category")
    fun getCategories(): Call<List<Category>>


    @GET("parts/{id}")
    fun getPartById(@Path("id") id: Int): Call<Part>

    @GET("partsByIdExtended/{id}")
    fun getPartByIdExtended(@Path("id") id: Int): Call<MutableList<Part>>


    @GET("productsByClientId/{id}")
    fun getProductsByClientId(@Path("id") id: Int): Call<List<ClientProducts>>


    @GET("subCategoriesByParentId/{id}")
    fun getSubCategoriesByParentId(@Path("id") id: Int): Call<List<Category>>


    @GET("clientsByUserId/{id}")
    fun getClientsByUserId(@Path("id") id: Int): Call<List<Client>>

    @GET("partsByCategoryAndSubCategoryId/{category_id},{sub_category_id}")
    fun getPartsByCategoryAndSubCategoryId(@Path("category_id") category_id: Int,@Path("sub_category_id") sub_category_id: Int): Call<List<Part>>

    @GET("getPartSize/{service_id},{limb_id}")
    fun getPartSizeByIDs(@Path("service_id") service_id: Int,@Path("limb_id") limb_id: Int): Call<List<LimbService>>


}