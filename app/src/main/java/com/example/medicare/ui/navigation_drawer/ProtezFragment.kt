package com.example.medicare.ui.navigation_drawer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medicare.KEYS
import com.example.medicare.R
import com.example.medicare.model.ClientProducts
import com.example.medicare.model.Product
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProtezFragment : Fragment() {
    private final val medicareAPI : MedicareAPI = NetworkService.medicareAPI
    private var clientIdHeaderRemoved: Int = 1
    private lateinit var clientProducts: List<ClientProducts>
    private var products: MutableList<Product> = arrayListOf()
    private lateinit var productSpinner : Spinner
    private lateinit var productImage: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.protez_layout,container,false)
        productSpinner = root.findViewById(R.id.productSpinner)
        productImage = root.findViewById(R.id.productImage)
        return root
    }


    fun newInstance(id : Int): Fragment{
        val args = Bundle()
        args.putInt(KEYS.CLIENT_ID,id)
        val fragment = ProtezFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle?.get(KEYS.CLIENT_ID) != null){
            clientIdHeaderRemoved = bundle.getInt(KEYS.CLIENT_ID) % KEYS.CLIENT_ID_HEADER
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Main).launch {
            apiFunctionsRun()
        }
    }


    private suspend fun apiFunctionsRun(){
        Log.i("coroutines", "apiFunctionsRun: function launched")
        initializeProductIds()
        delay(1000)
        Log.i("coroutines", "apiFunctionsRun: ${clientProducts.size}")
        initializeProducts()
        delay(1000)
        initializeSpinner()
        Log.i("coroutines", "apiFunctionsRun: ${products.size}")
    }

    private suspend fun initializeSpinner() {
        val spinnerArray : MutableList<String> = ArrayList<String>()
        for (product in products){
            spinnerArray.add(product.name)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            productSpinner.adapter = adapter
        Log.i("coroutines", "initializeSpinner: asd")
        productSpinner.setSelection(0)
        if(products.size != 0){
            Glide.with(requireContext()).load(products.get(0).image)
                .into(productImage)
        }
    }

    private fun initializeProductIds() {
        medicareAPI.getClientProductById(clientIdHeaderRemoved).enqueue(object : Callback<List<ClientProducts>>{
            override fun onResponse(call: Call<List<ClientProducts>>, response: Response<List<ClientProducts>>) {
                if(response.isSuccessful){
                    clientProducts = response.body()!!
                }
            }
            override fun onFailure(call: Call<List<ClientProducts>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initializeProducts(){
        for(clientProduct in clientProducts){
            medicareAPI.getProductById(clientProduct.product_id).enqueue(object : Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful){
                        products.add(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}