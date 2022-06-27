package com.example.bookshelfapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_request.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateRequestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class CreateRequestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_request.setOnClickListener{
            if(checkValidity()){
                createRequestAfterInternetCheck()
            }
        }
    }

    private fun checkValidity(): Boolean {
        if(request_title.text.toString().isBlank() || request_contact.text.toString().isBlank()){
            Toast.makeText(requireContext(),"Please Fill in all fields", Toast.LENGTH_LONG).show()
            return false
        }

        try{
            val number = request_contact.text.toString()
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

    private fun createRequestAfterInternetCheck(){
        val connectionManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectionManager.activeNetworkInfo
        if(activeNetwork != null){
            createRequest()
        }else{
            Toast.makeText(requireContext(),"Request Creation Failed. Please Check Internet Connection", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }
    }

    private fun createRequest(){
        val requestDao = RequestDao()
        progressBar.visibility = View.VISIBLE
        requestDao.addRequest(request_title.text.toString(),request_contact.text.toString(),request_description.text.toString())
        Toast.makeText(requireContext(),"Request Added successfully !!", Toast.LENGTH_LONG).show()
        requireActivity().onBackPressed()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateRequestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateRequestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}