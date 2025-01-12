package firebase.companionPersona.enti24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.graphics.drawable.IconCompat
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



