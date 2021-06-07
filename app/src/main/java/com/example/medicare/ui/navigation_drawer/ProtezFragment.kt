package com.example.medicare.ui.navigation_drawer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.*
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.KEYS
import com.example.medicare.R
import com.example.medicare.adapter.PartAdapter
import com.example.medicare.model.*
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProtezFragment : Fragment(),View.OnClickListener {
    private val medicareAPI : MedicareAPI = NetworkService.medicareAPI
    private var clientIdHeaderRemoved: Int = 1
    private var products: List<ClientProducts> = arrayListOf()
    private lateinit var productSpinner : Spinner
    private lateinit var productImage: ImageView
    private lateinit var productDescription: TextView
    private var currentProductId: Int = 0
    private var currentProductServiceId: Int = 0
    private var productPartsExtended: MutableList<Part> = arrayListOf()
    private lateinit var partsRecyclerView: RecyclerView
    private var productCategory: MutableList<String> = arrayListOf()
    private var productCategoryParentIds: MutableList<String> = arrayListOf()
    private var categoryID = 1
    private var subCategoryID = 1
    private var serviceId = 1
    private lateinit var addButton: Button
    private lateinit var deleteButton: View
    private lateinit var buttonSubmit: View
    private lateinit var productCategorySpinner : Spinner
    private lateinit var productSubCategorySpinner : Spinner
    private lateinit var productPartSpinner : Spinner
    private lateinit var productPartParentIdSpinner : Spinner
    private lateinit var productCategoriesParentIdSpinner: Spinner
    private lateinit var productSubCategoryParentIdSpinner: Spinner
    var partIDToAdd: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.protez_layout,container,false)
        findByIDs(root)
        deleteButton = inflater.inflate(R.layout.part_item,null,false)
        addButton.setOnClickListener(this)
        deleteButton.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
        return root
    }

    companion object {
        fun newInstance(id : Int): Fragment{
            val args = Bundle()
            args.putInt(KEYS.CLIENT_ID,id)
            val fragment = ProtezFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle?.get(KEYS.CLIENT_ID) != null){
            clientIdHeaderRemoved = bundle.getInt(KEYS.CLIENT_ID) % KEYS.CLIENT_ID_HEADER
            Log.i("test", "onViewCreated: ${bundle.getInt(KEYS.CLIENT_ID)}")
        }

    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(IO).launch {
            apiFunctionsRun()
        }
    }

    private suspend fun apiFunctionsRun(){
        initializeCategoryLists()
        initializeProducts(clientIdHeaderRemoved)
        delay(600)
        CoroutineScope(Main).launch{
            initializeSpinner()
        }
        Log.i("coroutines", "apiFunctionsRun: ${products.size}")
    }

    private fun initializeProducts(client_id: Int) {
        Log.i("test", "onResponse: ${client_id}")

        medicareAPI.getProductsByClientId(client_id).enqueue(object: Callback<List<ClientProducts>>{
            override fun onResponse(
                call: Call<List<ClientProducts>>,
                response: Response<List<ClientProducts>>
            ) {
                if(response.isSuccessful){
                    products = response.body()!!
                    Log.i("test", "onResponse: ${products.size}")
                }
            }

            override fun onFailure(call: Call<List<ClientProducts>>, t: Throwable) {

            }
        })
    }

    private fun initializeSpinner() {
        if(products.isNotEmpty()){
            currentProductId = products[0].product_id

            initializeParts(currentProductId)
        }
        val spinnerArray : MutableList<String> = ArrayList<String>()
        for (product in products){
            spinnerArray.add(product.name)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            productSpinner.adapter = adapter
        productSpinner.setSelection(0)
        serviceId =  products[0].service_id
        if(products.isNotEmpty()){
            Glide.with(requireContext()).load(products[0].image)
                .into(productImage)
            productDescription.text = products[0].description
            currentProductServiceId = products[0].service_id
        }


        //Spinner item selected
        productSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Glide.with(requireContext()).load(products[position].image)
                    .placeholder(R.drawable.placeholder)
                    .into(productImage)
                serviceId = products[position].service_id
                productDescription.text = products[position].description
                currentProductId = products[position].product_id
                currentProductServiceId = products[position].service_id
                GlobalScope.launch {
                    initializeParts(currentProductId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun initializeParts(currentProductId: Int) {
        medicareAPI.getPartByIdExtended(currentProductId).enqueue(object: Callback<MutableList<Part>>{
            override fun onResponse(call: Call<MutableList<Part>>, response: Response<MutableList<Part>>) {
                if(response.isSuccessful){
                    productPartsExtended = response.body()!!
                    initializeRecyclerView()
                }
            }

            override fun onFailure(call: Call<MutableList<Part>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initializeRecyclerView() {
        var partAdapter = PartAdapter(productPartsExtended,serviceId,requireContext())
        partsRecyclerView.adapter = partAdapter
        partsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

    }

    private fun findByIDs(root: View){
        productSpinner = root.findViewById(R.id.productSpinner)
        productImage = root.findViewById(R.id.productImage)
        productDescription = root.findViewById(R.id.productDescription)
        partsRecyclerView = root.findViewById(R.id.partsRecycler)
        productCategorySpinner = root.findViewById(R.id.productCategoriesSpinner)
        productSubCategorySpinner = root.findViewById(R.id.productSubCategories)
        productPartSpinner = root.findViewById(R.id.productPartSpinner)
        productCategoriesParentIdSpinner = root.findViewById(R.id.productCategoriesParentIdSpinner)
        productSubCategoryParentIdSpinner = root.findViewById(R.id.productSubCategoryParentIdSpinner)
        productPartParentIdSpinner = root.findViewById(R.id.productPartParentIdSpinner)
        addButton = root.findViewById(R.id.addButton)
        buttonSubmit = root.findViewById(R.id.buttonSubmit)
    }

    private suspend fun initializeCategoryLists(){
        medicareAPI.getCategories().enqueue(object : Callback<List<Category>>{
            override fun onResponse(
                call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful){
                    val categoryResults: List<Category> = response.body()!!
                    for(categoryResult in categoryResults){
                        if(categoryResult.id < 7){
                            productCategory.add(categoryResult.name)
                            productCategoryParentIds.add(categoryResult.id.toString())
                        }
                    }
                    GlobalScope.launch(Main) {
                        initializeAddSpinners()
                    }
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private suspend fun initializeAddSpinners() {
        //initialize category spinner
        val adapter1: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productCategory
        )
        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productCategoryParentIds
        )
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productCategorySpinner.adapter = adapter1
        productCategorySpinner.setSelection(0)
        productCategoriesParentIdSpinner.adapter = adapter2
        productCategoriesParentIdSpinner.setSelection(0)
//        var currentProductSubCategoryList = arrayListOf<String>()
//        var currentProductSubCategoryParentsIDList = arrayListOf<String>()
        //Spinner item selected
        val adapter3: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ArrayList<String>()
        )
        val adapter4: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ArrayList<String>()
        )
        productCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if(position==0){
                    adapter3.clear()
                    adapter4.clear()
                    return
                }
                productCategoriesParentIdSpinner.setSelection(position)
                categoryID = productCategoriesParentIdSpinner.selectedItem.toString().toInt()

                val currentProductSubCategoryList = ArrayList<String>()
                val currentProductSubCategoryParentsIDList = ArrayList<String>()
                medicareAPI.getSubCategoriesByParentId(categoryID).enqueue(
                    object : Callback<List<Category>>{
                        override fun onResponse(
                            call: Call<List<Category>>,
                            response: Response<List<Category>>
                        ) {
                            val subCategories: List<Category> = response.body()!!
                            for(subCategory in subCategories){
                                currentProductSubCategoryList.add(subCategory.name)
                                currentProductSubCategoryParentsIDList.add(subCategory.id.toString())


                            }
                            adapter3.clear()
                            adapter4.clear()
                            adapter3.addAll(currentProductSubCategoryList)
                            adapter4.addAll(currentProductSubCategoryParentsIDList)
                            adapter3.notifyDataSetChanged()
                            adapter4.notifyDataSetChanged()
                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            productSubCategorySpinner.adapter = adapter3
                            productSubCategoryParentIdSpinner.adapter = adapter4
                            productSubCategorySpinner.setSelection(0)
                            productSubCategoryParentIdSpinner.setSelection(0)
                            productSubCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onItemSelected(
                                    parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    productSubCategoryParentIdSpinner.setSelection(position)
                                    productSubCategorySpinner.setSelection(position)
                                    subCategoryID = productSubCategoryParentIdSpinner.selectedItem.toString().toInt()
                                    medicareAPI.getPartsByCategoryAndSubCategoryId(categoryID,subCategoryID).enqueue(
                                        object : Callback<List<Part>>{
                                            override fun onResponse(
                                                call: Call<List<Part>>,
                                                response: Response<List<Part>>
                                            ) {
                                                if (response.isSuccessful){
                                                    val parts: List<Part> = response.body()!!
                                                    val currentProductPartList = ArrayList<String>()
                                                    val currentProductPartParentsIDList = ArrayList<String>()
                                                    for(part in parts){
                                                        currentProductPartList.add(part.name + " " + part.code)
                                                        currentProductPartParentsIDList.add(part.id.toString())
                                                    }
                                                    val adapter5: ArrayAdapter<String> = ArrayAdapter<String>(
                                                        requireContext(),
                                                        android.R.layout.simple_spinner_item,
                                                        currentProductPartList
                                                        )
                                                    val adapter6: ArrayAdapter<String> = ArrayAdapter<String>(
                                                        requireContext(),
                                                        android.R.layout.simple_spinner_item,
                                                        currentProductPartParentsIDList
                                                    )
                                                    adapter5.addAll(currentProductPartList)
                                                    adapter6.addAll(currentProductPartParentsIDList)
                                                    adapter5.notifyDataSetChanged()
                                                    adapter6.notifyDataSetChanged()
                                                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                    productPartSpinner.adapter = adapter5
                                                    productPartParentIdSpinner.adapter = adapter6
                                                    productPartSpinner.setSelection(0)
                                                    productPartParentIdSpinner.setSelection(0)
                                                    productPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                                        override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                                                            productPartParentIdSpinner.setSelection(position)
                                                            productPartSpinner.setSelection(position)
                                                            partIDToAdd = productPartParentIdSpinner.selectedItem.toString().toInt()
                                                            Log.i("spinnerIdTest", "onItemSelected: ${categoryID} and ${subCategoryID} and ${partIDToAdd}")
                                                        }

                                                        override fun onNothingSelected(parent: AdapterView<*>?) {
                                                            TODO("Not yet implemented")
                                                        }
                                                    }
                                                }

                                            }

                                            override fun onFailure(
                                                call: Call<List<Part>>,
                                                t: Throwable
                                            ) {
                                                TODO("Not yet implemented")
                                            }
                                        }
                                    )
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                            }
                        }

                        override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    }
                    )


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }


    private fun addPartExtended(view: View){
        var part: Part
        if(partIDToAdd == 0){
            Toast.makeText(context,"Plesase choose category and part",Toast.LENGTH_SHORT).show()
        }else{
            medicareAPI.getPartById(partIDToAdd).enqueue(object: Callback<Part>{
                override fun onResponse(call: Call<Part>, response: Response<Part>) {
                    if(response.isSuccessful){
                        part = response.body()!!
                        productPartsExtended.add(part)
                        partsRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<Part>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.addButton){
            addPartExtended(v)
        }else if(v.id == R.id.buttonSubmit){
            Log.i("submit", "onClick: ${productPartsExtended.size}  product_id: ${currentProductId}  service_id: ${currentProductServiceId}")
        }

    }
}