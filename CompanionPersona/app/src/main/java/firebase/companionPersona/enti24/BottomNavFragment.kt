package firebase.companionPersona.enti24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.forum ->
                {
                    true;
                }
                R.id.compendium ->
                {
                    true;
                }
                R.id.chat ->
                {
                    true;
                }
                R.id.calendar ->
                {
                    true;
                }
                R.id.confident ->
                {
                    true;
                }
                else -> false

            }


            }

    }



}