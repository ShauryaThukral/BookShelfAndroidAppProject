package com.example.bookshelfapplication

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_create_post.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatePostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class CreatePostFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            storage = FirebaseStorage.getInstance()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val branches = mutableListOf<String>("All","CS","ECE","EEE","EIE","Mechanical","Chemical","Economics","Maths","Physics","Chemistry")
        branch.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,branches)

        val years = mutableListOf<String>("All Years","1st Year","2nd Year","3rd Year","4th Year","5th Year")
        Year.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,years)

        save_post.setOnClickListener {
            if(checkValidity()){
                createPostAfterInternetCheck()
            }
        }

        add_photo.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }
    }

    private fun checkValidity() : Boolean{
        if(post_title.text.toString().isBlank() || contact.text.toString().isBlank() || price.text.toString().isBlank()){
            Toast.makeText(requireContext(),"Please Fill in all Mandatory Fields",Toast.LENGTH_LONG).show()
            return false
        }else if(post_title.text.toString().length > 25){
            Toast.makeText(requireContext(),"Please Shorten Post Title to 25 characters",Toast.LENGTH_LONG).show()
            return false
        }else if(imageUri == null){
            Toast.makeText(requireContext(),"Please Add Image",Toast.LENGTH_LONG).show()
            return false
        }

        try{
            price.text.toString().toLong()
        }catch(ex :Exception){
            Toast.makeText(requireContext(),"Please Enter a Valid Price",Toast.LENGTH_LONG).show()
            return false
        }

        try{
            val number = contact.text.toString()
            if(number.length != 10){
                Toast.makeText(requireContext(),"Please Enter a Valid Contact Number",Toast.LENGTH_LONG).show()
                return false
            }
            number.toLong()
        }catch(ex :Exception){
            Toast.makeText(requireContext(),"Please Enter a Valid Price",Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun createPostAfterInternetCheck(){
        val connectionManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectionManager.activeNetworkInfo
        if(activeNetwork != null){
            createPost()
        }else{
            Toast.makeText(requireContext(),"Post Creation Failed. Please Check Internet Connection", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }
    }

    private fun createPost(){
        progress.visibility = View.VISIBLE
        Toast.makeText(requireContext(),"Please wait for few moments !!", Toast.LENGTH_LONG).show()
        val storageRef = storage.reference
        if(imageUri != null){
            val imageRef = storageRef.child("images/${imageUri?.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri!!)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addPostToFirestore(downloadUri.toString())
                } else {
                    Toast.makeText(requireContext(),"Task Failed. Please Try again later",Toast.LENGTH_LONG).show()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun addPostToFirestore(photoUrl: String){
        val postDao = PostDao()
        postDao.addPost(post_title.text.toString(), price.text.toString().toLong(), post_description.text.toString(),
            branch.selectedItem.toString(), Year.selectedItem.toString(), contact.text.toString(),photoUrl)
        Toast.makeText(requireContext(),"Post Added successfully !!", Toast.LENGTH_LONG).show()
        requireActivity().onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE ){
            if(resultCode == RESULT_OK){
                imageUri = data?.data
                books_photo.setImageURI(imageUri)
            }
        }
    }

    companion object {
        private val IMAGE_REQUEST_CODE: Int = 1001
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatePostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}