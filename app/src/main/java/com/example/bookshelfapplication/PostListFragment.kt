package com.example.bookshelfapplication

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_alert.view.*
import kotlinx.android.synthetic.main.fragment_post_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var postList: MutableList<Post>
    private lateinit var postDao: PostDao
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postList = mutableListOf()
        list.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter =  PostListAdapter{
                findNavController().navigate(PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(it))
            }
            setHasFixedSize(true)
        }

        auth = Firebase.auth
        postDao = PostDao()
        populateList()
    }

    private fun populateList(){
        val postsCollections = postDao.postCollections
        postsCollections.get()
            .addOnSuccessListener {
                if(it != null){
                    val docSnapshot = it.documents
                    for(document in docSnapshot) {
                        if(document != null){
                            val map = document.data
                            if(map != null) {
                                val entry = Post(map["title"].toString(),map["price"].toString().toLong(),
                                    map["description"].toString(),map["branch"].toString(),
                                    map["year"].toString(),map["contact"].toString(),
                                    map["photoUrl"].toString(),map["createdBy"].toString(),
                                    map["uid"].toString(),map["createdAt"].toString().toLong())
                                postList.add(entry)
                            }
                        }
                    }
                }

                postList.sortByDescending { it.createdAt }
                (list.adapter as PostListAdapter).dataset = postList

                message.visibility = if(postList.isEmpty()){
                    View.VISIBLE
                }else{
                    View.INVISIBLE
                }

                progress_bar.visibility = View.INVISIBLE
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(),"Task Failed. Please Try again later", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDialog(){
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.custom_alert,null)
        val builder = AlertDialog.Builder(requireContext())

        with(builder) {
            setView(view)
            val branches = mutableListOf("All","CS","ECE","EEE","EIE","Mechanical","Chemical","Economics","Maths","Physics","Chemistry")
            view.branch_spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,branches)

            val years = mutableListOf("All Years","1st Year","2nd Year","3rd Year","4th Year","5th Year")
            view.year_spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,years)
            //sets positive button
            setPositiveButton("FILTER") { _, _ ->
                updatePostList(view.year_spinner.selectedItem.toString(),view.branch_spinner.selectedItem.toString())
            }
            //sets negative button
            setNegativeButton("CANCEL") { _, _ ->

            }

            show()
        }
    }

    private fun updatePostList(year: String, branch: String){
        if(branch == "All" && year == "All Years"){
            (list.adapter as PostListAdapter).dataset = postList
            return
        }

        var listAfterFilter = mutableListOf<Post>()
        for(post in postList){
            if(branch == "Any"){
                if(post.year == year)
                    listAfterFilter.add(post)
            }else if(year == "All Years"){
                if(post.branch == branch)
                    listAfterFilter.add(post)
            }else{
                if(post.year == year && post.branch == branch)
                    listAfterFilter.add(post)
            }
        }
        (list.adapter as PostListAdapter).dataset = listAfterFilter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ordering_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_price -> {
                postList.sortBy { it.price }
                (list.adapter as PostListAdapter).dataset = postList
                true
            }
            R.id.sort_by_time -> {
                postList.sortByDescending { it.createdAt }
                (list.adapter as PostListAdapter).dataset = postList
                true
            }
            R.id.filter ->{
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}