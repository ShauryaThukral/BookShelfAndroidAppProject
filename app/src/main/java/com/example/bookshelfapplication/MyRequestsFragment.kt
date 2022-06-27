package com.example.bookshelfapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_requests.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyRequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyRequestsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var requestDao: RequestDao
    private lateinit var auth: FirebaseAuth
    private lateinit var myRequests: MutableList<Request>

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
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_request.setOnClickListener{
            findNavController().navigate(MyRequestsFragmentDirections.actionMyRequestsFragmentToCreateRequestFragment())
        }
        add_request.isClickable = false

        auth = Firebase.auth
        val currentUser = auth.currentUser?.uid
        requestDao = RequestDao()
        val requestCollections = requestDao.requestCollections

        with(my_requests){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MyRequestsAdapter({
                findNavController().navigate(MyRequestsFragmentDirections.actionMyRequestsFragmentToRequestDetailFragment(it))
            }){
                showDialog(it)
            }
            setHasFixedSize(true)
        }

        myRequests = mutableListOf<Request>()
        requestCollections.whereEqualTo("uid",currentUser).get()
            .addOnSuccessListener {
                if(it != null){
                    val docSnapshot = it.documents
                    for(document in docSnapshot) {
                        if(document != null){
                            val map = document.data
                            if(map != null) {
                                val entry = Request(map["title"].toString(),map["description"].toString(),
                                    map["contact"].toString(),map["createdBy"].toString(),map["uid"].toString(),
                                    map["createdAt"].toString().toLong())
                               myRequests.add(entry)
                            }
                        }
                    }
                }
                myRequests.sortByDescending { it.createdAt }
                (my_requests.adapter as MyRequestsAdapter).dataset = myRequests

                message.visibility = if(myRequests.isEmpty()){
                    View.VISIBLE
                }else{
                    View.INVISIBLE
                }

                add_request.isClickable = true
                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(),"Task Failed. Please Try again later", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDialog(request: Request){
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setMessage("Are you sure you want to delete this post ?");
            setPositiveButton("YES"){_,_->
                myRequests.remove(request)
                (my_requests.adapter as MyRequestsAdapter).dataset = myRequests
                requestDao.deleteRequest(request.uid+request.createdAt)
                Toast.makeText(requireContext(),"Request Deleted Successfully", Toast.LENGTH_LONG).show()
            }

            setNegativeButton("NO") { _, _ ->

            }

            show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyRequestsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyRequestsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}