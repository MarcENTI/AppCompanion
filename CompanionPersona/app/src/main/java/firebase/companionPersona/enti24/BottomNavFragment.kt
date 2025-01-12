package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView



class BottomNavFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.navBar)
        val titleImage: ImageView = view.findViewById(R.id.titleImage)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)

        profileImage.setOnClickListener(OnClickListener {
            val intent: Intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)

        })


        profileImage.setImageResource(R.drawable.jack_frost)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.forum -> {
                    val forumFragment = ForumFragment()
                    titleImage.setImageResource(R.drawable.title_forum)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.inner_fragment_container, forumFragment)
                        .commit()
                    true
                }
                R.id.confident -> {
                    val confidentsFragment = ConfidentsFragment()

                    titleImage.setImageResource(R.drawable.title_confidents)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.inner_fragment_container, confidentsFragment)
                        .commit()
                    true
                }
                R.id.chat -> {
                    val chatFragment = ChatFragment()

                    titleImage.setImageResource(R.drawable.title_chat)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.inner_fragment_container, chatFragment)
                        .commit()
                    true
                }
                R.id.calendar -> {
                    val calendarFragment = CalendarFragment()

                    titleImage.setImageResource(R.drawable.title_calendar)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.inner_fragment_container, calendarFragment)
                        .commit()
                    true
                }
                R.id.compendium -> {
                    val compendiumFragment = CompendiumFragment()

                    titleImage.setImageResource(R.drawable.title_compendium)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.inner_fragment_container, compendiumFragment)
                        .commit()
                    true
                }

                else -> false
            }
        }


    }

}



